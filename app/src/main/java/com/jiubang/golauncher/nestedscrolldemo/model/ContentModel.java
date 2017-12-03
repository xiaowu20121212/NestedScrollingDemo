package com.jiubang.golauncher.nestedscrolldemo.model;

import com.jiubang.golauncher.nestedscrolldemo.presenter.ContentPresenter;

import java.util.ArrayList;

/**
 * Created by yuanzhiwu on 2017/12/1.
 */

public class ContentModel extends AbsBaseModel<ContentPresenter> {

    public ContentModel(ContentPresenter presenter) {
        super(presenter);
    }

    public ArrayList<String>  getData() {
        ArrayList<String> data = new ArrayList<>();
        int i = 0;
        do {
            String text = "Number is " + i;
            data.add(text);
            i++;
        }while (i < 150);
        return data;
    };
}
