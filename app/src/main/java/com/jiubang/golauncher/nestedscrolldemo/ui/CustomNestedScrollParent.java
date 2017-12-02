package com.jiubang.golauncher.nestedscrolldemo.ui;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import com.jiubang.golauncher.nestedscrolldemo.presenter.ContentPresenter;
import com.jiubang.golauncher.nestedscrolldemo.viewinterfacae.IContentView;

import java.util.ArrayList;

/**
 * Created by yuanzhiwu on 2017/12/2
 * 嵌套滑动机制父View
 */

public class CustomNestedScrollParent extends LinearLayout implements NestedScrollingParent, IContentView {
    private ContentPresenter mPresenter;
    private ImageView mTopView;
    private TextView mTitle;
    private CustomNestedScrollChild myNestedScrollChild;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private int imgHeight;
    private int tvHeight;
    private OverScroller mScroller;

    public CustomNestedScrollParent(Context context) {
        super(context);
    }

    public CustomNestedScrollParent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPresenter = createPresenter();
        mScroller = new OverScroller(getContext());
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    //获取子view
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTopView = (ImageView) getChildAt(0);
        mTitle = (TextView) getChildAt(1);
        myNestedScrollChild = (CustomNestedScrollChild) getChildAt(2);
        mTopView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (imgHeight <= 0) {
                    imgHeight = mTopView.getMeasuredHeight();
                }
            }
        });
        mTitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (tvHeight <= 0) {
                    tvHeight = mTitle.getMeasuredHeight();
                }
            }
        });
        addContentView(mPresenter.getListView());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpec = MeasureSpec.makeMeasureSpec(myNestedScrollChild.getMeasuredWidth(), MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - mTitle.getMeasuredHeight(), MeasureSpec.EXACTLY);
        myNestedScrollChild.measure(widthSpec, heightSpec);
    }

    //在此可以判断参数target是哪一个子view以及滚动的方向，然后决定是否要配合其进行嵌套滚动
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (target instanceof CustomNestedScrollChild) {
            return true;
        }
        return false;
    }


    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
    }

    //先于child滚动
    //前3个为输入参数，最后一个是输出参数
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (showImg(dy) || hideImg(dy)) {//如果需要显示或隐藏图片，即需要自己(parent)滚动
            scrollBy(0, -dy);//滚动
            consumed[1] = dy;//告诉child我消费了多少
        }
    }

    //后于child滚动
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

    }

    //返回值：是否消费了fling
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    //返回值：是否消费了fling
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    //下拉的时候是否要向下滚动以显示图片
    public boolean showImg(int dy) {
        if (dy > 0) {
            if (getScrollY() > 0 && myNestedScrollChild.getScrollY() == 0) {
                return true;
            }
        }

        return false;
    }

    //上拉的时候，是否要向上滚动，隐藏图片
    public boolean hideImg(int dy) {
        if (dy < 0) {
            if (getScrollY() < imgHeight) {
                return true;
            }
        }
        return false;
    }

    //scrollBy内部会调用scrollTo
    //限制滚动范围
    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > imgHeight) {
            y = imgHeight;
        }

        super.scrollTo(x, y);
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

    @Override
    public void addContentView(ArrayList<View> views) {
        for (int i = 0; i < views.size(); i++) {
            myNestedScrollChild.addView(views.get(i), i);
        }
    }
}
