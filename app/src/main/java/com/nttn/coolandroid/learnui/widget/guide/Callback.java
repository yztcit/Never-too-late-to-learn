package com.nttn.coolandroid.learnui.widget.guide;

import android.view.View;

/**
 * Created by Apple.
 * Desc: 回调
 */
interface Callback {
    void onShow(IGuide guide);

    void onViewClicked(View view, IGuide guide);

    void onDismiss();
}
