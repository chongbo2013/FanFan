package com.fanfan.robot.adapter.recycler.city;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.model.City;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by Administrator on 2018/3/19/019.
 */

public class CityAdapter extends BaseMultiItemQuickAdapter<City, BaseViewHolder> {

    public CityAdapter(List<City> data) {
        super(data);
        addItemType(City.TYPE_LEVEL_COLUMN, R.layout.select_city_column_item);
        addItemType(City.TYPE_LEVEL_GROUP, R.layout.select_city_group_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, City item) {
        switch (helper.getItemViewType()) {
            case City.TYPE_LEVEL_GROUP:
                helper.setText(R.id.group_title, item.getGroupId());
                break;
            case City.TYPE_LEVEL_COLUMN:
                helper.setText(R.id.column_title, item.getCity());
                break;
        }
    }

}
