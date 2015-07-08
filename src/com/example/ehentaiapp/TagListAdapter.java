package com.example.ehentaiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.winderif.ehentaiapp.Tag;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TagListAdapter extends BaseAdapter {
	private ArrayList<Tag> tags;
	
	private LayoutInflater mLayoutInflater;
	
	public TagListAdapter(Context context) {
		this.mLayoutInflater = LayoutInflater.from(context);
	}
	
	public TagListAdapter(Context context, ArrayList<Tag> tags) {
		this.mLayoutInflater = LayoutInflater.from(context);
		this.tags = tags;
	}
	
	static class ViewHolder {
		@Bind(R.id.item_tag)
		TextView mTextView;

		@Bind(R.id.item_tag_new)
		TextView mNewTextView;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
	
	@Override
	public int getCount() {
		return tags.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;

		if(convertView == null) {
			view = this.mLayoutInflater.inflate(R.layout.item_list_tag, null);

			holder = new ViewHolder(view);

			view.setTag(holder);
		}
		else {
			holder = (ViewHolder)view.getTag();
		}		

		if(tags.get(position).getLatestCount() > 0) {
			holder.mNewTextView.setVisibility(View.VISIBLE);
			holder.mNewTextView.setText(tags.get(position).getName()
					+ " " + tags.get(position).getLatestCount());
			holder.mTextView.setVisibility(View.GONE);
		}
		else {
			holder.mNewTextView.setVisibility(View.GONE);
			holder.mTextView.setVisibility(View.VISIBLE);
			holder.mTextView.setText(tags.get(position).getName());
		}

		return view;
	}		
}
