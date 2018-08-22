package com.fanfan.novel.im.init;

public class TLSConfiguration {

    // TLS SDK
    public static long SDK_APPID;
    public static int ACCOUNT_TYPE;
    public static String APP_VERSION = "1.0";     // 指的是TLSDemo的版本号，而不是TLSSDK的版本号
    public static int TIMEOUT = 8000;
    public static int LANGUAGE_CODE = 2052;

    public static String COUNTRY_CODE = "86";


    public static void setSdkAppid(long sdkAppid) {
        SDK_APPID = sdkAppid;
    }


    public static void setAccountType(int accountType) {
        ACCOUNT_TYPE = accountType;
    }

    public static void setAppVersion(String appVersion) {
        APP_VERSION = appVersion;
    }

    public static void setTimeout(int timeout) {
        TIMEOUT = timeout;
    }

}
