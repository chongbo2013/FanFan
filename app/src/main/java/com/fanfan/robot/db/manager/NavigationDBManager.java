package com.fanfan.robot.db.manager;

import com.fanfan.robot.db.NavigationBeanDao;
import com.fanfan.robot.db.base.BaseManager;
import com.fanfan.robot.model.NavigationBean;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by android on 2017/12/20.
 */

public class NavigationDBManager extends BaseManager<NavigationBean, Long> {
    @Override
    public AbstractDao<NavigationBean, Long> getAbstractDao() {
        return daoSession.getNavigationBeanDao();
    }

    public List<NavigationBean> queryNavigationByQuestion(String question) {
        Query<NavigationBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(NavigationBeanDao.Properties.Title.eq(question))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

    public List<NavigationBean> queryLikeNavigationByQuestion(String question) {
        Query<NavigationBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(NavigationBeanDao.Properties.Title.like("%" + question + "%"))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

    public NavigationBean queryTitle(String question) {
        Query<NavigationBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(NavigationBeanDao.Properties.Title.eq(question))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<NavigationBean> beans = build.list();
        if (beans != null) {
            if (beans.size() == 1) {
                return beans.get(beans.size() - 1);
            } else {
                deleteList(beans);
            }
        }
        return null;
    }

    public List<NavigationBean> queryOrder(String order) {
        return getAbstractDao().queryBuilder()
                .where(NavigationBeanDao.Properties.NavigationData.eq(order))
                .build()
                .list();
    }

}
