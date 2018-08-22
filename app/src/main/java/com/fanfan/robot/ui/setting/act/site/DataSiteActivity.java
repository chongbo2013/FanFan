package com.fanfan.robot.ui.setting.act.site;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.adapter.recycler.site.SiteDataAdapter;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.db.manager.SiteDBManager;
import com.fanfan.robot.model.SiteBean;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 网址数据页面
 */
public class DataSiteActivity extends BarBaseActivity {

    @BindView(R.id.ptr_framelayout)
    PtrFrameLayout mPtrFrameLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, DataSiteActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private SiteDBManager mSiteDBManager;

    private List<SiteBean> siteBeanList = new ArrayList<>();

    private SiteDataAdapter siteDataAdapter;

    private int updatePosition;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_site;
    }

    @Override
    protected void initView() {
        super.initView();
        mPtrFrameLayout.disableWhenHorizontalMove(true);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, recyclerView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                }, 200);
            }
        });

        siteDataAdapter = new SiteDataAdapter(siteBeanList);
        siteDataAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showNeutralNotitleDialog(position);
                return false;
            }
        });
        siteDataAdapter.openLoadAnimation();

        recyclerView.setAdapter(siteDataAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mSiteDBManager = new SiteDBManager();
        updatePosition = -1;
    }

    @Override
    protected void initData() {

        siteBeanList = mSiteDBManager.loadAll();
        mPtrFrameLayout.refreshComplete();
        if (siteBeanList != null && siteBeanList.size() > 0) {
            isNuEmpty();
            siteDataAdapter.replaceData(siteBeanList);
        } else {
            isEmpty();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_black, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                AddSiteActivity.newInstance(this, AddSiteActivity.ADD_SITE_REQUESTCODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddSiteActivity.ADD_SITE_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                long id = data.getLongExtra(AddSiteActivity.RESULT_CODE, -1);
                if (id != -1) {
                    isNuEmpty();
                    SiteBean bean = mSiteDBManager.selectByPrimaryKey(id);
                    if (updatePosition == -1) {
                        if (siteBeanList.size() == 0) {
                            isNuEmpty();
                        }
                        siteBeanList.add(bean);
                        siteDataAdapter.addData(bean);
                    } else {
                        siteBeanList.remove(updatePosition);
                        siteBeanList.add(updatePosition, bean);
                        siteDataAdapter.remove(updatePosition);
                        siteDataAdapter.addData(updatePosition, bean);
                        updatePosition = -1;
                    }
                }
            }
        }
    }

    private void showNeutralNotitleDialog(final int position) {
        DialogUtils.showNeutralNotitleDialog(this, "选择您要执行的操作", "删除所有",
                "删除此条", "修改此条", new DialogUtils.OnNeutralDialogListener() {
                    @Override
                    public void neutralText() {
                        if (mSiteDBManager.deleteAll()) {
                            siteBeanList.clear();
                            siteDataAdapter.replaceData(siteBeanList);
                        }
                    }

                    @Override
                    public void negativeText() {
                        if (mSiteDBManager.delete(siteBeanList.get(position))) {
                            siteBeanList.remove(position);
                            siteDataAdapter.remove(position);
                        }
                    }

                    @Override
                    public void positiveText() {
                        updatePosition = position;
                        SiteBean siteBean = siteBeanList.get(updatePosition);
                        AddSiteActivity.newInstance(DataSiteActivity.this, siteBean.getId(), AddSiteActivity.ADD_SITE_REQUESTCODE);
                    }
                });
    }
}
