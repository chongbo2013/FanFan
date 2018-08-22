package com.fanfan.robot.dagger.componet;

import com.fanfan.robot.dagger.module.MainModule;
import com.fanfan.robot.ui.main.MainActivity;

import dagger.Component;

/**
 * Created by Administrator on 2018/3/8/008.
 */

@Component(modules = {MainModule.class})
public interface MainComponet {

    void inject(MainActivity activity);

//    void inject(MainNewActivity activity);
}
