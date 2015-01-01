package org.tomaswoj.basilisk;

public class SdlShiftMapper {

	public static int getShiftCode(String buTag) 
	{
		int keycode = 0;
		
		//tilde
		if (buTag.equals("kb_tilde")) keycode=303;
		//shifted1
		if (buTag.equals("kb_ex")) keycode=303;
		if (buTag.equals("kb_at")) keycode=303;
		if (buTag.equals("kb_hash")) keycode=303;
		if (buTag.equals("kb_dollar")) keycode=303;
		if (buTag.equals("kb_qmark")) keycode=303;		
		if (buTag.equals("kb_power")) keycode=303; //caret
		if (buTag.equals("kb_amp")) keycode=303;
		if (buTag.equals("kb_asterisk")) keycode=303;
		if (buTag.equals("kb_leftparen")) keycode=303;
		if (buTag.equals("kb_rightparen")) keycode=303;
		//shifted2
		if (buTag.equals("kb_under")) keycode=303;
		if (buTag.equals("kb_plus")) keycode=303;		
		if (buTag.equals("kb_less")) keycode=303;
		if (buTag.equals("kb_gt")) keycode=303;
		if (buTag.equals("kb_lcur")) keycode=303;
		if (buTag.equals("kb_rcur")) keycode=303;

		//shifted3
		if (buTag.equals("kb_colon")) keycode=303;
		if (buTag.equals("kb_quotdbl")) keycode=303;
		if (buTag.equals("kb_pipe")) keycode=303;
		
		//frameskip/cycles (ctrl)
		if (buTag.equals("kb_fsup")) keycode=306;
		if (buTag.equals("kb_fsdown")) keycode=306;	
		if (buTag.equals("kb_cycup")) keycode=306;
		if (buTag.equals("kb_cycdown")) keycode=306;	
		
		
		return keycode;
	}
}
