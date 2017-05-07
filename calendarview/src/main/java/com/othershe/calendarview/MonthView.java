package com.othershe.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

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
    private boolean findInitShowDay = false;//是否找到默认选中的日期（不设置则选中当天）

    private boolean showLastNext = true;//是否显示上个月、下个月
    private boolean showHoliday = true;//是否显示节假日
    private boolean showLunar = true;//是否显示农历
    private boolean enableBefore = true;//指定日期前的所有日期是否可用
    private int[] initDate;

    public MonthView(Context context) {
        this(context, null, null);
    }

    public MonthView(Context context, AttributeSet attrs, int[] initDate) {
        super(context, attrs, 0);
        mContext = context;
        this.initDate = initDate;
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
        this.currentMonthDays = currentMonthDays;
        for (int i = 0; i < dates.size(); i++) {
            final DateBean date = dates.get(i);

            if (!showLastNext) {
                enableBefore = true;
                if (date.getType() == 0 || date.getType() == 2) {
                    addView(new View(mContext), i);
                    continue;
                }
            }

            View view = LayoutInflater.from(mContext).inflate(R.layout.item_month_layout, null);
            if (date.getType() == 1) {
                view.setTag(date.getSolar()[2]);
            } else if (date.getType() == 0) {
                lastMonthDays++;
            } else if (date.getType() == 2) {
                nextMonthDays++;
            }
            TextView day = (TextView) view.findViewById(R.id.day);
            final TextView lunarDay = (TextView) view.findViewById(R.id.lunar_day);

            //设置上个月和下个月的阳历颜色
            if (date.getType() == 0 || date.getType() == 2) {
                day.setTextColor(Color.parseColor("#999999"));
            }
            day.setText(String.valueOf(date.getSolar()[2]));

            //设置农历（节假日显示）
            if (showLunar) {
                if ("初一".equals(date.getLunar()[1])) {
                    lunarDay.setText(date.getLunar()[0]);
                    if ("春节".equals(date.getLunar()[0])) {
                        lunarDay.setTextColor(Color.parseColor("#EC9729"));
                    }
                } else {
                    if (!TextUtils.isEmpty(date.getSolarHoliday()) && showHoliday) {//阳历节日
                        setLunarText(date.getSolarHoliday(), lunarDay, date.getType());
                    } else if (!TextUtils.isEmpty(date.getLunarHoliday()) && showHoliday) {//农历节日
                        setLunarText(date.getLunarHoliday(), lunarDay, date.getType());
                    } else if (!TextUtils.isEmpty(date.getTerm()) && showHoliday) {//节气
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
                    && initDate[0] == date.getSolar()[0]
                    && initDate[1] == date.getSolar()[1]
                    && initDate[2] == date.getSolar()[2]) {
                view.setBackgroundResource(R.drawable.blue_circle);
                lastClickedView = view;
                setTextColor(view, COLOR_SET);
                findInitShowDay = true;
            }

            if (!enableBefore
                    && (date.getSolar()[0] < initDate[0]
                    || (date.getSolar()[0] == initDate[0] && date.getSolar()[1] < initDate[1])
                    || (date.getSolar()[0] == initDate[0] && date.getSolar()[1] == initDate[1] && date.getSolar()[2] < initDate[2]))) {
                day.setTextColor(Color.parseColor("#999999"));
                lunarDay.setTextColor(Color.parseColor("#999999"));
                addView(view, i);
                continue;
            }

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CalendarView calendarView = (CalendarView) getParent();
                    calendarView.setLastClickDay(date.getSolar()[2]);
                    if (date.getType() == 1) {//点击当月
                        if (lastClickedView != null) {
                            lastClickedView.setBackgroundResource(0);
                            setTextColor(lastClickedView, COLOR_RESET);
                        }
                        v.setBackgroundResource(R.drawable.blue_circle);
                        setTextColor(v, COLOR_SET);
                        calendarView.getItemClickListener().onCurrentMonthClick(date);
                        lastClickedView = v;
                    } else if (date.getType() == 0) {//点击上月
                        calendarView.lastMonth();
                        calendarView.getItemClickListener().onLastMonthClick(date);

                    } else if (date.getType() == 2) {//点击下月
                        calendarView.nextMonth();
                        calendarView.getItemClickListener().onNextMonthClick(date);
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
            text.setTextColor(Color.parseColor("#EC9729"));
        }
        text.setTag("lunar");
    }

    private void setTextColor(View v, int type) {
        TextView day = (TextView) v.findViewById(R.id.day);
        TextView lunarDay = (TextView) v.findViewById(R.id.lunar_day);

        if (type == 0) {
            day.setTextColor(Color.BLACK);
            if ("lunar".equals(lunarDay.getTag())) {
                lunarDay.setTextColor(Color.parseColor("#EC9729"));
            } else {
                lunarDay.setTextColor(Color.parseColor("#999999"));
            }
        } else if (type == 1) {
            day.setTextColor(Color.WHITE);
            lunarDay.setTextColor(Color.WHITE);
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
            childView.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY));
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
        if (lastClickedView != null) {
            lastClickedView.setBackgroundResource(0);
            setTextColor(lastClickedView, COLOR_RESET);
        }
        View destView = findDestView(day);
        setTextColor(destView, COLOR_SET);
        destView.setBackgroundResource(R.drawable.blue_circle);
        lastClickedView = destView;
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
        return view;
    }
}
