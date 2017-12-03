package com.jiubang.golauncher.nestedscrolldemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by YuanZhiWu on 2017/12/3.
 */

public class EasyCoordinatorActivity extends AppCompatActivity {
    private TextView mObserver;
    public Button mObservable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_coordinator_layout_1);
        mObservable = (Button) findViewById(R.id.tv_observable);
        mObserver = (TextView) findViewById(R.id.tv_observer);
        mObservable.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        v.setX(event.getRawX() - v.getWidth() / 2);
                        v.setY(event.getRawY() - v.getHeight() / 2);
                        break;
                }
                return true;
            }
        });
    }
}
