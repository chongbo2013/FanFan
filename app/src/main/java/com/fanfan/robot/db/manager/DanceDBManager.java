package com.fanfan.robot.db.manager;

import com.fanfan.robot.db.DanceDao;
import com.fanfan.robot.db.base.BaseManager;
import com.fanfan.robot.model.Dance;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by android on 2018/1/11.
 */

public class DanceDBManager extends BaseManager<Dance, Long> {
    @Override
    public AbstractDao<Dance, Long> getAbstractDao() {
        return daoSession.getDanceDao();
    }

    public List<Dance> queryDanceByTitle(String title) {
        Query<Dance> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(DanceDao.Properties.Title.eq(title))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

}
