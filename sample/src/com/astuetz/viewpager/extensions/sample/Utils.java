package com.astuetz.viewpager.extensions.sample;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;

/**
 * Created by alessandro on 03/11/14.
 */
public class Utils {
    public static int getActionBarHeight(@NonNull Context context) {
        final int[] attrs;
        if (Build.VERSION.SDK_INT >= 11) {
            attrs = new int[]{android.R.attr.actionBarSize};
        } else {
            attrs = new int[]{R.attr.actionBarSize};
        }

        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(attrs);
        int size = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return size;
    }

    public static int getThemeDimension(@NonNull Context context, @AttrRes int resId) {
        final int[] attrs = new int[]{resId};
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(attrs);
        int size = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return size;
    }
}
