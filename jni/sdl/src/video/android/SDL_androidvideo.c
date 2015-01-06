/*
    SDL - Simple DirectMedia Layer
    Copyright (C) 1997-2009 Sam Lantinga

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Sam Lantinga
    slouken@libsdl.org
*/
#include "SDL_config.h"

#include "SDL_video.h"
#include "SDL_mouse.h"
#include "SDL_mutex.h"
#include "SDL_thread.h"
#include "../SDL_sysvideo.h"
#include "../SDL_pixels_c.h"
#include "../../events/SDL_events_c.h"

#include "SDL_androidvideo.h"

#include <jni.h>
#include <android/log.h>
#include <GLES/gl.h>
#include <GLES/glext.h>
#include <sys/time.h>
#include <time.h>
#include <android/log.h>
#include <stdint.h>
#include <math.h>
#include <string.h> // for memset()



#define ANDROIDVID_DRIVER_NAME "android"

/* Initialization/Query functions */
static int ANDROID_VideoInit(_THIS, SDL_PixelFormat *vformat);
static SDL_Rect **ANDROID_ListModes(_THIS, SDL_PixelFormat *format, Uint32 flags);
static SDL_Surface *ANDROID_SetVideoMode(_THIS, SDL_Surface *current, int width, int height, int bpp, Uint32 flags);
static int ANDROID_SetColors(_THIS, int firstcolor, int ncolors, SDL_Color *colors);
static void ANDROID_VideoQuit(_THIS);

/* Hardware surface functions */
static int ANDROID_AllocHWSurface(_THIS, SDL_Surface *surface);
static int ANDROID_LockHWSurface(_THIS, SDL_Surface *surface);
static void ANDROID_UnlockHWSurface(_THIS, SDL_Surface *surface);
static void ANDROID_FreeHWSurface(_THIS, SDL_Surface *surface);
static int ANDROID_FlipHWSurface(_THIS, SDL_Surface *surface);
static void ANDROID_GL_SwapBuffers(_THIS);

// Stubs to get rid of crashing in OpenGL mode
// The implementation dependent data for the window manager cursor
struct WMcursor {
    int unused ;
};


void ANDROID_FreeWMCursor(_THIS, WMcursor *cursor) {
    SDL_free (cursor);
    return;
}
WMcursor * ANDROID_CreateWMCursor(_THIS, Uint8 *data, Uint8 *mask, int w, int h, int hot_x, int hot_y) {
    WMcursor * cursor;
    cursor = (WMcursor *) SDL_malloc (sizeof (WMcursor)) ;
    if (cursor == NULL) {
        SDL_OutOfMemory () ;
        return NULL ;
    }
    return cursor;
}
int ANDROID_ShowWMCursor(_THIS, WMcursor *cursor) {
    return 1;
}
void ANDROID_WarpWMCursor(_THIS, Uint16 x, Uint16 y) { }
void ANDROID_MoveWMCursor(_THIS, int x, int y) { }


/* etc. */
static void ANDROID_UpdateRects(_THIS, int numrects, SDL_Rect *rects);


/* Private display data */

#define SDL_NUMMODES 14
struct SDL_PrivateVideoData {
	SDL_Rect *SDL_modelist[SDL_NUMMODES+1];
};

#define SDL_modelist		(THIS->hidden->SDL_modelist)


static int sWindowWidth  = 480;
static int sWindowHeight = 360;
static int sWindowFiltering = 0;
static int followMouse = 0;
static int oversizeMode = 1; //0 = paged, 1 = shrink
static int keepAspect = 1; //0 - no aspect, 1 - keep aspect
static int panStepX = 80;
static int panStepY = 60;
static int panX=0;
static int panY=0;
//sdl screen
static int winX=320;
static int winY=240;
static float mouseResizeX=1;
static float mouseResizeY=1;
// Pointer to in-memory video surface
static int memX = 0;
static int memY = 0;
// In-memory surfaces
static void * memBuffer1 = NULL;
static void * memBuffer2 = NULL;
static void * memBuffer = NULL;
static SDL_Thread * mainThread = NULL;
static enum { GL_State_Init, GL_State_Ready, GL_State_Uninit, GL_State_Uninit2 } openglInitialized = GL_State_Uninit2;
static GLuint texture = 0;

