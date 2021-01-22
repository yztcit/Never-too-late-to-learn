package com.nttn.coolandroid.learnui.widget.capture;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.Display;
import android.view.WindowManager;

import com.nttn.coolandroid.tool.LogUtil;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Description:  相机参数配置  <br>
 * Version: 1.0     <br>
 * Update：2021/1/21   <br>
 * Created by Apple.
 */
public final class CameraConfigManager {
    private static final String TAG = CameraConfigManager.class.getSimpleName();
    private static final int TEN_DESIRED_ZOOM = 5;
    private static final int DESIRED_SHARPNESS = 30;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private Point screenResolution;
    private Point cameraResolution;

    public CameraConfigManager() {

    }

    public void initFromCameraParameters(Context context, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        this.screenResolution = new Point(display.getWidth(), display.getHeight());
        LogUtil.d(TAG, "Screen resolution: " + this.screenResolution);
        Point screenResolutionForCamera = new Point();
        screenResolutionForCamera.x = this.screenResolution.x;
        screenResolutionForCamera.y = this.screenResolution.y;
        if (this.screenResolution.x < this.screenResolution.y) {
            screenResolutionForCamera.x = this.screenResolution.y;
            screenResolutionForCamera.y = this.screenResolution.x;
        }

        this.cameraResolution = getCameraResolution(parameters, screenResolutionForCamera);
    }

    public void setDesiredCameraParameters(Camera camera) {
        LogUtil.d(TAG, "Setting preview size: " + this.cameraResolution);
        Camera.Parameters parameters = camera.getParameters();
        // 设置预览格式
        parameters.setPreviewFormat(ImageFormat.NV21);

        parameters.setPreviewSize(this.cameraResolution.x, this.cameraResolution.y);
        parameters.setPictureSize(this.cameraResolution.x, this.cameraResolution.y);
        this.setZoom(parameters);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
    }

    public Point getCameraResolution() {
        return this.cameraResolution;
    }

    public Point getScreenResolution() {
        return this.screenResolution;
    }

    private static Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {
        String previewSizeValueString = parameters.get("preview-size-values");
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }

        Point cameraResolution = null;
        if (previewSizeValueString != null) {
            LogUtil.d(TAG, "preview-size-values parameter: " + previewSizeValueString);
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
        }

        if (cameraResolution == null) {
            cameraResolution = new Point(screenResolution.x >> 3 << 3, screenResolution.y >> 3 << 3);
        }

        return cameraResolution;
    }

    private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int diff = 2147483647;
        String[] var5 = COMMA_PATTERN.split(previewSizeValueString);

        for (String s : var5) {
            String previewSize = s;
            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf(120);
            if (dimPosition < 0) {
                LogUtil.w(TAG, "Bad preview-size: " + previewSize);
            } else {
                int newX;
                int newY;
                try {
                    newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                    newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
                } catch (NumberFormatException var13) {
                    LogUtil.w(TAG, "Bad preview-size: " + previewSize);
                    continue;
                }

                int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
                if (newDiff == 0) {
                    bestX = newX;
                    bestY = newY;
                    break;
                }

                if (newDiff < diff) {
                    bestX = newX;
                    bestY = newY;
                    diff = newDiff;
                }
            }
        }

        return bestX > 0 && bestY > 0 ? new Point(bestX, bestY) : null;
    }

    private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
        LogUtil.i("CameraManager", "findBestMotZoomValue");
        int tenBestValue = 0;
        String[] var3 = COMMA_PATTERN.split(stringValues);

        for (String s : var3) {
            String stringValue = s;
            stringValue = stringValue.trim();

            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException var10) {
                return tenDesiredZoom;
            }

            int tenValue = (int) (10.0D * value);
            if (Math.abs((double) tenDesiredZoom - value) < (double) Math.abs(tenDesiredZoom - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }

        LogUtil.i("findBestMotZoomValue", tenBestValue + "");
        return tenBestValue;
    }

    private void setZoom(Camera.Parameters parameters) {
        LogUtil.i("CameraManager", "setZoom");
        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString == null || Boolean.parseBoolean(zoomSupportedString)) {
            int tenDesiredZoom = TEN_DESIRED_ZOOM;
            String maxZoomString = parameters.get("max-zoom");
            if (maxZoomString != null) {
                try {
                    int tenMaxZoom = (int)(10.0D * Double.parseDouble(maxZoomString));
                    if (tenDesiredZoom > tenMaxZoom) {
                        tenDesiredZoom = tenMaxZoom;
                    }
                } catch (NumberFormatException var13) {
                    LogUtil.w(TAG, "Bad max-zoom: " + maxZoomString);
                }
            }

            String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
            if (takingPictureZoomMaxString != null) {
                try {
                    int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                    if (tenDesiredZoom > tenMaxZoom) {
                        tenDesiredZoom = tenMaxZoom;
                    }
                } catch (NumberFormatException var12) {
                    LogUtil.w(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
                }
            }

            String motZoomValuesString = parameters.get("mot-zoom-values");
            if (motZoomValuesString != null) {
                tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
            }

            String motZoomStepString = parameters.get("mot-zoom-step");
            if (motZoomStepString != null) {
                try {
                    double motZoomStep = Double.parseDouble(motZoomStepString.trim());
                    int tenZoomStep = (int)(10.0D * motZoomStep);
                    if (tenZoomStep > 1) {
                        tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                    }
                } catch (NumberFormatException var11) {
                }
            }

            if (maxZoomString != null || motZoomValuesString != null) {
                parameters.set("zoom", String.valueOf((double)tenDesiredZoom / 10.0D));
            }

            if (takingPictureZoomMaxString != null) {
                parameters.set("taking-picture-zoom", tenDesiredZoom);
            }

        }
    }

    public static int getDesiredSharpness() {
        return DESIRED_SHARPNESS;
    }
}
