package com.fanfan.robot.presenter.ipersenter;

import com.fanfan.robot.app.common.presenter.BasePresenter;
import com.fanfan.robot.app.common.presenter.BaseView;

/**
 * Created by android on 2018/1/3.
 */

public abstract class ISynthesizerPresenter implements BasePresenter {

    private ITtsView mBaseView;

    public ISynthesizerPresenter(ITtsView baseView) {
        mBaseView = baseView;
    }

    public abstract void initTts();

    public abstract void buildTts();

    public abstract void stopTts();

    public abstract void doAnswer(String answer);

    public abstract void stopHandler();

    public abstract void stopAll(String wakeUp);

    public abstract boolean isSpeaking();

    public interface ITtsView extends BaseView {

        void onSpeakBegin(String s);

        void onRunable();

        void stopSound();
    }
}
