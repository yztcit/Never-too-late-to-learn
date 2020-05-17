package com.nttn.coolandroid.learnui.widget.guide;

import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by Apple.
 * Desc: 按步骤引导
 */
public interface IFlow {
    void next();

    void last();

    void finish();

    <V extends View> V findChildInTipView(@IdRes int id);
}