static SDLKey keymap[KEYCODE_LAST+1];

extern int processAndroidTrackballKeyDelays( int key, int action ); // Defined in androidinput.c

/* ANDROID driver bootstrap functions */

static int ANDROID_Available(void)
{
	/*
	const char *envr = SDL_getenv("SDL_VIDEODRIVER");
	if ((envr) && (SDL_strcmp(envr, ANDROIDVID_DRIVER_NAME) == 0)) {
		return(1);
	}

	return(0);
	*/
	return 1;
}

static void ANDROID_DeleteDevice(SDL_VideoDevice *device)
{
	SDL_free(device->hidden);
	SDL_free(device);
}

static SDL_VideoDevice *ANDROID_CreateDevice(int devindex)
{
	SDL_VideoDevice *device;

	/* Initialize all variables that we clean on shutdown */
	device = (SDL_VideoDevice *)SDL_malloc(sizeof(SDL_VideoDevice));
	if ( device ) {
		SDL_memset(device, 0, (sizeof *device));
		device->hidden = (struct SDL_PrivateVideoData *)
				SDL_malloc((sizeof *device->hidden));
	}
	if ( (device == NULL) || (device->hidden == NULL) ) {
		SDL_OutOfMemory();
		if ( device ) {
			SDL_free(device);
		}
		return(0);
	}
	SDL_memset(device->hidden, 0, (sizeof *device->hidden));

	/* Set the function pointers */
	device->VideoInit = ANDROID_VideoInit;
	device->ListModes = ANDROID_ListModes;
	device->SetVideoMode = ANDROID_SetVideoMode;
	device->CreateYUVOverlay = NULL;
	device->SetColors = ANDROID_SetColors;
	device->UpdateRects = ANDROID_UpdateRects;
	device->VideoQuit = ANDROID_VideoQuit;
	device->AllocHWSurface = ANDROID_AllocHWSurface;
	device->CheckHWBlit = NULL;
	device->FillHWRect = NULL;
	device->SetHWColorKey = NULL;
	device->SetHWAlpha = NULL;
	device->LockHWSurface = ANDROID_LockHWSurface;
	device->UnlockHWSurface = ANDROID_UnlockHWSurface;
	device->FlipHWSurface = ANDROID_FlipHWSurface;
	device->FreeHWSurface = ANDROID_FreeHWSurface;
	device->SetCaption = NULL;
	device->SetIcon = NULL;
	device->IconifyWindow = NULL;
	device->GrabInput = NULL;
	device->GetWMInfo = NULL;
	device->InitOSKeymap = ANDROID_InitOSKeymap;
	device->PumpEvents = ANDROID_PumpEvents;

	// Stubs
	device->FreeWMCursor = ANDROID_FreeWMCursor;
	device->CreateWMCursor = ANDROID_CreateWMCursor;
	device->ShowWMCursor = ANDROID_ShowWMCursor;
	device->WarpWMCursor = ANDROID_WarpWMCursor;
	device->MoveWMCursor = ANDROID_MoveWMCursor;

	return device;
}

VideoBootStrap ANDROID_bootstrap = {
	ANDROIDVID_DRIVER_NAME, "SDL android video driver",
	ANDROID_Available, ANDROID_CreateDevice
};


