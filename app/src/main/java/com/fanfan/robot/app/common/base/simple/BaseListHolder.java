package com.fanfan.robot.app.common.base.simple;

import android.util.SparseArray;
import android.view.View;

public class BaseListHolder extends BaseListAdapter.ViewHolder {

    private SparseArray<View> mHolderViews;


    public BaseListHolder(View itemView) {
        super(itemView);
        mHolderViews = new SparseArray<>();
    }

    public void hold(int... resIds) {
        for (int id : resIds) {
            mHolderViews.put(id, itemView.findViewById(id));
        }
    }

    public <V> V get(int id) {
        return (V) mHolderViews.get(id);
    }
}
