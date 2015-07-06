package com.example.ehentaiapp.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.ehentaiapp.Filter;
import com.example.ehentaiapp.database.EhentaiDBHelper;
import com.example.winderif.ehentaiapp.DaoMaster;
import com.example.winderif.ehentaiapp.DaoSession;
import com.example.winderif.ehentaiapp.Gallery;
import com.example.winderif.ehentaiapp.GalleryDao;
import com.example.winderif.ehentaiapp.GallerysToTags;
import com.example.winderif.ehentaiapp.GallerysToTagsDao;
import com.example.winderif.ehentaiapp.ImageDao;
import com.example.winderif.ehentaiapp.Tag;
import com.example.winderif.ehentaiapp.TagDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataLoader {

	public static DataLoader instance = null;

	private Context context;

	private ImageDao imageDao;
	private GalleryDao galleryDao;
	private TagDao tagDao;
	private GallerysToTagsDao gallerysToTagsDao;

	private static final Pattern gUrlPattern =
			Pattern.compile("http://g\\.e-hentai\\.org/g/(\\d+)/(\\w+)/");

	public DataLoader(Context context) {
		this.context = context;

		setDatabase();
	}

	public static DataLoader getInstance(Context context) {
		if(instance == null) {
			instance = new DataLoader(context.getApplicationContext());
		}
		return instance;
	}

	private void setDatabase() {
//		EhentaiDBHelper helper = EhentaiDBHelper.getInstance(context);
		SQLiteDatabase db = EhentaiDBHelper.getInstance(context).getDatabase(context);
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		imageDao = daoSession.getImageDao();
		galleryDao = daoSession.getGalleryDao();
		tagDao = daoSession.getTagDao();
		gallerysToTagsDao = daoSession.getGallerysToTagsDao();
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
			e.printStackTrace();
		} catch (JSONException e) {
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
			e.printStackTrace();
		}
		return -1;
	}

	public Gallery getGallery(String url) {
		Gallery mGallery = new Gallery();

		try {
			Document mBookDoc = Jsoup.connect(url).get();

			Matcher matcher = gUrlPattern.matcher(url);
			JSONObject data = new JSONObject();

			matcher.find();
			data.put("id", matcher.group(1));
			data.put("token", matcher.group(2));

			Element mBookTable = mBookDoc.getElementById("gd1");
			if(mBookTable == null) {
				// TODO no gallery
				return null;
			}
			else {
				data.put("thumbnail", mBookTable.select("img").attr("abs:src"));
				Element mBookTitle = mBookDoc.getElementById("gd2");
				data.put("title", mBookTitle.child(0).text());
				data.put("subtitle", mBookTitle.child(1).text());

				Element mBookInfo = mBookDoc.getElementById("gdd");
				data.put("date", mBookInfo.select("tr").first().child(1).text());
				data.put("category", mBookDoc.getElementById("gdc").select("img").attr("alt"));
				data.put("size", mBookInfo.select("tr").get(5).text().split(" ")[1]);

				Element mUpload = mBookDoc.getElementById("gdn");
				data.put("uploader", mUpload.text());

				JSONArray tags = new JSONArray();
				Element mBookTag = mBookDoc.getElementById("taglist");
				for(Element tag : mBookTag.select("tr")) {
					for(Element child : tag.select("td").get(1).children()) {
						tags.put(child.text());
					}
				}

				setGallerysToTags(data.getLong("id"), tags);

				mGallery.setId(data.getLong("id"));
				mGallery.setToken(data.getString("token"));
				mGallery.setTitle(data.getString("title"));
				mGallery.setSubtitle(data.getString("subtitle"));
				mGallery.setThumbnail(data.getString("thumbnail"));
				mGallery.setCategory(data.getString("category"));
				mGallery.setSize(data.getInt("size"));
				mGallery.setCreated(data.getString("date"));
				mGallery.setCount(0);
				mGallery.setLastRead(new Date());
				mGallery.setStarred(false);
				mGallery.setUploader("uploader");

				galleryDao.insertInTx(mGallery);

				return mGallery;
			}
		} catch(IOException e) {
			e.printStackTrace();
		} catch(JSONException ej) {
			ej.printStackTrace();
		}
		return null;
	}

	private void setGallerysToTags(long galleryId, JSONArray tags) {
		try {
			for(int i=0; i<tags.length(); i++) {
				Tag mTag = new Tag();
				mTag.setName(tags.getString(i));
				tagDao.insertInTx(mTag);

				GallerysToTags relation = new GallerysToTags();
				relation.setGalleryId(galleryId);
				relation.setTagId(tagDao.getKey(mTag));

				gallerysToTagsDao.insertInTx(relation);
			}
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
}
