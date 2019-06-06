package com.dfsx.logonproject.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.andrew.datechoosewheelviewdemo.widget.OnWheelChangedListener;
import com.andrew.datechoosewheelviewdemo.widget.OnWheelScrollListener;
import com.andrew.datechoosewheelviewdemo.widget.WheelView;
import com.andrew.datechoosewheelviewdemo.widget.adapters.AbstractWheelTextAdapter;
import com.dfsx.logonproject.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heyang on  2018/4/19
 */
public class TimeBottomPopupwindow implements View.OnClickListener {
    private Context context;

    private PopupWindow popupWindow;

    private View view;

    //控件
    private WheelView mYearWheelView;
    private WheelView mDateWheelView;
    private WheelView mDayWheelView;
    private CalendarTextAdapter mDateAdapter;
    private CalendarTextAdapter mDayAdapter;
    private CalendarTextAdapter mYearAdapter;
    private Button mSureButton;
    private Button mCloseDialog;
    private TextView mLongTermTextView;

    //变量
    private ArrayList<String> arry_date = new ArrayList<String>();
    private ArrayList<String> arry_day = new ArrayList<String>();
    private ArrayList<String> arry_year = new ArrayList<String>();

    private int nowDateId = 0;
    private int nowDayId = 0;
    private int nowYearId = 0;
    private String mYearStr;
    private String mDateStr;
    private String mDayStr;

    private Map<String, ArrayList<String>> dayMap = new HashMap<>();
    DateChooseInterface dateChooseInterface;

    //常量
    private final int MAX_TEXT_SIZE = 18;
    private final int MIN_TEXT_SIZE = 16;

    private Context mContext;


    public TimeBottomPopupwindow(Context context, DateChooseInterface dateChooseInterface) {
        this.context = context;
        this.mContext = context;
        this.dateChooseInterface = dateChooseInterface;
        initView();
        initData();
    }

    private void initData() {
        initYear();
        initDate();
        initDay();
        initListener();
    }

