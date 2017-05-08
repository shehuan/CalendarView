package com.othershe.calendarview.listener;

import android.view.View;

import com.othershe.calendarview.DateBean;

public interface CalendarViewAdapter {
    void convertView(View view, DateBean date);
}
