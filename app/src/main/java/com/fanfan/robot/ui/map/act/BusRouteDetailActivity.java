package com.fanfan.robot.ui.map.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.novel.utils.map.AMapUtil;
import com.fanfan.robot.adapter.recycler.map.BusSegmentAdapter;
import com.fanfan.robot.model.map.SchemeBusStep;
import com.fanfan.novel.utils.map.overlay.BusRouteOverlay;
import com.fanfan.robot.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/3/7/007.
 */

public class BusRouteDetailActivity extends BarBaseActivity
        implements
        AMap.OnMapLoadedListener,
        AMap.OnMapClickListener,
        AMap.InfoWindowAdapter,
        AMap.OnInfoWindowClickListener,
        AMap.OnMarkerClickListener {

    @BindView(R.id.tv_toolbar)
    TextView tvTitle;
    @BindView(R.id.firstline)
    TextView mTitleBusRoute;
    @BindView(R.id.secondline)
    TextView mDesBusRoute;
    @BindView(R.id.show_layout)
    LinearLayout showLayout;
    @BindView(R.id.bus_segment_list)
    RecyclerView busSegmentList;
    @BindView(R.id.route_map)
    MapView mapView;
    @BindView(R.id.bus_path)
    RelativeLayout mBuspathview;

    private BusPath mBuspath;

    private BusRouteResult mBusRouteResult;
    private AMap aMap;

    private List<SchemeBusStep> mBusStepList;

    private BusSegmentAdapter busSegmentAdapter;

    private Menu mMenu;
    private boolean isMapShow;

    private BusRouteOverlay mBusrouteOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);
    }

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
        mBuspath = intent.getParcelableExtra("bus_path");
        mBusRouteResult = intent.getParcelableExtra("bus_result");

        if (aMap == null) {
            aMap = mapView.getMap();
        }
        registerListener();

        setBusText();

        loadData();
        setAdapter();
    }

    private void registerListener() {
        aMap.setOnMapLoadedListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
    }

    private void setBusText() {
        tvTitle.setText("公交路线详情");

        String dur = AMapUtil.getFriendlyTime((int) mBuspath.getDuration());
        String dis = AMapUtil.getFriendlyLength((int) mBuspath.getDistance());
        mTitleBusRoute.setText(dur + "(" + dis + ")");

        int taxiCost = (int) mBusRouteResult.getTaxiCost();
        mDesBusRoute.setText("打车约" + taxiCost + "元");
        mDesBusRoute.setVisibility(View.VISIBLE);
    }

    private void loadData() {
        mBusStepList = new ArrayList<>();

        List<BusStep> busSteps = mBuspath.getSteps();

        SchemeBusStep start = new SchemeBusStep(null);
        start.setStart(true);
        mBusStepList.add(start);

        for (BusStep busStep : busSteps) {
            if (busStep.getWalk() != null && busStep.getWalk().getDistance() > 0) {
                SchemeBusStep walk = new SchemeBusStep(busStep);
                walk.setWalk(true);
                mBusStepList.add(walk);
            }
            if (busStep.getBusLine() != null) {
                SchemeBusStep bus = new SchemeBusStep(busStep);
                bus.setBus(true);
                mBusStepList.add(bus);
            }
            if (busStep.getRailway() != null) {
                SchemeBusStep railway = new SchemeBusStep(busStep);
                railway.setRailway(true);
                mBusStepList.add(railway);
            }
            if (busStep.getTaxi() != null) {
                SchemeBusStep taxi = new SchemeBusStep(busStep);
                taxi.setTaxi(true);
                mBusStepList.add(taxi);
            }
        }

        SchemeBusStep end = new SchemeBusStep(null);
        end.setEnd(true);
        mBusStepList.add(end);
    }

    private void setAdapter() {
        busSegmentAdapter = new BusSegmentAdapter(mBusStepList);
        busSegmentAdapter.openLoadAnimation();

        busSegmentList.setAdapter(busSegmentAdapter);
        busSegmentList.setLayoutManager(new LinearLayoutManager(this));
        busSegmentList.setItemAnimator(new DefaultItemAnimator());

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mMenu = menu;
        getMenuInflater().inflate(R.menu.add_map, menu);
        mMenu.findItem(R.id.map).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map:
                isMapShow = true;
                mMenu.findItem(R.id.map).setVisible(false);
                mBuspathview.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                aMap.clear();// 清理地图上的所有覆盖物
                mBusrouteOverlay = new BusRouteOverlay(this, aMap, mBuspath, mBusRouteResult.getStartPos(),
                        mBusRouteResult.getTargetPos());
                mBusrouteOverlay.removeFromMap();
                mBusrouteOverlay.addToMap();
                mBusrouteOverlay.zoomToSpan();
                break;
            case android.R.id.home:
                if (isMapShow) {
                    isMapShow = false;
                    mMenu.findItem(R.id.map).setVisible(true);
                    mBuspathview.setVisibility(View.VISIBLE);
                    mapView.setVisibility(View.GONE);
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isMapShow) {
            isMapShow = false;
            mMenu.findItem(R.id.map).setVisible(true);
            mBuspathview.setVisibility(View.VISIBLE);
            mapView.setVisibility(View.GONE);
            return;
        } else {
            finish();
        }
        super.onBackPressed();
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
