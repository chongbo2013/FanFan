package com.fanfan.robot.adapter.base.holder;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by android on 2017/12/20.
 */

public class TreeViewHolder extends RecyclerView.ViewHolder {


    private SparseArray<View> mViews;

    public TreeViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    public static TreeViewHolder createViewHolder(View itemView) {
        return new TreeViewHolder(itemView);
    }

    public static TreeViewHolder createViewHolder(ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent,
                false);
        return createViewHolder(itemView);
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (null == view) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public TreeViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public TreeViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

}
