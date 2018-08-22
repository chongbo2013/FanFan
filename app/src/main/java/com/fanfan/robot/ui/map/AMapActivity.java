package com.fanfan.robot.ui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.adapter.recycler.map.BusResultAdapter;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.novel.utils.map.AMapUtil;
import com.fanfan.novel.utils.map.overlay.DrivingRouteOverlay;
import com.fanfan.novel.utils.map.overlay.PoiOverlay;
import com.fanfan.novel.utils.map.overlay.WalkRouteOverlay;
import com.fanfan.robot.R;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.ui.map.act.BusRouteDetailActivity;
import com.fanfan.robot.ui.map.act.DriveRouteDetailActivity;
import com.fanfan.robot.ui.map.act.WalkRouteDetailActivity;
import com.seabreeze.log.Print;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;

/**
 * Created by android on 2018/2/28.
 */

public class AMapActivity extends BarBaseActivity implements SearchView.OnQueryTextListener,
        AMapLocationListener, LocationSource, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter,
        PoiSearch.OnPoiSearchListener, Inputtips.InputtipsListener, RouteSearch.OnRouteSearchListener {

    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.routemap_header)
    RelativeLayout routemapHeader;
    @BindView(R.id.route_bus)
    ImageView mBus;
    @BindView(R.id.route_drive)
    ImageView mDrive;
    @BindView(R.id.route_walk)
    ImageView mWalk;
    @BindView(R.id.bottom_layout)
    RelativeLayout mBottomLayout;
    @BindView(R.id.firstline)
    TextView mRotueTimeDes;
    @BindView(R.id.secondline)
    TextView mRouteDetailDes;
    @BindView(R.id.bus_result)
    LinearLayout mBusResultLayout;
    @BindView(R.id.bus_result_list)
    RecyclerView mBusResultList;

    //map
    private AMap aMap;
    public static final int ZOOM = 15;

    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;

    private boolean isFirstIn = true;

    private LatLng lastLatLng;

    // 当前页面，从0开始计数
    private int currentPage = 0;
    // Poi查询条件类
    private PoiSearch.Query query;
    // POI搜索
    private PoiSearch poiSearch;
    // poi返回的结果
    private PoiResult poiResult;

    //驾车路劲规划
    private RouteSearch mRouteSearch;

    private BusRouteResult mBusRouteResult;
    private DriveRouteResult mDriveRouteResult;
    private WalkRouteResult mWalkRouteResult;

    //起始位置
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;

    private final int ROUTE_TYPE_BUS = 1;
    private final int ROUTE_TYPE_DRIVE = 2;
    private final int ROUTE_TYPE_WALK = 3;
    private final int ROUTE_TYPE_CROSSTOWN = 4;

    private String keyWord;

    private SearchView mSearchView;
    private SearchView.SearchAutoComplete mSearchAutoComplete;

    private MaterialDialog progDialog = null;// 搜索时进度条

    private PopupWindow popupWindow;
    private boolean popIsshow;

    private RecyclerView mRecyclerView;
    private BaseQuickAdapter baseAdapter;

    private Marker targetMarker;

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, AMapActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);
        initMap();
        routemapHeader.setVisibility(View.GONE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_amap;
    }

    @Override
    protected void initData() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchAutoComplete.isShown()) {
                    closeImm();
                } else {
                    finish();
                }
            }
        });
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

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        //通过MenuItem得到SearchView
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchAutoComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        mSearchView.setQueryHint("搜索相关地点");

        try {
            Class cls = Class.forName("android.support.v7.widget.SearchView");
            Field field = cls.getDeclaredField("mSearchSrcTextView");
            field.setAccessible(true);
            TextView tv = (TextView) field.get(mSearchView);
            Class[] clses = cls.getDeclaredClasses();
            for (Class cls_ : clses) {
                if (cls_.toString().endsWith("android.support.v7.widget.SearchView$SearchAutoComplete")) {
                    Class targetCls = cls_.getSuperclass().getSuperclass().getSuperclass().getSuperclass();
                    Field cuosorIconField = targetCls.getDeclaredField("mCursorDrawableRes");
                    cuosorIconField.setAccessible(true);
                    cuosorIconField.set(tv, R.drawable.cursor_color);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);
        //设置输入框提示文字样式
        mSearchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.white));
        mSearchAutoComplete.setTextColor(getResources().getColor(android.R.color.background_light));
        mSearchAutoComplete.setTextSize(14);
        //设置触发查询的最少字符数（默认2个字符才会触发查询）
        mSearchAutoComplete.setThreshold(1);
        //设置搜索框有字时显示叉叉，无字时隐藏叉叉
        mSearchView.onActionViewExpanded();
        mSearchView.setIconified(true);
        mSearchView.setIconifiedByDefault(false);
        //修改搜索框控件间的间隔（这样只是为了更加接近网易云音乐的搜索框）
        LinearLayout search_edit_frame = (LinearLayout) mSearchView.findViewById(R.id.search_edit_frame);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search_edit_frame.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 10;
        search_edit_frame.setLayoutParams(params);

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImm();
                mSearchView.setIconifiedByDefault(false);
            }
        });

        closeImm();
        return super.onCreateOptionsMenu(menu);
    }


    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();

            aMap.setLocationSource(this);
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
            myLocationStyle.strokeColor(Color.BLUE);
            myLocationStyle.strokeWidth(2);
            aMap.setMyLocationStyle(myLocationStyle);
            UiSettings settings = aMap.getUiSettings();
            settings.setMyLocationButtonEnabled(true);
            settings.setCompassEnabled(false);

            aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
            aMap.setMyLocationEnabled(true);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM));

            aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));

            aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
            aMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件

            mRouteSearch = new RouteSearch(this);
            mRouteSearch.setRouteSearchListener(this);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Print.e("onQueryTextSubmit : " + query);
        if (!AMapUtil.IsEmptyOrNullString(keyWord)) {
            doSearchQuery();
            closeImm();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Print.e("onQueryTextChange : " + newText);
        keyWord = newText.trim();
        if (!AMapUtil.IsEmptyOrNullString(keyWord)) {
            InputtipsQuery inputquery = new InputtipsQuery(keyWord, "");
            Inputtips inputTips = new Inputtips(this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
        return false;
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        mBusResultLayout.setVisibility(View.GONE);
        mapView.setVisibility(View.VISIBLE);
        dismissPopWindow();
        showProgressDialog("正在搜索:\n" + keyWord);// 显示进度框
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keyWord, "", "");
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    private void openImm() {
        tvToolbar.setVisibility(View.GONE);

        mSearchView.requestFocusFromTouch();
        mSearchView.setFocusable(true);
        mSearchView.setFocusableInTouchMode(true);
        mSearchView.requestFocus();
    }

    private void closeImm() {
        try {
            mSearchAutoComplete.setText("");
            Method method = mSearchView.getClass().getDeclaredMethod("onCloseClicked");
            method.setAccessible(true);
            method.invoke(mSearchView);
            mSearchView.clearFocus();
            mSearchView.setIconifiedByDefault(true);

            tvToolbar.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //**********************************************************************************************

    /**
     * 公交路线搜索
     */
    public void onBusClick(View view) {
        searchRouteResult(ROUTE_TYPE_BUS, RouteSearch.BusDefault);
        mDrive.setImageResource(R.drawable.route_drive_normal);
        mBus.setImageResource(R.drawable.route_bus_select);
        mWalk.setImageResource(R.drawable.route_walk_normal);
        mapView.setVisibility(View.GONE);
        mBusResultLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 驾车路线搜索
     */
    public void onDriveClick(View view) {
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
        mDrive.setImageResource(R.drawable.route_drive_select);
        mBus.setImageResource(R.drawable.route_bus_normal);
        mWalk.setImageResource(R.drawable.route_walk_normal);
        mapView.setVisibility(View.VISIBLE);
        mBusResultLayout.setVisibility(View.GONE);
    }

    /**
     * 步行路线搜索
     */
    public void onWalkClick(View view) {
        searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
        mDrive.setImageResource(R.drawable.route_drive_normal);
        mBus.setImageResource(R.drawable.route_bus_normal);
        mWalk.setImageResource(R.drawable.route_walk_select);
        mapView.setVisibility(View.VISIBLE);
        mBusResultLayout.setVisibility(View.GONE);
    }

    /**
     * 跨城公交路线搜索
     */
    public void onCrosstownBusClick(View view) {
//        searchRouteResult(ROUTE_TYPE_CROSSTOWN, RouteSearch.BusDefault);
//        mDrive.setImageResource(R.drawable.route_drive_normal);
//        mBus.setImageResource(R.drawable.route_bus_normal);
//        mWalk.setImageResource(R.drawable.route_walk_normal);
//        mapView.setVisibility(View.GONE);
        mBusResultLayout.setVisibility(View.GONE);
        AMapUtil.startAMapNavi(this, targetMarker);
    }

    //**********************************************************************************************

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null) {
            progDialog = new MaterialDialog.Builder(this)
                    .title("请稍后")
                    .content(message)
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();
        } else {
            progDialog.setTitle(message);
            progDialog.show();
        }
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                double latitude = aMapLocation.getLatitude();//纬    度
                double longitude = aMapLocation.getLongitude();//经    度
                float accuracy = aMapLocation.getAccuracy();//精    度
                String address = aMapLocation.getAddress();
                String cityName = aMapLocation.getCity();
                String time = AMapUtil.formatUTC(aMapLocation.getTime(), "yyyy-MM-dd HH:mm:ss");

                if (isFirstIn) {
                    lastLatLng = new LatLng(latitude, longitude);
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM));
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(lastLatLng));
                    mListener.onLocationChanged(aMapLocation);
                    isFirstIn = false;

                    mStartPoint = new LatLonPoint(latitude, longitude);

                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Print.e(errText);
            }
        }
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        Print.e("activate : " + "开始定位");
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            setLocationClient();
        }
    }

    private void setLocationClient() {
        mLocationClient = new AMapLocationClient(this);
        AMapLocationClientOption locationOption = getDefaultOption();
        mLocationClient.setLocationOption(locationOption);
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();
    }

    /**
     * 默认的定位参数
     *
     * @return
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }


    /**
     * 点击marker事件
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    /**
     * 悬浮信息
     *
     * @param marker
     * @return
     */
    @Override
    public View getInfoWindow(final Marker marker) {
        targetMarker = marker;
        View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri, null);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());

        TextView button = view.findViewById(R.id.start_amap_app);
        // 调起高德地图app
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AMapUtil.startAMapNavi(AMapActivity.this, marker);
                mEndPoint = AMapUtil.convertToLatLonPoint(marker.getPosition());
                onDriveClick(null);
            }
        });
        return view;
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            mLocationClient.startLocation();
            return;
        }
        if (mEndPoint == null) {
            showToast("终点未设置");
        }
        showProgressDialog("规划路径中");
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_BUS) {
            // 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, mode, RobotInfo.getInstance().getCityName(), 0);
            mRouteSearch.calculateBusRouteAsyn(query);
        } else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == ROUTE_TYPE_WALK) {
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        Print.e("onPoiSearched : " + rCode);
        dissmissProgressDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            // 搜索poi的结果
            if (result != null && result.getQuery() != null) {
                // 是否是同一条
                if (result.getQuery().equals(query)) {
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页, 取得第一页的poiitem数据，页数从数字0开始
                    List<PoiItem> poiItems = poiResult.getPois();
                    // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    List<SuggestionCity> cities = poiResult.getSearchSuggestionCitys();

                    if (poiItems != null && poiItems.size() > 0) {
                        Print.e("搜索结果为 ： " + poiItems.size() + " 条");
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (cities != null && cities.size() > 0) {
                        String infomation = "推荐城市\n";
                        for (int i = 0; i < cities.size(); i++) {
                            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:" + cities.get(i).getCityCode()
                                    + "城市编码:" + cities.get(i).getAdCode() + "\n";
                        }
                        showToast(infomation);
                    } else {
                        showToast(R.string.no_result);
                    }
                }
            } else {
                showToast(R.string.no_result);
            }
        } else {
            showToast(rCode);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int rCode) {
        Print.e("onPoiItemSearched : " + rCode);
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        // 正确返回
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            Print.e("onGetInputtips : " + tipList.size());
            if (popIsshow) {
                setAdapter(tipList);
            } else {
                showPopupWindow();
                setAdapter(tipList);
            }
        } else {
            showToast(rCode);
        }
    }

    private void showPopupWindow() {
        popIsshow = true;
        View contentView = LayoutInflater.from(AMapActivity.this).inflate(R.layout.map_pop_list_layout, null);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.pop_recycler_view);
        baseAdapter = null;
        int xPos = 100;

        popupWindow = new PopupWindow(contentView, Constants.displayWidth - (2 * xPos),
                Constants.displayHeight / 2, true);

        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        ColorDrawable dw = new ColorDrawable(00000000);
        popupWindow.setBackgroundDrawable(dw);
