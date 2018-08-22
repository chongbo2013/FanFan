package com.fanfan.robot.view.recyclerview.refresh.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fanfan.robot.adapter.base.holder.RecyclerViewHolder;

/**
 * Created by android on 2017/12/19.
 */

public abstract class BaseViewProvider<T> {
    private LayoutInflater mInflater;
    private int mLayoutId;
    protected Context mContext;

    public BaseViewProvider(@NonNull Context context, @NonNull @LayoutRes int layout_id) {
        mInflater = LayoutInflater.from(context);
        mLayoutId = layout_id;
        mContext = context;
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);
        onViewHolderIsCreated(viewHolder);
        return viewHolder;
    }

    public void onViewHolderIsCreated(RecyclerViewHolder holder) {

    }

    public abstract void onBindView(RecyclerViewHolder holder, T bean);
}
