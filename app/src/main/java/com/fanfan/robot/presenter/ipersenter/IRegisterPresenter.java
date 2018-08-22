package com.fanfan.robot.presenter.ipersenter;

import com.fanfan.robot.app.common.presenter.BasePresenter;
import com.fanfan.robot.app.common.presenter.BaseView;
import com.fanfan.robot.model.UserInfo;

/**
 * Created by android on 2017/12/25.
 */

public abstract class IRegisterPresenter implements BasePresenter {

    private IRegisterView mBaseView;

    public IRegisterPresenter(IRegisterView baseView) {
        mBaseView = baseView;
    }

    public abstract void doRegister(UserInfo info);

    public interface IRegisterView extends BaseView {


        void registerSuccess();

        void registerFail(String errMsg);
    }

}
