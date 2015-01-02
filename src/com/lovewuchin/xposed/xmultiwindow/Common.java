package com.lovewuchin.xposed.xmultiwindow;


import android.os.Build;

public class Common {

	//Ohers
	public static final String THIS_PACKAGE_NAME = Common.class.getPackage().getName();
	public static final String LOG_TAG = "XMultiWindow(SDK: " + Build.VERSION.SDK_INT + ") - ";
	public static final String PREFERENCE_APP="sidebar_app";
	public static final String PREFERENCE_MAIN="main_preference";
	public static final String PREFERENCE_WIDTH="sidebar_width";
	public static final String REFRESH_APP_LAYOUT = THIS_PACKAGE_NAME + ".REFRESH_APP_LAYOUT";
	public static final int LAYOUT_RECEIVER_TAG = android.R.id.background;

	//Broadcast
	public static final String CHANGE_APP_FOCUS = THIS_PACKAGE_NAME + ".CHANGE_APP_FOCUS";
	public static final String INTENT_APP_TOKEN = "token";
	public static final String INTENT_APP_ID = "id";
	public static final String REMOVE_NOTIFICATION_RESTORE = THIS_PACKAGE_NAME + ".REMOVE_NOTIFICATION_RESTORE.";

	//Preference Keys
	public static final String KEY_LAUNCH_FLOAT="lunch_float";
	public static final String KEY_LAUNCH_SIDEBAR="lunch_sidebar";
	public static final String KEY_SIDEBAR_APP="key_sidebar_app";
	public static final String KEY_SIDEBAR_WIDTH="sidebar_width";

	//String Values
	public static final int STRING_FLOATING_FOLDER_NAME = R.string.float_folder_name;
	public static final int STRING_FLOATING_FOLDER_ADD = R.string.float_folder_addapp;
	public static final int STRING_FLOATING_FOLDER_CLEAR =R.string.float_folder_clear;
	public static final int STRING_FLOATING_FOLDER_MSG_CLEAR = R.string.float_folder_msg_clear;
	public static final int STRING_POP_ADD_UPVIEW = R.string.pop_add_upview;
	public static final int STRING_POP_ADD_DOWNVIEW = R.string.pop_add_downview;
	public static final int STRING_ACTION_INSTRUC = R.string.action_instruction;
	public static final int STRING_ACTION_MESSAGE = R.string.action_message;
	public static final int STRING_OK = R.string.ok;
	public static final int STRING_CANCEL = R.string.cancel;
	public static final int STRING_NOFIY_CLEAR=R.string.nofiy_clear;

	//Menu
	public static final int MENU = R.menu.main;
	
	//Flags
	public static final int FLAG_ACTIVITY_UPVIEW = 0x001;
	public static final int FLAG_ACTIVITY_DOWNVIEW = 0x002;
}
