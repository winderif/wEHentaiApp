package com.example.ehentaiapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.ehentaiapp.ComicScrollView.ScrollViewListener;
import com.example.ehentaiapp.ComicScrollView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.FailReason.FailType;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class ScrollActivity extends Activity implements ScrollViewListener {
	private static final int NUM_OF_SHOW_PAGE = 40;
	private int numOfComicPage = 0;
	private int idxOfPage = 0;
	
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ProgressDialog mDialog;
	
	private String urlOfComic;
	private ArrayList<String> urlOfComicPage;
	private ArrayList<String> urlOfComicPageImg;
	
	private LinearLayout layout;
	private ComicScrollView mScrollView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroll);
		mScrollView = (ComicScrollView)findViewById(R.id.comic_scrollview);
		mScrollView.setScrollViewListener(this);
		layout = (LinearLayout)findViewById(R.id.comic_scroll_layout);
			
		Intent mIntent = super.getIntent();
		urlOfComic = mIntent.getStringExtra("url_comic");
		numOfComicPage = mIntent.getIntExtra("num_comic_page", 0); 
		urlOfComicPageImg = new ArrayList<String>();
		urlOfComicPage = new ArrayList<String>();
		
		new ParserTask().execute();	
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
	
	private void showProgressDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(ScrollActivity.this);
            mDialog.setTitle("Please wait");
			mDialog.setMessage("Loading...");
            mDialog.setCancelable(false);
        }
        mDialog.show();
    }
	
	private boolean isEndPage() {
		if(idxOfPage == numOfComicPage) {
			return false;
		}
		else 
			return true;
	}

    private void dismissProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
    
    private void showToast(String msg) {
    	Toast.makeText(ScrollActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
	
	private class ParserTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			showProgressDialog();
		}
		
		@Override
		protected Void doInBackground(Void... param) {
			// TODO Auto-generated method stub
			
			try {
				int numOfUrlPage = numOfComicPage / NUM_OF_SHOW_PAGE
						+ ((numOfComicPage % NUM_OF_SHOW_PAGE == 0)?0:1);
				for(int i=0; i<numOfUrlPage; i++) {
					Document mBookDoc = Jsoup.connect(urlOfComic + "?p=" + i).get();
					Element mBookTable = mBookDoc.getElementById("gdt");
					Elements mBookPage = mBookTable.getElementsByClass("gdtm");
					
					// Get url of each comic page
					for(Element page : mBookPage) {
						urlOfComicPage.add(page.select("a").attr("abs:href"));
					}
					
					// Get image url of the first comic page 
					Document pageImg = Jsoup.connect(urlOfComicPage.get(0)).get();
					Element pageImgLink = pageImg.getElementById("i3");
					urlOfComicPageImg.add(pageImgLink.select("img").attr("abs:src"));
				}
			} catch(IOException e) {
				e.printStackTrace();
			}		
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dismissProgressDialog();
			
			ImageView image = new ImageView(ScrollActivity.this);
			image.setId(R.id.scroll_image);
			imageLoader.displayImage(urlOfComicPageImg.get(0), 
					image, options, new SimpleImageLoadingListener() {
				@Override
		        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					ImageView imageView = (ImageView) view.findViewById(R.id.scroll_image);
					int width = ScrollActivity.this.getResources().getDisplayMetrics().widthPixels;
					imageView.getLayoutParams().width = width;
					imageView.getLayoutParams().height = loadedImage.getHeight()*width/loadedImage.getWidth();
					imageView.setImageBitmap(loadedImage);
					mScrollView.LoadingComplete();
	        	}
			});
			layout.addView(image);
		}	
	}
	
	@Override
	public void onBackPressed() {
		imageLoader.stop();
		ScrollActivity.this.setResult(RESULT_OK);
		ScrollActivity.this.finish();   
		super.onBackPressed();
	}
	
	private class PageLoaderTask extends AsyncTask<Integer, Void, Void> {
		
		@Override
		protected Void doInBackground(Integer... param) {
			// TODO Auto-generated method stub
			try {
				Document pageImg = Jsoup.connect(urlOfComicPage.get(param[0])).get();
				Element pageImgLink = pageImg.getElementById("i3");
				urlOfComicPageImg.add(pageImgLink.select("img").attr("abs:src"));
			} catch(IOException e) {
				e.printStackTrace();
			}		
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {			
			ImageView image = new ImageView(ScrollActivity.this);
			image.setId(R.id.scroll_image);
			imageLoader.displayImage(urlOfComicPageImg.get(urlOfComicPageImg.size()-1), 
					image, options, new SimpleImageLoadingListener() {
				@Override
		        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					ImageView imageView = (ImageView) view.findViewById(R.id.scroll_image);
					int width = ScrollActivity.this.getResources().getDisplayMetrics().widthPixels;
					imageView.getLayoutParams().width = width;
					imageView.getLayoutParams().height = loadedImage.getHeight()*width/loadedImage.getWidth();
					imageView.setImageBitmap(loadedImage);		
					mScrollView.LoadingComplete();
	        	}
				
				@Override
			    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					switch(failReason.getType()) {
						case IO_ERROR:
							new PageReLoaderTask().execute(idxOfPage);
							break;
						case OUT_OF_MEMORY:
							imageLoader.clearMemoryCache();
							new PageReLoaderTask().execute(idxOfPage);
							break;
						default:
							break;
					}
			    }
			});
			layout.addView(image);
		}	
	}

private class PageReLoaderTask extends AsyncTask<Integer, Void, Void> {
		
		@Override
		protected Void doInBackground(Integer... param) {
			// TODO Auto-generated method stub
			try {
				Document pageImg = Jsoup.connect(urlOfComicPage.get(param[0])).get();
				Element pageImgLink = pageImg.getElementById("i3");
				urlOfComicPageImg.set(param[0], pageImgLink.select("img").attr("abs:src"));
			} catch(IOException e) {
				e.printStackTrace();
			}		
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {			
			ImageView image = new ImageView(ScrollActivity.this);
			image.setId(R.id.scroll_image);
			imageLoader.displayImage(urlOfComicPageImg.get(urlOfComicPageImg.size()-1), 
					image, options, new SimpleImageLoadingListener() {
				@Override
		        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					ImageView imageView = (ImageView) view.findViewById(R.id.scroll_image);
					int width = ScrollActivity.this.getResources().getDisplayMetrics().widthPixels;
					imageView.getLayoutParams().width = width;
					imageView.getLayoutParams().height = loadedImage.getHeight()*width/loadedImage.getWidth();
					imageView.setImageBitmap(loadedImage);				
	        	}
				
				@Override
			    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					switch(failReason.getType()) {
						case IO_ERROR:
							new PageReLoaderTask().execute(idxOfPage);
							break;
						case OUT_OF_MEMORY:
							imageLoader.clearMemoryCache();
							new PageReLoaderTask().execute(idxOfPage);
							break;
						default:
							break;
					}
			    }
			});
			layout.addView(image);
		}	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scroll, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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
		Log.i("PAGE", idxOfPage+" "+numOfComicPage);
		if(isEndPage()) {
			new PageLoaderTask().execute(idxOfPage);
		}
		else {
			/**
			 * End Page
			 */
		}
	}
}
