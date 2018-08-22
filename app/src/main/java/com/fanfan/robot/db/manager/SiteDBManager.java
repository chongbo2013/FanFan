package com.fanfan.robot.db.manager;

import com.fanfan.robot.db.SiteBeanDao;
import com.fanfan.robot.db.base.BaseManager;
import com.fanfan.robot.model.SiteBean;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by android on 2018/2/23.
 */

public class SiteDBManager extends BaseManager<SiteBean, Long> {

    @Override
    public AbstractDao<SiteBean, Long> getAbstractDao() {
        return daoSession.getSiteBeanDao();
    }


    public List<SiteBean> querySiteByName(String name) {
        Query<SiteBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(SiteBeanDao.Properties.Name.eq(name))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }


    public List<SiteBean> queryLikeSiteByName(String name) {
        Query<SiteBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(SiteBeanDao.Properties.Name.like("%" + name + "%"))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }
}