    private void initView() {
        view = LayoutInflater.from(context).
                inflate(R.layout.item_time_popupwindow_layout, null);
        popupWindow = new PopupWindow(view);

        //这里需要设置成可以获取焦点，否则无法响应OnKey事件
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 这里用上了我们在popupWindow中定义的animation了
//        popupWindow.setAnimationStyle(R.style.UpInDownOutPopupStyle);
        Drawable drawable = context.getResources().getDrawable(R.color.transparent);
        popupWindow.setBackgroundDrawable(drawable);

        view.findViewById(R.id.empty_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mYearWheelView = (WheelView) view.findViewById(R.id.year_wv);
        mDateWheelView = (WheelView) view.findViewById(R.id.date_wv);
        mDayWheelView = (WheelView) view.findViewById(R.id.day_wv);
        mSureButton = (Button) view.findViewById(R.id.sure_btn);
        //      mCloseDialog = (Button) view.findViewById(R.id.date_choose_close_btn);
        //      mLongTermTextView = (TextView) view.findViewById(R.id.long_term_tv);

        mSureButton.setOnClickListener(this);
        //      mCloseDialog.setOnClickListener(this);
        //      mLongTermTextView.setOnClickListener(this);

        mSureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    String year = mYearStr.replace("年", "");
//                    int y = Integer.parseInt(year);
//                    String month = mDateStr.replace("月", "");
//                    int m = Integer.parseInt(month);
//                    String day = mDayStr.replace("日", "");
//                    int d = Integer.parseInt(day);
//                    int p[] = getMinDay(y, m, d);
//                    dateChooseInterface.getDateTime(strStratTimeToDateFormat2(mYearStr, mDateStr, mDayStr)
//                            , strEndTimeToDateFormat2(mYearStr, p[0] + "月", p[1] + "日"));

                    dateChooseInterface.getDateTime(strStratTimeToDateFormat2(mYearStr, mDateStr, mDayStr)
                            , strEndTimeToDateFormat2(mYearStr, mDateStr, mDayStr));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
    }

    private int[] getMinDay(int year, int month, int day) {
        boolean isRun = isRunNian(year);
        int nowMonth = month;
        int nowDay = day;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (day < 31) {
                    nowDay++;
                } else {
                    nowMonth++;
                    nowDay = 1;
                }
                break;
            case 2:
                if (isRun) {
                    if (day < 29) {
                        nowDay++;
                    } else {
                        nowMonth++;
                        nowDay = 1;
                    }
                } else {
                    if (day < 28) {
                        nowDay++;
                    } else {
                        nowMonth++;
                        nowDay = 1;
                    }
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (day < 30) {
                    nowDay++;
                } else {
                    nowMonth++;
                    nowDay = 1;
                }
                break;
            default:
                break;
        }
        int a[] = {nowMonth, nowDay};
        return a;
    }

    /**
     * 初始化年
     */
    private void initYear() {
        Calendar nowCalendar = Calendar.getInstance();
        int nowYear = nowCalendar.get(Calendar.YEAR);
        arry_year.clear();
        for (int i = 0; i <= 99; i++) {
            int year = nowYear - 90 + i;
            arry_year.add(year + "年");
            if (nowYear == year) {
                nowYearId = arry_year.size() - 1;
            }
        }
        mYearAdapter = new CalendarTextAdapter(mContext, arry_year, nowYearId, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
        mYearWheelView.setVisibleItems(5);
        mYearWheelView.setViewAdapter(mYearAdapter);
        mYearWheelView.setCurrentItem(nowYearId);
        mYearStr = arry_year.get(nowYearId);
    }


    /**
     * 初始化日期
     */
    private void initDay() {
        Calendar nowCalendar = Calendar.getInstance();
        int nowYear = nowCalendar.get(Calendar.YEAR);
        arry_day.clear();
        setDay(nowYear, -1);
        mDayAdapter = new CalendarTextAdapter(mContext, arry_day, nowDayId, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
        mDayWheelView.setVisibleItems(5);
        mDayWheelView.setViewAdapter(mDayAdapter);
        mDayWheelView.setCurrentItem(nowDayId);

        mDayStr = arry_day.get(nowDayId);
        setTextViewStyle(mDayStr, mDayAdapter);
    }


    private void initDate() {
        Calendar nowCalendar = Calendar.getInstance();
        int nowYear = nowCalendar.get(Calendar.YEAR);
        arry_date.clear();
        setDate(nowYear);
        mDateAdapter = new CalendarTextAdapter(mContext, arry_date, nowDateId, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
        mDateWheelView.setVisibleItems(5);
        mDateWheelView.setViewAdapter(mDateAdapter);
        mDateWheelView.setCurrentItem(nowDateId);

        mDateStr = arry_date.get(nowDateId);
        setTextViewStyle(mDateStr, mDateAdapter);
    }

    /**
     * 将改年的所有日期写入数组
     *
     * @param year
     */
    private void setDate(int year) {
        Calendar nowCalendar = Calendar.getInstance();
        int nowMonth = nowCalendar.get(Calendar.MONTH) + 1;
        for (int month = 1; month <= 12; month++) {
            arry_date.add(month + "月");
            if (month == nowMonth) {
                nowDateId = arry_date.size() - 1;
            }
        }
    }

    /**
     * 将改年的所有日期写入数组
     *
     * @param year
     */
    private void setDay(int year, int monthtf) {
        boolean isRun = isRunNian(year);
        Calendar nowCalendar = Calendar.getInstance();
        int nowMonth = monthtf;
        if (nowMonth == -1)
            nowMonth = nowCalendar.get(Calendar.MONTH) + 1;
        int nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);
        for (int month = 1; month <= 12; month++) {
            ArrayList<String> list = new ArrayList<>();
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    for (int day = 1; day <= 31; day++) {
                        list.add(day + "日");

                        if (month == nowMonth && day == nowDay) {
                            nowDayId = list.size() - 1;
                        }
                    }
                    dayMap.put(month + "月", list);
                    break;
                case 2:
                    if (isRun) {
                        for (int day = 1; day <= 29; day++) {
                            list.add(day + "日");
                            if (month == nowMonth && day == nowDay) {
                                nowDayId = list.size() - 1;
                            }
                        }
                    } else {
                        for (int day = 1; day <= 28; day++) {
                            list.add(day + "日");
                            if (month == nowMonth && day == nowDay) {
                                nowDayId = list.size() - 1;
                            }
                        }
                    }
                    dayMap.put(month + "月", list);
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    for (int day = 1; day <= 30; day++) {
                        list.add(day + "日");
                        if (month == nowMonth && day == nowDay) {
                            nowDayId = list.size() - 1;
                        }
                    }
                    dayMap.put(month + "月", list);
                    break;
                default:
                    break;
            }
        }
        arry_day = dayMap.get(nowMonth + "月");
    }

    /**
     * 判断是否是闰年
     *
     * @param year
     * @return
     */
    private boolean isRunNian(int year) {
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 设置文字的大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextViewStyle(String curriteItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(MAX_TEXT_SIZE);
                textvew.setTextColor(mContext.getResources().getColor(R.color.text_10));
            } else {
                textvew.setTextSize(MIN_TEXT_SIZE);
                textvew.setTextColor(mContext.getResources().getColor(R.color.text_11));
            }
        }
    }

//    private String strTimeToDateFormat(String yearStr, String dateStr, String hourStr, String minuteStr) {
//
//        return yearStr.replace("年", "-") + dateStr.replace("月", "-").replace("日", " ")
//                + hourStr + ":" + minuteStr;
//    }

    private String strTimeToDateFormat(String yearStr, String dateStr, String dayStr) {

        return yearStr.replace("年", "-") + dateStr.replace("月", "-") + dayStr.replace("日", "-");
    }

    private String strStratTimeToDateFormat2(String yearStr, String dateStr, String dayStr) {

        return yearStr.replace("年", "-") + dateStr.replace("月", "-") + dayStr.replace("日", " ") + "00:00:00";
    }

    private String strEndTimeToDateFormat2(String yearStr, String dateStr, String dayStr) {

        return yearStr.replace("年", "-") + dateStr.replace("月", "-") + dayStr.replace("日", " ") + "23:59:59";
    }

    /**
     * 初始化滚动监听事件
     */
    private void initListener() {
        //年份*****************************
        mYearWheelView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mYearAdapter);
                mYearStr = arry_year.get(wheel.getCurrentItem()) + "";
            }
        });

        mYearWheelView.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mYearAdapter);
            }
        });

        //日期********************
        mDateWheelView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mDateAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mDateAdapter);
