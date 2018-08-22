package com.fanfan.robot.ui.media.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.app.common.base.BaseFragment;
import com.fanfan.robot.service.PlayService;
import com.fanfan.robot.other.cache.MusicCache;
import com.fanfan.robot.listener.music.OnPlayerEventListener;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.music.MusicUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.recycler.media.LocalMusicAdapter;
import com.fanfan.robot.model.Music;
import com.fanfan.robot.ui.media.MultimediaActivity;
import com.seabreeze.log.Print;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/10.
 */

public class SongFragment extends BaseFragment implements OnPlayerEventListener {

    private static final String LOCAL_MUSIC_POSITION = "local_music_position";
    private static final String LOCAL_MUSIC_OFFSET = "local_music_offset";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.iv_play_bar_cover)
    ImageView ivPlayBarCover;
    @BindView(R.id.tv_play_bar_title)
    TextView tvPlayBarTitle;
    @BindView(R.id.tv_play_bar_artist)
    TextView tvPlayBarArtist;
    @BindView(R.id.iv_play_bar_play)
    ImageView ivPlayBarPlay;
    @BindView(R.id.iv_play_bar_next)
    ImageView ivPlayBarNext;
    @BindView(R.id.pb_play_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.fl_play_bar)
    RelativeLayout flPlayBar;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    private boolean isPlay;

    public static final String FRAG_IS_PLAY = "frag_is_play";

    public static SongFragment newInstance() {
        return new SongFragment();
    }

    private LocalMusicAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_song;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }

    @Override
    protected void initData() {

        mAdapter = new LocalMusicAdapter(MusicCache.get().getMusicList());
        mAdapter.openLoadAnimation();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                stopListener();
                getPlayService().play(position);
            }
        });

        onChangeImpl(getPlayService().getPlayingMusic());
    }

    @Override
    protected void setListener(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (isAdded()) {//判断Fragment已经依附Activity
            isPlay = getArguments().getBoolean(FRAG_IS_PLAY);
        }
        if (isPlay) {
            onPlay();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        int position = mAdapter.getPlayingPosition();
        int offset = (recyclerView.getChildAt(0) == null) ? 0 : recyclerView.getChildAt(0).getTop();
        outState.putInt(LOCAL_MUSIC_POSITION, position);
        outState.putInt(LOCAL_MUSIC_OFFSET, offset);
    }

    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                int position = savedInstanceState.getInt(LOCAL_MUSIC_POSITION);
                int offset = savedInstanceState.getInt(LOCAL_MUSIC_OFFSET);
                Print.e(position + " " + offset);
            }
        });
    }

    @Override
    public void onDestroy() {
        PlayService service = MusicCache.get().getPlayService();
        if (service != null) {
            service.setOnPlayEventListener(null);
        }
        super.onDestroy();
    }

    private void onChangeImpl(Music music) {
        if (music == null) {
            return;
        }

        ImageLoader.loadImage(this, ivPlayBarCover,
                music.getAlbumId() == -1 ? MusicUtils.getMediaDataAlbumPic(music.getPath()) : MusicUtils.getMediaStoreAlbumCoverUri(music.getAlbumId()),
                R.mipmap.default_cover);

        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(getPlayService().isPlaying() || getPlayService().isPreparing());
        mProgressBar.setMax((int) music.getDuration());
        mProgressBar.setProgress((int) getPlayService().getCurrentPosition());

        if (MusicCache.get().getMusicList().isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        mAdapter.updatePlayingPosition(getPlayService());
        recyclerView.smoothScrollToPosition(getPlayService().getPlayingPosition());
    }


    @OnClick({R.id.fl_play_bar, R.id.iv_play_bar_play, R.id.iv_play_bar_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_play_bar:
                break;
            case R.id.iv_play_bar_play:
                onPlay();
                break;
            case R.id.iv_play_bar_next:
                stopListener();
                next();
                break;
        }
    }

    public void play() {
        getPlayService().playPause();
    }

    private void next() {
        getPlayService().next();
    }

    @Override
    public void onChange(Music music) {
        onChangeImpl(music);
    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        if (ivPlayBarPlay != null) {
            ivPlayBarPlay.setSelected(false);
        }
    }

    @Override
    public void onPublish(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onTimer(long remain) {

    }

    @Override
    public void onMusicListUpdate() {
        if (MusicCache.get().getMusicList().isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        mAdapter.replaceData(MusicCache.get().getMusicList());
        mAdapter.updatePlayingPosition(getPlayService());
        mAdapter.notifyDataSetChanged();
    }


    public void stopMusic() {
        getPlayService().stop();
    }

    public void stopListener() {
        Print.e("停止监听 ...... ");
        assert ((MultimediaActivity) getActivity()) != null;
        ((MultimediaActivity) getActivity()).stopListener();
    }

    public void startListener() {
        Print.e("启动监听 ...... ");
        assert ((MultimediaActivity) getActivity()) != null;
        ((MultimediaActivity) getActivity()).startListener();
    }

    public void back() {
        if (getPlayService().isPlaying()) {
            stopMusic();
        } else {
            ((MultimediaActivity) getActivity()).onPlayerPause();
        }
    }

    public void onPlay() {
        if (getPlayService().isPlaying()) {
            startListener();
        } else {
            stopListener();
        }
        play();
    }
}
