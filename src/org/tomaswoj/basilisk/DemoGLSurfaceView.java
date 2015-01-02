package org.tomaswoj.basilisk;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

class DemoGLSurfaceView extends GLSurfaceView {
	
	static int dosWinX = 320;
	static int dosWinY = 240;
	static int dosFiltering =0;
	
	public float startX=-1;
	public float startY=-1;
	public float lastX;
	public float lastY;
	public static boolean startedMove = true;
	public int dosPosX;
	public int dosPosY;

	public int sdlMouseX=0;
	public int sdlMouseY=0;
	
	public static GestureDetector detector = new GestureDetector(new MacGestureListener());
	public static int tapClick=0;
	
	public static boolean mouseLock = false;
	
	public DemoGLSurfaceView(Activity context, int sizex, int sizey, int filtering) {
		super(context);
		mParent = context;
		mRenderer = new DemoRenderer();
		dosWinX = sizex;
		dosWinY = sizey;
		dosFiltering = filtering;
		Log.v("droiddos", "SEtting renderer filtering:"+dosFiltering);
		mRenderer.initRenderer(dosWinX, dosWinY, dosFiltering);
		setRenderer(mRenderer);
	}
	
	public void setFiltering(int nFiltering) {
		mRenderer.nativeFiltering(nFiltering);
	}

	public void setAspect(int aspect) {
		mRenderer.nativeAspect(aspect);
	}

	static float getPanRatioL() {
		if (BasiliskMain.orientationSel==0) {
			//portrait
			return 0.2f;
		}
		else
		{
			//landscape
			return 0.1f;
		}
	}
	
	static float getPanRatioH() {
		if (BasiliskMain.orientationSel==0) {
			//portrait
			return 0.8f;
		}
		else
		{
			//landscape
			return 0.9f;
		}		
	}
	
	static float getCenterRatioL() {
		if (BasiliskMain.orientationSel==0) {
			//portrait
			return 0.3f;
		}
		else
		{
			//landscape
			return 0.1f;
		}		
	}

	static float getCenterRatioH() {
		if (BasiliskMain.orientationSel==0) {
			//portrait
			return 0.7f;
		}
		else
		{
			//landscape
			return 0.9f;
		}		
	}


	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		

		// TODO: add multitouch support (added in Android 2.0 SDK)
		int action = -1;
		int eventx = (int) event.getX();
		int eventy = (int) event.getY();
		int lr=0;
		int ud=0;
		float ratioPL = this.getPanRatioL();
		float ratioPH = this.getPanRatioH();
		float ratioCL = this.getCenterRatioL();
		float ratioCH = this.getCenterRatioH();

		//tap tap only if close to the center
		if ((eventx > ratioPL * dosWinX) && 
			(eventx < ratioPH * dosWinX) && 
			(eventy > ratioPL * dosWinY) &&
			(eventy < ratioPH * dosWinY)) 
			{
				if (!BasiliskMain.lmbLock) { 
					if (detector.onTouchEvent(event)) {
						/*
						Log.v("droidmac", "tap tap");
						if (tapClick==0) tapClick=1;
						else
						{
							SystemClock.sleep(250);
						}						
						DemoGLSurfaceView.nativeMouse(0,0, 0,2);
						SystemClock.sleep(100);
						DemoGLSurfaceView.nativeMouse(0,0, 1,2);
						//SystemClock.sleep(100);
						tapClick=0;*/

						//tap confirmed or double tap
						if (MacGestureListener.lastGesture==MacGestureListener.SINGLETAP)
						{
							DemoGLSurfaceView.nativeMouse(0,0, 0,2);
							SystemClock.sleep(100);
							DemoGLSurfaceView.nativeMouse(0,0, 1,2);							
						}
						if (MacGestureListener.lastGesture==MacGestureListener.DOUBLETAP)
						{
							DemoGLSurfaceView.nativeMouse(0,0, 0,2);
							SystemClock.sleep(100);
							DemoGLSurfaceView.nativeMouse(0,0, 1,2);
							SystemClock.sleep(100);
							DemoGLSurfaceView.nativeMouse(0,0, 0,2);
							SystemClock.sleep(100);
							DemoGLSurfaceView.nativeMouse(0,0, 1,2);
						}
						MacGestureListener.lastGesture=MacGestureListener.NOGESTURE;
						return true;
					}
					else if (MacGestureListener.lastGesture==MacGestureListener.LONGPRESS) {
						//long press
						
					}
				}
			}
		
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			action = 0;
			
			if (eventx >ratioCL * dosWinX && eventx <ratioCH*dosWinX && eventy > ratioCL * dosWinY && eventy < ratioCH * dosWinY) {								
				//landscape mode, simulat mouse movement
				Log.v("myTag", "---------dos screen down-----------");
				startX=event.getX();
				startY=event.getY();
				lastX=startX;
				lastY=startY;
				startedMove=true;
			}
		}
		if (event.getAction()==MotionEvent.ACTION_UP) {
			if (startedMove) {
				Log.v("myTag", "---------dos ^^^^^^^^^^^^^^-----------");
				startX=-1;
				startY=-1;
				startedMove=false;
				return true;
			}
		}
		if (startedMove) {
			
			float dX = event.getX()- lastX;
			float dY = event.getY()- lastY;
				
			lastX=event.getX();
			lastY=event.getY();
			nativeMouse((int)(DosPanelListener.getMouseSense()*dX), (int)(DosPanelListener.getMouseSense()*dY), 2,2);
			Log.v("myTag", "dos screen tpad delta:"+dX+","+dY);
		}
		
		return true;
	}

	public void exitApp() {
		mRenderer.exitApp();
	};

	@Override
	public boolean onKeyDown(int keyCode, final KeyEvent event) {
		nativeKey(keyCode, 1);
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, final KeyEvent event) {
		nativeKey(keyCode, 0);
		return true;
	}

	DemoRenderer mRenderer;
	Activity mParent;

	public static native void nativeMouse(int x, int y, int laction, int raction);
	//public static native void stylusMouseMove(int x, int y, int action);
	//public static native void stylusMouseSet(int x, int y);
	
	public static native void nativeKey(int keyCode, int down);
	
	//public static native int getMousePosX();	
	//public static native int getMousePosY();
	
	public static native int getDosSizeX();
	public static native int getDosSizeY();
	//public static native int sdlMousePosX();
	//public static native int sdlMousePosY();
	//public static native void updateDosMouse();
	
	public static native void panVirtualWindow(int lr, int ud);
	public static native void dosFollowMouse(int fm);	
	public static native void dosOversizeMode(int mode);
	public static native void nativeExit();

}