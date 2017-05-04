package com.othershe.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MonthView extends ViewGroup {

    private static final int ROW = 6;
    private static final int COLUMN = 7;

    private Context mContext;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
    }

    public void setDateList(List<DateBean> datas) {
        if (getChildCount() > 0) {
            removeAllViews();
        }

        for (int i = 0; i < datas.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_month_layout, null);
            final DateBean data = datas.get(i);
            TextView day = (TextView) view.findViewById(R.id.day);
            TextView lunarDay = (TextView) view.findViewById(R.id.lunar_day);
            if (data.getType() == 0 || data.getType() == 2) {
                day.setTextColor(Color.GRAY);
            }
            day.setText(String.valueOf(data.getSolar()[2]));

            if ("初一".equals(data.getLunar()[1])) {
                lunarDay.setText(data.getLunar()[0]);
            } else {
                lunarDay.setText(data.getLunar()[1]);
            }

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getParent() instanceof CalendarView) {
                        CalendarView calendarView = (CalendarView) getParent();
                        calendarView.getItemClickListener().onItemClick(data);
                    }
                }
            });

            addView(view);
        }

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int itemWidth = widthSpecSize / COLUMN;

        int width = widthSpecSize;
        int height = itemWidth * ROW;

        setMeasuredDimension(width, height);

        int itemHeight;

        if (getChildCount() == 35) {
            itemHeight = getMeasuredHeight() / 5;
        } else {
            itemHeight = itemWidth;
        }

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

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int left = i % COLUMN * itemWidth;
            int top = i / COLUMN * itemHeight;
            int right = left + itemWidth;
            int bottom = top + itemHeight;
            view.layout(left, top, right, bottom);
        }
    }
}
