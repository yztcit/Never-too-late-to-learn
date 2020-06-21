package com.nttn.coolandroid.custom.calender;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.nttn.coolandroid.R;

import java.util.List;


public class CalendarRangeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private List<DateBean> list;

    private int[] selectRange = new int[2];

    private boolean canSelectRange  = false;
    private boolean canSelectBefore = false;
    private boolean canSelectNow    = false;

    private final int TYPE_MONTH = 0;
    private final int TYPE_DAY = 1;

    public CalendarRangeAdapter(Context context, List<DateBean> list) {
        this.context = context;
        this.list = list;
        initSelect();
    }

    public CalendarRangeAdapter canSelectRange(boolean canSelectRange) {
        this.canSelectRange = canSelectRange;
        return this;
    }

    public CalendarRangeAdapter canSelectNow(boolean canSelectNow) {
        this.canSelectNow = canSelectNow;
        return this;
    }

    public CalendarRangeAdapter canSelectBefore(boolean canSelectBefore) {
        this.canSelectBefore = canSelectBefore;
        return this;
    }

    public void initSelect() {
        clearSelect();
    }

    public void clearSelect() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSelect(false);
            list.get(i).setSelectRange(false);
            list.get(i).setBottomString("");
        }

        selectRange[0] = -1;
        selectRange[1] = -1;
    }

    public void notifySelect() {
        notifyDataSetChanged();
    }

    public void setData(List<DateBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == 0) {
            //月份
            View view = LayoutInflater.from(context).inflate(R.layout.item_calender_month,
                    parent, false);
            holder = new MonthViewHolder(view);
        } else {
            //日期
            View view = LayoutInflater.from(context).inflate(R.layout.item_calender_day,
                    parent, false);
            holder = new DayViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final int fPosition = holder.getAdapterPosition();
        String date = list.get(position).getDate();
        if (holder instanceof MonthViewHolder) {
            ((MonthViewHolder) holder).tvMonth.setText(String.format("%s月", date.replace("-", "年")));
        } else {
            final DayViewHolder viewHolder = (DayViewHolder) holder;

            viewHolder.tvCenter.setText(list.get(fPosition).getCenterString());

            if (viewHolder.llDay.getTag() instanceof View.OnClickListener){
                viewHolder.llDay.setOnClickListener(null);
            }
            if (TextUtils.isEmpty(date)) {
                viewHolder.llDay.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.color_calendar_background_normal));
                return;
            }
            final int compareTo = TimeUtils.compareTo(date);
            if (list.get(fPosition).isCanSelect()) {
                //今天之前（不可选）
                if (compareTo < 0) {
                    viewHolder.tvCenter.setTextColor(
                            ContextCompat.getColor(context, R.color.color_calendar_can_not_select));
                } else {
                    if (compareTo > 0) {
                        viewHolder.tvCenter.setTextColor(
                                ContextCompat.getColor(context, R.color.color_calendar_can_select));
                    } else {
                        viewHolder.tvCenter.setTextColor(
                                ContextCompat.getColor(context, R.color.color_calendar_today));
                        list.get(fPosition).setCanSelect(canSelectNow);
                    }
                }
            } else {
                viewHolder.llDay.setBackgroundColor(ContextCompat.getColor(context, R.color.color_calendar_background_normal));
                viewHolder.tvCenter.setTextColor(ContextCompat.getColor(context, R.color.color_calendar_can_not_select));
            }

            if (list.get(fPosition).isSelect()) {
                viewHolder.llDay.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.color_calendar_background_select));
                viewHolder.tvCenter.setTextColor(
                        ContextCompat.getColor(context, R.color.color_calendar_select));
            } else if (list.get(fPosition).isSelectRange()) {
                viewHolder.llDay.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.color_calendar_background_select_range));
                viewHolder.tvCenter.setTextColor(
                        ContextCompat.getColor(context, R.color.color_calendar_select));
            } else {
                viewHolder.llDay.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.color_calendar_background_normal));
            }

            if (onItemSelect != null) {
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!canSelectBefore) {
                            if (compareTo < 0) return;
                        }
                        if (canSelectRange) {
                            selectRange(fPosition);
                        } else {
                            if (compareTo == 0 && !canSelectNow)
                                return;
                            selectSingle(fPosition);
                        }
                    }
                };
                viewHolder.llDay.setOnClickListener(clickListener);
                viewHolder.llDay.setTag(clickListener);
            }
        }
    }

    private void selectSingle(int fPosition) {
        clearSelect();
        DateBean dateBean = list.get(fPosition);
        dateBean.setSelect(true);
        onItemSelect.onItemClick(fPosition, dateBean.getDate());
        notifyDataSetChanged();
    }

    private void selectRange(int fPosition) {
        DateBean dateBean = list.get(fPosition);
        if (selectRange[0] == -1 && selectRange[1] == -1) {
            selectRange[0] = fPosition;
            dateBean.setSelect(true);
            dateBean.setBottomString("开始");
            onItemSelect.onItemClick(fPosition, dateBean.getDate());
            notifyDataSetChanged();

        } else if (selectRange[0] != -1 && selectRange[1] == -1) {
            onItemSelect.onItemClick(fPosition, dateBean.getDate());
            if (fPosition < selectRange[0]) {
                clearSelect();
                selectRange[0] = fPosition;
                dateBean.setSelect(true);
                dateBean.setBottomString("开始");
            } else if (fPosition > selectRange[0]) {
                selectRange[1] = fPosition;
                dateBean.setSelect(true);
                dateBean.setBottomString("结束");
                for (int i = selectRange[0] + 1; i < selectRange[1]; i++) {
                    list.get(i).setSelectRange(true);
                    list.get(i).setBottomString("");
                }
                onItemSelect.onItemRangeSelect(list.get(selectRange[0]).getDate(), list.get(selectRange[1]).getDate());
            } else {
                clearSelect();
            }
            notifyDataSetChanged();
        } else {
            clearSelect();
            selectRange[0] = fPosition;
            dateBean.setSelect(true);
            dateBean.setBottomString("开始");
            onItemSelect.onItemClick(fPosition,  dateBean.getDate());
            notifyDataSetChanged();
        }
    }

    public OnItemSelect onItemSelect;


    public void setOnItemSelect(OnItemSelect onItemSelect) {
        this.onItemSelect = onItemSelect;
    }

    public interface OnItemSelect {
        void onItemClick(int position, String date);

        void onItemRangeSelect(String startDate, String endDate);
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType() == 0 ? TYPE_MONTH : TYPE_DAY;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    static class MonthViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth;

        public MonthViewHolder(View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tv_date);
        }
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llDay;
        TextView tvCenter;

        public DayViewHolder(View itemView) {
            super(itemView);
            llDay = itemView.findViewById(R.id.ll_day);
            tvCenter = itemView.findViewById(R.id.tv_day);
        }
    }
}
