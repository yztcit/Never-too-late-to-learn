package com.nttn.coolandroid.learncomponent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseHeadActivity;

/**1.四大组件学习*/
public class SecondActivity extends BaseHeadActivity implements View.OnClickListener{
    private TextView desc;
    private Button secondButton;

    @Override
    public int getTitleResId() {
        return R.string.learn_component;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_second;
    }

    @Override
    public void initView() {
        desc = findViewById(R.id.desc_second);
        desc.setText(this.toString());

        secondButton = findViewById(R.id.btn_second);
        secondButton.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_second:
                Intent intent = new Intent(this, ComponentActivity.class);
                startActivity(intent);
                break;
        }
    }
}
