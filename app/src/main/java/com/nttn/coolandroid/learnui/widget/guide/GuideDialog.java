package com.nttn.coolandroid.learnui.widget.guide;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

import com.nttn.coolandroid.R;

/**
 * Created by Apple.
 * Desc: 引导蒙层的载体
 */
public class GuideDialog extends DialogFragment {
    private FrameLayout contentView;
    private GuideView guideView;
    private int animationStyle;
    private Dialog dialog;

    public void show() {
        FragmentActivity activity = (FragmentActivity) guideView.getContext();
        show(activity.getSupportFragmentManager(), GuideDialog.class.getSimpleName());
    }

    public void setGuideView(GuideView guideView) {
        this.guideView = guideView;
    }

    public void setAnimationStyle(int animationStyle){
        this.animationStyle= animationStyle;
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

    private void initDialog(){
        if (guideView == null) {
            throw new NullPointerException("guideView can't be null, setGuideView() first");
        }
        this.contentView = new FrameLayout(guideView.getContext());
        contentView.removeAllViews();
        contentView.addView(guideView);

        dialog = new AlertDialog.Builder(guideView.getContext(), R.style.TransparentDialog)
                .setView(contentView)
                .create();

        setAnimation(dialog);
    }

    private void setAnimation(Dialog dialog) {
        if (dialog != null && animationStyle != 0 && dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(animationStyle);
        }
    }
}
