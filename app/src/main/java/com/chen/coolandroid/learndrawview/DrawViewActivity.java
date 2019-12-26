package com.chen.coolandroid.learndrawview;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.chen.coolandroid.R;
import com.chen.coolandroid.activity.BaseHeadActivity;
import com.chen.coolandroid.learndrawview.widget.FollowView;

/**
 * 编号7: 画图学习
 */
public class DrawViewActivity extends BaseHeadActivity implements View.OnClickListener {
    private FrameLayout frame_container;
    private FollowView followView;

    @Override
    public int getTitleResId() {
        return R.string.learn_draw_view;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_draw_view;
    }

    @Override
    public void initView() {
        findViewById(R.id.btn_draw_follow_view).setOnClickListener(this);
        frame_container = findViewById(R.id.frame_container);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_draw_follow_view) {
            if (followView == null) initFollowView();
            frame_container.removeAllViews();
            frame_container.addView(followView);
        }
    }

    private void initFollowView(){
        followView = new FollowView(mContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(30, 30);
        layoutParams.gravity = Gravity.CENTER;
        followView.setLayoutParams(layoutParams);
        followView.setBackgroundColor(getResources().getColor(R.color.color_bg));
    }
}
