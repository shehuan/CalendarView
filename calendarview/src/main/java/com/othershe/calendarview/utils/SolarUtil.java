package com.othershe.calendarview.utils;

import java.util.Calendar;

public class SolarUtil {
    //母亲节5月的第二个星期日。父亲节6月的第三个星期日

    /**
     * 计算阳历节日
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getSolarHoliday(int year, int month, int day) {
        String holiday = "";
        if (month == 1 && day == 1) {
            holiday = "元旦";
        } else if (month == 2 && day == 14) {
            holiday = "情人节";
        } else if (month == 3 && day == 8) {
            holiday = "妇女节";
        } else if (month == 3 && day == 12) {
            holiday = "植树节";
        } else if (month == 4 && day == 1) {
            holiday = "愚人节";
        } else if (month == 4) {
            if (day >= 4 && day <= 6) {
                if (year <= 1999) {
                    int compare = (int) (((year - 1900) * 0.2422 + 5.59) - ((year - 1900) / 4));
                    if (compare == day) {
                        holiday = "清明节";
                    }
                } else {
                    int compare = (int) (((year - 2000) * 0.2422 + 4.81) - ((year - 2000) / 4));
                    if (compare == day) {
                        holiday = "清明节";
                    }
                }
            }
        } else if (month == 5 && day == 1) {
            holiday = "劳动节";
        } else if (month == 5 && day == 4) {
            holiday = "青年节";
        } else if (month == 5 && day == 12) {
            holiday = "护士节";
        } else if (month == 6 && day == 1) {
            holiday = "儿童节";
        } else if (month == 7 && day == 1) {
            holiday = "建党节";
        } else if (month == 8 && day == 1) {
            holiday = "建军节";
        } else if (month == 9 && day == 10) {
            holiday = "教师节";
        } else if (month == 10 && day == 1) {
            holiday = "国庆节";
        } else if (month == 11 && day == 11) {
            holiday = "光棍节";
        } else if (month == 12 && day == 24) {
            holiday = "平安夜";
        } else if (month == 12 && day == 25) {
            holiday = "圣诞节";
        }
        return holiday;
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
     * 计算当前日期
     *
     * @return
     */
    public static int[] getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
    }
}
