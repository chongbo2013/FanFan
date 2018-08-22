package com.fanfan.robot.ui.setting.act.face.local;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.aip.api.FaceApi;
import com.baidu.aip.entity.Group;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserGroupManagerActivity extends BarBaseActivity {


    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_group_btn)
    Button viewGroupBtn;
    @BindView(R.id.add_group_btn)
    Button addGroupBtn;
    @BindView(R.id.user_reg_btn)
    Button userRegBtn;
    @BindView(R.id.batch_import_btn)
    Button batchImportBtn;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, UserGroupManagerActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_user_group_manager;
    }

    @Override
    protected void initData() {
        batchImportBtn.setVisibility(View.GONE);
    }


    @OnClick({R.id.view_group_btn, R.id.add_group_btn, R.id.user_reg_btn, R.id.batch_import_btn})
    public void onViewClicked(View view) {
        List<Group> groupList = FaceApi.getInstance().getGroupList(0, 1000);
        switch (view.getId()) {
            case R.id.view_group_btn:
                if (groupList.size() <= 0) {
                    showToast("还没有分组，请创建分组并添加用户");
                    return;
                }
                GroupListActivity.newInstance(this);
                break;
            case R.id.add_group_btn:
                AddGroupActivity.newInstance(this);
                break;
            case R.id.user_reg_btn:
                if (groupList.size() == 0) {
                    showToast("请先添加分组");
                    return;
                }
                RegActivity.newInstance(this);
                break;
            case R.id.batch_import_btn:
                break;
        }
    }
}
