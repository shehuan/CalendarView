package com.othershe.calendarview;

import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.othershe.calendarview.utils.CalendarUtil;

import java.util.LinkedList;

public class CalendarPagerAdapter extends PagerAdapter {

    private AttributeSet attrs;
    //缓存上一次回收的MonthView
    private LinkedList<MonthView> cache = new LinkedList<>();
    private SparseArray<MonthView> mViews = new SparseArray<>();

    private int count;
    private int[] start;

    public CalendarPagerAdapter(AttributeSet attrs, int count, int[] start) {
        this.attrs = attrs;
        this.count = count;
        this.start = start;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MonthView view;
        if (!cache.isEmpty()) {
            view = cache.removeFirst();
        } else {
            view = new MonthView(container.getContext(), attrs);
        }

        int[] date = CalendarUtil.positionToDate(position, start[0], start[1]);
        view.setTag(String.valueOf(position));
        view.setDateList(CalendarUtil.getMonthDate(date[0], date[1]));

        mViews.put(position, view);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((MonthView) object);
        cache.addLast((MonthView) object);
        mViews.remove(position);
    }

    public SparseArray<MonthView> getViews() {
        return mViews;
    }
}
