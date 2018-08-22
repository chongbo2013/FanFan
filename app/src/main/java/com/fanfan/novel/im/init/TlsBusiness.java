package com.fanfan.novel.im.init;

import android.content.Context;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.novel.im.MyTLSService;

/**
 * Created by Administrator on 2017-06-21.
 */

public class TlsBusiness {

    private TlsBusiness() {
    }

    public static void init(Context context) {
        TLSConfiguration.setSdkAppid(Constants.IMSDK_APPID);
        TLSConfiguration.setAccountType(Constants.IMSDK_ACCOUNT_TYPE);
        TLSConfiguration.setTimeout(8000);
        MyTLSService.getInstance().initTlsSdk(context);
    }

    public static void logout(String id) {
        MyTLSService.getInstance().clearUserInfo(id);
    }

    /**
     * 重新登录逻辑
     */
    public void reLogin() {


    }

}
