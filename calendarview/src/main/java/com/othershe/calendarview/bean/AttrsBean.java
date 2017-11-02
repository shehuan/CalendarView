package com.othershe.calendarview.bean;

import android.graphics.Color;

import com.othershe.calendarview.R;

import java.util.List;
import java.util.Map;

public class AttrsBean {

    private int[] startDate;//日历的开始年、月
    private int[] endDate;//日历的结束年、月
    private int[] singleDate;//单选是默认选中的日期（年、月、日）
    private List<int[]> multiDates;//多选时默认选中的日期集合
    private int[] disableStartDate;//单选时默认选中的年、月、日disableStar
    private int[] disableEndDate;//单选时默认选中的年、月、日
    private boolean showLastNext = true;//是否显示上个月、下个月
    private boolean showLunar = true;//是否显示农历
    private boolean showHoliday = true;//是否显示节假日(不显示农历则节假日无法显示，节假日会覆盖农历显示)
    private boolean showTerm = true;//是否显示节气
    private boolean switchChoose = true;//单选时切换月份，是否选中上次的日期
    private int colorSolar = Color.BLACK;//阳历的日期颜色
    private int colorLunar = Color.parseColor("#999999");//阴历的日期颜色
    private int colorHoliday = Color.parseColor("#EC9729");//节假日的颜色
    private int colorChoose = Color.WHITE;//选中的日期文字颜色
    private int sizeSolar = 14;//阳历日期文字尺寸
    private int sizeLunar = 8;//阴历日期文字尺寸
    private int dayBg = R.drawable.blue_circle;//选中的背景
    private Map<String, String> specifyMap;//指定日期对应的文字map
    private int chooseType = 0;//表示日历是单选还是多选

    public int[] getStartDate() {
        return startDate;
    }

    public void setStartDate(int[] startDate) {
        this.startDate = startDate;
    }

    public int[] getEndDate() {
        return endDate;
    }

    public void setEndDate(int[] endDate) {
        this.endDate = endDate;
    }

    public int[] getSingleDate() {
        return singleDate;
    }

    public void setSingleDate(int[] singleDate) {
        this.singleDate = singleDate;
    }

    public List<int[]> getMultiDates() {
        return multiDates;
    }

    public int[] getDisableStartDate() {
        return disableStartDate;
    }

    public void setDisableStartDate(int[] disableStartDate) {
        this.disableStartDate = disableStartDate;
    }

    public int[] getDisableEndDate() {
        return disableEndDate;
    }

    public void setDisableEndDate(int[] disableEndDate) {
        this.disableEndDate = disableEndDate;
    }

    public void setMultiDates(List<int[]> multiDates) {
        this.multiDates = multiDates;
    }

    public boolean isShowLastNext() {
        return showLastNext;
    }

    public void setShowLastNext(boolean showLastNext) {
        this.showLastNext = showLastNext;
    }

    public boolean isShowLunar() {
        return showLunar;
    }

    public void setShowLunar(boolean showLunar) {
        this.showLunar = showLunar;
    }

    public boolean isShowHoliday() {
        return showHoliday;
    }

    public void setShowHoliday(boolean showHoliday) {
        this.showHoliday = showHoliday;
    }

    public boolean isShowTerm() {
        return showTerm;
    }

    public void setShowTerm(boolean showTerm) {
        this.showTerm = showTerm;
    }

    public boolean isSwitchChoose() {
        return switchChoose;
    }

    public void setSwitchChoose(boolean switchChoose) {
        this.switchChoose = switchChoose;
    }

    public int getColorSolar() {
        return colorSolar;
    }

    public void setColorSolar(int colorSolar) {
        this.colorSolar = colorSolar;
    }

    public int getColorLunar() {
        return colorLunar;
    }

    public void setColorLunar(int colorLunar) {
        this.colorLunar = colorLunar;
    }

    public int getColorHoliday() {
        return colorHoliday;
    }

    public void setColorHoliday(int colorHoliday) {
        this.colorHoliday = colorHoliday;
    }

    public int getColorChoose() {
        return colorChoose;
    }

    public void setColorChoose(int colorChoose) {
        this.colorChoose = colorChoose;
    }

    public int getSizeSolar() {
        return sizeSolar;
    }

    public void setSizeSolar(int sizeSolar) {
        this.sizeSolar = sizeSolar;
    }

    public int getSizeLunar() {
        return sizeLunar;
    }

    public void setSizeLunar(int sizeLunar) {
        this.sizeLunar = sizeLunar;
    }

    public int getDayBg() {
        return dayBg;
    }

    public void setDayBg(int dayBg) {
        this.dayBg = dayBg;
    }

    public Map<String, String> getSpecifyMap() {
        return specifyMap;
    }

    public void setSpecifyMap(Map<String, String> specifyMap) {
        this.specifyMap = specifyMap;
    }

    public int getChooseType() {
        return chooseType;
    }

    public void setChooseType(int chooseType) {
        this.chooseType = chooseType;
    }
}
