package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.base.ListenerManager;
import com.example.myapplication.fragment.task.DB;

public class QuestActivity extends AppCompatActivity {

    private Button b1;
    private ImageView iv1;
    private Button b2;
    private ImageView iv2;
    private Button b3;
    private ImageView iv3;
    private Button back;
    int iv[] = {R.drawable.paster1,R.drawable.paster2,R.drawable.paster3,R.drawable.paster4,
            R.drawable.paster5,R.drawable.paster6};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        showDialog();
    }
    private void showDialog() {

        b1 = (Button) findViewById(R.id.b1);
        iv1 = (ImageView) findViewById(R.id.iv1);
        b2 = (Button) findViewById(R.id.b2);
        iv2 = (ImageView) findViewById(R.id.iv2);
        b3 = (Button) findViewById(R.id.b3);
        iv3 = (ImageView) findViewById(R.id.iv3);
        back = (Button) findViewById(R.id.BackButton);

        setimage(iv1);
        setimage(iv2);
        setimage(iv3);
        SharedPreferences sp = getSharedPreferences("suc",Context.MODE_PRIVATE);//两个参数分别是文件名 和操作方式
        int f1 = sp.getInt("f1",0);//从中获取数据
        int f2 = sp.getInt("f2",0);
        int f3 = sp.getInt("f3",0);

//        注释掉三个if，就变成只可接一次
        if (f1 == 3){
            f1 = 0;
        }
        if (f2 == 3){
            f2 = 0;
        }
        if (f3 == 3){
            f3 = 0;
        }

        settext(b1,f1);
        settext(b2,f2);
        settext(b3,f3);
        final int finalF = f1;
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butt(finalF,b1,"f1");
            }
        });
        final int finalF1 = f2;
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butt(finalF1,b2,"f2");
            }
        });
        final int finalF2 = f3;
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butt(finalF2,b3,"f3");
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void setimage(ImageView iv1) {
        SharedPreferences sp1 = getSharedPreferences("math",Context.MODE_PRIVATE);
        int t = sp1.getInt("math",6);
        int i = (int)((Math.random()*5));
        SharedPreferences sp = getSharedPreferences("math",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("math",i);
        editor.commit();
        if (t == 6){
            iv1.setImageResource(iv[i]);
        }else {
            iv1.setImageResource(iv[t]);
        }
    }
    /**
     * 使用getSharedPreferences判断任务状态
     */
    private void butt(int f1, Button b1, String s) {
        if (f1 == 0){
            SharedPreferences sp = getSharedPreferences("suc",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(s,1);
            editor.commit();
            b1.setText("进行任务");
            b1.setBackgroundResource(R.drawable.partner_man2);
        }else if (f1 == 2){
            DB db = new DB(QuestActivity.this, DB.DB_NAME, null, 1);
            SharedPreferences sp1 = getSharedPreferences("math",Context.MODE_PRIVATE);
            int t = sp1.getInt("math",0);
            db.insert(t);
            SharedPreferences sp = getSharedPreferences("suc",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(s,3);
            editor.commit();
            ListenerManager.getInstance().sendBroadCast(1,"1", "1");
            b1.setText("已完成");
        }else {
            b1.setEnabled(false);
        }
    }
    private void settext(Button b1, int f1) {
        switch (f1){
            case 0:
                b1.setText("接受任务");
                b1.setBackgroundResource(R.drawable.partner_man);
                break;
            case 1:
                b1.setText("进行任务");
                b1.setBackgroundResource(R.drawable.partner_man2);
                break;
            case 2:
                b1.setText("完成任务");
                b1.setBackgroundResource(R.drawable.partner_man3);
                break;
            case 3:
                b1.setText("已完成");
                b1.setBackgroundResource(R.drawable.partner_man3);
                break;
        }
    }
}