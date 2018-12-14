package com.chen.coolandroid.learnui.uibase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chen.coolandroid.R;

public class UIBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uibase);
    }
}
