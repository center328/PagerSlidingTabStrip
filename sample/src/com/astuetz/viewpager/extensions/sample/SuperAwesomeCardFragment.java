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
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.nineoldandroids.animation.ObjectAnimator;

public class SuperAwesomeCardFragment extends ListFragment implements PixelScrollListener {
    private static final String ARG_POSITION = "position";
    private int            position;
    private ScrollDelegate delegate;
    private View           toolbarContainer;
    private Toolbar        toolbar;

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

        delegate = new ScrollDelegate();
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

    private static class ScrollDelegate implements AbsListView.OnScrollListener {
        View    mFirstView;
        boolean mScrollStarted;
        int mFirstVisibleItem = -1;
        int mFirstViewTop;
        int mFirstViewTopAmount;
        int mAnimationDirection;
        private int newBottom;

        @Override
        public void onScrollStateChanged(final AbsListView view, final int scrollState) {
            Log.v("TAG", "onScrollStateChanged: " + scrollState);

            if (scrollState == 1) {
                // scroll starting
                if (view.getChildCount() > 0) {
                    mFirstView = view.getChildAt(0);
                    if (null == mFirstView) {
                        return;
                    }
                    mFirstViewTop = mFirstView.getTop();
                    mScrollStarted = true;
                }
                mFirstViewTopAmount = 0;
                mAnimationDirection = 0;
            } else if (scrollState == 0) {
                // scroll ended
                mFirstVisibleItem = -1;
                mScrollStarted = false;
            }
        }

        @Override
        public void onScroll(
            final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
            if (totalItemCount == 0 || !mScrollStarted) {
                return;
            }

            boolean newChild;
            int newTop;
            int diff = 0;

            if (mFirstVisibleItem != firstVisibleItem) {
                mFirstVisibleItem = firstVisibleItem;
                mFirstView = view.getChildAt(0);
                if (null == mFirstView) {
                    return;
                }

                Log.v("TAG", "OLD: " + mFirstViewTop + "x" + newBottom);
                Log.v("TAG", "NEW: " + mFirstView.getTop() + "x" + mFirstView.getBottom());

                diff = mFirstView.getTop() + newBottom;
                Log.w("TAG", "diff: " + diff);

                mFirstViewTop = mFirstView.getTop();
                newTop = mFirstViewTop - (diff * 2);
                newChild = true;

            } else {
                newTop = mFirstView.getTop();
                newBottom = mFirstView.getBottom();
            }

            int delta = (mFirstViewTop - newTop);
            Log.d("TAG", "delta: " + delta);

            mFirstViewTopAmount += delta;
            mFirstViewTop = newTop;

            if (true) {
                if (mFirstViewTopAmount > 0) {
                    mAnimationDirection = 1;
                } else {
                    mAnimationDirection = -1;
                }
            }

            //Log.d("TAG", "scrolled: " + mFirstViewTopAmount + ", direction: " + mAnimationDirection);

        }
    }

    /**
     * Created by budius on 16.05.14.
     * This improves on Zsolt Safrany answer on stack-overflow (see link)
     * by making it a detector that can be attached to any AbsListView.
     * http://stackoverflow.com/questions/8471075/android-listview-find-the-amount-of-pixels-scrolled
     */
    public class PixelScrollDetector implements AbsListView.OnScrollListener {
        private final PixelScrollListener listener;
        private       View                mTrackedChild;
        private       int                 mTrackedChildPrevPosition;
        private       int                 mTrackedChildPrevTop;

        public PixelScrollDetector(PixelScrollListener listener) {
            this.listener = listener;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // init the values every time the list is moving
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                if (mTrackedChild == null) {
                    syncState(view);
                }
            }
            listener.onScrollStateChanged(scrollState);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mTrackedChild == null) {
                // case we don't have any reference yet, try again here
                syncState(view);
            } else {
                boolean childIsSafeToTrack =
                    (mTrackedChild.getParent() == view) && (view.getPositionForView(mTrackedChild) == mTrackedChildPrevPosition);
                if (childIsSafeToTrack) {
                    int top = mTrackedChild.getTop();
                    if (listener != null) {
                        float deltaY = top - mTrackedChildPrevTop;
                        listener.onScroll(view, deltaY);
                    }
                    // re-syncing the state make the tracked child change as the list scrolls,
                    // and that gives a much higher true state for `childIsSafeToTrack`
                    syncState(view);
                } else {
                    mTrackedChild = null;
                }
            }
        }

        private void syncState(AbsListView view) {
            if (view.getChildCount() > 0) {
                mTrackedChild = getChildInTheMiddle(view);
                mTrackedChildPrevTop = mTrackedChild.getTop();
                mTrackedChildPrevPosition = view.getPositionForView(mTrackedChild);
            }
        }

        private View getChildInTheMiddle(AbsListView view) {
            return view.getChildAt(view.getChildCount() / 2);
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