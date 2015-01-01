package org.tomaswoj.basilisk;

public class SdlKeycodeMapper {

	public static int getKeyCode(String buTag) 
	{
		int keycode = 0;
		if (buTag.equals("kb_up")) keycode=273;
		if (buTag.equals("kb_down")) keycode=274;
		if (buTag.equals("kb_left")) keycode=276;
		if (buTag.equals("kb_right")) keycode=275;
		if (buTag.equals("kb_enter")) keycode=13;
		//row1
		if (buTag.equals("kb_esc")) keycode=27;
		if (buTag.equals("kb_f1")) keycode=282;
		if (buTag.equals("kb_f2")) keycode=283;
		if (buTag.equals("kb_f3")) keycode=284;
		if (buTag.equals("kb_f4")) keycode=285;
		if (buTag.equals("kb_f5")) keycode=286;
		if (buTag.equals("kb_f6")) keycode=287;
		if (buTag.equals("kb_f7")) keycode=288;
		if (buTag.equals("kb_f8")) keycode=289;
		//frameskip extras
		if (buTag.equals("kb_fsup")) keycode=288;
		if (buTag.equals("kb_fsdown")) keycode=289;

		if (buTag.equals("kb_f9")) keycode=290;
		if (buTag.equals("kb_f10")) keycode=291;
		//cycles extras (f11, f12)
		if (buTag.equals("kb_cycup")) keycode=292;
		if (buTag.equals("kb_cycdown")) keycode=293;

		if (buTag.equals("kb_fsup")) keycode=288;
		if (buTag.equals("kb_fsdown")) keycode=289;
		if (buTag.equals("kb_bkspc")) keycode=8;
		//row2
		if (buTag.equals("kb_tilde")) keycode=96;
		if (buTag.equals("kb_1")) keycode=49;
		if (buTag.equals("kb_2")) keycode=50;
		if (buTag.equals("kb_3")) keycode=51;
		if (buTag.equals("kb_4")) keycode=52;
		if (buTag.equals("kb_5")) keycode=53;
		if (buTag.equals("kb_6")) keycode=54;
		if (buTag.equals("kb_7")) keycode=55;
		if (buTag.equals("kb_8")) keycode=56;
		if (buTag.equals("kb_9")) keycode=57;
		if (buTag.equals("kb_0")) keycode=48;
		if (buTag.equals("kb_pgup")) keycode=280;
		//row3
		if (buTag.equals("kb_tab")) keycode=9;
		if (buTag.equals("kb_q")) keycode=113;
		if (buTag.equals("kb_w")) keycode=119;
		if (buTag.equals("kb_e")) keycode=101;
		if (buTag.equals("kb_r")) keycode=114;
		if (buTag.equals("kb_t")) keycode=116;
		if (buTag.equals("kb_y")) keycode=121;
		if (buTag.equals("kb_u")) keycode=117;
		if (buTag.equals("kb_i")) keycode=105;
		if (buTag.equals("kb_o")) keycode=111;
		if (buTag.equals("kb_p")) keycode=112;
		if (buTag.equals("kb_pgdw")) keycode=281;
		//row4
		if (buTag.equals("kb_ctrl")) keycode=306;
		if (buTag.equals("kb_a")) keycode=97;
		if (buTag.equals("kb_s")) keycode=115;
		if (buTag.equals("kb_d")) keycode=100;
		if (buTag.equals("kb_f")) keycode=102;
		if (buTag.equals("kb_g")) keycode=103;
		if (buTag.equals("kb_h")) keycode=104;
		if (buTag.equals("kb_i")) keycode=105;
		if (buTag.equals("kb_j")) keycode=106;
		if (buTag.equals("kb_k")) keycode=107;
		if (buTag.equals("kb_l")) keycode=108;
		if (buTag.equals("kb_enter")) keycode=13;
		if (buTag.equals("kb_pgdw")) keycode=281;
		//row5
		if (buTag.equals("kb_alt")) keycode=308;
		if (buTag.equals("kb_z")) keycode=122;
		if (buTag.equals("kb_x")) keycode=120;
		if (buTag.equals("kb_c")) keycode=99;
		if (buTag.equals("kb_v")) keycode=118;
		if (buTag.equals("kb_b")) keycode=98;
		if (buTag.equals("kb_n")) keycode=110;
		if (buTag.equals("kb_m")) keycode=109;
		if (buTag.equals("kb_space")) keycode=32;
		if (buTag.equals("kb_home")) keycode=278;
		if (buTag.equals("kb_end")) keycode=279;
		
		//shifted1
		if (buTag.equals("kb_backq")) keycode=96;
		if (buTag.equals("kb_ex")) keycode=49;
		if (buTag.equals("kb_at")) keycode=50;
		if (buTag.equals("kb_hash")) keycode=51;
		if (buTag.equals("kb_dollar")) keycode=52;
		if (buTag.equals("kb_qmark")) keycode=47;		
		if (buTag.equals("kb_power")) keycode=54; //caret
		if (buTag.equals("kb_amp")) keycode=55;
		if (buTag.equals("kb_asterisk")) keycode=56;
		if (buTag.equals("kb_leftparen")) keycode=57;
		if (buTag.equals("kb_rightparen")) keycode=48;
		//shifted2
		if (buTag.equals("kb_minus")) keycode=45;
		if (buTag.equals("kb_under")) keycode=45;
		if (buTag.equals("kb_plus")) keycode=61;
		if (buTag.equals("kb_equal")) keycode=61;
		if (buTag.equals("kb_lbra")) keycode=91;
		if (buTag.equals("kb_rbra")) keycode=93;
		if (buTag.equals("kb_lcur")) keycode=91;
		if (buTag.equals("kb_rcur")) keycode=93;
		if (buTag.equals("kb_backslash")) keycode=92;
		if (buTag.equals("kb_slash")) keycode=47;
		if (buTag.equals("kb_pipe")) keycode=92; //not in DOS??
		if (buTag.equals("kb_less")) keycode=44;
		if (buTag.equals("kb_gt")) keycode=46;
		//shifted3
		if (buTag.equals("kb_colon")) keycode=59;
		if (buTag.equals("kb_semicolon")) keycode=59;
		if (buTag.equals("kb_quotdbl")) keycode=39;
		if (buTag.equals("kb_quot")) keycode=39;
		if (buTag.equals("kb_comma")) keycode=44;
		if (buTag.equals("kb_period")) keycode=46;
		
		if (buTag.equals("kb_del")) keycode=127;
		return keycode;
	}
}
