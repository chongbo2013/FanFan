package com.fanfan.robot.adapter.recycler.map;

import android.support.annotation.Nullable;

import com.amap.api.services.route.DriveStep;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.novel.utils.map.AMapUtil;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by Administrator on 2018/3/6/006.
 */

public class DriveSegmentAdapter extends BaseQuickAdapter<DriveStep, BaseViewHolder> {

    public DriveSegmentAdapter(@Nullable List<DriveStep> data) {
        super(R.layout.item_drive_segment, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DriveStep item) {
        if (helper.getLayoutPosition() == 0) {
            helper.setBackgroundRes(R.id.bus_dir_icon, R.drawable.dir_start);
            helper.setText(R.id.bus_line_name, "出发");
            helper.setVisible(R.id.bus_dir_icon_up, false);
            helper.setVisible(R.id.bus_dir_icon_down, true);
            helper.setVisible(R.id.bus_seg_split_line, false);
        } else if (helper.getLayoutPosition() == mData.size() - 1) {
            helper.setBackgroundRes(R.id.bus_dir_icon, R.drawable.dir_end);
            helper.setText(R.id.bus_line_name, "到达终点");
            helper.setVisible(R.id.bus_dir_icon_up, true);
            helper.setVisible(R.id.bus_dir_icon_down, false);
            helper.setVisible(R.id.bus_seg_split_line, true);
        } else {
            String actionName = item.getAction();
            helper.setBackgroundRes(R.id.bus_dir_icon, AMapUtil.getDriveActionID(actionName));
            helper.setText(R.id.bus_line_name, item.getInstruction());
            helper.setVisible(R.id.bus_dir_icon_up, true);
            helper.setVisible(R.id.bus_dir_icon_down, true);
            helper.setVisible(R.id.bus_seg_split_line, true);
        }
    }
}
