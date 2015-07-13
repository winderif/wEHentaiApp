package com.example.ehentaiapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ehentaiapp.ComicScrollView.ScrollViewListener;
import com.example.ehentaiapp.database.EhentaiDBHelper;
import com.example.ehentaiapp.util.DataLoader;
import com.example.winderif.ehentaiapp.DaoMaster;
import com.example.winderif.ehentaiapp.DaoSession;
import com.example.winderif.ehentaiapp.Gallery;
import com.example.winderif.ehentaiapp.GalleryDao;
import com.example.winderif.ehentaiapp.Image;

import java.util.ArrayList;
import java.util.List;

public class ScrollActivity extends Activity implements ScrollViewListener {

	private int idxOfPage = 0;

	private ComicScrollView mScrollView;
	private ImageAdapter adapter;
	private ProgressDialog mDialog;

	private Gallery mGallery;
	private List<Image> images;
	private GalleryDao galleryDao;

	private DataLoader dataLoader;

	private ParserTask task = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroll);

		images = new ArrayList<Image>();
		mScrollView = (ComicScrollView)findViewById(R.id.comic_scrollview);
		adapter = new ImageAdapter(this, images);
		mScrollView.setAdapter(adapter);

		setDatabase();

		Intent mIntent = super.getIntent();
		long id = mIntent.getLongExtra("galleryId", 0);

		mGallery = galleryDao.load(id);

		if(mGallery == null) {
			return ;
		}
		else {
			dataLoader = DataLoader.getInstance(this);

			task = new ParserTask();
			task.execute();
		}
	}
	
	@Override
	protected void onPause() {
		dismissProgressDialog();
		super.onPause();
	}
	
	@Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

	private void setDatabase() {
		EhentaiDBHelper helper = EhentaiDBHelper.getInstance(getApplicationContext());
		SQLiteDatabase db = helper.getDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		galleryDao = daoSession.getGalleryDao();
	}

	private void showProgressDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(ScrollActivity.this);
            mDialog.setTitle("Please wait");
			mDialog.setMessage("Loading...");
            mDialog.setCancelable(false);
        }
        mDialog.show();
    }
	
    private void dismissProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
    
	private class ParserTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			showProgressDialog();
		}
		
		@Override
		protected Void doInBackground(Void... param) {

			images.addAll(dataLoader.getImageList(mGallery));

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			task = null;

			dismissProgressDialog();

			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		setResult(RESULT_OK);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.scroll, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLoading(ComicScrollView scrollView) {
		// TODO Auto-generated method stub
		idxOfPage++;
		Log.i("PAGE", "page: " + idxOfPage);
	}
}
