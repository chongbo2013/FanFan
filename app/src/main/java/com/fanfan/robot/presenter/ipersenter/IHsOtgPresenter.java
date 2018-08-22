package com.fanfan.robot.presenter.ipersenter;

import com.fanfan.robot.app.common.presenter.BasePresenter;
import com.fanfan.robot.app.common.presenter.BaseView;
import com.fanfan.robot.model.PersonInfo;

/**
 * Created by android on 2018/1/10.
 */

public abstract class IHsOtgPresenter implements BasePresenter {

    private IHsOtgView mBaseView;

    public IHsOtgPresenter(IHsOtgView baseView) {
        mBaseView = baseView;
    }

    public abstract void onStart();

    public abstract void onStop();

    public abstract int init();

    public abstract int authenticate();

    public abstract int readCard();

    public abstract void identityRead(PersonInfo info);

    public abstract void authFail();

    public abstract void compareFail();

    public abstract String getFPcode(int FPcode);

    public interface IHsOtgView extends BaseView {

        void init(int code);

        void authenticate(int code);

        void readCard(int code);

        void identityFinish(PersonInfo info);

        void identityFail(String msg);
    }
}
