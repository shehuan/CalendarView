package com.othershe.calendarview;

import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.othershe.calendarview.utils.CalendarUtil;

import java.util.LinkedList;

public class CalendarPagerAdapter extends PagerAdapter {

    private AttributeSet attrs;
    //缓存上一次回收的MonthView
    private LinkedList<MonthView> cache = new LinkedList<>();

    public CalendarPagerAdapter(AttributeSet attrs) {
        this.attrs = attrs;
    }

    @Override
    public int getCount() {
        return 150 * 12;
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

        int[] date = CalendarUtil.positionToDate(position);

        view.setDateList(CalendarUtil.getMonthDate(date[0], date[1]));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        cache.addLast((MonthView) object);
    }
}
