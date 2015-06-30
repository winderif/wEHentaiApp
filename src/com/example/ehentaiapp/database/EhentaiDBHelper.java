package com.example.ehentaiapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class EhentaiDBHelper extends SQLiteOpenHelper {
	// 資料庫名稱
    public static final String DATABASE_NAME = "ehentai_data.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 2;    
    // 資料庫物件，固定的欄位變數
    private static SQLiteDatabase mDatabase;
	
	public EhentaiDBHelper(Context context, String name, CursorFactory factory,
            int version) {
		// TODO Auto-generated constructor stub
        super(context, name, factory, version);
	}

    public static SQLiteDatabase getDatabase(Context context) {
        if (mDatabase == null || !mDatabase.isOpen()) {
        	mDatabase = new EhentaiDBHelper(
        			context, DATABASE_NAME, null, VERSION).getWritableDatabase();
        }
 
        return mDatabase;
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(ItemDAO.CREATE_TABLE);
    	db.execSQL(TagDAO.CREATE_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	db.execSQL("DROP TABLE IF EXISTS " + ItemDAO.TABLE_NAME);
    	db.execSQL("DROP TABLE IF EXISTS " + TagDAO.TABLE_NAME);
    	
        onCreate(db);
    }
}
