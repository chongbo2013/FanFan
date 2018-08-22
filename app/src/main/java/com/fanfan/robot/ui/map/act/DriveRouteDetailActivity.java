package com.fanfan.robot.ui.map.act;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.TMC;
import com.fanfan.robot.adapter.recycler.map.DriveSegmentAdapter;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.novel.utils.map.AMapUtil;
import com.fanfan.robot.R;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/3/6/006.
 */

public class DriveRouteDetailActivity extends BarBaseActivity {

    @BindView(R.id.tv_toolbar)
    TextView tvTitle;
    @BindView(R.id.firstline)
    TextView mTitleDriveRoute;
    @BindView(R.id.secondline)
    TextView mDesDriveRoute;
    @BindView(R.id.show_layout)
    LinearLayout showLayout;
    @BindView(R.id.bus_segment_list)
    RecyclerView busSegmentList;


    private DrivePath mDrivePath;
    private DriveRouteResult mDriveRouteResult;

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
        mDrivePath = intent.getParcelableExtra("drive_path");
        mDriveRouteResult = intent.getParcelableExtra("drive_result");
        for (int i = 0; i < mDrivePath.getSteps().size(); i++) {
            DriveStep step = mDrivePath.getSteps().get(i);
            List<TMC> tmclist = step.getTMCs();
            for (int j = 0; j < tmclist.size(); j++) {
                String s = "" + tmclist.get(j).getPolyline().size();
            }
        }

        tvTitle.setText("驾车路线详情");

        String dur = AMapUtil.getFriendlyTime((int) mDrivePath.getDuration());
        String dis = AMapUtil.getFriendlyLength((int) mDrivePath.getDistance());
        mTitleDriveRoute.setText(dur + "(" + dis + ")");

        int taxiCost = (int) mDriveRouteResult.getTaxiCost();
        mDesDriveRoute.setText("打车约" + taxiCost + "元");

        List<DriveStep> driveSteps = mDrivePath.getSteps();
        driveSteps.add(0, new DriveStep());
        driveSteps.add(new DriveStep());

        DriveSegmentAdapter segmentAdapter = new DriveSegmentAdapter(driveSteps);
        segmentAdapter.openLoadAnimation();

        busSegmentList.setAdapter(segmentAdapter);
        busSegmentList.setLayoutManager(new LinearLayoutManager(this));
        busSegmentList.setItemAnimator(new DefaultItemAnimator());
    }


}
