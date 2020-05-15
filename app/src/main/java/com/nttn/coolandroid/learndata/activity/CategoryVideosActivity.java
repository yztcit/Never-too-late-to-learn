package com.nttn.coolandroid.learndata.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nttn.coolandroid.R;


/**
 * home_tab-category,gv_item-videos
 */
public class CategoryVideosActivity extends AppCompatActivity {
    private static final String TAG = "CategoryVideosActivity";
    private Context mContext;

    //start this activity by context;
    //other params which be used in this class can be defined in actionStart(params);
    public static void actionStart(Context context){
        Intent intent = new Intent(context,CategoryVideosActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_videos);
        mContext = CategoryVideosActivity.this;
    }
}
