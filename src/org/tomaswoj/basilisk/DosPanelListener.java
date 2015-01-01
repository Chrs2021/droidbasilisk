package org.tomaswoj.basilisk;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

public class DosPanelListener implements OnTouchListener {
	
	public SurfaceView splatView;
	public Activity mainActivity;
	public LinearLayout mainView;
	
	public DemoGLSurfaceView dosView;

	public float startX=-1;
	public float startY=-1;
	public float lastX;
	public float lastY;
	public int dosPosX;
	public int dosPosY;
	public static int tpadX;
	public static int tpadY;
	public static boolean startedMove = true;
	public static float mouseSense = 0.5f;
	
	public void setSplatView(SurfaceView nSplatView) {
		splatView = nSplatView;
		
	}
	
	public static void setMouseSense(float nSense) {
		mouseSense = nSense;
	}
	
	public static float getMouseSense() {
		return mouseSense;
	}
	
 	public void setDosView(DemoGLSurfaceView nDosView) {
		dosView = nDosView;
	}
	
	public void setTpadSize(int nTpadX, int nTpadY){
		tpadX = nTpadX;
		tpadY = nTpadY;
	}
	
	public void setMainView(LinearLayout nview) {
		mainView = nview;
	}
	public void setActivity(Activity nActivity) {
		mainActivity=nActivity;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if (arg1.getAction()==MotionEvent.ACTION_DOWN) {
			//Log.v("myTag", "---------touchpad down-----------");
			startX=arg1.getX();
			startY=arg1.getY();
			lastX=startX;
			lastY=startY;
			
			//Log.v("droiddos","touch down at:"+startX+", "+startY);
			//Log.v("droiddos","tpad size:"+tpadX+", "+tpadY);
			
			//note down, if its a move (from the middle) or edge push (for mouse +-1)
			/*if (startX<tpadX*0.2f ||startX>tpadX*0.8f || startY<tpadY*0.2f || startY>tpadY*0.8f ) {
				//its not a move, its a smallstep
				startedMove = false;
				//do a smallstep;
				int ud=0;
				int lr=0;
				if (startX<tpadX*0.2f) lr = -1;
				if (startX>tpadX*0.8f) lr = 1;
				if (startY>tpadY*0.8f) ud = 1;
				if (startY<tpadY*0.2f) ud = -1;
				dosView.nativeMouse(lr, ud, 2,2);
				//Log.v("droiddos", "edge move");
			}
			else 
			{*/	
				startedMove = true;
				//Log.v("droiddos", "started long move");
			//}
		}
		
		if (arg1.getAction()==MotionEvent.ACTION_UP) {
			Log.v("myTag", "---------^^^^^^^^^^^^^^-----------");
			startX=-1;
			startY=-1;
			return true;
		}
		
		if (startedMove) {
			float xTouch = arg1.getX();
			float yTouch = arg1.getY();
		
			float dX = arg1.getX()- lastX;
			float dY = arg1.getY()- lastY;
			
			lastX=arg1.getX();
			lastY=arg1.getY();
			dosView.nativeMouse((int)(mouseSense*dX), (int)(mouseSense*dY), 2,2);
			Log.v("basilisk","dmouse,"+dX+","+dY);
			//Log.v("droiddos", "continuous long move");
		
		}
		
		if (splatView!=null) {
		//	splatView.
		}
		return true;
	}	

}
