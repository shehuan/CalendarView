package com.othershe.calendarview.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;

import com.othershe.calendarview.bean.DateBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CalendarUtil {
    /**
     * 获得当月显示的日期（上月 + 当月 + 下月）
     *
     * @param year  当前年份
     * @param month 当前月份
     * @return
     */
    public static List<DateBean> getMonthDate(int year, int month, Map<String, String> map) {
        List<DateBean> datas = new ArrayList<>();
        int week = SolarUtil.getFirstWeekOfMonth(year, month - 1);

        int lastYear;
        int lastMonth;
        if (month == 1) {
            lastMonth = 12;
            lastYear = year - 1;
        } else {
            lastMonth = month - 1;
            lastYear = year;
        }
        int lastMonthDays = SolarUtil.getMonthDays(lastYear, lastMonth);//上个月总天数

        int currentMonthDays = SolarUtil.getMonthDays(year, month);//当前月总天数

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
            datas.add(initDateBean(lastYear, lastMonth, lastMonthDays - week + 1 + i, 0, map));
        }

        for (int i = 0; i < currentMonthDays; i++) {
            datas.add(initDateBean(year, month, i + 1, 1, map));
        }

        for (int i = 0; i < 7 * getMonthRows(year, month) - currentMonthDays - week; i++) {
            datas.add(initDateBean(nextYear, nextMonth, i + 1, 2, map));
        }

        return datas;
    }

    private static DateBean initDateBean(int year, int month, int day, int type, Map<String, String> map) {
        DateBean dateBean = new DateBean();
        dateBean.setSolar(year, month, day);

        if (map == null) {
            String[] temp = LunarUtil.solarToLunar(year, month, day);
            dateBean.setLunar(new String[]{temp[0], temp[1]});
            dateBean.setLunarHoliday(temp[2]);
        } else {
            if (map.containsKey(year + "." + month + "." + day)) {
                dateBean.setLunar(new String[]{"", map.get(year + "." + month + "." + day), ""});
            } else {
                dateBean.setLunar(new String[]{"", "", ""});
            }
        }

        dateBean.setType(type);
        dateBean.setTerm(LunarUtil.getTermString(year, month - 1, day));

        if (type == 0) {
            dateBean.setSolarHoliday(SolarUtil.getSolarHoliday(year, month, day - 1));
        } else {
            dateBean.setSolarHoliday(SolarUtil.getSolarHoliday(year, month, day));
        }
        return dateBean;
    }

    public static DateBean getDateBean(int year, int month, int day) {
        return initDateBean(year, month, day, 1, null);
    }

    /**
     * 计算当前月需要显示几行
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthRows(int year, int month) {
        int items = SolarUtil.getFirstWeekOfMonth(year, month - 1) + SolarUtil.getMonthDays(year, month);
        int rows = items % 7 == 0 ? items / 7 : (items / 7) + 1;
        if (rows == 4) {
            rows = 5;
        }
        return rows;
    }

    /**
     * 根据ViewPager position 得到对应年月
     *
     * @param position
     * @return
     */
    public static int[] positionToDate(int position, int startY, int startM) {
        int year = position / 12 + startY;
        int month = position % 12 + startM;

        if (month > 12) {
            month = month % 12;
            year = year + 1;
        }

        return new int[]{year, month};
    }

    /**
     * 根据年月得到ViewPager position
     *
     * @param year
     * @param month
     * @return
     */
    public static int dateToPosition(int year, int month, int startY, int startM) {
        return (year - startY) * 12 + month - startM;
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

    public static int[] strToArray(String str) {
        if (!TextUtils.isEmpty(str)) {
            String[] strArray = str.split("\\.");
            int[] result = new int[strArray.length];
            for (int i = 0; i < strArray.length; i++) {
                result[i] = Integer.valueOf(strArray[i]);
            }
            return result;
        }
        return null;
    }

    public static long dateToMillis(int[] date) {
        int day = date.length == 2 ? 1 : date[2];
        Calendar calendar = Calendar.getInstance();
        calendar.set(date[0], date[1], day);
        return calendar.getTimeInMillis();
    }

    public static int getPxSize(Context context, int size) {
        return size * context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getTextSize1(Context context, int size) {
        return (int) (size * context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int getTextSize(Context context, int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, context.getResources().getDisplayMetrics());

    }
}
