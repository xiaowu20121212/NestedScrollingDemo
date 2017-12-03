package com.jiubang.golauncher.nestedscrolldemo.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.jiubang.golauncher.nestedscrolldemo.R;

/**
 * Created by YuanZhiWu on 2017/12/3.
 */

public class NestedScrollViewBehavior extends CoordinatorLayout.Behavior {
    public NestedScrollViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency.getId() == R.id.observer;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        child.setTranslationY(dependency.getTranslationY());
        return true;
    }
}
