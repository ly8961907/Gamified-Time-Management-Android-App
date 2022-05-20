package com.example.myapplication.fragment;


import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import com.example.myapplication.R;
import com.example.myapplication.activity.GraphView;
import com.example.myapplication.fragment.task.taskDB;
import com.example.myapplication.fragment.PlanFragment;
import com.example.myapplication.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class StatisticFragment extends BaseFragment {

    private View mView;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView weeklist;
    private boolean isPrepared;
    private static boolean already = false;
    AnyChartView anyChartView;
    Cartesian cartesian;
    taskDB db;
    public String date;
    Button graphButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //加载布局
        mView = inflater.inflate(R.layout.fragment_statistic, container, false);
        isPrepared = true;
        lazyLoad();
        initView();

        return mView;
    }

    private void initView() {
        db = PlanFragment.db;


        /**
         * 绑定组件
         */
        weeklist = (ListView) mView.findViewById(R.id.weeklist);
        items = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
        weeklist.setAdapter(itemsAdapter);
        date =  getArguments().getString("date");

        if(date == null)
            Log.d("statisticDate" , "sDate为空");
        else
            Log.d("statisticDate" , date);
        //date = PlanFragment.date;

        for(int j = 0; j < 7; j++) {
            Integer parsedDate = Integer.parseInt(date);
            if(j!=0)//把今天算进去
                parsedDate++;
            date = String.valueOf(parsedDate);

            // -----------------------------------
            // 调整月闰年等情况的边界值
            // -----------------------------------

            String monthNum = date.substring(4,6);
            String dayNum = date.substring(6);
            String yearNum = date.substring(0,4);



            boolean leapYear = false;
            int nextLeapYear = 2024;

            while (Integer.parseInt(yearNum) > nextLeapYear) {
                nextLeapYear = nextLeapYear + 4;
            }
            if(nextLeapYear == Integer.parseInt(yearNum))
                leapYear = true;

            if(monthNum.equals("01") && dayNum.equals("32")){
                monthNum = "02";
                dayNum = "01";
            }
            if(monthNum.equals("02") && dayNum.equals("29") && leapYear == false){
                monthNum = "03";
                dayNum = "01";
            }
            if(monthNum.equals("02") && dayNum.equals("30") && leapYear == true){
                monthNum = "03";
                dayNum = "01";
            }
            if(monthNum.equals("03") && dayNum.equals("32")){
                monthNum = "04";
                dayNum = "01";
            }
            if(monthNum.equals("04") && dayNum.equals("31")){
                monthNum = "05";
                dayNum = "01";
            }
            if(monthNum.equals("05") && dayNum.equals("32")){
                monthNum = "06";
                dayNum = "01";
            }
            if(monthNum.equals("06") && dayNum.equals("31")){
                monthNum = "06";
                dayNum = "01";
            }
            if(monthNum.equals("07") && dayNum.equals("32")){
                monthNum = "08";
                dayNum = "01";
            }
            if(monthNum.equals("08") && dayNum.equals("32")){
                monthNum = "09";
                dayNum = "01";
            }
            if(monthNum.equals("09") && dayNum.equals("31")){
                monthNum = "10";
                dayNum = "01";
            }
            if(monthNum.equals("10") && dayNum.equals("32")){
                monthNum = "11";
                dayNum = "01";
            }
            if(monthNum.equals("11") && dayNum.equals("31")){
                monthNum = "12";
                dayNum = "01";
            }
            if(monthNum.equals("12") && dayNum.equals("32")){
                monthNum = "01";
                dayNum = "01";
                yearNum = Integer.toString(Integer.parseInt(yearNum) + 1);
            }

            date = yearNum + monthNum + dayNum;


            //Log.d("afterDate", date);



            //  -----------------------------------
            //  -----------------------------------
            //  -----------------------------------

            ArrayList<String> tasksOfWeek = db.getTasksOfWeek(date);

            for (int i = 0; i < tasksOfWeek.size(); i++) {
                itemsAdapter.add(tasksOfWeek.get(i));
            }
        }

        date = getArguments().getString("date");
        //date = PlanFragment.date;
        if(date == null)
            Log.d("statisticDate" , "sDate为空");
        else
            Log.d("statisticDate" , date);

        graphButton = (Button) mView.findViewById(R.id.graphButton);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GraphView.class);
                intent.putExtra("date", getDate());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
            }
        });



    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void sedate(String newDate)
    {
        this.date = newDate;
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

        if(getUserVisibleHint()&&isPrepared) {
            date = getArguments().getString("date");
            initView();
            if(date == null)
                Log.d("visDate" , "date为空");
            else
                Log.v("visDate" , date);
        }

    }
    private  void  setAnychart()
    {
        //绑定统计图
        AnyChartView anyChartView = mView.findViewById(R.id.columnView1);
        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();

        //yoink some tasks bruv
        ArrayList<String> titlesOfDay = db.getTitlesOfDay(date);
        ArrayList<Integer> HoursOfDay = db.getHoursOfDay(date);
        //populating dataset for the chart

        for(int i=0;i<HoursOfDay.size();i++){
            Log.d("titlefromlist",titlesOfDay.get(i));
            Log.d("hoursfromlist",Integer.toString(HoursOfDay.get(i)));
            data.add(new ValueDataEntry(titlesOfDay.get(i),HoursOfDay.get(i)));//x和y
        }

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)//锚点
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: } Tyler");

        cartesian.animation(true);
        cartesian.title("Time to Completion Per Task");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: } Hours");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Task");
        cartesian.yAxis(0).title("Hours Remaining");


        anyChartView.setChart(cartesian);
    }
    public String getDate()
    {
        return  date;
    }
}