package com.othershe.calendarviewtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.othershe.calendarview.CalendarView;
import com.othershe.calendarview.DateBean;
import com.othershe.calendarview.listener.OnItemClickListener;
import com.othershe.calendarview.listener.OnPagerChangeListener;
import com.othershe.calendarview.utils.SolarUtil;


public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView title = (TextView) findViewById(R.id.title);
        calendarView = (CalendarView) findViewById(R.id.calendar);

        title.setText(SolarUtil.getCurrentDate()[0] + "年"
                + SolarUtil.getCurrentDate()[1] + "月"
                + SolarUtil.getCurrentDate()[2] + "日");

        calendarView.setOnPagerChangeListener(new OnPagerChangeListener() {
            @Override
            public void onPagerChanged(int[] date) {
                title.setText(date[0] + "年" + date[1] + "月");
            }
        });

        calendarView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(DateBean date) {
                title.setText(date.getSolar()[0] + "年" + date.getSolar()[1] + "月" + date.getSolar()[2] + "日");
            }
        });

        calendarView.setOnCalendarViewAdapter(10, null);

    }

    public void today(View view) {
        calendarView.today();
    }

    public void lastMonth(View view) {
        calendarView.lastMonth();
    }

    public void nextMonth(View view) {
        calendarView.nextMonth();
    }

    public void start(View view) {
        calendarView.toSpecifyDate(1900, 1, 1);
    }

    public void end(View view) {
        calendarView.toSpecifyDate(2049, 12, 1);
    }
}
