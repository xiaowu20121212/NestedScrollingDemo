package com.jiubang.golauncher.nestedscrolldemo.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import com.jiubang.golauncher.nestedscrolldemo.R;
import com.jiubang.golauncher.nestedscrolldemo.presenter.ContentPresenter;
import com.jiubang.golauncher.nestedscrolldemo.viewinterfacae.IContentView;

import java.util.ArrayList;


/**
 * Created by yuanzhiwu on 2017/12/1.
 */

public class NestedScrollingEventContainer extends LinearLayout implements IContentView, NestedScrollingParent {
    private static final String TAG = NestedScrollingEventContainer.class.getSimpleName();
    private ContentPresenter mPresenter;
    private ImageView mTopView;
    private TextView mTitle;
    private NestedScrollView mScrollView;
    private LinearLayout mContentViews;
    private int mTopViewHeight;
    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;
    private float mLastY, mMaximumVelocity, mMinimumVelocity;
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
        mScrollView = (NestedScrollView) findViewById(R.id.list_content);
        mContentViews = (LinearLayout) findViewById(R.id.content_views);
        addContentView(mPresenter.getListView());
    }

    @Override
    public void addContentView(ArrayList<View> views) {
        for(int i = 0; i< views.size(); i++) {
            mContentViews.addView(views.get(i), i);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mScrollView.getLayoutParams().height = getMeasuredHeight() - mTitle.getMeasuredHeight();

    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes)
    {
        Log.e(TAG, "onStartNestedScroll");
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes)
    {
        Log.e(TAG, "onNestedScrollAccepted");
    }

    @Override
    public void onStopNestedScroll(View target)
    {
        Log.e(TAG, "onStopNestedScroll");
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed)
    {
        Log.d("onNestedScroll value", "dxConsumed:" + dxConsumed + " dyConsumed:" + dyConsumed
                + " dxUnconsumed:" + dxUnconsumed + " dyUnconsumed:" + dyUnconsumed);
        Log.e(TAG, "onNestedScroll");
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed)
    {
        Log.e(TAG, "onNestedPreScroll");
        boolean hiddenTop = dy > 0 && getScrollY() < mTopViewHeight;
        boolean showTop = dy < 0 && getScrollY() >= 0 && !ViewCompat.canScrollVertically(target, -1);

        if (hiddenTop || showTop)
        {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
        Log.d("onNestedPreScroll value", "dx:" + dx + " dy:" + dy + " consumed[0]:" + consumed[0] + " consumed[1]:" + consumed[1]);
    }


    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed)
    {
        Log.e(TAG, "onNestedFling");
        if (!consumed) {
            animateScroll(velocityY, computeDuration(0),consumed);
        } else {
            animateScroll(velocityY, computeDuration(velocityY),consumed);
        }
        return true;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY)
    {
        Log.e(TAG, "onNestedPreFling");
        //不做拦截 可以传递给子View
        return false;
    }

    @Override
    public int getNestedScrollAxes()
    {
        Log.e(TAG, "getNestedScrollAxes");
        return 0;
    }

    /**
     * 根据速度计算滚动动画持续时间
     * @param velocityY
     * @return
     */
    private int computeDuration(float velocityY) {
        final int distance;
        if (velocityY > 0) {
            distance = Math.abs(mTopView.getHeight() - getScrollY());
        } else {
            distance = Math.abs(mTopView.getHeight() - (mTopView.getHeight() - getScrollY()));
        }


        final int duration;
        velocityY = Math.abs(velocityY);
        if (velocityY > 0) {
            duration = 3 * Math.round(1000 * (distance / velocityY));
        } else {
            final float distanceRatio = (float) distance / getHeight();
            duration = (int) ((distanceRatio + 1) * 150);
        }

        return duration;

    }
    private ValueAnimator mOffsetAnimator;
    private void animateScroll(float velocityY, final int duration,boolean consumed) {
        final int currentOffset = getScrollY();
        final int topHeight = mTopView.getHeight();
        if (mOffsetAnimator == null) {
            mOffsetAnimator = new ValueAnimator();
            mOffsetAnimator.setInterpolator(new LinearInterpolator());
            mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation.getAnimatedValue() instanceof Integer) {
                        scrollTo(0, (Integer) animation.getAnimatedValue());
                    }
                }
            });
        } else {
            mOffsetAnimator.cancel();
        }
        mOffsetAnimator.setDuration(Math.min(duration, 600));

        if (velocityY >= 0) {
            mOffsetAnimator.setIntValues(currentOffset, topHeight);
            mOffsetAnimator.start();
        }else {
            //如果子View没有消耗down事件 那么就让自身滑倒0位置
            if(!consumed){
                mOffsetAnimator.setIntValues(currentOffset, 0);
                mOffsetAnimator.start();
            }

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTopView.getMeasuredHeight();
    }

  /*  @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                mLastY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;

                if (!mDragging && Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                }
                if (mDragging) {
                    scrollBy(0, (int) -dy);
                    mLastY = y;
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
    }*/

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

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                if (Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                    if (!isTopHidden || (mScrollView.getScrollY() == 0 && isTopHidden && dy > 0)) {
                        return true;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }*/

    @Override
    public ContentPresenter createPresenter() {
        return new ContentPresenter(this);
    }
}