//        backgroundAlpha(0.5f);// 设置背景半透明
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setTouchable(true);
        popupWindow.showAsDropDown(line, xPos, 0);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popIsshow = false;
            }
        });
    }

    // 设置popupWindow背景半透明
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getWindow().setAttributes(lp);
    }

    private void setAdapter(final List<Tip> tipList) {
        if (baseAdapter == null) {
            baseAdapter = new BaseQuickAdapter<Tip, BaseViewHolder>(R.layout.route_inputs, tipList) {
                @Override
                protected void convert(BaseViewHolder helper, Tip item) {
                    helper.setText(R.id.tv_route, item.getName().trim());
                }
            };
            baseAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Tip tip = tipList.get(position);
                    keyWord = tip.getName().trim();
                    if (!AMapUtil.IsEmptyOrNullString(keyWord)) {
                        doSearchQuery();
                        closeImm();
                    }
                }
            });
            baseAdapter.isFirstOnly(false); //设置不仅是首次填充数据时有动画,以后上下滑动也会有动画
            baseAdapter.openLoadAnimation();
            mRecyclerView.setAdapter(baseAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        } else {
            baseAdapter.replaceData(tipList);
        }
    }

    private void dismissPopWindow() {
        if (popIsshow) {
            popIsshow = false;
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }
    }


    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {
        dissmissProgressDialog();
        routemapHeader.setVisibility(View.VISIBLE);
        mBottomLayout.setVisibility(View.GONE);
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mBusRouteResult = result;
                    final List<BusPath> busPaths = mBusRouteResult.getPaths();
                    BusResultAdapter busResultAdapter = new BusResultAdapter(busPaths);
                    busResultAdapter.openLoadAnimation();

                    mBusResultList.setAdapter(busResultAdapter);
                    mBusResultList.setLayoutManager(new LinearLayoutManager(this));
                    mBusResultList.setItemAnimator(new DefaultItemAnimator());
                    busResultAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Intent intent = new Intent(mContext, BusRouteDetailActivity.class);
                            intent.putExtra("bus_path", busPaths.get(position));
                            intent.putExtra("bus_result", mBusRouteResult);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    showToast(R.string.no_result);
                }
            } else {
                showToast(R.string.no_result);
            }
        } else {
            showToast(errorCode);
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        Print.e("onDriveRouteSearched : " + errorCode);
        dissmissProgressDialog();
        routemapHeader.setVisibility(View.VISIBLE);
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths().get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(mContext, aMap, drivePath,
                            mDriveRouteResult.getStartPos(), mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();

                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    mRouteDetailDes.setText("打车约" + taxiCost + "元");
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, DriveRouteDetailActivity.class);
                            intent.putExtra("drive_path", drivePath);
                            intent.putExtra("drive_result", mDriveRouteResult);
                            startActivity(intent);
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    showToast(R.string.no_result);
                }

            } else {
                showToast(R.string.no_result);
            }
        } else {
            showToast(errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        dissmissProgressDialog();
        routemapHeader.setVisibility(View.VISIBLE);
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = result.getPaths().get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(mContext, aMap, walkPath,
                            mWalkRouteResult.getStartPos(), mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();

                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.GONE);
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, WalkRouteDetailActivity.class);
                            intent.putExtra("walk_path", walkPath);
                            intent.putExtra("walk_result", mWalkRouteResult);
                            startActivity(intent);
                        }
                    });

                } else if (result != null && result.getPaths() == null) {
                    showToast(R.string.no_result);
                }
            } else {
                showToast(R.string.no_result);
            }
        } else {
            showToast(errorCode);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}