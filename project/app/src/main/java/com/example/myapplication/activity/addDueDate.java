package com.example.myapplication.activity;


import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.fragment.task.taskDB;
import com.example.myapplication.fragment.PlanFragment;
import com.example.myapplication.fragment.TodayFragment;


public class addDueDate extends AppCompatActivity {
    taskDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_due_date);

        db = PlanFragment.db;
        final String date = getIntent().getExtras().getString("date");

        Button addbut = (Button) findViewById(R.id.addbutton);
        addbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titleText = (EditText) findViewById(R.id.editText3);
                String title = titleText.getText().toString();
                EditText dueDateText = (EditText) findViewById(R.id.editText4);
                String dueDate = dueDateText.getText().toString();
                EditText hoursText = (EditText) findViewById(R.id.editText5);
                String hours_string = hoursText.getText().toString();

                if(title.equals("") || dueDate.equals("") || hours_string.equals("")){
                    Toast.makeText(addDueDate.this, "Please fulfill all" +
                            " criteria correctly", Toast.LENGTH_SHORT).show();
                }
                else {
                    int hours = Integer.parseInt(hours_string);
                    dueDate = dueDate.replaceAll("\\s+", "");
                    dueDate = dueDate.replaceAll("-", "");
                    dueDate = dueDate.replaceAll("/", "");
                    if(dueDate.length() != 8){
                        Toast.makeText(addDueDate.this, "Please correctly" +
                                " format date: make sure to add a leading 0 to month or day if " +
                                " necessary", Toast.LENGTH_LONG).show();
                    }
                    else {
                        /**
                         * 从数据库获取数据显示添加到适配器中
                         */
                        String duerDater = dueDate.substring(4) + dueDate.substring(0, 2) +
                                dueDate.substring(2, 4);
                        db.insert(duerDater, title, hours, date);
                        Log.d("1111111", duerDater);
                        Log.d("2222222", title);
                        Log.d("3333333", String.valueOf(hours));
                        Log.d("4444444", date);
                        SharedPreferences sp = getSharedPreferences("suc",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        int f1 = sp.getInt("f1",3);
                        int f2 = sp.getInt("f2",3);
                        if (f1 == 1){
                            editor.putInt("f1",2);
                            editor.commit();
                        }
                        SharedPreferences sp2 = getSharedPreferences("num",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = sp2.edit();
                        int num = sp2.getInt("num",0);
                        if (f2 == 1){
                            num ++ ;
                            if (num == 3){
                                editor.putInt("f2",2);
                            }else {
                                editor1.putInt("num",num);
                            }
                        }

                        Bundle args = new Bundle();
                        PlanFragment planFragment = new PlanFragment();
                        args.putString("date",date);
                        planFragment.setArguments(args);
                        finish();
                    }
                }
            }
        });

        Button cancbutton = (Button) findViewById(R.id.CancelButton);
        cancbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(addDueDate.this,
                        PlanFragment.class);
                intent.putExtra("date", date);

                startActivity(intent);
                */
                finish();

            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}