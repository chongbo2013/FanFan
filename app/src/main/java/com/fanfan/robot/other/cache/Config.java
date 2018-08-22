package com.fanfan.robot.other.cache;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;

import com.fanfan.novel.utils.ACache;

import java.io.Serializable;

/**
 * Created by android on 2017/12/19.
 */

public class Config {

    private static int M = 1024 * 1024;
    private volatile static Config mConfig;
    private static LruCache<String, Object> mLruCache = new LruCache<>(1 * M);
    private static ACache mDiskCache;

    private Config(Context context) {
        mDiskCache = ACache.get(context, "config");
    }

    public static Config init(Context context) {
        if (null == mConfig) {
            synchronized (Config.class) {
                if (null == mConfig) {
                    mConfig = new Config(context);
                }
            }
        }
        return mConfig;
    }

    public static Config getSingleInstance() {
        return mConfig;
    }

    //--- 基础 -----------------------------------------------------------------------------------

    public <T extends Serializable> void saveData(@NonNull String key, @NonNull T value) {
        mLruCache.put(key, value);
        mDiskCache.put(key, value);
    }

    public <T extends Serializable> T getData(@NonNull String key, @Nullable T defaultValue) {
        T result = (T) mLruCache.get(key);
        if (result != null) {
            return result;
        }
        result = (T) mDiskCache.getAsObject(key);
        if (result != null) {
            mLruCache.put(key, result);
            return result;
        }
        return defaultValue;
    }

    private String Key_RoomList_PageIndex = "Key_RoomList_PageIndex";

    public void saveRoomListPageIndex(Integer pageIndex) {
        saveData(Key_RoomList_PageIndex, pageIndex);
    }

    public Integer getRoomListPageIndex() {
        return getData(Key_RoomList_PageIndex, 0);
    }

    private String Key_RoomList_LastPosition = "Key_RoomList_LastPosition";

    public void saveRoomListPosition(Integer lastPosition) {
        saveData(Key_RoomList_LastPosition, lastPosition);
    }

    public Integer getRoomListLastPosition() {
        return getData(Key_RoomList_LastPosition, 0);
    }
}
