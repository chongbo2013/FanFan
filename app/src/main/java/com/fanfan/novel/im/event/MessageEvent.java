package com.fanfan.novel.im.event;


import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;

import java.util.List;
import java.util.Observable;

/**
 * Created by zhangyuanyuan on 2017/9/26.
 * 消息通知事件，上层界面可以订阅此事件
 */

public class MessageEvent extends Observable implements TIMMessageListener {

    private volatile static MessageEvent instance;


    public static MessageEvent getInstance() {
        if (instance == null) {
            synchronized (MessageEvent.class) {
                if (instance == null) {
                    instance = new MessageEvent();
                }
            }
        }
        return instance;
    }


    private MessageEvent() {
        //添加一个消息监听器
        //默认情况下所有消息监听器都将按添加顺序被回调一次
        //除非用户在OnNewMessages回调中返回true，此时将不再继续回调下一个消息监听器
        TIMManager.getInstance().addMessageListener(this);

    }


//    public TIMUserConfig init(TIMUserConfig config) {
//        TIMUserConfigMsgExt userConfigMsgExt = new TIMUserConfigMsgExt(config);
//        //禁用消息存储
//        userConfigMsgExt.enableStorage(false)
//                //开启消息已读回执
//                .enableReadReceipt(true);
//        //设置消息撤回通知监听器
////        userConfigMsgExt.setMessageRevokedListener
//        return userConfigMsgExt;
//    }

    /**
     * 收到新消息回调
     *
     * @param list
     * @return
     */
    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        ////消息的内容解析请参考消息收发文档中的消息解析说明
        for (TIMMessage item : list) {
            setChanged();
            notifyObservers(item);
        }
        return false;
    }

    /**
     * 主动通知新消息
     */
    public void onNewMessage(TIMMessage message) {
        setChanged();
        notifyObservers(message);
    }

    /**
     * 清理消息监听
     */
    public void clear() {

        //删除一个消息监听器，消息监听器被删除后，将不再被调用。
        TIMManager.getInstance().removeMessageListener(this);
        instance = null;
    }

}
