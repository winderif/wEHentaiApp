package com.example.ehentaiapp.database;

import com.example.ehentaiapp.Filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by winderif on 2015/7/6.
 */
public abstract class GalleryBase {
    protected abstract Long getId();
    protected abstract String getToken();
    protected abstract Integer getCategory();
    protected abstract void setCategory(Integer category);
    protected abstract String getTitle();
    protected abstract String getSubtitle();
    protected abstract void setCreated(java.util.Date created);

    public String getCategoryText() {
        return Filter.getOptionText(getCategory());
    }

    public void setCategory(String category) {
        setCategory(Filter.MAP_FILTER.get(category));
    }

    public void setCreated(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            setCreated(dateFormat.parse(date));
        } catch(ParseException e) {
            e.printStackTrace();
        }
    }
}
