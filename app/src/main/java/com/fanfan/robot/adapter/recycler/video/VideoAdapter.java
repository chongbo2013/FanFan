package com.fanfan.robot.adapter.recycler.video;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.model.VideoBean;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class VideoAdapter extends BaseQuickAdapter<VideoBean, BaseViewHolder> {

    public VideoAdapter(@Nullable List<VideoBean> data) {
        super(R.layout.item_video_simple, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoBean item) {
        helper.setText(R.id.tv_name, item.getShowTitle());
    }

}
