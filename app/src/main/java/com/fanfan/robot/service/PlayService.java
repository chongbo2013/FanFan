package com.fanfan.robot.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.other.cache.MusicCache;
import com.fanfan.novel.utils.media.SingleMediaScanner;
import com.fanfan.robot.listener.music.Actions;
import com.fanfan.robot.other.music.AudioFocusManager;
import com.fanfan.robot.listener.music.EventCallback;
import com.fanfan.robot.other.music.MediaSessionManager;
import com.fanfan.robot.other.music.NoisyAudioStreamReceiver;
import com.fanfan.robot.listener.music.OnPlayerEventListener;
import com.fanfan.robot.other.music.PlayModeEnum;
import com.fanfan.robot.other.music.QuitTimer;
import com.fanfan.novel.utils.system.PreferencesUtils;
import com.fanfan.novel.utils.music.MusicUtils;
import com.fanfan.robot.db.manager.MusicDBManager;
import com.fanfan.robot.model.Music;
import com.seabreeze.log.Print;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.fanfan.robot.app.common.Constants.unusual;

/**
 * Created by android on 2018/1/10.
 */

public class PlayService extends Service implements MediaPlayer.OnCompletionListener {

    private static final long TIME_UPDATE = 300L;

    public static final String MUSIC_ID = "music_id";
    public static final String PLAY_MODE = "play_mode";
    public static final String SPLASH_URL = "splash_url";
    public static final String NIGHT_MODE = "night_mode";

    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private final NoisyAudioStreamReceiver mNoisyReceiver = new NoisyAudioStreamReceiver();
    private final IntentFilter mNoisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private AudioFocusManager mAudioFocusManager;
    private MediaSessionManager mMediaSessionManager;

    private MediaPlayer mPlayer = new MediaPlayer();
    private final Handler mHandler = new Handler();
    // 正在播放的歌曲[本地|网络]
    private Music mPlayingMusic;
    // 正在播放的本地歌曲的序号
    private int mPlayingPosition = -1;
    private int mPlayState = STATE_IDLE;

