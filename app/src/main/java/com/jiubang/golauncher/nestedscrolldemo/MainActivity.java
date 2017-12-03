package com.jiubang.golauncher.nestedscrolldemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mButton1, mButton2, mButton3, mButton4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton1 = (Button) findViewById(R.id.btn_button_1);
        mButton2 = (Button) findViewById(R.id.btn_button_2);
        mButton3 = (Button) findViewById(R.id.btn_button_3);
        mButton4 = (Button) findViewById(R.id.btn_button_4);
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_button_1:
                intent = new Intent(this, EventActivity.class);
                break;
            case R.id.btn_button_2:
                intent = new Intent(this, CustomActivity.class);
                break;
            case R.id.btn_button_3:
                intent = new Intent(this, NestedScrollViewActivity.class);
                break;
            case R.id.btn_button_4:
                intent = new Intent(this, ComplexCoordinatorActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
