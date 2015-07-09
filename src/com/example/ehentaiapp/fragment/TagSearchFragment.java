package com.example.ehentaiapp.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ehentaiapp.ComicAdapter;
import com.example.ehentaiapp.Constants;
import com.example.ehentaiapp.HomePageActivity;
import com.example.ehentaiapp.R;
import com.example.ehentaiapp.database.EhentaiDBHelper;
import com.example.ehentaiapp.util.DataLoader;
import com.example.ehentaiapp.util.NetworkHelper;
import com.example.winderif.ehentaiapp.DaoMaster;
import com.example.winderif.ehentaiapp.DaoSession;
import com.example.winderif.ehentaiapp.Tag;
import com.example.winderif.ehentaiapp.TagDao;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TagSearchFragment extends AbsListViewBaseFragment {
	public static final String TAG = "TagSearchFragment";

	private ArrayList<String> urlOfComicCover;
	private ArrayList<String> categoryOfComic;
	private ArrayList<String> urlOfComic;

	private ComicAdapter mComicAdapter;

//	@Bind(R.id.actionbar_filter_button)
	ImageButton mImageButton;

	TextView tagTextView;

	@Bind(R.id.error)
	TextView errorView;

	@Bind(R.id.retry)
	Button retryButton;

	private NetworkHelper networkHelper;
	private DataLoader dataLoader;

	private ParserTask task = null;

	private TagDao tagDao;
	private Tag tag;
	private String tagName;

	private int numOfTotalPages = 0;
	private int idxOfPage = 0;

	private static final Pattern gUrlPattern =
			Pattern.compile("http://g\\.e-hentai\\.org/g/(\\d+)/(\\w+)");

	public TagSearchFragment() {
		initData();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setDatabase();

		Intent mIntent = getActivity().getIntent();
		long tagId = mIntent.getLongExtra("tagId", 0);
		tagName = mIntent.getStringExtra("tagName");
		tag = tagDao.load(tagId);

		setActionBarCustomView();

		tag.setLastRead(new Date());
		tag.setLatestCount(0);
		tag.setCount(tag.getCount() + 1);
		tagDao.update(tag);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fr_tag_search_grid, container, false);
		ButterKnife.bind(this, rootView);

		listView = (GridView) rootView.findViewById(R.id.fr_tag_search_grid);
		
		mComicAdapter = new ComicAdapter(getActivity(), urlOfComicCover, categoryOfComic);
		
		((GridView) listView).setAdapter(mComicAdapter);
		((GridView) listView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {

				Intent mIntent = new Intent();
				mIntent.setClass(getActivity(), HomePageActivity.class);

				Matcher matcher = gUrlPattern.matcher(urlOfComic.get(position));
				matcher.find();
				mIntent.putExtra("galleryId", matcher.group(1));
				mIntent.putExtra("galleryToken", matcher.group(2));
				getActivity().startActivityForResult(mIntent, Constants.RequestCode.SEARCH);
			}
		});
		
		networkHelper = NetworkHelper.getInstance(getActivity());
		dataLoader = DataLoader.getInstance(getActivity());

		getGalleryGrid();
		
		return rootView;
	}

	private void setDatabase() {
		EhentaiDBHelper helper = EhentaiDBHelper.getInstance(getActivity().getApplicationContext());
		SQLiteDatabase db = helper.getDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		tagDao = daoSession.getTagDao();
	}

	private void setActionBarCustomView() {
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getActivity().getActionBar().setDisplayShowHomeEnabled(false);
//		getActivity().getActionBar().setIcon(android.R.color.transparent);

		getActivity().getActionBar().setDisplayShowCustomEnabled(true);
		getActivity().getActionBar().setCustomView(R.layout.actionbar_tag_search);
		View view = getActivity().getActionBar().getCustomView();

		tagTextView = (TextView) view.findViewById(R.id.actionbar_tag);
		tagTextView.setText(tagName);

		mImageButton = (ImageButton) view.findViewById(R.id.actionbar_tab_subscribe);
		mImageButton.setSelected(tag.getSubscribed());
		mImageButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				v.setSelected(!v.isSelected());
				if (v.isSelected()) {
					tag.setSubscribed(true);
					showToast("Subscribed");
				} else {
					tag.setSubscribed(false);
					showToast("Unsubscribed");
				}

				tagDao.update(tag);
			}
		});
	}

	private void getGalleryGrid() {
		if(networkHelper.isAvailable()) {
			task = new ParserTask();
			task.execute(Integer.toString(idxOfPage), tagName);
		}
		else {
			showError("No Internet", true);
		}
	}

	private void showError(String errMsg, boolean retry) {
		errorView.setText(errMsg);
		errorView.setVisibility(View.VISIBLE);
		retryButton.setVisibility(View.VISIBLE);
		retryButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getGalleryGrid();
			}
		});
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
			showProgressDialog();
		}
		
		@Override
		protected Void doInBackground(String... query) {

			JSONArray dataList = dataLoader.getGalleryList(
					Constants.TAG_URL, query[0], query[1]);

			if(dataList == null) {
				return null;
			}
			else {
				try {
					for(int i = 0; i < dataList.length() - 1; i++) {
						categoryOfComic.add(dataList.getJSONObject(i).getString("category"));
						urlOfComicCover.add(dataList.getJSONObject(i).getString("urlcover"));
						urlOfComic.add(dataList.getJSONObject(i).getString("urlcomic"));
					}
					numOfTotalPages = dataList.getJSONObject(dataList.length() - 1).getInt("pages");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			task = null;

			dismissProgressDialog();
			
			if(numOfTotalPages == 0) {
				mComicAdapter.notifyDataSetChanged();
				showToast("No Found");
			}
			else {
				mComicAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void applyScrollListener() {
		mark = 0;
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {

				if ((totalItemCount - visibleItemCount) == firstVisibleItem
						&& totalItemCount > mark) {

					mark = totalItemCount;
					idxOfPage++;

					showToast(idxOfPage + "/" + numOfTotalPages);

					if (idxOfPage < numOfTotalPages) {
						task = new ParserTask();
						task.execute(Integer.toString(idxOfPage), tagName);
					} else {
						showToast("End of result");
					}
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}
		});
	}
}
