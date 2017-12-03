package com.jiubang.golauncher.nestedscrolldemo.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jiubang.golauncher.nestedscrolldemo.R;

/**
 * Created by YuanZhiWu on 2017/12/3.
 */

public class EasyBehavior extends CoordinatorLayout.Behavior<TextView> {
    public EasyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private final String TAG = "EasyBehavior";
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        Log.d(TAG, "onDependentViewChanged: ");
        child.setX(dependency.getX()+200);
        child.setY(dependency.getY()+200);
        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        Log.d(TAG, "layoutDependsOn: ");
        return dependency.getId() == R.id.tv_observable;
    }
}
