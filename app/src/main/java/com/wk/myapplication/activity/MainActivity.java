package com.wk.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.wk.myapplication.R;
import com.wk.myapplication.tool.LogUtils;

/**
 * https://blog.csdn.net/xiaole0313/article/details/51714223
 * 如何自学Android, 教大家玩爆Android
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private NavigationView navigationView;
    private Toolbar toolBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private FrameLayout drawer_right;
    private FrameLayout drawer_left;
    private ListView mainListView;

    private String[] chapterStr;

    private boolean hasStoragePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Window window = getWindow();
//        window.requestFeature(Window.FEATURE_LEFT_ICON);
//        setContentView(R.layout.activity_main);
//        window.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.mipmap.ic_launcher);

//        initView();
//        initData();

        setContentView(R.layout.activity_main_new);
        initViewNew();
        initDataNew();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    hasStoragePermission = false;
                    finish();
                }
                break;
        }
    }

    private void initView() {
        navigationView = (NavigationView) findViewById(R.id.navigation);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private void initData() {
        //设置toolbar标题文本
        toolBar.setTitle("首页");
        //设置toolbar
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //设置左上角图标是否可点击
            actionBar.setHomeButtonEnabled(false);
            //左上角加上一个返回图标
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        //初始化ActionBarDrawerToggle(ActionBarDrawerToggle就是一个开关一样用来打开或者关闭drawer)
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.open, R.string.close) {
            /**
             * 抽屉菜单打开监听
             * @param drawerView 抽屉布局
             */
            @Override
            public void onDrawerOpened(View drawerView) {
                Toast.makeText(MainActivity.this, "drawer open", Toast.LENGTH_SHORT).show();
                super.onDrawerOpened(drawerView);
            }

            /**
             * 抽屉菜单关闭监听
             * @param drawerView 抽屉布局
             */
            @Override
            public void onDrawerClosed(View drawerView) {
                Toast.makeText(MainActivity.this, "drawer close", Toast.LENGTH_SHORT).show();
                super.onDrawerClosed(drawerView);
            }
        };

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
//                item.setChecked(true);
//                drawerLayout.closeDrawers();
                return false;
            }
        });

        toggle.syncState();
        drawerLayout.setDrawerListener(toggle);
    }

    private void initViewNew() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_right = (FrameLayout) findViewById(R.id.drawer_right);
        drawer_left = (FrameLayout) findViewById(R.id.drawer_left);
        mainListView = (ListView) findViewById(R.id.lv_main);
    }

    private void initDataNew() {

        //check and request permission
        hasStoragePermission = ContextCompat.checkSelfPermission(
                this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!hasStoragePermission) {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }


        chapterStr = new String[]{getString(R.string.learn_UI_base), getString(R.string.learn_component),
                getString(R.string.learn_layout), getString(R.string.learn_interaction),
                getString(R.string.learn_data_storage), getString(R.string.learn_http),
                getString(R.string.learn_animation), getString(R.string.learn_draw_view),
                getString(R.string.learn_media_camera), getString(R.string.learn_UI_advanced),
                getString(R.string.learn_style), getString(R.string.learn_threads),
                getString(R.string.learn_others)};

        @SuppressWarnings("unchecked")
        ArrayAdapter chapterAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, chapterStr);
        mainListView.setAdapter(chapterAdapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strAtPosition = chapterStr[position];
                String atPosition = (String) parent.getItemAtPosition(position);
                if (TextUtils.equals(strAtPosition,atPosition)) {
                    //item位置固定，不想写switch，所以用隐式启动setAction(如果没有设置category，清单中需设置default)
                    //action格式固定为packageName+.item[position],清单中的action要严格对照数字编号，详见strings.xml
                    String actionStr = getPackageName() + ".item" + position;
                    Intent startIntent = new Intent();
                    startIntent.setAction(actionStr);
                    MainActivity.this.startActivity(startIntent);
                }
//                Log.d(TAG, "onItemClick: " + "\natPosition: " + atPosition + "\nstrAtPosition: " + strAtPosition);
                LogUtils.d(TAG,"onItemClick: " + "\natPosition: " + atPosition + "\nstrAtPosition: " + strAtPosition);
            }
        });

        drawer_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        drawer_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
//                Toast.makeText(MainActivity.this,"open", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
//                Toast.makeText(MainActivity.this,"close", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }
}
