/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.astuetz.viewpager.extensions.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;

public class MainActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {
    private static final String  TAG     = "MainActivity";
    private final        Handler handler = new Handler();
    private PagerSlidingTabStrip tabs;
    private ViewPager            pager;
    private MyPagerAdapter       adapter;
    public  Toolbar              toolbar;
    public  View                 toolbarContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarContainer = findViewById(R.id.toolbar_container);
        setSupportActionBar(toolbar);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_contact:
                QuickContactFragment dialog = new QuickContactFragment();
                dialog.show(getSupportFragmentManager(), "QuickContactFragment");
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(final int i, final float v, final int i2) {
    }

    @Override
    public void onPageSelected(final int i) {
    }

    @Override
    public void onPageScrollStateChanged(final int i) {
    }

    public int getToolBarContainerHeight() {
        if (null != toolbarContainer) {
            Log.v(TAG, "toolbar container size: "+  Utils.getThemeDimension(this, R.attr.actionBarSize) + " + " + Utils
                .getThemeDimension(this,
                                                                                                             R.attr.actionBarTabSize));
            return Utils.getThemeDimension(this, R.attr.actionBarSize) + Utils.getThemeDimension(this, R.attr.actionBarTabSize);
        } else {
            return Utils.getThemeDimension(this, R.attr.actionBarTabSize);
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = {
            "Categories", "Home", "Top Paid", "Top Free", "Top Grossing", "Top New Paid", "Top New Free", "Trending"
        };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return SuperAwesomeCardFragment.newInstance(position);
        }

    }

}