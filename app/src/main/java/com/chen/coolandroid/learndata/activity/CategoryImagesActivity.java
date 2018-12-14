package com.chen.coolandroid.learndata.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.chen.coolandroid.R;
import com.chen.coolandroid.learndata.adapter.ImageAdapter;
import com.chen.coolandroid.learndata.utils.ImageWorker;
import com.chen.coolandroid.learndata.utils.LoadLoacalPhotoCursorTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


/**
 * No.4 data storage,images
 */
public class CategoryImagesActivity extends FragmentActivity {
    private static final String TAG = "CategoryImagesActivity";
    private Context mContext;

    //    private ExpandableListView mExpandListView;
//    private TextView mTVImagePath;
    private GridView mGVImages;


    private final static String RESULT_URIS = "result_uris";
    private final static String INTENT_CLAZZ = "clazz";
    private Class clazz; //需要跳转的Activity类对象
    private ImageWorker imageWorker;//下载图片的异步线程类
    private ArrayList<Uri> uriArray = new ArrayList<Uri>();//存放图片的uri数据
    private ArrayList<Long> origIdArray = new ArrayList<Long>();//存放图片的id
    private TreeMap<Long, Uri> selectedTree = new TreeMap<Long, Uri>();//存放已选中的图片的id和uri数据

    /**
     * 这个是SelectedTreeMap 的代码，非常简单的一个序列化元素。
     * 用于存放已经选中的图片TreeMap<Long, Uri> selectedTree
     */

    public class SelectedTreeMap implements Serializable {
        private TreeMap<Long, Uri> treeMap;

        public TreeMap<Long, Uri> getTreeMap() {
            return treeMap;
        }

        public void setTreeMap(TreeMap<Long, Uri> treeMap) {
            this.treeMap = treeMap;
        }
    }

    private SelectedTreeMap selectedTreeMap = new SelectedTreeMap();

    private ImageAdapter adapter;
    private GridView gridView;
    private View loadView;//进度条View
    private Button doneBtn;
    private TextView selectedNum;
    private LoadLoacalPhotoCursorTask cursorTask;//获取本地图片数据的异步线程类
    private AlphaAnimation inAlphaAni;//每个图片加载时渐隐渐显的效果动画
    private AlphaAnimation outAlphaAni;//每个图片加载时渐隐渐显的效果动画


    //start this activity by context;
    //others params which be used in this class can be defined in actionStart(params);
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CategoryImagesActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_images);
        mContext = this;

        initView();
        initData();

        init();
    }


    private void initView() {
//        mTVImagePath = (TextView) findViewById(R.id.tv_images_path);
//        mExpandListView = (ExpandableListView) findViewById(R.id.expand_lv_data);
//        mGVImages = (GridView) findViewById(R.id.gv_data_images);
        gridView = (GridView) findViewById(R.id.gv_data_images);
    }

    private void initData() {
        //来自AndroidFileManager中的加载图片的方法，实测图片数量过多会有漫长的等待
//        new LocalMediaLoader(this, LocalMediaLoader.TYPE_IMAGE).loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {
//            @Override
//            public void loadComplete(List<FolderInfo> folders) {
//                mTVImagePath.setText(folders.toString());
//            }
//        });
    }

    /**
     * 初始化
     * 其中的 getIntent().getExtras();为null，所以clazz=null。这里是为了方便复用该Activity。
     */
    private void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clazz = (Class) bundle.get(INTENT_CLAZZ);
        }else {
            Log.i(TAG, "bundle == null");
        }
        imageWorker = new ImageWorker(this);
        //这个bitmap是GridView中每一个item默认时的图片
        Bitmap b = Bitmap.createBitmap(new int[]{0x00000000}, 1, 1, Bitmap.Config.ARGB_8888);
        imageWorker.setLoadBitmap(b);
        adapter = new ImageAdapter(imageWorker, this);
        gridView.setAdapter(adapter);
        loadData();
        initAnimation();
