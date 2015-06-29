package com.example.ehentaiapp.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.SearchView.OnQueryTextListener;

import com.example.ehentaiapp.ComicAdapter;
import com.example.ehentaiapp.Constants;
import com.example.ehentaiapp.HomePageActivity;
import com.example.ehentaiapp.R;
import com.example.ehentaiapp.Filter;

public class SubscribeFragment extends AbsListViewBaseFragment 
		implements OnQueryTextListener {
	public static final String TAG = "SubscribeFragment";
	
	private ArrayList<String> urlOfComicCover;
	private ArrayList<String> categoryOfComic;
	private ArrayList<String> urlOfComic;
	
	private ComicAdapter mComicAdapter; 
	private ProgressDialog mDialog;
	private SearchView searchView;
	private TextView mTextView;
	private ImageButton mImageButton;
	
	private int numOfTotalPages = 0;
	private int idxOfPage = 0;
	private String searchQuery = "";
	
	public SubscribeFragment() {
		// TODO Auto-generated constructor stub
		initData();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		Log.i("LIFE", "sub create");
		
		initActionBar();
		
		if(getArguments() != null) {
			Log.i("LIFE", "sub create bundle 1");
			mTextView.setText(getArguments().getString("tag"));
			mTextView.setVisibility(View.VISIBLE);
			searchView.onActionViewCollapsed();
			searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
			new ParserTask().execute(Integer.toString(idxOfPage), getArguments().getString("tag"));	
		}
		else {
			Log.i("LIFE", "sub create bundle 0");
			new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
		}
	}

	private void initActionBar() {
		// TODO Auto-generated method stub
		getActivity().getActionBar().setCustomView(R.layout.actionbar_subscribe);
	    View view = getActivity().getActionBar().getCustomView();
	    view.setVisibility(View.VISIBLE);
		
	    searchView = (SearchView) view.findViewById(R.id.subscribe_search_view);
	    mTextView = (TextView) view.findViewById(R.id.actionbar_tag);
	    mImageButton = (ImageButton) view.findViewById(R.id.actionbar_filter_button);
	    
	    mTextView.setVisibility(View.GONE);
	    searchView.setOnQueryTextListener(this);
	    searchView.setOnSearchClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View arg0) {
	    		// TODO Auto-generated method stub
	    		mTextView.setVisibility(View.GONE);
	    	}
	    });
	    mImageButton.setOnClickListener(new OnClickListener(){
	        @Override
	        public void onClick(View v) {
	        	initFilterDialog();
	        }
	    });
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Log.i("LIFE", "sub create view");
		
		View rootView = inflater.inflate(R.layout.fr_subscribe_grid, container, false);
		listView = (GridView) rootView.findViewById(R.id.fr_subscribe_grid);
		
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
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.i("LIFE", "sub onBundle");
		super.onSaveInstanceState(outState);
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
			try {
				Document mDoc = Jsoup.connect(Constants.TAG_URL + query[1] + "/" + query[0]).get();
				
				Elements table = mDoc.getElementsByClass("itg"); 
				if(table.isEmpty()) {
					numOfTotalPages = 0;
				}
				else {
					for (Element tr : table.select("tr")) {
						Elements td = tr.select("td");
						if (td.size() >= 4) {
							categoryOfComic.add(td.get(0).child(0).select("img").attr("alt"));

							// comic thumbnail
							if (td.get(2).getElementsByClass("it2").first().select("img").size() 
									> 0) {
								urlOfComicCover.add(td.get(2)
										.getElementsByClass("it2").first()
										.select("img").attr("abs:src"));
							} else {
								String[] token = td.get(2).text().split("~");
								urlOfComicCover.add("http://" + token[1] + "/" + token[2]);
							}
							// comic url
							urlOfComic.add(td.get(2)
									.getElementsByClass("it5").select("a")
									.attr("abs:href"));
						}
					}
				
					// Get number of total books.
					Elements pageBar = mDoc.getElementsByClass("ptt");
					Elements pageBarItem = pageBar.select("td");
					numOfTotalPages = Integer.parseInt(pageBarItem.get(
							pageBarItem.size() - 2).text());
				}
			} catch(IOException e) {
				e.printStackTrace();
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
		Log.i("LIFE", "sub menu");
		menu.getItem(3).setIcon(R.drawable.ic_fr_search_selected);
		
	    getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME);
	    View homeIcon = getActivity().findViewById(android.R.id.home);
	    ((View) homeIcon.getParent()).setVisibility(View.GONE);
	    
//	    getActivity().getActionBar().setCustomView(R.layout.actionbar_subscribe);
//	    View view = getActivity().getActionBar().getCustomView();
//	    view.setVisibility(View.VISIBLE);
//	    searchView = (SearchView) view.findViewById(R.id.subscribe_search_view);
	    
//	    mTextView = (TextView) view.findViewById(R.id.actionbar_tag);
	    /*
	    mTextView.setVisibility(View.GONE);
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
	    	@Override
	    	public boolean onQueryTextSubmit(String query) {
	    		searchView.clearFocus();
	    		searchView.onActionViewCollapsed();
	    		searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
	    		
	    		mTextView.setText(query);
	    		mTextView.setVisibility(View.VISIBLE);
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
	    searchView.setOnSearchClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mTextView.setVisibility(View.GONE);
			}
		});
	    
//	    mImageButton = (ImageButton) view.findViewById(R.id.actionbar_filter_button);
	    mImageButton.setOnClickListener(new OnClickListener(){
	        @Override
	        public void onClick(View v) {
	        	initFilterDialog();
	        }
	    });
*/
	    super.onCreateOptionsMenu(menu, inflater);
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

	@Override
	public boolean onQueryTextChange(String query) {
		// TODO Auto-generated method stub
        return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		searchView.clearFocus();
		searchView.onActionViewCollapsed();
		searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		
		mTextView.setText(query);
		mTextView.setVisibility(View.VISIBLE);
		searchQuery = query;
		idxOfPage = 0;	    		
		mark = 0;
		clearData();
		new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
		return true;
	}
}	
