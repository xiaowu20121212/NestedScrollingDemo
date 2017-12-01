package com.jiubang.golauncher.nestedscrolldemo.viewinterfacae;

import android.view.View;

import com.jiubang.golauncher.nestedscrolldemo.presenter.ContentPresenter;

import java.util.ArrayList;

/**
 * Created by yuanzhiwu on 2017/12/1.
 */

public interface IContentView extends IBaseView<ContentPresenter> {
    public void addContentView(ArrayList<View> views);
}
