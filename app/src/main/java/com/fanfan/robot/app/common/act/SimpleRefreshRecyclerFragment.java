package com.fanfan.robot.app.common.act;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fanfan.robot.adapter.base.HeaderFooterAdapter;
import com.fanfan.youtu.api.base.event.BaseEvent;

import java.util.List;

/**
 * Created by android on 2017/12/19.
 */

public abstract class SimpleRefreshRecyclerFragment<T, Event extends BaseEvent<List<T>>> extends RefreshRecyclerActivity<T, Event> {

    @NonNull
    @Override
    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
//        return new SpeedyLinearLayoutManager(this);
        return new GridLayoutManager(this, 2);
    }

    @Override
    protected void onRefresh(Event event, HeaderFooterAdapter adapter) {
        adapter.clearDatas();
        adapter.addDatas(event.getBean());
        showToast("刷新成功");
    }

    @Override
    protected void onLoadMore(Event event, HeaderFooterAdapter adapter) {
        adapter.addDatas(event.getBean());
    }

    @Override
    protected void onError(Event event, String postType) {
        if (postType.equals(POST_LOAD_MORE)) {
            showToast("加载更多失败");
        } else if (postType.equals(POST_REFRESH)) {
            showToast("刷新数据失败");
        }
    }

}
