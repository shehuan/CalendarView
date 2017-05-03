package com.othershe.calendarview;

import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

public class CalendarViewAdapter extends PagerAdapter {

    private List<DateBean> datas;
    private AttributeSet attrs;

    private LinkedList<MonthView> cache = new LinkedList<>();

    public CalendarViewAdapter(List<DateBean> datas, AttributeSet attrs) {
        this.datas = datas;
        this.attrs = attrs;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
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
        view.setDatas(datas);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        container.removeView((View) object);
        cache.addLast((MonthView) object);
    }
}
