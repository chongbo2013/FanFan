package com.fanfan.robot.adapter.recycler.video;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.model.VideoBean;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class VideoDataAdapter extends BaseQuickAdapter<VideoBean, BaseViewHolder> {

    public VideoDataAdapter(@Nullable List<VideoBean> data) {
        super(R.layout.item_video_data, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoBean item) {
        helper.setText(R.id.show_title, item.getShowTitle());
        helper.setText(R.id.save_time, TimeUtils.getShortTime(item.getSaveTime()));
        helper.setText(R.id.tv_video_url, item.getVideoUrl());
        ImageLoader.loadImage(mContext, (ImageView) helper.getView(R.id.iv_video_image), item.getVideoImage(), R.mipmap.ic_logo);
    }

}
