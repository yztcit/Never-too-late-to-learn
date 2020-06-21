package com.nttn.coolandroid.custom.calender;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.nttn.coolandroid.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class CalenderPopHelper {
    private AppCompatActivity mContext;
    private RecyclerView recyclerView;
    private CalendarRangeAdapter calendarRangeAdapter;

    private CalendarRangeAdapter.OnItemSelect onItemSelect;
    private int mRefreshPage = 0;
    private int mLoadMorePage = 0;

    private View anchor;
    private int vertGravity;
    private int horizGravity;
    private int offX = 0;
    private int offY = 0;
    private int height = WindowManager.LayoutParams.WRAP_CONTENT;
    private int with = WindowManager.LayoutParams.MATCH_PARENT;

    private boolean canSelectBefore = false;
    private boolean canSelectNow    = true;
    private boolean canSelectRange  = false;
    private List<DateBean> dateList = new ArrayList<>();
    private List<DateBean> productList = new ArrayList<>();
    private List<DateBean> allDateList = new ArrayList<>();

    public CalenderPopHelper(AppCompatActivity context){
        mContext = context;
    }

    public CalenderPopHelper createCalenderPop(){
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_calender, null);

        return this;
    }


    public void show() {

    }

    public CalenderPopHelper dismiss() {

        return this;
    }

    public CalenderPopHelper setDateCheckedListener(CalendarRangeAdapter.OnItemSelect onItemSelect){
        this.onItemSelect = onItemSelect;
        if (calendarRangeAdapter != null) {
            calendarRangeAdapter.setOnItemSelect(onItemSelect);
        }
        return this;
    }

    public CalenderPopHelper setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {

        return this;
    }

    public boolean isCanSelectBefore() {
        return canSelectBefore;
    }

    public CalenderPopHelper setCanSelectBefore(boolean canSelectBefore) {
        this.canSelectBefore = canSelectBefore;
        return this;
    }

    public boolean isCanSelectNow() {
        return canSelectNow;
    }

    public CalenderPopHelper setCanSelectNow(boolean canSelectNow) {
        this.canSelectNow = canSelectNow;
        return this;
    }

    public boolean isCanSelectRange() {
        return canSelectRange;
    }

    public CalenderPopHelper setCanSelectRange(boolean canSelectRange) {
        this.canSelectRange = canSelectRange;
        return this;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            calendarRangeAdapter.setData(allDateList);
            //recyclerView.complete();
            super.handleMessage(msg);
        }
    };

    private void initRecyclerView(View rootView) {
        recyclerView = rootView.findViewById(R.id.rcy_calender);
        GridLayoutManager manager = new GridLayoutManager(mContext, 7){
            @Override
            public SpanSizeLookup getSpanSizeLookup() {
                return new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return allDateList.get(position).getType() == 0 ? 7 : 1;
                    }
                };
            }
        };
        recyclerView.setLayoutManager(manager);
        calendarRangeAdapter = new CalendarRangeAdapter(mContext, allDateList);
        calendarRangeAdapter.canSelectRange(canSelectRange).canSelectNow(canSelectNow)
                .canSelectBefore(canSelectBefore);
        recyclerView.setAdapter(calendarRangeAdapter);

        calendarRangeAdapter.setOnItemSelect(new CalendarRangeAdapter.OnItemSelect() {
            @Override
            public void onItemClick(int position, String date) {
                if (onItemSelect != null) {
                    onItemSelect.onItemClick(position, date);
                }
            }

            @Override
            public void onItemRangeSelect(String startDate, String endDate) {
                if (onItemSelect != null) {
                    onItemSelect.onItemRangeSelect(startDate, endDate);
                }
            }
        });

//        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
//            @Override
//            public void onRefresh() {
//                mRefreshPage --;
//                new Thread(){
//                    @Override
//                    public void run() {
//                        createDate(SwipeType.REFRESH);
//                    }
//                }.start();
//            }
//
//            @Override
//            public void onLoadMore() {
//                mLoadMorePage ++;
//                new Thread(){
//                    @Override
//                    public void run() {
//                        createDate(SwipeType.LOAD_MORE);
//                    }
//                }.start();
//            }
//        });
    }

//    private void createDate(@SwipeType int type) {
//        //1次1年
//        int a;
//        switch (type) {
//            case SwipeType.REFRESH:
//                a = mRefreshPage * 12;
//                break;
//            case SwipeType.LOAD_MORE:
//                a = mLoadMorePage * 12;
//                break;
//            case SwipeType.DEFAULT:
//            default:
//                a = 0;
//                break;
//        }
//        productList.clear();
//        for (int i = 0; i < 12; i++) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.MONTH, i + a);
//
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH) + 1;
//
//            int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//            String dateString = simpleDateFormat.format(calendar.getTime());
//
//            DateBean monthBean = new DateBean();
//            monthBean.setDate(dateString.substring(0, 7));
//            monthBean.setCanSelect(false);
//            monthBean.setType(0);
//
//            dateList.clear();
//            dateList.add(monthBean);
//
//            calendar.set(Calendar.DAY_OF_MONTH, 1);
//
//            //当月第一天是周几 0是周日，1是周一 以此类推
//            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//            //填充1号前面的空白
//            for (int j = 0; j < dayOfWeek - 1; j++) {
//                DateBean dateBean = new DateBean();
//                dateBean.setCanSelect(false);
//                dateBean.setSelect(false);
//                dateBean.setSelectRange(false);
//                dateBean.setType(1);
//                dateBean.setDate("");
//                dateList.add(dateBean);
//            }
//
//            for (int j = 0; j < maxDays; j++) {
//                DateBean dateBean = new DateBean();
//                dateBean.setType(1);
//                dateBean.setCenterString(String.valueOf(j + 1));
//                dateBean.setSelect(false);
//                String date = year + "-" + addZero(month) + "-" + addZero(j + 1);
//                dateBean.setDate(date);
//                //今天之前是否可选中
//                int compare = TimeUtils.compareTo(dateBean.getDate());
//                if (compare < 0) {
//                    dateBean.setCanSelect(false);
//                } else {
//                    dateBean.setCanSelect(true);
//                    if (compare == 0) {
//                        dateBean.setCenterString("今天");
//                    }
//                }
//                dateList.add(dateBean);
//            }
//
//            productList.addAll(dateList);
//        }
//        switch (type) {
//            case SwipeType.REFRESH:
//                productList.addAll(allDateList);
//                allDateList.clear();
//            case SwipeType.DEFAULT:
//            case SwipeType.LOAD_MORE:
//                allDateList.addAll(productList);
//                break;
//        }
//        Message msg = handler.obtainMessage();
//        handler.sendMessage(msg);
//    }

    private String addZero(int text) {
        if (text < 10) {
            return "0" + text;
        } else {
            return "" + text;
        }
    }

    private int getDisplayMetricsHeight() {
        DisplayMetrics dm;
        dm = mContext.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
}
