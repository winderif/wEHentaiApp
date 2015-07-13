package com.example.ehentaiapp.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.ehentaiapp.Constants;
import com.example.ehentaiapp.Filter;
import com.example.ehentaiapp.database.EhentaiDBHelper;
import com.example.winderif.ehentaiapp.DaoMaster;
import com.example.winderif.ehentaiapp.DaoSession;
import com.example.winderif.ehentaiapp.Gallery;
import com.example.winderif.ehentaiapp.GalleryDao;
import com.example.winderif.ehentaiapp.GallerysToTags;
import com.example.winderif.ehentaiapp.GallerysToTagsDao;
import com.example.winderif.ehentaiapp.Image;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.dao.query.QueryBuilder;

public class DataLoader {

	private static final String LOG_MATCHER = "MATCHER";

	public static DataLoader instance = null;

	private Context context;

	private ImageDao imageDao;
	private GalleryDao galleryDao;
	private TagDao tagDao;
	private GallerysToTagsDao gallerysToTagsDao;

	private static final Pattern gUrlPattern =
			Pattern.compile("http://g\\.e-hentai\\.org/g/(\\d+)/(\\w+)/");

	private static final Pattern iUrlPattern =
			Pattern.compile("http://g\\.e-hentai\\.org/s/(\\w+)/(\\d+)-(\\d+)");

	private static final Pattern imageInfo =
			Pattern.compile("^(\\w+\\.\\w+)\\s::\\s(\\d+)\\sx\\s(\\d+)\\s::\\s.+");

