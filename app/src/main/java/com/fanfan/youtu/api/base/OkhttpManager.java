package com.fanfan.youtu.api.base;

import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.youtu.token.YoutuSign;
import com.fanfan.youtu.utils.OkHttpUtils;
import com.seabreeze.log.Print;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkhttpManager {


    private final Map<String, HttpUrl> mDomainNameHub;

    private OkHttpClient mClient;

    private OkhttpManager() {
        mDomainNameHub = new HashMap<>();
    }

    private volatile static OkhttpManager mOkhttpManager;

    public static OkhttpManager getInstance() {
        if (null == mOkhttpManager) {
            synchronized (OkhttpManager.class) {
                if (null == mOkhttpManager) {
                    mOkhttpManager = new OkhttpManager();
                }
            }
        }
        return mOkhttpManager;
    }

    public void init() {
        putDomain(Constant.YOUTU_NAME, Constant.API_YOUTU_BASE);
        putDomain(Constant.ROBOT_NAME, Constant.API_ROBOT_BASE);

        //创建keyManagers
        KeyManager[] keyManagers = OkHttpUtils.prepareKeyManager(null, null);

        //创建TrustManager
        TrustManager[] trustManagers = OkHttpUtils.prepareTrustManager();

        //创建X509TrustManager
        X509TrustManager manager = new SafeTrustManager();

        SSLContext sslContext = null;
        try {
            // 创建TLS类型的SSLContext对象， that uses our TrustManager
            sslContext = SSLContext.getInstance("TLS");
            // 用上面得到的trustManagers初始化SSLContext，这样sslContext就会信任keyStore中的证书
            // 第一个参数是授权的密钥管理器，用来授权验证，比如授权自签名的证书验证。第二个是被授权的证书管理器，用来验证服务器端的证书
            sslContext.init(keyManagers, new TrustManager[]{manager}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        // 设置 Log 拦截器，可以用于以后处理一些异常情况
        HttpLoggingInterceptor.Logger logger = new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Print.e(message);
//                try {
//                    String text = URLDecoder.decode(message, "utf-8");
//                    Print.e(text);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    Print.e(message);
//                }
            }
        };
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(logger);
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        File cacheFile = new File(Constants.PRINT_TIMLOG_PATH, "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        // 为所有请求自动添加 token
        Interceptor mTokenInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                // 如果当前没有缓存 token 或者请求已经附带 token 了，就不再添加
                String accessToken = YoutuSign.getSingleInstance().getAccessToken();
                Request authorised = originalRequest.newBuilder()
                        .header("Authorization", accessToken)
                        .build();
                return chain.proceed(authorised);
            }
        };

        // 自动刷新 token
        Authenticator mAuthenticator = new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) {
                String accessToken = YoutuSign.getSingleInstance().getAccessToken();
                return response.request().newBuilder()
                        .header("Authorization", accessToken)
                        .build();
            }
        };

        Interceptor urlInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                HttpUrl oldUrl = request.url();
                String url = request.url().toString();
                Request.Builder newBuilder = request.newBuilder();

                List<String> headers = request.headers(Constant.H_NAME);

                if (headers != null && headers.size() > 0) {
                    newBuilder.removeHeader(Constant.H_NAME);
                    String domainName = headers.get(0);

                    if (!TextUtils.isEmpty(domainName)) {
                        HttpUrl httpUrl = null;
                        httpUrl = fetchDomain(domainName);

                        HttpUrl newUrl = parseUrl(httpUrl, request.url());
                        //http://47.104.142.138/youtu/robot/UpdateProgram.php?type=1
                        Request newRequest = newBuilder
                                .url(newUrl)
                                .build();
                        return chain.proceed(newRequest);
                    }

                    return chain.proceed(request);
                }
                return chain.proceed(request);

            }
        };
        // 配置 client
        mClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)                // 设置拦截器
                .addInterceptor(urlInterceptor)                // 设置拦截器
                .retryOnConnectionFailure(true)             // 是否重试
                .connectTimeout(5, TimeUnit.SECONDS)        // 连接超时事件
                .readTimeout(5, TimeUnit.SECONDS)           // 读取超时时间
                .writeTimeout(5, TimeUnit.SECONDS)
                .addNetworkInterceptor(mTokenInterceptor)   // 自动附加 token
                .addNetworkInterceptor(new StethoInterceptor())
//                .authenticator(mAuthenticator)              // 认证失败自动刷新token
                .sslSocketFactory(sslContext.getSocketFactory(), manager)
                .hostnameVerifier(new SafeHostnameVerifier())
                .cache(cache)
                .build();
    }

    public OkHttpClient getOkhttpClient() {
        if (mClient == null) {
            init();
        }
        return mClient;
    }

    private void putDomain(String domainName, String domainUrl) {
        synchronized (mDomainNameHub) {
            HttpUrl parseUrl = HttpUrl.parse(domainUrl);
            if (parseUrl == null) {
                throw new RuntimeException(domainUrl);
            }
            mDomainNameHub.put(domainName, parseUrl);
        }
    }

    private HttpUrl fetchDomain(String domainName) {
        return mDomainNameHub.get(domainName);
    }

    private HttpUrl parseUrl(HttpUrl domainUrl, HttpUrl url) {

        // 如果 HttpUrl.parse(url); 解析为 null 说明,url 格式不正确,正确的格式为 "https://github.com:443"
        // http 默认端口 80,https 默认端口 443 ,如果端口号是默认端口号就可以将 ":443" 去掉
        // 只支持 http 和 https

        if (null == domainUrl) return url;

        return url.newBuilder()
                .scheme(domainUrl.scheme())
                .host(domainUrl.host())
                .port(domainUrl.port())
                .build();
    }

}