//                mDateCalendarTextView.setText(" " + arry_date.get(wheel.getCurrentItem()));
                int mp = nowDateId;
                int mm = wheel.getCurrentItem();
                mDateStr = arry_date.get(wheel.getCurrentItem());
            }
        });

        mDateWheelView.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mDateAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mDateAdapter);
//                setDay(nowYearId,nowDateId);

                if (!(dayMap == null || dayMap.isEmpty())) {
                    arry_day = dayMap.get(mDateStr);
                    mDayAdapter.update(arry_day);
                    mDayWheelView.setCurrentItem(nowDayId);
                    String currentTexts = (String) mDayAdapter.getItemText(nowDayId);
                    setTextViewStyle(currentTexts, mDayAdapter);
                }
            }
        });

        //  天数   ===================================
        mDayWheelView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mDayAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mDayAdapter);
//                mDateCalendarTextView.setText(" " + arry_date.get(wheel.getCurrentItem()));
                mDayStr = arry_day.get(wheel.getCurrentItem());
            }
        });

        mDayWheelView.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mDayAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mDayAdapter);
            }
        });
    }

    /**
     * 滚轮的adapter
     */
    private class CalendarTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.item_birth_year, R.id.tempValue, currentItem, maxsize, minsize);
            this.list = list;
        }

        public void update(ArrayList<String> list) {
            this.list = list;
            notifyDataChangedEvent();
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            String str = list.get(index) + "";
            return str;
        }
    }

    /**
     * 回调选中的时间（默认时间格式"yyyy-MM-dd HH:mm:ss"）
     */
    public interface DateChooseInterface {
        void getDateTime(String start, String end);
    }


    public void show(View parent) {
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }


    @Override
    public void onClick(View v) {

    }
}
