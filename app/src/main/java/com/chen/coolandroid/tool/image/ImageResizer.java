package com.chen.coolandroid.tool.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

/**
 * Created by Apple on 2018/12/13.
 *
 * 《Android艺术探索》chapter-12
 *  https://github.com/singwhatiwanna/android-art-res/blob/master/Chapter_12/
 *
 * 图片压缩
 */

public class ImageResizer {
    private static final String TAG = "ImageResizer";

    public ImageResizer() {
        //Null constructor
    }

    public Bitmap decodeSampleBitmapFromResource(
            Resources res, int resId, int reqWidth, int reqHeight) {
        //First decode with inJustDecodeBounds=true to check dimensions;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        //Calculate inSampleSize;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //Decode bitmap with inSampleSize set;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    public Bitmap decodeSampleBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {
        //First decode width inJustDecodeBounds = true to check dimensions;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        //Calculate inSampleSize;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //Decode bitmap with inSampleSize set;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight){
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        //Raw width and height of image;
        final int width = options.outWidth;
        final int height = options.outHeight;

        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            //calculate the largest inSampleSize value;
            while ((halfHeight / inSampleSize) >= reqHeight &&
                    (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
