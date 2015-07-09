package com.example.ehentaiapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.ehentaiapp.Constants;
import com.example.ehentaiapp.GalleryGridAdapter;
import com.example.ehentaiapp.HomePageActivity;
import com.example.ehentaiapp.R;
import com.example.ehentaiapp.database.EhentaiDBHelper;
import com.example.winderif.ehentaiapp.DaoMaster;
import com.example.winderif.ehentaiapp.DaoSession;
import com.example.winderif.ehentaiapp.Gallery;
import com.example.winderif.ehentaiapp.GalleryDao;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.QueryBuilder;

public class FavoriteFragment extends AbsListViewBaseFragment {
	public static final String TAG = "FavoriteFragment";

	@Bind(R.id.fr_favorite)
	GridView mGridView;

	private ArrayList<Gallery> gallerys;

	private GalleryGridAdapter gridAdapter;

	private GalleryDao galleryDao;

	OnFavoriteChangeListener listener;
	
	public interface OnFavoriteChangeListener {
		public void favoriteChange();
	}
	
	public FavoriteFragment() {
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
		Log.i("LIFE", "favo create " + getId());
		
		setDatabase();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Log.i("LIFE", "favo create view " + getId());
		
		View rootView = inflater.inflate(R.layout.fr_favorite_grid, container, false);
		ButterKnife.bind(this, rootView);

		gridAdapter = new GalleryGridAdapter(getActivity(), gallerys);
		mGridView.setAdapter(gridAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {

				Intent mIntent = new Intent();
				mIntent.setClass(getActivity(), HomePageActivity.class);
				mIntent.putExtra("galleryId", gallerys.get(position).getId().toString());
				mIntent.putExtra("galleryToken", gallerys.get(position).getToken());
				getActivity().startActivityForResult(mIntent, Constants.RequestCode.FAVORITE);
			}
		});
		
		new ParserTask().execute();
		
		return rootView;
	}

	private void setDatabase() {
		EhentaiDBHelper helper = EhentaiDBHelper.getInstance(getActivity().getApplicationContext());
		SQLiteDatabase db = helper.getDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		galleryDao = daoSession.getGalleryDao();
	}
	
	private void initData() {
		gallerys = new ArrayList<Gallery>();
	}
	
	private void clearData() {
		gallerys.clear();
	}
	
	private class ParserTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			clearData();
			
			showProgressDialog();
		}
		
		@Override
		protected Void doInBackground(Void... arg) {

			QueryBuilder query = galleryDao.queryBuilder();
			query.where(GalleryDao.Properties.Starred.eq(true));
			gallerys.addAll(query.list());
			/**
			 * if no favorite data
			 */
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dismissProgressDialog();

			if(gallerys.isEmpty()) {
				gridAdapter.notifyDataSetChanged();
				showToast("No Found");
			}
			else {
				gridAdapter.notifyDataSetChanged();
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
		//
	}
	
	public void updateFavorite() {
		new ParserTask().execute();
	}
}	
