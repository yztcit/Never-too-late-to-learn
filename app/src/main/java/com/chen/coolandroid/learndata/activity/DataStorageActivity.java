package com.chen.coolandroid.learndata.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.chen.coolandroid.R;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

public class DataStorageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DataStorageActivity";
    private ListView mLVDataFiles;
    private TextView mTVHintTitle;

    private Button mBtnImages;
    private Button mBtnAudios;
    private Button mBtnVideos;
    private Button mBtnDocuments;
    private Button mBtnArchives;
    private Button mBtnApps;
    private Button mBtnDownloads;
    private Button mBtnMore;

    private Context mContext;
    private ArrayList<String> mFilesPath = new ArrayList<>();//adapter data,only 1 sub file path;
    //    private ArrayList<File> mFiles = new ArrayList<>();
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
//        mFiles = null;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_data_images:
                CategoryImagesActivity.actionStart(mContext);
                break;
            case R.id.btn_data_audios:
                CategoryAudiosActivity.actionStart(mContext);
                break;
            case R.id.btn_data_videos:
                CategoryVideosActivity.actionStart(mContext);
                break;
            case R.id.btn_data_documents:
                CategoryDocumentsActivity.actionStart(mContext);
                break;
            case R.id.btn_data_archives:
                CategoryArchivesActivity.actionStart(mContext);
                break;
            case R.id.btn_data_apps:
                CategoryAppsActivity.actionStart(mContext);
                break;
            case R.id.btn_data_downloads:
                CategoryDownloadsActivity.actionStart(mContext);
                break;
            case R.id.btn_data_more:
                CategoryMoreActivity.actionStart(mContext);
                break;
            default:
                break;
        }
    }

    private void initView() {
        mTVHintTitle = (TextView) findViewById(R.id.tv_data_storage);
        mLVDataFiles = (ListView) findViewById(R.id.lv_data_file);

        mBtnImages = (Button) findViewById(R.id.btn_data_images);
        mBtnAudios = (Button) findViewById(R.id.btn_data_audios);
        mBtnVideos = (Button) findViewById(R.id.btn_data_videos);
        mBtnDocuments = (Button) findViewById(R.id.btn_data_documents);
        mBtnArchives = (Button) findViewById(R.id.btn_data_archives);
        mBtnApps = (Button) findViewById(R.id.btn_data_apps);
        mBtnDownloads = (Button) findViewById(R.id.btn_data_downloads);
        mBtnMore = (Button) findViewById(R.id.btn_data_more);
    }

    private void initData() {
        mContext = this;
        aboutFileDir();

        initButtonClickListener();

    }

    private void initButtonClickListener() {
        mBtnImages.setOnClickListener(this);
        mBtnAudios.setOnClickListener(this);
        mBtnVideos.setOnClickListener(this);
        mBtnDocuments.setOnClickListener(this);
        mBtnArchives.setOnClickListener(this);
        mBtnApps.setOnClickListener(this);
        mBtnDownloads.setOnClickListener(this);
        mBtnMore.setOnClickListener(this);
    }

    private void aboutFileDir() {
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
                // FIXME: 2018/4/28 应该设置多维数组记录不同层级滑动的位置
                scrollPosition = mLVDataFiles.getFirstVisiblePosition();

                mTitle = (String) parent.getItemAtPosition(position);
                String filePath = mFilesPath.get(position);
                if (TextUtils.equals(mTitle, filePath)) {
                    boolean isDir = FileUtils.isDir(filePath);
                    if (isDir) {
                        mItemInner++;
                        mFileDirStack.push(mTitle + "/");
                        mTVHintTitle.setHint(mTitle);

                        ArrayList<String> tempFilesPath = new ArrayList<>();//temp file path collection;
                        tempFilesPath.addAll(mFilesPath);
                        mFilePathStack.push(tempFilesPath);

                        //clear&reset ArrayList mFilesPath and refresh adapter;
                        List<File> files = FileUtils.listFilesInDir(filePath);
                        mFilesPath.clear();
                        for (File file : files) {
                            mFilesPath.add(file.toString());
                        }
                    } else {
                        Toast.makeText(mContext, "No more files,for this is a file.", Toast.LENGTH_SHORT).show();
                    }
                    mFilesAdapter.notifyDataSetChanged();
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
//        mFiles.add(Environment.getDataDirectory());
        mFilesPath.add(Environment.getDownloadCacheDirectory().toString());
//        mFiles.add(Environment.getDownloadCacheDirectory());
        mFilesPath.add(Environment.getRootDirectory().toString());
//        mFiles.add(Environment.getRootDirectory());
        /**
         * external
         */
        for (String str : STANDARD_DIRECTORIES) {//9 public external dir
            mFilesPath.add(Environment.getExternalStoragePublicDirectory(str).toString());
//            mFiles.add(Environment.getExternalStoragePublicDirectory(str));
        }
        /**
         * private---ues Context to get
         */
        if (mContext.getExternalCacheDir() != null) {
            mFilesPath.add(mContext.getExternalCacheDir().toString());
//            mFiles.add(mContext.getExternalCacheDir());
        }
        for (String str : STANDARD_DIRECTORIES) {//9 public external dir
            File externalFilesDir = mContext.getExternalFilesDir(str);
            if (externalFilesDir != null) {
                mFilesPath.add(externalFilesDir.toString());
//                mFiles.add(externalFilesDir);
            }
        }
        mFilesPath.add(mContext.getCacheDir().toString());
//        mFiles.add(mContext.getCacheDir());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//21 OS version 5.0
            mFilesPath.add(mContext.getCodeCacheDir().toString());
//            mFiles.add(mContext.getCodeCacheDir());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//24 OS version 7.0
            mFilesPath.add(mContext.getDataDir().toString());
//            mFiles.add(mContext.getDataDir());
        }
        /**
         * SD card
         */
        String extendedMemoryPath = getExtendedMemoryPath(mContext);
        if (extendedMemoryPath == null) {
            mFilesPath.add("/storage/exSDCard---null");
        } else {
            mFilesPath.add(extendedMemoryPath);
        }

        mFilesAdapter = new ArrayAdapter<String>(
                mContext, android.R.layout.simple_list_item_1, mFilesPath);
        mLVDataFiles.setAdapter(mFilesAdapter);
    }

    private String getDirTitle() {
        mTitle = mFileDirStack.pop();

        return mTitle = mFileDirStack.peek();
    }

    /**
     * 反射方式获取拓展卡路径
     * @param mContext 上下文对象
     * @return 拓展卡路径
     */
    private String getExtendedMemoryPath(Context mContext) {
        try {
            StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
            Class<?> storageVolumeClazz = null;
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            if (storageVolumeClazz != null) {

                Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
                Method getPath = storageVolumeClazz.getMethod("getPath");
                Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
                Object result = getVolumeList.invoke(mStorageManager);
                final int length = Array.getLength(result);
                for (int i = 0; i < length; i++) {
                    Object storageVolumeElement = Array.get(result, i);
                    String path = (String) getPath.invoke(storageVolumeElement);
                    boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                    if (removable) {
                        return path;
                    }
                }
            }
        } catch (ClassNotFoundException | InvocationTargetException |
                NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
