package com.lovewuchin.xposed.xmultiwindow.hooks;


import java.lang.reflect.Field;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.lovewuchin.xposed.xmultiwindow.Common;
import com.lovewuchin.xposed.xmultiwindow.helpers.WindowLayout;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.callbacks.XCallback;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findClass;

@SuppressLint("NewApi")
public class HookMultiWindow {
	static XSharedPreferences mPrefs;
	static boolean isMultiWindow=false;
	static boolean multiWindow;
	static View overlayView;
	static int mPreviousOrientation;
	static final String INTENT_APP_PKG = "pkg";

	public static void handleLoadPackage(LoadPackageParam lpparam,
			XSharedPreferences mPref) {
		// TODO Auto-generated method stub
		initHook(lpparam);
	}

	private static void initHook(LoadPackageParam lpparam) {
		// TODO Auto-generated method stub
		try {
			hookActivityRecord(lpparam);
		} catch (Throwable e) {
			// TODO: handle exception
			XposedBridge.log(Common.LOG_TAG + "(ActivityRecord)");
			XposedBridge.log(e);
		}
		try {
			hookActivityStack(lpparam);
		} catch (Throwable e) {
			// TODO: handle exception
			XposedBridge.log(Common.LOG_TAG + "(ActivityStack)");
			XposedBridge.log(e);
		}
		try {
			removeAppStartingWindow(lpparam);
		} catch (Throwable e) {
			// TODO: handle exception
			XposedBridge.log(Common.LOG_TAG + "(removeAppStartingWindow)");
			XposedBridge.log(e);
		}
		try {
			hookActivity(lpparam);
		} catch (Throwable e) {
			// TODO: handle exception
			XposedBridge.log(Common.LOG_TAG + "(Activity)");
			XposedBridge.log(e);
		}
		try {
			fixExceptionWhenResuming(lpparam);
		} catch (Throwable e) {
			// TODO: handle exception
			XposedBridge.log(Common.LOG_TAG + "(fixExceptionWhenResuming)");
			XposedBridge.log(e);
		}
	}
/*********************************************************************************/
	static boolean mExceptionHook = false;
	private static void fixExceptionWhenResuming(LoadPackageParam lpparam) throws Throwable {
		XposedBridge.hookAllMethods(findClass("android.app.ActivityThread", lpparam.classLoader), "performResumeActivity", 
				new XC_MethodHook() {
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				mExceptionHook = true;
			}
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				mExceptionHook = false;
			}
		});
		XposedBridge.hookAllMethods(android.app.Instrumentation.class, "onException",
				new XC_MethodReplacement() {
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
				return mExceptionHook;
			}
		});
	}
/*********************************************************************************/
private static void hookActivity(LoadPackageParam lpparam) {
		// TODO Auto-generated method stub
		XposedBridge.hookAllMethods(Activity.class, "onResume", new XC_MethodHook() {
          @Override
        protected void beforeHookedMethod(MethodHookParam param)
        		throws Throwable {
        	// TODO Auto-generated method stub
        	Activity thiz=(Activity)param.thisObject;
        	Intent intent=thiz.getIntent();
        	DisplayMetrics metrics=new DisplayMetrics();
        	thiz.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        	isMultiWindow=((intent.getFlags()&Common.FLAG_ACTIVITY_UPVIEW)==Common.FLAG_ACTIVITY_UPVIEW)||((intent.getFlags()&Common.FLAG_ACTIVITY_DOWNVIEW)==Common.FLAG_ACTIVITY_DOWNVIEW);
        	if(isMultiWindow){
            		WindowLayout.applyLayout(thiz.getWindow());
        	
        	if(metrics.heightPixels>metrics.widthPixels){
        	if((intent.getFlags()&Common.FLAG_ACTIVITY_DOWNVIEW)==Common.FLAG_ACTIVITY_DOWNVIEW){
            	updateView(thiz.getWindow(),0,metrics.widthPixels);
            }else if((intent.getFlags()&Common.FLAG_ACTIVITY_UPVIEW)==Common.FLAG_ACTIVITY_UPVIEW){
            	updateView(thiz.getWindow(),0,0);
            }
        }else{
        	if((intent.getFlags()&Common.FLAG_ACTIVITY_DOWNVIEW)==Common.FLAG_ACTIVITY_DOWNVIEW){
            	updateView(thiz.getWindow(),metrics.heightPixels,0);
            }else if((intent.getFlags()&Common.FLAG_ACTIVITY_UPVIEW)==Common.FLAG_ACTIVITY_UPVIEW){
            	updateView(thiz.getWindow(),0,0);
            }
        }}
          }
        });
		XposedBridge.hookAllMethods(Activity.class, "onStart", new XC_MethodHook() {
			@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				if (!isMultiWindow) return;
				Activity activity = (Activity) param.thisObject;
				Window window = (Window) activity.getWindow();

				// register the receiver for syncing window position
				registerLayoutBroadcastReceiver(window);
				// set layout position from previous activity if available
				setLayoutPositioning(window);

				Context context = window.getContext();

				FrameLayout decorView = (FrameLayout) window.peekDecorView().getRootView();
				if (decorView == null) return;
				// make sure the titlebar/drag-to-move-bar is not behind the statusbar
				decorView.setFitsSystemWindows(true);
				try {
					// disable resizing animation to speed up scaling (doesn't work on all roms)
					XposedHelpers.callMethod(decorView, "hackTurnOffWindowResizeAnim", true);
				} catch (Throwable e) {
				}

				RelativeLayout.LayoutParams paramz = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
				paramz.setMargins(0, 0, 0, 0);
			}
		});
	}
