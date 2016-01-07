package com.reddit.material.libraries;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reddit.material.ImageGalleryAdapter;
import com.reddit.material.R;

/**
 * Created by liangfeizc on 3/26/15.
 */
public class PageIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    private int mActivePosition = -1;
    private boolean mIndicatorTypeChanged = false;
    private ViewPager mViewPager;

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        if (!(getLayoutParams() instanceof FrameLayout.LayoutParams)) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.BOTTOM | Gravity.START;
            setLayoutParams(params);
        }
    }

    public void setViewPager(ViewPager pager) {
        mViewPager = pager;
        ImageGalleryAdapter adapter = (ImageGalleryAdapter) pager.getAdapter();
        addIndicator(adapter.getCount());
        pager.addOnPageChangeListener(this);
    }

    private void removeIndicator() {
        removeAllViews();
    }

    private void addIndicator(int count) {
        removeIndicator();
        if (count <= 0) return;
        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.WHITE);
        int padding = dp2px(getContext(), 10);
        textView.setPadding(padding, padding >> 1, padding, padding >> 1);
        textView.setBackgroundResource(R.drawable.fraction_indicator_bg);
        textView.setTag(count);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(textView, params);
        updateIndicator(mViewPager.getCurrentItem());
    }

    private void updateIndicator(int position) {
        if (mIndicatorTypeChanged || mActivePosition != position) {
            mIndicatorTypeChanged = false;
            TextView textView = (TextView) getChildAt(0);
            textView.setText(String.format("%d/%d", position + 1, (int) textView.getTag()));
            mActivePosition = position;
        }
    }

    private int dp2px(Context context, int dpValue) {
        return (int) context.getResources().getDisplayMetrics().density * dpValue;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        updateIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

