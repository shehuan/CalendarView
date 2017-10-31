package com.othershe.calendarview.listener;

import android.view.View;

import com.othershe.calendarview.bean.DateBean;

/**
 * 多选接口
 */
public interface OnMonthItemChooseListener {
    /**
     * @param view
     * @param date
     * @param flag 多选时flag=true代表选中数据，flag=false代表取消选中
     */
    void onMonthItemChoose(View view, DateBean date, boolean flag);
}