static boolean layout_moved;
static int layout_x;
static int layout_y;
static int layout_width;
static int layout_height;
static float layout_alpha;
private static void registerLayoutBroadcastReceiver(final Window window) {
	BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
				Configuration config = window.getContext().getResources().getConfiguration();
				if (config.orientation != mPreviousOrientation) {
					WindowManager.LayoutParams paramz = window.getAttributes();
					final int old_x = paramz.x;
					final int old_y = paramz.y;
					final int old_height = paramz.height;
					final int old_width = paramz.width;
					paramz.x = old_y;
					paramz.y = old_x;
					paramz.width = old_height;
					paramz.height = old_width;
					window.setAttributes(paramz);
					mPreviousOrientation = config.orientation;
				}
				return;
			}
			if (intent.getStringExtra(INTENT_APP_PKG).equals(
					window.getContext().getApplicationInfo().packageName)) {
				setLayoutPositioning(window);
			}
		}
	};
}
private static void setLayoutPositioning(Window window) {

	WindowManager.LayoutParams params = window.getAttributes();
	params.x = layout_x;
	params.y = layout_y;
	params.width = layout_width;
	params.height = layout_height;
	params.alpha = layout_alpha;
	params.gravity = Gravity.LEFT | Gravity.TOP;
	window.setAttributes(params);
}
private static void updateView(Window mWindow, float x, float y) {
	WindowManager.LayoutParams params = mWindow.getAttributes();
	params.x = (int) x;
	params.y = (int) y;
	mWindow.setAttributes(params);
}

/***********************************************************************************/
private static void removeAppStartingWindow(LoadPackageParam lpparam) {
		// TODO Auto-generated method stub
	Class<?> hookClass = findClass("com.android.server.wm.WindowManagerService", lpparam.classLoader);
	XposedBridge.hookAllMethods(hookClass, "setAppStartingWindow", new XC_MethodHook() {
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
			if (!multiWindow) return;
			if ("android".equals((String) param.args[1])) return;
			// Change boolean "createIfNeeded" to FALSE
			if (param.args[param.args.length - 1] instanceof Boolean) {
				param.args[param.args.length - 1] = Boolean.FALSE;
				// Last param of the arguments
				// It's length has changed in almost all versions of Android.
				// Since it is always the last value, we use this to our advantage.
			}
		}
	});
	}
