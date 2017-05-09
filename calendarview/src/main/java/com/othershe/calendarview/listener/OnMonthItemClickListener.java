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
     * @param flag 多选时flag=true代表选中数据，flag=false代表取消选中
     */
    void onMonthItemClick(View view, DateBean date, boolean flag);

    /**
     * 是否启用多选
     *
     * @return
     */
    boolean isMultiChoose();
}
