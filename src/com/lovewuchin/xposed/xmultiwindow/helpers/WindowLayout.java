package com.lovewuchin.xposed.xmultiwindow.helpers;

import com.lovewuchin.xposed.xmultiwindow.Common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import de.robv.android.xposed.XSharedPreferences;


public class WindowLayout {
	public static void applyLayout(Window window){ 
		window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setGravity(Gravity.LEFT | Gravity.TOP);
		WindowManager.LayoutParams params = window.getAttributes();
		window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	    window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
		params.alpha=1f;
		params.dimAmount=0.05f;
		window.setAttributes(params);
		Context context=window.getContext();
		DisplayMetrics metrics = new DisplayMetrics();
		try {
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			display.getMetrics(metrics);
		} catch (Exception e) {
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			metrics = new DisplayMetrics();
			metrics = dm;
		}
		if(metrics.heightPixels>metrics.widthPixels){
			window.setLayout((int)(metrics.widthPixels), (int)(metrics.heightPixels*0.5));
		}else{
			window.setLayout((int)(metrics.widthPixels*0.5), (int)(metrics.heightPixels));
		}
	}
	}
	