//        onItemClick();
//        onScroll();
//        doneClick();
    }

    /**
     * GridView中每个item图片加载初始化动画-渐隐渐显的效果
     */
    private void initAnimation() {
        float fromAlpha = 0;
        float toAlpha = 1;
        int duration = 200;
        inAlphaAni = new AlphaAnimation(fromAlpha, toAlpha);
        inAlphaAni.setDuration(duration);
        inAlphaAni.setFillAfter(true);
        outAlphaAni = new AlphaAnimation(toAlpha, fromAlpha);
        outAlphaAni.setDuration(duration);
        outAlphaAni.setFillAfter(true);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        cursorTask = new LoadLoacalPhotoCursorTask(this);//获取本地图片的异步线程类
        /**
         * 回调接口。当完成本地图片数据的获取之后，回调LoadLoacalPhotoCursorTask类中的OnLoadPhotoCursor接口
         * 的onLoadPhotoSursorResult方法，把数据传递到了这里。
         */
        cursorTask.setOnLoadPhotoCursor(new LoadLoacalPhotoCursorTask.OnLoadPhotoCursor() {
            @Override
            public void onLoadPhotoCursorResult(HashMap<String, List<Long>> groupMap,
                                                ArrayList<Uri> uriArray,
                                                ArrayList<Long> origIdArray) {
                if (isNotNull(uriArray) && isNotNull(origIdArray)) {
                    CategoryImagesActivity.this.uriArray = uriArray;
                    CategoryImagesActivity.this.origIdArray = origIdArray;
//                    loadView.setVisibility(View.GONE);
                    Log.i(TAG, "onLoadPhotoCursorResult: \n" + groupMap.size() + "\n" + groupMap.toString());
                    adapter.setOrigIdArray(origIdArray);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        cursorTask.execute();
    }

    /**
     * 点击每一项选择图片
     */
    /*private void onItemClick() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox selectBtn = (CheckBox) view.findViewById(R.id.select_btn);
                boolean checked = !selectBtn.isChecked();
                selectBtn.setChecked(checked);
                //adapter中保存已经点击过的图片的选中情况
                adapter.putSelectMap(id, checked);
                Uri uri = uriArray.get(position);
                if (checked) {
                    selectedTree.put(id, uri);
                } else {
                    selectedTree.remove(id);
                }
                if (doneBtn.getVisibility() == View.GONE
                        && selectedTree.size() > 0) {
                    doneBtn.startAnimation(inAlphaAni);
                    doneBtn.setVisibility(View.VISIBLE);
                } else if (doneBtn.getVisibility() == View.VISIBLE
                        && selectedTree.size() == 0) {
                    doneBtn.startAnimation(outAlphaAni);
                    doneBtn.setVisibility(View.GONE);
                }
                CharSequence text = selectedTree.size() == 0 ? "" : "已选择 " + selectedTree.size() + " 张";
                selectedNum.setText(text);
            }
        });
    }*/

    /**
     * 滚动的时候不加载图片-该功能通过imageWorker中锁机制实现的。
     */
    private void onScroll() {
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //SCROLL_STATE_IDLE表示停止滚动。
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    imageWorker.setPauseWork(false);
                } else {
                    imageWorker.setPauseWork(true);
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    /**
     * 点击“完成”-完成事件，由于clazz==null 所以该方法并不实现什么功能。
     */
    /*private void doneClick() {
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clazz != null) {
                    selectedTreeMap.setTreeMap(selectedTree);
                    Intent intent = new Intent(CategoryImagesActivity.this, clazz);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(RESULT_URIS, selectedTreeMap);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    Log.i(CategoryImagesActivity.class.getSimpleName(), "clazz==null");
                }
            }
        });

    }*/

    /**
     * 判断list不为空
     * @param list
     * @return
     */
    private static boolean isNotNull(ArrayList list) {
        return list != null && list.size() > 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursorTask.setExitTasksEarly(true);
        imageWorker.setExitTasksEarly(true);
    }
}
