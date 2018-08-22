package com.fanfan.robot.ui.media.fragment;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fanfan.robot.app.common.base.BaseFragment;
import com.fanfan.robot.service.PlayService;
import com.fanfan.robot.listener.music.OnPlayerEventListener;
import com.fanfan.robot.other.music.PlayModeEnum;
import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.system.PreferencesUtils;
import com.fanfan.novel.utils.system.ScreenUtil;
import com.fanfan.novel.utils.system.SystemUtils;
import com.fanfan.novel.utils.music.MusicUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.other.PlayPagerAdapter;
import com.fanfan.robot.model.Music;
import com.fanfan.robot.ui.media.MultimediaActivity;
import com.fanfan.robot.view.AlbumCoverView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/13.
 */

public class PlayFragment extends BaseFragment implements OnPlayerEventListener, SeekBar.OnSeekBarChangeListener {


    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.iv_play_page_bg)
    ImageView ivPlayPageBg;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_artist)
    TextView tvArtist;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.sb_progress)
    SeekBar sbProgress;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.iv_mode)
    ImageView ivMode;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_prev)
    ImageView ivPrev;
    @BindView(R.id.iv_next)
    ImageView ivNext;
    @BindView(R.id.vp_play_page)
    ViewPager vpPlay;

    private AlbumCoverView albumCoverView;

    private int mLastProgress;
    private boolean isDraggingProgress;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_play;
    }

    @Override
    protected void initView(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = ScreenUtil.getStatusBarHeight(getActivity());
            llContent.setPadding(0, top, 0, 0);
        }
        AppUtil.setColor(getActivity(), ContextCompat.getColor(getActivity(), android.R.color.black));

        View coverView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_play_page_cover, null);
        albumCoverView = coverView.findViewById(R.id.album_cover_view);

        albumCoverView.initNeedle(getPlayService().isPlaying());

        List<View> mViews = new ArrayList<>();
        mViews.add(coverView);
        vpPlay.setAdapter(new PlayPagerAdapter(mViews));

        initPlayMode();

        onChangeImpl(getPlayService().getPlayingMusic());
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener(View view) {
        sbProgress.setOnSeekBarChangeListener(this);
    }


    private void initPlayMode() {
        int mode = PreferencesUtils.getInt(getActivity(), PlayService.PLAY_MODE, 0);
        ivMode.setImageLevel(mode);
    }


    private void onChangeImpl(Music music) {
        if (music == null) {
            return;
        }

        tvTitle.setText(music.getTitle());
        tvArtist.setText(music.getArtist());

        sbProgress.setProgress((int) getPlayService().getCurrentPosition());
        sbProgress.setSecondaryProgress(0);
        sbProgress.setMax((int) music.getDuration());

        mLastProgress = 0;
        tvCurrentTime.setText(R.string.play_time_start);
        tvTotalTime.setText(formatTime(music.getDuration()));

        albumCoverView.setCoverBitmap(MusicUtils.loadRound(getActivity(), music));

        ImageLoader.loadImage(this, ivPlayPageBg,
                music.getAlbumId() == -1 ? MusicUtils.getMediaDataAlbumPic(music.getPath()) : MusicUtils.getMediaStoreAlbumCoverUri(music.getAlbumId()),
                R.mipmap.default_cover);

        if (getPlayService().isPlaying() || getPlayService().isPreparing()) {
            ivPlay.setSelected(true);
            albumCoverView.play();
        } else {
            ivPlay.setSelected(false);
            albumCoverView.pause();
        }
    }


    @OnClick({R.id.iv_back, R.id.iv_mode, R.id.iv_play, R.id.iv_prev, R.id.iv_next})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                ((MultimediaActivity) getActivity()).onBackPressed();
                ivBack.setEnabled(false);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivBack.setEnabled(true);
                    }
                }, 300);
                break;
            case R.id.iv_mode:
                PlayModeEnum mode = PlayModeEnum.valueOf(PreferencesUtils.getInt(getActivity(), PlayService.PLAY_MODE, 0));
                switch (mode) {
                    case LOOP:
                        mode = PlayModeEnum.SHUFFLE;
                        showToast(R.string.mode_shuffle);
                        break;
                    case SHUFFLE:
                        mode = PlayModeEnum.SINGLE;
                        showToast(R.string.mode_one);
                        break;
                    case SINGLE:
                        mode = PlayModeEnum.LOOP;
                        showToast(R.string.mode_loop);
                        break;
                }
                PreferencesUtils.putInt(getActivity(), PlayService.PLAY_MODE, mode.value());
                initPlayMode();
                break;
            case R.id.iv_play:
                getPlayService().playPause();
                break;
            case R.id.iv_prev:
                getPlayService().prev();
                break;
            case R.id.iv_next:
                getPlayService().next();
                break;
        }
    }

    private String formatTime(long time) {
        return SystemUtils.formatTime("mm:ss", time);
    }

    @Override
    public void onChange(Music music) {
        onChangeImpl(music);
    }

    @Override
    public void onPlayerStart() {
        ivPlay.setSelected(true);
        albumCoverView.play();
    }

    @Override
    public void onPlayerPause() {
        ivPlay.setSelected(false);
        albumCoverView.pause();
    }

    @Override
    public void onPublish(int progress) {
        if (!isDraggingProgress) {
            sbProgress.setProgress(progress);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        sbProgress.setSecondaryProgress(sbProgress.getMax() * 100 / percent);
    }

    @Override
    public void onTimer(long remain) {

    }

    @Override
    public void onMusicListUpdate() {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbProgress) {
            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tvCurrentTime.setText(formatTime(progress));
                mLastProgress = progress;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = false;
            if (getPlayService().isPlaying() || getPlayService().isPausing()) {
                int progress = seekBar.getProgress();
                getPlayService().seekTo(progress);
            } else {
                seekBar.setProgress(0);
            }
        }
    }

}
