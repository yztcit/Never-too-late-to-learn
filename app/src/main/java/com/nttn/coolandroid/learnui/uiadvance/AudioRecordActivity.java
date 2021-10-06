package com.nttn.coolandroid.learnui.uiadvance;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;

import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseHeadActivity;
import com.nttn.coolandroid.learnui.widget.audiorecord.AudioRecordPresenter;
import com.nttn.coolandroid.tool.LogUtil;

import java.io.File;

/**
 * Desc: 仿微信录音
 * Update: 2021/4/18
 * Created by Chen.
 */
public class AudioRecordActivity extends BaseHeadActivity implements AudioRecordPresenter.IRecordView {
    private com.nttn.coolandroid.databinding.ActivityAudioRecordBinding dataBinding;
    private final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WAKE_LOCK};


    @Override
    public int getTitleResId() {
        return R.string.ui_audio_record;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_audio_record;
    }

    @Override
    public void initView() {
        dataBinding = DataBindingUtil.bind(getContentView());
    }

    @Override
    public void initData() {
        if (PermissionUtils.isGranted(permissions)) {
            prepareAudio();
        } else {
            PermissionUtils.permission(permissions).callback(new PermissionUtils.SimpleCallback() {
                @Override
                public void onGranted() {
                    prepareAudio();
                }

                @Override
                public void onDenied() {
                    ToastUtils.showShort("without permissions");
                    finish();
                }
            }).request();
        }

    }

    private void prepareAudio() {
        AudioRecordPresenter recordPresenter = new AudioRecordPresenter(this, getExternalCacheDir().getAbsolutePath());
        dataBinding.setRecordPresenter(recordPresenter);
        getLifecycle().addObserver(recordPresenter);
    }

    @Override
    public void onRecording(int count) {

    }

    @Override
    public void onFinishRecord(int duration, File file) {
        LogUtil.d("Audio", "duration = " + duration + ", path = " + (file == null? "null":file.getAbsolutePath()));
    }
}
