package com.fanfan.novel.im.init;

import com.tencent.TIMUserStatusListener;

import java.util.LinkedList;

public class StatusObservable implements TIMUserStatusListener {

    // 消息监听链表
    private LinkedList<TIMUserStatusListener> listObservers = new LinkedList<>();
    // 句柄
    private static StatusObservable instance;


    public static StatusObservable getInstance() {
        if (null == instance) {
            synchronized (StatusObservable.class) {
                if (null == instance) {
                    instance = new StatusObservable();
                }
            }
        }
        return instance;
    }


    // 添加观察者
    public void addObserver(TIMUserStatusListener listener) {
        if (!listObservers.contains(listener)) {
            listObservers.add(listener);
        }
    }

    // 移除观察者
    public void deleteObserver(TIMUserStatusListener listener) {
        listObservers.remove(listener);
    }

    @Override
    public void onForceOffline() {
        // 拷贝链表
        LinkedList<TIMUserStatusListener> tmpList = new LinkedList<>(listObservers);
        for (TIMUserStatusListener listener : tmpList) {
            listener.onForceOffline();
        }
    }

    @Override
    public void onUserSigExpired() {

    }
}
