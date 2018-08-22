package com.fanfan.robot.dagger.module;

import com.fanfan.robot.presenter.ChatPresenter;
import com.fanfan.robot.presenter.ipersenter.IChatPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2018/3/8/008.
 */

@Module
public class ChatModel {

    IChatPresenter.IChatView baseView;


    public ChatModel(IChatPresenter.IChatView baseView) {
        this.baseView = baseView;
    }

    @Provides
    public ChatPresenter providerChat() {
        return new ChatPresenter(baseView);
    }
}
