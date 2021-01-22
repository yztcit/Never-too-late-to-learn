package com.nttn.coolandroid.learnui.widget.capture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.ImageView;

import com.nttn.coolandroid.tool.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Desc: 处理拍照回调数据 byte[]
 * Version: 1.0.0
 * Update by:
 * Created by cp on 2021/1/22.
 */
public class PictureDataHandler {
    private static final String TAG = PictureDataHandler.class.getSimpleName();
    private PictureConfig config;
    private Bitmap bitmap;

    private PictureDataHandler(PictureConfig config) {
        this.config = config;
    }

    public synchronized PictureDataHandler generateBitmap() {
        // generate bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config.preferredConfig == null ? Bitmap.Config.ARGB_8888 : config.preferredConfig;
        bitmap = BitmapFactory.decodeByteArray(config.data, config.offset, config.data.length, options);

        LogUtil.e("buildBitmap", "\n byteCount: " + bitmap.getByteCount()
                + "\n size(" + bitmap.getWidth() + ", " + bitmap.getHeight() + ")");
        return this;
    }

    public synchronized PictureDataHandler rotateBitmap() {
        checkBitmapNotNull();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(config.rotate);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        LogUtil.e("rotateBitmap", "\n byteCount: " + bitmap.getByteCount()
                + "\n size(" + bitmap.getWidth() + ", " + bitmap.getHeight() + ")");
        return this;
    }

    public synchronized PictureDataHandler cropBitmap() {
        checkBitmapNotNull();
        LogUtil.e("cropBitmap", "originBitmap: " + bitmap.getWidth() + ", " + bitmap.getHeight());

        float topPercent = config.topPercent;
        float leftPercent = config.leftPercent;
        int left = (int) (bitmap.getWidth() * leftPercent);
        int top = (int) (bitmap.getHeight() * topPercent);

        int width = (int) (bitmap.getWidth() * (1 - leftPercent * 2));
        int height = (int) (bitmap.getHeight() * (1 - topPercent * 2));

        LogUtil.e("cropBitmap", "cropWidth: (" + left + ", " + top + ", " + width + ", " + height + ")");

        bitmap = Bitmap.createBitmap(bitmap, left, top, width, height);

        LogUtil.e("cropBitmap", "cropBitmap: " + bitmap.getWidth() + ", " + bitmap.getHeight());
        return this;
    }

    public synchronized PictureDataHandler saveAndCompressBitmap(boolean isCompress) throws Exception{
        checkBitmapNotNull();

        saveBitmap(bitmap, config.path);
        if (!isCompress) return this;

        compressImage(config.path, getCompressFormat(config.format), config.compressWidth, config.compressHeight, config.quality);
        return this;
    }

    public void load(ImageView imageView) {
        imageView.setImageBitmap(bitmap);
    }

    public void release() {
        if (bitmap == null) return;
        bitmap.recycle();
        bitmap = null;
        config = null;
    }

    public static File saveBitmap(Bitmap image, String imagePath) throws Exception {
        LogUtil.i("saveBitmap", " saveBitmap into path " + imagePath + " image == null " + (null == image));
        if (null == image) {
            return null;
        } else {
            String format = "png";
            int index = imagePath.lastIndexOf(".");
            if (index != -1) {
                format = imagePath.substring(index + 1);
            }

            Bitmap.CompressFormat compressFormat = getCompressFormat(format);
            checkPathDirExist(imagePath);
            File file = new File(imagePath);
            BufferedOutputStream out = null;
            out = new BufferedOutputStream(new FileOutputStream(file));
            image.compress(compressFormat, 100, out);
            out.flush();
            if (out != null) {
                out.close();
            }
            return file;
        }
    }

    public static void compressImage(String imagePath, Bitmap.CompressFormat compressFormat, int limitWidth, int limitHeight, int mQuality) throws Exception {
        // todo 压缩处理
        BitmapFactory.Options opts = null;
        if (limitWidth > 0 && limitHeight > 0) {
            opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, opts);
            Math.min(limitWidth, limitHeight);
            opts.inSampleSize = 2;
            opts.inJustDecodeBounds = false;
            opts.inInputShareable = true;
            opts.inPurgeable = true;
        }

        Bitmap bm = null;

        try {
            bm = BitmapFactory.decodeFile(imagePath, opts);
            saveBitmap(bm, imagePath);
        } catch (OutOfMemoryError var12) {
            LogUtil.e(TAG, var12.getMessage());
        } finally {
            if (bm != null) {
                bm.recycle();
            }

        }

    }

    public static Bitmap.CompressFormat getCompressFormat(String format) {
        if ("jpeg".equalsIgnoreCase(format)) {
            return Bitmap.CompressFormat.JPEG;
        } else if ("jpg".equalsIgnoreCase(format)) {
            return Bitmap.CompressFormat.JPEG;
        } else {
            return "webp".equalsIgnoreCase(format) ? Bitmap.CompressFormat.WEBP : Bitmap.CompressFormat.PNG;
        }
    }

    private static void checkPathDirExist(String path) {
        String dirPath = path.substring(0, path.lastIndexOf("/"));
        LogUtil.i(TAG, " checkPathDirExsit dir " + dirPath);
        File fileDir = new File(dirPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
    }

    private void checkBitmapNotNull() {
        if (bitmap == null) {
            throw new NullPointerException("call generate bitmap first");
        }
    }

    public static class PictureConfig {
        /**生成*/
        private byte[] data;
        private int offset = 0;
        private Bitmap.Config preferredConfig = Bitmap.Config.ARGB_8888;
        /**旋转*/
        private int rotate = 90;
        /**压缩*/
        private String format, path;
        private int compressWidth, compressHeight, quality = 80;
        /**剪裁*/
        private float leftPercent, topPercent;

        public PictureConfig() {

        }

        public PictureConfig configGenerate(byte[] data) {
            this.data = data;
            return this;
        }

        public PictureConfig configGenerate(byte[] data, int offset, Bitmap.Config preferredConfig) {
            this.data = data;
            this.offset = offset;
            this.preferredConfig = preferredConfig;
            return this;
        }

        public PictureConfig configRotate() {
            return this;
        }

        public PictureConfig configRotate(int rotate) {
            this.rotate = rotate;
            return this;
        }

        public PictureConfig configCompress(String format, String path, int compressWidth, int compressHeight, int quality) {
            this.format = format;
            this.path = path;
            this.compressWidth = compressWidth;
            this.compressHeight = compressHeight;
            this.quality = quality;
            return this;
        }

        public PictureConfig configCrop(float leftPercent, float topPercent) {
            this.leftPercent = leftPercent;
            this.topPercent = topPercent;
            return this;
        }

        public PictureDataHandler build() {
            return new PictureDataHandler(this);
        }
    }
}
