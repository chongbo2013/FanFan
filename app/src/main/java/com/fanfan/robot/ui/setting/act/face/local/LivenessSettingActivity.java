package com.fanfan.robot.ui.setting.act.face.local;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.aip.utils.PreferencesUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LivenessSettingActivity extends BarBaseActivity {


    public static final int TYPE_NO_LIVENSS = 1;
    public static final int TYPE_RGB_LIVENSS = 2;
    public static final int TYPE_RGB_IR_LIVENSS = 3;
    public static final int TYPE_RGB_DEPTH_LIVENSS = 4;
    public static final int TYPE_RGB_IR_DEPTH_LIVENSS = 5;
    public static final String TYPE_LIVENSS = "TYPE_LIVENSS";


    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.no_liveness_rb)
    RadioButton noLivenessRb;
    @BindView(R.id.rgb_liveness_rb)
    RadioButton rgbLivenessRb;
    @BindView(R.id.rgb_ir_liveness_rb)
    RadioButton rgbIrLivenessRb;
    @BindView(R.id.rgb_depth_liveness_rb)
    RadioButton rgbDepthLivenessRb;
    @BindView(R.id.rgb_ir_depth_liveness_rb)
    RadioButton rgbIrDepthLivenessRb;
    @BindView(R.id.liveness_rg)
    RadioGroup livenessRg;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, LivenessSettingActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_liveness_setting;
    }

    @Override
    protected void initData() {

        int livenessType = PreferencesUtil.getInt(TYPE_LIVENSS, TYPE_NO_LIVENSS);

        if (livenessType == TYPE_NO_LIVENSS) {
            noLivenessRb.setChecked(true);
        } else if (livenessType == TYPE_RGB_LIVENSS) {
            rgbLivenessRb.setChecked(true);
        } else if (livenessType == TYPE_RGB_DEPTH_LIVENSS) {
            rgbDepthLivenessRb.setChecked(true);
        } else if (livenessType == TYPE_RGB_IR_LIVENSS) {
            rgbIrLivenessRb.setChecked(true);
        } else if (livenessType == TYPE_RGB_IR_DEPTH_LIVENSS) {
            rgbIrDepthLivenessRb.setChecked(true);
        }
    }

    @Override
    protected void setListener() {
        livenessRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                switch (checkedId) {
                    case R.id.no_liveness_rb:
                        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_NO_LIVENSS);
                        break;
                    case R.id.rgb_liveness_rb:
                        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_LIVENSS);
                        break;
                    case R.id.rgb_ir_liveness_rb:
                        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_IR_LIVENSS);
                        break;
                    case R.id.rgb_depth_liveness_rb:
                        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_DEPTH_LIVENSS);
                        break;
                    case R.id.rgb_ir_depth_liveness_rb:
                        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_IR_DEPTH_LIVENSS);
                        break;
                }
            }
        });
    }
}
