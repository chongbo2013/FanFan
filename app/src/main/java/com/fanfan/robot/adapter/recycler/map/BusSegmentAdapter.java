package com.fanfan.robot.adapter.recycler.map;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.route.RailwayStationItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.model.map.SchemeBusStep;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by Administrator on 2018/3/7/007.
 */

public class BusSegmentAdapter extends BaseQuickAdapter<SchemeBusStep, BaseViewHolder> {

    public BusSegmentAdapter(@Nullable List<SchemeBusStep> data) {
        super(R.layout.item_bus_segment, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final SchemeBusStep item) {
        if (helper.getLayoutPosition() == 0) {
            helper.setImageResource(R.id.bus_dir_icon, R.drawable.dir_start);
            helper.setText(R.id.bus_line_name, "出发");
            helper.setVisible(R.id.bus_dir_icon_up, false);
            helper.setVisible(R.id.bus_dir_icon_down, true);
            helper.setVisible(R.id.bus_seg_split_line, false);
            helper.setVisible(R.id.bus_station_num, false);
            helper.setVisible(R.id.bus_expand_image, false);
        } else if (helper.getLayoutPosition() == getData().size() - 1) {
            helper.setImageResource(R.id.bus_dir_icon, R.drawable.dir_end);
            helper.setText(R.id.bus_line_name, "到达终点");
            helper.setVisible(R.id.bus_dir_icon_up, true);
            helper.setVisible(R.id.bus_dir_icon_down, false);
            helper.setVisible(R.id.bus_station_num, false);
            helper.setVisible(R.id.bus_expand_image, false);
        } else {
            if (item.isWalk() && item.getWalk() != null && item.getWalk().getDistance() > 0) {
                helper.setImageResource(R.id.bus_dir_icon, R.drawable.dir13);
                helper.setVisible(R.id.bus_dir_icon_up, true);
                helper.setVisible(R.id.bus_dir_icon_down, true);
                helper.setText(R.id.bus_line_name, "步行" + (int) item.getWalk().getDistance() + "米");
                helper.setVisible(R.id.bus_station_num, false);
                helper.setVisible(R.id.bus_expand_image, false);
            } else if (item.isBus() && item.getBusLines().size() > 0) {
                helper.setImageResource(R.id.bus_dir_icon, R.drawable.dir14);
                helper.setVisible(R.id.bus_dir_icon_up, true);
                helper.setVisible(R.id.bus_dir_icon_down, true);
                helper.setText(R.id.bus_line_name, item.getBusLines().get(0).getBusLineName());
                helper.setVisible(R.id.bus_station_num, true);
                helper.setText(R.id.bus_station_num, (item.getBusLines().get(0).getPassStationNum() + 1) + "站");
                helper.setVisible(R.id.bus_expand_image, true);
                helper.setTag(R.id.stationinfo, true);
                helper.setOnClickListener(R.id.drive_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout stationinfo = helper.getView(R.id.stationinfo);
                        LinearLayout expandContent = helper.getView(R.id.expand_content);
                        if ((Boolean) stationinfo.getTag()) {
                            stationinfo.setTag(false);
                            helper.setImageResource(R.id.bus_expand_image, R.drawable.up);
                            BusStationItem busStationItem = item.getBusLine().getDepartureBusStation();
                            addBusStation(busStationItem, expandContent);

                            for (BusStationItem station : item.getBusLine().getPassStations()) {
                                addBusStation(station, expandContent);
                            }
                            addBusStation(item.getBusLine().getArrivalBusStation(), expandContent);

                        } else {
                            stationinfo.setTag(true);
                            helper.setImageResource(R.id.bus_expand_image, R.drawable.down);
                            expandContent.removeAllViews();
                        }
                    }
                });
            } else if (item.isRailway() && item.getRailway() != null) {
                helper.setImageResource(R.id.bus_dir_icon, R.drawable.dir16);
                helper.setVisible(R.id.bus_dir_icon_up, true);
                helper.setVisible(R.id.bus_dir_icon_down, true);
                helper.setText(R.id.bus_line_name, item.getRailway().getName());
                helper.setVisible(R.id.bus_station_num, true);
                helper.setText(R.id.bus_station_num, (item.getRailway().getViastops().size() + 1) + "站");
                helper.setVisible(R.id.bus_expand_image, true);
                helper.setTag(R.id.stationinfo, true);
                helper.setOnClickListener(R.id.drive_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout stationinfo = helper.getView(R.id.stationinfo);
                        LinearLayout expandContent = helper.getView(R.id.expand_content);
                        if ((Boolean) stationinfo.getTag()) {
                            stationinfo.setTag(false);
                            helper.setImageResource(R.id.bus_expand_image, R.drawable.up);
                            addRailwayStation(item.getRailway().getDeparturestop(), expandContent);
                            for (RailwayStationItem station : item.getRailway().getViastops()) {
                                addRailwayStation(station, expandContent);
                            }
                            addRailwayStation(item.getRailway().getArrivalstop(), expandContent);

                        } else {
                            stationinfo.setTag(true);
                            helper.setImageResource(R.id.bus_expand_image, R.drawable.down);
                            expandContent.removeAllViews();
                        }
                    }
                });
            } else if (item.isTaxi() && item.getTaxi() != null) {
                helper.setImageResource(R.id.bus_dir_icon, R.drawable.dir14);
                helper.setVisible(R.id.bus_dir_icon_up, true);
                helper.setVisible(R.id.bus_dir_icon_down, true);
                helper.setText(R.id.bus_line_name, "打车到终点");
                helper.setVisible(R.id.bus_station_num, false);
                helper.setVisible(R.id.bus_expand_image, false);
            }
        }
    }

    private void addBusStation(BusStationItem busStationItem, LinearLayout expandContent) {
        LinearLayout busEx = (LinearLayout) mLayoutInflater.inflate(R.layout.item_bus_segment_ex, null);
        TextView tv = busEx.findViewById(R.id.bus_line_station_name);
        tv.setText(busStationItem.getBusStationName());
        expandContent.addView(busEx);
    }

    private void addRailwayStation(RailwayStationItem railwayStationItem, LinearLayout expandContent) {
        LinearLayout busEx = (LinearLayout) mLayoutInflater.inflate(R.layout.item_bus_segment_ex, null);
        TextView tv = busEx.findViewById(R.id.bus_line_station_name);
        tv.setText(railwayStationItem.getName() + " " + getRailwayTime(railwayStationItem.getTime()));
        expandContent.addView(busEx);
    }

    private String getRailwayTime(String time) {
        return time.substring(0, 2) + ":" + time.substring(2, time.length());
    }

}