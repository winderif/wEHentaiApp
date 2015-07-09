package com.example.ehentaiapp.fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ehentaiapp.R;

import java.lang.reflect.Method;

public class FavoriteParentFragment extends AbsListViewBaseFragment {
//	FragmentTabHost mTabHost;
	private ActionBar mActionbar;
	
	public static final String TAG = "FavoriteParentFragment";
	
	public FavoriteParentFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		Log.i("LIFE", "favo pare create");
		mActionbar = getActivity().getActionBar();
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		mActionbar.removeAllTabs();
//		View tabView = getActivity().getLayoutInflater().inflate(R.layout.item_tab, null);
		mActionbar.addTab(mActionbar.newTab()
//					.setCustomView(tabView)
					.setText("Favorite")
					.setTabListener(new FavoriteParentFragmentTabListener<FavoriteFragment>(
						getActivity(), 
						FavoriteFragment.TAG, FavoriteFragment.class)), 0);
		
		mActionbar.addTab(mActionbar.newTab()
					.setText("History")
					.setTabListener(new FavoriteParentFragmentTabListener<HistoryFragment>(
						getActivity(), 
						HistoryFragment.TAG, HistoryFragment.class)), 1, false);
//		}
//		enableEmbeddedTabs();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		Log.i("LIFE", "favo pare create view");
		
		return inflater.inflate(R.layout.actionbar_favorite, null);
	}
	
	public void onStart() {
		super.onStart();
		
		Log.i("LIFE", "favo pare start");
		
		mActionbar.setSelectedNavigationItem(0);		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu,inflater);
		
		Log.i("LIFE", "favo pare menu");
		menu.getItem(Menu.NONE).setIcon(R.drawable.button_selected);
//		getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
//		getActivity().getActionBar().setDisplayShowCustomEnabled(false);
		getActivity().getActionBar().setDisplayShowHomeEnabled(false);
		getActivity().getActionBar().setDisplayShowTitleEnabled(false);
	}
	
	@Override
	protected void applyScrollListener() {
		// TODO Auto-generated method stub
		
	}
	
	// Let tab embedded on action bar, like on landscape mode
	public void enableEmbeddedTabs() {
        try {
            final ActionBar actionBar = getActivity().getActionBar();
            final Method setHasEmbeddedTabsMethod = actionBar.getClass()
                .getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
            setHasEmbeddedTabsMethod.setAccessible(true);
            setHasEmbeddedTabsMethod.invoke(actionBar, true);
        }
        catch(final Exception e) {
            // Handle issues as needed: log, warn user, fallback etc
            // This error is safe to ignore, standard tabs will appear.
        }
    }
}
