package com.nttn.coolandroid.learncomponent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nttn.coolandroid.R;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView desc;
    private Button secondButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();
    }

    private void initView() {
        desc = findViewById(R.id.desc_second);
        desc.setText(this.toString());

        secondButton = findViewById(R.id.btn_second);
        secondButton.setOnClickListener(this);
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
