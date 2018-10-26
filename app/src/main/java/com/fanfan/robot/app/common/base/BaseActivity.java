package com.fanfan.robot.app.common.base;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fanfan.novel.im.event.MessageEvent;
import com.fanfan.novel.im.init.StatusObservable;
import com.fanfan.novel.im.init.TlsBusiness;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.novel.utils.system.PhoneUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.model.UserInfo;
import com.fanfan.robot.other.cache.UserInfoCache;
import com.fanfan.robot.presenter.ScreenPresenter;
import com.fanfan.robot.presenter.ipersenter.IScreenPresenter;
import com.fanfan.robot.service.CallSerivice;
import com.fanfan.robot.service.ScreenService;
import com.fanfan.robot.ui.auxiliary.LockActivity;
import com.fanfan.robot.ui.call.SimpleCallActivity;
import com.fanfan.robot.ui.land.SplashActivity;
import com.fanfan.robot.ui.main.MainActivity;
import com.seabreeze.log.Print;
import com.tencent.TIMUserStatusListener;
import com.tencent.callsdk.ILVCallConfig;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVIncomingListener;
import com.tencent.callsdk.ILVIncomingNotification;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static com.fanfan.robot.app.common.Constants.unusual;


public abstract class BaseActivity extends AppCompatActivity implements
        IScreenPresenter.ISreenView,
        ILVIncomingListener,
        TIMUserStatusListener {

    protected Context mContext;
    protected Handler mHandler = new Handler();
    protected Handler mainHandler = new Handler();

    protected RelativeLayout backdrop;
    protected Toolbar toolbar;

//    private ScreenPresenter mScreenPresenter;

    private static OnPermissionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCollector.addActivity(this);
        setRequestedOrientation(getOrientation());
        super.onCreate(savedInstanceState);
        BaseApplication.addActivity(this);
        setBeforeLayout();
        mContext = this;
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        ButterKnife.bind(this);

        StatusObservable.getInstance().addObserver(this);

        ILVCallManager.getInstance().init(new ILVCallConfig()
                .setAutoBusy(true));
        ILVCallManager.getInstance().addIncomingListener(this);//添加来电回调

        initView();
        initDb();
        initData();
        setListener();

//        if (unusual) {
//            Intent intent = new Intent(this, ScreenService.class);
//            startService(intent);
//        } else {
//            mScreenPresenter = new ScreenPresenter(this);
//        }
    }


    public int getOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    protected void setBeforeLayout() {
        PhoneUtil.getDispaly(this);
    }

    /**
     * 返回当前界面布局文件
     */
    protected abstract int getLayoutId();

    /**
     * 此方法描述的是： 初始化所有view
     */
    protected void initView() {
        backdrop();
        toolbar();
    }

    protected abstract int setBackgroundGlide();

    /**
     * 初始化数据库
     */
    protected void initDb() {

    }

    /**
     * 此方法描述的是： 初始化所有数据的方法
     */
    protected abstract void initData();


    /**
     * 此方法描述的是： 设置所有事件监听
     */
    protected void setListener() {

    }

    /**
     * @return true 不返回
     */
    protected abstract boolean whetherNotReturn();


    @Override
    protected void onResume() {
        super.onResume();

        postInteraction();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.NET_LOONGGG_EXITAPP);
        this.registerReceiver(this.finishAppReceiver, filter);

