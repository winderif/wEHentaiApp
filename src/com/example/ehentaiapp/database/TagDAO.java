package com.example.ehentaiapp.database;

import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class TagDAO {
	private Context context;
	
	public static final String TABLE_NAME = "item";
	
	public static final String KEY_ID = "_id";
	
	public static final String TAG_COLUMN = "tag";
	public static final String LASTREAD_COLUMN = "lastread";
	public static final String LATESTPOST_COLUMN = "latestpost";
	public static final String LATESTCOUNT_COLUMN = "latestcount";
	public static final String SUBSCRIBED_COLUMN = "subscribed";
	public static final String HASNEW_COLUMN = "hasnew";
	
	public static final String CREATE_TABLE = 
            "CREATE TABLE " + TABLE_NAME + " (" + 
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TAG_COLUMN + " TEXT NOT NULL, " +
            LASTREAD_COLUMN + " INTEGER NOT NULL, " +
            LATESTPOST_COLUMN + " INTEGER NOT NULL, " +
            LATESTCOUNT_COLUMN + " INTEGER NOT NULL, " +
            SUBSCRIBED_COLUMN + " BOOLEAN DEFAULT FALSE, " +
            HASNEW_COLUMN + " BOOLEAN DEFAULT FALSE" + ")";

	private SQLiteDatabase db;
	
	public TagDAO(Context context) {
		this.context = context;
	}
	
	public void open() {
		db = EhentaiDBHelper.getDatabase(context);
	}
	
	public void close() {
        db.close();
    }

	public Tag insert(Tag tag) {
        ContentValues cv = new ContentValues();     
 
        cv.put(TAG_COLUMN, tag.getTag());
        cv.put(LASTREAD_COLUMN, tag.getLastRead().getTime());
        cv.put(LATESTPOST_COLUMN, tag.getLatestPost().getTime());
        cv.put(LATESTCOUNT_COLUMN, tag.getLatestCount());
        cv.put(SUBSCRIBED_COLUMN, tag.isSubscribed());
        cv.put(HASNEW_COLUMN, tag.isHasNew());
 
        long id = db.insert(TABLE_NAME, null, cv);
 
        tag.setId(id);
        
        return tag;
    }
	
	public boolean update(Tag tag) {
		ContentValues cv = new ContentValues();     
		 
        cv.put(TAG_COLUMN, tag.getTag());
        cv.put(LASTREAD_COLUMN, tag.getLastRead().getTime());
        cv.put(LATESTPOST_COLUMN, tag.getLatestPost().getTime());
        cv.put(LATESTCOUNT_COLUMN, tag.getLatestCount());
        cv.put(SUBSCRIBED_COLUMN, tag.isSubscribed());
        cv.put(HASNEW_COLUMN, tag.isHasNew());
 
        String where = KEY_ID + "=" + tag.getId();
        
        return db.update(TABLE_NAME, cv, where, null) > 0;
	}
	
	public boolean delete(long id){
        String where = KEY_ID + "=" + id;
        
        return db.delete(TABLE_NAME, where , null) > 0;
    }
	
	public boolean delete(Tag tag){
		String where = KEY_ID + "=" + tag.getId();
		
        return db.delete(TABLE_NAME, where , null) > 0;
    }
}
