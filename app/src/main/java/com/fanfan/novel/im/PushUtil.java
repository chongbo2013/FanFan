package com.fanfan.novel.im;

import com.fanfan.novel.im.event.MessageEvent;
import com.tencent.TIMMessage;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by zhangyuanyuan on 2017/9/26.
 */

public class PushUtil implements Observer {

    private static PushUtil instance = new PushUtil();

    private PushUtil() {
        MessageEvent.getInstance().addObserver(this);
    }

    public static PushUtil getInstance() {
        return instance;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            if (data instanceof TIMMessage) {
                TIMMessage msg = (TIMMessage) data;
                if (msg != null) {
                    PushNotify(msg);
                }
            }
        }
    }

    private void PushNotify(TIMMessage msg) {
//        Print.e(msg);
    }

}
