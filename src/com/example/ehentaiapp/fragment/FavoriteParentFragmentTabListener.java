package com.example.ehentaiapp.fragment;

import com.example.ehentaiapp.R;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class FavoriteParentFragmentTabListener <T extends Fragment> 
	implements ActionBar.TabListener {

	private Fragment mFragment;
    private FragmentActivity mActivity;
    private String mTag;
    private Class<T> mClass;
	
	public FavoriteParentFragmentTabListener(FragmentActivity activity, String tag, Class<T> clz) {
		// TODO Auto-generated constructor stub
		mActivity = activity;
		mTag = tag;
		mClass = clz;
		mFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {  
		// TODO Auto-generated method stub
		Log.i("LIFE", "favo pare retab" + mTag);
		mFragment = Fragment.instantiate(mActivity, mClass.getName());
		FragmentManager fm = mActivity.getSupportFragmentManager();
		fm.beginTransaction().replace(R.id.container, mFragment, mTag).commit();
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		mFragment = Fragment.instantiate(mActivity, mClass.getName());
		FragmentManager fm = mActivity.getSupportFragmentManager();
		fm.beginTransaction().replace(R.id.container, mFragment, mTag).commit();
		/*
		if(mFragment == null) {
			Log.i("LISTENER", "null " + mTag);
			mFragment = Fragment.instantiate(mActivity, mClass.getName());
			FragmentManager fm = mActivity.getSupportFragmentManager();
//			fm.beginTransaction().add(R.id.container, mFragment, mTag).commit();
			fm.beginTransaction().replace(R.id.container, mFragment, mTag).commit();
		} 
		else {
			Log.i("LISTENER", "attach " + mTag);
			FragmentManager fm = mActivity.getSupportFragmentManager();
			fm.beginTransaction().attach(mFragment).commit();
		}
		*/
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {  
		// TODO Auto-generated method stub
		/*
		if(mFragment != null) {
			Log.i("LISTENER", "detach " + mTag);
			FragmentManager fm = mActivity.getSupportFragmentManager();
			fm.beginTransaction().detach(mFragment).commit();
		}
		*/
	}

}
