package com.fanfan.robot.adapter.recycler.media;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.robot.R;
import com.fanfan.robot.model.Dance;

import java.util.List;

/**
 * Created by android on 2018/1/11.
 */

public class LocalDanceAdapter extends BaseQuickAdapter<Dance, BaseViewHolder> {

    public LocalDanceAdapter(@Nullable List<Dance> data) {
        super(R.layout.view_holder_dance, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Dance item) {
        ImageView ivCover = helper.getView(R.id.iv_cover);

        ImageLoader.loadImage(mContext, ivCover, item.getCoverPath(), R.mipmap.default_cover_dance);

        helper.setText(R.id.tv_title, item.getTitle());
    }

}
