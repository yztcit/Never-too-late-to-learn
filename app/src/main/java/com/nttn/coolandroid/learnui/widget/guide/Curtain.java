package com.nttn.coolandroid.learnui.widget.guide;

import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.View;

import com.nttn.coolandroid.R;
import com.nttn.coolandroid.tool.LogUtil;

/**
 * Created by Apple.
 * Desc: 自定义引导蒙层
 */
public class Curtain {
    private static final String TAG = "Curtain >>>";
    private FragmentActivity activity;
    private SparseArray<Hollow> mHollows;
    private int mCurtainColor = 0xAA000000;

    public Curtain(Fragment fragment) {
        this(fragment.getActivity());
    }

    public Curtain(FragmentActivity activity) {
        this.activity = activity;
        this.mHollows = new SparseArray<>();
    }

    public Curtain target(@NonNull View targetView) {
        target(targetView, true);
        return this;
    }

    public Curtain target(@NonNull View targetView, boolean isAutoAdaptViewBackground) {
        initHollow(targetView, isAutoAdaptViewBackground);
        return this;
    }

    public Curtain target(@NonNull View targetView, Shape shape) {
        initHollow(targetView, shape);
        return this;
    }

    public Curtain target(@NonNull View targetView, Hollow.Padding padding) {
        initHollow(targetView, padding);
        return this;
    }

    public Curtain target(@NonNull View targetView, Hollow.Offset offset) {
        initHollow(targetView, offset);
        return this;
    }

    public Curtain target(@NonNull View targetView, Hollow.Padding padding, Hollow.Offset offset) {
        initHollow(targetView, padding, offset);
        return this;
    }

    public Curtain target(@NonNull View targetView, Shape shape, Hollow.Padding padding) {
        initHollow(targetView, shape, padding);
        return this;
    }

    public Curtain target(@NonNull View targetView, Shape shape, Hollow.Offset offset) {
        initHollow(targetView, shape, offset);
        return this;
    }

    public Curtain target(@NonNull View targetView, Shape shape, Hollow.Padding padding, Hollow.Offset offset) {
        initHollow(targetView, shape, padding, offset);
        return this;
    }

    public Curtain curtainColor(int color) {
        mCurtainColor = color;
        return this;
    }

    public Curtain curtainColorRes(@ColorRes int color) {
        mCurtainColor = activity.getResources().getColor(color);
        return this;
    }

    public void show(){
        if (mHollows.size() <= 0) {
            LogUtil.d(TAG, "rootView without any view");
            return;
        }
        View checkThreadView = mHollows.valueAt(0).targetView;
        if (checkThreadView.getWidth() <= 0) {
            checkThreadView.post(new Runnable() {
                @Override
                public void run() {
                    show();
                }
            });
            return;
        }

        GuideView guideView = new GuideView(activity);
        guideView.setHollows(getHollows());
        guideView.setCurtainColor(mCurtainColor);
        GuideDialog guideDialog = new GuideDialog();
        guideDialog.setAnimationStyle(R.style.DialogAnimation);
        guideDialog.setGuideView(guideView);
        guideDialog.show();
    }

    private Hollow[] getHollows(){
        Hollow[] hollows = new Hollow[mHollows.size()];
        for (int i = 0; i< mHollows.size(); i++) {
            hollows[i] = mHollows.valueAt(i);
        }
        return hollows;
    }

    private Hollow initHollow(View which, boolean isAutoAdaptViewBackground) {
        Hollow hollow = mHollows.get(which.hashCode());
        if (hollow == null) {
            hollow = new Hollow(which);
            hollow.targetRect = new Rect();
            hollow.targetView.getDrawingRect(hollow.targetRect);
            hollow.setAutoAdaptViewBackground(isAutoAdaptViewBackground);
            mHollows.append(which.hashCode(), hollow);
        }
        return hollow;
    }

    private void initHollow(View which, Shape shape) {
        initHollow(which, false).shape = shape;
    }

    private void initHollow(View which, Hollow.Padding padding) {
        initHollow(which, false).padding = padding;
    }

    private void initHollow(View which, Hollow.Offset offset) {
        initHollow(which, false).offset = offset;
    }

    private void initHollow(View which, Hollow.Padding padding, Hollow.Offset offset) {
        Hollow hollow = initHollow(which, false);
        hollow.padding = padding;
        hollow.offset = offset;
    }

    private void initHollow(View which, Shape shape, Hollow.Padding padding) {
        Hollow hollow = initHollow(which, false);
        hollow.shape = shape;
        hollow.padding = padding;
    }

    private void initHollow(View which, Shape shape, Hollow.Offset offset) {
        Hollow hollow = initHollow(which, false);
        hollow.shape = shape;
        hollow.offset = offset;
    }

    private void initHollow(View which, Shape shape, Hollow.Padding padding, Hollow.Offset offset) {
        Hollow hollow = initHollow(which, false);
        hollow.shape = shape;
        hollow.padding = padding;
        hollow.offset = offset;
    }
}
