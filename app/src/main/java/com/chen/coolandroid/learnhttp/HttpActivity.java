package com.chen.coolandroid.learnhttp;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.text.TextUtils;
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 编号5：网络访问
 */
public class HttpActivity extends BaseHeadActivity {
    private final int timeout = 15000;

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

    /**
     * HttpURLConnection 在 Android 2.2 及之前的版本，会存在一些 bug ：
     * 比如对一个可读的 InputStream 调用的 close() 方法，可能导致连接池失效。
     * 通常解决方法是判断版本，禁用连接池的功能。{@link #disableConnectionReuseIfNecessary()}
     * 所以，在2.2及之前的版本使用 HttpClient 是最好的选择；而在2.3及之后的版本 HttpURLConnection 是
     * 最好的选择，它的 API 简单，体积较小，因而非常适合Android项目。HttpURLConnection 的压缩和缓存
     * 机制可以有效的减少网络访问的流量，在提升速度、节省流量和电量方面起到了较大的作用。
     * 另外，Android 6.0 移除了 HttpClient，HttpURLConnection 成了唯一的选择。
     *
     * @param view button
     */
    public void httpUrlConnectionTest(View view) {
        disableConnectionReuseIfNecessary();
        new Thread(new Runnable() {
            @Override
            public void run() {
                useHttpURLConnectionGet(url);
                useHttpURLConnectionPost(url_taobao);
            }
        }).start();
    }

    private void useHttpURLConnectionGet(String url) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = createHttpURLConnection(url, GET);
        try {
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            int responseCode = httpURLConnection.getResponseCode();
            String response = convertInputStream2String(inputStream);
            LogUtil.d("httpURLConnectionGet", "responseCode = " + responseCode + "\n" + response);
            inputStream.close();
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void useHttpURLConnectionPost(String url){
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = createHttpURLConnection(url, POST);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("ip", testIp));
        try {
            httpURLConnection.connect();
            postParams(httpURLConnection.getOutputStream(), params);
            inputStream = httpURLConnection.getInputStream();
            int responseCode = httpURLConnection.getResponseCode();
            String response = convertInputStream2String(inputStream);
            LogUtil.d("httpURLConnectionPost", "responseCode = " + responseCode + "\n" + response);
            inputStream.close();
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postParams(OutputStream outputStream, List<NameValuePair> params) throws IOException{
        StringBuilder builder = new StringBuilder();
        for (NameValuePair n : params) {
            if (!TextUtils.isEmpty(builder)){
                builder.append("&");
            }
            builder.append(URLEncoder.encode(n.getName(), "UTF-8")).append("=");
            builder.append(URLEncoder.encode(n.getValue(), "UTF-8"));
        }
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        LogUtil.d("params", builder.toString());
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }

    private static final String POST = "POST";
    private static final String GET = "GET";
    @Target({ElementType.PARAMETER})
    @StringDef({GET, POST})
    @Retention(RetentionPolicy.CLASS)
    private @interface Method {}

    private HttpURLConnection createHttpURLConnection(String url, @NonNull @Method String method) {
        HttpURLConnection httpURLConnection = null;
        try {
            URL aUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) aUrl.openConnection();
            //set connect timeout
            httpURLConnection.setConnectTimeout(timeout);
            //set read timeout
            httpURLConnection.setReadTimeout(timeout);
            //set request method
            httpURLConnection.setRequestMethod(method);
            //add header
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //receive input stream
            httpURLConnection.setDoInput(true);
            //out stream open while send params(POST method)
            httpURLConnection.setDoOutput(TextUtils.equals(POST, method));
            //设置该HttpURLConnection实例是否自动执行重定向
            httpURLConnection.setInstanceFollowRedirects(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpURLConnection;
    }

    private void disableConnectionReuseIfNecessary() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
            Properties properties = new Properties();
            properties.setProperty("http.keepAlive", "false");
            System.setProperties(properties);
        }
    }

    //url host must not be null!
    private final String url = "https://www.baidu.com";
    private final String url_taobao = "http://ip.taobao.com/service/getIpInfo.php";
    private final String testIp = "59.108.54.37";

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
            params.add(new BasicNameValuePair("ip", testIp));

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
     *
     * @param url test url
     */
    private void useHttpClientGet(String url) {
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

    private String convertInputStream2String(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        return buffer.toString();
    }

    private HttpClient createHttpClient() {
        HttpParams defaultHttpParams = new BasicHttpParams();
        //set connect timeout
        HttpConnectionParams.setConnectionTimeout(defaultHttpParams, timeout);
        //set request timeout
        HttpConnectionParams.setSoTimeout(defaultHttpParams, timeout);
        HttpConnectionParams.setTcpNoDelay(defaultHttpParams, true);
        HttpProtocolParams.setVersion(defaultHttpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(defaultHttpParams, HTTP.UTF_8);
        //keep hand shake
        HttpProtocolParams.setUseExpectContinue(defaultHttpParams, true);
        return new DefaultHttpClient(defaultHttpParams);
    }
}
