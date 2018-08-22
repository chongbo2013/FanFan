package com.fanfan.novel.im.event;

import com.tencent.TIMGroupAssistantListener;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMManager;

import java.util.List;
import java.util.Observable;

/**
 * Created by zhangyuanyuan on 2017/9/26.
 */

public class GroupEvent extends Observable implements TIMGroupAssistantListener {

    private volatile static GroupEvent instance;

    private GroupEvent() {
    }

    public static GroupEvent getInstance() {
        if (instance == null) {
            synchronized (GroupEvent.class) {
                if (instance == null) {
                    instance = new GroupEvent();
                }
            }
        }
        return instance;
    }

    public void init() {
        TIMManager.getInstance().setGroupAssistantListener(this);
    }

//    public TIMUserConfig init(TIMUserConfig config){
//        TIMGroupSettings settings = new TIMGroupSettings();
//        config.setGroupSettings(settings);
//
//        //群组管理扩展用户配置
//        TIMUserConfigGroupExt userConfigGroupExt =  new TIMUserConfigGroupExt(config);
//        userConfigGroupExt.enableGroupStorage(true)
//                .setGroupAssistantListener(this);
//        return userConfigGroupExt;
//    }


    @Override
    public void onMemberJoin(String s, List<TIMGroupMemberInfo> list) {

    }

    @Override
    public void onMemberQuit(String s, List<String> list) {

    }

    @Override
    public void onMemberUpdate(String s, List<TIMGroupMemberInfo> list) {

    }

    @Override
    public void onGroupAdd(TIMGroupCacheInfo timGroupCacheInfo) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.ADD, timGroupCacheInfo));
    }

    @Override
    public void onGroupDelete(String s) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.DEL, s));
    }

    @Override
    public void onGroupUpdate(TIMGroupCacheInfo timGroupCacheInfo) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.UPDATE, timGroupCacheInfo));
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
        REFRESH,//刷新
        ADD,//添加群
        DEL,//删除群
        UPDATE,//更新群信息
    }

}
