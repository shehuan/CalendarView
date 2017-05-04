package com.othershe.calendarview.utils;

import com.othershe.calendarview.DateBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarUtil {
    /**
     * 获得当月显示的日期（上月 + 当月 + 下月）
     *
     * @param year  当前年份
     * @param month 当前月份
     * @return
     */
    public static List<DateBean> getMonthDate(int year, int month) {
        List<DateBean> datas = new ArrayList<>();
        int week = getFirstWeekOfMonth(year, month - 1);

        int lastYear;
        int lastMonth;
        if (month == 1) {
            lastMonth = 12;
            lastYear = year - 1;
        } else {
            lastMonth = month - 1;
            lastYear = year;
        }
        int lastMonthDays = getMonthDays(lastYear, lastMonth);//上个月总天数

        int currentMonthDays = getMonthDays(year, month);//当前月总天数

        int nextYear;
        int nextMonth;
        if (month == 12) {
            nextMonth = 1;
            nextYear = year + 1;
        } else {
            nextMonth = month + 1;
            nextYear = year;
        }

        for (int i = 0; i < week; i++) {
            datas.add(initDateBean(lastYear, lastMonth, lastMonthDays - week + 1 + i, 0));
        }

        for (int i = 0; i < currentMonthDays; i++) {
            datas.add(initDateBean(year, month, i + 1, 1));
        }

        for (int i = 0; i < 7 * getMonthRows(year, month) - currentMonthDays - week; i++) {
            datas.add(initDateBean(nextYear, nextMonth, i + 1, 2));
        }

        return datas;
    }

    private static DateBean initDateBean(int year, int month, int day, int type) {
        DateBean dateBean = new DateBean();
        dateBean.setSolar(year, month, day);
        dateBean.setLunar(LunarUtil.solarToLunar(year, month, day));
        dateBean.setType(type);

        if (type == 0) {
            dateBean.setHoliday(getHoliday(month, day - 1));
        } else {
            dateBean.setHoliday(getHoliday(month, day));
        }
        return dateBean;
    }


    /**
     * 获取国家法定节假日
     *
     * @param month
     * @param day
     * @return
     */
    public static String getHoliday(int month, int day) {
        String message = "";
        if (month == 1 && day == 1) {
            message = "元旦";
        } else if (month == 2 && day == 14) {
            message = "情人节";
        } else if (month == 3 && day == 8) {
            message = "妇女节";
        } else if (month == 3 && day == 12) {
            message = "植树节";
        } else if (month == 4 && day == 1) {
            message = "愚人节";
        } else if (month == 5 && day == 1) {
            message = "劳动节";
        } else if (month == 5 && day == 4) {
            message = "青年节";
        } else if (month == 5 && day == 12) {
            message = "护士节";
        } else if (month == 6 && day == 1) {
            message = "儿童节";
        } else if (month == 7 && day == 1) {
            message = "建党节";
        } else if (month == 8 && day == 1) {
            message = "建军节";
        } else if (month == 9 && day == 10) {
            message = "教师节";
        } else if (month == 10 && day == 1) {
            message = "国庆节";
        } else if (month == 11 && day == 11) {
            message = "光棍节";
        } else if (month == 12 && day == 24) {
            message = "平安夜";
        } else if (month == 12 && day == 25) {
            message = "圣诞节";
        }
        return message;
    }

    /**
     * 计算当月1号是周几
     *
     * @param year
     * @param month
     * @return
     */
    public static int getFirstWeekOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 计算指定月份的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 计算当前月需要显示几行
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthRows(int year, int month) {
        int count = getFirstWeekOfMonth(year, month - 1) + getMonthDays(year, month);
        return count % 7 == 0 ? count / 7 : (count / 7) + 1;
    }

    /**
     * 计算当前日期
     *
     * @return
     */
    public static int[] getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
    }

    /**
     * 根据ViewPager position 得到对应年月
     *
     * @param position
     * @return
     */
    public static int[] positionToDate(int position) {
        int year = position / 12 + 1900;
        int month = position % 12;

        if (position % 12 == 0) {
            year -= 1;
            month = 12;
        }

        return new int[]{year, month};
    }

    public static int dateToPosition(int year, int month) {
        return (year - 1900) * 12 + month;
    }
}
