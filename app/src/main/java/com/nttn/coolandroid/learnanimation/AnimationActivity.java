package com.nttn.coolandroid.learnanimation;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseActivity;
import com.nttn.coolandroid.activity.BaseHeadActivity;

/**
 * 动画学习<!--编号6-->
 *  视图(View)动画[补间(Tween)动画、帧(Frame)动画]、属性(Property)动画
 * 传送门：https://blog.csdn.net/harvic880925/article/details/39996643
 */
public class AnimationActivity extends BaseHeadActivity implements View.OnClickListener{
    private static final String TAG = "AnimationActivity";
    private Button mScaleButton;
    private Button mTranslateButton;
    private Button mRotateButton;
    private Button mAlphaButton;
    private Button mSetButton;

    private Button mFrameButton;

    private Button mPropertyButton;
    private ImageView mTestImage;

    private Animation scaleAnim;
    private Animation alphaAnim;
    private Animation rotateAnim;
    private Animation translateAnim;
    private Animation setAnim;

    private AnimationDrawable animDrawable;

    @Override
    public int getTitleResId() {
        return R.string.learn_animation;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_animation;
    }

    @Override
    public void initView() {
        //Tween Animation
        mScaleButton = findViewById(R.id.btn_scale_anim);
        mScaleButton.setOnClickListener(this);
        mTranslateButton = findViewById(R.id.btn_translate_anim);
        mTranslateButton.setOnClickListener(this);
        mRotateButton = findViewById(R.id.btn_rotate_anim);
        mRotateButton.setOnClickListener(this);
        mAlphaButton = findViewById(R.id.btn_alpha_anim);
        mAlphaButton.setOnClickListener(this);
        mSetButton = findViewById(R.id.btn_set_anim);
        mSetButton.setOnClickListener(this);
        //Frame Animation
        mFrameButton = findViewById(R.id.btn_frame_anim);
        mFrameButton.setOnClickListener(this);
        //Property Animation
        mPropertyButton = findViewById(R.id.btn_property_anim);
        mPropertyButton.setOnClickListener(this);

        mTestImage = findViewById(R.id.iv_test_anim);
        mTestImage.setOnClickListener(this);
    }

    @Override
    public void initData() {
        //Tween Animation
        scaleAnim = AnimationUtils.loadAnimation(mContext, R.anim.cool_scale);
        alphaAnim = AnimationUtils.loadAnimation(mContext, R.anim.cool_alpha);
        rotateAnim = AnimationUtils.loadAnimation(mContext, R.anim.cool_rotate);
        translateAnim = AnimationUtils.loadAnimation(mContext, R.anim.cool_translate);
        setAnim = AnimationUtils.loadAnimation(mContext, R.anim.cool_set);
        //Frame Animation
        animDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.anim_frame);
        mTestImage.setImageDrawable(animDrawable);
        //Property Animation
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_scale_anim:
                mTestImage.startAnimation(scaleAnim);
                break;
            case R.id.btn_translate_anim:
                mTestImage.startAnimation(translateAnim);
                break;
            case R.id.btn_alpha_anim:
                mTestImage.startAnimation(alphaAnim);
                break;
            case R.id.btn_rotate_anim:
                mTestImage.startAnimation(rotateAnim);
                break;
            case R.id.btn_set_anim:
                mTestImage.startAnimation(setAnim);
                break;
            case R.id.btn_frame_anim:
                boolean running = animDrawable.isRunning();
                if (running) {
                    animDrawable.stop();
                } else {
                    animDrawable.start();
                }
                break;
            case R.id.btn_property_anim:

                break;
            case R.id.iv_test_anim:
                ToastUtils.showShort("I'm here!");
                break;
        }
    }
}
