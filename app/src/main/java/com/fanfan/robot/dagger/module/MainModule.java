package com.fanfan.robot.dagger.module;

import com.fanfan.robot.app.common.presenter.BaseView;
import com.fanfan.robot.presenter.ChatPresenter;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.IChatPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ISynthesizerPresenter;
import com.fanfan.robot.presenter.LineSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ILineSoundPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/3/8/008.
 */

@Module
public class MainModule {

    BaseView baseView;

    public MainModule(BaseView baseView) {
        this.baseView = baseView;
    }

    @Provides
    public ChatPresenter providerChat() {
        return new ChatPresenter((IChatPresenter.IChatView) baseView);
    }

    @Provides
    public LineSoundPresenter providerLineSound() {
        return new LineSoundPresenter((ILineSoundPresenter.ILineSoundView) baseView);
    }

    @Provides
    public SerialPresenter providerSerial() {
        return new SerialPresenter((ISerialPresenter.ISerialView) baseView);
    }

//    @Provides
//    public SynthesizerPresenter providerSynthesizer() {
//        return new SynthesizerPresenter((ISynthesizerPresenter.ITtsView) baseView);
//    }

//    @Provides
//    public MainManager provideMainManager(ChatPresenter chatPresenter, SerialPresenter serialPresenter,
//                                          SynthesizerPresenter synthesizerPresenter,
//                                          LineSoundPresenter lineSoundPresenter) {
//        return new MainManager(chatPresenter, serialPresenter, synthesizerPresenter, lineSoundPresenter);
//    }

}