int ANDROID_VideoInit(_THIS, SDL_PixelFormat *vformat)
{
	int i;
	/* Determine the screen depth (use default 16-bit depth) */
	/* we change this during the SDL_SetVideoMode implementation... */
	//vformat->BitsPerPixel = 32;
	//Mac
	vformat->BitsPerPixel = 32;
	vformat->BytesPerPixel = 4;

	for ( i=0; i<SDL_NUMMODES; ++i ) {
		SDL_modelist[i] = SDL_malloc(sizeof(SDL_Rect));
		SDL_modelist[i]->x = SDL_modelist[i]->y = 0;
	}
	/* Modes sorted largest to smallest */
	SDL_modelist[0]->w = sWindowWidth; SDL_modelist[0]->h = sWindowHeight;
	SDL_modelist[1]->w = 640; SDL_modelist[1]->h = 480; // Always available on any screen and any orientation
	SDL_modelist[2]->w = 640; SDL_modelist[2]->h = 400; // Always available on any screen and any orientation
	SDL_modelist[3]->w = 640; SDL_modelist[3]->h = 350; // Always available on any screen and any orientation
    SDL_modelist[4]->w = 360; SDL_modelist[4]->h = 400; // Always available on any screen and any orientation
    SDL_modelist[5]->w = 360; SDL_modelist[5]->h = 256; // Settlers
	SDL_modelist[6]->w = 360; SDL_modelist[6]->h = 240; // Always available on any screen and any orientation
	SDL_modelist[7]->w = 360; SDL_modelist[7]->h = 200; // Always available on any screen and any orientation
	SDL_modelist[8]->w = 320; SDL_modelist[8]->h = 400; // Always available on any screen and any orientation
	SDL_modelist[9]->w = 320; SDL_modelist[9]->h = 256; // Always available on any screen and any orientation
	SDL_modelist[10]->w = 320; SDL_modelist[10]->h = 240; // Always available on any screen and any orientation
	SDL_modelist[11]->w = 320; SDL_modelist[11]->h = 200; // Always available on any screen and any orientation
	SDL_modelist[12]->w = 400; SDL_modelist[12]->h = 600; // Fraccint
	SDL_modelist[13]->w = 512; SDL_modelist[13]->h = 384; // Mac resolution
	SDL_modelist[14] = NULL;

	/* We're done! */
	return(0);
}

SDL_Rect **ANDROID_ListModes(_THIS, SDL_PixelFormat *format, Uint32 flags)
{
	//if(format->BitsPerPixel != 32)
	//Mac
	if(format->BitsPerPixel != 32)
		return NULL;
	return SDL_modelist;
}

SDL_Surface *ANDROID_SetVideoMode(_THIS, SDL_Surface *current,
				int width, int height, int bpp, Uint32 flags)
{
  ALOG_DEBUG("in set video mode : %d, %d, %d", width, height, bpp);
	if ( memBuffer1 ) SDL_free( memBuffer1 );
	if ( memBuffer2 ) SDL_free( memBuffer2 );

	memBuffer = memBuffer1 = memBuffer2 = NULL;

	memX = width;
	memY = height;

	memBuffer1 = SDL_malloc(memX * memY * (bpp / 8));

	if ( ! memBuffer1 ) {
		SDL_SetError("Couldn't allocate buffer for requested mode");
		return(NULL);
	}
	SDL_memset(memBuffer1, 0, memX * memY * (bpp / 8));

	if( flags & SDL_DOUBLEBUF )
	{
	    ALOG_DEBUG("in set video mode : double buffering requested");
		memBuffer2 = SDL_malloc(memX * memY * (bpp / 8));
		if ( ! memBuffer2 ) {
			SDL_SetError("Couldn't allocate buffer for requested mode");
			return(NULL);
		}
		SDL_memset(memBuffer2, 0, memX * memY * (bpp / 8));
	}

	memBuffer = memBuffer1;
	openglInitialized = GL_State_Init;

	/* Allocate the new pixel format for the screen */
	if ( ! SDL_ReallocFormat(current, bpp, 0, 0, 0, 0) ) {
		if(memBuffer)
		SDL_free(memBuffer);
		memBuffer = NULL;
		SDL_SetError("Couldn't allocate new pixel format for requested mode");
		return(NULL);
	}

	/* Set up the new mode framebuffer */
	current->flags = (flags & SDL_FULLSCREEN) | (flags & SDL_DOUBLEBUF);
	current->w = width;
	current->h = height;
	current->pitch = memX * (bpp / 8);
	current->pixels = memBuffer;

	/* Wait 'till we can draw */
	ANDROID_FlipHWSurface(THIS, current);
	/* We're done */
	ALOG_DEBUG("exiting set video mode");
	return(current);
}

