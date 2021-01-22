package com.nttn.coolandroid.learnui.uiadvance;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseHeadActivity;

public class UIAdvancedActivity extends BaseHeadActivity {
    private ListView uiListView;
    private final String[] chapterStr = new String[]{
            "拖拽效果", "侧滑删除", "侧滑列表", "测试TextView加载HTML", "手写签名", "翻页效果", "OCR第一步"
    };

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
                switch (chapterStr[position]) {
                    case "拖拽效果":
                    case "侧滑删除":
                    case "侧滑列表":
                        boolean[] menuSettings = new boolean[position + 1];
                        menuSettings[position] = true;
                        DragDemoActivity.actionStart(mContext, menuSettings);
                        break;
                    case "测试TextView加载HTML":
                        HtmlTagActivity.actionStart(mContext);
                        break;
                    case "翻页效果":
                        startActivity(new Intent(mContext, FlipperActivity.class));
                        break;
                    case "手写签名":
                        startActivity(new Intent(mContext, SignatureActivity.class));
                        break;
                    case "OCR第一步":
                        startActivity(new Intent(mContext, CaptureActivity.class));
                        break;
                }
            }
        });
    }
}
