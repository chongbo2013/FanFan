package com.fanfan.robot.ui.auxiliary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.adapter.recycler.city.CityAdapter;
import com.fanfan.robot.adapter.recycler.city.SearchCityAdapter;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.db.other.CityDB;
import com.fanfan.robot.model.City;
import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.view.manager.DividerItemDecoration;
import com.fanfan.robot.view.plistview.BladeView;
import com.seabreeze.log.Print;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 城市选择页面
 */
public class SelectCtiyActivity extends BarBaseActivity implements SearchView.OnQueryTextListener, AMapLocationListener {

    private static final String FORMAT = "^[a-z,A-Z].*$";

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.city_locate_img)
    ImageView ivCityLocateImg;
    @BindView(R.id.city_locate_state)
    TextView tvCityLocateState;
    @BindView(R.id.city_locate_layout)
    LinearLayout cityLocateLayout;
    @BindView(R.id.city_locate_failed)
    LinearLayout cityLocateFailed;
    @BindView(R.id.city_content_container)
    RelativeLayout mCityContainer;
    @BindView(R.id.search_list)
    RecyclerView mSearchRecyclerView;
    @BindView(R.id.search_empty)
    TextView searchEmpty;
    @BindView(R.id.search_container_layout)
    FrameLayout mSearchContainerLayout;
    @BindView(R.id.city_locate)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.citys_list)
    RecyclerView mCityRecyclerView;
    @BindView(R.id.citys_list_empty)
    LinearLayout citysListEmpty;
    @BindView(R.id.citys_bladeview)
    BladeView mLetter;

    public static final int CITY_REQUEST_CODE = 228;
    public static final int CITY_RESULT_CODE = 229;
    public static final String RESULT_CODE = "city_result";

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, SelectCtiyActivity.class);
        context.startActivityForResult(intent, CITY_REQUEST_CODE);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    //搜索
    private SearchView mSearchView;
    private SearchView.SearchAutoComplete mSearchAutoComplete;

    //定位
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption = null;
    private boolean isLoacte;
    private City LoacteCity;

    private CityAdapter mCityAdapter;

    private CityDB mCityDB;

    private InputMethodManager mInputMethodManager;


    private List<City> mCityList;
    private Map<String, Integer> mIndexer;

    private SearchCityAdapter mSearchCityAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_city;
    }

    @Override
    protected void initView() {
        super.initView();

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

        mlocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();

        tvCityLocateState.setVisibility(View.VISIBLE);
        cityLocateFailed.setVisibility(View.GONE);
        ivCityLocateImg.setVisibility(View.GONE);


        mLetter.setVisibility(View.GONE);
        mLetter.setOnBladeClickListener(new BladeView.OnBladeClickListener() {

            @Override
            public void onBladeClick(String s) {
                if (mIndexer.get(s) != null) {
                    mCityRecyclerView.scrollToPosition(mIndexer.get(s));
                }
            }
        });


        initCityAdapter();

        mSearchContainerLayout.setVisibility(View.GONE);
        initSearchAdapter();

    }

    private void initCityAdapter() {
        mCityAdapter = new CityAdapter(mCityList);
        mCityAdapter.isFirstOnly(false);
        mCityAdapter.openLoadAnimation();
        mCityRecyclerView.setAdapter(mCityAdapter);
        mCityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCityRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mCityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Print.e(mCityAdapter.getItem(position));
                chooseCity(mCityAdapter.getItem(position));
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initSearchAdapter() {
        mSearchCityAdapter = new SearchCityAdapter(mCityList);
        mSearchCityAdapter.isFirstOnly(false);
        mSearchCityAdapter.openLoadAnimation();
        mSearchRecyclerView.setAdapter(mSearchCityAdapter);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSearchRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSearchRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mInputMethodManager.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                return false;
            }
        });

        mSearchCityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Print.e(mSearchCityAdapter.getItem(position));
                chooseCity(mSearchCityAdapter.getItem(position));
            }
        });

        mSearchCityAdapter.setOnDataChangeListener(new SearchCityAdapter.OnDataChangeListener() {
            @Override
            public void onDataListener(int count) {
                if (count > 0) {
                    searchEmpty.setVisibility(View.INVISIBLE);
                    mSearchRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    searchEmpty.setVisibility(View.VISIBLE);
                    mSearchRecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
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

        mCityList = new ArrayList<>();
        mIndexer = new HashMap<>();

        new AsyncTask<Void, Void, List<City>>() {
            @Override
            protected List<City> doInBackground(Void... voids) {
                return prepareCityList();
            }

            @Override
            protected void onPostExecute(List<City> cities) {
                mCityList = cities;
                mLetter.setVisibility(View.VISIBLE);
                mCityAdapter.replaceData(mCityList);

                mSearchCityAdapter.replaceData(mCityList);
            }
        }.execute();

        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        //通过MenuItem得到SearchView
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchAutoComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        mSearchView.setQueryHint(getResources().getString(R.string.biz_plugin_weather_search_city_hint));

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


    @OnClick({R.id.city_locate})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_locate:
                if (isLoacte) {
                    Print.e(LoacteCity);
                    chooseCity(LoacteCity);
                } else {
                    showToast("未定位所在城市,请稍后重试！");
                }
                break;
        }
    }

    private void chooseCity(City city) {
        if (city != null) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_CODE, city.getCity());
            setResult(CITY_RESULT_CODE, intent);
        }
        finish();
    }


    private List<City> prepareCityList() {
        String path = "/data" + Environment.getDataDirectory().getAbsolutePath() + File.separator
                + AppUtil.getPackageName(SelectCtiyActivity.this) + File.separator + CityDB.CITY_DB_NAME;
        File db = new File(path);
        if (!db.exists()) {
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mCityDB = new CityDB(SelectCtiyActivity.this, path);
        List<City> allCity = mCityDB.getAllCity();
        for (int i = 0; i < BladeView.b.length; i++) {
            City city = new City();
            city.setGroupId(BladeView.b[i]);
            city.setFirstPY(BladeView.b[i]);
            city.setItemtype(City.TYPE_LEVEL_GROUP);
            allCity.add(city);
        }
        Collections.sort(allCity, new Comparator<City>() {
            @Override
            public int compare(City o1, City o2) {
                if (o1.getFirstPY().equals(o2.getFirstPY())) {
                    if (o1.getCity() == null) {
                        return -1;
                    } else if (o2.getCity() == null) {
                        return 1;
                    } else {
                        return o1.getCity().compareTo(o2.getCity());
                    }
                } else {
                    if ("#".equals(o1.getFirstPY())) {
                        return 1;
                    } else if ("#".equals(o2.getFirstPY())) {
                        return -1;
                    }
                    return o1.getFirstPY().compareTo(o2.getFirstPY());
                }
            }
        });
        for (int i = 0; i < allCity.size(); i++) {
            if (allCity.get(i).getItemType() == City.TYPE_LEVEL_GROUP) {
                mIndexer.put(allCity.get(i).getFirstPY(), i);
            }
        }
        Print.e(mIndexer);
        return allCity;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (mCityList.size() < 1 || TextUtils.isEmpty(newText)) {
            mCityContainer.setVisibility(View.VISIBLE);
            mSearchContainerLayout.setVisibility(View.INVISIBLE);
        } else {
            mCityContainer.setVisibility(View.INVISIBLE);
            mSearchContainerLayout.setVisibility(View.VISIBLE);
            mSearchCityAdapter.getFilter().filter(newText);
        }
        return false;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                Print.e(aMapLocation.getLatitude());//获取纬度
                Print.e(aMapLocation.getLongitude());//获取经度
                Print.e(aMapLocation.getAccuracy());//获取精度信息
                Print.e(aMapLocation.getCity());
                Print.e(aMapLocation.getTime());

                if (aMapLocation.getCity() != null && !aMapLocation.getCity().equals("")) {

                    mlocationClient.stopLocation();
                    cityLocateFailed.setVisibility(View.GONE);
                    tvCityLocateState.setVisibility(View.VISIBLE);
                    ivCityLocateImg.setVisibility(View.VISIBLE);
                    tvCityLocateState.setText(aMapLocation.getCity());
                    LoacteCity = mCityDB.getCity(aMapLocation.getCity());
                    isLoacte = true;
                    tvToolbar.setText(LoacteCity.getCity());

                } else {
                    mlocationClient.startLocation();

                    cityLocateLayout.setVisibility(View.GONE);
                    cityLocateFailed.setVisibility(View.VISIBLE);
                }

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Print.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                cityLocateLayout.setVisibility(View.GONE);
                cityLocateFailed.setVisibility(View.VISIBLE);
            }
        }
    }
}
