package com.othershe.calendarview.bean;

import android.graphics.Color;

import com.othershe.calendarview.R;

import java.util.Map;

public class AttrsBean {

    private int[] dateStart;//日历的开始年、月
    private int[] dateEnd;//日历的结束年、月
    private int[] dateInit;//默认展示、选中的日期（年、月、日）
    private boolean showLastNext = true;//是否显示上个月、下个月
    private boolean showLunar = true;//是否显示农历
    private boolean showHoliday = true;//是否显示节假日(不显示农历则节假日无法显示，节假日会覆盖农历显示)
    private boolean showTerm = true;//是否显示节气
    private boolean disableBefore = false;//是否禁用默认选中日期前的所有日期
    private boolean switchChoose = true;//单选时切换月份，是否选中上次的日期
    private int colorSolar = Color.BLACK;//阳历的日期颜色
    private int colorLunar = Color.parseColor("#999999");//阴历的日期颜色
    private int colorHoliday = Color.parseColor("#EC9729");//节假日的颜色
    private int colorChoose = Color.WHITE;//选中的日期文字颜色
    private int sizeSolar = 14;//阳历日期文字尺寸
    private int sizeLunar = 8;//阴历日期文字尺寸
    private int dayBg = R.drawable.blue_circle;//选中的背景
    private Map<String, String> specifyMap;//指定日期对应的文字map
    private boolean showDateInit = true;//是否标记默认日期

    public int[] getDateStart() {
        return dateStart;
    }

    public void setDateStart(int[] dateStart) {
        this.dateStart = dateStart;
    }

    public int[] getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(int[] dateEnd) {
        this.dateEnd = dateEnd;
    }

    public int[] getDateInit() {
        return dateInit;
    }

    public void setDateInit(int[] dateInit) {
        this.dateInit = dateInit;
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

    public boolean isDisableBefore() {
        return disableBefore;
    }

    public void setDisableBefore(boolean disableBefore) {
        this.disableBefore = disableBefore;
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

    public boolean isShowDateInit() {
        return showDateInit;
    }

    public void setShowDateInit(boolean showDateInit) {
        this.showDateInit = showDateInit;
    }
}
