package com.fanfan.robot.ui.setting.act.other;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.fanfan.novel.utils.system.PreferencesUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class GreetingActivity extends BarBaseActivity {

    public static final String GREETING1 = "greeting1";
    public static final String GREETING2 = "greeting2";
    public static final String GREETING3 = "greeting3";
    public static final String GREETING_STATE = "GREETING_STATE";

    @BindView(R.id.editText1)
    EditText editText1;
    @BindView(R.id.editText2)
    EditText editText2;
    @BindView(R.id.editText3)
    EditText editText3;
    @BindView(R.id.tb_switch)
    ToggleButton tbSwitch;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, GreetingActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_greeting;
    }

    @Override
    protected void initData() {
        editText1.setText(PreferencesUtils.getString(this, GREETING1, ""));
        editText2.setText(PreferencesUtils.getString(this, GREETING2, ""));
        editText3.setText(PreferencesUtils.getString(this, GREETING3, ""));

        tbSwitch.setChecked(PreferencesUtils.getBoolean(this, GREETING_STATE));
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
                PreferencesUtils.putString(this, GREETING1, editText1.getText().toString());
                PreferencesUtils.putString(this, GREETING2, editText2.getText().toString());
                PreferencesUtils.putString(this, GREETING3, editText3.getText().toString());
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.tb_switch)
    public void onViewClicked() {
        if (tbSwitch.isChecked()) {
            PreferencesUtils.putBoolean(this, GREETING_STATE, true);
        } else {
            PreferencesUtils.putBoolean(this, GREETING_STATE, false);
        }
    }
}
