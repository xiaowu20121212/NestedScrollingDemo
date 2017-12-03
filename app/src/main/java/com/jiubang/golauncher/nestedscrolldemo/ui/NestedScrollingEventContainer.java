package com.jiubang.golauncher.nestedscrolldemo.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jiubang.golauncher.nestedscrolldemo.R;
import com.jiubang.golauncher.nestedscrolldemo.presenter.ContentPresenter;
import com.jiubang.golauncher.nestedscrolldemo.viewinterfacae.IContentView;

import java.util.ArrayList;


/**
 * Created by yuanzhiwu on 2017/12/1.
 */

public class NestedScrollingEventContainer extends LinearLayout implements IContentView {
    private static final String TAG = NestedScrollingEventContainer.class.getSimpleName();
    private ContentPresenter mPresenter;
    private ImageView mTopView;
    private TextView mTitle;
    private ScrollView mScrollView;
    private LinearLayout mContentViews;
    private int mTopViewHeight;
    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;
    private float mLastY,mDy,  mMaximumVelocity, mMinimumVelocity;
    private float mTouchSlop;
    private boolean mDragging;
    private boolean isTopHidden;

    public NestedScrollingEventContainer(Context context) {
        this(context, null);
    }

    public NestedScrollingEventContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollingEventContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPresenter = createPresenter();
        mScroller = new OverScroller(context);
        mVelocityTracker = VelocityTracker.obtain();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTopView = (ImageView) findViewById(R.id.img_top_view);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mScrollView = (ScrollView) findViewById(R.id.list_content);
        mContentViews = (LinearLayout) findViewById(R.id.content_views);
        addContentView(mPresenter.getListView());
    }

    @Override
    public void addContentView(ArrayList<View> views) {
        for (int i = 0; i < views.size(); i++) {
            mContentViews.addView(views.get(i), i);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mScrollView.getLayoutParams().height = getMeasuredHeight() - mTitle.getMeasuredHeight();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTopView.getMeasuredHeight();
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                mDy = y - mLastY;
                mLastY = y;
                if (Math.abs(mDy) > mTouchSlop) {
                    if (!isTopHidden
                            || (mScrollView.getScrollY() == 0 && isTopHidden && mDy > 0)) {
                        onTouchEvent(ev);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                onTouchEvent(ev);
                mDy = 0;
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    //如果子view调用requestDisallowInterceptTouchEvent()将不能拦截了
    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        int action = ev.getAction();
        float y = ev.getY();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                if (Math.abs(dy) > mTouchSlop)
                {
                    mDragging = true;
                    if (!isTopHidden
                            || (mScrollView.getScrollY() == 0 && isTopHidden && dy > 0))
                    {
                        return true;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!mDragging && Math.abs(mDy) > mTouchSlop) {
                    mDragging = true;
                }
                if (mDragging) {
                    scrollBy(0, (int) -mDy);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                }
                mVelocityTracker.clear();
                break;
        }

        return super.onTouchEvent(event);
    }

    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        invalidate();
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > mTopViewHeight) {
            y = mTopViewHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }

        isTopHidden = getScrollY() == mTopViewHeight;
        float faction = getScrollY() * 1.0f / mTopViewHeight;
        mTitle.setTranslationX(-300 * faction);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }


    @Override
    public ContentPresenter createPresenter() {
        return new ContentPresenter(this);
    }
}