/* Note:  If we are terminated, this could be called in the middle of
   another SDL video routine -- notably UpdateRects.
*/
void ANDROID_VideoQuit(_THIS)
{
	openglInitialized = GL_State_Uninit;
	while( openglInitialized != GL_State_Uninit2 )
		SDL_Delay(50);

	memX = 0;
	memY = 0;
	memBuffer = NULL;
	SDL_free( memBuffer1 );
	memBuffer1 = NULL;
	if( memBuffer2 )
		SDL_free( memBuffer2 );
	memBuffer2 = NULL;

	int i;

	if (THIS->screen->pixels != NULL)
	{
		SDL_free(THIS->screen->pixels);
		THIS->screen->pixels = NULL;
	}
	/* Free video mode lists */
	for ( i=0; i<SDL_NUMMODES; ++i ) {
		if ( SDL_modelist[i] != NULL ) {
			SDL_free(SDL_modelist[i]);
			SDL_modelist[i] = NULL;
		}
	}
}

void ANDROID_PumpEvents(_THIS)
{
}

/* We don't actually allow hardware surfaces other than the main one */
// TODO: use OpenGL textures here
static int ANDROID_AllocHWSurface(_THIS, SDL_Surface *surface)
{
	return(-1);
}
static void ANDROID_FreeHWSurface(_THIS, SDL_Surface *surface)
{
	return;
}

/* We need to wait for vertical retrace on page flipped displays */
static int ANDROID_LockHWSurface(_THIS, SDL_Surface *surface)
{
	return(0);
}

static void ANDROID_UnlockHWSurface(_THIS, SDL_Surface *surface)
{
	return;
}

static void ANDROID_UpdateRects(_THIS, int numrects, SDL_Rect *rects)
{
	ANDROID_FlipHWSurface(THIS, SDL_VideoSurface);
}

static int ANDROID_FlipHWSurface(_THIS, SDL_Surface *surface)
{
    //TODO: Add double buffering back in
    if( surface->flags & SDL_DOUBLEBUF )
    {
			if( memBuffer == memBuffer1 )
				memBuffer = memBuffer2;
			else
				memBuffer = memBuffer1;
			surface->pixels = memBuffer;
    }

	return(0);
};

int ANDROID_SetColors(_THIS, int firstcolor, int ncolors, SDL_Color *colors)
{
	return(1);
}

/* JNI-C++ wrapper stuff */

void sdlSleep (void) {
    SDL_Delay(50);
}

extern int main( int argc, char ** argv );
static int SDLCALL MainThreadWrapper(void * dummy)
{
	int argc = 3;
	char * argv[] = { "basilisk", "--config", "/sdcard/droidmac.prefs" };
	chdir(SDL_CURDIR_PATH);
	return main( argc, argv );
	//return 1;
};

#ifndef SDL_JAVA_PACKAGE_PATH
#error You have to define SDL_JAVA_PACKAGE_PATH to your package path with dots replaced with underscores, for example "com_example_SanAngeles"
#endif
#define JAVA_EXPORT_NAME2(name,package) Java_##package##_##name
#define JAVA_EXPORT_NAME1(name,package) JAVA_EXPORT_NAME2(name,package)
#define JAVA_EXPORT_NAME(name) JAVA_EXPORT_NAME1(name,SDL_JAVA_PACKAGE_PATH)

extern void
JAVA_EXPORT_NAME(DemoRenderer_nativeInit) ( JNIEnv*  env, jobject  thiz, jint sizex, jint sizey, jint dosFiltering )
{
  sWindowWidth = sizex;
  sWindowHeight = sizey;
  winX = sizex;
  winY = sizey;
  sWindowFiltering = dosFiltering;
  ALOG_DEBUG("in rendered native init: %d, %d, %d", sWindowWidth, sWindowHeight, sWindowFiltering);
	mainThread = SDL_CreateThread( MainThreadWrapper, NULL );
}


