package com.fanfan.robot.db.manager;

import com.fanfan.robot.db.CheckInDao;
import com.fanfan.robot.db.base.BaseManager;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.model.CheckIn;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by android on 2018/1/10.
 */

public class CheckInDBManager extends BaseManager<CheckIn, Long> {
    @Override
    public AbstractDao<CheckIn, Long> getAbstractDao() {
        return daoSession.getCheckInDao();
    }

    public List<CheckIn> queryByName(String name) {
        Query<CheckIn> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(CheckInDao.Properties.Name.eq(name))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return build.list();
    }

    public List<CheckIn> queryByToday() {

        Query<CheckIn> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(CheckInDao.Properties.Time.between(TimeUtils.getTimesmorning(), TimeUtils.getTimesnight()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return build.list();
    }

}
