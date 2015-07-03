package com.example.ehentaiapp.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.Context;

public class ItemDAO {
	private Context context;
	
	public static final String TABLE_NAME = "item";
	
	public static final String KEY_ID = "_id";
	
	public static final String DATETIME_COLUMN = "datetime";
	public static final String CATEFORY_COLUMN = "category";
    public static final String TITLE_COLUMN = "title";
    public static final String URL_OF_COMIC_COLUMN = "urlofbook";
    public static final String URL_OF_COVER_COLUMN = "urlofcover";
    public static final String NUM_OF_PAGES_COLUMN = "numofpages";
    public static final String IS_FAVORITE_COLUMN = "isfavorite";
	
    public static final String CREATE_TABLE = 
            "CREATE TABLE " + TABLE_NAME + " (" + 
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DATETIME_COLUMN + " INTEGER NOT NULL, " +
            CATEFORY_COLUMN + " TEXT NOT NULL, " +
            TITLE_COLUMN + " TEXT NOT NULL, " +
            URL_OF_COMIC_COLUMN + " TEXT NOT NULL, " +
            URL_OF_COVER_COLUMN + " TEXT NOT NULL, " +
            NUM_OF_PAGES_COLUMN + " INTEGER NOT NULL, " +
            IS_FAVORITE_COLUMN + " BOOLEAN DEFAULT FALSE" + ")";
    
    // 資料庫物件    
    private SQLiteDatabase db;
    
	public ItemDAO(Context context) {
		this.context = context;
	}
	
	public void open() {
		db = EhentaiDBHelper.getDatabase(context);
	}
	
	public void close() {
        db.close();
    }
	
	public Item insert(Item item) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();     
 
        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(DATETIME_COLUMN, item.getDatetime());
        cv.put(CATEFORY_COLUMN, item.getCategory());
        cv.put(TITLE_COLUMN, item.getTitle());
        cv.put(URL_OF_COMIC_COLUMN, item.getUrlOfComic());
        cv.put(URL_OF_COVER_COLUMN, item.getUrlOfComicCover());
        cv.put(NUM_OF_PAGES_COLUMN, item.getNumOfPages());
        cv.put(IS_FAVORITE_COLUMN, item.isFavorite());
 
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);
 
        // 設定編號
        item.setId(id);
        // 回傳結果
        return item;
    }

	public boolean update(Item item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();
        
        cv.put(DATETIME_COLUMN, item.getDatetime());
        cv.put(CATEFORY_COLUMN, item.getCategory());
        cv.put(TITLE_COLUMN, item.getTitle());
        cv.put(URL_OF_COMIC_COLUMN, item.getUrlOfComic());
        cv.put(URL_OF_COVER_COLUMN, item.getUrlOfComicCover());
        cv.put(NUM_OF_PAGES_COLUMN, item.getNumOfPages());
        cv.put(IS_FAVORITE_COLUMN, item.isFavorite());
        
        String where = KEY_ID + "=" + item.getId();
        
        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0; 
	}
	
	public boolean delete(long id){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where , null) > 0;
    }
	
	public boolean delete(Item item){
		String where = KEY_ID + "=" + item.getId();
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where , null) > 0;
    }
	
	public List<Item> getAll() {
        List<Item> result = new ArrayList<Item>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);
 
        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }
 
        cursor.close();
        return result;
    }
	
	public List<String> getAllUrlOfComic(String where) {
		List<String> result = new ArrayList<String>();
        Cursor cursor = db.query(
                TABLE_NAME, 
                new String[]{URL_OF_COMIC_COLUMN}, where, null, null, null, null, null);
 
        while (cursor.moveToNext()) {
            result.add(cursor.getString(0));
        }
 
        cursor.close();
        return result;
    }
	
	public List<String> getAllUrlOfComicCover(String where) {
		List<String> result = new ArrayList<String>();
        Cursor cursor = db.query(
                TABLE_NAME, 
                new String[]{URL_OF_COVER_COLUMN}, where, null, null, null, null, null);
 
        while (cursor.moveToNext()) {
            result.add(cursor.getString(0));
        }
 
        cursor.close();
        return result;
    }
	
	public List<String> getAllCategory(String where) {
        List<String> result = new ArrayList<String>();
        Cursor cursor = db.query(
                TABLE_NAME, 
                new String[]{CATEFORY_COLUMN}, where, null, null, null, null, null);
 
        while (cursor.moveToNext()) {
            result.add(cursor.getString(0));
        }
 
        cursor.close();
        return result;
    }
	
	public Item get(long id) {
        // 準備回傳結果用的物件
        Item item = null;
        // 使用編號為查詢條件
        String where = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);
 
        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecord(result);
        }
 
        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return item;
    }
	
	public Item get(String urlOfComic) {
        Item item = null;
        String where = URL_OF_COMIC_COLUMN + "=\"" + urlOfComic + "\"";
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);
 
        if (result.moveToFirst()) {
            item = getRecord(result);
        }
 
        result.close();
        return item;
	}
	
	public Item getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Item result = new Item();
 
        result.setId(cursor.getLong(0));
        result.setDatetime(cursor.getString(1));
        result.setCategory(cursor.getString(2));
        result.setTitle(cursor.getString(3));
        result.setUrlOfComic(cursor.getString(4));
        result.setUrlOfComicCover(cursor.getString(5));
        result.setNumOfPages(cursor.getInt(6));
        result.setFavorite(cursor.getInt(7) > 0);
 
        // 回傳結果
        return result;
    }
	
	public boolean isExist(String url) {
		String where = URL_OF_COMIC_COLUMN + "=\"" + url + "\"";
		Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);
		
		boolean exist = (result.getCount() > 0);
		result.close();
		return exist;
	}
	
	public boolean isFavorite(String url) {
		String where = URL_OF_COMIC_COLUMN + "=\"" + url + "\"";
		Cursor result = db.query(
                TABLE_NAME, 
                new String[]{IS_FAVORITE_COLUMN}, where, null, null, null, null, null);
		
		boolean favorite = false;
		if(result.moveToFirst()) {
			favorite = result.getInt(0) > 0;
		}
		
		result.close();
		return favorite;
	}
}