extern void
JAVA_EXPORT_NAME(DemoRenderer_nativeFiltering) ( JNIEnv*  env, jobject  thiz, jint dosFiltering )
{
  sWindowFiltering= dosFiltering;
}

extern void
JAVA_EXPORT_NAME(DemoRenderer_nativeAspect) ( JNIEnv*  env, jobject  thiz, jint flag )
{
  ALOG_DEBUG("setting native aspect to:%d",flag);
  keepAspect = flag;
}


extern void
JAVA_EXPORT_NAME(DemoRenderer_nativeResize) ( JNIEnv*  env, jobject  thiz, jint w, jint h )
{
    sWindowWidth  = w;
    sWindowHeight = h;
    __android_log_print(ANDROID_LOG_INFO, "libSDL", "resize w=%d h=%d", w, h);
//dummy
}

/* Call to finalize the graphics state */
extern void
JAVA_EXPORT_NAME(DemoRenderer_nativeDone) ( JNIEnv*  env, jobject  thiz )
{
	if( mainThread )
	{
		__android_log_print(ANDROID_LOG_INFO, "libSDL", "quitting...");
		SDL_PrivateQuit();
		SDL_WaitThread(mainThread, NULL);
		mainThread = NULL;
		__android_log_print(ANDROID_LOG_INFO, "libSDL", "quit OK");
	}
}


//some debug helper functions
void renderInitFunctions()
{
 			glClearColor(0,0,0,0);

			// Set projection
			ALOG_DEBUG("openglInit: glMatrixMode");
			glMatrixMode( GL_PROJECTION );
			glLoadIdentity();
			#if defined(GL_VERSION_ES_CM_1_0)
				#define glOrtho glOrthof
			#endif
			glOrtho( 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f );

			// Now Initialize modelview matrix
			glMatrixMode( GL_MODELVIEW );
			glLoadIdentity();

			glDisable(GL_DEPTH_TEST);
			glDisable(GL_CULL_FACE);
			glDisable(GL_DITHER);
			glDisable(GL_MULTISAMPLE);

			glEnable(GL_TEXTURE_2D);

}

void textureInitFunctions(GLuint texture)
{
 			ALOG_DEBUG("openglInit: glGenTextures");
			//creates a texture
			glGenTextures(1, &texture);

      //binds our texture to GLTEXTURE2D target
			glBindTexture(GL_TEXTURE_2D, texture);

			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            ALOG_DEBUG("openglInit: glTexParameteri");
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

			ALOG_DEBUG("openglInit: filtering");

            ALOG_DEBUG("openglInit: glTexEnvf");
			glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

}

void textureAllocs(int textX, int textY)
{
        void * textBuffer = SDL_malloc( textX*textY*4 ); // 4 bytes per pixel
        SDL_memset( textBuffer, 0, textX*textY*4 );
        ALOG_DEBUG("openglInit: after sdl_memset");


        //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textX, textY, 0, GL_RGBA, GL_UNSIGNED_BYTE, textBuffer);
        //Mac
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textX, textY, 0, GL_RGBA, GL_UNSIGNED_BYTE, textBuffer);
			  glColor4f(1.0f, 1.0f, 1.0f, 1.0f);


            ALOG_DEBUG("openglInit: glFinish");
			glFinish();

			SDL_free( textBuffer );
			SDL_Delay(100);
			ALOG_DEBUG("native render end");
}

void renderCopy(void* memBufferTemp)
{
    //shuffle memBufferTemp;
    unsigned char* uint8 = memBufferTemp;
    unsigned char temp1;
    unsigned char temp2;
    /*
    int i=0;
    for (;i<memX*memY*3;) {
        temp1 = uint8[0];
        uint8[0]=uint8[2];
        uint8[2]=temp1;
        i+=4;
        uint8+=4;
    }

    */
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, memX, memY, GL_RGBA, GL_UNSIGNED_BYTE, memBufferTemp);
    //Mac
    //glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, memX, memY, GL_RGB, GL_UNSIGNED_BYTE, memBufferTemp);
}


