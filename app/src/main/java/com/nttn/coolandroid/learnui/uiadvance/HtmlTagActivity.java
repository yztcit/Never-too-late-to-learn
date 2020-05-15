package com.nttn.coolandroid.learnui.uiadvance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.widget.TextView;

import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseHeadActivity;
import com.nttn.coolandroid.tool.DownloadUtils;

import java.util.HashMap;

/**
 * <pre>
 *     author : Apple
 *     e-mail : xxx@xx
 *     time   : 2019/10/28
 *     desc   : TextView 加载HTML标签
 *     version: 1.0
 * </pre>
 */
public class HtmlTagActivity extends BaseHeadActivity {
    private static final String TAG = "HtmlTagActivity";
    private TextView textView;
    private static final String CACHE = "cache";
    String htmlStr = "<p><img src=\"http://file.mxtang.net//uploads/20190708/FpnXhJB-jJttz3BA_GGJ_ksuCMU3.jpg\" data-filename=\"filename\" style=\"width: 470px;\"></p><p>李总lizong,,,。。123</p>";

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, HtmlTagActivity.class);
        context.startActivity(intent);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            textView.setText(Html.fromHtml(htmlStr, imgGetter, null));
        }
    };

    @Override
    public int getTitleResId() {
        return R.string.ui_html_tag;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_html_tag;
    }

    @Override
    public void initView() {
        textView = findViewById(R.id.tv_html_tag);
    }

    @Override
    public void initData() {
        textView.setText(Html.fromHtml(htmlStr, imgGetter, null));
    }

    private HashMap<String, Drawable> imgHashMap = new HashMap<>();
    Html.ImageGetter imgGetter = new Html.ImageGetter() {
        public Drawable getDrawable(final String source) {
            Drawable cacheDrawable = imgHashMap.get(source);
            try {
                if (cacheDrawable != null) {
                    return cacheDrawable;
                }
                DownloadUtils.getInstance(mContext)
                        .setDownloadCallback(new DownloadUtils.DownloadBitmapCallback<Bitmap>() {
                            @Override
                            public void onComplete(Bitmap data) {
                                Drawable drawable = new BitmapDrawable(data);
                                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                        drawable.getIntrinsicHeight());
                                imgHashMap.put(source, drawable);
                                mHandler.sendEmptyMessage(0);
                            }
                        })
                        .downloadFromUrl(source);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    };
}
