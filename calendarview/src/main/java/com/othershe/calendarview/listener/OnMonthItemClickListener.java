package com.othershe.calendarview.listener;

import android.view.View;

import com.othershe.calendarview.DateBean;

/**
 * 日期点击接口
 */
public interface OnMonthItemClickListener {
    /**
     * @param view
     * @param date
     */
    void onMonthItemClick(View view, DateBean date);
}
