package com.lovewuchin.xposed.xmultiwindow.helpers;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application{
public static Context appContext;
     @Override
    	public void onCreate() {
    		// TODO Auto-generated method stub
    		super.onCreate();
    		appContext=getApplicationContext();
    	}
}
