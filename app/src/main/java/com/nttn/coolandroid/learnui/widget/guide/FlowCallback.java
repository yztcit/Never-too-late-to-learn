package com.nttn.coolandroid.learnui.widget.guide;

import android.view.View;

/**
 * Created by Apple.
 * Desc: 按步骤引导
 */
public interface FlowCallback {

    boolean onFlow(IFlow flow, int step);

    boolean onViewClicked(View view, IFlow flow, int step);

    void onFinish(boolean finish);
}
