package com.fanfan.novel.im.event;

import com.tencent.TIMConversation;
import com.tencent.TIMManager;
import com.tencent.TIMRefreshListener;

import java.util.List;
import java.util.Observable;

/**
 * Created by zhangyuanyuan on 2017/9/26.
 * IMSDK提供的刷新和被动更新的通知,登录前注册
 */

public class RefreshEvent extends Observable implements TIMRefreshListener {

    private volatile static RefreshEvent instance;

    private RefreshEvent() {
    }

    public static RefreshEvent getInstance() {
        if (instance == null) {
            synchronized (RefreshEvent.class) {
                if (instance == null) {
                    instance = new RefreshEvent();
                }
            }
        }
        return instance;
    }

    public void init() {
        TIMManager.getInstance().setRefreshListener(this);
    }


    @Override
    public void onRefresh() {
        setChanged();
        notifyObservers();
    }

    @Override
    public void onRefreshConversation(List<TIMConversation> list) {
        setChanged();
        notifyObservers();
    }
}
