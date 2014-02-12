package com.lovewuchin.xposed.xmultiwindow.widget.sidebar.adapter;

import java.util.List;

import com.lovewuchin.xposed.xmultiwindow.R;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ApplicationAdapter extends ArrayAdapter<ActivityInfo> {
	LayoutInflater mInflater;
	PackageManager mPackageManager;
	int mResourceId;
	public ApplicationAdapter(Context context, int resource,
			List<ActivityInfo> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);
		mPackageManager = context.getPackageManager();
		mResourceId = resource;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {		
		// TODO Auto-generated method stub
		final MyViewHolder viewHolder;
		final ActivityInfo app = getItem(position);
		if(convertView==null){
			convertView=mInflater.inflate(mResourceId, parent,false);
			viewHolder=new MyViewHolder();
			viewHolder.mImage=(ImageView)convertView.findViewById(R.id.icon);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(MyViewHolder)convertView.getTag();
		}
		final View view=convertView;
		viewHolder.position=position;
		new Thread(new Runnable() {
			public void run() {
				final CharSequence label = app.loadLabel(mPackageManager);
				final Drawable drawable = app.loadIcon(mPackageManager);
				view.post(new Runnable() {

					@Override
					public void run() {
						if (viewHolder.position == position) {
							viewHolder.mImage.setImageDrawable(drawable);
						}
					}
				});
			}
		}).start();
		return convertView;
	}
	class MyViewHolder{
		int position;
		ImageView mImage;
	}

}
