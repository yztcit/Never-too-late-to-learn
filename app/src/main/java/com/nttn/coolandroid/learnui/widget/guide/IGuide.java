package com.nttn.coolandroid.learnui.widget.guide;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;

/**
 * Created by Apple.
 * Desc:
 */
public interface IGuide {
    <V extends View> V findChildInTipView(@IdRes int id);

    void updateHollows(Hollow... hollows);

    void updateTipView(@LayoutRes int layoutResId);

    void update(@LayoutRes int tipLayoutResId, @IdRes int operateButtonId, Hollow... hollows);

    void dismissGuide();
}
