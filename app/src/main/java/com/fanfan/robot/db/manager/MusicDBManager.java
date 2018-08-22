package com.fanfan.robot.db.manager;

import com.fanfan.robot.db.base.BaseManager;
import com.fanfan.robot.model.Music;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by android on 2018/1/13.
 */

public class MusicDBManager extends BaseManager<Music, Long> {
    @Override
    public AbstractDao<Music, Long> getAbstractDao() {
        return daoSession.getMusicDao();
    }
}