/***********************************************************************************/
	static Field activityField;
	static Object previous = null;
	static boolean appPauseEnabled;
	private static void hookActivityStack(LoadPackageParam lpparam) {
		// TODO Auto-generated method stub
		XposedBridge.hookAllMethods(findClass("com.android.server.am.ActivityStack", lpparam.classLoader), "resumeTopActivityLocked", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param)
					throws Throwable {
				// TODO Auto-generated method stub
				if(!multiWindow)return;
				Class<?> clazz = param.thisObject.getClass();
				activityField = clazz.getDeclaredField(("mResumedActivity"));
				activityField.setAccessible(true);
				previous = null;
				final Object prevActivity = activityField.get(param.thisObject);
				if (prevActivity != null) {
					previous = prevActivity;
				}
				activityField.set(param.thisObject, null);
			}
			
			@Override
			protected void afterHookedMethod(MethodHookParam param)
					throws Throwable {
				// TODO Auto-generated method stub
				if(!multiWindow)return;
				if(previous!=null){
					Class<?> clazz = param.thisObject.getClass();
					if (activityField == null) activityField = clazz.getDeclaredField(("mResumedActivity"));
					activityField.setAccessible(true);
					activityField.set(param.thisObject, previous);
				}
			}
		});
		XposedBridge.hookAllMethods(findClass("com.android.server.am.ActivityStack", lpparam.classLoader), "startActivityLocked", new XC_MethodHook() {
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!multiWindow) return;
				if (param.args[1] instanceof Intent) return;
				Object activityRecord = param.args[0];
				Class<?> activityRecordClass = activityRecord.getClass();
				Field activityField = activityRecordClass.getDeclaredField(("fullscreen"));
				activityField.setAccessible(true);
				activityField.set(activityRecord, Boolean.FALSE);
			}
		});
		XposedBridge.hookAllMethods(findClass("com.android.server.am.ActivityStack", lpparam.classLoader), "moveHomeToFrontFromLaunchLocked", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				int launchFlags = (Integer) param.args[0];
				if ((launchFlags & (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME))
						== (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME)) {
					boolean floating = ((launchFlags & Common.FLAG_ACTIVITY_UPVIEW) == Common.FLAG_ACTIVITY_UPVIEW)||((launchFlags & Common.FLAG_ACTIVITY_DOWNVIEW) == Common.FLAG_ACTIVITY_DOWNVIEW);
					if (floating) param.setResult(null);
					// if the app is a floating app, and is a new task on home.
					// then skip this method.
				} else {
					param.setResult(null);
					// This is not a new task on home. Dont allow the method to continue.
					// Since there is no point to run method which checks for the same thing
				}
			}
		});
		
	}
