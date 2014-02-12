package com.lovewuchin.xposed.xmultiwindow;

import com.lovewuchin.xposed.xmultiwindow.hooks.HookMultiWindow;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMain implements IXposedHookLoadPackage,IXposedHookZygoteInit
{
static XSharedPreferences mPrefs;
static XSharedPreferences mAlloew;
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		// TODO Auto-generated method stub
		mPrefs = new XSharedPreferences(Common.THIS_PACKAGE_NAME, Common.PREFERENCE_APPSHOW);
	}

	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		// TODO Auto-generated method stub		
		HookMultiWindow.handleLoadPackage(lpparam,mPrefs);
	}
}
