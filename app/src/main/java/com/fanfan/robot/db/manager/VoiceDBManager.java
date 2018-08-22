package com.fanfan.robot.db.manager;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.fanfan.robot.db.VoiceBeanDao;
import com.fanfan.robot.db.base.BaseManager;
import com.fanfan.robot.model.VoiceBean;
import com.seabreeze.log.Print;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by android on 2017/12/20.
 */

public class VoiceDBManager extends BaseManager<VoiceBean, Long> {
    @Override
    public AbstractDao<VoiceBean, Long> getAbstractDao() {
        return daoSession.getVoiceBeanDao();
    }


    public List<VoiceBean> queryVoiceByQuestion(String question) {
        return getAbstractDao()
                .queryBuilder()
                .where(VoiceBeanDao.Properties.ShowTitle.eq(question))
                .build()
                .list();
    }


    public List<VoiceBean> queryWhereOr(String key1, String key2, String key3, String key4) {

        List<VoiceBean> list1 = loadBeanList(key1);
        List<VoiceBean> list2 = loadBeanList(key2);
        List<VoiceBean> list3 = loadBeanList(key3);
        List<VoiceBean> list4 = loadBeanList(key4);
        List<VoiceBean> listAll = new ArrayList<>();

        if (list1 != null) listAll.addAll(list1);
        if (list2 != null) listAll.addAll(list2);
        if (list3 != null) listAll.addAll(list3);
        if (list4 != null) listAll.addAll(list4);

        ArrayMap<VoiceBean, Integer> map = new ArrayMap<>();
        for (VoiceBean bean : listAll) {
            if (map.containsKey(bean)) {
                int count = map.get(bean);
                map.put(bean, count + 1);
            } else {
                map.put(bean, 1);
            }
        }

        if (map.size() == 0) {

            return null;
        }
        Collection<Integer> values = map.values();
        Integer[] objects = values.toArray(new Integer[values.size()]);
        Arrays.sort(objects);
        int max = objects[objects.length - 1];

        Print.e("max ： " + max);

        List<VoiceBean> resultBeans = new ArrayList<>();
        Set<Map.Entry<VoiceBean, Integer>> arrayList = map.entrySet();
        for (Map.Entry<VoiceBean, Integer> entry : arrayList) {
            if (entry.getValue() == max) {
                resultBeans.add(entry.getKey());
            }
        }
        return resultBeans;
    }

    private List<VoiceBean> loadBeanList(String key) {
        if (!TextUtils.isEmpty(key)) {
            WhereCondition condition = VoiceBeanDao.Properties.ShowTitle.like("%" + key + "%");
            return getAbstractDao().queryBuilder().where(condition).build().list();
        }
        return null;
    }


    public List<VoiceBean> queryWhereOr(String title) {
        return getAbstractDao()
                .queryBuilder()
                .where(VoiceBeanDao.Properties.ShowTitle.like("%" + title + "%"))
                .build()
                .list();
    }
}
