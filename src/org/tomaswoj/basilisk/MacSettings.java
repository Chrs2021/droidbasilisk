package org.tomaswoj.basilisk;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Environment;
import android.util.Log;
 
public class MacSettings {
	
	
	private static List<String> macLines;
	//memory size (1,2,4,8)
	private static int macRamSize=8388608;
	private static String macRamString="ramsize ";
	
	//2, 5, 7, 10
	private static int macFrameskip=5;
	private static String macFrameskipString="frameskip ";
	
	//mac model
	private static int macModel=14;
	private static String macModelString="modelid ";
	//14 - for Quadra 900(MacOS 8.x)
	//5 - for Mac IIci (MacOS 7.x)

	//core cycles (2000, 3000, 5000, 8000)
	private static int macCpuType=2;
	//2 - 68020
	//2 + fup - 68020 + FPU
	//3 - 68030 [+FPU]
	//4 - 68040 (automatically with FPU)
	private static String macCpuTypeString="cpu ";
	private static String macFPU="false";
	private static String macFPUString="fpu ";

	//stoper (for benchmarking)
	private static boolean stoper=false;
	private static String stoperString="stoper ";
	
	//screen (512x384 and 640x480 supported)
	private static String screenLow="screen win/512/384";
	private static String screenHigh="screen win/640/480";
	private static String screenString="screen";
	private static int screenSel = 1; //high res

	//external disk
	private static String diskOff="extfs";
	private static String diskOn="extfs /mnt/sdcard";	
	private static String diskString="extfs";
	private static int diskSel = 1; //on

	public static void setStoper(boolean nstoper) {
		stoper = nstoper;
	}
	
	public static boolean getStoper() {
		return stoper;
	}

	public static void setRamSize(int nsize) {
		switch (nsize) {
		case 0: //2mb
			macRamSize=2097152;
			break;
		case 1: //4mb
			macRamSize=4194304;
			break;
		case 2: //8mb
			macRamSize=8388608;
			break;
		case 3: //16mb
			macRamSize=16777216;
			break;
		case 4: //24mb
			macRamSize=25165824;
			break;
		default: //8mb
			macRamSize=8388608;			
		}
		
		//save the dos config file
	}
	
	public static int getRamSize() {
		if (macRamSize==2097152) return 0;
		if (macRamSize==4194304) return 1;
		if (macRamSize==8388608) return 2;
		if (macRamSize==16777216) return 3;
		if (macRamSize==25165824) return 4;
		return 2;
	}
	
		
	public static void setModel(int core) {
		if (core==0) macModel = 5;
		if (core==1) macModel = 14;		
	}
	
	public static int getModel() {
		if (macModel==5) return 0;
		if (macModel==14) return 1;
		return 0;
	}
	
	public static void setFrameskip(int nframeskip) {
		if (nframeskip==0) macFrameskip=2;
		if (nframeskip==1) macFrameskip=5;
		if (nframeskip==2) macFrameskip=7;
		if (nframeskip==3) macFrameskip=10;
	}
	
	public static int getFrameskip() {
		if (macFrameskip==2) return 0;
		if (macFrameskip==5) return 1;
		if (macFrameskip==7) return 2;
		if (macFrameskip==10) return 3;
		return 1;
	}
	
	public static int getFrameskipValue() {
		return macFrameskip;
	}

	public static void setCpuType(int ntype) {
		if (ntype==0) {
			macCpuType=2; //68020
			macFPU="false";
		}
		if (ntype==1)  {
			macCpuType=2; //68020+FPU
			macFPU="true";
		}
		if (ntype==2) 
			{
			macCpuType=3; //68030
			macFPU="false";
			}
		
		if (ntype==3)  {
			macCpuType=3; //68030+FPU
			macFPU="true";
		}
		if (ntype==4)  {
			macCpuType=4; //68040 incl. FPU
			macFPU="true";
		}

	}
	
	public static int getCpuType() {
		if (macCpuType==2) {
			if (macFPU.contains("false")) return 0;
			else return 1;
		}
		if (macCpuType==3) {
			if (macFPU.contains("false")) return 2;
			else return 3;
		}		
		if (macCpuType==4) return 4;
		return 1;
	}
	
	public static void setScreen(int nScreenSel) {
		screenSel = nScreenSel;
	}
	
	public static int getScreen() {
		return screenSel;
	}
	
	public static void setDisk(int nDiskSel) {
		diskSel = nDiskSel;
	}
	
	public static int getDisk() {
		return diskSel;
	}
	
	
	
