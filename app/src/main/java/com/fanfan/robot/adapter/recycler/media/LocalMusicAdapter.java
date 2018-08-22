package com.fanfan.robot.adapter.recycler.media;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.service.PlayService;
import com.fanfan.novel.utils.FucUtil;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.music.MusicUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.model.Music;

import java.util.List;

/**
 * Created by android on 2018/1/10.
 */

public class LocalMusicAdapter extends BaseQuickAdapter<Music, BaseViewHolder> {

    private int mPlayingPosition;

    public LocalMusicAdapter(@Nullable List<Music> data) {
        super(R.layout.view_holder_music, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Music item) {
        View vPlaying = helper.getView(R.id.v_playing);
        if (helper.getLayoutPosition() == mPlayingPosition) {
            vPlaying.setVisibility(View.VISIBLE);
        } else {
            vPlaying.setVisibility(View.INVISIBLE);
        }

//        ImageLoader.loadImage(mContext, (ImageView) helper.getView(R.id.iv_cover),
//                item.getAlbumId() == -1 ? MusicUtils.getMediaDataAlbumPic(item.getPath()) : MusicUtils.getMediaStoreAlbumCoverUri(item.getAlbumId()),
//                R.mipmap.default_cover);

        ImageLoader.loadImage(mContext, (ImageView) helper.getView(R.id.iv_cover), item.getAlbumId() == -1 ? MusicUtils.getMediaDataAlbumPic(item.getPath()) : MusicUtils.getMediaStoreAlbumCoverUri(item.getAlbumId()),
                R.mipmap.default_cover, R.mipmap.default_cover, false, DiskCacheStrategy.NONE);


        helper.setText(R.id.tv_title, item.getTitle());

        String artist = FucUtil.getArtistAndAlbum(item.getArtist(), item.getAlbum());
        helper.setText(R.id.tv_artist, artist);

        ImageView ivMore = helper.getView(R.id.iv_more);


        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void updatePlayingPosition(PlayService playService) {
        int forPos = mPlayingPosition;
        if (playService.getPlayingMusic() != null) {
            mPlayingPosition = playService.getPlayingPosition();
        } else {
            mPlayingPosition = -1;
        }
        if (mPlayingPosition != -1) {
            notifyItemChanged(forPos);
            notifyItemChanged(mPlayingPosition);
        }
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
    }


}
