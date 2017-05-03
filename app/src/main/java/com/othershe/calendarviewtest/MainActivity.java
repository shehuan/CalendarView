package com.othershe.calendarviewtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.othershe.calendarview.CalendarUtil;
import com.othershe.calendarview.DateBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.text);
        StringBuilder stringBuilder = new StringBuilder();

        List<DateBean> l = CalendarUtil.getMonthDate(2017, 5);
        for (DateBean dateBean : l) {
            stringBuilder.append(dateBean.getYear() + "-" + dateBean.getMonth() + "-" + dateBean.getDay() + "-" + dateBean.getHoliday() + "\n");
        }

        textView.setText(stringBuilder.toString());

//        Log.e("llll", CalendarUtil.getCurrentDate()[0] + "-" + CalendarUtil.getCurrentDate()[1] + "-" + CalendarUtil.getCurrentDate()[2]);
    }
}
