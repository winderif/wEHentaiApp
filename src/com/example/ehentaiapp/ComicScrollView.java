package com.example.ehentaiapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;

public class ComicScrollView extends ScrollView {

    private ListAdapter adapter;
    private DataSetObserver dataSetObserver;
    private LinearLayout layout;

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

    public ListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ListAdapter adapter) {
        if(adapter == null) {
            return ;
        }

        if(dataSetObserver == null) {
            dataSetObserver = new AdapterObserver();
        }

        if (layout == null) {
            layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);

            // ScrollView only has one child
            if(this.getChildCount() > 1) {
                this.removeAllViews();
            }

            this.addView(layout);
        }

        if(this.adapter != null) {
            this.adapter.unregisterDataSetObserver(dataSetObserver);
            layout.removeAllViews();
        }

        if(adapter != null) {
            this.adapter = adapter;
            this.adapter.registerDataSetObserver(dataSetObserver);
            setViewFromAdapter();
        }
    }

    private void setViewFromAdapter() {
        for(int i=0; i<adapter.getCount(); i++) {
            final View view = adapter.getView(i, null, this);
            layout.addView(view);
        }
    }

    public class AdapterObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            layout.removeAllViews();
            setViewFromAdapter();
        }

        @Override
        public void onInvalidated() {
            layout.removeAllViews();
        }
    }
}
