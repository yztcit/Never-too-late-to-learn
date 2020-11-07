package com.nttn.coolandroid.learnui.uiadvance;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ToastUtils;
import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseHeadActivity;
import com.nttn.coolandroid.custom.SignatureView;

/**
 * Description: 手写签名 <br>
 * Version: 1.0         <br>
 * Update：            <br>
 * Created by Apple.
 */
public class SignatureActivity extends BaseHeadActivity implements View.OnClickListener {
    private static final String TAG = "SignatureActivity";
    private SignatureView signature_view;

    @Override
    public int getTitleResId() {
        return R.string.ui_signature;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_signature;
    }

    @Override
    public void initView() {
        signature_view = findViewById(R.id.signature_view);
        Button btn_clear_signature = findViewById(R.id.btn_clear_signature);
        Button btn_complete_signature = findViewById(R.id.btn_complete_signature);
        btn_clear_signature.setOnClickListener(this);
        btn_complete_signature.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_clear_signature) {
            if (!signature_view.isSigned()) return;
            signature_view.clear();
        } else if (id == R.id.btn_complete_signature) {
            if (signature_view.isSigned()) {
                Bitmap bitmap = signature_view.getBitmap();
                String bmInfo = "bitmap info >>> "
                        + "[ByteCount = " + bitmap.getByteCount()
                        + ", AllocationByteCount = " + bitmap.getAllocationByteCount()
                        + ", density = " + bitmap.getDensity()
                        + ", w = " + bitmap.getWidth()
                        + ", h = " + bitmap.getHeight() + "]";
                ToastUtils.showShort(bmInfo);
            } else {
                ToastUtils.showShort("未签名");
            }
        }
    }
}
