package com.nttn.coolandroid.learnui.uibase;

import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseHeadActivity;
import com.nttn.coolandroid.learnui.widget.guide.Curtain;
import com.nttn.coolandroid.learnui.widget.guide.CurtainCallback;
import com.nttn.coolandroid.learnui.widget.guide.CurtainFlow;
import com.nttn.coolandroid.learnui.widget.guide.CurtainFlowCallback;
import com.nttn.coolandroid.learnui.widget.guide.IFlow;
import com.nttn.coolandroid.learnui.widget.guide.Hollow;
import com.nttn.coolandroid.learnui.widget.guide.IGuide;
import com.nttn.coolandroid.learnui.widget.guide.RadiusRectShape;
import com.nttn.coolandroid.tool.LogUtil;

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
        findViewById(R.id.btn_flow_show).setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(final View v) {
        int id = v.getId();
        if (id == R.id.btn_show) {
            Curtain.with(this)
                    .target(findViewById(R.id.image), new RadiusRectShape(45))
                    //.target(findViewById(R.id.text), new Hollow.Padding(10))
                    //.target(findViewById(R.id.button))
                    .curtainColor(0xBB000000)
                    .tipLayoutResId(R.layout.layout_ui_curtain_tip1)
                    .setCallback(new CurtainCallback() {
                        @Override
                        public void onShow(final IGuide guide) {
                            ToastUtils.showShort("开始引导");
                            guide.findChildInTipView(R.id.curtain_tip_button_next);
                        }

                        @Override
                        public void onViewClicked(View view, IGuide guide) {
                            int id = view.getId();
                            ToastUtils.showShort("点击操作提示");
                            if (id == R.id.curtain_tip_button_next) {
                                guide.updateHollows(new Hollow(findViewById(R.id.text)));
                                guide.updateTipView(R.layout.layout_ui_curtain_tip3);
                                guide.findChildInTipView(R.id.curtain_tip_button_last);
                                guide.findChildInTipView(R.id.curtain_tip_button_finish);
                            }
                            if (id == R.id.curtain_tip_button_last) {
                                guide.updateHollows(new Hollow(findViewById(R.id.image)));
                                guide.updateTipView(R.layout.layout_ui_curtain_tip1);
                                guide.findChildInTipView(R.id.curtain_tip_button_next);
                            }
                            if (id == R.id.curtain_tip_button_finish) {
                                guide.dismissGuide();
                            }
                        }

                        @Override
                        public void onDismiss() {
                            ToastUtils.showShort("完成引导");
                        }
                    })
                    .show();
        }

        if (id == R.id.btn_flow_show) {
            CurtainFlow.with(this)
                    .curtainColor(0xBB000000)
                    .setBackCancelable(false)
                    .step(findViewById(R.id.image))
                    .step(findViewById(R.id.button))
                    .step(findViewById(R.id.text), R.layout.layout_ui_curtain_tip1, R.id.curtain_tip_button_next)
                    .step(findViewById(R.id.image))
                    .start(new CurtainFlowCallback() {
                        @Override
                        public boolean onFlow(IFlow flow, int step) {
                            ToastUtils.showShort("操作(" + step + ")提示");
                            return false;
                        }

                        @Override
                        public boolean onViewClicked(View view, IFlow flow, int step) {
                            LogUtil.d("UIBase", "点击(" + step + ")提示");
                            return false;
                        }

                        @Override
                        public void onFinish(boolean finish) {
                            ToastUtils.showShort("完成引导(" + finish + ")");
                        }
                    });
        }
    }
}
