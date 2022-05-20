package com.example.myapplication.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GestureDetectorCompat;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.myapplication.R;
import com.example.myapplication.fragment.task.taskDB;
import com.example.myapplication.activity.addDueDate;
import com.example.myapplication.MainActivity;

public class PlanFragment extends BaseFragment  {
    private View mView;

    public static taskDB db;
    public static String date;
    private GestureDetectorCompat gestureObject;
    private boolean isPrepared;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //加载布局
        mView = inflater.inflate(R.layout.fragment_plan, container, false);
        isPrepared = true;
        lazyLoad();
        initView();

        return mView;
    }



    private void initView() {

        //notification channel
        createNotificationChannel();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        date = df.format(c);
        Log.d("planDate" , date);
        CalendarView calendarView = (CalendarView) mView.findViewById(R.id.calendar);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                month++;
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), ""+month+"-"+dayOfMonth+
                        "-"+year, Toast.LENGTH_LONG);
                if(month < 10 && dayOfMonth < 10){
                    date = "" + year + "0" + month + "0" + dayOfMonth;
                }
                else if (month < 10){
                    date = "" + year + "0" + month + dayOfMonth;
                }
                else if (dayOfMonth < 10){
                    date = "" + year + month + "0" + dayOfMonth;
                }
                else{
                    date = "" + year + month + dayOfMonth;
                }

                //((MainActivity)getActivity()).jumpTodayFragment();

                //Bundle args = new Bundle();
                //TodayFragment todayFragment = new TodayFragment();
                //args.putString("date",date);
                //todayFragment.setArguments(args);
                ((MainActivity)getActivity()).jumpTodayFragment();
                //todayFragment.setDate(date);
                toast.show();
            }
        });

        db = new taskDB(getContext(), db.DB_NAME, null, 1);

        //checking for things due today and add notifcation
        ArrayList<String> tasksOfDay = db.getTasksOfDay(date);
        if(tasksOfDay != null) {

            //有问题
            addNotification(tasksOfDay);
        }

        FloatingActionButton fab = mView.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), addDueDate.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void addNotification(ArrayList<String> tasksOfDay){
        //build notification
        StringBuilder content = new StringBuilder("");
        String title = "Smart Calendar";
        if(tasksOfDay.size() == 1){
            content.append("You have one task to do today: " + "\n" + tasksOfDay.get(0));
        }
        else{
            content.append("You have tasks to do today: " +"\n");
            for (int i = 0; i < tasksOfDay.size(); i++) {
                content.append(tasksOfDay.get(i) + " " + "\n");
            }
        }
        String CHANNEL_ID = "my_channel_01";

        Intent intent = new Intent(getActivity(), PlanFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.start)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        // notificationId is a unique int for each notification that you must define
        //有问题
        //notificationManager.notify(1, builder.build());

    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "New Channel";
            String description = "A new channel for notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_channel_01", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
        //date = getArguments().getString("date");
        //填充各控件
    }
}