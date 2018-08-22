package com.fanfan.novel.im;

import android.content.Context;

import com.fanfan.novel.im.init.TLSConfiguration;

import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSGuestLoginListener;
import tencent.tls.platform.TLSLoginHelper;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by Administrator on 2017-06-21.
 */

public class MyTLSService {

    private static MyTLSService tlsService;

    private TLSLoginHelper loginHelper;
    private TLSAccountHelper accountHelper;

    private static int lastErrno = -1;

    private MyTLSService() {
    }

    public static MyTLSService getInstance() {
        if (tlsService == null) {
            tlsService = new MyTLSService();
        }
        return tlsService;
    }

    /**
     * 在onCreate中获取TLSLoginHelper单例并初始化:
     *
     * @param context: 关联的activity
     * @function: 初始化TLS SDK, 必须在使用TLS SDK相关服务之前调用
     */
    public void initTlsSdk(Context context) {
        loginHelper = TLSLoginHelper.getInstance().init(context.getApplicationContext(),
                TLSConfiguration.SDK_APPID, TLSConfiguration.ACCOUNT_TYPE, TLSConfiguration.APP_VERSION);
        loginHelper.setTimeOut(TLSConfiguration.TIMEOUT);
        loginHelper.setLocalId(TLSConfiguration.LANGUAGE_CODE);
        loginHelper.setTestHost("", true);

        accountHelper = TLSAccountHelper.getInstance().init(context.getApplicationContext(),
                TLSConfiguration.SDK_APPID, TLSConfiguration.ACCOUNT_TYPE, TLSConfiguration.APP_VERSION);
        accountHelper.setCountry(Integer.parseInt(TLSConfiguration.COUNTRY_CODE)); // 存储注册时所在国家，只须在初始化时调用一次
        accountHelper.setTimeOut(TLSConfiguration.TIMEOUT);
        accountHelper.setLocalId(TLSConfiguration.LANGUAGE_CODE);
        accountHelper.setTestHost("", true);
    }

    /**
     * 账号密码登陆
     *
     * @param identifier
     * @param password
     * @param listener
     * @return
     */
    public int TLSPwdLogin(String identifier, String password, TLSPwdLoginListener listener) {
        return loginHelper.TLSPwdLogin(identifier, password.getBytes(), listener);
    }

    /**
     * 注册新用户
     *
     * @param account
     * @param password
     * @param listener
     * @return
     */
    public int TLSStrAccReg(String account, String password, TLSStrAccRegListener listener) {
        return accountHelper.TLSStrAccReg(account, password, listener);
    }

    public int TLSPwdResetCommit(String password, TLSPwdResetListener listener) {
        return accountHelper.TLSPwdResetCommit(password, listener);
    }

    /**
     * 匿名登录
     */
    public int TLSGuestLogin(TLSGuestLoginListener listener) {

        return loginHelper.TLSGuestLogin(listener);
    }


    public void clearUserInfo(String identifier) {
        loginHelper.clearUserInfo(identifier);
        lastErrno = -1;
    }

    public boolean needLogin(String identifier) {
        if (identifier == null)
            return true;
        return loginHelper.needLogin(identifier);
    }


    public String getLastUserIdentifier() {
        TLSUserInfo userInfo = getLastUserInfo();
        if (userInfo != null)
            return userInfo.identifier;
        else
            return null;
    }

    public TLSUserInfo getLastUserInfo() {
        return loginHelper.getLastUserInfo();
    }


    public String getUserSig(String identify) {
        return loginHelper.getUserSig(identify);
    }

    public static void setLastErrno(int errno) {
        lastErrno = errno;
    }

    public static int getLastErrno() {
        return lastErrno;
    }


}
