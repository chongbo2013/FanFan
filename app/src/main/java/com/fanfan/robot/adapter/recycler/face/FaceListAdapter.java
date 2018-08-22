package com.fanfan.robot.adapter.recycler.face;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by android on 2018/1/9.
 */

public class FaceListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public FaceListAdapter(@Nullable List<String> data) {
        super(android.R.layout.simple_list_item_1, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(android.R.id.text1, item);
    }
}