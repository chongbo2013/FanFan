package com.fanfan.robot.adapter.recycler.other;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.model.AirQuery;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by dell on 2018/1/22.
 */

public class AirAdapter extends BaseQuickAdapter<AirQuery, BaseViewHolder> {


    public AirAdapter(List<AirQuery> airListQuery) {
        super(R.layout.layout_air_query, airListQuery);
    }

    @Override
    protected void convert(BaseViewHolder helper, AirQuery item) {
        helper.setText(R.id.tv_airName, item.getAirName());
        helper.setText(R.id.tv_airPlanTime, item.getAirPlanTime());
        helper.setText(R.id.tv_airActualTime, item.getAirActualTime());
        helper.setText(R.id.tv_airStart, item.getAirStart());
        helper.setText(R.id.tv_airPlanArriveTime, item.getAirPlanArriveTime());

        helper.setText(R.id.tv_airArrive, item.getAirArrive());
        helper.setText(R.id.tv_airOnTime, item.getAirOnTime());
        if ("0".equals(item.getAirStatus())) {
            helper.setText(R.id.tv_airStatus, item.getAirStatus());
            helper.setTextColor(R.id.tv_airStatus, mContext.getResources().getColor(R.color.green));
        } else {
            helper.setText(R.id.tv_airStatus, item.getAirStatus());
            helper.setTextColor(R.id.tv_airStatus, mContext.getResources().getColor(R.color.red));
        }
    }
}
