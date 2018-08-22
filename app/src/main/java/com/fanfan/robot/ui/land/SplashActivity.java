package com.fanfan.robot.ui.land;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.app.common.base.BaseHandler;
import com.fanfan.novel.im.MyTLSService;
import com.fanfan.novel.im.PushUtil;
import com.fanfan.novel.im.event.FriendshipEvent;
import com.fanfan.novel.im.event.GroupEvent;
import com.fanfan.novel.im.event.MessageEvent;
import com.fanfan.novel.im.event.RefreshEvent;
import com.fanfan.novel.im.init.InitBusiness;
import com.fanfan.novel.im.init.LoginBusiness;
import com.fanfan.novel.im.init.TlsBusiness;
import com.fanfan.robot.model.UserInfo;
import com.fanfan.robot.presenter.SplashPresenter;
import com.fanfan.robot.presenter.SplashView;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.service.UdpService;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.system.PhoneUtil;
import com.fanfan.novel.utils.permiss.HiPermission;
import com.fanfan.novel.utils.permiss.PermissionCallback;
import com.fanfan.robot.R;
import com.fanfan.robot.ui.main.MainActivity;
import com.seabreeze.log.Print;
import com.tencent.TIMCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;

/**
 * 闪屏
 */
public class SplashActivity extends BarBaseActivity implements SplashView, BaseHandler.HandleMessage, TIMCallBack {


    private SplashPresenter presenter;

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    //权限
    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案

    private static final int OVERLAY_PERMISSION_REQ_CODE = 600;

    //登陆
    public static final int LOGIN_RESULT_CODE = 570;
    public static final int LOGIN_RESULTCODE = 571;
    public static final int BACK_RESULTCODE = 572;
    //handler
    private static final int REFRESH_COMPLETE = 0X153;

    //标志位确定SDK是否初始化，避免客户SDK未初始化的情况，实现可重入的init操作
    private static boolean isSDKInit = false;

    private Handler handler = new BaseHandler<>(SplashActivity.this);

    @Override
    protected boolean whetherNotReturn() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

        ImageView ivSplash = (ImageView) findViewById(R.id.iv_splash);

        presenter = new SplashPresenter(this);

        ImageLoader.loadImage(this, ivSplash, R.mipmap.splash_bg, false, 500);

        @SuppressLint("WrongConstant") WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        Constants.displayWidth = wm.getDefaultDisplay().getWidth();
        Constants.displayHeight = wm.getDefaultDisplay().getHeight();
        Constants.IP = PhoneUtil.getWifiIP(this);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        Print.e("屏幕宽度 width : " + width);
        Print.e("屏幕高度 height : " + height);
        Print.e("屏幕密度 density : " + density);
        Print.e("屏幕密度DPI densityDpi : " + densityDpi);

    }


    @Override
    protected void initData() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                showToast("当前需开启悬浮框权限，请授权！");
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                checkPermissions();
            }
        } else {
            checkPermissions();
        }
    }

    private void checkPermissions() {
        HiPermission.create(this)
                .checkMutiPermission(PERMISSIONS, new PermissionCallback() {
                    @Override
                    public void onClose() {
                        showMissingPermissionDialog();
                    }

                    @Override
                    public void onFinish() {
                        initIM();
                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        Print.e("onDeny");
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        Print.e("onGuarantee");
                    }
                });

    }

    @Override
    protected void setListener() {

    }

    private void initIM() {
        init();

        presenter = new SplashPresenter(this);
        presenter.start();
    }

    private void init() {
        if (isSDKInit)
            return;
        //初始化IMSDK
        InitBusiness.start(getApplicationContext());
        //初始化TLS
        TlsBusiness.init(getApplicationContext());

        ILiveSDK.getInstance().initSdk(getApplicationContext(), Constants.IMSDK_APPID, Constants.IMSDK_ACCOUNT_TYPE);

        isSDKInit = true;
    }


    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
            }
        });

        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RESULT_CODE) {
            if (resultCode == LOGIN_RESULTCODE) {//判断登陆是否成功
                if (0 == MyTLSService.getInstance().getLastErrno()) {
                    navToHome();
                } else if (resultCode == RESULT_CANCELED) {
                    handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
                }
            } else if (resultCode == BACK_RESULTCODE) {
                handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
            }
        } else if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                showToast("授权后请重启应用，否则无法开启悬浮窗");
            } else {
                showToast("权限授予成功！");
                checkPermissions();
            }
        }
    }


    @Override
    public void navToHome() {
//        TIMUserConfig userConfig = TIMUserConfigSetting.setTIMUserConfig(this);
//        TIMManager.getInstance().setUserConfig(userConfig);
        RefreshEvent.getInstance().init();
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
        RefreshEvent.getInstance().init();

        LoginBusiness.loginIm(UserInfo.getInstance().getIdentifier(), UserInfo.getInstance().getUserSig(), this);
    }


    @Override
    public void navToLogin() {
        LoginActivity.newInstance(this, LOGIN_RESULT_CODE);
    }

    @Override
    public boolean isUserLogin() {
        String id = UserInfo.getInstance().getIdentifier();

        boolean isNeed = MyTLSService.getInstance().needLogin(id);

        boolean islogin = id != null && (!isNeed);
        return islogin;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case REFRESH_COMPLETE:
                finish();
                break;
        }
    }


    @Override
    public void onError(int i, String s) {
        Print.e("login error : code " + i + " " + s);
        switch (i) {
            case 6208:
                //离线状态下被其他终端踢下线
                showKickOutDialog(this);
                break;
            case 6200:
                Print.e("登陆失败， 当前没有网络");
                navToLogin();
                break;
            default:
                Print.e("登陆失败， 请稍后重试");
                navToLogin();
                break;
        }
    }

    @Override
    public void onSuccess() {
        loginSDK();
    }

    private void loginSDK() {
        ILiveLoginManager.getInstance().iLiveLogin(UserInfo.getInstance().getIdentifier(), UserInfo.getInstance().getUserSig(), new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Print.e("完成登陆 onSuccess");
                //初始化程序后台后消息推送
                PushUtil.getInstance();
                //初始化消息监听
                MessageEvent.getInstance();

                RobotInfo.getInstance().setLoginTime(System.currentTimeMillis());

                startService(new Intent(SplashActivity.this, UdpService.class));
                startService(new Intent(SplashActivity.this, SerialService.class));
                Intent intent;
//                if (Constants.isTrain) {
//                    intent = new Intent(SplashActivity.this, MainNewActivity.class);
//                } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
//                }
                startActivity(intent);

                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Print.e("login failed:" + module + "|" + errCode + "|" + errMsg);
                Print.e("登陆失败， 请稍后重试");
                navToLogin();
            }
        });
    }


    /**
     * 显示被踢下线通知
     *
     * @param context activity
     */
    private void showKickOutDialog(final Context context) {
        DialogUtils.showBasicIconDialog(SplashActivity.this, R.mipmap.ic_logo, "登陆提示",
                "您的帐号已在其它地方登陆", "退出", "重新登陆",
                new DialogUtils.OnNiftyDialogListener() {
                    @Override
                    public void onClickLeft() {
                        LoginBusiness.logout(new TIMCallBack() {
                            @Override
                            public void onError(int i, String s) {
                                if (SplashActivity.this != null) {
                                    Print.e("退出登录失败，请稍后重试");
                                }
                            }

                            @Override
                            public void onSuccess() {
                                sendBroadcast(new Intent(Constants.NET_LOONGGG_EXITAPP));
                            }
                        });
                    }

                    @Override
                    public void onClickRight() {
                        navToHome();
                    }
                });
    }

}
