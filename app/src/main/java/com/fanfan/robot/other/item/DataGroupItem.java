package com.fanfan.robot.other.item;

import com.fanfan.novel.utils.TimeUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.model.Data;
import com.fanfan.robot.view.recyclerview.tree.ViewHolder;
import com.fanfan.robot.view.recyclerview.tree.factory.ItemHelperFactory;
import com.fanfan.robot.view.recyclerview.tree.item.TreeItem;
import com.fanfan.robot.view.recyclerview.tree.item.TreeItemGroup;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by android on 2018/1/10.
 */

public class DataGroupItem extends TreeItemGroup<Data> {

    @Override
    protected List<TreeItem> initChildList(Data data) {
        List<TreeItem> treeItemList = ItemHelperFactory.createTreeItemList(data.getSingleBeen(), this);
        return treeItemList;
    }

    @Override
    protected int initLayoutId() {
        return R.layout.item_sort_group;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder) {
        holder.setText(R.id.tv_content, TimeUtils.getGroupTime(data.getData()));
    }

}
