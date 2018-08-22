package com.fanfan.robot.adapter.recycler.site;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.model.SiteBean;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/2/28.
 */

public class SiteDataAdapter extends BaseQuickAdapter<SiteBean, BaseViewHolder> {

    public SiteDataAdapter(@Nullable List<SiteBean> data) {
        super(R.layout.item_site_data, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SiteBean item) {
        if (item.getName().isEmpty())
            return;
        helper.setText(R.id.name, item.getName());
        ImageView icon = helper.getView(R.id.icon);
        if (item.getAvatar_url() != null) {
            ImageLoader.loadImage(mContext, icon, item.getAvatar_url(), R.mipmap.ic_logo);
        } else {
            icon.setVisibility(View.GONE);
        }
    }

}
