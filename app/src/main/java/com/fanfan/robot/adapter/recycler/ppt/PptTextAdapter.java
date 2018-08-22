package com.fanfan.robot.adapter.recycler.ppt;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.R;
import com.seabreeze.log.Print;

import java.util.List;

/**
 * Created by android on 2018/2/23.
 */

public class PptTextAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public PptTextAdapter(@Nullable List data) {
        super(R.layout.item_ppt_text, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_content, item);
    }


}
