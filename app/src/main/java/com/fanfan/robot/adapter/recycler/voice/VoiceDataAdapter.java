package com.fanfan.robot.adapter.recycler.voice;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.model.VoiceBean;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class VoiceDataAdapter extends BaseQuickAdapter<VoiceBean, BaseViewHolder> {

    public VoiceDataAdapter(@Nullable List<VoiceBean> data) {
        super(R.layout.item_voice_data, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VoiceBean item) {
        helper.setText(R.id.show_title, item.getShowTitle());
        helper.setText(R.id.save_time, TimeUtils.getShortTime(item.getSaveTime()));
        helper.setText(R.id.tv_expression, item.getExpression());
        helper.setText(R.id.tv_action, item.getAction());
        ImageLoader.loadImage(mContext, (ImageView) helper.getView(R.id.iv_voice_image), item.getImgUrl(), R.mipmap.ic_logo);
    }
}
