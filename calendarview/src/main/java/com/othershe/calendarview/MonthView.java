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
        this(context, attrs, 0);
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setWillNotDraw(false);
    }

    public void setDatas(List<DateBean> datas) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_month_layout, this, false);

        for (int i = 0; i < datas.size(); i++) {
            DateBean data = datas.get(i);
            TextView day = (TextView) view.findViewById(R.id.day);
            if (data.getType() == 0 || data.getType() == 2) {
                day.setTextColor(Color.GRAY);
            }
            day.setText(data.getDay());
            addViewInLayout(view, i, view.getLayoutParams(), true);
        }

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int itemWidth = widthMeasureSpec / COLUMN;

        int width = widthSpecSize;
        int height = itemWidth * ROW;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
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
