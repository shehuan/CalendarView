package com.othershe.calendarview.listener;

import com.othershe.calendarview.DateBean;

/**
 * 日期点击接口
 */
public interface OnMonthItemClickListener {
    /**
     * 点击当月
     *
     * @param date
     */
    void onCurrentMonthClick(DateBean date);

    /**
     * 点击上月
     *
     * @param date
     */
    void onLastMonthClick(DateBean date);

    /**
     * 点击下月
     *
     * @param date
     */
    void onNextMonthClick(DateBean date);
}
