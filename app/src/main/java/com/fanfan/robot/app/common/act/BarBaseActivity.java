package com.fanfan.robot.app.common.act;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanfan.robot.R;
import com.fanfan.robot.app.common.base.BaseActivity;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.utils.ErrorMsg;
import com.seabreeze.log.Print;

import java.util.Random;

/**
 * Created by zhangyuanyuan on 2017/12/15.
 */

public abstract class BarBaseActivity extends BaseActivity {

    @Override
    protected boolean whetherNotReturn() {
        return false;
    }

    @Override
    protected int setBackgroundGlide() {
        return 0;
    }

    @Override
    protected boolean setResult() {
        return false;
    }

    public void onError(int code, String msg) {
        Print.e("onError  code : " + code + " ; msg : " + msg + " ; describe : " + ErrorMsg.getCodeDescribe(code));
    }

    public void onError(BaseEvent event) {
        Print.e("onError : " + event.getCode() + "  " + event.getCodeDescribe());
    }

    protected void stopAll() {

    }

    protected void isEmpty() {
        RelativeLayout rlLayout = findViewById(R.id.rl_empty);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        if (rlLayout != null && tvEmpty != null) {
            rlLayout.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    protected void isNuEmpty() {
        RelativeLayout rlLayout = findViewById(R.id.rl_empty);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        if (rlLayout != null && tvEmpty != null) {
            rlLayout.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    public String resFoFinal(int id) {
        String[] arrResult = getResources().getStringArray(id);
        return arrResult[new Random().nextInt(arrResult.length)];
    }

}
