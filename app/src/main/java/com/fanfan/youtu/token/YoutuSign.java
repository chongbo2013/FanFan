package com.fanfan.youtu.token;

import com.fanfan.youtu.utils.Base64Util;
import com.fanfan.youtu.utils.HMACSHA1;

import java.util.Random;

/**
 * Created by admin on 2017/8/25.
 */

public class YoutuSign {

    public static int EXPIRED_SECONDS = 2592000;

    public static String APP_ID = "10112770";
    public static String SECRET_ID = "AKIDx7e2JQURzfeT8cojHE09AA0uiL5iO9sR";
    public static String SECRET_KEY = "98WfgrLxn2P4bxT5npSBkTasqgBdGhCQ";

    private volatile static YoutuSign mYoutuSign;

    private static String accessToken;

    private YoutuSign() {
    }

    public static YoutuSign getSingleInstance() {
        if (null == mYoutuSign) {
            synchronized (YoutuSign.class) {
                if (null == mYoutuSign) {
                    mYoutuSign = new YoutuSign();
                }
            }
        }
        return mYoutuSign;
    }

    public static YoutuSign init() {

        appSign();
        return getSingleInstance();
    }

    public String getAccessToken() {
        return accessToken;
    }

    private static int appSign() {

        return appSignBase(APP_ID, SECRET_ID, SECRET_KEY, System.currentTimeMillis() / 1000 + EXPIRED_SECONDS, "3041722595");
    }

    private static int appSignBase(String appId, String secret_id, String secret_key, long expired, String userid) {

        if (empty(secret_id) || empty(secret_key)) {
            return -1;
        }

        String puserid = "";
        if (!empty(userid)) {
            if (userid.length() > 64) {
                return -2;
            }
            puserid = userid;
        }

        long now = System.currentTimeMillis() / 1000;
        int rdm = Math.abs(new Random().nextInt());
        String plain_text = "a=" + appId + "&k=" + secret_id + "&e=" + expired + "&t=" + now + "&r=" + rdm + "&u=" + puserid;

        byte[] bin = hashHmac(plain_text, secret_key);

        byte[] all = new byte[bin.length + plain_text.getBytes().length];
        System.arraycopy(bin, 0, all, 0, bin.length);
        System.arraycopy(plain_text.getBytes(), 0, all, bin.length, plain_text.getBytes().length);

        accessToken = Base64Util.encode(all);
        return 0;
    }

    private static byte[] hashHmac(String plain_text, String accessKey) {

        try {
            return HMACSHA1.getSignature(plain_text, accessKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean empty(String s) {
        return s == null || s.trim().equals("") || s.trim().equals("null");
    }
}
