package com.nttn.coolandroid.learnui.widget.capture;

import android.hardware.Camera;

import com.nttn.coolandroid.tool.LogUtil;

/**
 * Description:  open camera   <br>
 * Version: 1.0     <br>
 * Update：2021/1/21   <br>
 * Created by Apple.
 */
public final class OpenCameraImp {
    private static final String TAG = OpenCameraImp.class.getSimpleName();
    private OpenCameraImp() {

    }

    /**
     * 打开指定摄像头
     *
     * @param cameraId 相机id
     * @return camera
     */
    public static Camera open(int cameraId) {
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras == 0) {
            LogUtil.w(TAG, "no camera");
            return null;
        }

        boolean explicitRequest = cameraId >= 0;
        if (!explicitRequest) {
            int index;
            for (index = 0; index < numberOfCameras; ++index) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(index, cameraInfo);
                if (cameraInfo.facing == 0) break;
            }
            cameraId = index;
        }

        Camera camera;
        if (cameraId < numberOfCameras) {
            LogUtil.i(TAG, "Open camera #" + cameraId);
            camera = Camera.open(cameraId);
        } else if (explicitRequest) {
            LogUtil.i(TAG, "Requested camera does not exist: " + cameraId);
            camera = null;
        } else {
            LogUtil.i(TAG, "No camera facing back; returning camera #0");
            camera = Camera.open(0);
        }
        return camera;
    }

    /**
     * 默认打开后置摄像头
     *
     * @return camera
     */
    public static Camera open() {
        // open camera face back
        return open(-1);
    }
}
