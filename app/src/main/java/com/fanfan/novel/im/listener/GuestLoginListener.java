package com.fanfan.novel.im.listener;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSGuestLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by zhangyuanyuan on 2017/9/26.
 */

public class GuestLoginListener implements TLSGuestLoginListener {


    @Override
    public void OnGuestLoginSuccess(TLSUserInfo tlsUserInfo) {

    }

    @Override
    public void OnGuestLoginFail(TLSErrInfo tlsErrInfo) {

    }

    @Override
    public void OnGuestLoginTimeout(TLSErrInfo tlsErrInfo) {

    }
}
