package com.fanfan.robot.adapter.recycler.vr;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.robot.R;
import com.fanfan.robot.model.VrImage;

import java.util.List;

/**
 * Created by Administrator on 2018/3/7/007.
 */

public class VrImageAdapter extends BaseQuickAdapter<VrImage, BaseViewHolder> {

    public VrImageAdapter(@Nullable List<VrImage> data) {
        super(R.layout.item_vr_image, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VrImage item) {
        ImageLoader.loadImage(mContext, (ImageView) helper.getView(R.id.iv_vr_image), item.getPath());
        helper.setText(R.id.tv_vr_text, item.getName());
    }
}
