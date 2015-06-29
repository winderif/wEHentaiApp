package com.example.ehentaiapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ComicScrollView extends ScrollView {
	private ScrollViewListener scrollViewListener = null;
	private boolean isLoading = false;

	public interface ScrollViewListener {
	    public void onLoading(ComicScrollView scrollView);
	}
	
    public ComicScrollView(Context context) {
        super(context);
    }

    public ComicScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ComicScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }
    
    public void LoadingComplete() {
    	isLoading = true;
    }
    
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        /**
         * computeVerticalScrollRange(): the height of ScrollView
         * getHeight(): the height of screen
         * getScrollY(): the height of scroll Y-direction
         */
        
        if(scrollViewListener != null
        		&& getHeight() + getScrollY() >= computeVerticalScrollRange()
        		&& isLoading) {
        	isLoading = false;
            scrollViewListener.onLoading(this);
        }
    }
}
