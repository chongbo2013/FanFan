package com.fanfan.robot.adapter.recycler.voice;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.model.VoiceBean;
import com.fanfan.robot.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class VoiceAdapter extends BaseQuickAdapter<VoiceBean, BaseViewHolder> {

    private List<Boolean> isClicks;

    public VoiceAdapter(@Nullable List<VoiceBean> data) {
        super(R.layout.item_voice_simple, data);
        isClicks = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            isClicks.add(false);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, VoiceBean item) {
        helper.setText(R.id.tv_showtitle, item.getShowTitle());
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int positions) {
        super.onBindViewHolder(holder, positions);
        if (isClicks.get(positions)) {
            ((TextView) holder.getView(R.id.tv_showtitle)).setTextColor(Color.WHITE);
            ((CardView) holder.getView(R.id.card_voice)).setCardBackgroundColor(mContext.getResources().getColor(R.color.voice_item_back));
        } else {
            ((TextView) holder.getView(R.id.tv_showtitle)).setTextColor(Color.BLACK);
            ((CardView) holder.getView(R.id.card_voice)).setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
    }

    @Override
    public void replaceData(@NonNull Collection<? extends VoiceBean> data) {
        for (int i = 0; i < data.size(); i++) {
            isClicks.add(false);
        }
        super.replaceData(data);
    }

    public void notifyClick(int position) {
        for (int i = 0; i < isClicks.size(); i++) {
            isClicks.set(i, false);
        }
        isClicks.set(position, true);
        notifyDataSetChanged();
    }

}
