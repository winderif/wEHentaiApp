package com.example.ehentaiapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.winderif.ehentaiapp.DaoMaster;

public class EhentaiDBHelper extends SQLiteOpenHelper {
    private static EhentaiDBHelper instance = null;

    public static final String DATABASE_NAME = "ehentai_data.db";
    public static final int VERSION = 4;
    private static SQLiteDatabase mDatabase;

    public EhentaiDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

	public EhentaiDBHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
	}

    public static EhentaiDBHelper getInstance(Context context) {
        if(instance == null) {
            instance = new EhentaiDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    public SQLiteDatabase getDatabase() {
        if (mDatabase == null || !mDatabase.isOpen()) {
            mDatabase = instance.getWritableDatabase();
        }
 
        return mDatabase;
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        DaoMaster.createAllTables(db, false);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DaoMaster.dropAllTables(db, true);
        onCreate(db);
    }
}
