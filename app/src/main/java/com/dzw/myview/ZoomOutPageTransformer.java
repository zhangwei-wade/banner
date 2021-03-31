package com.dzw.myview;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager2.widget.ViewPager2;

/**
 * @author zhangwei on 2021/3/16.
 */
public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.95f;

    @Override
    public void transformPage(View view, float position) {
        float scale;
        if (0.0f <= position && position <= 1.0f) {
            scale = 1.0f - position;
            scale = Math.max(MIN_SCALE, scale);
        } else if (-1.0f <= position && position < 0.0f) {
            scale = position + 1.0f;
            scale = Math.max(MIN_SCALE, scale);
        } else {
            scale = MIN_SCALE;
        }
        view.setScaleX(scale);
        view.setScaleY(scale);
    }
}
