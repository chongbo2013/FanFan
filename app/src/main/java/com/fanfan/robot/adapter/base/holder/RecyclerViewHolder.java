package com.fanfan.robot.adapter.base.holder;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created by android on 2017/12/19.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private View mRootView;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        mRootView = itemView;
    }

    public View getRootView() {
        return mRootView;
    }

    private final SparseArray<View> mViews = new SparseArray<View>();

    private <T extends View> T bindView(int id) {
        T view = (T) mViews.get(id);
        if (view == null) {
            view = (T) mRootView.findViewById(id);
            mViews.put(id, view);
        }
        return view;
    }

    public <T extends View> T get(int id) {
        return (T) bindView(id);
    }

    public void setOnClickListener(View.OnClickListener l, int... ids) {
        if (ids == null) {
            return;
        }
        for (int id : ids) {
            get(id).setOnClickListener(l);
        }
    }

    public void setText(int id, String text) {
        TextView textView = get(id);
        textView.setText(text);
    }
}

