package com.xiay.applib.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class VerticalSwipeRefreshLayout extends SwipeRefreshLayout {
    public VerticalSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;//避免滑动广告轮播的时候触发刷新
    }
}