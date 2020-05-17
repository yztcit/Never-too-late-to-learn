package com.nttn.coolandroid.learnui.widget.guide;

import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.nttn.coolandroid.R;

import java.util.LinkedList;

/**
 * Created by Apple.
 * Desc: 按步骤引导
 * TODO: 添加自定义形状，间距等
 */
public class CurtainFlow implements Callback, IFlow {
    private static final String TAG = "CurtainFlow>>>";

    private static final int SHIFT = 25;
    private static final int next_button_tag = 0x1 << SHIFT;
    private static final int last_button_tag = 0x2 << SHIFT;
    private static final int target_view_tag = 0x3 << SHIFT;
    private static final int tips_layout_tag = 0x4 << SHIFT;

    private Curtain curtain;
    private LinkedList<SparseArray<Object>> stepList;
    private int currentIndex = 0;
    private boolean hasFlowAll = false;
    private FlowCallback callback;
    private IGuide guide;

    private CurtainFlow(Fragment fragment) {
        curtain = Curtain.with(fragment);
        stepList = new LinkedList<>();
    }

    private CurtainFlow(FragmentActivity activity) {
        curtain = Curtain.with(activity);
        stepList = new LinkedList<>();
    }

    public static CurtainFlow with(Fragment fragment) {
        return new CurtainFlow(fragment);
    }

    public static CurtainFlow with(FragmentActivity activity) {
        return new CurtainFlow(activity);
    }

    public CurtainFlow step(@NonNull View target) {
        SparseArray<Object> sparseArray = new SparseArray<>();
        sparseArray.append(target_view_tag, target);
        stepList.offer(sparseArray);
        return this;
    }

    public CurtainFlow step(@NonNull View target, @LayoutRes int tipLayout, @IdRes int nextButton) {
        SparseArray<Object> sparseArray = new SparseArray<>();
        sparseArray.append(target_view_tag, target);
        sparseArray.append(tips_layout_tag, tipLayout);
        sparseArray.append(next_button_tag, nextButton);
        stepList.offer(sparseArray);
        return this;
    }

    public CurtainFlow step(@NonNull View target, @LayoutRes int tipLayout,
                            @IdRes int nextButton, @IdRes int lastButton) {
        SparseArray<Object> sparseArray = new SparseArray<>();
        sparseArray.append(target_view_tag, target);
        sparseArray.append(tips_layout_tag, tipLayout);
        sparseArray.append(next_button_tag, nextButton);
        sparseArray.append(last_button_tag, lastButton);
        stepList.offer(sparseArray);
        return this;
    }

    public CurtainFlow curtainColor(int color) {
        curtain.curtainColor(color);
        return this;
    }

    public CurtainFlow curtainColorRes(@ColorRes int color) {
        curtain.curtainColorRes(color);
        return this;
    }

    public CurtainFlow setBackCancelable(boolean cancelable) {
        curtain.setBackCancelable(cancelable);
        return this;
    }

    public void start(FlowCallback callback) {
        if (stepList.size() <= 0) {
            Log.w(TAG, "nothing to show");
            return;
        }
        this.callback = callback;
        SparseArray<Object> peek = stepList.peek();
        View target = (View) peek.get(target_view_tag);
        Object tipLayout = peek.get(tips_layout_tag);
        int tipLayoutId;
        if (null == tipLayout) {
            tipLayoutId = R.layout.layout_ui_curtain_tip1;
        }else {
            tipLayoutId = (int) tipLayout;
        }

        if (callback != null) {
            boolean interrupted = callback.onFlow(this, currentIndex);
            if (interrupted) return;
        }

        curtain.target(target).tipLayoutResId(tipLayoutId);
        curtain.setCallback(this);
        curtain.show();
    }

