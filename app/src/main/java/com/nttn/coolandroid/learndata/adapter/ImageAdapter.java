package com.nttn.coolandroid.learndata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nttn.coolandroid.R;
import com.nttn.coolandroid.learndata.utils.ImageWorker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Chen on 2018/4/28.
 *
 */

public class ImageAdapter extends BaseAdapter {
    private ImageWorker imageWorker;
    private HashMap<Long, Boolean> seletedMap = new HashMap<Long, Boolean>();
    private ArrayList<Long> origIdArray = new ArrayList<Long>();

    private LayoutInflater mInflater;
    //构造器
    public ImageAdapter(ImageWorker imageWorker, Context context) {
        this.imageWorker = imageWorker;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return origIdArray.size();
    }

    @Override
    public Object getItem(int position) {
        return origIdArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return origIdArray.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_gv_images, parent, false);

            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.iv_images_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final long origId = origIdArray.get(position);
        //加载图片
        imageWorker.loadImage(origId, holder.img);
        return convertView;
    }

    public ImageAdapter putSelectMap(Long origId, Boolean isChecked) {
        seletedMap.put(origId, isChecked);
        return this;
    }

    public ImageAdapter setOrigIdArray(ArrayList<Long> origIdArray) {
        this.origIdArray = origIdArray;
        return this;
    }

    private static class ViewHolder {
        ImageView img;
    }
}