//        if (mScreenPresenter != null) {
//            mScreenPresenter.startTipsTimer();
//        }
    }

    /**
     * 显示toast
     *
     * @param resId
     */
    public void showToast(final int resId) {
        showToast(getString(resId));
    }

    /**
     * 显示toast
     *
     * @param resStr
     * @return Toast对象，便于控制toast的显示与关闭
     */
    public void showToast(final String resStr) {

        if (TextUtils.isEmpty(resStr)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(BaseActivity.this, resStr, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        removeToMain();

//        if (mScreenPresenter != null) {
//            mScreenPresenter.endTipsTimer();
//        }
    }

    @Override
    protected void onDestroy() {

        removeToMain();

        super.onDestroy();

        ILVCallManager.getInstance().removeIncomingListener(this);
        StatusObservable.getInstance().deleteObserver(this);
        this.unregisterReceiver(this.finishAppReceiver);
        BaseApplication.removeActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract boolean setResult();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i]);
                }
            }

            if (deniedPermissions.isEmpty()) {
                if (callback != null) {
                    callback.onGranted();
                }
            } else {
                if (callback != null) {
                    callback.onDenied(deniedPermissions);
                }
            }

        }
    }

    public static void requestPermission(String[] permissions, OnPermissionCallback onPermissionCallback) {
        if (BaseApplication.getTopAcitivity() == null) {
            return;
        }
        callback = onPermissionCallback;
        List<String> permissionsList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(BaseApplication.getTopAcitivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (!permissionsList.isEmpty()) {
            ActivityCompat.requestPermissions(BaseApplication.getTopAcitivity(), permissionsList.toArray(new String[permissionsList.size()]), 1);
        } else {
            if (callback != null) {
                callback.onGranted();
            }
        }
    }

    /**
     * 关闭Activity的广播，放在自定义的基类中，让其他的Activity继承这个Activity就行
     */
    protected BroadcastReceiver finishAppReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private void backdrop() {
        backdrop = findViewById(R.id.backdrop);
        if (backdrop != null) {
            if (setBackgroundGlide() != 0) {
                Glide.with(this)
                        .asBitmap()
                        .load(setBackgroundGlide())
                        .into(new SimpleTarget<Bitmap>(Constants.displayWidth / 2, Constants.displayHeight / 2) {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                Drawable drawable = new BitmapDrawable(resource);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    backdrop.setBackground(drawable);
                                }
                            }
                        });
            }
        }
    }

    private void toolbar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle("");
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ActivityCollector.finishActivity(this);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
//        if (mScreenPresenter != null) {
//            mScreenPresenter.resetTipsTimer();
//        }
        postInteraction();
    }


    public static final int BACK_MAIN_DELAY_MILLIS = 300 * 1000;

    private void removeToMain() {
        if (!whetherNotReturn()) {
            mainHandler.removeCallbacks(toMainRunnable);
        }
    }

    private void postToMain() {
        if (!whetherNotReturn()) {
            mainHandler.postDelayed(toMainRunnable, BACK_MAIN_DELAY_MILLIS);
        }
    }

    protected void postInteraction() {

        removeToMain();
        postToMain();
    }


    Runnable toMainRunnable = new Runnable() {
        @Override
        public void run() {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);

            String className = info.topActivity.getClassName();
            if (className.equals("com.fanfan.robot.ui.main.MainActivity")) {
                Print.e("当前页面");
            } else {
                Print.e("跳转到main");
                startActivity(new Intent(BaseActivity.this, MainActivity.class));
            }
        }
    };

    @Override
    public void showTipsView() {
        if (mContext instanceof MainActivity) {
//            LockActivity.newInstance(this);
        } else {
            finish();
        }
    }

    public interface OnPermissionCallback {
        void onGranted();

        void onDenied(List<String> deniedPermissions);
    }

    @Override
    public void onNewIncomingCall(final int callId, final int callType, final ILVIncomingNotification notification) {
        Print.e("视频来电 新的来电 : " + notification);
        callStop();
        if (Constants.isCall) {
            Intent intent = new Intent(this, CallSerivice.class);
            intent.putExtra(CallSerivice.CALL_ID, callId);
            intent.putExtra(CallSerivice.CALL_TYPE, callType);
            intent.putExtra(CallSerivice.SENDER, notification.getSender());
            startService(intent);
        } else {
            SimpleCallActivity.newInstance(this, callId, callType, notification.getSender());
        }
    }

    protected void callStop() {

    }

    @Override
    public void onForceOffline() {
        DialogUtils.showBasicIconDialog(BaseActivity.this, R.mipmap.ic_logo, "登陆提示",
                "您的帐号已在其它地方登陆", "退出", "重新登陆",
                new DialogUtils.OnNiftyDialogListener() {
                    @Override
                    public void onClickLeft() {
                        LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(new Intent(Constants.NET_LOONGGG_EXITAPP));
                    }

                    @Override
                    public void onClickRight() {
                        logout();
                    }
                });
    }

    @Override
    public void onUserSigExpired() {

    }


    public void logout() {
        TlsBusiness.logout(UserInfo.getInstance().getIdentifier());
        UserInfoCache.clearCache(this);
        UserInfo.getInstance().setIdentifier(null);
        MessageEvent.getInstance().clear();
//        FriendshipInfo.getInstance().clear();
//        GroupInfo.getInstance().clear();
        Intent intent = new Intent(BaseActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }


}
