package com.jiubang.golauncher.nestedscrolldemo.presenter;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jiubang.golauncher.nestedscrolldemo.model.ContentModel;
import com.jiubang.golauncher.nestedscrolldemo.viewinterfacae.IContentView;

import java.util.ArrayList;

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
        return new ArrayAdapter<String>(mViewRef.get().getContext(), android.R.layout.simple_list_item_1, mModel.getData());
    }

    public ArrayList<View> getListView() {
        ArrayList<String> data = mModel.getData();
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            TextView textView = new TextView(mViewRef.get().getContext());
            textView.setText(data.get(i));
            views.add(textView);
        }
        return views;
    }
}
