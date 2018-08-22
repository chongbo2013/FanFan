package com.fanfan.novel.utils.music;

import android.content.Context;
import android.media.session.PlaybackState;
import android.net.Uri;

import com.fanfan.novel.utils.system.AppUtil;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.seabreeze.log.Print;

/**
 * Created by zhangyuanyuan on 2017/11/10.
 */

public class MediaPlayerUtil2 {

    private static MediaPlayerUtil2 mMusicPlayerManager;

    private Context mContext;
    private ExoPlayer mPlayer;
    private ControlDispatcher mDispatcher;
    private boolean mActive;
    private OnMusicCompletionListener listener;
    private int mCurrentIndex = -1;
    public static synchronized MediaPlayerUtil2 getInstance() {
        if (mMusicPlayerManager == null) {
            synchronized (MediaPlayerUtil2.class) {
                if (mMusicPlayerManager == null) {
                    mMusicPlayerManager = new MediaPlayerUtil2();
                }
            }
        }
        return mMusicPlayerManager;
    }

    private MediaPlayerUtil2() {

    }

    public void initMediaplayer(Context context) {
        this.mContext = context;
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);

        this.mDispatcher = new DefaultControlDispatcher();

        mPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playWhenReady) {
                    mActive = true;
                }
                Print.e("media onPlayerStateChanged");
                switch (playbackState) {
                    case PlaybackState.STATE_PLAYING:
                        //初始化播放点击事件并设置总时长
                        Print.e("播放状态: 准备 playing");
                        break;
                    case PlaybackState.STATE_ERROR://错误
                        Print.e("播放状态: 错误 STATE_ERROR");
                        break;
                    case PlaybackState.STATE_FAST_FORWARDING:
                        Print.e("播放状态: 快速传递");
                        listener.onFastForwarding();
                        break;
                    case PlaybackState.STATE_PAUSED:
                        Print.e("播放状态: 暂停 PAUSED");
                        break;
                    case PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM:
                        Print.e("播放状态: 跳到指定的Item");
                        break;
                    case PlaybackState.STATE_STOPPED:
                        Print.e("播放状态: 停止的 STATE_STOPPED");
                        break;
                }
            }
        });
    }

    public void playMusic(String url) {

        DynamicConcatenatingMediaSource source = new DynamicConcatenatingMediaSource();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, AppUtil.getPackageName(mContext)), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        Uri uri = Uri.parse(url);
        MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);

        source.addMediaSource(mediaSource);

        mPlayer.prepare(source);
        mPlayer.setPlayWhenReady(true);
        mDispatcher.dispatchSetPlayWhenReady(mPlayer, true);
    }

    public void setListener(OnMusicCompletionListener listener) {
        this.listener = listener;
    }

    public void stopMusic() {

                mDispatcher.dispatchSetPlayWhenReady(mPlayer, false);
//        mDispatcher.dispatchSeekTo();
//        mDispatcher.dispatchSetPlayWhenReady()
//        mDispatcher.dispatchSetRepeatMode()
//        mDispatcher.dispatchSetShuffleModeEnabled()

    }

    public interface OnMusicCompletionListener {

        void onFastForwarding();
    }

}
