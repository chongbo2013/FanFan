package com.fanfan.robot.other.item;

import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.model.CheckIn;
import com.fanfan.robot.view.recyclerview.tree.ViewHolder;
import com.fanfan.robot.view.recyclerview.tree.item.TreeItem;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by android on 2018/1/10.
 */

public class SingItem extends TreeItem<CheckIn> {

    @Override
    protected int initLayoutId() {
        return R.layout.item_sing;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder) {
        holder.setText(R.id.tv_name, getData().getName());
        holder.setText(R.id.tv_time, TimeUtils.getItemTime(getData().getTime()));
    }
}
