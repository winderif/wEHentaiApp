package com.example.ehentaiapp.fragment;

import android.app.ActionBar;
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
import android.widget.ListView;

import com.example.ehentaiapp.Constants;
import com.example.ehentaiapp.R;
import com.example.ehentaiapp.TagListAdapter;
import com.example.ehentaiapp.TagSearchActivity;
import com.example.ehentaiapp.database.EhentaiDBHelper;
import com.example.ehentaiapp.util.DataLoader;
import com.example.winderif.ehentaiapp.DaoMaster;
import com.example.winderif.ehentaiapp.DaoSession;
import com.example.winderif.ehentaiapp.GalleryDao;
import com.example.winderif.ehentaiapp.GallerysToTagsDao;
import com.example.winderif.ehentaiapp.Tag;
import com.example.winderif.ehentaiapp.TagDao;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.QueryBuilder;

public class TagSubscribeFragment extends AbsListViewBaseFragment {
	public static final String TAG = "TagSubscribeFragment";
	
	private ArrayList<Tag> tags;

	@Bind(R.id.fr_tagsubscribe_list)
	ListView mListView;

	private TagListAdapter mTagListAdapter;

	private TagDao tagDao;
	private GallerysToTagsDao gallerysToTagsDao;
	private GalleryDao galleryDao;

	private DataLoader dataLoader;

	OnSubscribeChangeListener listener;

	public interface OnSubscribeChangeListener {
		public void subscribeChange();
	}

	public TagSubscribeFragment() {
		initData();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			listener = (OnSubscribeChangeListener)activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() +
					" must implement OnSubscribeChangeListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		setDatabase();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fr_tagsubscribe_list, container, false);
		ButterKnife.bind(this, rootView);

		mTagListAdapter = new TagListAdapter(getActivity(), tags);
		
		mListView.setAdapter(mTagListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
				showToast(tags.get(position).getName());

				Intent mIntent = new Intent();
				mIntent.setClass(getActivity(), TagSearchActivity.class);
				mIntent.putExtra("tagId", tags.get(position).getId());
				mIntent.putExtra("tagName", tags.get(position).getName());
				getActivity().startActivityForResult(mIntent, Constants.RequestCode.SUBSCRIBE);
			}
		});

		dataLoader = DataLoader.getInstance(getActivity().getApplicationContext());

		new ParserTask().execute();
		
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i("LIFE", "sub menu");
//		menu.getItem(3).setIcon(R.drawable.ic_fr_search_selected);
		getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setDisplayShowCustomEnabled(false);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	private void initData() {
		tags = new ArrayList<Tag>();
	}
	
	private void clearData() {
		tags.clear();
	}

	private void setDatabase() {
		EhentaiDBHelper helper = EhentaiDBHelper.getInstance(getActivity().getApplicationContext());
		SQLiteDatabase db = helper.getDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		tagDao = daoSession.getTagDao();
		gallerysToTagsDao = daoSession.getGallerysToTagsDao();
		galleryDao = daoSession.getGalleryDao();
	}
	
	private class ParserTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			clearData();
			
			showProgressDialog();
		}
		
		@Override
		protected Void doInBackground(Void... arg) {

			dataLoader.updateTagSubscribe();

			QueryBuilder query = tagDao.queryBuilder();
			query.where(TagDao.Properties.Subscribed.eq(true));

			tags.addAll(query.list());
//			tags.addAll(tagDao.loadAll());

//			tagDao.deleteAll();
//			gallerysToTagsDao.deleteAll();
//			galleryDao.deleteAll();

			/**
			 * if no tags
			 */
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dismissProgressDialog();
			
			if(tags.isEmpty()) {
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

	public void updateSubscribe() {
		new ParserTask().execute();
	}
}
