package com.othershe.calendarview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LunarUtil {
    //1900-2049
    private final static int[] LUNAR_INFO = {
            0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
            0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
            0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
            0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
            0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,
            0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
            0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
            0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
            0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
            0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
            0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
            0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
            0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0
    };

    // 允许输入的最小年份
    private final static int MIN_YEAR = 1900;
    // 允许输入的最大年份
    private final static int MAX_YEAR = 2049;
    // 阳历日期计算起点
    private final static String START_DATE = "19000130";

    /**
     * 阳历转阴历
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String[] solarToLunar(int year, int month, int day) {
        int i;
        int temp = 0;
        int lunarYear;
        int lunarMonth; //农历月份
        int lunarDay; //农历当月第几天
        boolean leapMonthFlag = false;

        String solarDate = "" + year;

        if (month < 10) {
            solarDate = solarDate + "0" + month;
        } else {
            solarDate = solarDate + month;
        }

        if (day < 10) {
            solarDate = solarDate + "0" + day;
        } else {
            solarDate = solarDate + day;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date myDate = null;
        Date startDate = null;
        try {
            myDate = formatter.parse(solarDate);
            startDate = formatter.parse(START_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int offset = daysBetween(startDate, myDate);

        for (i = MIN_YEAR; i <= MAX_YEAR; i++) {
            temp = getYearDays(i);  //求当年农历年天数
            if (offset - temp < 1) {
                break;
            } else {
                offset -= temp;
            }
        }
        lunarYear = i;

        int leapMonth = getLeapMonth(lunarYear);//计算该年闰哪个月
        //设定当年是否有闰月
        boolean isLeapYear;
        if (leapMonth > 0) {
            isLeapYear = true;
        } else {
            isLeapYear = false;
        }

        for (i = 1; i <= 12; i++) {
            if (i == leapMonth + 1 && isLeapYear) {
                temp = getLeapMonthDays(lunarYear);
                isLeapYear = false;
                leapMonthFlag = true;
                i--;
            } else {
                try {
                    temp = getMonthDays(lunarYear, i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            offset -= temp;
            if (offset <= 0) {
                break;
            }
        }

        offset += temp;
        lunarMonth = i;
        lunarDay = offset;
        return new String[]{(leapMonthFlag & (lunarMonth == leapMonth) ? "闰" : "") + getLunarMonth(lunarMonth),
                getLunarDay(lunarDay)};
    }

    /**
     * 计算阴历{@code lunarYeay}年{@code month}月的天数
     *
     * @param lunarYear 阴历年
     * @param month     阴历月
     * @return 该月天数
     */
    private static int getMonthDays(int lunarYear, int month) throws Exception {
        if ((month > 31) || (month < 0)) {
            throw (new Exception("月份有错！"));
        }
        int bit = 1 << (16 - month);
        if (((LUNAR_INFO[lunarYear - 1900] & 0x0FFFF) & bit) == 0) {
            return 29;
        } else {
            return 30;
        }
    }

    /**
     * 计算两个阳历日期相差的天数。
     *
     * @param startDate 开始时间
     * @param endDate   截至时间
     * @return 天数
     */
    private static int daysBetween(Date startDate, Date endDate) {
        int days = 0;
        //将转换的两个时间对象转换成Calendar对象
        Calendar can1 = Calendar.getInstance();
        can1.setTime(startDate);
        Calendar can2 = Calendar.getInstance();
        can2.setTime(endDate);
        //拿出两个年份
        int year1 = can1.get(Calendar.YEAR);
        int year2 = can2.get(Calendar.YEAR);
        //天数

        Calendar can = null;
        //如果can1 < can2
        //减去小的时间在这一年已经过了的天数
        //加上大的时间已过的天数
        if (can1.before(can2)) {
            days -= can1.get(Calendar.DAY_OF_YEAR);
            days += can2.get(Calendar.DAY_OF_YEAR);
            can = can1;
        } else {
            days -= can2.get(Calendar.DAY_OF_YEAR);
            days += can1.get(Calendar.DAY_OF_YEAR);
            can = can2;
        }
        for (int i = 0; i < Math.abs(year2 - year1); i++) {
            //获取小的时间当前年的总天数
            days += can.getActualMaximum(Calendar.DAY_OF_YEAR);
            //再计算下一年。
            can.add(Calendar.YEAR, 1);
        }
        return days;
    }

    /**
     * 计算阴历年的总天数
     *
     * @param year 阴历年
     * @return 总天数
     */
    private static int getYearDays(int year) {
        int sum = 29 * 12;
        for (int i = 0x8000; i >= 0x8; i >>= 1) {
            if ((LUNAR_INFO[year - 1900] & 0xfff0 & i) != 0) {
                sum++;
            }
        }
        return sum + getLeapMonthDays(year);
    }

    /**
     * 计算阴历年闰月多少天
     *
     * @param year 阴历年
     * @return 天数
     */
    private static int getLeapMonthDays(int year) {
        if (getLeapMonth(year) != 0) {
            if ((LUNAR_INFO[year - 1900] & 0xf0000) == 0) {
                return 29;
            } else {
                return 30;
            }
        } else {
            return 0;
        }
    }

    /**
     * 计算阴历年闰哪个月 1-12 , 没闰传回 0
     *
     * @param year 阴历年
     * @return 月份
     */
    private static int getLeapMonth(int year) {
        return LUNAR_INFO[year - 1900] & 0xf;
    }

    private static String getLunarMonth(int month) {
        String[] monthStrs = new String[]{"", "正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月"};
        return monthStrs[month];
    }

    private static String getLunarDay(int day) {
        if (day == 10) {
            return "初十";
        } else if (day == 20) {
            return "二十";
        } else if (day == 30) {
            return "三十";
        }
        String[] dayStrs = new String[]{"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String str1 = "";
        int d1 = day / 10;
        if (d1 == 0) {
            str1 = "初";
        } else if (d1 == 1) {
            str1 = "十";
        } else if (d1 == 2) {
            str1 = "廿";
        } else if (d1 == 3) {
            str1 = "三";
        }
        return str1 + dayStrs[day % 10];
    }
}
