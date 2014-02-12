package com.lovewuchin.xposed.xmultiwindow;

import com.lovewuchin.xposed.xmultiwindow.widget.SideBarControl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SideBarControlPanel extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	Toast.makeText(this, "open sidebar",Toast.LENGTH_SHORT);
    	Intent i=new Intent(this,SideBarControl.class);
    	startService(i);
    	finish();
    }
}
