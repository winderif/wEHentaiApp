package com.example.ehentaiapp;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ehentaiapp.database.EhentaiDBHelper;
import com.example.ehentaiapp.util.DataLoader;
import com.example.winderif.ehentaiapp.DaoMaster;
import com.example.winderif.ehentaiapp.DaoSession;
import com.example.winderif.ehentaiapp.Gallery;
import com.example.winderif.ehentaiapp.GalleryDao;
import com.example.winderif.ehentaiapp.GallerysToTags;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wefika.flowlayout.FlowLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomePageActivity extends Activity {
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	@Bind(R.id.home_tags)
	FlowLayout mTagLayout;

	@Bind(R.id.home_text_1)
	TextView titleTextView;

	@Bind(R.id.home_text_2)
	TextView sizeTextView;

	@Bind(R.id.home_text_3)
	TextView dateTextView;

	@Bind(R.id.home_text_4)
	TextView categoryTextView;

	@Bind(R.id.home_img)
	ImageView mImageView;

	@Bind(R.id.home_but_1)
	Button mButton;

	private ImageButton mImageButton;

	private GalleryDao galleryDao;

	private Gallery mGallery;

	private DataLoader dataLoader;

	private long gId = 0;
	private String gToken = null;

	private ParseCoverTask task = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		ButterKnife.bind(this);

		Intent mIntent = super.getIntent();
		gId = Long.parseLong(mIntent.getStringExtra("galleryId"));
		gToken = mIntent.getStringExtra("galleryToken");

		dataLoader = DataLoader.getInstance(this);

		setDatabase();

		initView();

		mGallery = galleryDao.load(gId);
		if(mGallery == null) {
			showToast("null");
			task = new ParseCoverTask();
			task.execute();
		}
		else {
			showToast("exist");
			showGallery();

			mGallery.setCount(mGallery.getCount() + 1);
			galleryDao.update(mGallery);
		}
	}
	
	private void initView() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setActionBarFavoriteButton();
	}

	private void setActionBarFavoriteButton() {
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setCustomView(R.layout.actionbar_homepage);
		mImageButton = (ImageButton)findViewById(R.id.actionbar_favorite_button);
	}

	private void setDatabase() {
		EhentaiDBHelper helper = EhentaiDBHelper.getInstance(getApplicationContext());
		SQLiteDatabase db = helper.getDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		galleryDao = daoSession.getGalleryDao();
	}

	@OnClick(R.id.home_but_1)
	public void onReadClick(View v) {
		/*
		Intent mIntent = new Intent();				
		mIntent.setClass(HomePageActivity.this, ScrollActivity.class);
		mIntent.putExtra("url_comic", urlOfComic);
		mIntent.putExtra("num_comic_page", mItem.getNumOfPages());
		startActivity(mIntent);
		*/
	}
	
	public void onFavoriteClick(View v) {
		v.setSelected(!v.isSelected());
    	if(v.isSelected()) {
    		showToast("favorite");
			mGallery.setStarred(true);
    	}
    	else {
    		showToast("no favorite");
			mGallery.setStarred(false);
    	}

		galleryDao.update(mGallery);
	}
		
	private void showToast(String msg) {
    	Toast.makeText(HomePageActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
	
	private class ParseCoverTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... param) {
			mGallery = dataLoader.getGallery(gId, gToken);

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			task = null;

			if(mGallery == null) {
				mButton.setVisibility(View.INVISIBLE);
				mImageButton.setVisibility(View.INVISIBLE);
				return ;
			}

			showGallery();
		}
	}

	private void showGallery() {
		setGalleryThumbnail();
		setGalleryInfo();
		setGalleryTag();
	}

	private void setGalleryThumbnail() {
		imageLoader.displayImage(mGallery.getThumbnail(), mImageView,
				options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						mImageView.setImageBitmap(loadedImage);
					}
				});
	}
	
	private void setGalleryInfo() {
		titleTextView.setText(mGallery.getTitle());
		sizeTextView.setText("Length:\t" + mGallery.getSize() + " Pages");
		dateTextView.setText("Date:\t" + mGallery.getCreated());
		categoryTextView.setText("Category:\t" + mGallery.getCategoryText());

		mImageButton.setSelected(mGallery.getStarred());
	}
	
	private void setGalleryTag() {
		List<GallerysToTags> relationTags = mGallery.getTags();

		LayoutParams layoutParams = new FlowLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		for(GallerysToTags rTag : relationTags) {
			TextView b = new TextView(HomePageActivity.this);
			Drawable d = getResources().getDrawable(R.drawable.tag_button);
			b.setBackgroundDrawable(d);
			b.setText(rTag.getTag().getName());
			b.setTextSize(18);
			b.setGravity(Gravity.CENTER_HORIZONTAL);
			b.setClickable(true);
			b.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					showToast(((TextView)v).getText().toString());
					/*
					Intent result = getIntent();
					result.putExtra("tag", ((TextView)v).getText());
					setResult(1, result);
					finish();
					*/
				}
			});

			mTagLayout.addView(b, layoutParams);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_page, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				Intent result = getIntent();
				setResult(RESULT_OK, result);
            	finish();  
				return true;
			default:
				return super.onOptionsItemSelected(item); 
		}
	}
}
