package com.fanfan.novel.face;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.baidu.aip.manager.FaceDetector;
import com.baidu.aip.manager.FaceFeature;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.PreferencesUtil;
import com.baidu.idl.facesdk.FaceSDK;
import com.baidu.idl.license.AndroidLicenser;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.baidu.bean.Activate;
import com.fanfan.youtu.utils.RxSchedulers;
import com.seabreeze.log.Print;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.baidu.aip.manager.FaceSDKManager.LICENSE_NAME;
import static com.baidu.aip.manager.FaceSDKManager.SDK_FAIL;
import static com.baidu.aip.manager.FaceSDKManager.SDK_INITED;
import static com.baidu.aip.manager.FaceSDKManager.SDK_UNACTIVATION;

public class FaceInitManager {

    private Context mContext;

    private Youtucode youtucode;

    private FaceInitManager() {
        youtucode = Youtucode.getSingleInstance();
    }

    private static class HolderClass {
        private static final FaceInitManager instance = new FaceInitManager();
    }

    public static FaceInitManager getInstance() {
        return FaceInitManager.HolderClass.instance;
    }

    private volatile int initStatus = SDK_UNACTIVATION;

    @SuppressLint("CheckResult")
    public void init(final Context context, final SdkInitListener listener) {
        this.mContext = context;

        FaceSDK.initLicense(mContext, Constants.FACE_KEY, LICENSE_NAME, false);
        int status = FaceSDK.getAuthorityStatus();

        if (status == AndroidLicenser.ErrorCode.SUCCESS.ordinal()) {
            initStatus = SDK_INITED;
            success(context, listener);
        } else {
            if (status == AndroidLicenser.ErrorCode.LICENSE_EXPIRED.ordinal()) {
                Print.e("FaceSDK 授权过期");
            } else {
                Print.e("FaceSDK 授权失败" + status);
            }
            initStatus = SDK_FAIL;


            String device = AndroidLicenser.get_device_id(context.getApplicationContext());
            youtucode.activateKey(device, Constants.FACE_KEY)
                    .compose(RxSchedulers.<Activate>rxSchedulerHelper())
                    .subscribe(new Consumer<Activate>() {
                        @Override
                        public void accept(Activate activate) throws Exception {
                            Activate.ResultBean resultBean = activate.getResult();
                            if (resultBean != null) {
                                String license = resultBean.getLicense();
                                if (!TextUtils.isEmpty(license)) {
                                    String[] licenses = license.split(",");
                                    if (licenses.length == 2) {
                                        PreferencesUtil.putString("activate_key", Constants.FACE_KEY);
                                        ArrayList<String> list = new ArrayList<>();
                                        list.add(licenses[0]);
                                        list.add(licenses[1]);
                                        boolean c = FileUitls.c(mContext, LICENSE_NAME, list);
                                        if (c) {
                                            success(context, listener);
                                            return;
                                        }
                                    }
                                }
                            }
                            if (listener != null) {
                                listener.initFail("face other error");
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (listener != null) {
                                listener.initFail(throwable.getMessage());
                            }
                        }
                    });

        }

    }

    private void success(Context context, SdkInitListener listener) {
        FaceSDKManager.getInstance().getFaceDetector().setInitStatus(initStatus);
        FaceSDKManager.getInstance().getFaceDetector().init(context);
        FaceSDKManager.getInstance().getFaceFeature().init(context);
        FaceSDKManager.getInstance().initLiveness(context);
        FaceSDKManager.getInstance().cameraOrientation(Constants.unusual);
        if (listener != null) {
            listener.initSuccess();
        }
    }


    public interface SdkInitListener {

        void initSuccess();

        void initFail(String msg);
    }

}
