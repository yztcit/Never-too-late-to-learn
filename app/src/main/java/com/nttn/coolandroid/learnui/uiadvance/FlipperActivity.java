package com.nttn.coolandroid.learnui.uiadvance;

import com.blankj.utilcode.util.ToastUtils;
import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseHeadActivity;
import com.nttn.coolandroid.learnui.widget.flip.SingleFlipView;
import com.nttn.coolandroid.tool.DisplayUtil;
import com.nttn.coolandroid.tool.LogUtil;

/**
 * Description: 翻页效果 <br>
 * Version: 1.0         <br>
 * Update：            <br>
 * Created by Apple.
 */
public class FlipperActivity extends BaseHeadActivity {
    private static final String TAG = "FlipperActivity";
    @Override
    public int getTitleResId() {
        return R.string.ui_flipper;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_flipper;
    }

    @Override
    public void initView() {
        LogUtil.d(TAG, "[" + DisplayUtil.getScreenWidth(mContext) + ", " + DisplayUtil.getScreenHeight(mContext) + "]");
        /*SingleFlipView flipView = findViewById(R.id.flip);
        flipView.setFlipperListener(new SingleFlipView.FlipperListener() {
            @Override
            public void onFlip(boolean towardLeft) {
                ToastUtils.showShort("direction toward " + (towardLeft ? "left" : "right"));
            }
        });*/
    }

    @Override
    public void initData() {

    }
}
