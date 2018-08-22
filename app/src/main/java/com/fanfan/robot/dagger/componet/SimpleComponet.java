package com.fanfan.robot.dagger.componet;

import com.fanfan.robot.dagger.module.SimpleModule;
import com.fanfan.robot.ui.face.act.AuthenticationActivity;

import dagger.Component;

/**
 * Created by Administrator on 2018/3/8/008.
 */

@Component(modules = {SimpleModule.class})
public interface SimpleComponet {

    void inject(AuthenticationActivity activity);

}
