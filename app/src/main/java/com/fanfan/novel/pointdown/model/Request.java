package com.fanfan.novel.pointdown.model;

import android.os.Build;
import android.text.TextUtils;

import com.fanfan.robot.app.NovelApp;
import com.fanfan.youtu.api.base.OkhttpManager;
import com.seabreeze.log.Print;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/31/031.
 */

public class Request implements Serializable {

    public static final String HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String HEAD_KEY_USER_AGENT = "User-Agent";
    public static final String HEAD_KEY_RANGE = "Range";

    protected String url;

    public LinkedHashMap<String, String> headersMap;

    public Request(String url) {
        this.url = url;
        headersMap = new LinkedHashMap<>();
        //
        getLanguage();
        //
        getAgent();
    }

    private void getAgent() {
        String webUserAgent = null;
        try {
            Class<?> sysResCls = Class.forName("com.android.internal.R$string");
            Field webUserAgentField = sysResCls.getDeclaredField("web_user_agent");
            Integer resId = (Integer) webUserAgentField.get(null);
            webUserAgent = NovelApp.getInstance().getString(resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(webUserAgent)) {
            webUserAgent = "okhttp-okgo/jeasonlzy";
        }

        Locale locale = Locale.getDefault();
        StringBuffer buffer = new StringBuffer();
        final String version = Build.VERSION.RELEASE;
        if (version.length() > 0) {
            buffer.append(version);
        } else {
            buffer.append("1.0");
        }
        buffer.append("; ");
        final String language = locale.getLanguage();
        if (language != null) {
            buffer.append(language.toLowerCase(locale));
            final String country = locale.getCountry();
            if (!TextUtils.isEmpty(country)) {
                buffer.append("-");
                buffer.append(country.toLowerCase(locale));
            }
        } else {
            buffer.append("en");
        }
        if ("REL".equals(Build.VERSION.CODENAME)) {
            final String model = Build.MODEL;
            if (model.length() > 0) {
                buffer.append("; ");
                buffer.append(model);
            }
        }
        final String id = Build.ID;
        if (id.length() > 0) {
            buffer.append(" Build/");
            buffer.append(id);
        }
        String userAgent = String.format(webUserAgent, buffer, "Mobile ");
        headersMap.put(HEAD_KEY_USER_AGENT, userAgent);
    }

    private void getLanguage() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        StringBuilder acceptLanguageBuilder = new StringBuilder(language);
        if (!TextUtils.isEmpty(country))
            acceptLanguageBuilder.append('-').append(country).append(',').append(language).append(";q=0.8");
        String acceptLanguage = acceptLanguageBuilder.toString();

        headersMap.put(HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);
    }

    public void setStartPosition(long startPosition) {
        headersMap.put(HEAD_KEY_RANGE, "bytes=" + startPosition + "-");
    }

    public Response execute() throws IOException {
        //
//        StringBuilder sb = new StringBuilder();
//        sb.append(url);
//        if (url.indexOf('&') > 0 || url.indexOf('?') > 0) {
//            sb.append("&");
//        } else {
//            sb.append("?");
//        }
//        for (Map.Entry<String, List<String>> urlParams : urlParamsMap.entrySet()) {
//            List<String> urlValues = urlParams.getValue();
//            for (String value : urlValues) {
//                //对参数进行 utf-8 编码,防止头信息传中文
//                String urlValue = URLEncoder.encode(value, "UTF-8");
//                sb.append(urlParams.getKey()).append("=").append(urlValue).append("&");
//            }
//        }
//        sb.deleteCharAt(sb.length() - 1);
//        url = sb.toString();

        //
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();

        Headers.Builder headerBuilder = new Headers.Builder();
        try {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                //对头信息进行 utf-8 编码,防止头信息传中文,这里暂时不编码,可能出现未知问题,如有需要自行编码
//                String headerValue = URLEncoder.encode(entry.getValue(), "UTF-8");
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
        }
        requestBuilder.headers(headerBuilder.build());

        okhttp3.Request request = requestBuilder.get().url(url).tag(url).build();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.SECONDS);       // 连接超时事件
        builder.readTimeout(5, TimeUnit.SECONDS);           // 读取超时时间
        builder.writeTimeout(5, TimeUnit.SECONDS);
//        return OkhttpManager.getInstance().getOkhttpClient().newCall(request).execute();
        return builder.build().newCall(request).execute();
    }

}