	public static int parseConfig() {
		File root = Environment.getExternalStorageDirectory();

		macLines = new ArrayList<String>();
		String[] lineStrip;
		try{
		   File f = new File(root, "/droidmac.prefs");
		   FileInputStream fileIS = new FileInputStream(f);
		   BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
		   String readString = new String();
		   //just reading each line and pass it on the debugger
		   while((readString = buf.readLine())!= null){
		      macLines.add(readString);
		      //check if some setup needs to be updated
		      
		      if (readString.toString().contains("stoper true")) {
		    	stoper = true;  
		      }
		      
		      if (readString.toString().contains(macRamString.subSequence(0, macRamString.length())))
		      {
		    	  lineStrip = readString.split(" ");
		    	  if (lineStrip.length>1) macRamSize=Integer.parseInt(lineStrip[1]);
		    	  //Log.v("droiddos","found ram in config:"+dosRamSize);
		      }
		      if (readString.toString().contains(macModelString.subSequence(0, macModelString.length())))
		      {
		    	  //parse model
		    	  lineStrip = readString.split(" ");
		    	  if (lineStrip.length>1) macModel=Integer.parseInt(lineStrip[1]);
		    	  //Log.v("droiddos","found core in config:"+dosCore);
		      }
		      if (readString.toString().contains(screenString.subSequence(0, screenString.length())))
		      {
		    	  //parse low or highres
		    	  if (readString.toString().contains(screenLow.subSequence(0, screenLow.length())))
		    	  {
		    		  screenSel=0;
		    		  Log.v("droiddos", "loading low res");
		    	  }
		    	  if (readString.toString().contains(screenHigh.subSequence(0, screenHigh.length())))
		    	  {
		    		  screenSel=1;
		    		  Log.v("droiddos", "loading high res");
		    	  }
		      }	
		      if (readString.toString().contains(diskString.subSequence(0, diskString.length())))
		      {
		    	  //parse low or highres
		    	  if (readString.toString().contains(diskOff.subSequence(0, diskOff.length())))
		    	  {
		    		  diskSel=0;
		    	  }
		    	  if (readString.toString().contains(diskOn.subSequence(0, diskOn.length())))
		    	  {
		    		  diskSel=1;
		    	  }
		      }	
		      if (readString.toString().contains(macCpuTypeString.subSequence(0, macCpuTypeString.length())))
		      {
		    	  //parse type
		    	  lineStrip = readString.split(" ");
		    	  if (lineStrip.length>1) macCpuType=Integer.parseInt(lineStrip[1]);
		    	  //now parse fpu
		    	  readString = buf.readLine();
		    	  //add it to the maclines!!!
		    	  macLines.add(readString);
		    	  lineStrip = readString.split(" ");
		    	  if (lineStrip.length>1) {
		    		  macFPU=lineStrip[1];
		    		  Log.v("droidmac","loaded fpu:"+macFPU);
		    	  }
		    	  
		      }		            
		      if (readString.toString().contains(macFrameskipString.subSequence(0, macFrameskipString.length())))
		      {
		    	  //parse core
		    	  lineStrip = readString.split("=");
		    	  if (lineStrip.length>1) macFrameskip=Integer.parseInt(lineStrip[1]);
		    	  Log.v("droiddos","found core in config:"+macFrameskip);
		      }		      
		   }
		   buf.close();
		} catch (FileNotFoundException e) {
		   e.printStackTrace();
		   return 1;
		} catch (IOException e){
		   e.printStackTrace();
		   return 2;
		}	
		return 0;
	}
	
	public static void saveConfig() {
		
		try {
		    File root = Environment.getExternalStorageDirectory();
		    String lineWrite=null;
		    if (root.canWrite()){
		        File dosfile = new File(root, "/droidmac.prefs");
		        FileWriter dosWriter = new FileWriter(dosfile);
		        BufferedWriter out = new BufferedWriter(dosWriter);
		        //traverse through dos lines and write them (with update)
		        Iterator <String> dosIter = macLines.iterator();
		        while (dosIter.hasNext()) {
		        	lineWrite = dosIter.next();
		        	if (lineWrite.contains(MacSettings.macRamString)) {
		        		lineWrite = new String(MacSettings.macRamString+Integer.toString(MacSettings.macRamSize));
		        	}
		        	if (lineWrite.contains(MacSettings.macModelString)) {
		        		lineWrite = new String(MacSettings.macModelString+Integer.toString(MacSettings.macModel));
		        	}
		        	if (lineWrite.contains(MacSettings.macCpuTypeString)) {
		        		lineWrite = new String(MacSettings.macCpuTypeString+Integer.toString(MacSettings.macCpuType));
		        	}
		        	if (lineWrite.contains(MacSettings.macFPUString)) {
		        		lineWrite = new String(MacSettings.macFPUString+MacSettings.macFPU);
		        		Log.v("droidmac", "saving fpu:"+lineWrite);
		        	}
		        	if (lineWrite.contains(MacSettings.macFrameskipString)) {
		        		lineWrite = new String(MacSettings.macFrameskipString+Integer.toString(MacSettings.macFrameskip));
		        	}
		        	if (lineWrite.contains(MacSettings.screenString)) {
		        		if (screenSel==0) {
		        			//lowres
		        		lineWrite = screenLow;
		        		 Log.v("droiddos", "saving low res");
		        		}
		        		else
		        		{
		        			//highers
		        			lineWrite = screenHigh;
		        			Log.v("droiddos", "saving high res");
		        		}
		        	}
		        	if (lineWrite.contains(MacSettings.diskString)) {
		        		if (diskSel==0) {
		        			//lowres
		        		lineWrite = diskOff;
		        		}
		        		else
		        		{
		        			//highers
		        			lineWrite = diskOn;
		        		}
		        	}

		        	out.write(lineWrite);
		        	out.newLine();
		        }		        
		        out.close();
		    }
		} catch (IOException e) {
		    Log.v("droiddos", "Could not write file " + e.getMessage());
		}
		
	}
	
}
