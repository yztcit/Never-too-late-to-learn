package com.nttn.coolandroid.tool.httputil;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


import com.nttn.coolandroid.tool.LogUtil;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class UpBandLimitedRequest extends RequestBody{
    private static final String TAG = "UpBandLimitedRequest";
    private MediaType mContentType;
    private File mFile;
    private long mMaxRate; // bit/ms

    private ProgressListener mListener;

    public static RequestBody createRequestBody(@Nullable final MediaType contentType,
                                                final File file, long rate,
                                                ProgressListener listener) {
        if (file == null) {
            throw new NullPointerException("content == null");
        } else {
            return new UpBandLimitedRequest(contentType, file, rate, listener);
        }
    }

    private UpBandLimitedRequest(MediaType contentType, File file,
                                 long maxRate, ProgressListener listener) {
        //maxRate 2MB/s  --> 2 * 1024 * 1024 * 8 b/1000ms
        this.mContentType = contentType;
        this.mFile = file;
        this.mMaxRate = maxRate;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mContentType;
    }

    @Override
    public void writeTo(@NonNull BufferedSink bufferedSink) throws IOException {
        Source source = null;
        try {
            /*
             *  reflect instead of Okio.source(mFile) because of build error at platform 23.
             *  the error is java.nio.** can't find.
             */
            source = Okio.source(mFile);
//            String className = "okio.Okio";
//            String methodName = "source";
//            Class<?> clazz = Class.forName(className);
//            Method method = clazz.getMethod(methodName, File.class);
//            source = (Source) method.invoke(null, mFile);
            writeAll(bufferedSink, source);
        } catch (/*NoSuchMethodException | IllegalAccessException
                | ClassNotFoundException | InvocationTargetException
                |*/ InterruptedException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(source);
        }
    }

    //限制上传流的均速
    private void writeAll(BufferedSink sink, Source source) throws IOException, InterruptedException {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        } else {
            long fileLengthTotal = mFile.length();
            LogUtil.e(TAG, "writeTo: " + fileLengthTotal);
            long totalBytesRead = 0L;

            long readCount;
            long start = System.currentTimeMillis();

            while ((readCount = source.read(sink.buffer(), 8192L)) != -1L) {
                totalBytesRead += readCount;
                sink.emitCompleteSegments();

                long time = System.currentTimeMillis();
                if (time == start) continue;
                long rate = (totalBytesRead * 8) / (time - start);

                LogUtil.e(TAG, "mMaxRate: " + mMaxRate / 1000);
                if (rate > mMaxRate / 1000) {
                    int sleep = (int) (totalBytesRead * 8 * 1000 / mMaxRate - (time - start));
                    LogUtil.e(TAG, "totalBytesRead:" + totalBytesRead +
                            "B " + " Rate:" + rate * 1000 + "bits" + " time:" + (time - start));
                    LogUtil.e(TAG, "sleep:" + sleep);
                    if (sleep > 0) {
                        Thread.sleep(sleep);
                    }
                }

                if (mListener != null) {
                    mListener.onProgress(
                            totalBytesRead,
                            fileLengthTotal,
                            totalBytesRead == fileLengthTotal
                    );
                }
            }

            long end = System.currentTimeMillis();
            long rate = (totalBytesRead * 8 * 1000) / ((end - start));
            LogUtil.e(TAG, "totalBytesRead:" + totalBytesRead +
                    "B " + " Rate:" + rate + "bits" + " total time:" + (end - start));
        }
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException var2) {
                throw var2;
            } catch (Exception var3) {
                Log.e("SDK", "closeQuietly close exception");
            }
        }
    }
}
