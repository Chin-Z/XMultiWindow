package com.lovewuchin.xposed.xmultiwindow.widget.sidebar;

import java.util.Set;

import com.lovewuchin.xposed.xmultiwindow.Common;
import com.lovewuchin.xposed.xmultiwindow.R;
import com.lovewuchin.xposed.xmultiwindow.widget.sidebar.adapter.AppAdapter;
import com.lovewuchin.xposed.xmultiwindow.widget.sidebar.adapter.AppAdapter.AppItem;
import com.lovewuchin.xposed.xmultiwindow.widget.sidebar.adapter.PackageNameAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SideBarApp extends Activity{
	SharedPreferences mPrefs;
	ListView mListView;
	PackageNameAdapter mPkgAdapter;
	//Dialog
	Dialog dDialog;
	ListView dListView;
	ProgressBar dProgressBar;
	EditText dSearch;
	ImageButton dButton;
	AppAdapter dAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	mPrefs=getSharedPreferences(Common.PREFERENCE_APP, MODE_WORLD_READABLE);
    	setLayout();
    	showAppDialog();
    	
    }
   private void setLayout() {
		// TODO Auto-generated method stub
	    mPkgAdapter = new PackageNameAdapter(this, getSetStrings());
		mListView = new ListView(this);
		mListView.setAdapter(mPkgAdapter);
		setContentView(mListView);
	}
@Override
   public boolean onCreateOptionsMenu(Menu menu) {
	// TODO Auto-generated method stub
	   getMenuInflater().inflate(R.menu.app_list, menu);
	    return true;
   }
   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	   switch(item.getItemId()){
	   case R.id.item_add:{
           dDialog.show();
		   break;
	   }
	   }
	return false;
}
private void showAppDialog() {
	// TODO Auto-generated method stub
	dDialog = new Dialog(this);
	dDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dDialog.setContentView(R.layout.dialog_sidebar_app);
	
	dProgressBar = (ProgressBar) dDialog.findViewById(R.id.progressBar1);
	dListView = (ListView) dDialog.findViewById(R.id.dialog_sidebar_id);
	dSearch = (EditText) dDialog.findViewById(R.id.searchText);
	dButton = (ImageButton) dDialog.findViewById(R.id.searchButton);
	
	dAdapter = new AppAdapter(this, dProgressBar);
	dListView.setAdapter(dAdapter);
	dListView.setOnItemClickListener(new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> v, View arg1, int pos, long arg3) {
			AppItem info = (AppItem) v.getItemAtPosition(pos);
			addApp(info.packageName);
			dDialog.dismiss();
		}
	});
	dButton.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			dAdapter.getFilter().filter(dSearch.getText().toString());
		}
	});
	
	dAdapter.update();
	
}
public void removeApp(String pkg) {
	mPrefs.edit().remove(pkg).commit();
	mPkgAdapter.update(getSetStrings());
	updateList();
}
private Set<String> getSetStrings() {
	return mPrefs.getAll().keySet();
}
public void addApp(String pkg) {
	mPrefs.edit().putBoolean(pkg, true).commit();
	updateList();
}
private void updateList() {
	mPkgAdapter.update(getSetStrings());
}
}