void renderDraw()
{
    if (keepAspect>0) {
        if (memX>=sWindowWidth || memY>=sWindowHeight)
        {
            //full screen
            if (oversizeMode==0)
            {
                //paged, just use max screen
                glDrawTexiOES(0, 0, 1, sWindowWidth, sWindowHeight); // GLES extension (should be faster)
            }
            else
            {
                //shrinked
                float aspectdos = (float) memX/(float) memY;
                float aspectgl = (float) sWindowWidth/(float) sWindowHeight;
                if (aspectdos >= aspectgl)
                {

                    //dos wider than ogl
                    float upscale=(float)sWindowWidth/(float) memX;
                    int nheight = (int)memY*upscale;
                    //ALOG_DEBUG("Dos wider:%f",upscale);
                    glDrawTexiOES(0, (sWindowHeight - nheight)/2, 1, sWindowWidth, nheight); // GLES extension (should be faster)
                }
                else
                {
                    //ogl wider than dos
                    float upscale=(float)sWindowHeight/(float) memY;
                    int nwidth = (int)memX*upscale;
                    glDrawTexiOES((sWindowWidth-nwidth)/2, 0, 1, nwidth, sWindowHeight); // GLES extension (should be faster)
                }

            }

        }
        else // dos screen smaller than ogl
        {
            //upscale with aspec
            float aspectdos = (float) memX/(float) memY;
            float aspectgl = (float) sWindowWidth/(float) sWindowHeight;
            if (aspectdos >= aspectgl)
            {
                //dos wider than ogl
                float upscale=(float)sWindowWidth/(float) memX;
                int nheight = memY*upscale;
                glDrawTexiOES(0, (sWindowHeight - nheight)/2, 1, sWindowWidth, nheight); // GLES extension (should be faster)
            }
            else
            {
                //ogl wider than dos
                float upscale=(float)sWindowHeight/(float) memY;
                int nwidth=memX*upscale;
                glDrawTexiOES((sWindowWidth-nwidth)/2, 0, 1, nwidth, sWindowHeight); // GLES extension (should be faster)
            }

        }
    }
    else // dont care about the aspect
    {
        glDrawTexiOES(0, 0, 1, sWindowWidth, sWindowHeight); // GLES extension (should be faster)
    }
}

