package org.tomaswoj.basilisk;

public class GUISettings {
	
	private static int qwertySize=14;
	private static int shiftSize=20;
	private static int dpadSize=22;
	private static int dosWindowX=320;
	private static int dosWindowY=240;
	private static int dosFiltering=0; // 0 - GL_NEAREST, 1 - GL_LINEAR
	private static int dosAspect=0; // 0 - no keep, 1 - keep aspect
	private static int qwertyLayout=0; // 0 - 11x6, 1 - 10x7
	private static int buttonSize=14;
	private static int orientation=0; //0 - portrait, 1 - landscape
	private static int oversizeMode=0; //0 - paged, 1 - shrinked
	
	//tpad size 
	private static int tpadX = 210;
	private static int tpadY = 180;
	

	public static void setDosWindowSize(int dosSize) {
		switch (dosSize) {
		case 0: //HVGA - 320x240
			dosWindowX=320;
			dosWindowY=240;
			break;
		case 1: //WVGA - 480x360
			dosWindowX=480;
			dosWindowY=360;
			break;
		case 2: //QHD - 540x405
			dosWindowX=540;
			dosWindowY=405;			
			break;
		case 3: //QHD - 600x450
			dosWindowX=600;
			dosWindowY=450;			
			break;
		case 4: //VGA for landscape
			dosWindowX=640;
			dosWindowY=480;
			break;
		default:
		dosWindowX=320;
		dosWindowY=240;				
		}
	}
	
	public static void setOversizeMode(int nmode) {
		oversizeMode = nmode;
	}
	
	public static int getOversizeMode() {
		return oversizeMode;
	}

	public static void setTpadSize(int tpadSize) {
		switch (tpadSize) {
		case 0: //HVGA - 320x240
			tpadX=210;
			tpadY=180;
			break;
		case 1: //WVGA - 480x360
			tpadX=370;
			tpadY=300;
			break;
		case 2: //QHD - 540x405
			tpadX=430;
			tpadY=360;			
			break;
		default:
		tpadX=210;
		tpadY=180;				
		}
	}
	
	
	public static int getDosWindowX() {
		return dosWindowX;
	}
	
	public static int getDosWindowY() {
		return dosWindowY;
	}
	
	public static int getTpadX() {
		return tpadX;
	}
	
	public static int getTpadY() {
		return tpadY;
	}

	public static void setOrientation(int nOrientation) {
		orientation = nOrientation;
	}

	public static int getOrientation() {
		return orientation;
	}
	public static void setDosFiltering(int newFiltering) {
		dosFiltering = newFiltering;
	}
	
	public static int getDosFiltering() {
		return dosFiltering;
	}

	public static void setDosAspect(int newAspect) {
		dosAspect = newAspect;
	}
	
	public static int getDosAspect() {
		return dosAspect;
	}

	public static void setQwertyLayout(int newLayout) {
		qwertyLayout = newLayout;
	}
	
	public static int getQwertyLayout() {
		return qwertyLayout;
	}
	
	public static void setQwertySize(int nQwertySize) {
		qwertySize = nQwertySize;				
	}

	public static void setShiftSize(int nShiftSize) {
		shiftSize = nShiftSize;
	}

	public static void setButtonSize(int nButtonSize) {
    	switch (nButtonSize) {
		case 0:
			buttonSize = 14;
			break;
		case 1:
			buttonSize = 18;
			break;
		case 2:
			buttonSize = 22;
			break;
		case 3:
			buttonSize = 26;
			break;
		default:
			buttonSize = 14;
    	}		
	}

	public static int getButtonSize() {
		return buttonSize;
	}
		
	public static void setDpadSize(int nDpadSize) {
		dpadSize = nDpadSize;
	}
	
	public static int getQwertySize() {
		return qwertySize;
	}

	public static int getShiftSize() {
		return shiftSize;
	}

	public static int getDpadSize() {
		return dpadSize;
	}

}
