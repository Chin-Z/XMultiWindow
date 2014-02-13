package com.lovewuchin.xposed.xmultiwindow.widget.sidebar.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.lovewuchin.xposed.xmultiwindow.R;
import com.lovewuchin.xposed.xmultiwindow.helpers.MyApplication;
import com.lovewuchin.xposed.xmultiwindow.widget.sidebar.SideBarApp;
import com.lovewuchin.xposed.xmultiwindow.widget.sidebar.adapter.PackageNameAdapter.PackageItem;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ApplicationAdapter extends BaseAdapter {
	final Handler mHandler;
	final PackageManager mPackageManager;
	final LayoutInflater mLayoutInflater;
	protected List<PackageInfo> mInstalledAppInfo;
	protected List<PackageItem> mApps = new LinkedList<PackageItem>();
	public ApplicationAdapter(Context act, Set<String> app_array) {
		act=MyApplication.appContext;
		mHandler = new Handler();
		mPackageManager = act.getPackageManager();
		mLayoutInflater = (LayoutInflater) act
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		update(app_array);
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {		
		// TODO Auto-generated method stub
		final MyViewHolder viewHolder;
		if(convertView==null){
			convertView=mLayoutInflater.inflate(R.layout.sidebar_package_list, parent,false);
			viewHolder=new MyViewHolder();
			viewHolder.mImage=(ImageView) convertView.findViewById(android.R.id.icon);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(MyViewHolder)convertView.getTag();
		}
		final View view=convertView;
		final PackageItem appInfo = getItem(position);
		viewHolder.position=position;
		viewHolder.mImage.setImageDrawable(appInfo.icon);
		return convertView;
	}
	class MyViewHolder{
		int position;
		ImageView mImage;
	}
	@Override
	public int getCount() {
		return mApps.size();
	}
	
	@Override
	public PackageItem getItem(int position) {
		return mApps.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return mApps.get(position).hashCode();
	}
	public void update(final Set<String> app_array) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (mApps) {
					mApps.clear();
					for (String pkg_name : app_array) {
						try {
						ApplicationInfo ai = mPackageManager.getApplicationInfo(pkg_name, 0);
						final PackageItem item = new PackageItem();
						item.title = ai.loadLabel(mPackageManager);
						item.icon =  ai.loadIcon(mPackageManager);
						item.packageName = ai.packageName;
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								final int index = Collections.binarySearch(mApps, item);
								if (index < 0) {
									mApps.add((-index - 1), item);
								}
							}
						});
						} catch (Exception e) {
							
						}
					}
					notifyDataSetChangedOnHandler();
				}
			}
		}).start();
	}
	
	private void notifyDataSetChangedOnHandler() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}
	public class PackageItem implements Comparable<PackageItem> {
		public CharSequence title;
		public String packageName;
		public Drawable icon;
		
		@Override
		public int compareTo(PackageItem another) {
			return this.title.toString().compareTo(another.title.toString());
		}
	}

}
