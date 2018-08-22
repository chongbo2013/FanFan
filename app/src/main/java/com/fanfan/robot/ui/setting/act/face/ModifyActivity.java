package com.fanfan.robot.ui.setting.act.face;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.R;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.face.bean.PersonModify;
import com.fanfan.youtu.api.face.event.PersonModifyEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * Created by zhangyuanyuan on 2017/10/16.
 */

public class ModifyActivity extends BarBaseActivity {

    public static final int MODIFY_NAME_CODE = 150;
    public static final int MODIFY_TAG_CODE = 151;

    @BindView(R.id.et_modify)
    EditText etModify;

    private String userInfoId;
    private int requestCode;
    private String etTxt;

    public static void navToModify(Activity activity, String userInfoId, int requestCode, String etTxt) {
        Intent intent = new Intent(activity, ModifyActivity.class);
        intent.putExtra("userInfoId", userInfoId);
        intent.putExtra("code", requestCode);
        intent.putExtra("etTxt", etTxt);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private Youtucode youtucode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify;
    }

    @Override
    protected void initView() {
        super.initView();
        userInfoId = getIntent().getStringExtra("userInfoId");
        requestCode = getIntent().getIntExtra("code", -1);
        etTxt = getIntent().getStringExtra("etTxt");

        if (!etTxt.equals("请设置")) {
            etModify.setText(etTxt);
        }

        youtucode = Youtucode.getSingleInstance();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.finish_white, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish:
                String etMod = etModify.getText().toString();
                if (etMod == null || etMod.length() == 0 || etMod.equals(etTxt)) {
                    Print.i("etMood 格式不符");
                    break;
                }
                if (requestCode == MODIFY_NAME_CODE) {
                    youtucode.modifyPersonName(userInfoId, etMod);
                } else if (requestCode == MODIFY_TAG_CODE) {
                    youtucode.modifyPersonTag(userInfoId, etMod);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(PersonModifyEvent event) {
        if (event.isOk()) {
            PersonModify personModify = event.getBean();
            Print.e(personModify);
            if (personModify.getErrorcode() == 0) {
                Intent intent = new Intent();
                intent.putExtra("etModify", etModify.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            } else {
                onError(personModify.getErrorcode(), personModify.getErrormsg());
            }
        } else {
            onError(event);
        }
    }

}
