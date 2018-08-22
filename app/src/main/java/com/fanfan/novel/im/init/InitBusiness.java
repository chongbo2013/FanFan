package com.fanfan.novel.im.init;

import android.content.Context;

import com.fanfan.robot.app.common.Constants;
import com.seabreeze.log.Print;
import com.tencent.TIMConnListener;
import com.tencent.TIMLogLevel;
import com.tencent.TIMLogListener;
import com.tencent.TIMManager;

/**
 * Created by Administrator on 2017-06-21.
 */

public class InitBusiness {

    private static final String TAG = InitBusiness.class.getSimpleName();

    public static int IMSDK_APPID = 1400043768;

    public static int IMSDK_ACCOUNT_TYPE = 17967;

    private InitBusiness() {
    }

    public static void start(Context context) {
        initImsdk(context, TIMLogLevel.DEBUG.ordinal());
    }

    public static void start(Context context, int logLevel) {
        initImsdk(context, logLevel);
    }


    /**
     * 初始化SDK
     */
    private static void initImsdk(final Context context, int logLevel) {
        //初始化SDK基本配置
//        TIMSdkConfig config = new TIMSdkConfig(Constants.IMSDK_APPID)
//                .enableCrashReport(false)//设置是否开启bugly的crash上报功能， 必须在sdk初始化之前设置
//                .enableLogPrint(true)//设置是否把日志输出到控制台， 必须在sdk初始化之前设置
//                .setLogLevel(TIMLogLevel.values()[logLevel])//可以通过设置日志级别为TIMLogLevel.OFF来关闭ImSDK的文件日志输出，提升性能，建议在开发期间打开日志，方便排查问题
//                .setLogPath(Environment.getExternalStorageDirectory().getPath() + "/fangfangbig/Log/")
//                .setLogListener(new TIMLogListener() {
//                    @Override
//                    public void log(int i, String s, String s1) {
//                        //可以通过此回调将sdk的log输出到自己的日志系统中
//                    }
//                });


        TIMManager.getInstance().setConnectionListener(new TIMConnListener() {//连接监听器
            @Override
            public void onConnected() {//连接建立
                Print.e("connected");
            }

            @Override
            public void onDisconnected(int code, String desc) {//连接断开
                //接口返回了错误码code和错误描述desc，可用于定位连接断开原因
                //错误码code含义请参见错误码表
                Print.e("disconnected");
            }

            @Override
            public void onWifiNeedAuth(String s) {

            }
        });

        //禁用crash上报
        TIMManager.getInstance().disableCrashReport();
        TIMManager.getInstance().disableStorage();
        TIMManager.getInstance().setLogLevel(TIMLogLevel.values()[logLevel]);
        TIMManager.getInstance().initLogSettings(false, Constants.PRINT_TIMLOG_PATH);
        TIMManager.getInstance().setLogListener(new TIMLogListener() {
            @Override
            public void log(int level, String tag, String msg) {
                //可以通过此回调将sdk的log输出到自己的日志系统中
            }
        });
        //设置用户状态变更监听器，在回调中进行相应的处理
        TIMManager.getInstance().setUserStatusListener(StatusObservable.getInstance());
//        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(new Intent(Constants.EXIT_APP));

        TIMManager timManager = TIMManager.getInstance();
        timManager.init(context);
        Print.d("initIMsdk");

    }

}
