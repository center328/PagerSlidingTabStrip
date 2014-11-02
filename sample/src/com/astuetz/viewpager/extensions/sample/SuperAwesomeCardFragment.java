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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.nineoldandroids.animation.ObjectAnimator;

public class SuperAwesomeCardFragment extends ListFragment implements PixelScrollDetector.PixelScrollListener {
    private static final String ARG_POSITION = "position";
    private int     position;
    private View    toolbarContainer;
    private Toolbar toolbar;
    private final Handler handler = new Handler();

    public static SuperAwesomeCardFragment newInstance(int position) {
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbarContainer = ((MainActivity) getActivity()).toolbarContainer;
        toolbar = ((MainActivity) getActivity()).toolbar;

        String[] objects = new String[101];

        for (int i = 0; i < 101; i++) {
            objects[i] = "ITEM " + (i - 1);
        }
        MyAdapter adapter = new MyAdapter(getActivity(), android.R.layout.simple_list_item_1, objects);
        setListAdapter(adapter);
        setListShown(true);

        getListView().setOnScrollListener(new PixelScrollDetector(this));
        getListView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                return false;
            }
        });

    }

    @Override
    public void onScroll(final AbsListView view, final float deltaY) {
        int height = toolbar.getHeight();
        float translationY = ViewCompat.getTranslationY(toolbarContainer) + deltaY;
        translationY = Math.max(Math.min(translationY, 0), -height);
        ViewCompat.setTranslationY(toolbarContainer, translationY);
    }

    @Override
    public void onScrollStateChanged(final int scrollState) {
        if (scrollState == 0) {
            final View toolbarContainer = ((MainActivity) getActivity()).toolbarContainer;
            final View toolbar = ((MainActivity) getActivity()).toolbar;
            int height = toolbar.getHeight();

            float translationY = ViewCompat.getTranslationY(toolbarContainer);

            if (translationY > -height) {
                ObjectAnimator.ofFloat(toolbarContainer, "translationY", 0).setDuration(100).start();
            }
        }
    }

    private class MyAdapter extends ArrayAdapter<String> {
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getItemViewType(final int position) {
            return position == 0 ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        public MyAdapter(final Context context, final int resource, final String[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if (position == 0) {
                view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                  toolbarContainer.getHeight()));
            }
            return view;
        }
    }

}