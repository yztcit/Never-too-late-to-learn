package com.nttn.coolandroid.learnui.widget.audiorecord;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.nttn.coolandroid.tool.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


/**
 * Desc:
 * Update: 2021/4/18
 * Created by Chen.
 */
public class AudioRecordPresenter implements LifecycleObserver {
    private IRecordView iRecordView;

    private String mDir;
    private File mAudioFile;

    private MediaRecorder mMediaRecorder;
    private Timer mTimer;

    private MediaPlayer mMediaPlayer;


    private boolean isRecording = false, isPlaying = false;

    public AudioRecordPresenter(IRecordView iRecordView, String dir) {
        this.iRecordView = iRecordView;
        this.mDir = dir;
    }

    private int count = 0;

    public void startRecord(View view) {
        if (isRecording || isPlaying) return;

        try {
            FileUtils.createOrExistsDir(mDir);
            mAudioFile = new File(mDir, generateFileName());
            LogUtil.d("AudioRecord", mAudioFile.getAbsolutePath());

            mTimer = new Timer("record", true);

            mMediaRecorder = new MediaRecorder();
            //设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式
            // THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式，H263视频/ARM音频编码)、
            // MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //设置音频的格式为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 设置时长60s
            mMediaRecorder.setMaxDuration(1000 * 60);
            //设置输出文件
            mMediaRecorder.setOutputFile(mAudioFile.getAbsolutePath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    count++;
                    LogUtil.d("AudioRecord", "time = " + count);
                    if (count == 60){
                        stopRecord(null);
                    }
                }
            }, 0, 1000);
            isRecording = true;
        } catch (Exception e) {
            releaseRecord();
            e.printStackTrace();
        }
    }

    public void stopRecord(View view) {
        if (!isRecording || mMediaRecorder == null) return;
        mTimer.cancel();
        mMediaRecorder.stop();
//        mMediaRecorder.release();
        iRecordView.onFinishRecord(count, mAudioFile);
        isRecording = false;
        count = 0;
    }

    public void playRecord(View view) {
        if (isRecording || isPlaying || mAudioFile == null || !mAudioFile.exists()) return;

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPlaying = false;
                LogUtil.e("AudioRecord", "what-> " + what + "extra-> " + extra);
                mMediaPlayer.reset();
                return false;
            }
        });

        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    LogUtil.d("AudioRecord", "finish playing.");
                }
            });
            mMediaPlayer.setDataSource(mAudioFile.getAbsolutePath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();

            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
            isPlaying = false;
        }
    }

    private void releaseRecord() {
        count = 0;
        isRecording = false;
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        if (mAudioFile != null && mAudioFile.exists())
            mAudioFile.delete();
    }

    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resume() {
        if (isPlaying) {
            mMediaPlayer.start();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pause() {
        if (isRecording) {
            releaseRecord();
        }

        if (isPlaying) {
            mMediaPlayer.pause();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void releaseMedia() {
        count = 0;
        isRecording = false;
        isPlaying = false;
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public interface IRecordView {
        void onRecording(int count);
        void onFinishRecord(int duration, File file);
    }
}
