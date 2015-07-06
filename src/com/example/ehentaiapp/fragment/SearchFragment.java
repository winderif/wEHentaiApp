package com.example.ehentaiapp.fragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.example.ehentaiapp.ComicAdapter;
import com.example.ehentaiapp.Constants;
import com.example.ehentaiapp.Filter;
import com.example.ehentaiapp.HomePageActivity;
import com.example.ehentaiapp.R;
import com.example.ehentaiapp.util.DataLoader;
import com.example.ehentaiapp.util.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchFragment extends AbsListViewBaseFragment {
	public static final String TAG = "SearchFragment";
	
	private ArrayList<String> urlOfComicCover;
	private ArrayList<String> categoryOfComic;
	private ArrayList<String> urlOfComic;
	
	private ComicAdapter mComicAdapter;

//	@Bind(R.id.search_view)
	SearchView searchView;

//	@Bind(R.id.actionbar_filter_button)
	ImageButton mImageButton;

	@Bind(R.id.error)
	TextView errorView;

	@Bind(R.id.retry)
	Button retryButton;
	
	private NetworkHelper networkHelper;
	private DataLoader dataLoader;
	
	private ParserTask task = null;
	
	private int numOfTotalPages = 0;
	private int idxOfPage = 0;
	private String searchQuery = "";
	
	public SearchFragment() {
		// TODO Auto-generated constructor stub
		initData();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		
//		new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fr_search_grid, container, false);
		ButterKnife.bind(this, rootView);

		listView = (GridView) rootView.findViewById(R.id.fr_search_grid);
		
		mComicAdapter = new ComicAdapter(getActivity(), urlOfComicCover, categoryOfComic);
		
		((GridView) listView).setAdapter(mComicAdapter);
		((GridView) listView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
				View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent();
				mIntent.setClass(getActivity(), HomePageActivity.class);
				mIntent.putExtra("comic_url", urlOfComic.get(position));
				getActivity().startActivityForResult(mIntent, Constants.RequestCode.SEARCH);
			}
		});
		
		networkHelper = NetworkHelper.getInstance(getActivity());
		dataLoader = DataLoader.getInstance(getActivity());
		
		getComicGrid();
		
//		new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
		
		return rootView;
	}
	
	private void getComicGrid() {
		// TODO Auto-generated method stub
		if(networkHelper.isAvailable()) {
			task = new ParserTask();
			task.execute(Integer.toString(idxOfPage), searchQuery);
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
				getComicGrid();
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
			// TODO Auto-generated method stub
			JSONArray dataList = dataLoader.getGalleryList(
					Constants.BASE_URL, query[0], query[1], filter.getOption());
			
			numOfTotalPages = dataLoader.getGalleryListCount(
					Constants.BASE_URL, query[0], query[1], filter.getOption());
			
			if(dataList.length() != 0) {
				for(int i=0; i<dataList.length(); i++) {
					try {
						categoryOfComic.add(dataList.getJSONObject(i).getString("category"));
						urlOfComicCover.add(dataList.getJSONObject(i).getString("urlcover"));
						urlOfComic.add(dataList.getJSONObject(i).getString("urlcomic"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
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
		// TODO Auto-generated method stub
		mark = 0;
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, 
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if((totalItemCount-visibleItemCount) == firstVisibleItem
						&& totalItemCount > mark) {
					mark = totalItemCount;
					idxOfPage++;
					showToast(idxOfPage +"/"+ numOfTotalPages);
					if(idxOfPage < numOfTotalPages) {
						new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);	
					}
					else {
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
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    menu.getItem(2).setIcon(R.drawable.ic_fr_search_selected);
	    getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	    getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	    getActivity().getActionBar().setCustomView(R.layout.actionbar_search);
	    View view = getActivity().getActionBar().getCustomView();
	    view.setVisibility(View.VISIBLE);

	    searchView = (SearchView) view.findViewById(R.id.search_view);
//	    searchView.onActionViewExpanded();
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
	    	@Override
	    	public boolean onQueryTextSubmit(String query) {
	    		searchView.clearFocus();
	    		
	    		searchQuery = query;
	    		idxOfPage = 0;	    		
	    		mark = 0;
	    		clearData();
	    		new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
	            return true;
	    	}

	    	@Override
	    	public boolean onQueryTextChange(String newText) {
	    		return false;
	    	}
	    });

	    mImageButton = (ImageButton) view.findViewById(R.id.actionbar_filter_button);
	    mImageButton.setOnClickListener(new OnClickListener(){
	        @Override
	        public void onClick(View v) {
	        	initFilterDialog();
	        }
	    });

	    super.onCreateOptionsMenu(menu,inflater);
	}

	private void initFilterDialog() {
		AlertDialog.Builder checkDlg = new AlertDialog.Builder(getActivity());
		checkDlg.setTitle("Search Option");
        checkDlg.setMultiChoiceItems(
        		Filter.ARRAY_OF_SEARCH_OPTION_FILTER, 
        		filter.getOption(),
            new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialog,
                                    int which, boolean flag) {
                }
            });
        checkDlg.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
                }
            });

        checkDlg.create().show();
	}
}	
