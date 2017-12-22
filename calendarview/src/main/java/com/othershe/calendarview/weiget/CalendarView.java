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
import com.othershe.calendarview.listener.OnMultiChooseListener;
import com.othershe.calendarview.listener.OnSingleChooseListener;
import com.othershe.calendarview.listener.OnPagerChangeListener;
import com.othershe.calendarview.utils.CalendarUtil;
import com.othershe.calendarview.utils.SolarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarView extends ViewPager {
    //记录当前PagerAdapter的position
    private int currentPosition;

    private OnPagerChangeListener pagerChangeListener;
    private OnSingleChooseListener singleChooseListener;
    private OnMultiChooseListener multiChooseListener;
    private CalendarViewAdapter calendarViewAdapter;
    private int item_layout;

    private int[] initDate;//日历初始显示的年月
    private int[] startDate;//日历的开始年、月
    private int[] endDate;//日历的结束年、月

    private int count;//ViewPager的页数
    private int[] lastClickDate = new int[2];//记录单选的ViewPager position以及选中的日期
    private SparseArray<HashSet<Integer>> chooseDate;//记录多选时全部选中的日期
    private Set<Integer> positions;//多选时记录选中日期对应的ViewPager position

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
                mAttrsBean.setShowLastNext(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_show_lunar) {
                mAttrsBean.setShowLunar(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_show_holiday) {
                mAttrsBean.setShowHoliday(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_show_term) {
                mAttrsBean.setShowTerm(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_switch_choose) {
                mAttrsBean.setSwitchChoose(ta.getBoolean(attr, true));
            } else if (attr == R.styleable.CalendarView_solar_color) {
                mAttrsBean.setColorSolar(ta.getColor(attr, mAttrsBean.getColorSolar()));
            } else if (attr == R.styleable.CalendarView_solar_size) {
                mAttrsBean.setSizeSolar(CalendarUtil.getTextSize(context, ta.getInteger(attr, mAttrsBean.getSizeSolar())));
            } else if (attr == R.styleable.CalendarView_lunar_color) {
                mAttrsBean.setColorLunar(ta.getColor(attr, mAttrsBean.getColorLunar()));
            } else if (attr == R.styleable.CalendarView_lunar_size) {
                mAttrsBean.setSizeLunar(CalendarUtil.getTextSize(context, ta.getInt(attr, mAttrsBean.getSizeLunar())));
            } else if (attr == R.styleable.CalendarView_holiday_color) {
                mAttrsBean.setColorHoliday(ta.getColor(attr, mAttrsBean.getColorHoliday()));
            } else if (attr == R.styleable.CalendarView_choose_color) {
                mAttrsBean.setColorChoose(ta.getColor(attr, mAttrsBean.getColorChoose()));
            } else if (attr == R.styleable.CalendarView_day_bg) {
                mAttrsBean.setDayBg(ta.getResourceId(attr, mAttrsBean.getDayBg()));
            } else if (attr == R.styleable.CalendarView_choose_type) {
                mAttrsBean.setChooseType(ta.getInt(attr, 0));
            }
        }

        ta.recycle();

        startDate = new int[]{1900, 1};
        endDate = new int[]{2049, 12};
        mAttrsBean.setStartDate(startDate);
        mAttrsBean.setEndDate(endDate);
    }

    public void init() {
        //根据设定的日期范围计算日历的页数
        count = (endDate[0] - startDate[0]) * 12 + endDate[1] - startDate[1] + 1;
        calendarPagerAdapter = new CalendarPagerAdapter(count);
        calendarPagerAdapter.setAttrsBean(mAttrsBean);
        calendarPagerAdapter.setOnCalendarViewAdapter(item_layout, calendarViewAdapter);
        setAdapter(calendarPagerAdapter);

        currentPosition = CalendarUtil.dateToPosition(initDate[0], initDate[1], startDate[0], startDate[1]);

        //单选
        if (mAttrsBean.getChooseType() == 0) {
            int[] singleDate = mAttrsBean.getSingleDate();
            if (singleDate != null) {
                lastClickDate[0] = CalendarUtil.dateToPosition(singleDate[0], singleDate[1], startDate[0], startDate[1]);
                lastClickDate[1] = singleDate[2];
            }
        }

        //多选
        if (mAttrsBean.getChooseType() == 1) {
            positions = new HashSet<>();
            chooseDate = new SparseArray<>();
            if (mAttrsBean.getMultiDates() != null) {
                for (int[] date : mAttrsBean.getMultiDates()) {
                    if (isIllegal(date)) {
                        int datePosition = CalendarUtil.dateToPosition(date[0], date[1], startDate[0], startDate[1]);
                        positions.add(datePosition);
                        setChooseDate(date[2], true, datePosition);
                    }
                }
            }
        }

        setCurrentItem(currentPosition, false);

        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                refreshMonthView(position);
                currentPosition = position;
                if (pagerChangeListener != null) {
                    int[] date = CalendarUtil.positionToDate(position, startDate[0], startDate[1]);
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
        if (mAttrsBean.getChooseType() == 1) {//多选
            if (chooseDate.get(position) != null)
                monthView.multiChooseRefresh(chooseDate.get(position));
        } else {
            //单选时，如果设置切换月份不选中上次选中的日期但如果切换回有选中日期的页则需要刷新选中，或者切换选中开启则需要刷新选中
            boolean flag = (!mAttrsBean.isSwitchChoose() && lastClickDate[0] == position)
                    || mAttrsBean.isSwitchChoose();
            monthView.refresh(lastClickDate[1], flag);
        }
    }

    /**
     * 设置单选时选中的日期
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
     * @param flag     多选时flag=true代表选中数据，flag=false代表取消选中
     * @param position 代表记录viewpager哪一页的数据
     */
    public void setChooseDate(int day, boolean flag, int position) {
        if (position == -1) {
            position = currentPosition;
        }
        HashSet<Integer> days = chooseDate.get(position);
        if (flag) {
            if (days == null) {
                days = new HashSet<>();
                chooseDate.put(position, days);
            }
            days.add(day);
            positions.add(position);
        } else {
            days.remove(day);
        }
    }

    /**
     * 检查初始化选中的日期，或者要跳转的日期是否合法
     *
     * @param destDate
     * @return
     */
    private boolean isIllegal(int[] destDate) {

        if (destDate[1] > 12 || destDate[1] < 1) {
            return false;
        }

        if (CalendarUtil.dateToMillis(destDate) < CalendarUtil.dateToMillis(startDate)) {
            return false;
        }

        if (CalendarUtil.dateToMillis(destDate) > CalendarUtil.dateToMillis(endDate)) {
            return false;
        }

        if (destDate[2] > SolarUtil.getMonthDays(destDate[0], destDate[1]) || destDate[2] < 1) {
            return false;
        }


        if (mAttrsBean.getDisableStartDate() != null) {
            if (CalendarUtil.dateToMillis(destDate) < CalendarUtil.dateToMillis(mAttrsBean.getDisableStartDate())) {
                return false;
            }
        }

        if (mAttrsBean.getDisableEndDate() != null) {
            if (CalendarUtil.dateToMillis(destDate) > CalendarUtil.dateToMillis(mAttrsBean.getDisableEndDate())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 设置日期单选回调
     *
     * @param singleChooseListener
     */
    public void setOnSingleChooseListener(OnSingleChooseListener singleChooseListener) {
        this.singleChooseListener = singleChooseListener;
    }

    public OnMultiChooseListener getMultiChooseListener() {
        return multiChooseListener;
    }

    /**
     * 设置日期多选回调
     *
     * @param multiChooseListener
     */
    public void setOnMultiChooseListener(OnMultiChooseListener multiChooseListener) {
        this.multiChooseListener = multiChooseListener;
    }

    public OnSingleChooseListener getSingleChooseListener() {
        return singleChooseListener;
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
    public CalendarView setOnCalendarViewAdapter(int item_layout, CalendarViewAdapter calendarViewAdapter) {
        this.item_layout = item_layout;
        this.calendarViewAdapter = calendarViewAdapter;
        return this;
    }

    /**
     * 单选时跳转到今天
     */
    public void today() {
        int destPosition = CalendarUtil.dateToPosition(CalendarUtil.getCurrentDate()[0], CalendarUtil.getCurrentDate()[1], startDate[0], startDate[1]);
        lastClickDate[0] = destPosition;
        lastClickDate[1] = CalendarUtil.getCurrentDate()[2];
        if (destPosition == currentPosition) {
            refreshMonthView(destPosition);
        } else {
            setCurrentItem(destPosition, false);
        }
    }

    /**
     * 单选时跳转到指定日期
     *
     * @param year
     * @param month
     * @param day
     */
    public boolean toSpecifyDate(int year, int month, int day) {
        if (!isIllegal(new int[]{year, month, day})) {
            return false;
        }
        toDestDate(year, month, day);
        return true;
    }

    private void toDestDate(int year, int month, int day) {
        int destPosition = CalendarUtil.dateToPosition(year, month, startDate[0], startDate[1]);
        if (!mAttrsBean.isSwitchChoose() && day != 0) {
            lastClickDate[0] = destPosition;
        }
        lastClickDate[1] = day != 0 ? day : lastClickDate[1];
        if (destPosition == currentPosition) {
            //在当月进行日期跳转
            refreshMonthView(destPosition);
        } else {
            setCurrentItem(destPosition, false);
        }
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
        toDestDate(startDate[0], startDate[1], 0);
    }

    /**
     * 跳转到日历的结束年月
     */
    public void toEnd() {
        toDestDate(endDate[0], endDate[1], 0);
    }

    /**
     * 将指定日期的农历替换成对应文字
     */
    public CalendarView setSpecifyMap(HashMap<String, String> map) {
        mAttrsBean.setSpecifyMap(map);
        return this;
    }

    /**
     * 设置日历初始显示的年月
     *
     * @param date
     * @return
     */
    public CalendarView setInitDate(String date) {
        initDate = CalendarUtil.strToArray(date);
        return this;
    }

    /**
     * 设置日历的开始年月、结束年月
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public CalendarView setStartEndDate(String startDate, String endDate) {
        this.startDate = CalendarUtil.strToArray(startDate);
        if (startDate == null) {
            this.startDate = new int[]{1900, 1};
        }
        this.endDate = CalendarUtil.strToArray(endDate);
        if (endDate == null) {
            this.endDate = new int[]{2049, 12};
        }
        mAttrsBean.setStartDate(this.startDate);
        mAttrsBean.setEndDate(this.endDate);
        return this;
    }

    /**
     * 设置多选时默认选中的日期集合
     *
     * @param dates
     * @return
     */
    public CalendarView setMultiDate(List<String> dates) {
        List<int[]> multiDates = new ArrayList<>();
        for (String date : dates) {
            int[] d = CalendarUtil.strToArray(date);
            if (isIllegal(d)) {
                multiDates.add(d);
            }
        }
        mAttrsBean.setMultiDates(multiDates);
        return this;
    }


    /**
     * 设置单选时默认选中的日期
     *
     * @param date
     * @return
     */
    public CalendarView setSingleDate(String date) {
        int[] singleDate = CalendarUtil.strToArray(date);
        if (!isIllegal(singleDate)) {
            singleDate = null;
        }
        mAttrsBean.setSingleDate(singleDate);
        return this;
    }

    /**
     * 设置日历禁用范围
     *
     * @param startDate 禁用startDate之前的日期
     * @param endDate   禁用endDate之后的日期
     * @return
     */
    public CalendarView setDisableStartEndDate(String startDate, String endDate) {
        mAttrsBean.setDisableStartDate(CalendarUtil.strToArray(startDate));
        mAttrsBean.setDisableEndDate(CalendarUtil.strToArray(endDate));
        return this;
    }

    /**
     * 得到单选时当前选中的日期
     *
     * @return
     */
    public DateBean getSingleDate() {
        int[] date = CalendarUtil.positionToDate(lastClickDate[0], startDate[0], startDate[1]);
        return CalendarUtil.getDateBean(date[0], date[1], lastClickDate[1]);
    }

    /**
     * 得到多选时选中的日期
     *
     * @return
     */
    public List<DateBean> getMultiDate() {
        List<DateBean> list = new ArrayList<>();
        for (Integer position : positions) {
            HashSet<Integer> days = chooseDate.get(position);
            if (days.size() > 0) {
                int[] date = CalendarUtil.positionToDate(position, startDate[0], startDate[1]);
                for (Integer day : days) {
                    list.add(CalendarUtil.getDateBean(date[0], date[1], day));
                }
            }
        }
        return list;
    }
}
