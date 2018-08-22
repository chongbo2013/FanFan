package com.fanfan.robot.dagger.module;

import com.fanfan.robot.presenter.LineSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ILineSoundPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/3/8/008.
 */

@Module
public class LineSoundModel {

    ILineSoundPresenter.ILineSoundView baseView;

    public LineSoundModel(ILineSoundPresenter.ILineSoundView baseView) {
        this.baseView = baseView;
    }

    @Provides
    public LineSoundPresenter providerLineSound() {
        return new LineSoundPresenter(baseView);
    }
}
