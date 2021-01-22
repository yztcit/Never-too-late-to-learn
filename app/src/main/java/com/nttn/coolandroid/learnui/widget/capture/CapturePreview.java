package com.nttn.coolandroid.learnui.widget.capture;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.nttn.coolandroid.tool.LogUtil;

import java.io.IOException;

/**
 * Description:  拍照预览   <br>
 * Version: 1.0     <br>
 * Update：2021/1/21   <br>
 * Created by Apple.
 */
public class CapturePreview extends SurfaceView implements View.OnTouchListener, SurfaceHolder.Callback,
        Camera.AutoFocusCallback {
    private static final String TAG = CapturePreview.class.getSimpleName();

    private Camera camera;
    private CameraConfigManager configManager;
    private boolean isInit, isPreviewing;

    public CapturePreview(Context context) {
        super(context);
        initConfigs();
    }

    public CapturePreview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initConfigs();
    }

    private void initConfigs() {
        SurfaceHolder holder = getHolder();
        // 指定用于捕捉拍照事件的SurfaceHolder.Callback对象
        holder.addCallback(this);
        // deprecated method, auto set now
        // holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        configManager = new CameraConfigManager();
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int viewWidth = MeasureSpec.getSize(widthSpec);
        int viewHeight = MeasureSpec.getSize(heightSpec);
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isPreviewing) camera.autoFocus(this);
        }
        return true;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!checkCameraHardware(getContext())) return;
        camera = OpenCameraImp.open();
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            LogUtil.e(TAG, e.getMessage());
            release();
        }

        if (camera == null) return;

        updateParameters();

        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        release();
    }

    private void updateParameters() {
        if (!this.isInit) {
            this.isInit = true;
            this.configManager.initFromCameraParameters(getContext(), camera);
        }

        Camera.Parameters parameters = camera.getParameters();
        String parametersFlattened = parameters == null ? null : parameters.flatten();

        try {
            this.configManager.setDesiredCameraParameters(camera);
        } catch (RuntimeException var8) {
            LogUtil.w(TAG, "Camera rejected parameters. Setting only minimal safe-mode parameters");
            LogUtil.i(TAG, "Resetting to saved camera params: " + parametersFlattened);
            if (parametersFlattened != null) {
                parameters = camera.getParameters();
                parameters.unflatten(parametersFlattened);

                try {
                    camera.setParameters(parameters);
                    this.configManager.setDesiredCameraParameters(camera);
                } catch (RuntimeException var7) {
                    LogUtil.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
                }
            }
        }
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void takePicture(Camera.PictureCallback callback) {
        if (camera == null) return;
        try {
            camera.takePicture(null, null, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (camera == null) return;
        isPreviewing = true;
        camera.startPreview();
    }

    public void stop() {
        if (camera == null) return;
        isPreviewing = false;
        camera.stopPreview();
    }

    public void release() {
        if (camera == null) return;
        isPreviewing = false;
        camera.release();
        camera = null;
    }
}
