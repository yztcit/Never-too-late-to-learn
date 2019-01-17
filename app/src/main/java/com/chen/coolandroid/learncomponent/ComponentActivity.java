package com.chen.coolandroid.learncomponent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chen.coolandroid.R;

public class ComponentActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView desc;
    private Button rootButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component);
        initView();
    }

    private void initView() {
        desc = findViewById(R.id.desc);
        desc.setText(this.toString());

        rootButton = findViewById(R.id.btn_root);
        rootButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_root:
                toSecondActivity();
                break;
        }
    }

    private void toSecondActivity() {
        /**
         * Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
         * 如果一个Intent中包含此属性，则它转向的那个Activity以及在那个Activity其上的所有Activity
         * 都会在task重置时被清除出task。
         * 当我们将一个后台的task重新回到前台时，
         * 系统会在特定情况下为这个动作附带一个FLAG_ACTIVITY_RESET_TASK_IF_NEEDED标记，
         * 意味着必要时重置task，这时FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET就会生效。
         * 经过测试发现:
         * 对于一个处于后台的应用，如果在主选单点击应用，
         * 这个动作中含有FLAG_ACTIVITY_RESET_TASK_IF_NEEDED标记;
         * 长按Home键，然后点击最近记录，这个动作不含FLAG_ACTIVITY_RESET_TASK_IF_NEEDED标记,
         * 所以前者会清除，后者不会
         ---------------------
         作者：liuhe688
         来源：CSDN
         原文：https://blog.csdn.net/liuhe688/article/details/6761337
         版权声明：本文为博主原创文章，转载请附上博文链接！
         */
        Intent intent = new Intent(ComponentActivity.this, SecondActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivity(intent);
    }
}
