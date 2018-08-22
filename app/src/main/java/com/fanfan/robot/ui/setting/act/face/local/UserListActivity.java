package com.fanfan.robot.ui.setting.act.face.local;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.baidu.aip.api.FaceApi;
import com.baidu.aip.entity.Group;
import com.baidu.aip.entity.User;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.view.manager.FullyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListActivity extends BarBaseActivity {

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_list_rv)
    RecyclerView userListRv;


    public static void newInstance(Activity context, String groupId) {
        Intent intent = new Intent(context, UserListActivity.class);
        intent.putExtra("group_id", groupId);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private String groupId = "";

    private List<User> userList = new ArrayList<>();
    private UserAdapter userAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_user_list;
    }

    @Override
    protected void initData() {

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        groupId = intent.getStringExtra("group_id");
        userList = FaceApi.getInstance().getUserList(groupId);

        userAdapter = new UserAdapter(userList);
        userListRv.setAdapter(userAdapter);

        userListRv.setLayoutManager(new FullyLinearLayoutManager(this));
        userListRv.setLayoutManager(new LinearLayoutManager(this));
        userListRv.setItemAnimator(new DefaultItemAnimator());
        userListRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        userAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position < userList.size()) {
                    UserActivity.newInstance(UserListActivity.this, userList.get(position));
                }
            }
        });
        userAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (position <= userList.size()) {
                    showAlertDialog(position);
                }
                return false;
            }
        });
        userAdapter.openLoadAnimation();
        userAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
    }


    public void showAlertDialog(final int position) {
        final User user = userList.get(position);

        DialogUtils.showBasicDialog(this, "删除用户",
                "确认删除该用户？删除该用户将删除用户下所有的用户数据",
                "取消", "确定",
                new DialogUtils.OnNiftyDialogListener() {
                    @Override
                    public void onClickLeft() {

                    }

                    @Override
                    public void onClickRight() {
                        if (FaceApi.getInstance().userDelete(user.getUserId(), user.getGroupId())) {
                            showToast("删除成功");
                            userAdapter.remove(position);
                        }
                    }
                });

    }


    public class UserAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

        public UserAdapter(@Nullable List<User> data) {
            super(R.layout.item_face_user_layout, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, User item) {
            helper.setText(R.id.user_id_tv, "User ID: " + item.getUserId());
            helper.setText(R.id.user_info_tv, "Userinfo：" + item.getUserInfo());
        }
    }
}
