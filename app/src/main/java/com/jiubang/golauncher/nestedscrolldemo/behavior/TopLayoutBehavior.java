package com.jiubang.golauncher.nestedscrolldemo.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by YuanZhiWu on 2017/12/3.
 */

public class TopLayoutBehavior extends CoordinatorLayout.Behavior {
    public TopLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        int imgHeight = ((ViewGroup)child).getChildAt(0).getMeasuredHeight();
        if (showImg(child, -dy, -imgHeight, target) || hideImg(child, -dy, -imgHeight, target)) {
            float tranY = child.getTranslationY() - dy;
            if (tranY > 0) {
                tranY = 0;
            }
            if (tranY < (-imgHeight)) {
                tranY = -imgHeight;
            }
            float nowY = child.getTranslationY();
            child.setTranslationY(tranY);
            float newY = child.getTranslationY();
            consumed[1] = (int) (newY - nowY);
            ((ViewGroup)((ViewGroup)child).getChildAt(1)).getChildAt(0).setTranslationX(-300f * child.getTranslationY()/ -imgHeight );
        }

    }

    //下拉的时候是否要向下滚动以显示图片
    public boolean showImg(View view, int dy, int maxTranY, View target) {
        if (dy > 0) {
            if (view.getTranslationY() < 0 && target.getScrollY() == 0) {
                return true;
            }
        }

        return false;
    }

    //上拉的时候，是否要向上滚动，隐藏图片
    public boolean hideImg(View view ,int dy, int maxTranY, View target) {
        if (dy < 0) {
            if (view.getTranslationY() > maxTranY) {
                return true;
            }
        }
        return false;
    }
}
