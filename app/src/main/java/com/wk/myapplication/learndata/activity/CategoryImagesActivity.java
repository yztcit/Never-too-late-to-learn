package com.wk.myapplication.learndata.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wk.myapplication.R;


/**
 * home_tab-category,gv_item-images
 */
public class CategoryImagesActivity extends AppCompatActivity {
    private static final String TAG = "CategoryImagesActivity";
    private Context mContext;

    //start this activity by context;
    //others params which be used in this class can be defined in actionStart(params);
    public static void actionStart(Context context){
        Intent intent = new Intent(context,CategoryImagesActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_images);
        mContext = CategoryImagesActivity.this;
    }
}
