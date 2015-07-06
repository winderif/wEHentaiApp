package com.example.winderif.ehentaiapp;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.example.winderif.ehentaiapp.Image;
import com.example.winderif.ehentaiapp.Gallery;
import com.example.winderif.ehentaiapp.Tag;
import com.example.winderif.ehentaiapp.GallerysToTags;

import com.example.winderif.ehentaiapp.ImageDao;
import com.example.winderif.ehentaiapp.GalleryDao;
import com.example.winderif.ehentaiapp.TagDao;
import com.example.winderif.ehentaiapp.GallerysToTagsDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig imageDaoConfig;
    private final DaoConfig galleryDaoConfig;
    private final DaoConfig tagDaoConfig;
    private final DaoConfig gallerysToTagsDaoConfig;

    private final ImageDao imageDao;
    private final GalleryDao galleryDao;
    private final TagDao tagDao;
    private final GallerysToTagsDao gallerysToTagsDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        imageDaoConfig = daoConfigMap.get(ImageDao.class).clone();
        imageDaoConfig.initIdentityScope(type);

        galleryDaoConfig = daoConfigMap.get(GalleryDao.class).clone();
        galleryDaoConfig.initIdentityScope(type);

        tagDaoConfig = daoConfigMap.get(TagDao.class).clone();
        tagDaoConfig.initIdentityScope(type);

        gallerysToTagsDaoConfig = daoConfigMap.get(GallerysToTagsDao.class).clone();
        gallerysToTagsDaoConfig.initIdentityScope(type);

        imageDao = new ImageDao(imageDaoConfig, this);
        galleryDao = new GalleryDao(galleryDaoConfig, this);
        tagDao = new TagDao(tagDaoConfig, this);
        gallerysToTagsDao = new GallerysToTagsDao(gallerysToTagsDaoConfig, this);

        registerDao(Image.class, imageDao);
        registerDao(Gallery.class, galleryDao);
        registerDao(Tag.class, tagDao);
        registerDao(GallerysToTags.class, gallerysToTagsDao);
    }
    
    public void clear() {
        imageDaoConfig.getIdentityScope().clear();
        galleryDaoConfig.getIdentityScope().clear();
        tagDaoConfig.getIdentityScope().clear();
        gallerysToTagsDaoConfig.getIdentityScope().clear();
    }

    public ImageDao getImageDao() {
        return imageDao;
    }

    public GalleryDao getGalleryDao() {
        return galleryDao;
    }

    public TagDao getTagDao() {
        return tagDao;
    }

    public GallerysToTagsDao getGallerysToTagsDao() {
        return gallerysToTagsDao;
    }

}