package com.fanfan.robot.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.fanfan.robot.app.common.Constants;
import com.seabreeze.log.Print;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallOption;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.view.AVRootView;

/**
 * Created by android on 2018/2/26.
 */

public class CallSerivice extends Service implements ILVCallListener {

    private WindowManager windowManager;
    private WindowManager.LayoutParams wmParams;
    private LinearLayout linearLayout;

    private String hostId;
    private int callId;
    private int callType;

    public static final String CALL_ID = "call_id";
    public static final String CALL_TYPE = "call_type";
    public static final String SENDER = "sender";
    public static final String CALLNUMBERS = "callnumbers";

    private ILVCallOption ilvCallOption;

    private AVRootView avRootView;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();

        // 设置悬浮窗体属性
        // 1.得到WindoeManager对象：
        windowManager = (WindowManager) getApplicationContext().getSystemService("window");
        // 2.得到WindowManager.LayoutParams对象，为后续设置相关参数做准备：
        wmParams = new WindowManager.LayoutParams();
        // 3.设置相关的窗口布局参数，要实现悬浮窗口效果，要需要设置的参数有
        // 3.1设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 3.2设置图片格式，效果为背景透明 //wmParams.format = PixelFormat.RGBA_8888;
        wmParams.format = 1;
        // 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 4.// 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 5. 调整悬浮窗口至中间
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER;
        // 6. 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        // 7.将需要加到悬浮窗口中的View加入到窗口中了：
        // 如果view没有被加入到某个父组件中，则加入WindowManager中

        avRootView = new AVRootView(this);

        WindowManager.LayoutParams params_sur = new WindowManager.LayoutParams();
        params_sur.width = 1;
        params_sur.height = 1;
        params_sur.alpha = 255;
        avRootView.setLayoutParams(params_sur);

        linearLayout = new LinearLayout(this);
        WindowManager.LayoutParams params_rel = new WindowManager.LayoutParams();
        params_rel.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params_rel.height = WindowManager.LayoutParams.WRAP_CONTENT;
        linearLayout.setLayoutParams(params_rel);
        linearLayout.addView(avRootView);
        windowManager.addView(linearLayout, wmParams); // 创建View
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        hostId = intent.getStringExtra(SENDER);
        callId = intent.getIntExtra(CALL_ID, 0);
        callType = intent.getIntExtra(CALL_TYPE, ILVCallConstants.CALL_TYPE_VIDEO);

        ILVCallManager.getInstance().addCallListener(this);

        ilvCallOption = new ILVCallOption(hostId)
                .callTips("呼叫标题")
                .setCallType(callType);

        ILVCallManager.getInstance().initAvView(avRootView);
        if(Constants.unusual){
            avRootView.setRemoteRotationFix(180);
            avRootView.setLocalRotationFix(180);
        }else{
            avRootView.setRemoteRotationFix(90);
            avRootView.setLocalRotationFix(90);
        }
        ILVCallManager.getInstance().acceptCall(callId, ilvCallOption);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        ILVCallManager.getInstance().endCall(callId);
        ILVCallManager.getInstance().removeCallListener(this);
        ILVCallManager.getInstance().onPause();
        ILVCallManager.getInstance().onDestory();
        windowManager.removeView(linearLayout);
        super.onDestroy();
    }

    @Override
    public void onCallEstablish(int callId) {
        ILVCallManager.getInstance().enableCamera(ILiveConstants.FRONT_CAMERA, true);
//        ILVCallManager.getInstance().switchCamera(ILiveConstants.FRONT_CAMERA);
        ILVCallManager.getInstance().enableMic(true);
        ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_SPEAKER);
        ILiveSDK.getInstance().getAvVideoCtrl().inputBeautyParam(0.0f);
    }


    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        stopSelf();
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {
        Print.e("onExceptiononExceptiononException");
    }

}
