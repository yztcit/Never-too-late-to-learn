package com.chen.coolandroid.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 *     author : Apple
 *     e-mail : xxx@xx
 *     time   : 2019/10/29
 *     desc   : Download Util
 *     todo：1.manage threads；2.parse data（progress, data type, download speed, reconnect）
 *     version: 1.0
 * </pre>
 */
public final class DownloadUtils {
    private static final String TAG = "DownloadUtils";
    private Context mContext;
    private static DownloadUtils sDownloadUtils;

    private static final int LOG_INFO  = 1;
    private static final int LOG_DEBUG = 2;
    private static final int LOG_WARN  = 3;
    private static final int LOG_ERROR = 4;
    @IntDef({LOG_INFO, LOG_DEBUG, LOG_WARN, LOG_ERROR})
    @Retention(RetentionPolicy.CLASS)
    private @interface LogType{}
    private int LOG_STATE = 0;//默认开启Log

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;
    private static final int IO_BUFFERED_SIZE = 8 * 1024;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, TAG + "#" + mCount.getAndIncrement());
        }
    };
    private static final ThreadPoolExecutor sThreadPoolExecutor = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), sThreadFactory
    );

    public static DownloadUtils getInstance(Context context) {
        if (sDownloadUtils == null) {
            synchronized (DownloadUtils.class) {
                if (sDownloadUtils == null) {
                    sDownloadUtils = new DownloadUtils(context);
                }
            }
        }
        return sDownloadUtils;
    }

    private DownloadUtils(Context context) {
        mContext = context.getApplicationContext();
    }

    public DownloadUtils setLogState(@LogType int logState) {
        LOG_STATE = logState;
        return this;
    }

    private DownloadCallback mDownloadCallback;
    public DownloadUtils setDownloadCallback(DownloadCallback downloadCallback){
        mDownloadCallback = downloadCallback;
        return this;
    }

    public DownloadUtils downloadFromUrl(String url) {
        logE("url>>>" + url);
        if (mDownloadCallback == null) {
            Log.w(TAG, "DownloadCallback cannot be null.");
        }

        Runnable runnable = null;
        if (mDownloadCallback instanceof DownloadBitmapCallback) {
            runnable = downloadBitmap(url);
        }

        if (runnable == null) return this;
        sThreadPoolExecutor.execute(runnable);
        return this;
    }

    private Runnable downloadBitmap(String url) {
        final String source = url;
        return new Runnable() {
            @Override
            public void run() {
                // TODO: 2019/10/29 将下载内容缓存起来
                Bitmap bitmap = downloadBitmapFromUrl(source);
                if (mDownloadCallback != null) {
                    mDownloadCallback.onComplete(bitmap);
                }
            }
        };
    }

    /**
     * disk cache is not created, download from url without any cache
     *
     * @param urlString bitmap url
     * @return bitmap
     */
    private Bitmap downloadBitmapFromUrl(String urlString) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFERED_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            Log.e(TAG, "downloadBitmapFromUrl: failed\n" + e);
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

    /**
     *  log msg according to LOG_STATE
     *
     * @param msg log message
     * @see #LOG_STATE
     */
    private void logE(String msg) {
        if (LOG_STATE >= LOG_ERROR) {
            Log.e(TAG, msg);
        }
    }

    public interface DownloadCallback<T>{
        void onComplete(T data);
    }

    public interface DownloadBitmapCallback<Bitmap> extends DownloadCallback<Bitmap>{}
}
