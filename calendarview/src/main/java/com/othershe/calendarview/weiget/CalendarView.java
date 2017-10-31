package com.othershe.calendarview.weiget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.othershe.calendarview.R;
import com.othershe.calendarview.bean.AttrsBean;
import com.othershe.calendarview.bean.DateBean;
import com.othershe.calendarview.listener.CalendarViewAdapter;
import com.othershe.calendarview.listener.OnMonthItemChooseListener;
import com.othershe.calendarview.listener.OnMonthItemClickListener;
import com.othershe.calendarview.listener.OnPagerChangeListener;
import com.othershe.calendarview.utils.CalendarUtil;
import com.othershe.calendarview.utils.SolarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CalendarView extends ViewPager {
    //记录当前PagerAdapter的position
    private int currentPosition;

    private OnPagerChangeListener pagerChangeListener;
    private OnMonthItemClickListener itemClickListener;
    private OnMonthItemChooseListener itemChooseListener;
    private CalendarViewAdapter calendarViewAdapter;
    private int item_layout;

    private int[] dateStart;//日历的开始年、月
    private int[] dateEnd;//日历的结束年、月
    private int[] dateInit;//默认展示、选中的日期（年、月、日）

    private int count;//ViewPager的页数
    private int[] lastClickDate = new int[2];//上次点击的日期
    private SparseArray<HashSet<Integer>> chooseDate = new SparseArray<>();//记录多选时全部选中的日期

    private List<int[]> multiDateInitList;

    private CalendarPagerAdapter calendarPagerAdapter;

    private AttrsBean mAttrsBean;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAttrsBean = new AttrsBean();
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.CalendarView_show_last_next) {
//                showLastNext = ta.getBoolean(attr, true);
                mAttrsBean.setShowLastNext(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_show_lunar) {
//                showLunar = ta.getBoolean(attr, true);
                mAttrsBean.setShowLunar(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_show_holiday) {
//                showHoliday = ta.getBoolean(attr, true);
                mAttrsBean.setShowHoliday(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_show_term) {
//                showTerm = ta.getBoolean(attr, true);
                mAttrsBean.setShowTerm(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_disable_before) {
//                disableBefore = ta.getBoolean(attr, false);
                mAttrsBean.setDisableBefore(ta.getBoolean(attr, false));
            } else if (attr == R.styleable.CalendarView_switch_choose) {
//                switchChoose = ta.getBoolean(attr, true);
                mAttrsBean.setSwitchChoose(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_color_solar) {
//                colorSolar = ta.getColor(attr, colorSolar);
                mAttrsBean.setColorSolar(ta.getColor(attr, mAttrsBean.getColorSolar()));
            } else if (attr == R.styleable.CalendarView_size_solar) {
//                sizeSolar = ta.getInteger(R.styleable.CalendarView_size_solar, sizeSolar);
                mAttrsBean.setSizeSolar(CalendarUtil.getTextSize(context, ta.getInteger(R.styleable.CalendarView_size_solar, mAttrsBean.getSizeSolar())));
            } else if (attr == R.styleable.CalendarView_color_lunar) {
//                colorLunar = ta.getColor(attr, colorLunar);
                mAttrsBean.setColorLunar(ta.getColor(attr, mAttrsBean.getColorLunar()));
            } else if (attr == R.styleable.CalendarView_size_lunar) {
//                sizeLunar = ta.getDimensionPixelSize(R.styleable.CalendarView_size_lunar, sizeLunar);
                mAttrsBean.setSizeLunar(CalendarUtil.getTextSize(context, ta.getDimensionPixelSize(R.styleable.CalendarView_size_lunar, mAttrsBean.getSizeLunar())));
            } else if (attr == R.styleable.CalendarView_color_holiday) {
//                colorHoliday = ta.getColor(attr, colorHoliday);
                mAttrsBean.setColorHoliday(ta.getColor(attr, mAttrsBean.getColorHoliday()));
            } else if (attr == R.styleable.CalendarView_color_choose) {
//                colorChoose = ta.getColor(attr, colorChoose);
                mAttrsBean.setColorChoose(ta.getColor(attr, mAttrsBean.getColorChoose()));
            } else if (attr == R.styleable.CalendarView_day_bg) {
//                dayBg = ta.getResourceId(attr, dayBg);
                mAttrsBean.setDayBg(ta.getResourceId(attr, mAttrsBean.getDayBg()));
            }
        }

        ta.recycle();

        dateStart = new int[]{1900, 1};
        dateEnd = new int[]{2049, 12};
        dateInit = SolarUtil.getCurrentDate();

        mAttrsBean.setDateStart(dateStart);
        mAttrsBean.setDateEnd(dateEnd);
        mAttrsBean.setDateInit(dateInit);
    }

    public void init() {
        //根据设定的日期范围计算日历的页数
        count = (dateEnd[0] - dateStart[0]) * 12 + dateEnd[1] - dateStart[1] + 1;
        calendarPagerAdapter = new CalendarPagerAdapter(count);
        calendarPagerAdapter.setAttrsBean(mAttrsBean);
        calendarPagerAdapter.setOnCalendarViewAdapter(item_layout, calendarViewAdapter);

        setAdapter(calendarPagerAdapter);

        currentPosition = CalendarUtil.dateToPosition(dateInit[0], dateInit[1], dateStart[0], dateStart[1]);
        lastClickDate[0] = currentPosition;
        lastClickDate[1] = dateInit[2];

        setLastChooseDate(dateInit[2], true);//因为有默认选中日期，所以需要此操作
        setCurrentItem(currentPosition, false);

        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                refreshMonthView(position);
                currentPosition = position;
                if (pagerChangeListener != null) {
                    int[] date = CalendarUtil.positionToDate(position, dateStart[0], dateStart[1]);
                    pagerChangeListener.onPagerChanged(new int[]{date[0], date[1], lastClickDate[1]});
                }
            }
        });
    }

    /**
     * 计算 ViewPager 高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int calendarHeight;
        if (getAdapter() != null) {
            MonthView view = (MonthView) getChildAt(0);
            if (view != null) {
                calendarHeight = view.getMeasuredHeight();
                setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(calendarHeight, MeasureSpec.EXACTLY));
            }
        }
    }

    /**
     * 刷新MonthView
     *
     * @param position
     */
    private void refreshMonthView(int position) {
        MonthView monthView = calendarPagerAdapter.getViews().get(position);
        if (itemChooseListener != null) {
            if (chooseDate.get(position) != null)
                monthView.multiChooseRefresh(chooseDate.get(position));
        } else {
            boolean flag = (!mAttrsBean.isSwitchChoose() && lastClickDate[0] == position) || mAttrsBean.isSwitchChoose();
            monthView.refresh(lastClickDate[1], flag);
        }
    }

    /**
     * 设置上次点击的日期
     *
     * @param day
     */
    public void setLastClickDay(int day) {
        lastClickDate[0] = currentPosition;
        lastClickDate[1] = day;
    }

    /**
     * 设置多选时选中的日期
     *
     * @param day
     * @param flag 多选时flag=true代表选中数据，flag=false代表取消选中
     */
    public void setLastChooseDate(int day, boolean flag) {
        HashSet<Integer> days = chooseDate.get(currentPosition);
        if (flag) {
            if (days == null) {
                days = new HashSet<>();
                chooseDate.put(currentPosition, days);
            }
            days.add(day);
        } else {
            days.remove(day);
        }
    }

    /**
     * 设置日期点击回调
     *
     * @param itemClickListener
     */
    public void setOnItemClickListener(OnMonthItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OnMonthItemChooseListener getItemChooseListener() {
        return itemChooseListener;
    }

    /**
     * 设置日期多选回调
     *
     * @param itemChooseListener
     */
    public void setOnMonthItemChooseListener(OnMonthItemChooseListener itemChooseListener) {
        this.itemChooseListener = itemChooseListener;
    }

    public OnMonthItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    /**
     * 设置月份切换回调
     *
     * @param pagerChangeListener
     */
    public void setOnPagerChangeListener(OnPagerChangeListener pagerChangeListener) {
        this.pagerChangeListener = pagerChangeListener;
    }

    /**
     * 设置自定义日期样式
     *
     * @param item_layout         自定义的日期item布局
     * @param calendarViewAdapter 解析item的接口
     */
    public void setOnCalendarViewAdapter(int item_layout, CalendarViewAdapter calendarViewAdapter) {
        this.item_layout = item_layout;
        this.calendarViewAdapter = calendarViewAdapter;

        init();
    }

    /**
     * 跳转到今天
     */
    public void today() {
        int destPosition = CalendarUtil.dateToPosition(SolarUtil.getCurrentDate()[0], SolarUtil.getCurrentDate()[1], dateStart[0], dateStart[1]);
        lastClickDate[0] = destPosition;
        lastClickDate[1] = SolarUtil.getCurrentDate()[2];
        if (destPosition == currentPosition) {
            refreshMonthView(destPosition);
        } else {
            setCurrentItem(destPosition, false);
        }
    }

    /**
     * 跳转到指定日期
     *
     * @param year
     * @param month
     * @param day
     */
    public void toSpecifyDate(int year, int month, int day) {
        int destPosition = CalendarUtil.dateToPosition(year, month, dateStart[0], dateStart[1]);
        if (!mAttrsBean.isSwitchChoose() && day != 0) {
            lastClickDate[0] = destPosition;
        }
        lastClickDate[1] = day != 0 ? day : lastClickDate[1];
        setCurrentItem(destPosition, false);
    }

    /**
     * 跳转到下个月
     */
    public void nextMonth() {
        if (currentPosition < count - 1)
            setCurrentItem(++currentPosition, false);
    }

    /**
     * 跳转到上个月
     */
    public void lastMonth() {
        if (currentPosition > 0)
            setCurrentItem(--currentPosition, false);
    }

    /**
     * 跳转到上一年的当前月
     */
    public void lastYear() {
        if (currentPosition - 12 >= 0) {
            setCurrentItem(currentPosition -= 12, false);
        }
    }

    /**
     * 跳转到下一年的当前月
     */
    public void nextYear() {
        if (currentPosition + 12 <= count) {
            setCurrentItem(currentPosition += 12, false);
        }
    }

    /**
     * 跳转到日历的开始年月
     */
    public void toStart() {
        toSpecifyDate(dateStart[0], dateStart[1], 0);
    }

    /**
     * 跳转到日历的结束年月
     */
    public void toEnd() {
        toSpecifyDate(dateEnd[0], dateEnd[1], 0);
    }

    /**
     * 得到默认选中的日期
     *
     * @return
     */
    public DateBean getDateInit() {
        return CalendarUtil.getDateBean(dateInit[0], dateInit[1], dateInit[2]);
    }

    /**
     * 将指定日期的农历替换成对应文字
     */
    public CalendarView setSpecifyMap(HashMap<String, String> map) {
        mAttrsBean.setSpecifyMap(map);
        return this;
    }

    /**
     * 设置初始选中的日期，是否标记默认日期
     *
     * @param dateStr
     * @return
     */
    public CalendarView setDateInit(String dateStr, boolean showDateInit) {
        dateInit = CalendarUtil.strToArray(dateStr);
        if (dateInit == null) {
            dateInit = SolarUtil.getCurrentDate();
        }
        mAttrsBean.setDateInit(dateInit);
        mAttrsBean.setShowDateInit(showDateInit);
        return this;
    }

    /**
     * 设置日历的开始年月、结束年月
     *
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public CalendarView setDateStartEnd(String dateStart, String dateEnd) {
        this.dateStart = CalendarUtil.strToArray(dateStart);
        if (dateStart == null) {
            this.dateStart = new int[]{1900, 1};
        }
        this.dateEnd = CalendarUtil.strToArray(dateEnd);
        if (dateEnd == null) {
            this.dateEnd = new int[]{2049, 12};
        }
        mAttrsBean.setDateStart(this.dateStart);
        mAttrsBean.setDateEnd(this.dateEnd);
        return this;
    }

    /**
     * 设置多选时默认选中的日期集合
     *
     * @param dates
     * @return
     */
    public CalendarView setMultiDateInit(List<String> dates) {
        multiDateInitList = new ArrayList<>();
        for (String date : dates) {
            multiDateInitList.add(CalendarUtil.strToArray(date));
        }
        return this;
    }
}
