package com.othershe.calendarview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.othershe.calendarview.listener.CalendarViewAdapter;
import com.othershe.calendarview.listener.OnMonthItemClickListener;
import com.othershe.calendarview.listener.OnMultiChooseListener;
import com.othershe.calendarview.listener.OnPagerChangeListener;
import com.othershe.calendarview.utils.CalendarUtil;
import com.othershe.calendarview.utils.SolarUtil;

public class CalendarView extends ViewPager {
    //记录当前PagerAdapter的position
    private int currentPosition;

    private OnPagerChangeListener pagerChangeListener;
    private OnMonthItemClickListener itemClickListener;
    private OnMultiChooseListener multiChooseListener;
    private CalendarViewAdapter calendarViewAdapter;
    private int item_layout;

    private int[] start = {1990, 1};
    private int[] end = {2025, 12};
    private int count;

    private int lastClickedDay;

    private CalendarPagerAdapter calendarPagerAdapter;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        lastClickedDay = SolarUtil.getCurrentDate()[2];
        //根据设定的日期范围计算日历的页数
        count = (end[0] - start[0]) * 12 + end[1] - start[1] + 1;
        calendarPagerAdapter = new CalendarPagerAdapter(attrs, count, start);
        setAdapter(calendarPagerAdapter);

        currentPosition = CalendarUtil.dateToPosition(SolarUtil.getCurrentDate()[0], SolarUtil.getCurrentDate()[1], start[0], start[1]);
        setCurrentItem(currentPosition, false);

        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                MonthView monthView = calendarPagerAdapter.getViews().get(position);
                monthView.refresh(lastClickedDay);
                currentPosition = position;
                if (pagerChangeListener != null) {
                    pagerChangeListener.onPagerChanged(CalendarUtil.positionToDate(position, start[0], start[1]));
                }
            }
        });
    }

    public void setLastClickDay(int day) {
        lastClickedDay = day;
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
     * 设置多选回调
     *
     * @param multiChooseListener
     */
    public void setOnMultiChooseListener(OnMultiChooseListener multiChooseListener) {
        this.multiChooseListener = multiChooseListener;
    }

    public OnMultiChooseListener getMultiChooseListener() {
        return multiChooseListener;
    }

    /**
     * 设置日期点击回调
     *
     * @param itemClickListener
     */
    public void setOnItemClickListener(OnMonthItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
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

    public void setOnCalendarViewAdapter(int item_layout, CalendarViewAdapter calendarViewAdapter) {
        this.item_layout = item_layout;
        this.calendarViewAdapter = calendarViewAdapter;
    }

    /**
     * 跳转到今天
     */
    public void today() {
        lastClickedDay = SolarUtil.getCurrentDate()[2];
        setCurrentItem(CalendarUtil.dateToPosition(SolarUtil.getCurrentDate()[0], SolarUtil.getCurrentDate()[1], start[0], start[1]), false);
    }

    /**
     * 跳转到指定日期
     *
     * @param year
     * @param month
     * @param day
     */
    public void toSpecifyDate(int year, int month, int day) {
        lastClickedDay = day;
        setCurrentItem(CalendarUtil.dateToPosition(year, month, start[0], start[1]), false);
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
}
