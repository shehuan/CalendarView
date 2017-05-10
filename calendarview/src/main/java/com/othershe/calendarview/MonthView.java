package com.othershe.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.othershe.calendarview.listener.CalendarViewAdapter;
import com.othershe.calendarview.listener.OnMonthItemChooseListener;
import com.othershe.calendarview.listener.OnMonthItemClickListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MonthView extends ViewGroup {

    private static final int ROW = 6;
    private static final int COLUMN = 7;

    private static final int COLOR_RESET = 0;//重置文字颜色
    private static final int COLOR_SET = 1;//设置文字颜色

    private Context mContext;

    private View lastClickedView;//记录上次点击的Item
    private int currentMonthDays;//记录当月天数
    private int lastMonthDays;//记录当月显示的上个月天数
    private int nextMonthDays;//记录当月显示的下个月天数

    private boolean showLastNext;
    private boolean showLunar;
    private boolean showHoliday;
    private boolean showTerm;
    private boolean disableBefore;
    private int colorSolar;
    private int colorLunar;
    private int colorHoliday;
    private int colorChoose;
    private int sizeSolar;
    private int sizeLunar;
    private int dayBg;
    private int[] dateInit;

    private int item_layout;
    private CalendarViewAdapter calendarViewAdapter;
    private Set<Integer> chooseDays = new HashSet<>();//记录多选时当前页选中的日期

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        mContext = context;
        setBackgroundColor(Color.WHITE);
    }

    /**
     * @param dates            需要展示的日期数据
     * @param currentMonthDays 当月天数
     */
    public void setDateList(List<DateBean> dates, int currentMonthDays) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        lastMonthDays = 0;
        nextMonthDays = 0;
        boolean findInitShowDay = false;//是否找到默认选中的日期
        chooseDays.clear();

        this.currentMonthDays = currentMonthDays;
        for (int i = 0; i < dates.size(); i++) {
            final DateBean date = dates.get(i);

            if (date.getType() == 0) {
                lastMonthDays++;
                if (!showLastNext) {
                    addView(new View(mContext), i);
                    continue;
                }
            }

            if (date.getType() == 2) {
                nextMonthDays++;
                if (!showLastNext) {
                    addView(new View(mContext), i);
                    continue;
                }
            }

            View view;
            TextView solarDay;//阳历TextView
            TextView lunarDay;//阴历TextView(节假日、节气同样使用阴历TextView来显示)
            if (item_layout != 0 && calendarViewAdapter != null) {
                view = LayoutInflater.from(mContext).inflate(item_layout, null);
                TextView[] views = calendarViewAdapter.convertView(view, date);
                solarDay = views[0];
                lunarDay = views[1];
            } else {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_month_layout, null);
                solarDay = (TextView) view.findViewById(R.id.solar_day);
                lunarDay = (TextView) view.findViewById(R.id.lunar_day);
            }

            solarDay.setTextColor(colorSolar);
            solarDay.setTextSize(sizeSolar);
            lunarDay.setTextColor(colorLunar);
            lunarDay.setTextSize(sizeLunar);

            //设置上个月和下个月的阳历颜色
            if (date.getType() == 0 || date.getType() == 2) {
                solarDay.setTextColor(colorLunar);
            }
            solarDay.setText(String.valueOf(date.getSolar()[2]));

            //设置农历（节假日显示）
            if (showLunar) {
                if ("初一".equals(date.getLunar()[1])) {
                    lunarDay.setText(date.getLunar()[0]);
                    if ("正月".equals(date.getLunar()[0]) && showHoliday) {
                        lunarDay.setTextColor(colorHoliday);
                        lunarDay.setText("春节");
                    }
                } else {
                    if (!TextUtils.isEmpty(date.getSolarHoliday()) && showHoliday) {//阳历节日
                        setLunarText(date.getSolarHoliday(), lunarDay, date.getType());
                    } else if (!TextUtils.isEmpty(date.getLunarHoliday()) && showHoliday) {//农历节日
                        setLunarText(date.getLunarHoliday(), lunarDay, date.getType());
                    } else if (!TextUtils.isEmpty(date.getTerm()) && showTerm) {//节气
                        setLunarText(date.getTerm(), lunarDay, date.getType());
                    } else {
                        lunarDay.setText(date.getLunar()[1]);//农历日期
                    }
                }
            } else {
                lunarDay.setVisibility(GONE);
            }

            //默认选中当天
            if (!findInitShowDay
                    && date.getType() == 1
                    && dateInit[0] == date.getSolar()[0]
                    && dateInit[1] == date.getSolar()[1]
                    && dateInit[2] == date.getSolar()[2]) {
                lastClickedView = view;
                chooseDays.add(date.getSolar()[2]);
                setDayColor(view, COLOR_SET);
                findInitShowDay = true;
            }

            if (date.getType() == 1) {
                view.setTag(date.getSolar()[2]);
                if ((date.getSolar()[0] < dateInit[0]
                        || (date.getSolar()[0] == dateInit[0] && date.getSolar()[1] < dateInit[1])
                        || (date.getSolar()[0] == dateInit[0] && date.getSolar()[1] == dateInit[1] && date.getSolar()[2] < dateInit[2]))) {
                    if (disableBefore) {
                        solarDay.setTextColor(colorLunar);
                        lunarDay.setTextColor(colorLunar);
                        view.setTag(-1);
                        addView(view, i);
                        continue;
                    }
                }
            }

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int day = date.getSolar()[2];
                    CalendarView calendarView = (CalendarView) getParent();
                    OnMonthItemClickListener clickListener = calendarView.getItemClickListener();
                    OnMonthItemChooseListener chooseListener = calendarView.getItemChooseListener();
                    if (date.getType() == 1) {//点击当月
                        if (chooseListener != null) {//多选的情况
                            boolean flag;
                            if (chooseDays.contains(day)) {
                                setDayColor(v, COLOR_RESET);
                                chooseDays.remove(day);
                                flag = false;
                            } else {
                                setDayColor(v, COLOR_SET);
                                chooseDays.add(day);
                                flag = true;
                            }
                            calendarView.setLastChooseDate(day, flag);
                            chooseListener.onMonthItemChoose(v, date, flag);
                        } else {
                            calendarView.setLastClickDay(day);
                            if (lastClickedView != null) {
                                setDayColor(lastClickedView, COLOR_RESET);
                            }
                            setDayColor(v, COLOR_SET);
                            lastClickedView = v;

                            if (clickListener != null) {
                                clickListener.onMonthItemClick(v, date);
                            }
                        }
                    } else if (date.getType() == 0) {//点击上月
                        calendarView.setLastClickDay(day);
                        calendarView.lastMonth();
                        if (clickListener != null) {
                            clickListener.onMonthItemClick(v, date);
                        }
                    } else if (date.getType() == 2) {//点击下月
                        calendarView.setLastClickDay(day);
                        calendarView.nextMonth();
                        if (clickListener != null) {
                            clickListener.onMonthItemClick(v, date);
                        }
                    }
                }
            });
            addView(view, i);
        }
        requestLayout();
    }

    private void setLunarText(String str, TextView text, int type) {
        text.setText(str);
        if (type == 1) {
            text.setTextColor(colorHoliday);
        }
        text.setTag("holiday");
    }

    private void setDayColor(View v, int type) {
        TextView solarDay = (TextView) v.findViewById(R.id.solar_day);
        TextView lunarDay = (TextView) v.findViewById(R.id.lunar_day);
        solarDay.setTextSize(sizeSolar);
        lunarDay.setTextSize(sizeLunar);

        if (type == 0) {
            v.setBackgroundResource(0);
            solarDay.setTextColor(colorSolar);
            if ("holiday".equals(lunarDay.getTag())) {
                lunarDay.setTextColor(colorHoliday);
            } else {
                lunarDay.setTextColor(colorLunar);
            }
        } else if (type == 1) {
            v.setBackgroundResource(dayBg);
            solarDay.setTextColor(colorChoose);
            lunarDay.setTextColor(colorChoose);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int itemWidth = widthSpecSize / COLUMN;// - 14

        int width = widthSpecSize;
        int height = itemWidth * ROW;

        setMeasuredDimension(width, height);

        int itemHeight;

//        if (getChildCount() == 35) {
//            itemHeight = getMeasuredHeight() / 5;
//        } else {
        itemHeight = itemWidth;
//        }

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childView.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }

        View childView = getChildAt(0);
        int itemWidth = childView.getMeasuredWidth();
        int itemHeight = childView.getMeasuredHeight();

        //当显示五行时扩大行间距
        int dy = 0;
        if (getChildCount() == 35) {
            dy = itemWidth / 5;
        }

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            int left = i % COLUMN * itemWidth;// + (2 * (i % COLUMN) + 1) * 7
            int top = i / COLUMN * (itemHeight + dy);
            int right = left + itemWidth;
            int bottom = top + itemHeight;
            view.layout(left, top, right, bottom);
        }
    }

    public void refresh(int day) {
        View destView = findDestView(day);
        if (destView == null) {
            return;
        }
        if (lastClickedView != null) {
            setDayColor(lastClickedView, COLOR_RESET);
        }
        setDayColor(destView, COLOR_SET);
        lastClickedView = destView;
        invalidate();
    }

    /**
     * 多选时刷新日期，以恢复之前选中的日期
     *
     * @param set
     */
    public void multiChooseRefresh(HashSet<Integer> set) {
        for (Integer day : set) {
            setDayColor(findDestView(day), COLOR_SET);
        }
        //防止造成默认日期取消后代码重复选中的假象
        if (chooseDays.contains(dateInit[2]) && !set.contains(dateInit[2])) {
            setDayColor(findDestView(dateInit[2]), COLOR_RESET);
            chooseDays.remove(dateInit[2]);
        }
        invalidate();
    }

    /**
     * 查找要跳转到的页面需要展示的日期View
     *
     * @param day
     * @return
     */
    private View findDestView(int day) {
        View view = null;
        for (int i = lastMonthDays; i < getChildCount() - nextMonthDays; i++) {
            if ((Integer) getChildAt(i).getTag() == day) {
                view = getChildAt(i);
                break;
            }
        }

        if (view == null) {
            view = getChildAt(currentMonthDays + lastMonthDays - 1);
        }

        if ((Integer) view.getTag() == -1) {
            view = null;
        }
        return view;
    }

    public void setAttrValues(int[] dateInit,
                              boolean showLastNext, boolean showLunar, boolean showHoliday, boolean showTerm, boolean disableBefore,
                              int colorSolar, int colorLunar, int colorHoliday, int colorChoose,
                              int sizeSolar, int sizeLunar, int dayBg) {
        this.dateInit = dateInit;
        this.showLastNext = showLastNext;
        this.showLunar = showLunar;
        this.showHoliday = showHoliday;
        this.showTerm = showTerm;
        this.disableBefore = disableBefore;
        this.colorSolar = colorSolar;
        this.colorLunar = colorLunar;
        this.colorHoliday = colorHoliday;
        this.colorChoose = colorChoose;
        this.sizeSolar = sizeSolar;
        this.sizeLunar = sizeLunar;
        this.dayBg = dayBg;
    }

    public void setOnCalendarViewAdapter(int item_layout, CalendarViewAdapter calendarViewAdapter) {
        this.item_layout = item_layout;
        this.calendarViewAdapter = calendarViewAdapter;
    }
}
