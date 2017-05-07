package com.othershe.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
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

    private int[] startDate;//日历的开始年、月
    private int[] endDate;//日历的结束年、月
    private int[] initDate;//默认展示、选中的日期（年、月、日）
    private int count;//ViewPager的页数

    private int lastClickedDay;//上次点击的日期

    private CalendarPagerAdapter calendarPagerAdapter;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        String startDateStr = ta.getString(R.styleable.CalendarView_start_date);
        String endDateStr = ta.getString(R.styleable.CalendarView_end_date);
        String initDateStr = ta.getString(R.styleable.CalendarView_init_date);

        startDate = CalendarUtil.strToArray(startDateStr);
        if (startDate == null) {
            startDate = new int[]{1900, 1};
        }
        endDate = CalendarUtil.strToArray(endDateStr);
        if (endDate == null) {
            endDate = new int[]{2049, 12};
        }

        initDate = CalendarUtil.strToArray(initDateStr);
        if (initDate == null) {
            initDate = SolarUtil.getCurrentDate();
        }

        ta.recycle();

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        lastClickedDay = initDate[2];
        //根据设定的日期范围计算日历的页数
        count = (endDate[0] - startDate[0]) * 12 + endDate[1] - startDate[1] + 1;
        calendarPagerAdapter = new CalendarPagerAdapter(attrs, count, startDate, initDate);
        setAdapter(calendarPagerAdapter);

        currentPosition = CalendarUtil.dateToPosition(initDate[0], initDate[1], startDate[0], startDate[1]);
        setCurrentItem(currentPosition, false);

        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                refreshMonthView(position);
                currentPosition = position;
                if (pagerChangeListener != null) {
                    int[] date = CalendarUtil.positionToDate(position, startDate[0], startDate[1]);
                    pagerChangeListener.onPagerChanged(new int[]{date[0], date[1], lastClickedDay});
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
        monthView.refresh(lastClickedDay);
    }

    /**
     * 设置上次点击的日期
     *
     * @param day
     */
    public void setLastClickDay(int day) {
        lastClickedDay = day;
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
        int destPosition = CalendarUtil.dateToPosition(SolarUtil.getCurrentDate()[0], SolarUtil.getCurrentDate()[1], startDate[0], startDate[1]);
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
        lastClickedDay = day != 0 ? day : lastClickedDay;
        setCurrentItem(CalendarUtil.dateToPosition(year, month, startDate[0], startDate[1]), false);
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
