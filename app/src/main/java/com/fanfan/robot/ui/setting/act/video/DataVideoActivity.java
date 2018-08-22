package com.fanfan.robot.ui.setting.act.video;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.adapter.recycler.video.VideoDataAdapter;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.db.manager.VideoDBManager;
import com.fanfan.robot.model.VideoBean;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 视频数据页面
 */
public class DataVideoActivity extends BarBaseActivity {

    @BindView(R.id.ptr_framelayout)
    PtrFrameLayout mPtrFrameLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, DataVideoActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private VideoDBManager mVideoDBManager;

    private List<VideoBean> videoBeanList = new ArrayList<>();

    private VideoDataAdapter videoDataAdapter;

    private int updatePosition;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_video;
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

        videoDataAdapter = new VideoDataAdapter(videoBeanList);
        videoDataAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showNeutralNotitleDialog(position);
                return false;
            }
        });
        videoDataAdapter.openLoadAnimation();

        recyclerView.setAdapter(videoDataAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        mVideoDBManager = new VideoDBManager();
        updatePosition = -1;
    }

    @Override
    protected void initData() {

        videoBeanList = mVideoDBManager.loadAll();
        mPtrFrameLayout.refreshComplete();
        if (videoBeanList != null && videoBeanList.size() > 0) {
            isNuEmpty();
            videoDataAdapter.replaceData(videoBeanList);
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
                AddVideoActivity.newInstance(this, AddVideoActivity.ADD_VIDEO_REQUESTCODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddVideoActivity.ADD_VIDEO_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                long id = data.getLongExtra(AddVideoActivity.RESULT_CODE, -1);
                if (id != -1) {
                    isNuEmpty();
                    VideoBean bean = mVideoDBManager.selectByPrimaryKey(id);
                    if (updatePosition == -1) {
                        if (videoBeanList.size() == 0) {
                            isNuEmpty();
                        }
                        videoBeanList.add(bean);
                        videoDataAdapter.addData(bean);
                    } else {
                        videoBeanList.remove(updatePosition);
                        videoBeanList.add(updatePosition, bean);
                        videoDataAdapter.remove(updatePosition);
                        videoDataAdapter.addData(updatePosition, bean);
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
                        if (mVideoDBManager.deleteAll()) {
                            videoBeanList.clear();
                            videoDataAdapter.replaceData(videoBeanList);
                        }
                    }

                    @Override
                    public void negativeText() {
                        if (mVideoDBManager.delete(videoBeanList.get(position))) {
                            videoBeanList.remove(position);
                            videoDataAdapter.remove(position);
                        }
                    }

                    @Override
                    public void positiveText() {
                        updatePosition = position;
                        VideoBean videoBean = videoBeanList.get(updatePosition);
                        AddVideoActivity.newInstance(DataVideoActivity.this, videoBean.getId(), AddVideoActivity.ADD_VIDEO_REQUESTCODE);
                    }
                });
    }
}
