package com.chen.coolandroid.learndata.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chen.coolandroid.R;


/**
 * home_tab-category,gv_item-documents
 */
public class CategoryDocumentsActivity extends AppCompatActivity {
    private static final String TAG = "CategoryDocumentsActivity";
    private Context mContext;

    //start this activity by context;
    //other params which be used in this class can be defined in actionStart(params);
    public static void actionStart(Context context){
        Intent intent = new Intent(context,CategoryDocumentsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_documents);
        mContext = CategoryDocumentsActivity.this;
    }
}
