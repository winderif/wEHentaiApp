package com.example.ehentaiapp.fragment;

import java.util.ArrayList;

import com.example.ehentaiapp.ComicAdapter;
import com.example.ehentaiapp.Constants;
import com.example.ehentaiapp.HomePageActivity;
import com.example.ehentaiapp.R;
import com.example.ehentaiapp.database.ItemDAO;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class HistoryFragment extends AbsListViewBaseFragment {
	private ArrayList<String> urlOfComicCover;
	private ArrayList<String> categoryOfComic;
	private ArrayList<String> urlOfComic;
	
	private GridView mGridView;
	private ComicAdapter mComicAdapter;
	private ProgressDialog mDialog;
	
	private int numOfFavoriteComics = 0;
	private int idxOfPage = 0;
	private String searchQuery = "";
	
	private ItemDAO mItemDAO;
	
	OnHistoryChangeListener listener;
	
	public interface OnHistoryChangeListener {
		public void historyChange();
	}
	
	public HistoryFragment() {
		// TODO Auto-generated constructor stub
		initData();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
        	listener = (OnHistoryChangeListener)activity;
        } catch(ClassCastException e) {
        	throw new ClassCastException(activity.toString() + 
        			" must implement OnHistoryChangeListener");
        }
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        mItemDAO = new ItemDAO(getActivity().getApplicationContext());
        mItemDAO.open();
//		new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fr_history_grid, container, false);
		mGridView = (GridView) rootView.findViewById(R.id.fr_history);
		mComicAdapter = new ComicAdapter(getActivity(), urlOfComicCover, categoryOfComic);
		mGridView.setAdapter(mComicAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
				View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent();
				mIntent.setClass(getActivity(), HomePageActivity.class);
				mIntent.putExtra("comic_url", urlOfComic.get(position));
				getActivity().startActivityForResult(mIntent, Constants.RequestCode.HISTORY);
			}
		});
		
		new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
		
		return rootView;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }
	
	private void initData() {
		urlOfComicCover = new ArrayList<String>();
		categoryOfComic = new ArrayList<String>();
		urlOfComic = new ArrayList<String>();
	}
	
	private void clearData() {
		urlOfComicCover.clear();
		categoryOfComic.clear();
		urlOfComic.clear();
	}
	
	private class ParserTask extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {
			clearData();
			
			showProgressDialog();
		}
		
		@Override
		protected Void doInBackground(String... query) {
			// TODO Auto-generated method stub
			// get favorite database
			String where = "";
			categoryOfComic.addAll(mItemDAO.getAllCategory(where));
			urlOfComic.addAll(mItemDAO.getAllUrlOfComic(where));
			urlOfComicCover.addAll(mItemDAO.getAllUrlOfComicCover(where));
			numOfFavoriteComics = urlOfComicCover.size();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dismissProgressDialog();
			if(numOfFavoriteComics == 0) {
				mComicAdapter.notifyDataSetChanged();
				showToast("No Found");
			}
			else {
				mComicAdapter.notifyDataSetChanged();
			}
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu,inflater);
	}
	
	@Override
	protected void applyScrollListener() {
		// TODO Auto-generated method stub
		
	}

	public void updateHistory() {
		new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
	}
}	
