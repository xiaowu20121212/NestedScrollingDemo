package com.jiubang.golauncher.nestedscrolldemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jiubang.golauncher.nestedscrolldemo.presenter.ContentPresenter;
import com.jiubang.golauncher.nestedscrolldemo.viewinterfacae.IContentView;

import java.util.ArrayList;

/**
 * Created by YuanZhiWu on 2017/12/3.
 */

public class ComplexCoordinatorActivity extends AppCompatActivity implements IContentView{
    ContentPresenter mPresenter;
    private LinearLayout mContentViews;
    private LinearLayout mObserver;
    private NestedScrollView mObservable;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_coordinator_layout_2);
        mPresenter =  createPresenter();
        mContentViews = (LinearLayout) findViewById(R.id.content_views);
        mObservable = (NestedScrollView) findViewById(R.id.observable);
        mObserver = (LinearLayout) findViewById(R.id.observer);
        mObserver.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom > 0) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mObservable.getLayoutParams();
                    params.topMargin = bottom;
                    mObserver.removeOnLayoutChangeListener(this);
                }
            }
        });
        addContentView(mPresenter.getListView());
    }

    @Override
    public ContentPresenter createPresenter() {
        return new ContentPresenter(this);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void addContentView(ArrayList<View> views) {
        for (int i = 0; i < views.size(); i++) {
            mContentViews.addView(views.get(i), i);
        }
    }
}
