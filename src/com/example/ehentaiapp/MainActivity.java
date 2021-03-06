package com.example.ehentaiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ehentaiapp.fragment.FavoriteFragment;
import com.example.ehentaiapp.fragment.FavoriteFragment.OnFavoriteChangeListener;
import com.example.ehentaiapp.fragment.FavoriteParentFragment;
import com.example.ehentaiapp.fragment.HistoryFragment;
import com.example.ehentaiapp.fragment.HistoryFragment.OnHistoryChangeListener;
import com.example.ehentaiapp.fragment.MainFragment;
import com.example.ehentaiapp.fragment.SearchFragment;
import com.example.ehentaiapp.fragment.SubscribeFragment;
import com.example.ehentaiapp.fragment.TagSubscribeFragment;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

public class MainActivity extends FragmentActivity
		implements OnFavoriteChangeListener, OnHistoryChangeListener,
					TagSubscribeFragment.OnSubscribeChangeListener {

	private SharedPreferences prefs;
	private int prefCacheDirValue = 0;
	private String prefCacheDir;
	
	private String tag = "";
	private boolean tagRequest = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("LIFE", "main act create");
		
		initCacheDir();

		initImageLoader();
		
		initMainView();
		
		initMainFragment();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.i("LIFE", "main act onBundle");
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onPostResume() {
		super.onPostResume();
		
		if(tagRequest) {
			Bundle args = new Bundle();
			args.putString("tag", tag);

			Fragment mFrag = new SubscribeFragment();
			mFrag.setArguments(args);
			getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, mFrag, SubscribeFragment.TAG)
				.commit();
			
			setTitle("Subscribe");
		}
		
		tagRequest = false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("BACK", requestCode + " " + resultCode);
		switch (resultCode) {
		case RESULT_OK:
			if(requestCode != Constants.RequestCode.SETTINGS) {
				if (requestCode == Constants.RequestCode.MAIN
						|| requestCode == Constants.RequestCode.SEARCH) {
					if (data.getBooleanExtra("isExist", false)) {
						showToast("update main");
					} else {
						showToast("Insert main");
						// Update view of HistoryFragment
						historyChange();
					}
					favoriteChange();
				}
				else if (requestCode == Constants.RequestCode.FAVORITE
						|| requestCode == Constants.RequestCode.HISTORY) {
					showToast("update favorite or history");
					// Update view of FavoriteFragment
					favoriteChange();
				}
				else if(requestCode == Constants.RequestCode.SUBSCRIBE) {
					showToast("update subscribe");
					subscribeChange();
				}
				else {
					//
				}
			}
			break;
		case 1:
			showToast(data.getStringExtra("tag"));
			tag = data.getStringExtra("tag");
			tagRequest = true;

		default:
			break;
		}

		super.onActivityResult(requestCode, requestCode, data);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("LIFE", "main act menu");
		getMenuInflater().inflate(R.menu.main_menu, menu);
		
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Fragment mFrag;
		
		switch(item.getItemId()) {						
			case R.id.action_main:	
				initMainFragment();
				return true;
			case R.id.action_favorite:
				mFrag = getSupportFragmentManager().findFragmentByTag(FavoriteParentFragment.TAG);
				if(mFrag == null) {
					Log.i("LIFE", "main act favo null frag");
					mFrag = new FavoriteParentFragment();
					
					getSupportFragmentManager().beginTransaction()
						.replace(android.R.id.content, mFrag, FavoriteParentFragment.TAG)
						.commit();
				}
				else {
					Log.i("LIFE", "main act favo frag");
					getSupportFragmentManager().beginTransaction()
						.detach(mFrag)
						.attach(mFrag)
						.commit();
				}
				
				setTitle("Favorite");
				return true;
			case R.id.action_search:
				mFrag = new SearchFragment();
				getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, mFrag, SearchFragment.TAG)
					.commit();

				setTitle("Search");
				return true;
			case R.id.action_subscribe:
//				mFrag = new SubscribeFragment();
				mFrag = new TagSubscribeFragment();
				getSupportFragmentManager().beginTransaction()
//					.replace(android.R.id.content, mFrag, SubscribeFragment.TAG)
					.replace(android.R.id.content, mFrag, TagSubscribeFragment.TAG)
					.commit();

				setTitle("Subscribe");
				return true;
			case R.id.action_settings:
	
				Intent mIntent = new Intent();
				mIntent.setClass(this, MainPreferenceActivity.class);
				startActivityForResult(mIntent, Constants.RequestCode.SETTINGS);
				return true;
			default:
				return super.onOptionsItemSelected(item);		
		}		
	}
	
	private void initMainView() {
		// Set action bar to no show home
		getActionBar().setDisplayShowHomeEnabled(false);
	}
	
	private void initMainFragment() {
		Fragment mFrag = new MainFragment();
		getSupportFragmentManager().beginTransaction()
			.replace(android.R.id.content, mFrag, MainFragment.TAG)
			.commit();
/*		
		Fragment mFrag;
		mFrag = getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
		if(mFrag == null) {
			Log.i("LIFE", "main act main null");
			mFrag = new MainFragment();
			
			getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, mFrag, MainFragment.TAG)
				.commit();
		}
		else {
			Log.i("LIFE", "main act main exist");
			getSupportFragmentManager().beginTransaction()
				.detach(mFrag)
				.attach(mFrag)
				.commit();	
		}
	*/	
		setTitle("Main");			
	}
	
	private void initCacheDir() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefCacheDirValue = Integer.parseInt(prefs.getString(
				MainPreferenceActivity.PREF_KEY_CACHE_DIR, "0"));		
		prefCacheDir = MainPreferenceActivity.PREF_CACHE_DIR[prefCacheDirValue];		
	}
	
	private void initImageLoader() {
		ImageLoaderConfiguration config = 
				new ImageLoaderConfiguration.Builder(this)
				.diskCache(new UnlimitedDiskCache(new File(prefCacheDir)))
//				.diskCacheExtraOptions(480, 320, null) // image size too small
				.denyCacheImageMultipleSizesInMemory()
				.build();
		ImageLoader.getInstance().init(config);
	}
	
	private void showToast(String msg) {
    	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
	
	@Override
	public void favoriteChange() {
		FavoriteFragment frag = 
				(FavoriteFragment)getSupportFragmentManager().findFragmentByTag(FavoriteFragment.TAG);
		if(frag != null) {
			frag.updateFavorite();	
		}
	}

	@Override
	public void historyChange() {
		HistoryFragment frag =
				(HistoryFragment)getSupportFragmentManager().findFragmentByTag(HistoryFragment.TAG);
		if(frag != null) {
			frag.updateHistory();
		}
	}

	@Override
	public void subscribeChange() {
		TagSubscribeFragment frag =
				(TagSubscribeFragment)getSupportFragmentManager().findFragmentByTag(TagSubscribeFragment.TAG);
		if(frag != null) {
			frag.updateSubscribe();
		}
	}
}
