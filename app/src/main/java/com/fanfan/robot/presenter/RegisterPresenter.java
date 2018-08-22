package com.fanfan.robot.presenter;

import com.fanfan.novel.im.MyTLSService;
import com.fanfan.robot.model.UserInfo;
import com.fanfan.robot.presenter.ipersenter.IRegisterPresenter;
import com.seabreeze.log.Print;
import com.tencent.ilivesdk.ILiveCallBack;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by android on 2017/12/25.
 */

public class RegisterPresenter extends IRegisterPresenter implements TLSStrAccRegListener, ILiveCallBack {

    private IRegisterView mRegisterView;


    public RegisterPresenter(IRegisterView baseView) {
        super(baseView);
        mRegisterView = baseView;

    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void doRegister(UserInfo info) {
        int result = MyTLSService.getInstance().TLSStrAccReg(info.getIdentifier(), info.getUserPass(), this);
        if (result == TLSErrInfo.INPUT_INVALID) {
            Print.e("引导用户输入合法的用户名和密码");
        }
    }


    @Override
    public void OnStrAccRegSuccess(TLSUserInfo tlsUserInfo) {
        Print.i("第一步成功注册 " + tlsUserInfo.identifier);
//        String id = MyTLSService.getInstance().getLastUserIdentifier();
//        ILiveLoginManager.getInstance().tlsRegister(UserInfo.getInstance().getIdentifier(), UserInfo.getInstance().getUserPass(), this);
        mRegisterView.registerSuccess();
    }

    @Override
    public void OnStrAccRegFail(TLSErrInfo tlsErrInfo) {
        Print.e(String.format("%s: %s", tlsErrInfo.ErrCode == TLSErrInfo.TIMEOUT ? "网络超时" : "错误", tlsErrInfo.Msg));
        mRegisterView.registerFail(tlsErrInfo.Msg);
    }

    @Override
    public void OnStrAccRegTimeout(TLSErrInfo tlsErrInfo) {
        Print.e(String.format("%s: %s", tlsErrInfo.ErrCode == TLSErrInfo.TIMEOUT ? "网络超时" : "错误", tlsErrInfo.Msg));
        mRegisterView.registerFail(tlsErrInfo.Msg);
    }

    //
    @Override
    public void onSuccess(Object data) {
        mRegisterView.registerSuccess();
    }

    @Override
    public void onError(String module, int errCode, String errMsg) {
        Print.e("module : " + module + " , errcode : " + errCode + " , errMsg : " + errMsg);
        mRegisterView.registerFail(errMsg);
    }
}
