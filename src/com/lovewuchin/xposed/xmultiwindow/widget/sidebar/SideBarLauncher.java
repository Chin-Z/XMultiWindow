package com.lovewuchin.xposed.xmultiwindow.widget.sidebar;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.os.Bundle;

public class SideBarLauncher extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	StandOutWindow.closeAll(this, SideBar.class);
    	StandOutWindow.show(this, SideBar.class, StandOutWindow.DEFAULT_ID);
    	finish();
    }
}
