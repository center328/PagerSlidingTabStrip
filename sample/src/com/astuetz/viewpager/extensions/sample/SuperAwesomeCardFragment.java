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
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

import de.greenrobot.event.EventBus;

public class SuperAwesomeCardFragment extends ListFragment implements PixelScrollDetector.PixelScrollListener {
    private static final String ARG_POSITION = "position";
    private static final String TAG          = "SuperAwesomeCardFragment";
    private View    toolbarContainer;
    private Toolbar toolbar;
    private final Handler handler = new Handler();

    static class OnScrollStateChanged {
        final Fragment caller;
        final float    translationY;
        final boolean  opened;

        OnScrollStateChanged(final Fragment caller, final float translationY) {
            this.caller = caller;
            this.translationY = translationY;
            this.opened = translationY == 0;
        }
    }

    public static SuperAwesomeCardFragment newInstance(int position) {
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    public void onEvent(OnScrollStateChanged event) {
        if (event.caller != this) {
            Log.i(TAG, "onEvent: " + this + ", opened: " + event.opened);
            handleScrollPosition(event.translationY);
        }
    }

    private void handleScrollPosition(final float position) {
        Log.i(TAG, "handleScrollPosition: " + position);

        if (getListView().getChildCount() > 0) {
            if (getListView().getFirstVisiblePosition() == 0) {
                View view = getListView().getChildAt(0);
                int top = view.getTop();

                Log.v(TAG, "top: " + top);

                if (top != position) {
                    Log.w(TAG, "ok, scrolling list to " + position);
                    getListView().setSelectionFromTop(0, (int) position);
                } else {
                    Log.v(TAG, "top == position");
                }
            } else {
                Log.v(TAG, "firstVisiblePosition > 0");
            }
        } else {
            Log.v(TAG, "child count < 1");
        }
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

        if (null != toolbarContainer) {
            getListView().setOnScrollListener(new PixelScrollDetector(this));
        }

        getListView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                return false;
            }
        });

        EventBus.getDefault().register(this);

        Log.v(TAG, "handler: " + getView().getHandler());

        if (null != toolbarContainer) {
            if (null != handler) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleScrollPosition(ViewCompat.getTranslationY(toolbarContainer));
                    }
                });

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onScroll(final AbsListView view, final float deltaY) {
        int height = toolbar.getHeight();
        float translationY = ViewCompat.getTranslationY(toolbarContainer) + deltaY;
        translationY = Math.max(Math.min(translationY, 0), -height);
        ViewCompat.setTranslationY(toolbarContainer, translationY);
    }

    ObjectAnimator animator;

    @Override
    public void onScrollStateChanged(final int scrollState) {
        if (scrollState == 0) {
            int height = toolbar.getHeight();
            float translationY = ViewCompat.getTranslationY(toolbarContainer);

            if (null == animator) {
                animator = ObjectAnimator.ofFloat(toolbarContainer, "translationY", 0);
                animator.addListener(new Animator.AnimatorListener() {
                    boolean isCancelled;

                    @Override
                    public void onAnimationStart(final Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        if (!isCancelled) {
                            Log.v(TAG, "onAnimationEnd");
                            EventBus.getDefault()
                                .post(new OnScrollStateChanged(SuperAwesomeCardFragment.this,
                                                               ViewCompat.getTranslationY(toolbarContainer)));
                        }
                    }

                    @Override
                    public void onAnimationCancel(final Animator animation) {
                        isCancelled = true;
                    }

                    @Override
                    public void onAnimationRepeat(final Animator animation) {

                    }
                });
                animator.setDuration(100);
            }

            if (translationY > -height / 2) {
                animator.setFloatValues(0);
            } else {
                animator.setFloatValues(-height);
            }
            animator.start();

        } else {
            if (null != animator) {
                animator.cancel();
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
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        public MyAdapter(final Context context, final int resource, final String[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if (position == 0 && null == convertView) {
                view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                  ((MainActivity) getActivity()).getToolBarContainerHeight()));
            }
            return view;
        }
    }

}