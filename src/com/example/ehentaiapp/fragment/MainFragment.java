package com.example.ehentaiapp.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.ehentaiapp.Constants;
import com.example.ehentaiapp.HomePageActivity;
import com.example.ehentaiapp.ComicAdapter;
import com.example.ehentaiapp.R;
import com.example.ehentaiapp.util.DataLoader;
import com.example.ehentaiapp.util.NetworkHelper;

import android.widget.AbsListView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class MainFragment extends AbsListViewBaseFragment {
	public static final String TAG = "MainFragment";
	
	private ArrayList<String> urlOfComicCover;
	private ArrayList<String> categoryOfComic;
	private ArrayList<String> urlOfComic;
	
	private ComicAdapter mComicAdapter; 
	private SearchView searchView;
	private TextView errorView;
	private Button retryButton;
	
	private NetworkHelper networkHelper;
	private DataLoader dataLoader;
	
	private ParserTask task = null;
	
	private int numOfTotalPages = 0;
	private int idxOfPage = 0;
	private String searchQuery = "";
	
	public MainFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("LIFE", "main create");
		
		setHasOptionsMenu(true);
		
		initData();
//		new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Log.i("LIFE", "main create view");
		View rootView = inflater.inflate(R.layout.fr_main_grid, container, false);
		listView = (GridView) rootView.findViewById(R.id.fr_main_grid);
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
				getActivity().startActivityForResult(mIntent, Constants.RequestCode.MAIN);
			}
		});
		
		errorView = (TextView) rootView.findViewById(R.id.error);
		retryButton = (Button) rootView.findViewById(R.id.retry);
		
		networkHelper = NetworkHelper.getInstance(getActivity());
		dataLoader = DataLoader.getInstance();
		
		getComicGrid();
		
//		new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
		
		return rootView;
	}

	@Override
	public void onPause() {
		Log.i("LIFE", "main create pause");
		super.onPause();
	}
	
	@Override
    public void onDestroy() {
		Log.i("LIFE", "main create destroy");
        super.onDestroy();
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i("LIFE", "main menu");
	    menu.getItem(Menu.FIRST).setIcon(R.drawable.ic_main_selected);
	    getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
	    getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	    getActivity().getActionBar().setDisplayShowCustomEnabled(false);
//	    View view = getActivity().getActionBar().getCustomView();
//	    searchView = (SearchView) view.findViewById(R.id.search_view);
//	    searchView.onActionViewCollapsed();
	    super.onCreateOptionsMenu(menu,inflater);
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
			/*
			try {
				Document mDoc = Jsoup.connect(Constants.BASE_URL)
						.data("page", query[0])
						.data("f_doujinshi",searchOption.getOption(0))
						.data("f_manga", 	searchOption.getOption(1))
						.data("f_artistcg", searchOption.getOption(2))
						.data("f_gamecg", 	searchOption.getOption(3))
						.data("f_western", 	searchOption.getOption(4))
						.data("f_non-h", 	searchOption.getOption(5))
						.data("f_imageset", searchOption.getOption(6))
						.data("f_cosplay", 	searchOption.getOption(7))
						.data("f_asianporn",searchOption.getOption(8))
						.data("f_misc", 	searchOption.getOption(9))
						.data("f_search", query[1])
						.data("f_apply", "Apply Filter").get();
				
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
			*/
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			task = null;
			
			dismissProgressDialog();
			
			// No result
			if(numOfTotalPages == 0) {
				mComicAdapter.notifyDataSetChanged();
				showToast("No Found");
				setActionBarTitle();
			}
			else {
				mComicAdapter.notifyDataSetChanged();
				setActionBarTitle(idxOfPage + 1, numOfTotalPages);
			}
		}
	}

	@Override
	protected void applyScrollListener() {
		// TODO Auto-generated method stub
		listView.setOnScrollListener(new OnScrollListener() {
			int mark = 0;
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, 
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				// 觸發兩次 15 10 25, 18 7 25
				if((totalItemCount-visibleItemCount) == firstVisibleItem
						&& totalItemCount > mark) {
					mark = totalItemCount;
					idxOfPage++;
					task = new ParserTask();
					task.execute(Integer.toString(idxOfPage), searchQuery);
//					new ParserTask().execute(Integer.toString(idxOfPage), searchQuery);
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}
			
		});
	}
}	