    @Override
    public void onShow(IGuide guide) {
        this.guide = guide;
        if (stepList.size() <= 0) {
            Log.w(TAG, "nothing to show");
            return;
        }

        SparseArray<Object> peek = stepList.peek();
        int nextButtonResId;
        Object nextTag = peek.get(next_button_tag);
        if (nextTag == null) {
            nextButtonResId = R.id.curtain_tip_button_next;
        } else {
            nextButtonResId = (int) nextTag;
        }
        guide.findChildInTipView(nextButtonResId);
    }

    @Override
    public void onViewClicked(View v, IGuide guide) {
        if (callback != null) {
            boolean intercepted = callback.onViewClicked(v, this, currentIndex);
            if (intercepted) return;
        }

        int id = v.getId();
        SparseArray<Object> sparseArray = stepList.get(currentIndex);

        int nextButtonId;
        Object nextTag = sparseArray.get(next_button_tag);
        if (nextTag == null) {
            nextButtonId = R.id.curtain_tip_button_next;
            if (currentIndex == stepList.size() - 1) {
                nextButtonId = R.id.curtain_tip_button_finish;
            }
        } else {
            nextButtonId = (int) nextTag;
        }

        if (id == nextButtonId) {
            next();
        }

        int lastButtonId;
        Object lastTag = sparseArray.get(last_button_tag);
        if (lastTag == null) {
            lastButtonId = R.id.curtain_tip_button_last;
        } else {
            lastButtonId = (int) lastTag;
        }

        if (id == lastButtonId) {
            last();
        }
    }

    @Override
    public void onDismiss() {
        if (callback != null) {
            callback.onFinish(hasFlowAll);
        }
    }

    @Override
    public void next() {
        if (currentIndex == stepList.size() - 1) {
            hasFlowAll = true;
            guide.dismissGuide();
            return;
        }

        if (callback != null) {
            boolean interrupted = callback.onFlow(this, currentIndex);
            if (interrupted) return;
        }

        currentIndex++;
        updateCurrentTarget();
    }

    private void updateCurrentTarget() {
        SparseArray<Object> target = stepList.get(currentIndex);
        int nextButtonId;
        Object nextTag = target.get(next_button_tag);
        if (nextTag == null) {
            nextButtonId = R.id.curtain_tip_button_next;
            if (currentIndex == stepList.size() - 1) {
                nextButtonId = R.id.curtain_tip_button_finish;
            }
        } else {
            nextButtonId = (int) nextTag;

        }

        Object tipLayoutObject = target.get(tips_layout_tag);
        int tipLayoutId;
        if (null == tipLayoutObject) {
            if (currentIndex == 0) {
                tipLayoutId = R.layout.layout_ui_curtain_tip1;
            } else if (currentIndex < stepList.size() - 1) {
                tipLayoutId = R.layout.layout_ui_curtain_tip2;
            } else {
                tipLayoutId = R.layout.layout_ui_curtain_tip3;
            }
        } else {
            tipLayoutId = (int) tipLayoutObject;
        }
        View targetView = (View) target.get(target_view_tag);
        guide.update(tipLayoutId, nextButtonId, new Hollow(targetView));

        int lastButtonResId;
        Object lastTag = target.get(last_button_tag);
        if (lastTag != null) {
            lastButtonResId = (int) lastTag;
        } else {
            lastButtonResId = R.id.curtain_tip_button_last;
        }
        if (currentIndex == 0) return;
        guide.findChildInTipView(lastButtonResId);
    }

    @Override
    public void last() {
        if (currentIndex == 0) {
            return;
        }

        if (callback != null) {
            boolean interrupted = callback.onFlow(this, currentIndex);
            if (interrupted) return;
        }

        hasFlowAll = false;
        currentIndex--;
        updateCurrentTarget();
    }

    @Override
    public void finish() {
        hasFlowAll = (currentIndex == stepList.size() - 1);

        guide.dismissGuide();
    }

    @Override
    public <V extends View> V findChildInTipView(int id) {
        return guide.findChildInTipView(id);
    }
}
