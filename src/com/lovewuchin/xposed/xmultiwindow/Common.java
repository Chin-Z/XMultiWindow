package com.lovewuchin.xposed.xmultiwindow;


import android.os.Build;

public class Common {
//Ohers
	public static final String THIS_PACKAGE_NAME = Common.class.getPackage().getName();
	public static final int FLAG_ACTIVITY_UPVIEW = 0x001;
	public static final int FLAG_ACTIVITY_DOWNVIEW = 0x002;
	public static final String LOG_TAG = "XMultiWindow(SDK: " + Build.VERSION.SDK_INT + ") - ";
	public static final String PREFERENCE_APPSHOW="window_show";
//Preference Keys
	public static final String KEY_LUNCH_FLOAT="lunch_float";
	public static final String KEY_LUNCH_SIDEBAR="lunch_sidebar";
//String Values
	public static final int FLOATING_FOLDER_NAME = R.string.float_folder_name;
	public static final int FLOATING_FOLDER_ADD = R.string.float_folder_addapp;
	public static final int FLOATING_FOLDER_CLEAR =R.string.float_folder_clear;
	public static final int FLOATING_FOLDER_MSG_CLEAR = R.string.float_folder_msg_clear;
	public static final int POP_ADD_UPVIEW = R.string.pop_add_upview;
	public static final int POP_ADD_DOWNVIEW = R.string.pop_add_downview;
	public static final int ACTION_INSTRUC = R.string.action_instruction;
	public static final int ACTION_MESSAGE = R.string.action_message;
	public static final int OK = R.string.ok;
	public static final int CANCLE = R.string.cancel;
	public static final int NOFIY_CLEAR=R.string.nofiy_clear;
//Menu
	public static final int MENU = R.menu.main;
}
