package com.nttn.coolandroid.learnui.uibase;

import android.view.View;

import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseHeadActivity;
import com.nttn.coolandroid.learnui.widget.guide.Curtain;
import com.nttn.coolandroid.learnui.widget.guide.GuideDialog;
import com.nttn.coolandroid.learnui.widget.guide.GuideView;
import com.nttn.coolandroid.learnui.widget.guide.Hollow;
import com.nttn.coolandroid.learnui.widget.guide.RadiusRectShape;

/**
 * 0.基础UI学习
 */
public class UIBaseActivity extends BaseHeadActivity implements View.OnClickListener {

    @Override
    public int getTitleResId() {
        return R.string.learn_UI_base;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_uibase;
    }

    @Override
    public void initView() {
        findViewById(R.id.btn_show).setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_show) {
            new Curtain(this)
                    .target(findViewById(R.id.image), new RadiusRectShape(50))
                    .target(findViewById(R.id.text), new Hollow.Padding(10))
                    .target(findViewById(R.id.button))
                    .curtainColor(0xBB000000)
                    .show();
        }
    }
}
