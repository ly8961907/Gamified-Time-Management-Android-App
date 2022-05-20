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
import com.anychart.charts.Pie;
import com.example.myapplication.fragment.TodayFragment;
import com.example.myapplication.R;


import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.fragment.PlanFragment.db;

public class PiGraphView extends AppCompatActivity {

    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi_graph_view);

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
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.dailyPiGraph);
        anyChartView.setChart(pie);

        Button closePie = (Button) findViewById(R.id.backToDaily);
        closePie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PiGraphView.this, TodayFragment.class);


                //intent.putExtra("date", date);
                //startActivity(intent);
                //overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);

                Bundle args = new Bundle();
                TodayFragment todayFragment = new TodayFragment();
                args.putString("date",date);
                todayFragment.setArguments(args);

                finish();
            }
        });

    }

}