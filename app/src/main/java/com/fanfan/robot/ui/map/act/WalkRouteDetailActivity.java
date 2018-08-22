package com.fanfan.robot.ui.map.act;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkStep;
import com.fanfan.robot.adapter.recycler.map.WalkSegmentAdapter;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.novel.utils.map.AMapUtil;
import com.fanfan.robot.R;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/3/6/006.
 */

public class WalkRouteDetailActivity extends BarBaseActivity {

    @BindView(R.id.tv_toolbar)
    TextView tvTitle;
    @BindView(R.id.firstline)
    TextView mTitleWalkRoute;
    @BindView(R.id.show_layout)
    LinearLayout showLayout;
    @BindView(R.id.bus_segment_list)
    RecyclerView busSegmentList;

    private WalkPath mWalkPath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_route_detail;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mWalkPath = intent.getParcelableExtra("walk_path");

        tvTitle.setText("驾车路线详情");

        String dur = AMapUtil.getFriendlyTime((int) mWalkPath.getDuration());
        String dis = AMapUtil
                .getFriendlyLength((int) mWalkPath.getDistance());
        mTitleWalkRoute.setText(dur + "(" + dis + ")");

        List<WalkStep> walkSteps = mWalkPath.getSteps();
        walkSteps.add(0, new WalkStep());
        walkSteps.add(new WalkStep());

        WalkSegmentAdapter segmentAdapter = new WalkSegmentAdapter(walkSteps);
        segmentAdapter.openLoadAnimation();

        busSegmentList.setAdapter(segmentAdapter);
        busSegmentList.setLayoutManager(new LinearLayoutManager(this));
        busSegmentList.setItemAnimator(new DefaultItemAnimator());
    }
}
