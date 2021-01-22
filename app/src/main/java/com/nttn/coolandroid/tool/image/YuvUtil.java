package com.nttn.coolandroid.tool.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlend;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import com.nttn.coolandroid.tool.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Description: RenderScript 实现 NV21转Bitmap  <br>
 * Version: 1.0     <br>
 * Update：2021/1/22   <br>
 * Created by Apple.
 */
public final class YuvUtil {
    private static final String TAG = YuvUtil.class.getSimpleName();

    private YuvUtil() {
    }

    /**
     * nv21转bmp
     */
    public static Bitmap nv21ToBitmap(Context context, byte[] nv21, int width, int height) {
        long start = System.currentTimeMillis();

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21.length);
        Allocation yuvIn = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
        yuvIn.copyFrom(nv21);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        Allocation yuvOut = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);


        yuvToRgbIntrinsic.setInput(yuvIn);
        yuvToRgbIntrinsic.forEach(yuvOut);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        yuvOut.copyTo(bitmap);

        LogUtil.d(TAG, "nv21ToBitmap: " + (System.currentTimeMillis() - start) + "ms");
        return bitmap;
    }

    /**
     * 两个bmp按照透明度混合
     */
    public static Bitmap bitmapBlendBitmap(Context context, Bitmap bottom, Bitmap top) {
        long start = System.currentTimeMillis();
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlend scriptIntrinsicBlend = ScriptIntrinsicBlend.create(rs, Element.RGBA_8888(rs));

        Type.Builder rgbType1 = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(top.getWidth()).setY(top.getHeight());
        Type.Builder rgbType2 = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(bottom.getWidth()).setY(bottom.getHeight());
        Allocation argbPic1 = Allocation.createTyped(rs, rgbType1.create(), Allocation.USAGE_SCRIPT);
        Allocation argbPic2 = Allocation.createTyped(rs, rgbType2.create(), Allocation.USAGE_SCRIPT);

        argbPic1.copyFrom(top);
        argbPic2.copyFrom(bottom);
        // scriptIntrinsicBlend.forEachAdd(argbPic1, mYuvOut);
        // scriptIntrinsicBlend.forEachDstAtop(argbPic1, argbPic2);
        // scriptIntrinsicBlend.forEachDstIn(argbPic1, argbPic2);
        // scriptIntrinsicBlend.forEachDstOut(argbPic2, argbPic1);
        scriptIntrinsicBlend.forEachDstOver(argbPic2, argbPic1);

        Bitmap bitmap = Bitmap.createBitmap(top.getWidth(), top.getHeight(), Bitmap.Config.ARGB_8888);
        argbPic1.copyTo(bitmap);

        LogUtil.d(TAG, "bitmapBlendBitmap: " + (System.currentTimeMillis() - start) + "ms");
        return bitmap;
    }


    /**
     * 传统方法把YUV转jpg
     **/
    public static Bitmap nv21ToBitmapDirectSave(byte[] nv21, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        YuvImage im = new YuvImage(nv21, ImageFormat.NV21, width, height, new int[]{width, width, width});
        im.compressToJpeg(new Rect(0, 0, width, height), 100, os);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                LogUtil.e(TAG, "OutputStream close exception");
            }
        }
        return bitmap;
    }
}
