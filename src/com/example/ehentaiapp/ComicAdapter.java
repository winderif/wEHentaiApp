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

public class ComicAdapter extends BaseAdapter {
	private ArrayList<String> IMAGE_URLS;
	private ArrayList<String> CATEGORIES;	
	
	private LayoutInflater mLayoutInflater;
	
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = 
			new AnimateFirstDisplayListener();
	
	public ComicAdapter(Context context) {
		this.mLayoutInflater = LayoutInflater.from(context);
	}
	
	public ComicAdapter(Context context, ArrayList<String> imgs, ArrayList<String> categorys) {		
		this.mLayoutInflater = LayoutInflater.from(context);
		
		this.options = new DisplayImageOptions.Builder()		
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.cacheInMemory(false).cacheOnDisk(true).build();
		
		this.IMAGE_URLS = imgs;		
		this.CATEGORIES = categorys;
	}
	
	private class ViewHolder {
		public ImageView mImgView;
		public TextView mTextView;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return IMAGE_URLS.size();
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
//			view = this.mLayoutInflater.inflate(R.layout.grid_item, parent, false);
			view = this.mLayoutInflater.inflate(R.layout.grid_item, null);
			holder = new ViewHolder();			
			holder.mImgView = (ImageView) view.findViewById(R.id.image);
			holder.mTextView = (TextView) view.findViewById(R.id.text);
			view.setTag(holder);
		}
		else {
			holder = (ViewHolder)view.getTag();
		}		
		
		holder.mTextView.setText(CATEGORIES.get(position));
		ImageLoader.getInstance().displayImage(IMAGE_URLS.get(position), holder.mImgView, 
				options, animateFirstListener);		
				
		return view;
	}		
}
