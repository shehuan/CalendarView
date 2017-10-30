# CalendarView

### 功能
* 1、支持农历、节气、常用节假日
* 2、日期范围设置，默认支持的最大日期范围[1900.1~2049.12]
* 3、默认选中日期设置
* 4、单选、多选
* 5、跳转到指定日期
* 6、通过自定义属性定制日期外观，以及简单的日期item布局配置

#### [基本原理介绍](http://www.jianshu.com/p/304c8e70d0bd)    
#### [demo下载](https://fir.im/vehj?release_id=59154975ca87a8790e00015b)

### 效果图：

![](https://github.com/Othershe/CalendarView/blob/master/screenshot/1.gif)
![](https://github.com/Othershe/CalendarView/blob/master/screenshot/2.gif)
![](https://github.com/Othershe/CalendarView/blob/master/screenshot/3.gif)

### 基本用法：
**Step 1. 添加JitPack仓库**
在当前项目等根目录下的 `build.gradle` 文件中添加如下内容:
``` gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
**Step 2. 添加项目依赖**
``` gradle
dependencies {
        compile 'com.github.Othershe:CalendarView:1.0.1'
}
```
**Step 3. 在布局文件中添加WeekView、CalendarView**
```java
<com.othershe.calendarview.WeekView
        android:layout_width="match_parent"
        android:layout_height="35dp" />
        
<com.othershe.calendarview.CalendarView
        android:id="@+id/calendar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```
**Step 4. 相关初始化**
```java
CalendarView calendarView = (CalendarView) findViewById(R.id.calendar);
//日历init
calendarView.init();
//自定义日期ietm的布局样式
calendarView.setOnCalendarViewAdapter(R.layout.item_layout, new CalendarViewAdapter() {
            @Override
            public TextView[] convertView(View view, DateBean date) {
                TextView solarDay = (TextView) view.findViewById(R.id.solar_day);
                TextView lunarDay = (TextView) view.findViewById(R.id.lunar_day);
                return new TextView[]{solarDay, lunarDay};
            }
        });
*注意：init()和setOnCalendarViewAdapter()不要同时使用

//月份切换回调
calendarView.setOnPagerChangeListener(new OnPagerChangeListener() {
            @Override
            public void onPagerChanged(int[] date) {
                
            }
        });
        
//日期点击回调
calendarView.setOnItemClickListener(new OnMonthItemClickListener() {
            @Override
            public void onMonthItemClick(View view, DateBean date) {
                
            }
        });
        
//日期多选回调
calendarView.setOnMonthItemChooseListener(new OnMonthItemChooseListener() {
            @Override
            public void onMonthItemChoose(View view, DateBean date, boolean flag) {
               //flag=true代表选中数据，flag=false代表取消选中
            }
        });
*注意：点击和多选操作不要同时使用

```
### CalendarView相关方法：
|方法名|描述
|---|---|
|today()| 跳转到今天
|toSpecifyDate(int year, int month, int day)|跳转到指定年月日
|nextMonth()|跳转到下个月
|lastMonth()|跳转到上个月
|nextYear()|跳转到下一年的当前月
|lastYear()|跳转到上一年的当前月
|toStart()|跳转到日历的开始年月
|toEnd()|跳转到日历的结束年月
|getDateInit()|得到设置的默认选中日期（设置则返回当天日期）

### CalendarView的自定义属性
namespace：xmlns:calendarview="http://schemas.android.com/apk/res-auto"

|属性名|格式|描述|默认值
|---|---|---|---|
|show_lunar|boolean|是否显示农历|true
|show_last_next|boolean|是否在MonthView显示上月和下月日期|true
|show_holiday|boolean|是否显示节假日|true
|show_term|boolean|是否显示节气|true
|date_start|string|日历的开始年月（例如：1990.5）|1900.1
|date_end|string|日历的结束年月（例如：2025.12）|2049.12
|date_init|string|日历默认展示、选中的日期(例如：2017.5.20)，不设置则为当天
|disable_before|boolean|是否禁用默认选中日期前的所有日期|false
|switch_choose|boolean|单选时切换月份，是否选中上次的日期|true
|color_solar|color|阳历日期的颜色
|size_solar|integer|阳历的日期尺寸|14
|color_lunar|color|农历的日期颜色
|size_lunar|integer|农历的日期尺寸|8
|color_holiday|color|节假日、节气的颜色
|color_choose|color|选中的日期颜色
|day_bg|reference|选中的日期背景(图片)

### WeekView的自定义属性
namespace：xmlns:weekview="http://schemas.android.com/apk/res-auto"

|属性名|格式|描述|默认值
|---|---|---|---|
|week_str|string|周的表示形式，用点隔开（例如：日.一.二.三.四.五.六）
|week_color|color|周的颜色
|week_size|integer|周的尺寸|12
