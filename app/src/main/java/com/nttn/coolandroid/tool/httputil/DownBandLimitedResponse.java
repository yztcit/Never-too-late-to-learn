package com.nttn.coolandroid.tool.httputil;

import android.support.annotation.NonNull;

import com.nttn.coolandroid.tool.LogUtil;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class DownBandLimitedResponse extends ResponseBody {
    private static final String TAG = "DownBandLimited";
    private ResponseBody mResponseBody;
    private ProgressListener mListener;
    private long maxRate;
    private BufferedSource mBufferSource;

    public static DownBandLimitedResponse createDownBandLimitedResponse(ResponseBody responseBody,
                                                                        long maxRate,
                                                                        ProgressListener listener){
        return new DownBandLimitedResponse(responseBody, maxRate, listener);
    }

    private DownBandLimitedResponse(ResponseBody responseBody, long maxRate,
                                    ProgressListener listener) {
        this.mResponseBody = responseBody;
        this.maxRate = maxRate;
        this.mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @NonNull
    @Override
    public BufferedSource source() {
        if (mBufferSource == null) {
            mBufferSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferSource;
    }

    //限制下载流的均速
    private Source source(final Source source) {

        return new ForwardingSource(source) {
            long start = System.currentTimeMillis();
            long totalBytesRead = 0L;
            long contentLength = 0L;
            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                //只需要获取一次总长度
                if (totalBytesRead == 0) {
                    contentLength = contentLength();
                }

                long readCount;
                if ((readCount = source.read(sink, byteCount)) != -1) {
                    totalBytesRead += readCount;

                    //进度回调
                    mListener.onProgress(
                            totalBytesRead, contentLength, contentLength == totalBytesRead
                    );

                    long time = System.currentTimeMillis();
                    if (time == start) return totalBytesRead;

                    long rate = (totalBytesRead * 8) / (time - start);
                    LogUtil.e(TAG, "mMaxRate: " + maxRate / 1000);

                    if (rate > maxRate / 1000) {
                        int sleep = (int) (totalBytesRead * 8 * 1000 / maxRate - (time - start));
                        LogUtil.e(TAG, "totalBytesRead:" + totalBytesRead + "B "
                                        + " Rate:" + rate * 1000 + "bits"
                                        + " time:" + (time - start)
                                        + " sleep: " + sleep
                        );
                        try {
                            if (sleep > 0) {
                                Thread.sleep(sleep);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return totalBytesRead;
            }
        };
    }
}
