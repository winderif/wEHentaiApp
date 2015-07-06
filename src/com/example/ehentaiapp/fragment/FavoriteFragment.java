package com.example.ehentaiapp.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ehentaiapp.ComicAdapter;
import com.example.ehentaiapp.Constants;
import com.example.ehentaiapp.HomePageActivity;
import com.example.ehentaiapp.R;
import com.example.ehentaiapp.database.ItemDAO;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavoriteFragment extends AbsListViewBaseFragment {
	private ArrayList<String> urlOfComicCover;
	private ArrayList<String> categoryOfComic;
	private ArrayList<String> urlOfComic;

	@Bind(R.id.fr_favorite)
	GridView mGridView;
	private ComicAdapter mComicAdapter;
	private ProgressDialog mDialog;
	
	private int numOfFavoriteComics = 0;
	private int idxOfPage = 0;
	private String searchQuery = "";
	
	private ItemDAO mItemDAO;

	OnFavoriteChangeListener listener;
	
	public interface OnFavoriteChangeListener {
		public void favoriteChange();
	}
	
	public FavoriteFragment() {
		// TODO Auto-generated constructor stub
		initData();
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
        	listener = (OnFavoriteChangeListener)activity;
        } catch(ClassCastException e) {
        	throw new ClassCastException(activity.toString() + 
        			" must implement OnFavoriteChangeListener");
        }
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("LIFE", "favo create "+getId());
		
        mItemDAO = new ItemDAO(getActivity().getApplicationContext());
        mItemDAO.open();
		
//		new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Log.i("LIFE", "favo create view "+getId());
		
		View rootView = inflater.inflate(R.layout.fr_favorite_grid, container, false);
		ButterKnife.bind(this, rootView);

//		mGridView = (GridView) rootView.findViewById(R.id.fr_favorite);

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
				getActivity().startActivityForResult(mIntent, Constants.RequestCode.FAVORITE);
			}
		});
		
		new ParserTask().execute();
		
		return rootView;
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
	
	private class ParserTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			clearData();
			
			showProgressDialog();
		}
		
		@Override
		protected Void doInBackground(Void... query) {
			// TODO Auto-generated method stub
			// get favorite database
			String where = mItemDAO.IS_FAVORITE_COLUMN + "=" + 1;
//			String where = "";
			categoryOfComic.addAll(mItemDAO.getAllCategory(where));
			urlOfComic.addAll(mItemDAO.getAllUrlOfComic(where));
			urlOfComicCover.addAll(mItemDAO.getAllUrlOfComicCover(where));
			numOfFavoriteComics = urlOfComicCover.size();
			/**
			 * if no favorite data
			 */
			
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
		Log.i("LIFE", "favo menu");
	    super.onCreateOptionsMenu(menu,inflater);
	}
	
	@Override
	protected void applyScrollListener() {
		// TODO Auto-generated method stub
		
	}
	
	public void updateFavorite() {
		new ParserTask().execute();
	}
}	
