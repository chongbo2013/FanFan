package com.fanfan.novel.im.event;

import com.seabreeze.log.Print;
import com.tencent.TIMFriendGroup;
import com.tencent.TIMFriendshipProxyListener;
import com.tencent.TIMFriendshipProxyStatus;
import com.tencent.TIMManager;
import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMUserProfile;

import java.util.List;
import java.util.Observable;

/**
 * Created by zhangyuanyuan on 2017/9/26.
 */

public class FriendshipEvent extends Observable implements TIMFriendshipProxyListener {

    private volatile static FriendshipEvent instance;

    private FriendshipEvent() {
    }

    public static FriendshipEvent getInstance() {
        if (instance == null) {
            synchronized (FriendshipEvent.class) {
                if (instance == null) {
                    instance = new FriendshipEvent();
                }
            }
        }
        return instance;
    }

    public void init() {
        TIMManager.getInstance().setFriendshipProxyListener(this);
    }

//    public TIMUserConfig init(TIMUserConfig userConfig) {
//        TIMFriendshipSettings settings = new TIMFriendshipSettings();
//        userConfig.setFriendshipSettings(settings);
//        TIMUserConfigSnsExt userConfigSnsExt = new TIMUserConfigSnsExt(userConfig);
//        //开启资料关系链本地存储
//        userConfigSnsExt.enableFriendshipStorage(true)
//                //设置关系链变更事件监听器
//                .setFriendshipProxyListener(this);
//        return userConfigSnsExt;
//    }

    @Override
    public void OnProxyStatusChange(TIMFriendshipProxyStatus timFriendshipProxyStatus) {
        Print.e("OnProxyStatusChange");
    }

    @Override
    public void OnAddFriends(List<TIMUserProfile> list) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.ADD, list));
    }

    @Override
    public void OnDelFriends(List<String> list) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.DEL, list));
    }

    @Override
    public void OnFriendProfileUpdate(List<TIMUserProfile> list) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.PROFILE_UPDATE, null));
    }

    @Override
    public void OnAddFriendReqs(List<TIMSNSChangeInfo> list) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.ADD_REQ, list));
    }

    @Override
    public void OnAddFriendGroups(List<TIMFriendGroup> list) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.GROUP_UPDATE, list));
    }

    @Override
    public void OnDelFriendGroups(List<String> list) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.GROUP_UPDATE, list));
    }

    @Override
    public void OnFriendGroupUpdate(List<TIMFriendGroup> list) {

    }

    /**
     * 好友关系链消息已读通知
     */
    public void OnFriendshipMessageRead() {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.READ_MSG, null));
    }

    /**
     * 好友分组变更通知
     */
    public void OnFriendGroupChange() {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.PROFILE_UPDATE, null));
    }


    /**
     * 通知上层用的数据
     */
    public class NotifyCmd {
        public final NotifyType type;
        public final Object data;

        NotifyCmd(NotifyType type, Object data) {
            this.type = type;
            this.data = data;
        }

    }

    public enum NotifyType {
        REFRESH,//刷新数据
        ADD_REQ,//请求添加
        READ_MSG,//关系链通知已读
        ADD,//添加好友
        DEL,//删除好友
        PROFILE_UPDATE,//变更好友资料
        GROUP_UPDATE,//分组变更
    }

}
