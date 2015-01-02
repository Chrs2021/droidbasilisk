package org.tomaswoj.basilisk;

import java.util.ArrayList;


import org.tomaswoj.basilisk.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BasiliskMain extends Activity implements OnClickListener, OnLongClickListener, OnTouchListener{
	
	static final int DIALOG_MEMSIZE = 0;
	static final int DIALOG_SCREEN = 1;
	static final int DIALOG_CPU = 2;
	static final int DIALOG_FRAMESKIP=17;
	static final int DIALOG_MODEL=18;
	static final int DIALOG_XMS=19;
	static final int DIALOG_ABOUT = 3;
	static final int DIALOG_EXIT=99;
	static final int DIALOG_DPADSIZE=10;
	static final int DIALOG_QWERTYSIZE=11;
	static final int DIALOG_SHIFTSIZE=21;
	static final int DIALOG_DOSSIZE=12;
	static final int DIALOG_DOSFILTERING=13;
	static final int DIALOG_QWERTYLAYOUT=14;
	static final int DIALOG_BUTTONSIZE=15;
	static final int DIALOG_TPADSIZE=20;
	static final int DIALOG_CONFMIS=30;
	static final int DIALOG_CONFCORR=31;
	static final int DIALOG_ORIENTATION=35;
	static final int DIALOG_ASPECT=36;
	static final int DIALOG_DISK=37;
		
	static final int QWERTY_PANE=1;
	static final int SHIFT_PANE=2;
	static final int DPAD_PANE=3;
	
	static final int SDL_LALT=308;
	static final int SDL_RSHIFT=303; 
	
	public static int paneSelected=0;
	
	public static int tapClick=0;
		
	public static int dosPosX;
	public static int dosPosY;
	public GUISettings settings;
	public MacSettings macSettings;
	
	public static final String ApplicationName = "basilisk";
	public static final String APP_PREFS = "BasiliskPrefs";
	private DemoGLSurfaceView mGLView;
	private PowerManager.WakeLock wakeLock;
	
	private LinearLayout lin01;
	private LinearLayout lowPanel;
	private TelephonyManager tm;
	
	static int ramSizeSel = 0;
	static int modelSel = 0;
	static int cpuTypeSel = 0;
	static int frameskipSel = 0;
	static int dpadSizeSel = 0;
	static int qwertySizeSel=0;
	static int dosSizeSel=0;
	static int dosFilteringSel=0;
	static int mouseX=0;
	static int mouseY=0;
	static int qwertyLayoutSel=0;
	static int buttonSizeSel=0;
	static int tpadSizeSel=0;
	static int shiftSizeSel=0;
	static int orientationSel=0;
	static int aspectSel=0; //0 - no aspect, 1 - keep aspect
	static float mouseSense=1.0f;
	static int frameskip = 1;
	static long stoperStart;
	static int screenSel=1;
	static int diskSel=1;
	
	static int dosWinX = 480;
	static int dosWinY = 360;
	static boolean lmbLock = false;
	static boolean rmbLock = false;	
	
	static Button mplus;
	static Button mminus;
	static Button kbplus;
	static Button fm;
	static Button kbminus;
	static Button mainLMB;
	static Button mainEnter;
	static DosPanelListener newPanel;
	
	//cmd and shift locks
	static int cmdMode=0; // 0 - none, 1 - shortlong, 2 - longlock
	static int shiftMode=0; // 0 - none, 1 - shortlong, 2 - longlock
	static Button cmdModeBut;
	static Button shiftModeBut;

	static {
		System.loadLibrary(ApplicationName);
	}

    private Handler mHandler = new Handler();
    private Handler cpuHandler = new Handler();
    
    private TextView cpuText;
    private TextView frameskipText;
    

    private Runnable mUpdateTask = new Runnable()
    {
        public void run()
        {
            //Log.v("droiddos", "repeat click fm");
            mHandler.postAtTime(this, SystemClock.uptimeMillis() + 100);
        }
    }; 

    private Runnable mCpuTask = new Runnable()
    {
        public void run()
        {
            //Log.v("droiddos", "readcpu");
            int perc = (int) CPULoad.getUsage();
            Integer percInt = new Integer(perc);            
            //cpu.setText(percInt.toString());
            mHandler.postAtTime(this, SystemClock.uptimeMillis() + 5000);
        }
    }; 
    
    private Runnable mStoperTask = new Runnable()
    {
        public void run()
        {
            //Log.v("droiddos", "readcpu");
        	int seconds = (int)((SystemClock.uptimeMillis()-stoperStart)/1000);
            Integer percInt = new Integer(seconds);            
            //cpu.setText(percInt.toString());
            mHandler.postAtTime(this, SystemClock.uptimeMillis() + 1000);
        }
    }; 
    
    
    private DosButtonTask myUpdateTask = new DosButtonTask();
    
    private PhoneStateListener mPhoneListener = new PhoneStateListener() 
    {  
    	@Override
    	public void onCallStateChanged(int state, String incomingNumber) 
    	{   
    		try 
    		{    
    			switch (state) 
    			{    
    			case TelephonyManager.CALL_STATE_RINGING:
    				Log.v("droidmac", "phone is ringing");
    				moveTaskToBack(true);
    				break;    
    			case TelephonyManager.CALL_STATE_OFFHOOK:     
    				
    				break;    
    			case TelephonyManager.CALL_STATE_IDLE:     
    				     
    				break;    
    			default:    
   
    				Log.i("Default", "Unknown phone state=" + state);    
    			}   
    		} catch (Exception e) 
    			{    
    			Log.i("Exception", "PhoneStateListener() e = " + e);   
    			}  
    	}
    };
    
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    SharedPreferences prefs = getSharedPreferences(APP_PREFS, 0);
	    dpadSizeSel = prefs.getInt("dpadSizeSel", 14);
	    qwertySizeSel = prefs.getInt("qwertySizeSel",14);
	    qwertyLayoutSel = prefs.getInt("qwertyLayoutSel", 0);
	    //Log.v("droiddos","qwerty layout sel:"+qwertyLayoutSel);
	    dosSizeSel = prefs.getInt("dosSizeSel", 0);
	    //Log.v("droiddos","dos size:"+dosSizeSel);
	    dosFilteringSel = prefs.getInt("dosFilteringSel", 0);	    
	    //Log.v("droiddos","dos filtering:"+dosFilteringSel);
	    buttonSizeSel = prefs.getInt("buttonSizeSel", 0);
	    tpadSizeSel = prefs.getInt("tpadSizeSel", 0);
	    shiftSizeSel = prefs.getInt("shiftSizeSel", 14);
	    orientationSel = prefs.getInt("orientationSel", 0);
	    aspectSel = prefs.getInt("aspectSel", 0);	   
	    settings.setDpadSize(dpadSizeSel);
	    settings.setQwertySize(qwertySizeSel);
	    settings.setQwertyLayout(qwertyLayoutSel);
	    settings.setDosWindowSize(dosSizeSel);
	    settings.setDosFiltering(dosFilteringSel);
	    settings.setButtonSize(buttonSizeSel);
	    settings.setTpadSize(tpadSizeSel);
	    settings.setShiftSize(shiftSizeSel);
	    settings.setOrientation(orientationSel);
	
	    //intercept phone calls
	    tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	    
	    tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	    
	    //now set app orientation after settings
	    if (settings.getOrientation()==0) {
	    	Log.v("basil","portrait orientation");
	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	    else
	    {	    	
	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	    	Log.v("basil","landscape orientation");
	    	settings.setDosWindowSize(4);
	    }
	    newPanel = new DosPanelListener();

	    //read dos settings from file:
	    int readconf = macSettings.parseConfig();
	    //Log.v("droiddos","readconf:"+readconf);
	    if (readconf > 0)
	    {
	    	//something went wrong, display alert, and quit;
	    	//if 1 -> no file
	    	if (readconf==1) 
	    	{
	    		showDialog(DIALOG_CONFMIS);
	    	}
	    	else
	    	{
	    		showDialog(DIALOG_CONFCORR);
	    	}
	    }
	    
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//getWindow().addFlags(W)
		

		mGLView = new DemoGLSurfaceView(this, settings.getDosWindowX(), settings.getDosWindowY(), settings.getDosFiltering());
		

		settings = new GUISettings();
		
		LayoutParams dosWindowParams = new LayoutParams(settings.getDosWindowX(), settings.getDosWindowY());
		mGLView.setLayoutParams(dosWindowParams);
		mGLView.dosOversizeMode(1); 
		setContentView(R.layout.main);

		if (settings.getOrientation()==0) {
			fm = (Button) findViewById(R.id.marker_fm);
			fm.setTextSize(settings.getButtonSize());
			Button tpadB= (Button) findViewById(R.id.goTpad);
			tpadB.setTextSize(settings.getButtonSize());

		}
		mplus = (Button) findViewById(R.id.kb_mplus);
		mplus.setTextSize(settings.getButtonSize());
		mminus = (Button) findViewById(R.id.kb_mminus);
		mminus.setTextSize(settings.getButtonSize());
		kbplus = (Button) findViewById(R.id.kb_splus);
		kbplus.setTextSize(settings.getButtonSize());
		kbminus = (Button) findViewById(R.id.kb_sminus);
		kbminus.setTextSize(settings.getButtonSize());
		
		//these buttons are not present in portrait view
		if (settings.getOrientation()!=0) {
			mainLMB = (Button) findViewById(R.id.kb_lmbmain);
			mainEnter = (Button) findViewById(R.id.kb_entermain);
			mainLMB.setOnClickListener(this);
			mainLMB.setOnLongClickListener(this);
			mainLMB.setTextSize(settings.getButtonSize());
			mainEnter.setOnClickListener(this);
			mainEnter.setTextSize(settings.getButtonSize());
			
			//cmd button
			cmdModeBut = (Button) findViewById(R.id.kb_cmd);
			cmdModeBut.setOnClickListener(this);
			cmdModeBut.setOnLongClickListener(this);
			cmdModeBut.setTextSize(settings.getButtonSize());
			
			//shift button
			shiftModeBut = (Button) findViewById(R.id.kb_shift);
			shiftModeBut.setOnClickListener(this);
			shiftModeBut.setOnLongClickListener(this);
			shiftModeBut.setTextSize(settings.getButtonSize());

		}
						
		if (macSettings.getStoper()) {
			stoperStart = SystemClock.uptimeMillis();
			cpuHandler.postAtTime(mStoperTask, SystemClock.uptimeMillis() + 1000);
			kbminus.setOnClickListener(this);
		}
		else 
		{
		cpuHandler.postAtTime(mCpuTask, SystemClock.uptimeMillis() + 5000);
		}

		Button nButton = (Button) findViewById(R.id.goQwerty);
		nButton.setTextSize(settings.getButtonSize());
		nButton = (Button) findViewById(R.id.goShift);
		nButton.setTextSize(settings.getButtonSize());
		nButton = (Button) findViewById(R.id.goDpad);
		nButton.setTextSize(settings.getButtonSize());
		//nButton = (Button) findViewById(R.id.marker_fm);
		//nButton.setTextSize(settings.getButtonSize());
		//nButton = (Button) findViewById(R.id.kb_m1);
		//nButton.setTextSize(settings.getButtonSize());
		//nButton = (Button) findViewById(R.id.kb_m2);
		//nButton.setTextSize(settings.getButtonSize());
		//nButton = (Button) findViewById(R.id.kb_m3);
		//nButton.setTextSize(settings.getButtonSize());
		//nButton = (Button) findViewById(R.id.kb_cpu);
		//nButton.setTextSize(settings.getButtonSize());
		

		
		//fm.setOnClickListener(this);		
        lin01 = (LinearLayout) findViewById(R.id.lin01);
        lowPanel = (LinearLayout) findViewById(R.id.linchild);

        if (lin01 != null) {
            ArrayList<View> viewList = lin01.getTouchables();
            //Log.v("myTag","list of touchables:"+viewList.size());
            if (!viewList.isEmpty()) {
            	
	            for (View v:viewList) {
	            	if (v.getTag().toString().contains("go"))
	            	{
	            		v.setOnLongClickListener(this);
	            	}
	            }
	            for (View v:viewList) {
	            	if (v.getTag().toString().contains("kb_"))
	            	{
	            		v.setOnClickListener(this);
	            	}
	            }

            }
         }
        
		LinearLayout dosPanel = (LinearLayout) findViewById(R.id.dosPanel);
		LayoutParams dosPanelParams = new LayoutParams(settings.getDosWindowX(), settings.getDosWindowY());
		//dosPanel.setLayoutParams(dosPanelParams);
		dosPanel.addView(mGLView);
		// Receive keyboard events
		//mGLView.setFocusableInTouchMode(true);
		//mGLView.setFocusable(true);
		//mGLView.setOnTouchListener(this);
		//mGLView.requestFocus();
		
		myUpdateTask.setDosView(this.mGLView);
		myUpdateTask.setHandler(this.mHandler);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				ApplicationName);
		wakeLock.acquire();
	}

	public static void switchFM(int mode) {
		if (mode==0) fm.setBackgroundResource(R.drawable.switch_button);
		if (mode==1) fm.setBackgroundResource(R.drawable.selswitch_button);
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	
	public static int getCmdMode() {
		return cmdMode;
	}
	
	public static void setCmdMode(int nMode) {
		Log.v("mac","setting cmdmode:"+nMode);
		switch (nMode) {
			case 0:
				cmdMode=0;
				cmdModeBut.setBackgroundResource(R.drawable.switch_button);
				break;
			case 1:
				cmdMode=1;
				cmdModeBut.setBackgroundResource(R.drawable.selswitch_button);
				break;
			case 2:
				cmdMode=2;
				cmdModeBut.setBackgroundResource(R.drawable.selswitch_button);				
				break;
			default:
				cmdMode=0;
				cmdModeBut.setBackgroundResource(R.drawable.switch_button);
				break;
		}
	}

	public static int getShiftMode() {
		return shiftMode;
	}
	
	public static void setShiftMode(int nMode) {
		Log.v("mac","setting shiftmode:"+nMode);
		switch (nMode) {
			case 0:
				shiftMode=0;
				shiftModeBut.setBackgroundResource(R.drawable.switch_button);
				break;
			case 1:
				shiftMode=1;
				shiftModeBut.setBackgroundResource(R.drawable.selswitch_button);
				break;
			case 2:
				shiftMode=2;
				shiftModeBut.setBackgroundResource(R.drawable.selswitch_button);				
				break;
			default:
				shiftMode=0;
				shiftModeBut.setBackgroundResource(R.drawable.switch_button);
				break;
		}
	}
	
	public void setMouseFollow(boolean mouseFollow) {
		//find view.
		Button mfButton = (Button) findViewById(R.id.marker_fm);
		if (mouseFollow) mfButton.setBackgroundResource(R.drawable.selswitch_button);
		if (!mouseFollow) mfButton.setBackgroundResource(R.drawable.switch_button);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//Log.v("myTag", "clicked");
		String buTag = v.getTag().toString();
		int keycode=0;
		int shiftcode = 0;	

		keycode = SdlKeycodeMapper.getKeyCode(buTag);
		
		
		if (keycode!=0) {
			shiftcode = SdlShiftMapper.getShiftCode(buTag);
			if (shiftcode==0) {
				//check for cmd
				if (getCmdMode()>0) {
					DemoGLSurfaceView.nativeKey(this.SDL_LALT, 1);
				}
				if (getShiftMode()>0) {
					DemoGLSurfaceView.nativeKey(this.SDL_RSHIFT, 1);
				}				
				DemoGLSurfaceView.nativeKey(keycode,1);
				//sleep me
				SystemClock.sleep(50);
				DemoGLSurfaceView.nativeKey(keycode,0);
				if (getShiftMode()>0) {
					DemoGLSurfaceView.nativeKey(this.SDL_RSHIFT, 0);
					if (getShiftMode()==1) setShiftMode(0);
				}
				if (getCmdMode()>0) {
					DemoGLSurfaceView.nativeKey(this.SDL_LALT, 0);
					if (getCmdMode()==1) setCmdMode(0);
				}
				
			}
			else
			{
				//shiftme first				
				DemoGLSurfaceView.nativeKey(shiftcode,1);
				SystemClock.sleep(50);
				DemoGLSurfaceView.nativeKey(keycode,1);
				SystemClock.sleep(50);
				DemoGLSurfaceView.nativeKey(keycode,0);
				//SystemClock.sleep(100);
				DemoGLSurfaceView.nativeKey(shiftcode,0);
				/* 
				//update some label stuff (cpu/frameskip)
				if (shiftcode==306) {
					switch (keycode) {
					case 288: //fsup
						frameskip+=1;
						frameskipText.setText("Frameskip:"+frameskip);
						break;
					case 289: //fsdown
						frameskip-=1;
						frameskipText.setText("Frameskip:"+frameskip);
						break;
					case 292: //cycup
						cpuCycles+=100;
						cpuText.setText("Cycles:"+cpuCycles);
						break;
					case 293: //cycdown
						cpuCycles-=100;
						cpuText.setText("Cycles:"+cpuCycles);						
						break;
						default:
							break;
					}
				}
				*/
			}
		}

		else {
			//try if its not something special
		if (buTag.equals("kb_lmb")) {
			if (!lmbLock) {
			DemoGLSurfaceView.nativeMouse(0,0, 0,2);
			SystemClock.sleep(100);
			DemoGLSurfaceView.nativeMouse(0,0, 1,2);
			}
		}

		if (buTag.equals("kb_rmb")) {
			
			if (!rmbLock) {
			DemoGLSurfaceView.nativeMouse(0,0, 2,0);
			SystemClock.sleep(100);
			DemoGLSurfaceView.nativeMouse(0,0, 2,1);
			}
		}
		
		if (buTag.equals("kb_lmb2x")) {
			//mouseX = DemoGLSurfaceView.getMousePosX();
			//mouseY = DemoGLSurfaceView.getMousePosY();

			DemoGLSurfaceView.nativeMouse(0,0, 0,2);
			SystemClock.sleep(100);
			DemoGLSurfaceView.nativeMouse(0,0, 1,2);
			SystemClock.sleep(200);
			DemoGLSurfaceView.nativeMouse(0,0, 0,2);
			SystemClock.sleep(100);
			DemoGLSurfaceView.nativeMouse(0,0, 1,2);
		}
		
		if (buTag.equals("kb_pl")) DemoGLSurfaceView.panVirtualWindow(1,0);			
		if (buTag.equals("kb_pr")) DemoGLSurfaceView.panVirtualWindow(2,0);
		if (buTag.equals("kb_pu")) DemoGLSurfaceView.panVirtualWindow(0,1);
		if (buTag.equals("kb_pd")) DemoGLSurfaceView.panVirtualWindow(0,2);
		
		if (buTag.equals("kb_fm")) {
			if (DemoGLSurfaceView.mouseLock)
			{
				DemoGLSurfaceView.mouseLock = false;
				DemoGLSurfaceView.dosFollowMouse(0);
				BasiliskMain.switchFM(0);
				Log.v("droiddos", "follow mouse set to false");
			}
			else
			{
				DemoGLSurfaceView.mouseLock = true;
				DemoGLSurfaceView.dosFollowMouse(1);
				BasiliskMain.switchFM(1);
				Log.v("droiddos", "follow mouse set to true");
			}

		}

		if (buTag.equals("kb_mplus")) {
			mouseSense+=0.5f;
			//mplus.setBackgroundResource(R.drawable.selswitch_button);
			//mminus.setBackgroundResource(R.drawable.switch_button);
			//kbplus.setBackgroundResource(R.drawable.switch_button);
			newPanel.setMouseSense(this.mouseSense);
			
		}
		if (buTag.equals("kb_mminus")) {
			mouseSense-=0.5f;
			//mplus.setBackgroundResource(R.drawable.switch_button);
			//mminus.setBackgroundResource(R.drawable.selswitch_button);
			//kbplus.setBackgroundResource(R.drawable.switch_button);
			newPanel.setMouseSense(this.mouseSense);
		}
		
		if (buTag.equals("kb_cmd")){
			//if 0 -> 1
			if (getCmdMode()==0) { 
				setCmdMode(1);
				return;
				}			
			//if 1 -> 0
			if (getCmdMode()==1) {
				setCmdMode(0);
				return;
			}
			//if 2 -> 2
			// --
		}

		if (buTag.equals("kb_shift")){
			//if 0 -> 1
			if (getShiftMode()==0) {
				setShiftMode(1);
				return;
			}
			//if 1 -> 0
			if (getShiftMode()==1) { setShiftMode(0);
			return;
			}
			//if 2 -> 2
			// --
		}

		if (buTag.equals("kb_splus")) {
			//bigger keyboard
			if (paneSelected>0) {
    	        SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
    	        SharedPreferences.Editor editor = settings.edit();
    			Button myBu;
    			LinearLayout newLayout;
    	            	        
				switch (paneSelected) {
				case QWERTY_PANE:					
					GUISettings.setQwertySize(GUISettings.getQwertySize()+1);
	    	        editor.putInt("qwertySizeSel", GUISettings.getQwertySize());
	    			newLayout = (LinearLayout) findViewById(R.id.qwertyLin);
	    	        if (newLayout != null) {
	    	            ArrayList<View> viewList = newLayout.getTouchables();	    	            
	    	            if (!viewList.isEmpty()) {	    	            	
	    		            for (View vi:viewList) {
	    		            	if (vi.getTag().toString().contains("kb_"))
	    		            	{	    		            		
	    		            		myBu = (Button) vi;		            		
	    		            		myBu.setTextSize(GUISettings.getQwertySize());
	    		            	}
	    		            }
	    	            }
	    	         }	
					break;
				case SHIFT_PANE:
					GUISettings.setShiftSize(GUISettings.getShiftSize()+1);
					editor.putInt("shiftSizeSel", GUISettings.getShiftSize());
	    			newLayout = (LinearLayout) findViewById(R.id.shiftLin);
	    	        if (newLayout != null) {
	    	            ArrayList<View> viewList = newLayout.getTouchables();	    	            
	    	            if (!viewList.isEmpty()) {	    	            	
	    		            for (View vi:viewList) {
	    		            	if (vi.getTag().toString().contains("kb_"))
	    		            	{	    		            		
	    		            		myBu = (Button) vi;		            		
	    		            		myBu.setTextSize(GUISettings.getShiftSize());
	    		            	}
	    		            }
	    	            }
	    	         }						
					break;
				case DPAD_PANE:
					GUISettings.setDpadSize(GUISettings.getDpadSize()+1);
					editor.putInt("dpadSizeSel", GUISettings.getDpadSize());
	    			newLayout = (LinearLayout) findViewById(R.id.dpadLin);
	    	        if (newLayout != null) {
	    	            ArrayList<View> viewList = newLayout.getTouchables();	    	            
	    	            if (!viewList.isEmpty()) {	    	            	
	    		            for (View vi:viewList) {
	    		            	if (vi.getTag().toString().contains("kb_"))
	    		            	{	    		            		
	    		            		myBu = (Button) vi;		            		
	    		            		myBu.setTextSize(GUISettings.getDpadSize());
	    		            	}
	    		            }
	    	            }
	    	         }	
					break;
					default:
					break;						
				}
    	        // Commit the edits!
    	        editor.commit();				
			}
		}
		if (buTag.equals("kb_sminus")) {
			//smaller keyboard			
			if (paneSelected>0) {
    	        SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
    	        SharedPreferences.Editor editor = settings.edit();
    			Button myBu;
    			LinearLayout newLayout;

				switch (paneSelected) {
				case QWERTY_PANE:					
					GUISettings.setQwertySize(GUISettings.getQwertySize()-1);
	    	        editor.putInt("qwertySizeSel", GUISettings.getQwertySize());
	    			newLayout = (LinearLayout) findViewById(R.id.qwertyLin);
	    	        if (newLayout != null) {
	    	            ArrayList<View> viewList = newLayout.getTouchables();	    	            
	    	            if (!viewList.isEmpty()) {	    	            	
	    		            for (View vi:viewList) {
	    		            	if (vi.getTag().toString().contains("kb_"))
	    		            	{	    		            		
	    		            		myBu = (Button) vi;		            		
	    		            		myBu.setTextSize(GUISettings.getQwertySize());
	    		            	}
	    		            }
	    	            }
	    	         }	
					break;
				case SHIFT_PANE:
					GUISettings.setShiftSize(GUISettings.getShiftSize()-1);
					editor.putInt("shiftSizeSel", GUISettings.getShiftSize());
	    			newLayout = (LinearLayout) findViewById(R.id.shiftLin);
	    	        if (newLayout != null) {
	    	            ArrayList<View> viewList = newLayout.getTouchables();	    	            
	    	            if (!viewList.isEmpty()) {	    	            	
	    		            for (View vi:viewList) {
	    		            	if (vi.getTag().toString().contains("kb_"))
	    		            	{	    		            		
	    		            		myBu = (Button) vi;		            		
	    		            		myBu.setTextSize(GUISettings.getQwertySize());
	    		            	}
	    		            }
	    	            }
	    	         }						
					break;
				case DPAD_PANE:
					GUISettings.setDpadSize(GUISettings.getDpadSize()-1);
					editor.putInt("dpadSizeSel", GUISettings.getDpadSize());
	    			newLayout = (LinearLayout) findViewById(R.id.dpadLin);
	    	        if (newLayout != null) {
	    	            ArrayList<View> viewList = newLayout.getTouchables();	    	            
	    	            if (!viewList.isEmpty()) {	    	            	
	    		            for (View vi:viewList) {
	    		            	if (vi.getTag().toString().contains("kb_"))
	    		            	{	    		            		
	    		            		myBu = (Button) vi;		            		
	    		            		myBu.setTextSize(GUISettings.getQwertySize());
	    		            	}
	    		            }
	    	            }
	    	         }	
					break;
					default:
					break;						
				}
    	        // Commit the edits!
    	        editor.commit();				
			}			
			}
		}		
	}

	@Override
	public boolean onLongClick(View arg0) {
		Button goQwerty = (Button) findViewById(R.id.goQwerty);
		Button goShift = (Button) findViewById(R.id.goShift);
		Button goTouchpad = (Button) findViewById(R.id.goTpad);
		Button goDpad = (Button) findViewById(R.id.goDpad);
    	Button myButton;
		// TODO Auto-generated method stub
		Log.v("myTag", "long clicked");
		if (arg0.getId()== R.id.goQwerty) {
			Log.v("myTag", "touched goQwerty");			
			lowPanel.removeAllViews();
			View inflatedView;
			
			if (settings.getQwertyLayout()==0) {
				inflatedView = getLayoutInflater().inflate(R.layout.qwerty, null);			
			}
			else {
				inflatedView = getLayoutInflater().inflate(R.layout.qwerty2, null);
			}

			LinearLayout newLayout = (LinearLayout) inflatedView.findViewById(R.id.qwertyLin);
			
			lowPanel.addView(newLayout);
	        if (newLayout != null) {
	            ArrayList<View> viewList = newLayout.getTouchables();
	            Log.v("myTag","list of touchables:"+viewList.size());
	            if (!viewList.isEmpty()) {
	            	

		            for (View v:viewList) {
		            	if (v.getTag().toString().contains("kb_"))
		            	{
		            		v.setOnClickListener(this);
		            		myButton = (Button) v;		            		
		            		myButton.setTextSize(GUISettings.getQwertySize());
		            	}
		            }
	            }
	         }	
	        paneSelected=this.QWERTY_PANE;
	        goQwerty.setBackgroundResource(R.drawable.selswitch_button);
	        goShift.setBackgroundResource(R.drawable.switch_button);
	        //goTouchpad.setBackgroundResource(R.drawable.switch_button);
	        goDpad.setBackgroundResource(R.drawable.switch_button);
		}

		if (arg0.getId()== R.id.goShift) {
			Log.v("myTag", "touched goShift");			
			lowPanel.removeAllViews();
			View inflatedView = getLayoutInflater().inflate(R.layout.shift, null);			

			LinearLayout newLayout= (LinearLayout) inflatedView.findViewById(R.id.shiftLin);
			
									
			lowPanel.addView(newLayout);			
	        if (newLayout != null) {
	            ArrayList<View> viewList = newLayout.getTouchables();
	            Log.v("myTag","list of touchables:"+viewList.size());
	            if (!viewList.isEmpty()) {
	            	
		            for (View v:viewList) {
		            	if (v.getTag().toString().contains("kb_"))
		            	{
		            		v.setOnClickListener(this);
		            		myButton = (Button) v;
		            		myButton.setTextSize(GUISettings.getShiftSize());
		            		
		            	}
		            }
	            }
	         }	
	        paneSelected=this.SHIFT_PANE;
	        goQwerty.setBackgroundResource(R.drawable.switch_button);
	        goShift.setBackgroundResource(R.drawable.selswitch_button);
	        //goTouchpad.setBackgroundResource(R.drawable.switch_button);
	        goDpad.setBackgroundResource(R.drawable.switch_button);

		}
		
		if (arg0.getId()== R.id.goTpad) {
			Log.v("myTag", "touched goTpad");			
			lowPanel.removeAllViews();
			View inflatedView = getLayoutInflater().inflate(R.layout.tpad, null);			

			LinearLayout newLayout= (LinearLayout) inflatedView.findViewById(R.id.tpadLin);
			SurfaceView touchView = (SurfaceView) inflatedView.findViewById(R.id.SurfaceView04);
			
			LayoutParams tpadParams = touchView.getLayoutParams();
			tpadParams.width=settings.getTpadX();
			tpadParams.height=settings.getTpadY();
			touchView.setLayoutParams(tpadParams);
						
			newPanel.setTpadSize(settings.getTpadX(), settings.getTpadY());
			newPanel.setMouseSense(this.mouseSense);
			touchView.setOnTouchListener(newPanel);
			
			lowPanel.addView(newLayout);			
	        if (newLayout != null) {
	            ArrayList<View> viewList = newLayout.getTouchables();
	            Log.v("myTag","list of touchables:"+viewList.size());
	            if (!viewList.isEmpty()) {
	            	
		            for (View v:viewList) {
		            	if (v.getTag().toString().contains("kb_"))
		            	{
		            		v.setOnClickListener(this);
		            		
		            	}
		            	if (v.getTag().toString().equals("kb_lmb"))
		            	{
		            		Log.v("droiddos","set on lonc click listener to kb_lmb");
		            		v.setOnLongClickListener(this);
		            	}
		            	if (v.getTag().toString().equals("kb_rmb"))
		            	{
		            		Log.v("droiddos","set on lonc click listener to kb_rmb");
		            		v.setOnLongClickListener(this);
		            	}


		            }
	            }
	         }	
	        goQwerty.setBackgroundResource(R.drawable.switch_button);
	        goShift.setBackgroundResource(R.drawable.switch_button);
	        //goTouchpad.setBackgroundResource(R.drawable.selswitch_button);
	        goDpad.setBackgroundResource(R.drawable.switch_button);

		}
		if (arg0.getId()== R.id.goDpad) {
			Log.v("myTag", "touched goDpad");			
			lowPanel.removeAllViews();
			View inflatedView = getLayoutInflater().inflate(R.layout.dpad, null);			

			LinearLayout newLayout = (LinearLayout) inflatedView.findViewById(R.id.dpadLin);
			
			
			lowPanel.addView(newLayout);
	        if (newLayout != null) {
	            ArrayList<View> viewList = newLayout.getTouchables();
	            Log.v("myTag","list of touchables:"+viewList.size());
	            if (!viewList.isEmpty()) {
	            	
		            for (View v:viewList) {
		            	if (v.getTag().toString().contains("kb_"))
		            	{
		            		if (v.getTag().toString().contains("kb_up") || 
		            			v.getTag().toString().contains("kb_down") ||
		            			v.getTag().toString().contains("kb_left") ||
		            			v.getTag().toString().contains("kb_right") ||
		            			v.getTag().toString().contains("kb_space") ||
		            			v.getTag().toString().contains("kb_enter"))
		            		{
		            			v.setOnTouchListener(this);
		            		}
		            		else 
		            		{
		            		v.setOnClickListener(this);
		            		}
		            		myButton = (Button) v;
		            		myButton.setTextSize(GUISettings.getDpadSize());
		            	}
		            }
	            }
	         }
	        paneSelected=this.DPAD_PANE;
	        goQwerty.setBackgroundResource(R.drawable.switch_button);
	        goShift.setBackgroundResource(R.drawable.switch_button);
	        //goTouchpad.setBackgroundResource(R.drawable.switch_button);
	        goDpad.setBackgroundResource(R.drawable.selswitch_button);

		}		

		if (arg0.getTag().toString().contains("kb_lmb")) {
			//lock/unlock
			Log.v("droiddos", "long lmb click");
			if (lmbLock) {
				//unlock it.
				lmbLock = false;
				DemoGLSurfaceView.nativeMouse(0,0, 1,2);
				arg0.setBackgroundResource(R.drawable.switch_button);
			}
			else {
				lmbLock = true;
				DemoGLSurfaceView.nativeMouse(0,0, 0,2);
				arg0.setBackgroundResource(R.drawable.selswitch_button);
			}			
			return true;
		}
		if (arg0.getTag().toString().contains("kb_rmb")) {
			//lock/unlock
			if (rmbLock) {
				//unlock it.
				rmbLock = false;
				DemoGLSurfaceView.nativeMouse(0,0, 2,1);
				arg0.setBackgroundResource(R.drawable.switch_button);
			}
			else {
				rmbLock = true;
				DemoGLSurfaceView.nativeMouse(0,0, 2,0);
				arg0.setBackgroundResource(R.drawable.selswitch_button);
			}	
			return true;
		}
		
		if (arg0.getTag().toString().contains("kb_cmd")){
			//if 0 -> 2
			if (getCmdMode()==0) { setCmdMode(2); return true; }			
			//if 2 -> 0
			if (getCmdMode()==2) { setCmdMode(0); return true; } 
			//if 1 -> 2
			if (getCmdMode()==1) { setCmdMode(2); return true; }
		}

		if (arg0.getTag().toString().contains("kb_shift")){
			//if 0 -> 2
			if (getShiftMode()==0) { setShiftMode(2); return true; }			
			//if 2 -> 0
			if (getShiftMode()==2) { setShiftMode(0); return true; }
			//if 1 -> 2
			if (getShiftMode()==1) { setShiftMode(2); return true;}
		}
		return false;
	}
	
	/*
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{     
		Log.v("droidmac", "button pressed:"+keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
			//moveTaskToBack(true);
			Log.v("droidmac", "ignoring these buttons");
			//to avoid accidental app close
			return true;     
			}     
		return super.onKeyDown(keyCode, event);
	} */

//	@Override
	@SuppressLint("Override")
	public void onBackPressed() {
		showDialog(DIALOG_EXIT);
	}
	
	@Override
	public void onUserLeaveHint() {
		Log.v("droidmac", "hinting a user that app is going background");
		showDialog(DIALOG_EXIT);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu,menu);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog dialog=null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
        case DIALOG_MEMSIZE:
            // do the work to define the pause Dialog
        	final CharSequence[] items = {"2MB", "4MB", "8MB", "16MB","24MB"};

        	ramSizeSel = MacSettings.getRamSize();
        	
        	builder.setTitle("Set RAM size (requires restart):");
        	builder.setSingleChoiceItems(items, ramSizeSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	ramSizeSel = item;
        	    	MacSettings.setRamSize(ramSizeSel);
        	    	MacSettings.saveConfig();
        	    	dialog.dismiss();
        	    }
        	});
        	dialog= builder.create();        	
            break;

        case DIALOG_MODEL:
            // do the work to define the pause Dialog
        	final CharSequence[] itemsModel = {"Mac IIci (MacOS 7.x)", "Quadra 900 (MacOS 8.x)"};
        	modelSel = MacSettings.getModel();
        	        	builder.setTitle("Set Mac model (requires restart):");
        	builder.setSingleChoiceItems(itemsModel, modelSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	modelSel = item;
        	    	MacSettings.setModel(modelSel);
        	    	MacSettings.saveConfig();
        	    	dialog.dismiss();
        	    }
        	});
        	dialog= builder.create();        	
            break;

        case DIALOG_ORIENTATION:
            // do the work to define the pause Dialog
        	final CharSequence[] itemsOrientation = {"Portrait", "Landscape"};
        	orientationSel = GUISettings.getOrientation();
        	        	builder.setTitle("Set app orientation (requires restart):");
        	builder.setSingleChoiceItems(itemsOrientation, orientationSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	orientationSel = item;
        	    	settings.setOrientation(orientationSel);
        	        SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
        	        SharedPreferences.Editor editor = settings.edit();
        	        editor.putInt("orientationSel", orientationSel);

        	        // Commit the edits!
        	        editor.commit();
        	    	dialog.dismiss();
        	    }
        	});
        	dialog= builder.create();        	
            break;

        case DIALOG_CPU:
            // do the work to define the pause Dialog
        	final CharSequence[] itemsCpuType = {"68020", "68020+FPU", "68030", "68030+FPU", "68040"};
        	cpuTypeSel = MacSettings.getCpuType();
        	        	builder.setTitle("Set CPU type (requires restart):");
        	builder.setSingleChoiceItems(itemsCpuType, cpuTypeSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	cpuTypeSel = item;
        	    	MacSettings.setCpuType(cpuTypeSel);
        	    	MacSettings.saveConfig();
        	    	dialog.dismiss();
        	    }
        	});
        	dialog= builder.create();        	
            break;

        case DIALOG_FRAMESKIP:
            // do the work to define the pause Dialog
        	final CharSequence[] itemsFrameskip = {"2 frames", "5 frames", "7 frames", "10 frames"};
        	frameskipSel = MacSettings.getFrameskip();
        	        	builder.setTitle("Set frameskip (requires restart):");
        	builder.setSingleChoiceItems(itemsFrameskip, frameskipSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	frameskipSel = item;
        	    	MacSettings.setFrameskip(frameskipSel);
        	    	MacSettings.saveConfig();
        	    	dialog.dismiss();
        	    }
        	});
        	dialog= builder.create();        	
            break;
            
        case DIALOG_DPADSIZE:
        	final CharSequence[] itemsSizeDpad = {"Small", "Medium", "Large", "Very Large"};
        	
        	builder.setTitle("Set Dpad size:");
        	builder.setSingleChoiceItems(itemsSizeDpad, dpadSizeSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	dpadSizeSel = item;
        	    	settings.setDpadSize(dpadSizeSel);
        	        SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
        	        SharedPreferences.Editor editor = settings.edit();
        	        editor.putInt("dpadSizeSel", dpadSizeSel);

        	        // Commit the edits!
        	        editor.commit();

        	    	dialog.dismiss();
        	    }
        	});
        	dialog= builder.create();  
        	break;
        	
        case DIALOG_SCREEN:
        	final CharSequence[] itemsSizeScreen = {"512x384", "640x480"};
            screenSel = MacSettings.getScreen();
        	builder.setTitle("Set Mac screen size (requires restart):");
        	builder.setSingleChoiceItems(itemsSizeScreen, screenSel, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int item) {
        		//
        		screenSel = item;
        		MacSettings.setScreen(screenSel);
        		MacSettings.saveConfig();
        		dialog.dismiss();
        		}
        	});
        	dialog= builder.create();        	
        	break;
        case DIALOG_DISK:
        	final CharSequence[] itemsDisk = {"No","Yes"};
            diskSel = MacSettings.getDisk();
        	builder.setTitle("Mount SDCard as external disk (requires restart):");
        	builder.setSingleChoiceItems(itemsDisk, diskSel, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int item) {
        		//
        		diskSel = item;
        		MacSettings.setDisk(diskSel);
        		MacSettings.saveConfig();
        		dialog.dismiss();
        		}
        	});
        	dialog= builder.create();        	
        	break;

        case DIALOG_SHIFTSIZE:
        	final CharSequence[] itemsSizeShift = {"Small", "Medium", "Large", "Very Large"};
        	
        	builder.setTitle("Set Dpad size:");
        	builder.setSingleChoiceItems(itemsSizeShift, shiftSizeSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	shiftSizeSel = item;
        	    	settings.setShiftSize(shiftSizeSel);
        	        SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
        	        SharedPreferences.Editor editor = settings.edit();
        	        editor.putInt("shiftSizeSel", shiftSizeSel);

        	        // Commit the edits!
        	        editor.commit();
        	    	dialog.dismiss();

        	    }
        	});
        	dialog= builder.create();  
        	break;

        case DIALOG_BUTTONSIZE:
        	final CharSequence[] itemsSizeButtons = {"Small", "Medium", "Large", "Very Large"};
        	
        	builder.setTitle("Set top buttons size (requires restart):");
        	builder.setSingleChoiceItems(itemsSizeButtons, buttonSizeSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	buttonSizeSel = item;
        	    	settings.setButtonSize(buttonSizeSel);
        	        SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
        	        SharedPreferences.Editor editor = settings.edit();
        	        editor.putInt("buttonSizeSel", buttonSizeSel);

        	        // Commit the edits!
        	        editor.commit();
        	    	dialog.dismiss();

        	    }
        	});
        	dialog= builder.create();  
        	break;

        	
        case DIALOG_QWERTYLAYOUT:
        	final CharSequence[] itemsQwertyLayout = {"11x6 buttons", "9x8 buttons"};
        	
        	builder.setTitle("Set Qwerty Layout:");
        	builder.setSingleChoiceItems(itemsQwertyLayout, qwertyLayoutSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	qwertyLayoutSel = item;
        	    	settings.setQwertyLayout(qwertyLayoutSel);
        	        SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
        	        SharedPreferences.Editor editor = settings.edit();
        	        editor.putInt("qwertyLayoutSel", qwertyLayoutSel);

        	        // Commit the edits!
        	        editor.commit();
        	    	dialog.dismiss();

        	    }
        	});
        	dialog= builder.create();  
        	break;
        	
        case DIALOG_DOSSIZE:
        	final CharSequence[] itemsSizeDos = {"HVGA (320x240)", "WVGA (480x360)", "QHD (540x405)", "Tablets (600x450)", "Full (640x480)"};
        	
        	builder.setTitle("Set Mac display size (requires restart):");
        	builder.setSingleChoiceItems(itemsSizeDos, dosSizeSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	dosSizeSel = item;
        	    	settings.setDosWindowSize(dosSizeSel);
        	        SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
        	        SharedPreferences.Editor editor = settings.edit();
        	        editor.putInt("dosSizeSel", dosSizeSel);

        	        // Commit the edits!
        	        editor.commit();
        	    	dialog.dismiss();

        	    }
        	});
        	dialog= builder.create();  
        	break;

        case DIALOG_TPADSIZE:
        	final CharSequence[] itemsTpad = {"HVGA (320x240)", "WVGA (480x360)", "QHD (540x405)"};
        	
        	builder.setTitle("Set touchpad size:");
        	builder.setSingleChoiceItems(itemsTpad, tpadSizeSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	tpadSizeSel = item;
        	    	settings.setTpadSize(tpadSizeSel);
        	        SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
        	        SharedPreferences.Editor editor = settings.edit();
        	        editor.putInt("tpadSizeSel", tpadSizeSel);

        	        // Commit the edits!
        	        editor.commit();
        	    	dialog.dismiss();

        	    }
        	});
        	dialog= builder.create();  
        	break;
        	
        case DIALOG_DOSFILTERING:
        	final CharSequence[] itemsDosFiltering = {"Sharp", "Smooth"};
        	
        	builder.setTitle("Set Mac display crispness:");
        	builder.setSingleChoiceItems(itemsDosFiltering, dosFilteringSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	dosFilteringSel = item;
        	    	settings.setDosFiltering(dosFilteringSel);
        	    	mGLView.setFiltering(settings.getDosFiltering());
        	        SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
        	        SharedPreferences.Editor editor = settings.edit();
        	        editor.putInt("dosFilteringSel", dosFilteringSel);

        	        // Commit the edits!
        	        editor.commit();
        	    	dialog.dismiss();

        	    }
        	});
        	dialog= builder.create();  
        	break;

        case DIALOG_ASPECT:
        	final CharSequence[] itemsDosAspect = {"Fit to screen", "Keep aspect"};
        	
        	builder.setTitle("Set Mac screen aspect:");
        	builder.setSingleChoiceItems(itemsDosAspect, aspectSel, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //
        	    	aspectSel = item;
        	    	settings.setDosAspect(aspectSel);
        	    	mGLView.setAspect(settings.getDosAspect());
        	        SharedPreferences settings = getSharedPreferences(APP_PREFS, 0);
        	        SharedPreferences.Editor editor = settings.edit();
        	        editor.putInt("aspectSel", aspectSel);

        	        // Commit the edits!
        	        editor.commit();
        	    	dialog.dismiss();

        	    }
        	});
        	dialog= builder.create();  
        	break;


        case DIALOG_ABOUT:
            // do the work to define the game over Dialog
        	
        	builder.setMessage(R.string.droidmac_about)
        	       .setCancelable(false)
        	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.dismiss();
        	           }
        	       });
        	dialog = builder.create();        	
            break;

            
        case DIALOG_EXIT:        	
        	builder.setMessage("Are you sure you want to exit?")
        	       .setCancelable(false)
        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   cpuHandler.removeCallbacks(mCpuTask);
        	        	   DemoGLSurfaceView.nativeExit();
        	        	   	//SystemClock.sleep(500);
        	                //DosBoxMain.this.finish();
        	           }
        	       })
        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	           }
        	       });
        	dialog = builder.create();        	
        	break;
        case DIALOG_CONFMIS:
    		builder = new AlertDialog.Builder(this);
    		builder.setMessage("Cannot find droidmac.prefs file (/sdcard/droidmac.prefs)! Please make sure it is there before next run. DroidDOS will now close.")
    		       .setCancelable(false)
    		       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                BasiliskMain.this.finish();
    		           }
    		       });
    		dialog = builder.create();	
        	break;
        case DIALOG_CONFCORR:
    		builder = new AlertDialog.Builder(this);
    		builder.setMessage("An exception occured while reading droidmac.prefs file (/sdcard/droidmac.prefs)! Please make sure it has valid content. DroidDOS will now close.")
    		       .setCancelable(false)
    		       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                BasiliskMain.this.finish();
    		           }
    		       });
    		dialog = builder.create();	
        	break;
        default:
            dialog = null;
        }
        return dialog;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.options_ram:
            showDialog(DIALOG_MEMSIZE);
            return true;           
        case R.id.options_cpu:
            showDialog(DIALOG_CPU);
            return true;
        case R.id.options_model:
            showDialog(DIALOG_MODEL);
            return true;
        case R.id.options_frameskip:
            showDialog(DIALOG_FRAMESKIP);
            return true;
        case R.id.options_screen:
            showDialog(DIALOG_SCREEN);
            return true;
        case R.id.menu_about:
            showDialog(DIALOG_ABOUT);
            return true;
        case R.id.menu_quit:
            showDialog(DIALOG_EXIT);
            return true;
            
        //case R.id.options_dpadsize:
        //	showDialog(DIALOG_DPADSIZE);
        //	return true;

        case R.id.options_buttonsize:
        	showDialog(DIALOG_BUTTONSIZE);
        	return true;

        case R.id.options_disk:
        	showDialog(DIALOG_DISK);
        	return true;

        //case R.id.options_qwertysize:
        //	showDialog(DIALOG_QWERTYSIZE);
        //	return true;

        //case R.id.options_shiftsize:
        //	showDialog(DIALOG_SHIFTSIZE);
        //	return true;

        case R.id.options_qwertylayout:
        	showDialog(DIALOG_QWERTYLAYOUT);
        	return true;

        case R.id.options_dossize:
        	showDialog(DIALOG_DOSSIZE);
        	return true;

        case R.id.options_dosfiltering:
        	showDialog(DIALOG_DOSFILTERING);
        	return true;

        case R.id.options_aspect:
        	showDialog(DIALOG_ASPECT);
        	return true;

        case R.id.options_tpadsize:
        	showDialog(DIALOG_TPADSIZE);
        	return true;

        case R.id.options_orientation:
        	showDialog(DIALOG_ORIENTATION);
        	return true;

        	/*
        case R.id.menu_restart:
        	mGLView = new DemoGLSurfaceView(this);
    		LayoutParams dosWindowParams = new LayoutParams(320, 200);
    		mGLView.setLayoutParams(dosWindowParams);
    		LinearLayout dosPanel = (LinearLayout) findViewById(R.id.dosPanel);
    		dosPanel.removeAllViews();
    		dosPanel.addView(mGLView);
        	return true;
        	*/
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		String buTag = v.getTag().toString();
		int keycode = 0;
		if (buTag.equals("kb_up")) keycode=273;
		if (buTag.equals("kb_down")) keycode=274;
		if (buTag.equals("kb_left")) keycode=276;
		if (buTag.equals("kb_right")) keycode=275;
		if (buTag.equals("kb_space")) keycode=32;
		if (buTag.equals("kb_enter")) keycode=13;
		
		if (keycode == 0) return true;
		/*
		if (event.getAction()==MotionEvent.ACTION_DOWN) 
		{
			DemoGLSurfaceView.nativeKey(keycode,1);
		}
		if (event.getAction()==MotionEvent.ACTION_DOWN) 
		{		
			DemoGLSurfaceView.nativeKey(keycode,0);
		}*/
        
		myUpdateTask.setSdlCode(keycode);
		
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            Log.v("droiddos", "MotionEvent.ACTION_DOWN on keypad");
            mHandler.removeCallbacks(myUpdateTask);
            mHandler.postAtTime(myUpdateTask, SystemClock.uptimeMillis() + 20);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            Log.v("droiddos", "MotionEvent.ACTION_UP on keypad");
            mHandler.removeCallbacks(myUpdateTask);
        }
        return false;
	}
    
}
