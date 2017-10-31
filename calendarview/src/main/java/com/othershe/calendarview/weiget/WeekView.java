package com.othershe.calendarview.weiget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.othershe.calendarview.R;
import com.othershe.calendarview.utils.CalendarUtil;

public class WeekView extends View {

    private String[] weekArray = {"日", "一", "二", "三", "四", "五", "六"};
    private int weekSize = 12;//文字尺寸
    private int weekColor = Color.BLACK;//文字颜色

    private Paint mPaint;
    private Context context;

    public WeekView(Context context) {
        this(context, null);
    }

    public WeekView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        initPaint();
        setBackgroundColor(Color.WHITE);
    }

    private void initAttrs(AttributeSet attrs) {
        String weekStr = null;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WeekView);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.WeekView_week_color) {
                weekColor = ta.getColor(R.styleable.WeekView_week_color, weekColor);
            } else if (attr == R.styleable.WeekView_week_size) {
                weekSize = ta.getInteger(R.styleable.WeekView_week_size, weekSize);
            } else if (attr == R.styleable.WeekView_week_str) {
                weekStr = ta.getString(R.styleable.WeekView_week_str);
            }
        }
        ta.recycle();

        if (!TextUtils.isEmpty(weekStr)) {
            String[] weeks = weekStr.split("\\.");
            if (weeks.length != 7) {
                return;
            }
            System.arraycopy(weeks, 0, weekArray, 0, 7);
        }

        weekSize = CalendarUtil.getTextSize1(context, weekSize);
    }


    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(weekColor);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(weekSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = CalendarUtil.getPxSize(context, 35);
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = CalendarUtil.getPxSize(context, 300);
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int itemWidth = width / 7;

        for (int i = 0; i < weekArray.length; i++) {
            String text = weekArray[i];
            int textWidth = (int) mPaint.measureText(text);
            int startX = itemWidth * i + (itemWidth - textWidth) / 2;
            int startY = (int) (height / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(text, startX, startY, mPaint);
        }
    }
}
