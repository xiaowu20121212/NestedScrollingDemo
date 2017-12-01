package com.jiubang.golauncher.nestedscrolldemo.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by yuanzhiwu on 2017/12/1.
 */

public class NestedScrollingEventContainer extends LinearLayout {
    private ImageView mTopView;
    private TextView mTitle;
    private ListView mContentView;
    public NestedScrollingEventContainer(Context context) {
        super(context);
    }

    public NestedScrollingEventContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedScrollingEventContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTopView = findViewsWithText();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
}
