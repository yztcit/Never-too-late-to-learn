package com.nttn.coolandroid.learnui.widget.guide;

import android.view.View;

/**
 * Created by Apple.
 * Desc:
 */
public class CurtainFlowCallback implements FlowCallback {

    @Override
    public boolean onFlow(IFlow flow, int step) {
        return false;
    }

    @Override
    public boolean onViewClicked(View view, IFlow flow, int step) {
        return false;
    }

    @Override
    public void onFinish(boolean finish) {

    }
}
