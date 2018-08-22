package com.fanfan.robot.other.cache;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.fanfan.novel.utils.ACache;
import com.fanfan.novel.utils.system.FileUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 2017/12/18.
 */

public class DataCache {

    private static int M = 1024 * 1024;
    ACache mDiskCache;
    LruCache<String, Object> mLruCache;

    public DataCache(Context context) {
        mDiskCache = ACache.get(new File(FileUtil.getExternalCacheDir(context.getApplicationContext(), "diy-data")));
        mLruCache = new LruCache<>(5 * M);
    }

    public <T extends Serializable> void saveListData(String key, List<T> data) {
        ArrayList<T> datas = (ArrayList<T>) data;
        mLruCache.put(key, datas);
        mDiskCache.put(key, datas, ACache.TIME_WEEK);     // 数据缓存时间为 1 周
    }

    public <T extends Serializable> void saveData(@NonNull String key, @NonNull T data) {
        mLruCache.put(key, data);
        mDiskCache.put(key, data, ACache.TIME_WEEK);     // 数据缓存时间为 1 周
    }

    public <T extends Serializable> T getData(@NonNull String key) {
        T result = (T) mLruCache.get(key);
        if (result == null) {
            result = (T) mDiskCache.getAsObject(key);
            if (result != null) {
                mLruCache.put(key, result);
            }
        }
        return result;
    }

    public void removeDate(String key) {
        mDiskCache.remove(key);
    }


    public List<Object> getRoomInfoListObj() {
        return getData("room_list_obj_");
    }

    public void saveRoomInfoListObj(List<Object> datas) {
        ArrayList<Object> rooms = new ArrayList<>(datas);
        saveData("room_list_obj_", rooms);
    }
}
