package com.fanfan.robot.dagger.module;

import com.fanfan.robot.app.common.presenter.BaseView;
import com.fanfan.robot.presenter.LocalSoundPresenter;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ILocalSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/3/8/008.
 */

@Module
public class SimpleModule {

    BaseView baseView;

    public SimpleModule(BaseView baseView) {
        this.baseView = baseView;
    }

    @Provides
    public LocalSoundPresenter providerLocalSound() {
        return new LocalSoundPresenter((ILocalSoundPresenter.ILocalSoundView) baseView);
    }

    @Provides
    public SerialPresenter providerSerial() {
        return new SerialPresenter((ISerialPresenter.ISerialView) baseView);
    }

//    @Provides
//    public SimpleManager provideMainManager(LocalSoundPresenter localSoundPresenter, SerialPresenter serialPresenter) {
//        return new SimpleManager(localSoundPresenter, serialPresenter);
//    }

}
