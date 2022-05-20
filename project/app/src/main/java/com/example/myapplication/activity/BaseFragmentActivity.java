package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.myapplication.R;


//基类用来继承的 主要用来设置标题栏
public class BaseFragmentActivity extends FragmentActivity {

    protected Context mContext;
    protected Activity mActivity;
    private TextView title_back,title_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @SuppressLint("NewApi")
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            View v = findViewById(R.id.root_layout);
            if (v != null) {
                v.setFitsSystemWindows(true);
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        title_back = (TextView) findViewById(R.id.title_back);
        title_name = (TextView) findViewById(R.id.title_name);
        if(title_back != null){
            title_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    //设置标题栏的名字
    public void setTitleName(String str){
        if(title_name != null){
            title_name.setText(str);
        }
    }
}
