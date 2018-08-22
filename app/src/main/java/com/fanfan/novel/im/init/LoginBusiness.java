package com.fanfan.novel.im.init;

import com.fanfan.robot.app.common.Constants;
import com.seabreeze.log.Print;
import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;

/**
 * Created by Administrator on 2017-06-21.
 */

public class LoginBusiness {


    private static final String TAG = "LoginBusiness";

    private LoginBusiness() {
    }


    /**
     * 登录imsdk
     *
     * @param identify 用户id
     * @param userSig  用户签名
     * @param callBack 登录后回调
     */
    public static void loginIm(String identify, String userSig, TIMCallBack callBack) {

        if (identify == null || userSig == null) return;
        //发起登录请求
        TIMUser user = new TIMUser();
        user.setIdentifier(identify);
        TIMManager.getInstance().login(
                Constants.IMSDK_APPID,
                user,
                userSig,                    //用户帐号签名，由私钥加密获得，具体请参考文档
                callBack);
    }

    /**
     * 登出imsdk
     *
     * @param callBack 登出后回调
     */
    public static void logout(TIMCallBack callBack) {
        TIMManager.getInstance().logout(callBack);
    }

    /**
     * 这个方法仅供登录失败或者没有网络的情况下查看历史消息使用，如需要收发消息，请务必调用登录接口login。
     * 初始化本地存储，可以在无网络情况下加载本地会话和消息
     *
     * @param identifier
     * @param peer
     */
    public static void initStorage(String identifier, String peer) {
        TIMUser user = new TIMUser();
        user.setIdentifier(identifier);
        TIMManager.getInstance().getInstance().initStorage(Constants.IMSDK_APPID, user, peer, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                Print.e("initStorage failed, code: " + code + "|descr: " + desc);
            }

            @Override
            public void onSuccess() {
                Print.i("initStorage succ");
            }
        });

    }


}
