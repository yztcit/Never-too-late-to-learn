package com.chen.coolandroid.tool.image;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.chen.coolandroid.tool.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ---BigGod be here!----
 * ***┏┓******┏┓*********
 * *┏━┛┻━━━━━━┛┻━━┓*******
 * *┃     ━━━     ┃*******
 * *┃     ━━━     ┃*******
 * *┃  ━┳┛   ┗┳━  ┃*******
 * *┃             ┃*******
 * *┃     ━┻━     ┃*******
 * *┗━━━┓     ┏━━━┛*******
 * *****┃     ┃神兽保佑*****
 * *****┃     ┃代码无BUG！***
 * *****┃     ┗━━━━━━━━┓*****
 * *****┃              ┣┓****
 * *****┃              ┏┛****
 * *****┗━┓┓┏━━━━┳┓┏━━━┛*****
 * *******┃┫┫****┃┫┫********
 * *******┗┻┛****┗┻┛*********
 * ━━━━━━神兽出没━━━━━━
 * Created by Apple on 2018/12/13.
 * TODO: 处理 SkImageDecoder::Factory returned null
 */

public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private static final String DISK_CACHE_DIR_NAME = "bitmap";//disk cache dir

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final int MESSAGE_POST_RESULT = 1;

    //---init DiskLruCache---
    //app version,once the value be changed,the disk cache will be clear;
    private static final int appVersion = 1;
    private static final int valueCount = 1;//一个节点对应一个数据
    //---init DiskLruCache---
    private static long DISK_CACHE_SIZE = 1024 * 1024 * 50;//disk cache: 50M
    private static final int DISK_CACHE_INDEX = 0;
    private static final int IO_BUFFERED_SIZE = 8 * 1024;

    private boolean mIsDiskLruCacheCreated = false;

    private Context mContext;
    private ImageResizer mImageResizer = new ImageResizer();
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
        }
    };
    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), sThreadFactory
    );

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LoadResult loadResult = (LoadResult) msg.obj;
            ImageView imageView = loadResult.imageView;
            int id = loadResult.id;
            String url = (String) imageView.getTag(id);
            if (TextUtils.equals(loadResult.url, url)) {
                imageView.setImageBitmap(loadResult.bitmap);
            } else {
                LogUtil.w(TAG, "set image bitmap,but url changed,ignored!");
            }
        }
    };

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();

        //init memory cache
        initMemoryCache();

        //init disk cache
        initDiskCache();
    }

    /**
     * init memory cache
     */
    private void initMemoryCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    /**
     * init disk cache
     */
    private void initDiskCache() {
        File diskFileDir = getDiskCacheDir();
        if (!diskFileDir.exists()) {
            diskFileDir.mkdirs();
        }

        if (getUsableSpace(diskFileDir) > DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(
                        diskFileDir,
                        appVersion, valueCount,
                        DISK_CACHE_SIZE
                );
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.w(TAG, "usableSpace is not enough!");
        }
    }

    /**
     * build a new instance of ImageLoader
     *
     * @param context Context
     * @return a new instance of ImageLoader
     */
    public static ImageLoader build(Context context) {
        return new ImageLoader(context);
    }

    public void bindBitmap(String url, ImageView imageView) {
        bindBitmap(url, imageView, 0, 0);
    }

    public void bindBitmap(final String url, final ImageView imageView,
                           final int reqWidth, final int reqHeight) {
        final int id = imageView.getId();
        imageView.setTag(id, url);
        Bitmap bitmap = loadBitmapFromMemoryCache(url);
        if (bitmap != null) {
            LogUtil.d(TAG, "loadBitmap: memory cache\n" + url);
            imageView.setImageBitmap(bitmap);
            return;
        }

        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(url, reqWidth, reqHeight);
                if (bitmap != null) {
                    LoadResult loadResult = new LoadResult(url, id, imageView, bitmap);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT, loadResult).sendToTarget();
                }
            }
        };

        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    /**
     * load bitmap from memory cache or disk cache or network
     *
     * @param url       http url
     * @param reqWidth  ImageView width
     * @param reqHeight ImageView height
     * @return bitmap(may be null)
     */
    private Bitmap loadBitmap(String url, int reqWidth, int reqHeight) {
        Bitmap bitmap = loadBitmapFromMemoryCache(url);
        if (bitmap != null) {
            LogUtil.d(TAG, "loadBitmap: memory cache\n" + url);
            return bitmap;
        }
        try {
            bitmap = loadBitmapFromDiskCache(url, reqWidth, reqHeight);
            if (bitmap != null) {
                LogUtil.d(TAG, "loadBitmap: disk cache\n" + url);
                return bitmap;
            }
            bitmap = loadBitmapFromHttp(url, reqWidth, reqHeight);
            LogUtil.d(TAG, "loadBitmap: url\n" + url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null && !mIsDiskLruCacheCreated) {
            Log.w(TAG, "loadBitmap: encounter error, DiskLruCache is not created");
            bitmap = downloadUrlFromUrl(url);
        }
        return bitmap;
    }

    private Bitmap loadBitmapFromMemoryCache(String url) {
        final String hashKeyFromUrl = hashKeyFromUrl(url);
        return getBitmapFromMemoryCache(hashKeyFromUrl);
    }

    private Bitmap loadBitmapFromDiskCache(String url, int reqWidth, int reqHeight)
            throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "load bitmap in UI thread is not recommended!");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        Bitmap bitmap = null;
        String key = hashKeyFromUrl(url);
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if (snapshot != null) {
            FileInputStream fileInputStream = (FileInputStream) snapshot
                    .getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fd = fileInputStream.getFD();
            bitmap = mImageResizer.decodeSampleBitmapFromFileDescriptor(fd, reqWidth, reqHeight);
            if (bitmap != null) {
                addBitmapToMemoryCache(key, bitmap);
            }
        }

        return bitmap;
    }

    /**
     * download bitmap from url with cache
     *
     * @param url       bitmap url
     * @param reqWidth  ImageView width
     * @param reqHeight ImageView height
     * @return bitmap
     * @throws IOException when IO operation
     */
    private Bitmap loadBitmapFromHttp(String url, int reqWidth, int reqHeight)
            throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network in UI thread!");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        String key = hashKeyFromUrl(url);
        DiskLruCache.Editor edit = mDiskLruCache.edit(key);
        if (edit != null) {
            OutputStream outputStream = edit.newOutputStream(DISK_CACHE_INDEX);
            if (downloadUrlToStream(url, outputStream)) {
                edit.commit();
            } else {
                edit.abort();
            }
            mDiskLruCache.flush();
        }
        return loadBitmapFromDiskCache(url, reqWidth, reqHeight);
    }

    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        final URL url;
        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFERED_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFERED_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "downloadUrlToStream: failed\n" + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            close(in);
            close(out);
        }
        return false;
    }

    /**
     * disk cache is not created, download from url without any cache
     *
     * @param urlString bitmap url
     * @return bitmap
     */
    private Bitmap downloadUrlFromUrl(String urlString) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFERED_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            Log.e(TAG, "downloadUrlFromUrl: failed\n" + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            close(in);
        }
        return bitmap;
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String hashKeyFromUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url);
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    private File getDiskCacheDir() {
        boolean externalStorageAvailable = Environment
                .getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            File externalCacheDir = mContext.getExternalCacheDir();
            if (externalCacheDir == null) {
                externalCacheDir = new File(
                        Environment.getExternalStorageDirectory().getPath() +
                                File.separator + "cache"
                );
            }
            cachePath = externalCacheDir.getPath();
        } else {
            cachePath = mContext.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + DISK_CACHE_DIR_NAME);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private long getUsableSpace(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return file.getUsableSpace();
        }
        final StatFs statFs = new StatFs(file.getPath());
        return statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
    }

    private static class LoadResult {
        public String url;
        public ImageView imageView;
        public int id;
        public Bitmap bitmap;

        public LoadResult(String url, int id, ImageView imageView, Bitmap bitmap) {
            this.url = url;
            this.id = id;
            this.imageView = imageView;
            this.bitmap = bitmap;
        }
    }
}
