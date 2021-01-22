package com.nttn.coolandroid.learnui.uiadvance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseHeadActivity;
import com.nttn.coolandroid.learnui.widget.capture.CapturePreview;
import com.nttn.coolandroid.learnui.widget.capture.CaptureView;
import com.nttn.coolandroid.learnui.widget.capture.PictureDataHandler;

/**
 * Description:  拍照预览加截取固定区域（ocr第一步）  <br>
 * Version: 1.0     <br>
 * Update：2021/1/21   <br>
 * Created by Apple.
 */
public class CaptureActivity extends BaseHeadActivity implements Camera.PictureCallback, View.OnClickListener {
    private CapturePreview previewView;
    private CaptureView findView;
    private View captureLayout;
    private View capturedLayout;

    private boolean hasCameraPermission;
    private final int request_camera_permission = 11;

    @Override
    public int getTitleResId() {
        return R.string.ui_capture;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_capture;
    }

    private ImageView imageView;
    @Override
    public void initView() {
        imageView = findViewById(R.id.iv_find);
        previewView = findViewById(R.id.surfaceView);
        findView = findViewById(R.id.find_view);

        captureLayout = findViewById(R.id.layout_capture);
        Button btnCapture = findViewById(R.id.btn_capture);
        btnCapture.setOnClickListener(this);

        capturedLayout = findViewById(R.id.layout_captured);
        Button btnRetake = findViewById(R.id.btn_captured_retake);
        Button btnSubmit = findViewById(R.id.btn_captured_submit);
        btnRetake.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void initData() {
        //check and request permission
        hasCameraPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if (!hasCameraPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    request_camera_permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == request_camera_permission) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                hasCameraPermission = false;
                finish();
            } else {
                previewView.start();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasCameraPermission) previewView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hasCameraPermission) previewView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        previewView.release();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_capture) {
            showCaptureView(View.INVISIBLE);
            showCapturedView(View.VISIBLE);
            previewView.takePicture(this);
        } else if (id == R.id.btn_captured_retake) {
            previewView.start();
            showCapturedView(View.INVISIBLE);
            showCaptureView(View.VISIBLE);
        } else if (id == R.id.btn_captured_submit) {
            finish();
        }
    }

    private void showCaptureView(int visibility) {
        captureLayout.setVisibility(visibility);
    }

    private void showCapturedView(int visibility) {
        capturedLayout.setVisibility(visibility);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        previewView.stop();

        String cacheDir = getExternalFilesDir("").getAbsolutePath();
        String imagePath = cacheDir + "/capture/KTP.png";

        PictureDataHandler dataHandler = new PictureDataHandler.PictureConfig()
                .configGenerate(data)
                .configRotate()
                .configCrop(findView.getLeftPercent(), findView.getTopPercent())
                .configCompress("png", imagePath, 160, 90, 90)
                .build();

        try {
            dataHandler.generateBitmap()
                    .rotateBitmap()
                    .cropBitmap()
                    .saveAndCompressBitmap(false)
                    .load(imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
