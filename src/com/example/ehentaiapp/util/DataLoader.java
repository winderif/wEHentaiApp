package com.example.ehentaiapp.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.example.ehentaiapp.Constants;
import com.example.ehentaiapp.Filter;

import android.net.Uri;
import android.util.Log;

public class DataLoader {

	public static DataLoader instance = null;
	
	public static DataLoader getInstance() {
		if(instance == null) {
			instance = new DataLoader();
		}
		return instance;
	}
	
	public JSONArray getGalleryList(String base, String index, String query, boolean[] filter) {
		String url = getGalleryListUrl(base, index, query, filter);
		Log.i("URL", index);
		JSONArray dataList = new JSONArray();
		
		try {
			Document mDoc = Jsoup.connect(url).get();
			
			Elements table = mDoc.getElementsByClass("itg"); 
			if(table.isEmpty()) {
				return null;
			}
			else {
				for (Element tr : table.select("tr")) {
					Elements td = tr.select("td");
					
					if (td.size() >= 4) {
						JSONObject data = new JSONObject();
						
						data.put("category", td.get(0).child(0).select("img").attr("alt"));

						// comic thumbnail
						if (td.get(2).getElementsByClass("it2").first().select("img").size() 
								> 0) {
							data.put("urlcover", td.get(2)
									.getElementsByClass("it2").first()
									.select("img").attr("abs:src"));
						} else {
							String[] token = td.get(2).text().split("~");
							data.put("urlcover", "http://" + token[1] + "/" + token[2]);
						}
						// comic url
						data.put("urlcomic", td.get(2)
								.getElementsByClass("it5").select("a")
								.attr("abs:href"));
						
						dataList.put(data);
					}
				}
				
				return dataList;
			}		
		} catch(IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getGalleryListUrl(String base, String index, String query, boolean[] filter) {
		Uri.Builder builder = Uri.parse(base).buildUpon();
		
		builder.appendQueryParameter("page", index);
		for(int i=0; i<Filter.KEY_OF_FILTER.length; i++) {
			builder.appendQueryParameter(Filter.KEY_OF_FILTER[i], Filter.getOption(filter[i]));
		}
		builder.appendQueryParameter(Filter.KEY_OF_SEARCH, query);
		builder.appendQueryParameter(Filter.KEY_OF_APPLY, "Apple Filter");
		
		return builder.toString();
	}
	
	public int getGalleryListCount(String base, String index, String query, boolean[] filter) {
		String url = getGalleryListUrl(base, index, query, filter);
		
		try {
			Document mDoc = Jsoup.connect(url).get();
			
			Elements pageBar = mDoc.getElementsByClass("ptt");
			Elements pageBarItem = pageBar.select("td");
			int numOfTotalPages = Integer.parseInt(pageBarItem.get(
					pageBarItem.size() - 2).text());
			
			return numOfTotalPages;
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