/***********************************************************************************/
	private static void hookActivityRecord(LoadPackageParam lpparam) {
		// TODO Auto-generated method stub
		if (!lpparam.packageName.equals("android")) return;
		XposedBridge.hookAllConstructors(findClass("com.android.server.am.ActivityRecord",
				lpparam.classLoader), new XC_MethodHook(XCallback.PRIORITY_HIGHEST){
			@Override
			protected void afterHookedMethod(MethodHookParam param)
					throws Throwable {
				// TODO Auto-generated method stub
				isMultiWindow=false;
				multiWindow=false;
				Intent intent=null;
				Object stack=null;
				ActivityInfo aInfo=null;
				if (Build.VERSION.SDK_INT <= 17) { // JB 4.2 and below
					intent = (Intent) param.args[4];
					aInfo = (ActivityInfo) param.args[6];
					stack = param.args[1];
				} else if (Build.VERSION.SDK_INT == 18) { 
					// JB 4.3 has additional _launchedFromPackage. so indexs are affected
					intent = (Intent) param.args[5];
					aInfo = (ActivityInfo) param.args[7];
					stack = param.args[1];
				} else if (Build.VERSION.SDK_INT == 19) { 
					// Fuck Google. Changed params order again for KitKat.
					intent = (Intent) param.args[4];
					aInfo = (ActivityInfo) param.args[6];
					try {
						Object stackSupervisor = param.args[12]; // mStackSupervisor
						stack = XposedHelpers.callMethod(stackSupervisor, "getFocusedStack");
					} catch (Exception e) {
						Field field = param.args[12].getClass().getDeclaredField("mFocusedStack");
						field.setAccessible(true);
						stack = field.get(param.args[12]);
					}
				}
				if(intent==null)return;
				multiWindow=((intent.getFlags()&Common.FLAG_ACTIVITY_UPVIEW)==Common.FLAG_ACTIVITY_UPVIEW)||((intent.getFlags()&Common.FLAG_ACTIVITY_DOWNVIEW)==Common.FLAG_ACTIVITY_DOWNVIEW);
				Class<?> activityStack=stack.getClass();
				Field mHistoryField = null;
				if (Build.VERSION.SDK_INT == 19) { // Kitkat
					mHistoryField = activityStack.getDeclaredField("mTaskHistory"); // ArrayList<TaskRecord>
				} else { // JB4.3 and lower
					mHistoryField = activityStack.getDeclaredField("mHistory"); // ArrayList<ActivityRecord>
				}
				mHistoryField.setAccessible(true);
				ArrayList<?> alist = (ArrayList<?>) mHistoryField.get(stack);
                boolean upview;
                boolean downview;
				boolean taskAffinity;
				if (alist.size() > 0 && !multiWindow ) {
					if (Build.VERSION.SDK_INT == 19) {
						Object taskRecord = alist.get(alist.size() - 1);
						Field taskRecord_intent_field = taskRecord.getClass().getDeclaredField("intent");
						taskRecord_intent_field.setAccessible(true);
						Intent taskRecord_intent = (Intent) taskRecord_intent_field.get(taskRecord);
						upview=(taskRecord_intent.getFlags()&Common.FLAG_ACTIVITY_UPVIEW)==Common.FLAG_ACTIVITY_UPVIEW;
						downview=(taskRecord_intent.getFlags()&Common.FLAG_ACTIVITY_DOWNVIEW)==Common.FLAG_ACTIVITY_DOWNVIEW;
						String pkgName = taskRecord_intent.getPackage();
						taskAffinity = aInfo.applicationInfo.packageName.equals(pkgName /* info.packageName */);
					} else {
						Object baseRecord = alist.get(alist.size() - 1); // ActivityRecord
						Field baseRecordField = baseRecord.getClass().getDeclaredField("intent");
						baseRecordField.setAccessible(true);
						Intent baseRecord_intent = (Intent) baseRecordField.get(baseRecord);
						upview=(baseRecord_intent.getFlags()&Common.FLAG_ACTIVITY_UPVIEW)==Common.FLAG_ACTIVITY_UPVIEW;
						downview=(baseRecord_intent.getFlags()&Common.FLAG_ACTIVITY_DOWNVIEW)==Common.FLAG_ACTIVITY_DOWNVIEW;
						Field baseRecordField_2 = baseRecord.getClass().getDeclaredField("packageName");
						baseRecordField_2.setAccessible(true);
						String baseRecord_pkg = (String) baseRecordField_2.get(baseRecord);
						taskAffinity = aInfo.applicationInfo.packageName.equals(baseRecord_pkg );
						/*baseRecord.packageName*/
					}
				if ((upview||downview) && taskAffinity) {
					Field intentField = param.thisObject.getClass().getDeclaredField("intent");
					intentField.setAccessible(true);
					Intent newer = (Intent) intentField.get(param.thisObject);
					if ((intent.getFlags() & Common.FLAG_ACTIVITY_UPVIEW) == Common.FLAG_ACTIVITY_UPVIEW) {
					newer.addFlags(Common.FLAG_ACTIVITY_UPVIEW);
					}
					if ((intent.getFlags()&Common.FLAG_ACTIVITY_DOWNVIEW)==Common.FLAG_ACTIVITY_DOWNVIEW) {
					newer.addFlags(Common.FLAG_ACTIVITY_DOWNVIEW);
					}
					intentField.set(param.thisObject, newer);
					multiWindow=true;
				}
				
			
					if (multiWindow) {
						int intent_flag = intent.getFlags();
						intent_flag &= ~Intent.FLAG_ACTIVITY_TASK_ON_HOME;
						intent.setFlags(intent_flag);
						if (upview) {
							intent.addFlags(Common.FLAG_ACTIVITY_UPVIEW);
							}
							if (downview) {
							intent.addFlags(Common.FLAG_ACTIVITY_DOWNVIEW);
							}
						intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
						Field tt = param.thisObject.getClass().getDeclaredField("fullscreen");
						tt.setAccessible(true);
						tt.set(param.thisObject, Boolean.FALSE);
					}	
			}	
		}
		});
	}

}