    private OnPlayerEventListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioFocusManager = new AudioFocusManager(this);
        mMediaSessionManager = new MediaSessionManager(this, this);
        mPlayer.setOnCompletionListener(this);
        QuitTimer.getInstance().init(this, mHandler, new EventCallback<Long>() {
            @Override
            public void onEvent(Long aLong) {
                if (mListener != null) {
                    mListener.onTimer(aLong);
                }
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public static void startCommand(Context context, String action) {
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Actions.ACTION_MEDIA_PLAY_PAUSE:
                    playPause();
                    break;
                case Actions.ACTION_MEDIA_NEXT:
                    next();
                    break;
                case Actions.ACTION_MEDIA_PREVIOUS:
                    prev();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    /**
     * 扫描音乐
     */
    @SuppressLint("StaticFieldLeak")
    public void updateMusicList(final EventCallback<Void> callback) {
        PreferencesUtils.putBoolean(PlayService.this, Constants.MUSIC_UPDATE, false);
        if (PreferencesUtils.getBoolean(PlayService.this, Constants.MUSIC_UPDATE, false)) {
            scanMussicExecute(callback);
        } else {
            if (unusual) {

                new SingleMediaScanner(this, Environment.getExternalStorageDirectory(),
                        new SingleMediaScanner.ScanListener() {
                            @Override
                            public void onScanFinish(String s, Uri uri) {
                                Print.e("onScanCompleted : " + s);
                                loadMusic(callback);
                            }
                        });
            } else {
                loadMusic(callback);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loadMusic(final EventCallback<Void> callback) {
        new AsyncTask<Void, Void, List<Music>>() {
            @Override
            protected List<Music> doInBackground(Void... params) {
                return MusicUtils.scanMusic(PlayService.this, true);
            }

            @Override
            protected void onPostExecute(List<Music> musicList) {
                PreferencesUtils.putBoolean(PlayService.this, Constants.MUSIC_UPDATE, true);
                MusicDBManager mMusicDBManager = new MusicDBManager();
                mMusicDBManager.deleteAll();
                mMusicDBManager.insertList(musicList);
                scanMussicExecute(callback);
            }
        }.execute();
    }

    private void scanMussicExecute(EventCallback<Void> callback) {
        MusicDBManager mMusicDBManager = new MusicDBManager();
        List<Music> musicList = mMusicDBManager.loadAll();
        MusicCache.get().getMusicList().clear();
        MusicCache.get().getMusicList().addAll(musicList);

        if (!MusicCache.get().getMusicList().isEmpty()) {
            updatePlayingPosition();
            mPlayingMusic = MusicCache.get().getMusicList().get(mPlayingPosition);
        }

        if (mListener != null) {
            mListener.onMusicListUpdate();
        }

        if (callback != null) {
            callback.onEvent(null);
        }
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        next();
    }

    public void setOnPlayEventListener(OnPlayerEventListener listener) {
        mListener = listener;
    }

    public void play(int position) {
        if (MusicCache.get().getMusicList().isEmpty()) {
            return;
        }

        if (position < 0) {
            position = MusicCache.get().getMusicList().size() - 1;
        } else if (position >= MusicCache.get().getMusicList().size()) {
            position = 0;
        }

        mPlayingPosition = position;
        Music music = MusicCache.get().getMusicList().get(mPlayingPosition);
        PreferencesUtils.putLong(this, MUSIC_ID, music.getId());
        play(music);
    }

    public void play(Music music) {
        mPlayingMusic = music;
        try {
            mPlayer.reset();
            mPlayer.setDataSource(music.getPath());
            mPlayer.prepareAsync();
            mPlayState = STATE_PREPARING;
            mPlayer.setOnPreparedListener(mPreparedListener);
            mPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            if (mListener != null) {
                mListener.onChange(music);
            }
            mMediaSessionManager.updateMetaData(mPlayingMusic);
            mMediaSessionManager.updatePlaybackState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (isPreparing()) {
                start();
            }
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mListener != null) {
                mListener.onBufferingUpdate(percent);
            }
        }
    };

    public void playPause() {
        if (isPreparing()) {
            stop();
        } else if (isPlaying()) {
            pause();
        } else if (isPausing()) {
            start();
        } else {
            play(getPlayingPosition());
        }
    }

    public void start() {
        if (!isPreparing() && !isPausing()) {
            return;
        }

        if (mAudioFocusManager.requestAudioFocus()) {
            mPlayer.start();
            mPlayState = STATE_PLAYING;
            mHandler.post(mPublishRunnable);
            mMediaSessionManager.updatePlaybackState();
            registerReceiver(mNoisyReceiver, mNoisyFilter);

            if (mListener != null) {
                mListener.onPlayerStart();
            }
        }
    }

    public void pause() {
        if (!isPlaying()) {
            return;
        }

        mPlayer.pause();
        mPlayState = STATE_PAUSE;
        mHandler.removeCallbacks(mPublishRunnable);
        mMediaSessionManager.updatePlaybackState();
        unregisterReceiver(mNoisyReceiver);

        if (mListener != null) {
            mListener.onPlayerPause();
        }
    }

    public void stop() {
        if (isIdle()) {
            return;
        }

        pause();
        mPlayer.reset();
        mPlayState = STATE_IDLE;
    }

    public void next() {
        if (MusicCache.get().getMusicList().isEmpty()) {
            return;
        }

        PlayModeEnum mode = PlayModeEnum.valueOf(PreferencesUtils.getInt(this, PLAY_MODE, 0));
        switch (mode) {
            case SHUFFLE:
                mPlayingPosition = new Random().nextInt(MusicCache.get().getMusicList().size());
                play(mPlayingPosition);
                break;
            case SINGLE:
                play(mPlayingPosition);
                break;
            case LOOP:
            default:
                play(mPlayingPosition + 1);
                break;
        }
    }

    public void prev() {
        if (MusicCache.get().getMusicList().isEmpty()) {
            return;
        }

        PlayModeEnum mode = PlayModeEnum.valueOf(PreferencesUtils.getInt(this, PLAY_MODE, 0));
        switch (mode) {
            case SHUFFLE:
                mPlayingPosition = new Random().nextInt(MusicCache.get().getMusicList().size());
                play(mPlayingPosition);
                break;
            case SINGLE:
                play(mPlayingPosition);
                break;
            case LOOP:
            default:
                play(mPlayingPosition - 1);
                break;
        }
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mPlayer.seekTo(msec);
            mMediaSessionManager.updatePlaybackState();
            if (mListener != null) {
                mListener.onPublish(msec);
            }
        }
    }

    public boolean isPlaying() {
        return mPlayState == STATE_PLAYING;
    }

    public boolean isPausing() {
        return mPlayState == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return mPlayState == STATE_PREPARING;
    }

    public boolean isIdle() {
        return mPlayState == STATE_IDLE;
    }

    /**
     * 获取正在播放的本地歌曲的序号
     */
    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    /**
     * 获取正在播放的歌曲[本地|网络]
     */
    public Music getPlayingMusic() {
        return mPlayingMusic;
    }

    /**
     * 删除或下载歌曲后刷新正在播放的本地歌曲的序号
     */
    public void updatePlayingPosition() {
        int position = 0;
        long id = PreferencesUtils.getLong(this, MUSIC_ID, -1);
        for (int i = 0; i < MusicCache.get().getMusicList().size(); i++) {
            if (MusicCache.get().getMusicList().get(i).getId() == id) {
                position = i;
                break;
            }
        }
        mPlayingPosition = position;
        PreferencesUtils.putLong(this, MUSIC_ID, MusicCache.get().getMusicList().get(mPlayingPosition).getId());
    }

    public long getCurrentPosition() {
        if (isPlaying() || isPausing()) {
            return mPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying() && mListener != null) {
                mListener.onPublish(mPlayer.getCurrentPosition());
            }
            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };

    @Override
    public void onDestroy() {
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        mAudioFocusManager.abandonAudioFocus();
        mMediaSessionManager.release();
        MusicCache.get().setPlayService(null);
        super.onDestroy();
    }

    public void quit() {
        stop();
        QuitTimer.getInstance().stop();
        stopSelf();
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }
}
