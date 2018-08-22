package com.fanfan.robot.presenter;

import com.fanfan.novel.im.MyTLSService;
import com.fanfan.robot.model.UserInfo;
import com.fanfan.robot.presenter.ipersenter.IForgetPresenter;
import com.seabreeze.log.Print;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by android on 2017/12/26.
 */

public class ForgetPresenter extends IForgetPresenter implements TLSPwdResetListener {

    private IForgetView mForgetView;

    public ForgetPresenter(IForgetView baseView) {
        super(baseView);
        mForgetView = baseView;
    }

    @Override
    public void doModify(UserInfo info) {
        MyTLSService.getInstance().TLSPwdResetCommit(info.getUserPass(), this);
    }

    @Override
    public void OnPwdResetAskCodeSuccess(int i, int i1) {
        Print.e("OnPwdResetAskCodeSuccess");
    }

    @Override
    public void OnPwdResetReaskCodeSuccess(int i, int i1) {
        Print.e("OnPwdResetReaskCodeSuccess");
    }

    @Override
    public void OnPwdResetVerifyCodeSuccess() {
        Print.e("OnPwdResetVerifyCodeSuccess");
    }

    @Override
    public void OnPwdResetCommitSuccess(TLSUserInfo tlsUserInfo) {
        Print.e("OnPwdResetCommitSuccess");
    }

    @Override
    public void OnPwdResetFail(TLSErrInfo tlsErrInfo) {
        Print.e(String.format("%s: %s", tlsErrInfo.ErrCode == TLSErrInfo.TIMEOUT ? "网络超时" : "错误", tlsErrInfo.Msg));
        mForgetView.modifyFail(tlsErrInfo.Msg);
    }

    @Override
    public void OnPwdResetTimeout(TLSErrInfo tlsErrInfo) {
        Print.e(String.format("%s: %s", tlsErrInfo.ErrCode == TLSErrInfo.TIMEOUT ? "网络超时" : "错误", tlsErrInfo.Msg));
        mForgetView.modifyFail(tlsErrInfo.Msg);
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }
}
