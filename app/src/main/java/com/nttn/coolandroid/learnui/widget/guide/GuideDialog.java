package com.nttn.coolandroid.learnui.widget.guide;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.nttn.coolandroid.R;

/**
 * Created by Apple.
 * Desc: 引导蒙层的载体
 */
public class GuideDialog extends DialogFragment implements IGuide{
    /**
     * 最多加载两个子view，GuideView 和 tipView
     */
    public static final int MAX_CHILD_COUNT = 2;
    /**
     * 父容器，加载GuideView 和 tipView
     */
    private FrameLayout contentView;
    private GuideView guideView;
    private int layoutResId = 0;
    private int animationStyle = R.style.DialogAnimation;
    private Dialog dialog;
    private Callback callback;

    public void show() {
        FragmentActivity activity = (FragmentActivity) guideView.getContext();
        show(activity.getSupportFragmentManager(), GuideDialog.class.getSimpleName());
    }

    public void setTipView(@LayoutRes int layoutResId) {
        this.layoutResId = layoutResId;
    }

    public void setGuideView(GuideView guideView) {
        this.guideView = guideView;
    }

    public void setAnimationStyle(int animationStyle){
        this.animationStyle= animationStyle;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (dialog == null) {
            initDialog();
        }
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception e) {
            manager.beginTransaction()
                    .add(this, tag)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (callback != null) {
            callback.onDismiss();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (callback != null) {
            callback.onShow(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dialog != null) {
            dialog = null;
        }
    }

    @Override
    public <V extends View> V findChildInTipView(int id) {
        if (contentView != null) {
            V childView = contentView.findViewById(id);
            if (childView == null) return null;
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onViewClicked(v, GuideDialog.this);
                    }
                }
            });
            return childView;
        }
        return null;
    }

    @Override
    public void updateHollows(Hollow... hollows) {
        if (null == contentView || null == getActivity() || null == guideView) return;
        guideView.setHollows(hollows);
    }

    @Override
    public void updateTipView(int layoutResId) {
        if (null == contentView || getActivity() == null) return;
        setTipView(layoutResId);
        updateTipView();
    }

    @Override
    public void update(int tipLayoutResId, int operateButtonId, Hollow... hollows) {
        updateHollows(hollows);
        updateTipView(tipLayoutResId);
        findChildInTipView(operateButtonId);
    }

    @Override
    public void dismissGuide() {
        dismissAllowingStateLoss();
    }

    private void initDialog(){
        if (guideView == null) {
            throw new NullPointerException("guideView can't be null, setGuideView() first");
        }
        this.contentView = new FrameLayout(guideView.getContext());
        contentView.removeAllViews();
        contentView.addView(guideView);

        if (layoutResId != 0) {
            updateTipView();
        }

        dialog = new AlertDialog.Builder(guideView.getContext(), R.style.TransparentDialog)
                .setView(contentView)
                .create();

        setAnimation(dialog);
    }

    private void updateTipView() {
        if (contentView.getChildCount() == MAX_CHILD_COUNT) {
            contentView.removeViewAt(1);
        }
        LayoutInflater.from(contentView.getContext())
                .inflate(layoutResId, contentView, true);
    }

    private void setAnimation(Dialog dialog) {
        if (dialog != null && animationStyle != 0 && dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(animationStyle);
        }
    }
}
