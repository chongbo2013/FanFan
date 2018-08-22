package com.fanfan.robot.presenter.ipersenter;

/**
 * Created by android on 2018/2/26.
 */

public abstract class IScreenPresenter {

    private ISreenView mBaseView;

    public IScreenPresenter(ISreenView baseView) {
        mBaseView = baseView;
    }

    public abstract void startTipsTimer();

    public abstract void endTipsTimer();

    public abstract void resetTipsTimer();

    public interface ISreenView {

        void showTipsView();
    }
}
