package com.fanfan.robot.adapter.recycler.map;

import android.support.annotation.Nullable;

import com.amap.api.services.route.BusPath;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.novel.utils.map.AMapUtil;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by Administrator on 2018/3/6/006.
 */

public class BusResultAdapter extends BaseQuickAdapter<BusPath, BaseViewHolder> {

    public BusResultAdapter(@Nullable List<BusPath> data) {
        super(R.layout.item_bus_result, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BusPath item) {
        helper.setText(R.id.bus_path_title, AMapUtil.getBusPathTitle(item));
        helper.setText(R.id.bus_path_des, AMapUtil.getBusPathDes(item));
    }
}
