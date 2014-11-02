package com.astuetz.viewpager.extensions.sample;

import android.view.View;
import android.widget.AbsListView;

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

    /**
     * Created by alessandro on 02/11/14.
     */
    public static interface PixelScrollListener {
        public void onScroll(AbsListView view, float deltaY);

        void onScrollStateChanged(int scrollState);
    }
}
