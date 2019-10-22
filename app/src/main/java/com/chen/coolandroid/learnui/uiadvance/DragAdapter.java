package com.chen.coolandroid.learnui.uiadvance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chen.coolandroid.R;
import com.chen.coolandroid.learnui.widget.drag.IItemTouchHelper;

import java.util.Collections;
import java.util.List;

public class DragAdapter extends RecyclerView.Adapter<DragAdapter.VH> implements IItemTouchHelper {
    private Context context;
    private List<String> data;
    private LayoutInflater inflater;
    private OnItemMovedListener itemMovedListener;

    public DragAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_rcv_drag, viewGroup, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        vh.textView.setText(data.get(i));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public void onItemMove(RecyclerView.ViewHolder holder, int fromPosition, int targetPosition) {
        if (fromPosition < data.size() && targetPosition < data.size()) {
            if (itemMovedListener != null) {
                itemMovedListener.onItemMoved(fromPosition, targetPosition);
            }
            //下面注释的代码，滑动后数据和条目错乱，被舍弃
            //Collections.swap(mFunctionBeans, fromPosition, targetPosition);
            //notifyItemMoved(fromPosition, targetPosition);

            if (fromPosition < targetPosition) {
                for (int i = fromPosition; i < targetPosition; i++) {
                    Collections.swap(data, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > targetPosition; i--) {
                    Collections.swap(data, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, targetPosition);
        }
    }

    @Override
    public void onItemSelect(RecyclerView.ViewHolder holder) {

    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder holder) {
        //有背景色的item拖拽会出现背景色重叠，拖拽结束后设置透明的背景色来消除
        //holder.itemView.setBackgroundColor(getColorId(context, R.color.transparent));
        notifyDataSetChanged();
    }

    @Override
    public void onItemSwipe(RecyclerView.ViewHolder holder) {
        int adapterPosition = holder.getAdapterPosition();
        data.remove(adapterPosition);
        notifyDataSetChanged();
    }

    public static class VH extends RecyclerView.ViewHolder {
        private TextView textView;

        public VH(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_test);
        }
    }

    public interface OnItemMovedListener {
        void onItemMoved(int fromPosition, int targetPosition);
    }

    public void setOnItemMovedListener(OnItemMovedListener itemMovedListener) {
        this.itemMovedListener = itemMovedListener;
    }

    public int getColorId(Context context, int colorId) {
        return context.getResources().getColor(colorId);
    }
}