	private static final int NUM_PER_PAGE = 40;

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
		EhentaiDBHelper helper = EhentaiDBHelper.getInstance(context);
		SQLiteDatabase db = helper.getDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		imageDao = daoSession.getImageDao();
		galleryDao = daoSession.getGalleryDao();
		tagDao = daoSession.getTagDao();
		gallerysToTagsDao = daoSession.getGallerysToTagsDao();
	}
	
	public JSONArray getGalleryList(String url) {

		Log.i("URL", url);

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

				Elements pageBar = mDoc.getElementsByClass("ptt");
				Elements pageBarItem = pageBar.select("td");
				int numOfTotalPages = Integer.parseInt(pageBarItem.get(
						pageBarItem.size() - 2).text());

				JSONObject data = new JSONObject();
				data.put("pages", numOfTotalPages);
				dataList.put(data);

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
	
	public JSONArray getGalleryList(String base, String index, String query, boolean[] filter) {
		Uri.Builder builder = Uri.parse(base).buildUpon();

		Log.i("URL", index);

		builder.appendQueryParameter("page", index);
		for(int i=0; i<Filter.KEY_OF_FILTER.length; i++) {
			builder.appendQueryParameter(Filter.KEY_OF_FILTER[i], Filter.getOption(filter[i]));
		}
		builder.appendQueryParameter(Filter.KEY_OF_SEARCH, query);
		builder.appendQueryParameter(Filter.KEY_OF_APPLY, "Apple Filter");
		
		return getGalleryList(builder.toString());
	}

	public JSONArray getGalleryList(String base, String index, String tagName) {
		Uri.Builder builder = Uri.parse(base).buildUpon();

		builder.appendPath(tagName);
		builder.appendPath(index);

		return getGalleryList(builder.toString());
	}

	public Gallery getGallery(String url) {

		Log.i("URL", url);

		Gallery mGallery = new Gallery();

		try {
			Document mBookDoc = Jsoup.connect(url).get();

			Matcher matcher = gUrlPattern.matcher(url);
			JSONObject data = new JSONObject();

			if(matcher.find()) {
				data.put("id", matcher.group(1));
				data.put("token", matcher.group(2));
			}
			else {
				Log.e(LOG_MATCHER, "Gallery url not match");

				return null;
			}


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
				mGallery.setCount(1);
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

	public Gallery getGallery(String base, long id, String token) {
		return getGallery(getGalleryUrl(base, id, token));
	}

	public String getGalleryUrl(String base, long id, String token) {
		Uri.Builder builder = Uri.parse(base).buildUpon();

		builder.appendPath(Long.toString(id));
		builder.appendPath(token);
		builder.appendPath("");

		return builder.toString();
	}

	public String getGalleryUrl(Gallery gallery) {
		return getGalleryUrl(Constants.GALLERY_URL, gallery.getId(), gallery.getToken());
	}

	private void setGallerysToTags(long galleryId, JSONArray tags) {
		try {
			for(int i=0; i<tags.length(); i++) {
				QueryBuilder query = tagDao.queryBuilder();
				query.where(TagDao.Properties.Name.eq(tags.getString(i)));

				Tag mTag;
				if(query.list().isEmpty()) {
					mTag = new Tag();
					mTag.setName(tags.getString(i));
					mTag.setCount(1);
					mTag.setLastRead(new Date());
					mTag.setLatest(new Date());
					mTag.setLatestCount(0);
					mTag.setSubscribed(false);

					tagDao.insertInTx(mTag);
				}
				else {
					mTag = (Tag) query.list().get(0);
				}

				GallerysToTags relation = new GallerysToTags();
				relation.setGalleryId(galleryId);
				relation.setTagId(tagDao.getKey(mTag));

				gallerysToTagsDao.insertInTx(relation);
			}
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}

	public void updateTagSubscribe() {
		QueryBuilder query = tagDao.queryBuilder();
		query.where(TagDao.Properties.Subscribed.eq(true));
		List<Tag> tags = query.list();

		for(Tag tag : tags) {
			tag = updateTagSubscribe(tag);
			tagDao.update(tag);
		}
	}

	public Tag updateTagSubscribe(Tag oldTag) {
		try {
			Document mDoc = Jsoup.connect(getTagUrl(oldTag.getName())).get();

			Elements table = mDoc.getElementsByClass("itg");

			if(table.isEmpty()) {
				return oldTag;
			}
			else {
				int index = 0;
				boolean first = true;

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date oldDate;
				int latestCount;

				if(oldTag.getLatestCount() > 0) {
					oldDate = oldTag.getLatest();
					latestCount = oldTag.getLatestCount();
				}
				else {
					oldDate = oldTag.getLastRead();
					latestCount = 0;
				}

				for (Element tr : table.select("tr")) {
					Elements td = tr.select("td");

					if(index > 5) {
						break;
					}

					if (td.size() >= 4) {
						Log.i("TAGCOMPARE", oldTag.getName() +
								" " + oldDate + " " + td.get(1).text());

						index++;

						Date gDate = dateFormat.parse(td.get(1).text());

						if(oldDate.compareTo(gDate) < 0) {
							latestCount++;

							if(first) {
								oldTag.setLatest(gDate);
								first = false;
							}
						}
						else {
							break;
						}
					}
				}

				oldTag.setLatestCount(latestCount);

				return oldTag;
			}
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ParseException ep) {
			ep.printStackTrace();
		}
		return null;
	}

	public String getTagUrl(String tagName) {
		Uri.Builder builder = Uri.parse(Constants.TAG_URL).buildUpon();
		builder.appendPath(tagName);
		return builder.toString();
	}

	public List<Image> getImageList(Gallery gallery) {

		List<Image> images = gallery.getImages();

		if(images != null && !images.isEmpty()) {
			Log.i("URL", "has images id: " +
					gallery.getId() + " size: " +
					gallery.getImages().size());

			return images;
		}

		try {
			JSONArray dataList = new JSONArray();

			int numOfUrlPage = gallery.getSize() / NUM_PER_PAGE
					+ ((gallery.getSize() % NUM_PER_PAGE == 0) ? 0 : 1);

			for(int i=0; i<numOfUrlPage; i++) {
				Document mBookDoc = Jsoup.connect(getGalleryPageUrl(gallery, i)).get();

				Element mBookTable = mBookDoc.getElementById("gdt");
				Elements mBookPage = mBookTable.getElementsByClass("gdtm");

				// Get url of each comic page
				for(Element page : mBookPage) {
					JSONObject data = new JSONObject();

					Log.i("URL", "getImageList " + page.select("a").attr("abs:href"));

					Matcher matcher = iUrlPattern.matcher(page.select("a").attr("abs:href"));

					if(matcher.find()) {
						data.put("token", matcher.group(1));
						data.put("page", matcher.group(3));
					}
					else {
						Log.e(LOG_MATCHER, "Image url not match");

						return null;
					}

					dataList.put(data);
				}
			}

			List<Image> imageList = new ArrayList<Image>();

			for(int i=0; i<dataList.length(); i++) {
				Image image = new Image();
				image.setGalleryId(gallery.getId());
				image.setToken(dataList.getJSONObject(i).getString("token"));
				image.setPage(dataList.getJSONObject(i).getInt("page"));

				imageDao.insert(image);

				imageList.add(image);
			}

			return imageList;

		} catch(IOException e) {
			e.printStackTrace();
		} catch(JSONException ej) {
			ej.printStackTrace();
		}
		return null;
	}

	private String getGalleryPageUrl(Gallery gallery, int page) {
		Uri.Builder builder = Uri.parse(getGalleryUrl(gallery)).buildUpon();
		builder.appendQueryParameter("p", Integer.toString(page));

		return builder.toString();
	}


	public Image getImageInfo(long galleryId, int page, boolean retry) {
		QueryBuilder query = imageDao.queryBuilder();
		query.where(query.and(
				ImageDao.Properties.GalleryId.eq(galleryId),
				ImageDao.Properties.Page.eq(page)
		));

		Image image = (Image) query.list().get(0);

		if(image.getSrc() != null && !image.getSrc().isEmpty() && !retry) {
			Log.i("URL", "has image id: " + galleryId + " " + page);

			return image;
		}
		else {
			return getImageInfo(galleryId, image, page);
		}
	}

	public Image getImageInfo(Gallery gallery, int page, boolean retry) {
		return getImageInfo(gallery.getId(), page, retry);
	}

	public Image getImageInfo(long galleryId, Image image, int page) {

		String url = getImageUrl(image.getToken(), galleryId, page);

		Log.i("URL", "getImageInfo " + url);

		try {
			JSONObject data = new JSONObject();

			Document pageImg = Jsoup.connect(url).get();
			Element pageImgSrc = pageImg.getElementById("i3");
			data.put("src", pageImgSrc.select("img").attr("abs:src"));

			Element pageImgInfo = pageImg.getElementById("i2");
			Matcher matcher = imageInfo.matcher(pageImgInfo.child(1).text());

			if(matcher.find()) {
				data.put("filename", matcher.group(1));
				data.put("width", matcher.group(2));
				data.put("height", matcher.group(3));
			}
			else {
				Log.e(LOG_MATCHER, "Image info not match");

				return null;
			}

			image.setSrc(data.getString("src"));
			image.setFilename(data.getString("filename"));
			image.setWidth(data.getInt("width"));
			image.setHeight(data.getInt("height"));
			imageDao.update(image);

			return image;

		} catch(IOException e) {
			e.printStackTrace();
		} catch(JSONException ej) {
			ej.printStackTrace();
		}
		return null;
	}

	public Image getImageInfo(Image image) {
		return getImageInfo(image.getGalleryId(), image, image.getPage());
	}

	private String getImageUrl(String token, long galleryId, int page) {
		Uri.Builder builder = Uri.parse(Constants.IMAGE_URL).buildUpon();
		builder.appendPath(token);
		builder.appendPath(Long.toString(galleryId) + "-" + Integer.toString(page));

		return builder.toString();
	}
}
