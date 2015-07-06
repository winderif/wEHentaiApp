package com.example;

import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class MyDaoGenerator {
    private static final int DB_VERSION = 1;
    private static final String DB_PACKAGE = "com.example.winderif.ehentaiapp";
    private static final String DB_PATH = "./src-gen";

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(DB_VERSION, DB_PACKAGE);

        // Create Image
        Entity image = schema.addEntity("Image");

        image.addIdProperty().autoincrement();
        Property imagePage = image.addIntProperty("page").notNull().getProperty();
        image.addStringProperty("token").notNull();
        image.addStringProperty("filename");
        image.addIntProperty("width");
        image.addIntProperty("height");
        image.addStringProperty("src");
        image.addBooleanProperty("invalid");

        // Create Gallery
        Entity gallery = schema.addEntity("Gallery");
        gallery.setSuperclass("GalleryBase");

        gallery.addIdProperty();
        gallery.addStringProperty("token").notNull();
        gallery.addStringProperty("title");
        gallery.addStringProperty("subtitle");
        gallery.addIntProperty("category");
        gallery.addIntProperty("count");
        gallery.addStringProperty("thumbnail");
        gallery.addBooleanProperty("starred");
        gallery.addDateProperty("created");
        gallery.addDateProperty("lastRead");
        gallery.addStringProperty("uploader");
        gallery.addIntProperty("size");

        // One-to-Many (Gallery to Image)
        Property imageProperty = image.addLongProperty("galleryId").notNull().getProperty();
        ToMany galleryToImages = gallery.addToMany(image, imageProperty);
        galleryToImages.setName("images");
        galleryToImages.orderAsc(imagePage);

        // Create Tag
        Entity tag = schema.addEntity("Tag");

        tag.addIdProperty().autoincrement();
        tag.addStringProperty("name").notNull();
        tag.addIntProperty("count");
        tag.addDateProperty("lastRead");
        tag.addDateProperty("latest");
        tag.addIntProperty("latestCount");
        tag.addBooleanProperty("subscribed");

        // Create relation entity : Gallery to Tag
        Entity gallerysToTags = schema.addEntity("GallerysToTags");

        Property galleryProperty = gallerysToTags.addLongProperty("galleryId").notNull().getProperty();
        Property tagProperty = gallerysToTags.addLongProperty("tagId").notNull().getProperty();
        ToMany relationGallerys = gallery.addToMany(gallerysToTags, galleryProperty);
        relationGallerys.setName("tags");
        gallerysToTags.addToOne(tag, tagProperty);

        // Generate DAO
        File file = new File(DB_PATH);
        if (!file.exists()) file.mkdirs();

        new DaoGenerator().generateAll(schema, DB_PATH);
    }
}
