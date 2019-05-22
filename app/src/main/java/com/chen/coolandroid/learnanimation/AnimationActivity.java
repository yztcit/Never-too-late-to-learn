package com.chen.coolandroid.learnanimation;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.chen.coolandroid.R;
import com.chen.coolandroid.activity.BaseActivity;

/**
 * 动画学习<!--编号6-->
 *  视图(View)动画[补间(Tween)动画、帧(Frame)动画]、属性(Property)动画
 * 传送门：https://blog.csdn.net/harvic880925/article/details/39996643
 */
public class AnimationActivity extends BaseActivity implements View.OnClickListener{
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

    @NonNull
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_animation;
    }

    @Override
    protected void initView() {
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
    }

    @Override
    protected void initData() {
        scaleAnim = AnimationUtils.loadAnimation(mContext, R.anim.cool_scale);
        alphaAnim = AnimationUtils.loadAnimation(mContext, R.anim.cool_alpha);
        rotateAnim = AnimationUtils.loadAnimation(mContext, R.anim.cool_rotate);
        translateAnim = AnimationUtils.loadAnimation(mContext, R.anim.cool_translate);
        setAnim = AnimationUtils.loadAnimation(mContext, R.anim.cool_set);
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

                break;
            case R.id.btn_property_anim:

                break;
        }
    }
}
