package org.tomaswoj.basilisk;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class MacGestureListener extends SimpleOnGestureListener {
	
	static int tapCount=0;
	static final int NOGESTURE=0;
	static final int SINGLETAP=1;
	static final int DOUBLETAP=2;
	static final int LONGPRESS=3;
	
	static int lastGesture=0;
	//0 - no gesture
	//1 - single tap
	//2 - double tap
	@Override
	public boolean onSingleTapConfirmed(MotionEvent ev) {
	    Log.d("onSingleTapConfirmed",ev.toString());
	    //if (tapCount=)
	    lastGesture = MacGestureListener.SINGLETAP;
	    return true;
	}	

	public boolean onDoubleTap(MotionEvent ev) {
	    Log.d("onDoubleTap",ev.toString());
	    //if (tapCount=)
	    lastGesture = MacGestureListener.DOUBLETAP;
	    return true;
	}	
	
	public void onLongPress(MotionEvent ev) {
	    Log.d("onLongPress",ev.toString());
	    lastGesture = MacGestureListener.LONGPRESS;
	    //if (tapCount=)	    
	}	
}
