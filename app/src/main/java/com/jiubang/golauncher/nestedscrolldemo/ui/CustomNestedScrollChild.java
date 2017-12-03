package com.jiubang.golauncher.nestedscrolldemo.ui;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;


/**
 * Created by yuanzhiwu on 2017/12/2
 */

public class CustomNestedScrollChild extends LinearLayout implements NestedScrollingChild {
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] offset = new int[2]; //偏移量
    private int mNestedYOffset;
    private final int[] consumed = new int[2]; //消费
    private int lastY;
    private int mMaxScrollY;
    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;
    private float mMaximumVelocity, mMinimumVelocity;
    private float mTouchSlop;

    public CustomNestedScrollChild(Context context) {
        super(context);
    }

    public CustomNestedScrollChild(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new OverScroller(context);
        mVelocityTracker = VelocityTracker.obtain();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //第一次测量，由于父类限制了该子类高度为父容器高度减去mTitle的高度，拿到可视区域
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int showHeight = getMeasuredHeight();

        //第二次测量，没有任何限制，那么测量出来的就是完全展示内容所需要的高度
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算可以滚动的最大范围
        mMaxScrollY = getMeasuredHeight() - showHeight;
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取一个用于计算
        MotionEvent vtev = MotionEvent.obtain(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }
        vtev.offsetLocation(0, mNestedYOffset);
        switch (event.getAction()) {
            //按下
            case MotionEvent.ACTION_DOWN:
                initOrResetVelocityTracker();
                lastY = (int) event.getY();
                break;
            //移动
            case MotionEvent.ACTION_MOVE:
                //如果是getY()方法来计算偏移值，那么一定结合子类的offset来计算准确
                //如果是getRawY()方法来结算偏移值，那么不需要结合offset，并且不需要重新赋值mLastY,每次获取相减即可
                final int y = (int) (event.getY());
                int dy = y - lastY;
                if (Math.abs(dy) < mTouchSlop) {
                    return true;
                }
                mVelocityTracker.addMovement(event);
                Log.d("xiaowu_nested" , "\n\n产生dy: " + dy);
                if (startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL)
                        && dispatchNestedPreScroll(0, dy, consumed, offset)) {
                    //如果找到了支持嵌套滑动的父类,并且父类产生了消费consumed[0] != 0 || consumed[1] != 0
                    //dispatchNestedPreScroll方法返回 return consumed[0] != 0 || consumed[1] != 0;
                    Log.d("xiaowu_nested" , "父view消费: " + consumed[1]);
                    //获取滑动距离
                    dy -= consumed[1];
                    vtev.offsetLocation(0, offset[1]);
                    mNestedYOffset += offset[1];
                }
                //如果子类发生了view偏移，实际上y的值则offset[1]不等于0
                lastY = y - offset[1];
                final int oldY = getScrollY();
                //子类消费剩余的dy，内容滚动是相反的，所以取反
                scrollBy(0, -dy);
                final int dyConsumed = oldY - getScrollY();
                final int unconsumedY =  dy - dyConsumed ;
                Log.d("xiaowu_nested" , "子view消费: " + dyConsumed + " 剩：" + unconsumedY);
                //子类发生了滚动后再次通知父类
                if (dispatchNestedScroll(0, dyConsumed, 0, unconsumedY, offset)) {
                    //dispatchNestedScroll 的返回值dxConsumed != 0 || dyConsumed != 0 || dxUnconsumed != 0 || dyUnconsumed != 0
                    lastY -= offset[1];
                    vtev.offsetLocation(0, offset[1]);
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    flingWithNestedDispatch(-velocityY);
                }
                stopNestedScroll();
                recycleVelocityTracker();
                break;
        }
        //UP事件将不再收到
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(vtev);
        }
        vtev.recycle();
        return true;
    }

    //限制滚动范围
    @Override
    public void scrollTo(int x, int y) {
        if (y > mMaxScrollY) {
            y = mMaxScrollY;
        }
        if (y < 0) {
            y = 0;
        }
        super.scrollTo(x, y);
    }

    //初始化helper对象
    private NestedScrollingChildHelper getScrollingChildHelper() {
        if (mNestedScrollingChildHelper == null) {
            mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
            mNestedScrollingChildHelper.setNestedScrollingEnabled(true);
        }
        return mNestedScrollingChildHelper;
    }

    //实现一下接口
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        getScrollingChildHelper().setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return getScrollingChildHelper().isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return getScrollingChildHelper().startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        getScrollingChildHelper().stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return getScrollingChildHelper().hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return getScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return getScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
    }

    private void flingWithNestedDispatch(int velocityY) {
        final int scrollY = getScrollY();
        final boolean canFling = (scrollY > 0 || velocityY > 0)
                && (scrollY < getScrollRange() || velocityY < 0);
        if (!dispatchNestedPreFling(0, velocityY)) {
            dispatchNestedFling(0, velocityY, canFling);
            if (canFling) {
                fling(velocityY);
            }
        }
    }

    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mMaxScrollY);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    private int getScrollRange() {
        return mMaxScrollY;
    }
}
