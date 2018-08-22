package com.fanfan.robot.ui.setting.act.face.local;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.api.FaceApi;
import com.baidu.aip.entity.Group;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGroupActivity extends BarBaseActivity {

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_group_et)
    EditText addGroupEt;
    @BindView(R.id.add_group_btn)
    Button addGroupBtn;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, AddGroupActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_add_group;
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.add_group_btn)
    public void onViewClicked() {
        String groupId = addGroupEt.getText().toString().trim();
        if (TextUtils.isEmpty(groupId)) {
            showToast("组名不能为空");
            return;
        }

        Pattern pattern = Pattern.compile("^[0-9a-zA-Z_-]{1,}$");
        Matcher matcher = pattern.matcher(groupId);
        if (!matcher.matches()) {
            showToast("groupId、字母、下划线中的一个或者多个组合");
            return;
        }

        Group group = new Group();
        group.setGroupId(groupId);
        boolean ret = FaceApi.getInstance().groupAdd(group);

        showToast("添加" + (ret ? "成功" : "失败"));
        finish();
    }
}
