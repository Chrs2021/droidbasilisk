package org.tomaswoj.basilisk;

import android.os.Handler;
import android.os.SystemClock;

public class DosButtonTask implements Runnable {

	private Handler myHandler;
	private int sdlCode;
	private DemoGLSurfaceView dosView;
	
	public void setHandler(Handler nhandler) {
		myHandler=nhandler;
	}
	
	public void setSdlCode(int ncode) {
		sdlCode = ncode;
	}
	
	public void setDosView(DemoGLSurfaceView nDosView) {
		dosView = nDosView;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//post dos event
		dosView.nativeKey(sdlCode,1);
		SystemClock.sleep(50);
		dosView.nativeKey(sdlCode,0);
		myHandler.postAtTime(this,this, SystemClock.uptimeMillis() + 100 );
	}

}
