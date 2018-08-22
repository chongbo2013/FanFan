package com.fanfan.robot.ui.face.aip;

import android.app.Activity;
import android.content.Intent;

import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;

public class AddGroupActivity extends BarBaseActivity {


    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, AddGroupActivity.class);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }



    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initData() {

    }
}
