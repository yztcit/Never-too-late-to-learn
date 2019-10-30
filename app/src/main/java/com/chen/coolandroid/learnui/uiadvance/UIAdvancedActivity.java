package com.chen.coolandroid.learnui.uiadvance;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chen.coolandroid.R;
import com.chen.coolandroid.activity.BaseHeadActivity;

public class UIAdvancedActivity extends BaseHeadActivity {
    private ListView uiListView;
    private String[] chapterStr = new String[]{"拖拽效果", "侧滑删除", "侧滑列表", "测试TextView加载HTML"};

    @Override
    public int getTitleResId() {
        return R.string.learn_UI_advanced;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_uiadvanced;
    }

    @Override
    public void initView() {
        uiListView = findViewById(R.id.lv_ui);
    }

    @Override
    public void initData() {
        ArrayAdapter<String> chapterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chapterStr);
        uiListView.setAdapter(chapterAdapter);
        uiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == chapterStr.length - 1) {
                    HtmlTagActivity.actionStart(mContext);
                    return;
                }
                boolean[] menuSettings = new boolean[position + 1];
                menuSettings[position] = true;
                DragDemoActivity.actionStart(mContext, menuSettings);
            }
        });
    }
}
