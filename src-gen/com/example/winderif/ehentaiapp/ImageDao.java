package com.example.winderif.ehentaiapp;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.example.winderif.ehentaiapp.Image;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table IMAGE.
*/
public class ImageDao extends AbstractDao<Image, Long> {

    public static final String TABLENAME = "IMAGE";

    /**
     * Properties of entity Image.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Page = new Property(1, int.class, "page", false, "PAGE");
        public final static Property Token = new Property(2, String.class, "token", false, "TOKEN");
        public final static Property Filename = new Property(3, String.class, "filename", false, "FILENAME");
        public final static Property Width = new Property(4, Integer.class, "width", false, "WIDTH");
        public final static Property Height = new Property(5, Integer.class, "height", false, "HEIGHT");
        public final static Property Src = new Property(6, String.class, "src", false, "SRC");
        public final static Property Invalid = new Property(7, Boolean.class, "invalid", false, "INVALID");
        public final static Property GalleryId = new Property(8, long.class, "galleryId", false, "GALLERY_ID");
    };

    private Query<Image> gallery_ImagesQuery;

    public ImageDao(DaoConfig config) {
        super(config);
    }
    
    public ImageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'IMAGE' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'PAGE' INTEGER NOT NULL ," + // 1: page
                "'TOKEN' TEXT NOT NULL ," + // 2: token
                "'FILENAME' TEXT," + // 3: filename
                "'WIDTH' INTEGER," + // 4: width
                "'HEIGHT' INTEGER," + // 5: height
                "'SRC' TEXT," + // 6: src
                "'INVALID' INTEGER," + // 7: invalid
                "'GALLERY_ID' INTEGER NOT NULL );"); // 8: galleryId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'IMAGE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Image entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPage());
        stmt.bindString(3, entity.getToken());
 
        String filename = entity.getFilename();
        if (filename != null) {
            stmt.bindString(4, filename);
        }
 
        Integer width = entity.getWidth();
        if (width != null) {
            stmt.bindLong(5, width);
        }
 
        Integer height = entity.getHeight();
        if (height != null) {
            stmt.bindLong(6, height);
        }
 
        String src = entity.getSrc();
        if (src != null) {
            stmt.bindString(7, src);
        }
 
        Boolean invalid = entity.getInvalid();
        if (invalid != null) {
            stmt.bindLong(8, invalid ? 1l: 0l);
        }
        stmt.bindLong(9, entity.getGalleryId());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Image readEntity(Cursor cursor, int offset) {
        Image entity = new Image( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // page
            cursor.getString(offset + 2), // token
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // filename
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // width
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // height
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // src
            cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0, // invalid
            cursor.getLong(offset + 8) // galleryId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Image entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPage(cursor.getInt(offset + 1));
        entity.setToken(cursor.getString(offset + 2));
        entity.setFilename(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setWidth(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setHeight(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setSrc(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setInvalid(cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0);
        entity.setGalleryId(cursor.getLong(offset + 8));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Image entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Image entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "images" to-many relationship of Gallery. */
    public List<Image> _queryGallery_Images(long galleryId) {
        synchronized (this) {
            if (gallery_ImagesQuery == null) {
                QueryBuilder<Image> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.GalleryId.eq(null));
                queryBuilder.orderRaw("PAGE ASC");
                gallery_ImagesQuery = queryBuilder.build();
            }
        }
        Query<Image> query = gallery_ImagesQuery.forCurrentThread();
        query.setParameter(0, galleryId);
        return query.list();
    }

}