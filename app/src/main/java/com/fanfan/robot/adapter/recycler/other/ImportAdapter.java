package com.fanfan.robot.adapter.recycler.other;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.R;
import com.fanfan.robot.model.Channel;

import java.util.List;

/**
 * Created by android on 2018/2/23.
 */

public class ImportAdapter extends BaseMultiItemQuickAdapter<Channel, BaseViewHolder> {

    private boolean mIsEdit;

    private RecyclerView mRecyclerView;

    public ImportAdapter(List<Channel> data) {
        super(data);
        mIsEdit = false;
        addItemType(Channel.TYPE_TITLE, R.layout.item_channel_title);
        addItemType(Channel.TYPE_CONTENT, R.layout.channel_rv_item);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mRecyclerView = (RecyclerView) parent;
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected void convert(BaseViewHolder helper, Channel item) {
        switch (helper.getItemViewType()) {
            case Channel.TYPE_TITLE:
                helper.setText(R.id.tvTitle, item.getChannelName());
                helper.getView(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsEdit) {
                            startEditMode(true);
                        } else {
                            startEditMode(false);
                        }
                    }
                });
                break;
            case Channel.TYPE_CONTENT:
                helper.setText(R.id.tv_channelname, item.getChannelName())
                        .setVisible(R.id.img_edit, mIsEdit)
                        .addOnClickListener(R.id.img_edit);
                break;
        }
    }

    private void startEditMode(boolean isEdit) {
        mIsEdit = isEdit;
        int visibleChildCount = mRecyclerView.getChildCount();
        for (int i = 0; i < visibleChildCount; i++) {
            View view = mRecyclerView.getChildAt(i);
            ImageView imgEdit = (ImageView) view.findViewById(R.id.img_edit);
            TextView tvName = (TextView) view.findViewById(R.id.tv_channelname);
            TextView tvEdit = (TextView) view.findViewById(R.id.tv_edit);

            if (imgEdit != null) {
                imgEdit.setVisibility(imgEdit.getTag() != null && isEdit ? View.VISIBLE : View.INVISIBLE);
            }

            if (tvName != null) {
                if (tvName.getTag() == null) return;
                if (isEdit && (Boolean) tvName.getTag()) {
                    tvName.setTextColor(Color.GRAY);
                } else {
                    tvName.setTextColor(Color.BLACK);
                }
            }

            if (tvEdit != null) {
                if (isEdit) {
                    tvEdit.setText("完成");
                } else {
                    tvEdit.setText("编辑");
                }
            }

        }
    }
}