/* Call to render the next GL frame */
extern void
JAVA_EXPORT_NAME(DemoRenderer_nativeRender) ( JNIEnv*  env, jobject  thiz )
{
  // ALOG_DEBUG("DemoRenderer_nativeRender");

	static GLint texcoordsCrop[] = {0, 0, 0, 0};

    static float clearColor = 0.0f;
	static int clearColorDir = 1;

    int textX, textY;
	void * memBufferTemp;

  //TODO: Move this to an initialization function
	if( memBuffer && openglInitialized != GL_State_Uninit2 )
	{
		if( openglInitialized == GL_State_Init )
		{

			//textX = sWindowWidth;
			//textY = sWindowHeight;

            ALOG_DEBUG("openglInitialized == GL_State_Init: memx:%d; memy:%d", memX, memY);
			openglInitialized = GL_State_Ready;

			//textX = 1024; // handle up to 640
			//textY = 512; // handle up to 480;
			textX = winX;
			textY = winY;

            ALOG_DEBUG("view port: %d x %d", textX, textY);
            ALOG_DEBUG("openglInit: glViewPort");
			glViewport(0, 0, textX, textY);

            renderInitFunctions();
            textureInitFunctions(texture);
            textureAllocs(textX, textY);

		}
		else if( openglInitialized == GL_State_Uninit )
		{
			openglInitialized = GL_State_Uninit2;

			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisableClientState(GL_VERTEX_ARRAY);

			glDeleteTextures(1, &texture);

			return;
		}

        //render frame
        glClearColor(0,0,0,0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //ALOG_DEBUG("openglRender: membuffer");
		memBufferTemp = memBuffer;
		//check if doswindow > glwindow
		int full;
        //GFX_GetSize(&winX,&winY,&full);
		//ALOG_DEBUG("dos size:%d, %d window size:%d, %d", memX, memY, sWindowWidth, sWindowHeight);

        //texture positioning
        if (oversizeMode==1) //shrinked
            {
                texcoordsCrop[0] = 0;
    			texcoordsCrop[1] = memY;
		    	texcoordsCrop[2] = memX;
			    texcoordsCrop[3] = - memY;
            }
        else
            {
                //paged mode
                // dos bigger than device
                if (memX > sWindowWidth || memY > sWindowHeight)
                {
                    texcoordsCrop[0] = panX;
                    texcoordsCrop[1] = sWindowHeight+panY;
                    texcoordsCrop[2] = sWindowWidth;
                    texcoordsCrop[3] = - sWindowHeight;
                }
                else // dos smaller than device
                {
                //upscale
                    texcoordsCrop[0] = 0;
                    texcoordsCrop[1] = memY;
                    texcoordsCrop[2] = memX;
                    texcoordsCrop[3] = - memY;
                }
            }

        //ALOG_DEBUG("textcoord crops: %d, %d, %d, %d",texcoordsCrop[0], texcoordsCrop[1], texcoordsCrop[2], texcoordsCrop[3] );
        glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_CROP_RECT_OES, texcoordsCrop);

        //filtering
        if (sWindowFiltering == 0)
        {
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        }
        else
        {
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        }

        // copy dos/sdl screen to texture
        renderCopy(memBufferTemp);
        //GLenum target, GLint level, GLint xoffset, GLint yoffset, GLsizei width, GLsizei height,
        //GLenum format,
        //GLenum type,
        //const GLvoid * pixels

        renderDraw();
        //SDL_Delay(50);
        sdlSleep();

                //int x, int y, int z, int width, int height

	}
	else
	{

		// Flash the screen
		if( clearColor >= 1.0f )
			clearColorDir = -1;
		else if( clearColor <= 0.0f )
			clearColorDir = 1;

		clearColor += (float)clearColorDir * 0.01f;
		glClearColor(clearColor,clearColor,clearColor,0);
		glClear(GL_COLOR_BUFFER_BIT);
		SDL_Delay(50);
	}
}

enum MOUSE_ACTION { MOUSE_DOWN = 0, MOUSE_UP=1, MOUSE_MOVE=2 };

//TODO: Call this nativeTouchscreen or something


extern void
JAVA_EXPORT_NAME(DemoGLSurfaceView_dosFollowMouse) ( JNIEnv*  env, jobject  thiz, jint flag )
{
  followMouse = flag;
}

extern void
JAVA_EXPORT_NAME(DemoGLSurfaceView_dosOversizeMode) ( JNIEnv*  env, jobject  thiz, jint nmode )
{
  oversizeMode = nmode;
}

void panWindow(int lr, int up)
{
  ALOG_DEBUG("Panning window:%d, %d", lr, up);
  if (lr==1)
  {
    panX = panX - panStepX;
    if (panX<0) panX=0;
  }
  if (lr==2)
  {
    panX = panX + panStepX;
    if (panX+sWindowWidth>memX) panX = memX-sWindowWidth;
  }

  if (up==1)
  {
    panY = panY - panStepY;
    if (panY<0) panY=0;
  }
  if (up==2)
  {
    panY = panY + panStepY;
    if (panY+sWindowHeight>memY) panY = memY-sWindowHeight;
  }
}


extern void
JAVA_EXPORT_NAME(DemoGLSurfaceView_panVirtualWindow) ( JNIEnv*  env, jobject  thiz, jint lr, jint up )
{
  panWindow(lr,up);
}

