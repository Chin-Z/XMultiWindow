package com.lovewuchin.xposed.xmultiwindow;

import com.lovewuchin.xposed.xmultiwindow.widget.SideBarControl;
import com.lovewuchin.xposed.xmultiwindow.widget.preference.SeekBarPreference;
import com.lovewuchin.xposed.xmultiwindow.widget.sidebar.SideBarApp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

@SuppressLint("WorldReadableFiles")
public class MainPreference extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener{
	SeekBarPreference mSideWidth;
	SharedPreferences mPrefs;
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.pref_main);
    	mPrefs=getSharedPreferences(Common.PREFERENCE_MAIN, MODE_WORLD_READABLE);
    	findPreference(Common.KEY_LAUNCH_FLOAT).setOnPreferenceClickListener(this);
    	findPreference(Common.KEY_SIDEBAR_APP).setOnPreferenceClickListener(this);
    	mSideWidth=(SeekBarPreference)findPreference("sidebar_width");
    	mSideWidth.setValue(mPrefs.getInt(Common.PREFERENCE_WIDTH, 150));
    	mSideWidth.setOnPreferenceChangeListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(Common.MENU, menu);
		return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    	case R.id.action_instruction:{
    		new AlertDialog.Builder(this).setTitle(Common.STRING_ACTION_INSTRUC).setMessage(Common.STRING_ACTION_MESSAGE)
    		    .setPositiveButton(Common.STRING_OK,null)
    		    .setNegativeButton(Common.STRING_CANCEL, null)
    		    .show();
    		    
    	}
    	}
    	return false;
    }
    public boolean onPreferenceClick(Preference preference) {
         Intent mIntent;
         switch(preference.getKey()) {
         case Common.KEY_LAUNCH_FLOAT:
        	 Toast.makeText(this, "open sidebar",Toast.LENGTH_SHORT);
         	 startService(new Intent(this,SideBarControl.class));
        	 finish();
        	 return true;
         case Common.KEY_SIDEBAR_APP:
        	 mIntent = new Intent(this, SideBarApp.class);
        	 startActivity(mIntent);
        	 return true;
         default :
        	 return false;
         }
    }
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		switch(preference.getKey()) {
		case Common.KEY_SIDEBAR_WIDTH:
			mPrefs.edit().putInt(Common.PREFERENCE_WIDTH, (Integer) newValue).commit();
			return true;
		default:
			return false;
		}
	}
}
