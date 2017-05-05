package com.othershe.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.othershe.calendarview.utils.SolarUtil;

import java.util.List;

public class MonthView extends ViewGroup {

    private static final int ROW = 6;
    private static final int COLUMN = 7;

    private Context mContext;

    private View last;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
        setBackgroundColor(Color.WHITE);
    }

    public void setDateList(List<DateBean> datas) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        int[] today = SolarUtil.getCurrentDate();

        for (int i = 0; i < datas.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_month_layout, null);
            final DateBean date = datas.get(i);
            TextView day = (TextView) view.findViewById(R.id.day);
            final TextView lunarDay = (TextView) view.findViewById(R.id.lunar_day);
            if (date.getType() == 0 || date.getType() == 2) {
                day.setTextColor(Color.parseColor("#999999"));
            }
            day.setText(String.valueOf(date.getSolar()[2]));

            if ("初一".equals(date.getLunar()[1])) {
                lunarDay.setText(date.getLunar()[0]);
            } else {
                if (!TextUtils.isEmpty(date.getSolarHoliday())) {//阳历节日
                    lunarDay.setText(date.getSolarHoliday());
                    if (date.getType() == 1) {
                        lunarDay.setTextColor(Color.parseColor("#EC9729"));
                    }
                } else if (!TextUtils.isEmpty(date.getLunarHoliday())) {//农历节日
                    lunarDay.setText(date.getLunarHoliday());
                    if (date.getType() == 1) {
                        lunarDay.setTextColor(Color.parseColor("#EC9729"));
                    }
                } else if (!TextUtils.isEmpty(date.getTerm())) {//节气
                    lunarDay.setText(date.getTerm());
                    if (date.getType() == 1) {
                        lunarDay.setTextColor(Color.parseColor("#EC9729"));
                    }
                } else {
                    lunarDay.setText(date.getLunar()[1]);
                }
            }

            //默认选中当天
            if (today[0] == date.getSolar()[0] && today[1] == date.getSolar()[1] && today[2] == date.getSolar()[2]) {
                view.setBackgroundResource(R.drawable.blue_circle);
                last = view;
                setTextColor(view, 1);
            }

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CalendarView calendarView = (CalendarView) getParent();
                    if (date.getType() == 1) {//点击当月
                        if (last != null) {
                            last.setBackgroundResource(0);
                            setTextColor(last, 0);
                        }
                        v.setBackgroundResource(R.drawable.blue_circle);
                        setTextColor(v, 1);
                        calendarView.getItemClickListener().onItemClick(date);
                        last = v;
                    } else if (date.getType() == 0) {//点击上月
                        calendarView.lastMonth();

                    } else if (date.getType() == 2) {//点击下月
                        calendarView.nextMonth();
                    }
                }
            });

            addView(view, i);
        }

        requestLayout();
    }

    private void setTextColor(View v, int type) {
        TextView day = (TextView) v.findViewById(R.id.day);
        TextView lunarDay = (TextView) v.findViewById(R.id.lunar_day);

        if (type == 0) {
            day.setTextColor(Color.BLACK);
            lunarDay.setTextColor(Color.parseColor("#999999"));
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
}
