package com.othershe.calendarview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.othershe.calendarview.listener.CalendarViewAdapter;
import com.othershe.calendarview.listener.OnItemClickListener;
import com.othershe.calendarview.listener.OnMultiChooseListener;
import com.othershe.calendarview.listener.OnPagerChangeListener;
import com.othershe.calendarview.utils.CalendarUtil;

public class CalendarView extends ViewPager {
    //记录当前PagerAdapter的position
    private int currentPosition;

    private OnPagerChangeListener pagerChangeListener;
    private OnItemClickListener itemClickListener;
    private OnMultiChooseListener multiChooseListener;
    private CalendarViewAdapter calendarViewAdapter;
    private int item_layout;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAdapter(new CalendarPagerAdapter(attrs));

        currentPosition = CalendarUtil.dateToPosition(CalendarUtil.getCurrentDate()[0], CalendarUtil.getCurrentDate()[1]);
        setCurrentItem(currentPosition, false);
        getAdapter().notifyDataSetChanged();

        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                if (pagerChangeListener != null) {
                    pagerChangeListener.onPagerChanged(CalendarUtil.positionToDate(position));
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
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OnItemClickListener getItemClickListener() {
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
        setCurrentItem(CalendarUtil.dateToPosition(CalendarUtil.getCurrentDate()[0], CalendarUtil.getCurrentDate()[1]), false);
    }

    /**
     * 跳转到指定日期
     *
     * @param year
     * @param month
     * @param day
     */
    public void toSpecifyDate(int year, int month, int day) {
        setCurrentItem(CalendarUtil.dateToPosition(year, month), false);
    }

    /**
     * 跳转到下个月
     */
    public void nextMonth() {
        if (currentPosition < 150 * 12)
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
