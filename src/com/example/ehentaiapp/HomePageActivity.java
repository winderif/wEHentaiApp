package com.example.ehentaiapp;

import java.io.IOException;
import java.util.ArrayList;

import com.example.ehentaiapp.database.Item;
import com.example.ehentaiapp.database.ItemDAO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.support.v4.app.NavUtils;
import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.wefika.flowlayout.FlowLayout;

public class HomePageActivity extends Activity {
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
//	private LinearLayout mTagLayout;
	private FlowLayout mTagLayout;
	private TextView titleOfComicTextView;
	private TextView numOfComicPageTextView;
	private TextView datetimeTextView;
	private TextView categoryTextView;
	private ImageView mImageView;
	private ImageButton mImageButton;
	private Button mButton;
	private Button mTagButton;
	
	private ItemDAO mItemDAO;
	private Item mItem;
	private ArrayList<String> mTags;
	
	private String urlOfComic;
	private boolean isExist = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		
		Intent mIntent = super.getIntent();
		urlOfComic = mIntent.getStringExtra("comic_url");

		mTags = new ArrayList<String>();
		mItemDAO = new ItemDAO(getApplicationContext());
		setExist(mItemDAO.isExist(urlOfComic));
		if(isExist()) {			
			mItem = mItemDAO.get(urlOfComic);
		}
		else {
			mItem = new Item();
		}
		
		initView();
		
		new ParseCoverTask().execute();
	}
	
	private void initView() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		findView();
		
		setActionBarFavoriteButton();		
		/** 判斷是否為favorite，ture要設selected
		 * 
		 */
	}
	
	private void findView() {
		mTagLayout = (FlowLayout)findViewById(R.id.home_tags);
		titleOfComicTextView = (TextView)findViewById(R.id.home_text_1);
		numOfComicPageTextView = (TextView)findViewById(R.id.home_text_2);
		datetimeTextView = (TextView)findViewById(R.id.home_text_3);
		categoryTextView = (TextView)findViewById(R.id.home_text_4);
		mImageView = (ImageView)findViewById(R.id.home_img);
		mButton = (Button)findViewById(R.id.home_but_1);
		mTagButton = (Button)findViewById(R.id.tag_button);
	}
	
	private void setActionBarFavoriteButton() {
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setCustomView(R.layout.actionbar_homepage);
		mImageButton = (ImageButton)findViewById(R.id.actionbar_favorite_button);
		mImageButton.setSelected(mItem.isFavorite());
	}

	public void onReadClick(View v) {
		Intent mIntent = new Intent();				
		mIntent.setClass(HomePageActivity.this, ScrollActivity.class);
//		mIntent.setClass(HomePageActivity.this, CominPageActivity.class);
		mIntent.putExtra("url_comic", urlOfComic);
		mIntent.putExtra("num_comic_page", mItem.getNumOfPages());
		startActivity(mIntent);
	}
	
	public void onFavoriteClick(View v) {
		v.setSelected(!v.isSelected());
    	if(v.isSelected()) {
    		showToast("favorite");
    		mItem.setFavorite(true);
    	}
    	else {
    		showToast("no favorite");
    		mItem.setFavorite(false);
    	} 
	}
		
	private void showToast(String msg) {
    	Toast.makeText(HomePageActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
	
	private class ParseCoverTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... param) {
			// TODO Auto-generated method stub
			try {
				
				// 處理沒有漫畫
				Document mBookDoc = Jsoup.connect(urlOfComic).get();
				mItem.setUrlOfComic(urlOfComic);
				Element mBookTable = mBookDoc.getElementById("gd1");
				if(mBookTable == null) {
					setNotFoundComic();
				}
				else {
					mItem.setUrlOfComicCover(mBookTable.select("img").attr("abs:src"));
					Element mBookTitle = mBookDoc.getElementById("gd2");
					mItem.setTitle(mBookTitle.child(0).text());
					Element mBookInfo = mBookDoc.getElementById("gdd");
					mItem.setDatetime(mBookInfo.select("tr").first().child(1).text());
					mItem.setCategory(mBookDoc.getElementById("gdc").select("img").attr("alt"));
					mItem.setNumOfPages(Integer.parseInt(
							mBookInfo.select("tr").get(5).text().split(" ")[1]));
					
					Element mBookTag = mBookDoc.getElementById("taglist");
					for(Element tag : mBookTag.select("tr")) {
						for(Element child : tag.select("td").get(1).children()) {
							mTags.add(child.text());
						}
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			}		
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(mItem.getNumOfPages() == 0) {
				mButton.setVisibility(View.INVISIBLE);
				mImageButton.setVisibility(View.INVISIBLE);
			}
			
			setComicInfo();
			
			imageLoader.displayImage(mItem.getUrlOfComicCover(), mImageView, 
					options, new SimpleImageLoadingListener() {
				@Override
		        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					mImageView.setImageBitmap(loadedImage);				
	        	}
			});
			
			
			if(mTags.size() == 0) {
				mTagLayout.setVisibility(View.GONE);
			}
			else {
				setTagView();
			}
		}	
	}
	
	private void setNotFoundComic() {
		mItem.setDatetime("");
		mItem.setCategory("");
		mItem.setTitle("");
		mItem.setUrlOfComic("");
		mItem.setUrlOfComicCover("");
		mItem.setNumOfPages(0);
		mItem.setFavorite(false);
	}
	
	private void setComicInfo() {
		titleOfComicTextView.setText(mItem.getTitle());
		numOfComicPageTextView.setText("Length:\t" + mItem.getNumOfPages() + " Pages");
		datetimeTextView.setText("Date:\t" + mItem.getDatetime());
		categoryTextView.setText("Category:\t" + mItem.getCategory());
	}
	
	private void setTagView() {
		LayoutParams layoutParams = new FlowLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
		for(String tag : mTags) {
			TextView b = new TextView(HomePageActivity.this);
			Drawable d = getResources().getDrawable(R.drawable.tag_button);
			b.setBackgroundDrawable(d);
			b.setText(tag);
			b.setTextSize(18);
			b.setGravity(Gravity.CENTER_HORIZONTAL);
			b.setClickable(true);
			b.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
//					showToast(((TextView)v).getText().toString());
					Intent result = getIntent();
		            result.putExtra("tag", ((TextView)v).getText());
					setResult(1, result);
	            	finish();  	
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
//				NavUtils.navigateUpFromSameTask(this);
				Intent result = getIntent();
	            result.putExtra("com.example.ehentaiapp.database.Item", mItem);		            
	            result.putExtra("isExist", isExist());
				setResult(RESULT_OK, result);
            	finish();  
				return true;
			default:
				return super.onOptionsItemSelected(item); 
		}
	}

	public boolean isExist() {
		return isExist;
	}

	public void setExist(boolean isExist) {
		this.isExist = isExist;
	}
}
