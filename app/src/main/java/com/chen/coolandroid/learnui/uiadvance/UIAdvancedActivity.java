package com.chen.coolandroid.learnui.uiadvance;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chen.coolandroid.R;
import com.chen.coolandroid.activity.BaseHeadActivity;

public class UIAdvancedActivity extends BaseHeadActivity {
    private ListView uiListView;

    @Override
    protected int setTitleResId() {
        return R.string.learn_UI_advanced;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.activity_uiadvanced;
    }

    @Override
    protected void initView() {
        uiListView = findViewById(R.id.lv_ui);
    }

    @Override
    protected void initData() {
        String[] chapterStr = new String[]{"侧滑列表", "拖拽效果", "测试TextView加载HTML"};
        ArrayAdapter<String> chapterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chapterStr);
        uiListView.setAdapter(chapterAdapter);
        uiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    HtmlTagActivity.actionStart(mContext);
                    return;
                }
                DragDemoActivity.actionStart(mContext);
            }
        });
    }
}
