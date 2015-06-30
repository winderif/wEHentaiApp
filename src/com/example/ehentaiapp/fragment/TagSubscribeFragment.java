package com.example.ehentaiapp.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.SearchView.OnQueryTextListener;

import com.example.ehentaiapp.R;
import com.example.ehentaiapp.TagListAdapter;
import com.example.ehentaiapp.database.TagDAO;

public class TagSubscribeFragment extends AbsListViewBaseFragment {
	public static final String TAG = "TagSubscribeFragment";
	
	private ArrayList<String> tags;
	private int numOfTags = 0;
	
	private ListView mListView;
	private TagListAdapter mTagListAdapter;
	
	private TagDAO mTagDAO;
	
	
	
	public TagSubscribeFragment() {
		initData();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		mTagDAO = new TagDAO(getActivity().getApplicationContext());
		mTagDAO.open();
		
//		Log.i("LIFE", "sub create");
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
//		Log.i("LIFE", "sub create view");
		
		View rootView = inflater.inflate(R.layout.fr_tagsubscribe_list, container, false);
		mListView = (ListView) rootView.findViewById(R.id.fr_tagsubscribe_list);
		
		mTagListAdapter = new TagListAdapter(getActivity(), tags);
		
		mListView.setAdapter(mTagListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
				View view, int position, long id) {
				showToast(tags.get(position));
				/*
				Intent mIntent = new Intent();
				mIntent.setClass(getActivity(), HomePageActivity.class);
				mIntent.putExtra("comic_url", urlOfComic.get(position));
				getActivity().startActivityForResult(mIntent, Constants.RequestCode.SEARCH);
				*/
			}
		});
		
		new ParserTask().execute();
		
		return rootView;
	}
	
	private void initData() {
		tags = new ArrayList<String>();
	}
	
	private void clearData() {
		tags.clear();
	}
	
	private class ParserTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			clearData();
			
			showProgressDialog();
		}
		
		@Override
		protected Void doInBackground(Void... query) {
			tags.addAll(mTagDAO.getAllTag());
			numOfTags = tags.size();
			/**
			 * if no tags
			 */
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dismissProgressDialog();
			
			if(numOfTags == 0) {
				mTagListAdapter.notifyDataSetChanged();
				showToast("No Found");
			}
			else {
				mTagListAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void applyScrollListener() {

	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i("LIFE", "sub menu");
//		menu.getItem(3).setIcon(R.drawable.ic_fr_search_selected);
	    super.onCreateOptionsMenu(menu, inflater);
	}
}	
