package com.jiubang.golauncher.nestedscrolldemo.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import com.jiubang.golauncher.nestedscrolldemo.R;
import com.jiubang.golauncher.nestedscrolldemo.presenter.ContentPresenter;
import com.jiubang.golauncher.nestedscrolldemo.viewinterfacae.IContentView;

import java.util.ArrayList;

/**
 * Created by YuanZhiWu on 2017/12/3.
 */

public class NestedScrollingV4Parent extends LinearLayout implements NestedScrollingParent, IContentView {
    private ContentPresenter mPresenter;
    private ImageView mTopView;
    private TextView mTitle;
    private NestedScrollView myNestedScrollChild;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private int imgHeight;
    private int tvHeight;
    private OverScroller mScroller;
    public NestedScrollingV4Parent(Context context) {
        this(context, null);
    }

    public NestedScrollingV4Parent(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollingV4Parent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        mTopView = (ImageView) findViewById(R.id.img_top_view);
        mTitle = (TextView) findViewById(R.id.tv_title);
        myNestedScrollChild = (NestedScrollView) findViewById(R.id.nested_child);
        myNestedScrollChild.setNestedScrollingEnabled(true);
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
    @Override
    public ContentPresenter createPresenter() {
        return new ContentPresenter(this);
    }

    @Override
    public void addContentView(ArrayList<View> views) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(VERTICAL);
        for (int i = 0; i < views.size(); i++) {
            linearLayout.addView(views.get(i), i);
        }
        myNestedScrollChild.addView(linearLayout);

    }



    //在此可以判断参数target是哪一个子view以及滚动的方向，然后决定是否要配合其进行嵌套滚动
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (target instanceof NestedScrollView) {
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
        if (showImg(-dy) || hideImg(-dy)) {//如果需要显示或隐藏图片，即需要自己(parent)滚动
            scrollBy(0, dy);//滚动
            consumed[1] = dy;//告诉child我消费了多少
        }
    }

    //后于child滚动
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        //Log.d("xiaowu_nested" , "====dy: " + dyConsumed + "====undy:" + dyUnconsumed);
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
        if ( y != getScrollY()) {
            super.scrollTo(x, y);
            float faction = getScrollY() * 1.0f / imgHeight;
            mTitle.setTranslationX(-300 * faction);
        }
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }
}
