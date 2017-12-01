package com.jiubang.golauncher.nestedscrolldemo.presenter;

import com.jiubang.golauncher.nestedscrolldemo.model.AbsBaseModel;
import com.jiubang.golauncher.nestedscrolldemo.viewinterfacae.IBaseView;

import java.lang.ref.WeakReference;

/**
 * Created by yuanzhiwu on 2017/12/1.
 */

public abstract class AbsPresenter<UIInterface extends IBaseView, IModel extends AbsBaseModel> {
    protected WeakReference<UIInterface> mViewRef;
    protected IModel mModel;
    public AbsPresenter(UIInterface uiInterface) {
        mViewRef = new WeakReference<UIInterface>(uiInterface);
        mModel = createModel();
    }

    protected abstract IModel createModel();

}
