package com.lovewuchin.xposed.xmultiwindow.widget;

import com.lovewuchin.xposed.xmultiwindow.widget.sidebar.SideBarLauncher;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.MonthDisplayHelper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class SideBarControl extends Service{
private WindowManager wm;
private View mView;
private WindowManager.LayoutParams params;
private WindowManager.LayoutParams hide;
private WindowManager.LayoutParams show;
private WindowManager.LayoutParams touch;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initLayoutParams();
		initView();
	}
	private void initLayoutParams() {
		// TODO Auto-generated method stub
		show=new WindowManager.LayoutParams();
		hide=new WindowManager.LayoutParams();
		touch=new WindowManager.LayoutParams();
		
		show.flags=WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		show.gravity=Gravity.LEFT;
		show.format=PixelFormat.TRANSLUCENT;
		show.height=0;
		show.width=0;
		show.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		
		hide.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		hide.gravity=Gravity.LEFT;
		hide.format=PixelFormat.TRANSLUCENT;
		hide.height=0;
		hide.width=0;
		hide.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		
		touch.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		touch.gravity=Gravity.LEFT;
		touch.format=PixelFormat.TRANSLUCENT;
		touch.height=WindowManager.LayoutParams.MATCH_PARENT;
		touch.width=30;
		touch.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
	}
	private void initView() {
		// TODO Auto-generated method stub
		wm=(WindowManager)getSystemService(WINDOW_SERVICE);
		params=new WindowManager.LayoutParams();
		mView=new View(this);
		mView.setOnTouchListener(mTouchListener);
		wm.addView(mView, hide);
		wm.updateViewLayout(mView, touch);
	}
	private OnTouchListener mTouchListener=new OnTouchListener() {
		int mDownX=-1;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(MotionEvent.ACTION_DOWN==event.getAction()){
				mDownX=(int)event.getX();
			}else if(MotionEvent.ACTION_UP==event.getAction()&&mDownX!=-1){
				int offset=(int)(event.getX()-mDownX);
				if(offset>30){
					showSideBar();
				}
				mDownX=-1;
			}
			return false;
		}
		private void showSideBar() {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getApplicationContext(),SideBarLauncher.class);  
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			getApplicationContext().startActivity(intent);  
		}
	};
     
}
