package com.fanfan.robot.adapter.recycler.naviga;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.model.NavigationBean;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/2/6.
 */
@Deprecated
public class NavigationAdapterOld extends BaseQuickAdapter<NavigationBean, BaseViewHolder> {

    public NavigationAdapterOld(@Nullable List<NavigationBean> data) {
        super(R.layout.item_navigation_data1);
    }

    @Override
    protected void convert(BaseViewHolder helper, NavigationBean item) {
        helper.setText(R.id.show_title, item.getTitle());
        helper.setText(R.id.save_time, TimeUtils.getShortTime(item.getSaveTime()));
        helper.setText(R.id.tv_guide, item.getNavigation());
        helper.setText(R.id.tv_datail, item.getDatail());
        ImageLoader.loadImage(mContext, (ImageView) helper.getView(R.id.iv_navigation_image), item.getImgUrl(), R.mipmap.ic_logo);
    }


}
