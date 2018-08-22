package com.baidu.aip.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.aip.ui.Activation;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.PreferencesUtil;
import com.baidu.idl.facesdk.FaceConfig;
import com.baidu.idl.facesdk.FaceSDK;
import com.baidu.idl.license.AndroidLicenser;

public class FaceSDKManager {


    public static final int SDK_UNACTIVATION = 1;
    public static final int SDK_UNINIT = 2;
    public static final int SDK_INITING = 3;
    public static final int SDK_INITED = 4;
    public static final int SDK_FAIL = 5;

    public static final String LICENSE_NAME = "idl-license.face-android";

    private FaceDetector faceDetector;
    private FaceFeature faceFeature;

    private FaceSDKManager() {
        faceDetector = new FaceDetector();
        faceFeature = new FaceFeature();
    }

    private static class HolderClass {
        private static final FaceSDKManager instance = new FaceSDKManager();
    }

    public static FaceSDKManager getInstance() {
        return HolderClass.instance;
    }

    public void initLiveness(Context context) {
        FaceSDK.livenessSilentInit(context, FaceSDK.LivenessTypeId.LIVEID_VIS);
        FaceSDK.livenessSilentInit(context, FaceSDK.LivenessTypeId.LIVEID_IR);
        FaceSDK.livenessSilentInit(context, FaceSDK.LivenessTypeId.LIVEID_DEPTH);
        initStatus = SDK_INITED;
    }

    public FaceDetector getFaceDetector() {
        return faceDetector;
    }

    public FaceFeature getFaceFeature() {
        return faceFeature;
    }


    public int initStatus() {
        return initStatus;
    }

    private volatile int initStatus = SDK_UNACTIVATION;

    private boolean unusual;

    public void cameraOrientation(boolean unusual) {
        this.unusual = unusual;
    }

    public boolean isUnusual() {
        return unusual;
    }
}