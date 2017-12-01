package com.jiubang.golauncher.nestedscrolldemo.presenter;

import android.widget.ArrayAdapter;

import com.jiubang.golauncher.nestedscrolldemo.model.ContentModel;
import com.jiubang.golauncher.nestedscrolldemo.viewinterfacae.IContentView;

/**
 * Created by yuanzhiwu on 2017/12/1.
 */

public class ContentPresenter extends AbsPresenter<IContentView, ContentModel> {
    public ContentPresenter(IContentView iContentView) {
        super(iContentView);
    }

    @Override
    protected ContentModel createModel() {
        return new ContentModel(this);
    }

    public ArrayAdapter<String> getAdapter() {
        return new ArrayAdapter<String>(mViewRef.get().getContext(),android.R.layout.simple_list_item_1, mModel.getData());
    }
}
