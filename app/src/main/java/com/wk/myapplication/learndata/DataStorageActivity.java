package com.wk.myapplication.learndata;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.wk.myapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static android.os.Environment.DIRECTORY_ALARMS;
import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.DIRECTORY_MUSIC;
import static android.os.Environment.DIRECTORY_NOTIFICATIONS;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.DIRECTORY_PODCASTS;
import static android.os.Environment.DIRECTORY_RINGTONES;

public class DataStorageActivity extends AppCompatActivity {

    private static final String TAG = "DataStorageActivity";
    private ListView mLVDataFiles;
    private TextView mTVHintTitle;

    private Context mContext;
    private ArrayList<String> mFilesPath = new ArrayList<>();//adapter data,only 1 sub file path;
    private ArrayList<File> mFiles = new ArrayList<>();
    private Stack<ArrayList<String>> mFilePathStack;//record file path;
    private Stack<String> mFileDirStack;//record&show file path at title;
    private ArrayAdapter<String> mFilesAdapter;
    private int mItemInner = 0;//record inner times,default 0 is parent,others is subs;
    private int scrollPosition;
    private String mTitle;

    public static final String[] STANDARD_DIRECTORIES = {
            DIRECTORY_MUSIC,
            DIRECTORY_PODCASTS,
            DIRECTORY_RINGTONES,
            DIRECTORY_ALARMS,
            DIRECTORY_NOTIFICATIONS,
            DIRECTORY_PICTURES,
            DIRECTORY_MOVIES,
            DIRECTORY_DOWNLOADS,
            DIRECTORY_DCIM,
            DIRECTORY_DOCUMENTS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_storage);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mItemInner = 0;
        mFilesPath = null;
        mFiles = null;
        mFilePathStack = null;
    }

    @Override
    public void onBackPressed() {
        if (mItemInner > 0) {
            mItemInner--;
            mTVHintTitle.setHint(getDirTitle());
            mFilesPath.clear();
            mFilesPath.addAll(mFilePathStack.pop());
            mFilesAdapter.notifyDataSetChanged();
            mLVDataFiles.setSelection(scrollPosition);
            return;
        }
        super.onBackPressed();
    }

    private void initView() {
        mTVHintTitle = (TextView) findViewById(R.id.tv_data_storage);
        mLVDataFiles = (ListView) findViewById(R.id.lv_data_file);
    }

    private void initData() {
        mContext = this;
        mFilePathStack = new Stack<>();//file path collection stack;
        mFileDirStack = new Stack<>();//title stack;
        //记录当前title,防止pop重复;
        mTitle = mContext.getString(R.string.learn_data_storage);
        mFileDirStack.push(mTitle);

        updateFileDir();//获取各种路径，填充ArrayAdapter

        mLVDataFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //record current position;
                scrollPosition = mLVDataFiles.getFirstVisiblePosition();
                mTitle = (String) parent.getItemAtPosition(position);
                String filePath = mFilesPath.get(position);
                if (TextUtils.equals(mTitle, filePath)) {
                    File filePosition = mFiles.get(position);
                    boolean isDir = FileUtils.isDir(filePosition);
                    if (isDir) {
                        mItemInner++;
                        mFileDirStack.push(mTitle + "/");
                        mTVHintTitle.setHint(mTitle);

                        ArrayList<String> tempFilesPath = new ArrayList<>();//temp file path collection;
                        tempFilesPath.addAll(mFilesPath);
                        mFilePathStack.push(tempFilesPath);

                        //clear&reset ArrayList mFilesPath and refresh adapter;
                        List<File> files = FileUtils.listFilesInDir(filePosition);
                        mFilesPath.clear();
                        for (File file : files) {
                            mFilesPath.add(file.toString());
                        }
                        mFilesAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /**
     * 获取各种文件路径
     */
    private void updateFileDir() {
        /**
         * internal
         */
        mFilesPath.add(Environment.getDataDirectory().toString());
        mFiles.add(Environment.getDataDirectory());
        mFilesPath.add(Environment.getDownloadCacheDirectory().toString());
        mFiles.add(Environment.getDownloadCacheDirectory());
        mFilesPath.add(Environment.getRootDirectory().toString());
        mFiles.add(Environment.getRootDirectory());
        /**
         * external
         */
        for (String str : STANDARD_DIRECTORIES) {//9 public external dir
            mFilesPath.add(Environment.getExternalStoragePublicDirectory(str).toString());
            mFiles.add(Environment.getExternalStoragePublicDirectory(str));
        }
        /**
         * private---ues Context to get
         */
        if (mContext.getExternalCacheDir() != null) {
            mFilesPath.add(mContext.getExternalCacheDir().toString());
            mFiles.add(mContext.getExternalCacheDir());
        }
        for (String str : STANDARD_DIRECTORIES) {//9 public external dir
            File externalFilesDir = mContext.getExternalFilesDir(str);
            if (externalFilesDir != null) {
                mFilesPath.add(externalFilesDir.toString());
                mFiles.add(externalFilesDir);
            }
        }
        mFilesPath.add(mContext.getCacheDir().toString());
        mFiles.add(mContext.getCacheDir());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//21 OS version 5.0
            mFilesPath.add(mContext.getCodeCacheDir().toString());
            mFiles.add(mContext.getCodeCacheDir());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//24 OS version 7.0
            mFilesPath.add(mContext.getDataDir().toString());
            mFiles.add(mContext.getDataDir());
        }

        mFilesAdapter = new ArrayAdapter<String>(
                mContext, android.R.layout.simple_list_item_1, mFilesPath);
        mLVDataFiles.setAdapter(mFilesAdapter);
    }

    private String getDirTitle() {
        mTitle = mFileDirStack.pop();
        return mTitle = mFileDirStack.peek();
    }
}
