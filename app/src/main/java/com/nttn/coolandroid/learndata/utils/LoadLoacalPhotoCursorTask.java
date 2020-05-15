package com.nttn.coolandroid.learndata.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Chen on 2018/4/28.
 * <p>
 * 获取本地图片的异步线程类
 */

public class LoadLoacalPhotoCursorTask extends AsyncTask<Object, Object, Object> {
    private Context mContext;
    //分类并将结果存储到mGroupMap（Key是文件夹名，Value是文件夹中的图片路径orgId的List）中
    private HashMap<String, List<Long>> mGroupMap = new HashMap<>();
    private final ContentResolver mContentResolver;
    private boolean mExitTasksEarly = false;//退出任务线程的标志位
    private OnLoadPhotoCursor onLoadPhotoCursor;//定义回调接口，获取解析到的数据

    private ArrayList<Uri> uriArray = new ArrayList<Uri>();//存放图片URI
    private ArrayList<Long> origIdArray = new ArrayList<Long>();//存放图片ID

    public LoadLoacalPhotoCursorTask(Context mContext) {
        this.mContext = mContext;
        mContentResolver = mContext.getContentResolver();
    }

    @Override
    protected Object doInBackground(Object... params) {
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        Uri ex_uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri in_uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        String where = MediaStore.Images.Media.SIZE + ">=?";
        /**
         * 这个查询操作完成图片大小大于100K的图片的ID查询。
         * 大家可能疑惑为什么不查询得到图片DATA呢？
         * 这样是为了节省内存。通过图片的ID可以查询得到指定的图片
         * 如果这里就把图片数据查询得到，手机中的图片大量的情况下
         * 内存消耗严重。那么，什么时候查询图片呢？应该是在Adapter
         * 中完成指定的ID的图片的查询，并不一次性加载全部图片数据
         */
        Cursor c = MediaStore.Images.Media.query(
                mContentResolver,
                ex_uri,
                projection,
                /*where*/null,
                /*new String[]{100 * 1024 + ""}*/null,
                MediaStore.Images.Media.DATE_TAKEN + " desc");

        int columnIndex = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int bucketNameIndex = c.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        int i = 0;
        while (c.moveToNext() && i < c.getCount() && !mExitTasksEarly) {   //移到指定的位置，遍历数据库
            long origId = c.getLong(columnIndex);
            String bucketName = c.getString(bucketNameIndex);
            uriArray.add(Uri.withAppendedPath(ex_uri, origId + ""));

            origIdArray.add(origId);
            //根据父路径名将图片放入到mGruopMap中
            if (!mGroupMap.containsKey(bucketName)) {
                List<Long> childList = new ArrayList<>();
                childList.add(origId);
                mGroupMap.put(bucketName, childList);
            } else {
                mGroupMap.get(bucketName).add(origId);
            }
            c.moveToPosition(i);
            i++;
        }
        c.close();//关闭数据库
        if (mExitTasksEarly) {
            mGroupMap = new HashMap<>();
            uriArray = new ArrayList<Uri>();
            origIdArray = new ArrayList<Long>();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (onLoadPhotoCursor != null && !mExitTasksEarly) {
            /**
             * 查询完成之后，设置回调接口中的数据，把数据传递到Activity中
             */onLoadPhotoCursor.onLoadPhotoCursorResult(mGroupMap,uriArray, origIdArray);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();    //To change body of overridden methods use File | Settings | File Templates.
        mExitTasksEarly = true;
    }

    public void setExitTasksEarly(boolean exitTasksEarly) {
        this.mExitTasksEarly = exitTasksEarly;
    }

    public void setOnLoadPhotoCursor(OnLoadPhotoCursor onLoadPhotoCursor) {
        this.onLoadPhotoCursor = onLoadPhotoCursor;
    }

    public interface OnLoadPhotoCursor {
        void onLoadPhotoCursorResult(HashMap<String, List<Long>> groupMap, ArrayList<Uri> uriArray, ArrayList<Long> origIdArray);
    }
}
