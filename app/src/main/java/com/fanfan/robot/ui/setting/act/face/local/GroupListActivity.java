package com.fanfan.robot.ui.setting.act.face.local;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.aip.api.FaceApi;
import com.baidu.aip.entity.Group;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.view.manager.FullyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class GroupListActivity extends BarBaseActivity {


    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.group_list_rv)
    RecyclerView groupListRv;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, GroupListActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private List<Group> groupList = new ArrayList<>();
    private GroupAdapter groupAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_group_list;
    }

    @Override
    protected void initData() {

        groupList = FaceApi.getInstance().getGroupList(0, 1000);

        groupAdapter = new GroupAdapter(groupList);
        groupListRv.setAdapter(groupAdapter);

        groupListRv.setLayoutManager(new FullyLinearLayoutManager(this));
        groupListRv.setLayoutManager(new LinearLayoutManager(this));
        groupListRv.setItemAnimator(new DefaultItemAnimator());
        groupListRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        groupAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position < groupList.size()) {
                    Group group = groupList.get(position);
                    UserListActivity.newInstance(GroupListActivity.this, group.getGroupId());
                }
            }
        });
        groupAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (position < groupList.size()) {
                    showAlertDialog(position);
                }
                return false;
            }
        });
        groupAdapter.openLoadAnimation();
        groupAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
    }


    public void showAlertDialog(final int position) {

        final Group group = groupList.get(position);

        DialogUtils.showBasicDialog(this, "删除分组",
                "确认删除分组(" + group.getGroupId() + ")？删除分组将删除分组小所有的用户数据",
                "取消", "确定",
                new DialogUtils.OnNiftyDialogListener() {
                    @Override
                    public void onClickLeft() {

                    }

                    @Override
                    public void onClickRight() {
                        if (FaceApi.getInstance().groupDelete(group.getGroupId())) {
                            showToast("删除成功");
                            groupAdapter.remove(position);
                        }
                    }
                });

    }


    public class GroupAdapter extends BaseQuickAdapter<Group, BaseViewHolder> {

        public GroupAdapter(@Nullable List<Group> data) {
            super(R.layout.item_face_group_layout, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Group item) {
            helper.setText(R.id.group_id_tv, "Group ID：" + item.getGroupId());
        }
    }
}
