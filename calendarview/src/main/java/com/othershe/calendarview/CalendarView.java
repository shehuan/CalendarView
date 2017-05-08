package com.othershe.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
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

    private int[] dateStart;//日历的开始年、月
    private int[] dateEnd;//日历的结束年、月
    private int[] dateInit;//默认展示、选中的日期（年、月、日）
    private boolean showLastNext = true;//是否显示上个月、下个月
    private boolean showLunar = true;//是否显示农历
    private boolean showHoliday = true;//是否显示节假日(不显示农历则节假日无法显示，节假日会覆盖农历显示)
    private boolean disableBefore = false;//默认展示、选中的日期前的所有日期是否可用
    private int colorSolar = Color.BLACK;//阳历的日期颜色
    private int colorLunar = Color.parseColor("#999999");//阴历的日期颜色
    private int colorHoliday = Color.parseColor("#EC9729");//节假日的颜色
    private int colorChoose = Color.WHITE;//选中的日期文字颜色
    private int sizeSolar = 16;//阳历日期文字尺寸
    private int sizeLunar = 10;//阴历日期文字尺寸
    private int dayBg = R.drawable.blue_circle;//选中的背景

    private int count;//ViewPager的页数

    private int lastClickedDay;//上次点击的日期

    private CalendarPagerAdapter calendarPagerAdapter;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        String dateStartStr = null;
        String dateEndStr = null;
        String dateInitStr = null;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.CalendarView_date_start) {
                dateStartStr = ta.getString(attr);
            } else if (attr == R.styleable.CalendarView_date_end) {
                dateEndStr = ta.getString(attr);
            } else if (attr == R.styleable.CalendarView_date_init) {
                dateInitStr = ta.getString(attr);
            } else if (attr == R.styleable.CalendarView_show_last_next) {
                showLastNext = ta.getBoolean(attr, true);
            } else if (attr == R.styleable.CalendarView_show_lunar) {
                showLunar = ta.getBoolean(attr, true);
            } else if (attr == R.styleable.CalendarView_show_holiday) {
                showHoliday = ta.getBoolean(attr, true);
            } else if (attr == R.styleable.CalendarView_disable_before) {
                disableBefore = ta.getBoolean(attr, true);
            } else if (attr == R.styleable.CalendarView_color_solar) {
                colorSolar = ta.getColor(attr, colorSolar);
            } else if (attr == R.styleable.CalendarView_size_solar) {
                sizeSolar = ta.getInteger(R.styleable.CalendarView_size_solar, sizeSolar);
            } else if (attr == R.styleable.CalendarView_color_lunar) {
                colorLunar = ta.getColor(attr, colorLunar);
            } else if (attr == R.styleable.CalendarView_size_lunar) {
                sizeLunar = ta.getDimensionPixelSize(R.styleable.CalendarView_size_lunar, sizeLunar);
            } else if (attr == R.styleable.CalendarView_color_holiday) {
                colorHoliday = ta.getColor(attr, colorHoliday);
            } else if (attr == R.styleable.CalendarView_color_choose) {
                colorChoose = ta.getColor(attr, colorChoose);
            } else if (attr == R.styleable.CalendarView_day_bg) {
                dayBg = ta.getResourceId(attr, dayBg);
            }
        }

        ta.recycle();

        dateStart = CalendarUtil.strToArray(dateStartStr);
        if (dateStart == null) {
            dateStart = new int[]{1900, 1};
        }
        dateEnd = CalendarUtil.strToArray(dateEndStr);
        if (dateEnd == null) {
            dateEnd = new int[]{2049, 12};
        }

        dateInit = CalendarUtil.strToArray(dateInitStr);
        if (dateInit == null) {
            dateInit = SolarUtil.getCurrentDate();
        }

        sizeSolar = CalendarUtil.getTextSize(context, sizeSolar);
        sizeLunar = CalendarUtil.getTextSize(context, sizeLunar);
    }

    private void init() {
        lastClickedDay = dateInit[2];
        //根据设定的日期范围计算日历的页数
        count = (dateEnd[0] - dateStart[0]) * 12 + dateEnd[1] - dateStart[1] + 1;
        calendarPagerAdapter = new CalendarPagerAdapter(count);
        calendarPagerAdapter.setAttrValues(dateInit, dateStart,
                showLastNext, showLunar, showHoliday, disableBefore,
                colorSolar, colorLunar, colorHoliday, colorChoose,
                sizeSolar, sizeLunar, dayBg);
        setAdapter(calendarPagerAdapter);

        currentPosition = CalendarUtil.dateToPosition(dateInit[0], dateInit[1], dateStart[0], dateStart[1]);
        setCurrentItem(currentPosition, false);

        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                refreshMonthView(position);
                currentPosition = position;
                if (pagerChangeListener != null) {
                    int[] date = CalendarUtil.positionToDate(position, dateStart[0], dateStart[1]);
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
        int destPosition = CalendarUtil.dateToPosition(SolarUtil.getCurrentDate()[0], SolarUtil.getCurrentDate()[1], dateStart[0], dateStart[1]);
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
        setCurrentItem(CalendarUtil.dateToPosition(year, month, dateStart[0], dateStart[1]), false);
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
