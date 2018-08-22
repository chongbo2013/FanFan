package com.fanfan.robot.ui.media.fragment;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.app.common.base.BaseFragment;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.recycler.media.LocalDanceAdapter;
import com.fanfan.robot.db.manager.DanceDBManager;
import com.fanfan.robot.model.Dance;
import com.fanfan.robot.ui.media.act.DanceActivity;
import com.fanfan.robot.ui.setting.act.dance.DanceAddActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by android on 2018/1/10.
 */

public class DanceFragment extends BaseFragment {

    @BindView(R.id.ptr_framelayout)
    PtrFrameLayout mPtrFrameLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;


    public static DanceFragment newInstance() {
        return new DanceFragment();
    }

    private LocalDanceAdapter mAdapter;
    private List<Dance> dances;

    private DanceDBManager mDanceDBManager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dence;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

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

        dances = new ArrayList<>();
        mAdapter = new LocalDanceAdapter(dances);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        mAdapter.isFirstOnly(false); //设置不仅是首次填充数据时有动画,以后上下滑动也会有动画
        mAdapter.openLoadAnimation();

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                playDance(dances.get(position));
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showNeutralNotitleDialog(position);
                return false;
            }
        });

        mDanceDBManager = new DanceDBManager();
    }

    private void playDance(Dance dance) {
        DanceActivity.newInstance(getActivity(), dance.getId());
    }

    @Override
    protected void initData() {

        dances = mDanceDBManager.loadAll();
        mPtrFrameLayout.refreshComplete();
        if (dances != null && dances.size() > 0) {
            tvEmpty.setVisibility(View.GONE);
            mAdapter.replaceData(dances);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setListener(View view) {

    }

    public void add() {
        DanceAddActivity.newInstance(getActivity(), this, DanceAddActivity.ADD_DANCE_REQUESTCODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DanceAddActivity.ADD_DANCE_REQUESTCODE) {
            if (resultCode == DanceAddActivity.ADD_DANCE_RESULTCODE) {
                dances = mDanceDBManager.loadAll();
                if (dances != null && dances.size() > 0) {
                    tvEmpty.setVisibility(View.GONE);
                    mAdapter.replaceData(dances);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
            }
        }
    }

    private void showNeutralNotitleDialog(final int position) {
        DialogUtils.showNeutralNotitleDialog(getActivity(), "选择您要执行的操作", "删除此舞蹈",
                "修改此舞蹈", "开始跳舞", new DialogUtils.OnNeutralDialogListener() {
                    @Override
                    public void neutralText() {
                        if (mDanceDBManager.delete(dances.get(position))) {
                            dances.remove(position);
                            mAdapter.remove(position);
                        }
                    }

                    @Override
                    public void negativeText() {
                        Dance dance = dances.get(position);
                        DanceAddActivity.newInstance(getActivity(), DanceFragment.this, dance.getId(), DanceAddActivity.ADD_DANCE_REQUESTCODE);
                    }

                    @Override
                    public void positiveText() {
                        playDance(dances.get(position));
                    }
                });
    }
}
