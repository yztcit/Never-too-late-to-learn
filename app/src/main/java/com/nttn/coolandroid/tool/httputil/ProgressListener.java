package com.nttn.coolandroid.tool.httputil;

public interface ProgressListener {
    void onProgress(long currentBytes, long contentLength, boolean done);
}
