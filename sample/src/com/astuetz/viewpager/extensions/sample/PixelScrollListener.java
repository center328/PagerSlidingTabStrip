package com.astuetz.viewpager.extensions.sample;

import android.widget.AbsListView;

/**
* Created by alessandro on 02/11/14.
*/
public interface PixelScrollListener {
    public void onScroll(AbsListView view, float deltaY);

    void onScrollStateChanged(int scrollState);
}
