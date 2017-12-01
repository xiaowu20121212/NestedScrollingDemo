package com.jiubang.golauncher.nestedscrolldemo.viewinterfacae;

import android.content.Context;

import com.jiubang.golauncher.nestedscrolldemo.presenter.AbsPresenter;

/**
 * Created by yuanzhiwu on 2017/12/1.
 */

public interface IBaseView<presenter extends AbsPresenter> {
    presenter createPresenter();
    Context getContext();
}
