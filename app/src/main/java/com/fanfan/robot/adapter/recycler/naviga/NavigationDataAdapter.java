package com.fanfan.robot.adapter.recycler.naviga;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class NavigationDataAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public NavigationDataAdapter(@Nullable List<String> data) {
        super(R.layout.item_navigation_data, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        String title = null;
        if (item.indexOf(".") > 0) {
            title = item.substring(0, item.indexOf("."));
        }
        helper.setText(R.id.tv_navigation_title, title);
        ImageView imageView = helper.getView(R.id.iv_navigation_image);

        RequestOptions requestOptions = new RequestOptions().fitCenter();
        ImageLoader.loadImage(mContext, imageView, Constants.ASSEST_PATH + item, requestOptions);
    }


}
