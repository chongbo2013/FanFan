package com.fanfan.robot.db.manager;

import com.fanfan.robot.db.base.BaseManager;
import com.fanfan.robot.model.ChatMessageBean;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by Mao Jiqing on 2016/10/15.
 */

public class ChatDbManager extends BaseManager<ChatMessageBean, Long> {
    @Override
    public AbstractDao<ChatMessageBean, Long> getAbstractDao() {
        return daoSession.getChatMessageBeanDao();
    }
}
