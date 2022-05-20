package com.example.myapplication.fragment;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.example.myapplication.R;
import com.example.myapplication.fragment.task.taskDB;
import com.example.myapplication.fragment.PlanFragment;
import com.example.myapplication.activity.PiGraphView;
import com.example.myapplication.activity.GraphView;
import com.example.myapplication.MainActivity;
import android.content.Context;
import android.app.Activity;


import java.util.ArrayList;

public class TodayFragment extends BaseFragment {

    private GestureDetectorCompat gestureObject;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView checklist;
    private View mView;

    taskDB db;
    Button addButton;
    Button pieButton;
    public String date;

    private boolean isPrepared;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //加载布局
        mView = inflater.inflate(R.layout.fragment_today, container, false);
        //XXX初始化view的各控件
        isPrepared = true;
        lazyLoad();
        initView();

        return mView;
    }


    private void initView() {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        /**
         * 绑定控件，以及获取数据
         */
        db = PlanFragment.db;//绑定数据库
        checklist = (ListView) mView.findViewById(R.id.checklist);
        items = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
        checklist.setAdapter(itemsAdapter);
        date = getArguments().getString("date");//获取今日日期





        if(date == null)
            Log.d("todayDate" , "tdate为空");
        else
            Log.d("todayDate" , date);

        //date = PlanFragment.date;
        pieButton = (Button) mView.findViewById(R.id.pieButton);
        pieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PiGraphView.class);
                intent.putExtra("date", getDate());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
            }
        });

        /**
         * 从数据库获取数据显示添加到适配器中
         */
        ArrayList<String> tasksOfDay = db.getTasksOfDay(date);
        for (int i = 0; i < tasksOfDay.size(); i++) {
            itemsAdapter.add(tasksOfDay.get(i));
        }

        setupListViewListener();

        //gestureObject = new GestureDetectorCompat(this, new LearnGesture());

    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("pause" , "1111");
        //getActivity().finish();
    }


    //删除
    private void setupListViewListener() {
        checklist.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {

                        String delete_title = items.get(pos);
                        items.remove(pos);
                        delete_title=delete_title.replace("Task:","");
                        Log.d("ssssss",delete_title);
                        db.delete(delete_title,date);

                        itemsAdapter.notifyDataSetChanged();

                        return true;
                    }

                });
    }

    public void setDate(String newDate)
    {
        this.date = newDate;
    }

    public String getDate()
    {
        return  date;
    }


    @Override
    protected void lazyLoad()
    {
        if(!isPrepared || !isVisible) {
            return;
        }
        date = getArguments().getString("date");
        //填充各控件
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(getUserVisibleHint()) {
            date = getArguments().getString("date");
            initView();
            if(date == null)
                Log.d("visDate" , "tdate为空");
            else
                Log.d("visDate" , date);
        }

    }
}