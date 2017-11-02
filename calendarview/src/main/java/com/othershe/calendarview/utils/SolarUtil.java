package com.othershe.calendarview.utils;

import android.text.TextUtils;

import java.util.Calendar;

public class SolarUtil {
    /**
     * 计算阳历节日
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getSolarHoliday(int year, int month, int day) {
        int md = (int) (month * Math.pow(10, day >= 10 ? 2 : 1) + day);
        String holiday = "";
        switch (md) {
            case 11:
                holiday = "元旦";
                break;
            case 214:
                holiday = "情人节";
                break;
            case 38:
                holiday = "妇女节";
                break;
            case 312:
                holiday = "植树节";
                break;
            case 41:
                holiday = "愚人节";
                break;
            case 51:
                holiday = "劳动节";
                break;
            case 54:
                holiday = "青年节";
                break;
            case 512:
                holiday = "护士节";
                break;
            case 61:
                holiday = "儿童节";
                break;
            case 71:
                holiday = "建党节";
                break;
            case 81:
                holiday = "建军节";
                break;
            case 910:
                holiday = "教师节";
                break;
            case 101:
                holiday = "国庆节";
                break;
            case 1111:
                holiday = "光棍节";
                break;
            case 1224:
                holiday = "平安夜";
                break;
            case 1225:
                holiday = "圣诞节";
                break;
        }

        if (!TextUtils.isEmpty(holiday)) {
            return holiday;
        }

        if (month == 4) {
            holiday = chingMingDay(year, day);
        } else if (month == 5) {
            if (day == motherFatherDay(year, month, 1)) {
                holiday = "母亲节";
            }
        } else if (month == 6) {
            if (day == motherFatherDay(year, month, 2)) {
                holiday = "父亲节";
            }
        }

        return holiday;
    }

    /**
     * 计算母亲节、父亲节是几号
     *
     * @param year
     * @param month
     * @param delta
     * @return
     */
    private static int motherFatherDay(int year, int month, int delta) {
        int f = getFirstWeekOfMonth(year, month - 1);
        f = f == 0 ? 7 : f;
        return 7 - f + 1 + 7 * delta;
    }

    public static String chingMingDay(int year, int day) {
        String holiday = "";
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
}
