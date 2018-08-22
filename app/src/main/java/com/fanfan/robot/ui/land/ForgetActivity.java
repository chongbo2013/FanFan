package com.fanfan.robot.ui.land;

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

import com.fanfan.robot.R;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.model.UserInfo;
import com.fanfan.robot.presenter.ForgetPresenter;
import com.fanfan.robot.presenter.ipersenter.IForgetPresenter;
import com.seabreeze.log.Print;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 修改密码
 */
public class ForgetActivity extends BarBaseActivity implements IForgetPresenter.IForgetView {

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, ForgetActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @BindView(R.id.usernameWrapper)
    TextInputLayout usernameWrapper;
    @BindView(R.id.passwordWrapper)
    TextInputLayout passwordWrapper;
    @BindView(R.id.determine)
    Button determine;
    @BindView(R.id.span1)
    View span1;
    @BindView(R.id.span2)
    View span2;

    private ForgetPresenter mForgetPresenter;

    @Override
    protected boolean whetherNotReturn() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget;
    }

    @Override
    protected void initView() {
        super.initView();
        mForgetPresenter = new ForgetPresenter(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerKeyboardListener();
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
                String username = usernameWrapper.getEditText().getText().toString();
                String password = passwordWrapper.getEditText().getText().toString();
                if (username.isEmpty()) {
                    usernameWrapper.setError("账号不能为空!");
                } else if (!validateEmail(username)) {
                    usernameWrapper.setError("请输入6~8位账号!");
                } else if (password.isEmpty()) {
                    usernameWrapper.setError("密码不能为空!");
                } else if (!validatePassword(password)) {
                    passwordWrapper.setError("密码太短!");
                } else {
                    usernameWrapper.setErrorEnabled(false);
                    passwordWrapper.setErrorEnabled(false);
                    doModify(username, password);
                }

                break;
        }
    }

    private boolean validateEmail(String username) {
        if (username.length() > 5 && username.length() < 9) {
            return true;
        }
        return false;
    }

    public boolean validatePassword(String password) {
        return password.length() > 5;
    }


    private void doModify(String username, String password) {
        UserInfo.getInstance().setIdentifier(username);
        UserInfo.getInstance().setUserPass(password);
        mForgetPresenter.doModify(UserInfo.getInstance());
    }


    private void registerKeyboardListener() {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Print.e("onGlobalLayout");
                if (isKeyboardShown(rootView)) {
                    Print.e("软键盘弹起");
                    span1.setVisibility(View.GONE);
                    span2.setVisibility(View.GONE);
                } else {
                    Print.e("软键盘未弹起");
                    span1.setVisibility(View.INVISIBLE);
                    span2.setVisibility(View.INVISIBLE);
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


    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    @Override
    public void showMsg(int msg) {
        showToast(msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void modifySuccess() {

    }

    @Override
    public void modifyFail(String errMsg) {

    }
}
