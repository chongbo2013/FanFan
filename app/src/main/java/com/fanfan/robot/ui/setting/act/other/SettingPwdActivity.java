package com.fanfan.robot.ui.setting.act.other;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.design.widget.TextInputLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.model.UserInfo;
import com.fanfan.robot.R;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.hfrobot.bean.RobotMsg;
import com.fanfan.youtu.api.hfrobot.event.AddSetEvent;
import com.fanfan.youtu.api.hfrobot.event.UpdateSetEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置setting密码
 */
public class SettingPwdActivity extends BarBaseActivity {

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, SettingPwdActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @BindView(R.id.set_pwdWrapper)
    TextInputLayout setpwdWrapper;
    @BindView(R.id.determine)
    Button determine;
    @BindView(R.id.span1)
    View span1;
    @BindView(R.id.span2)
    View span2;

    private String setpwd;
    private Youtucode youtucode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_pwd;
    }

    @Override
    protected void initData() {
        youtucode = Youtucode.getSingleInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerKeyboardListener();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterKeyboardListener();
    }

    @OnClick({R.id.determine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.determine:
                hideKeyboard();
                setpwd = setpwdWrapper.getEditText().getText().toString();
                if (setpwd.isEmpty()) {
                    setpwdWrapper.setError("密码不能为空!");
                } else if (!validateSetpwd(setpwd)) {
                    setpwdWrapper.setError("请输入6~10位密码!");
                } else {
                    setpwdWrapper.setErrorEnabled(false);
                    youtucode.addSet(UserInfo.getInstance().getIdentifier(), setpwd);
                }
                break;
        }
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(AddSetEvent event) {
        if (event.isOk()) {
            RobotMsg bean = event.getBean();
            Print.e(bean);
            if (bean.getCode() == 0) {
                showToast("修改成功");
                finish();
            } else if (bean.getCode() == 1) {
                showToast("修改失败");
            } else if (bean.getCode() == 2) {
                youtucode.updateSet(UserInfo.getInstance().getIdentifier(), setpwd);
            } else {
                onError(bean.getCode(), bean.getMsg());
            }
        } else {
            onError(event);
        }
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(UpdateSetEvent event) {
        if (event.isOk()) {
            RobotMsg bean = event.getBean();
            Print.e(bean);
            if (bean.getCode() == 0) {
                showToast("修改成功");
                finish();
            } else if (bean.getCode() == 1) {
                showToast("修改失败");
            } else {
                onError(bean.getCode(), bean.getMsg());
            }
        } else {
            onError(event);
        }
    }

    private boolean validateSetpwd(String username) {
        if (username.length() > 5 && username.length() < 11) {
            return true;
        }
        return false;
    }


    private void registerKeyboardListener() {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Print.e("onGlobalLayout");
                if (isKeyboardShown(rootView)) {
                    Print.e("软键盘弹起");
//                    span1.setVisibility(View.GONE);
//                    span2.setVisibility(View.GONE);
                } else {
                    Print.e("软键盘未弹起");
//                    span1.setVisibility(View.INVISIBLE);
//                    span2.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void unRegisterKeyboardListener() {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(null);
    }

    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
