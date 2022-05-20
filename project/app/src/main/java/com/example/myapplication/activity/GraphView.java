package com.example.myapplication.activity;


import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.example.myapplication.fragment.StatisticFragment;
import com.example.myapplication.R;


import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.fragment.PlanFragment.db;

public class GraphView extends AppCompatActivity  {

    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);

        date = getIntent().getStringExtra("date");

        ArrayList<String> titlesOfDay = db.getTitlesOfDay(date);
        ArrayList<Integer> HoursOfDay = db.getHoursOfDay(date);
        for(int i=0;i<HoursOfDay.size();i++) {
            Integer temp1 = HoursOfDay.get(i);
            String temp2 = titlesOfDay.get(i);
            Log.d("tasksofDaytest", Integer.toString(temp1));
            Log.d("HoursTEst", temp2);
        }


        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();

        for(int i=0;i<HoursOfDay.size();i++){
            data.add(new ValueDataEntry(titlesOfDay.get(i),HoursOfDay.get(i)));
        }


        int totalHours=0;
        for (int i=0;i<HoursOfDay.size();i++){
            totalHours=totalHours+HoursOfDay.get(i);
        }
        if(totalHours<24){
            int remainingHours=0;
            remainingHours=24-totalHours;
            data.add(new ValueDataEntry("Free Time",remainingHours));
        }


        pie.data(data);
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.dailyPiGraph1);
        anyChartView.setChart(pie);


        setAnychart();



        Button closePie = (Button) findViewById(R.id.backToDaily1);
        closePie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GraphView.this, StatisticFragment.class);

                //intent.putExtra("date", date);
                //startActivity(intent);
                //overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);

                Bundle args = new Bundle();
                StatisticFragment statisticFragment = new StatisticFragment();
                args.putString("date",date);
                statisticFragment.setArguments(args);

                finish();
            }
        });

    }
    private  void  setAnychart()
    {
        //绑定统计图
        AnyChartView anyChartView = findViewById(R.id.columnView1);
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
}