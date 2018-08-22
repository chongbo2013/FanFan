package com.fanfan.robot.adapter.recycler.city;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Filter;
import android.widget.Filterable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.model.City;
import com.fanfan.robot.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2018/3/19/019.
 */

public class SearchCityAdapter extends BaseQuickAdapter<City, BaseViewHolder> implements Filterable {

    private OnDataChangeListener listener;

    private List<City> mCityList;
    private boolean isFirst;

    public SearchCityAdapter(@Nullable List<City> data) {
        super(R.layout.search_city_item, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, City item) {
        helper.setText(R.id.search_province, item.getProvince());
        helper.setText(R.id.column_title, item.getCity());
    }

    @Override
    public void replaceData(@NonNull Collection<? extends City> data) {
        super.replaceData(data);
        if (!isFirst) {
            isFirst = true;
            mCityList = (List<City>) data;
        }
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            protected void publishResults(CharSequence constraint, FilterResults results) {

                replaceData((Collection<? extends City>) results.values);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onDataListener(results.count);
                }
            }

            protected FilterResults performFiltering(CharSequence s) {
                String str = s.toString().toUpperCase();
                FilterResults results = new FilterResults();
                ArrayList<City> cityList = new ArrayList<>();
                if (mCityList != null && mCityList.size() != 0) {
                    for (City cb : mCityList) {
                        if (cb.getItemType() == City.TYPE_LEVEL_COLUMN) {
                            // 匹配全屏、首字母、和城市名中文
                            if (cb.getAllFristPY().contains(str) || cb.getAllPY().contains(str)
                                    || cb.getCity().contains(str)) {
                                cityList.add(cb);
                            }
                        }
                    }
                }
                results.values = cityList;
                results.count = cityList.size();
                return results;
            }
        };
    }

    public interface OnDataChangeListener {
        void onDataListener(int count);
    }

    public void setOnDataChangeListener(OnDataChangeListener listener) {
        this.listener = listener;
    }
}
