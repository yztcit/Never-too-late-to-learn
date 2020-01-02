package com.chen.coolandroid.learnhttp;

import android.view.View;

import com.chen.coolandroid.R;
import com.chen.coolandroid.activity.BaseHeadActivity;
import com.chen.coolandroid.tool.LogUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 编号5：网络访问
 */
public class HttpActivity extends BaseHeadActivity {

    @Override
    public int getTitleResId() {
        return R.string.learn_http;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_http;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    //url host must not be null!
    private final String url = "http://www.baidu.com";
    private final String url_taobao = "http://ip.taobao.com/service/getIpInfo.php";
    public void httpClientTest(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                useHttpClientGet(url);
                useHttpClientPost(url_taobao);
            }
        }).start();
    }

    private void useHttpClientPost(String url) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Connection", "Keep-Alive");

        try {
            HttpClient httpClient = createHttpClient();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("ip", "59.108.54.37"));

            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (null != httpEntity) {
                InputStream stream = httpEntity.getContent();
                String response = convertInputStream2String(stream);
                stream.close();
                LogUtil.d("httpClientPostTest", "statusCode = " + statusCode + "\n" + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * for android 6.0 remove HttpClient library from sdk
     * so config some params in module gradle
     * <code>
     *     useLibrary('org.apache.http.legacy')
     * </code>
     * @param url test url
     */
    private void useHttpClientGet(String url){
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("connection", "keep-alive");
        try {
            HttpClient httpClient = createHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (null != httpEntity) {
                InputStream stream = httpEntity.getContent();
                String response = convertInputStream2String(stream);
                stream.close();
                LogUtil.d("httpClientGetTest", "statusCode = " + statusCode + "\n" + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertInputStream2String(InputStream inputStream) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        return buffer.toString();
    }

    private HttpClient createHttpClient(){
        HttpParams defaultHttpParams = new BasicHttpParams();
        //set connect timeout
        HttpConnectionParams.setConnectionTimeout(defaultHttpParams, 15000);
        //set request timeout
        HttpConnectionParams.setSoTimeout(defaultHttpParams, 15000);
        HttpConnectionParams.setTcpNoDelay(defaultHttpParams, true);
        HttpProtocolParams.setVersion(defaultHttpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(defaultHttpParams, HTTP.UTF_8);
        //keep hand shake
        HttpProtocolParams.setUseExpectContinue(defaultHttpParams, true);
        return new DefaultHttpClient(defaultHttpParams);
    }
}
