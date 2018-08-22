package com.fanfan.robot.db.manager;
import com.fanfan.robot.db.FaceAuthDao;
import com.fanfan.robot.db.base.BaseManager;
import com.fanfan.robot.model.FaceAuth;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by android on 2018/1/9.
 */

public class FaceAuthDBManager extends BaseManager<FaceAuth, Long> {
    @Override
    public AbstractDao<FaceAuth, Long> getAbstractDao() {
        return daoSession.getFaceAuthDao();
    }

    public FaceAuth queryByAuth(String auth) {
        Query<FaceAuth> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(FaceAuthDao.Properties.AuthId.eq(auth))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<FaceAuth> faceAuths = build.list();
        if (faceAuths != null) {
            if (faceAuths.size() == 1) {
                return faceAuths.get(faceAuths.size() - 1);
            } else {
                deleteList(faceAuths);
            }
        }
        return null;
    }

    public FaceAuth queryByPersonId(String personId) {
        Query<FaceAuth> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(FaceAuthDao.Properties.PersonId.eq(personId))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<FaceAuth> faceAuths = build.list();
        if (faceAuths != null) {
            if (faceAuths.size() == 1) {
                return faceAuths.get(faceAuths.size() - 1);
            } else {
                deleteList(faceAuths);
            }
        }
        return null;
    }

}