int SDL_MouseVisible()
{
  int result = 1;
  //int mousex = Dos_MouseGetX();
  //int mousey = Dos_MouseGetY();
  //if  (mousex < panX+30 || mousex > panX + sWindowWidth + 30) return 0;
  //if  (mousey < panY+30 || mousey > panX + sWindowHeight + 30) return 0;
  return result;
}

int SDL_MouseOutW(void) {
// returns 1 - left, 2 - right, 0 - in place
  //int mousex = Dos_MouseGetX();
  //if (mousex < panX+30) return 1;
  //if ( mousex > panX + sWindowWidth-30) return 2;
  return 0;
}

int SDL_MouseOutH(void) {
// returns 1 - higher, 2 - lower,
  //int mousey = Dos_MouseGetY();
  //if (mousey < panY+30) return 1;
  //if ( mousey > panY +sWindowHeight-30) return 2;
  return 0;
}

void SDL_PanForMouse()
{
    //check if mouse is out of screen
	if (SDL_MouseVisible()==0) {
    int lr = SDL_MouseOutW();
    int ud = SDL_MouseOutH();
    if (lr>0 || ud>0) {
      panWindow(lr,ud);
    }
	}
}

extern void
JAVA_EXPORT_NAME(DemoGLSurfaceView_nativeMouse) ( JNIEnv*  env, jobject  thiz, jint x, jint y, jint laction, jint raction )
{
/*
  ALOG_DEBUG("Delta: %d, %d", x, y);
  if (oversizeMode==1) //shrink
  {
      x = x*2;
      y = y*2;
  }
  Dos_MouseCursorMove(x, y);
  if( laction == MOUSE_DOWN ) Dos_MouseButtonPressed(0);
  if( laction == MOUSE_UP ) Dos_MouseButtonReleased(0);
  if( raction == MOUSE_DOWN ) Dos_MouseButtonPressed(1);
  if( raction == MOUSE_UP ) Dos_MouseButtonReleased(1);

  //ALOG_DEBUG("Panning for mouse: %d, %d", x, y);
  if (followMouse>0) SDL_PanForMouse();
  */
      //Always fire off the motion event
	SDL_PrivateMouseMotion(0, 1, x, y);
	if( laction == MOUSE_DOWN || laction == MOUSE_UP )
	{
	   SDL_PrivateMouseButton( (laction == MOUSE_DOWN) ? SDL_PRESSED : SDL_RELEASED, 1, 0, 0 );
  }
}

extern void
JAVA_EXPORT_NAME(DemoGLSurfaceView_nativeExit) ( JNIEnv*  env, jobject  thiz)
{
  Mac_Exit();
  ALOG_DEBUG("After Mac_Exit... DroidMaC quit.");
}


//TODO: Call this nativeTouchscreen or something
extern jint
JAVA_EXPORT_NAME(DemoGLSurfaceView_getDosSizeX) ( JNIEnv*  env, jobject  thiz )
{
  int x;
  int y;
  int full;
    //Always fire off the motion event
  //GFX_GetSize(&x,&y,&full);
	return x;
}

//TODO: Call this nativeTouchscreen or something
extern jint
JAVA_EXPORT_NAME(DemoGLSurfaceView_getDosSizeY) ( JNIEnv*  env, jobject  thiz )
{
  int x;
  int y;
  int full;
    //Always fire off the motion event
  //GFX_GetSize(&x,&y,&full);
	return y;
}

SDL_keysym *TranslateKey(int scancode, SDL_keysym *keysym)
{
	/* Sanity check */

	/* Set the keysym information */
	keysym->scancode = scancode;
	keysym->sym = scancode;
	keysym->mod = KMOD_NONE;

	/* If UNICODE is on, get the UNICODE value for the key */
	keysym->unicode = 0;
	if ( SDL_TranslateUNICODE ) {
		/* Populate the unicode field with the ASCII value */
		keysym->unicode = scancode;
	}
	return(keysym);
}


