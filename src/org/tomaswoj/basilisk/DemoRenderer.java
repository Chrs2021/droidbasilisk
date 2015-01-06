package org.tomaswoj.basilisk;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.util.Log;

class DemoRenderer implements GLSurfaceView.Renderer {

	private static int dosWindowX = 320;
	private static int dosWindowY = 240;
	private static int dosFiltering = 0;

	public void initRenderer(int dosX, int dosY, int dosfilt) {
		dosWindowX = dosX;
		dosWindowY = dosY;
		dosFiltering = dosfilt;
		
	}
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.v("basilisk", "nativeInit "+dosWindowX+" "+dosWindowY);
		nativeInit(dosWindowX, dosWindowY, dosFiltering);
		nativeAspect(0);
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		// gl.glViewport(0, 0, w, h);
		nativeResize(dosWindowX, dosWindowY);
	}

	public void onDrawFrame(GL10 gl) {
		nativeRender();
	}

	public void exitApp() {
		nativeDone(); 
	};

	private static native void nativeInit(int sizex, int sizey, int dosFiltering);

	private static native void nativeResize(int w, int h);

	private static native void nativeRender();

	private static native void nativeDone();
	public static native void nativeFiltering(int dosFiltering);
	public static native void nativeAspect(int aspect);
	
}