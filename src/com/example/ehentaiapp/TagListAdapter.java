package com.example.ehentaiapp;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TagListAdapter extends BaseAdapter {
	private ArrayList<String> tags;
	
	private LayoutInflater mLayoutInflater;
	
	public TagListAdapter(Context context) {
		this.mLayoutInflater = LayoutInflater.from(context);
	}
	
	public TagListAdapter(Context context, ArrayList<String> tags) {		
		this.mLayoutInflater = LayoutInflater.from(context);
		this.tags = tags;
	}
	
	private class ViewHolder {
		public TextView mTextView;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tags.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View view = convertView;
		final ViewHolder holder;
		if(convertView == null) {
			view = this.mLayoutInflater.inflate(R.layout.item_list_tag, null);
			holder = new ViewHolder();			
			holder.mTextView = (TextView) view.findViewById(R.id.item_tag);
			view.setTag(holder);
		}
		else {
			holder = (ViewHolder)view.getTag();
		}		
		
		holder.mTextView.setText(tags.get(position));
				
		return view;
	}		
}
