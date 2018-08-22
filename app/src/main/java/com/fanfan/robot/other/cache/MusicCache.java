package com.fanfan.robot.other.cache;

import android.app.Application;
import android.content.Context;

import com.fanfan.robot.service.PlayService;
import com.fanfan.robot.model.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 2018/1/10.
 */

public class MusicCache {

    private Context mContext;

    private PlayService mPlayService;

    // 本地歌曲列表
    private final List<Music> mMusicList = new ArrayList<>();

    private MusicCache() {
    }

    private static class SingletonHolder {
        private static MusicCache instance = new MusicCache();
    }

    public static MusicCache get() {
        return SingletonHolder.instance;
    }

    public void init(Application application) {
        mContext = application.getApplicationContext();
    }

    public List<Music> getMusicList() {
        return mMusicList;
    }

    public void setPlayService(PlayService service) {
        mPlayService = service;
    }

    public PlayService getPlayService() {
        return mPlayService;
    }

}
