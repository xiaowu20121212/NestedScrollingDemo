package com.jiubang.golauncher.nestedscrolldemo.model;

import com.jiubang.golauncher.nestedscrolldemo.presenter.AbsPresenter;

/**
 * Created by yuanzhiwu on 2017/12/1.
 */

public abstract class AbsBaseModel<presenter extends AbsPresenter> {
    protected presenter mPresenter;
    public AbsBaseModel(presenter presenter) {
        mPresenter = presenter;
    }

}
