package com.chen.coolandroid.learncomponent;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chen.coolandroid.R;
import com.chen.coolandroid.activity.BaseHeadActivity;
import com.chen.coolandroid.learncomponent.rxpractise.RxActivity;
import com.chen.coolandroid.learnothers.OthersActivity;
import com.chen.coolandroid.tool.LogUtil;
import com.chen.coolandroid.tool.result.ActResultRequest;

/**编号1.四大组件学习*/
public class ComponentActivity extends BaseHeadActivity implements View.OnClickListener{
    private static final String TAG = "ComponentActivity";
    private TextView desc;
    private Button rootButton;

    @Override
    public int getTitleResId() {
        return R.string.learn_component;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_component;
    }

    @Override
    public void initView() {
        desc = findViewById(R.id.desc);
        desc.setText(this.toString());

        rootButton = findViewById(R.id.btn_root);
        rootButton.setOnClickListener(this);

        findViewById(R.id.btn_rx_process).setOnClickListener(this);
        findViewById(R.id.btn_easy_request).setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_root:
                toSecondActivity();
                break;
            case R.id.btn_rx_process:
                //这里的 RxActivity 是在新的私有进程中运行的；
                //延伸：启动其他的应用方法
                //1)PackageManager通过应用的包名启动
                //2)ComponentName 启动其他应用的 exported=true 的activity
                Intent intent = new Intent(this, RxActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_easy_request:
                easyRequest();
                break;
        }
    }

    private void easyRequest() {
        Intent resultIntent = new Intent(this, OthersActivity.class);
        ActResultRequest.init().startForResult(this, resultIntent, 11,
                new ActResultRequest.Callback() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        if (resultCode == RESULT_OK) {
                            LogUtil.e(TAG, "resultData:" + data.getStringExtra("data"));
                        }
                    }
                });
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
