package com.nttn.coolandroid.tool.result;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

public class OnActResultEventDispatcherFragment extends Fragment {
    public static final String TAG = "on_act_result_event_dispatcher";

    private SparseArray<ActResultRequest.Callback> mCallbacks = new SparseArray<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void requestPermissions(int requestCode, ActResultRequest.Callback callback, String... permissions) {
        mCallbacks.put(requestCode, callback);
        requestPermissions(permissions, requestCode);
    }

    public void startForResult(Intent intent, int requestCode, ActResultRequest.Callback callback) {
        // mRequestCode与callback需要一一对应
        mCallbacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ActResultRequest.Callback callback = mCallbacks.get(requestCode);
        mCallbacks.remove(requestCode);

        if (callback != null) {
            callback.onActivityResult(requestCode, resultCode, data);
        }
    }

}
