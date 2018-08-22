package com.fanfan.robot.presenter;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

import com.fanfan.robot.model.PersonInfo;
import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.robot.presenter.ipersenter.IHsOtgPresenter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.huashi.otg.sdk.HSInterface;
import com.huashi.otg.sdk.HsOtgService;
import com.seabreeze.log.Print;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by android on 2018/1/2.
 */

public class HsOtgPresenter extends IHsOtgPresenter {

    private IHsOtgView mHsOtgView;

    private HSInterface hSinterface;
    private GoogleApiClient client;

    private boolean isHSinterface;

    private Handler mHandler;

    public HsOtgPresenter(IHsOtgView baseView) {
        super(baseView);
        mHsOtgView = baseView;

        mHandler = new Handler();
    }

    @Override
    public void onStart() {
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, "Main Page", Uri.parse("http://host/path"),
                Uri.parse("android-app://" + AppUtil.getPackageName(mHsOtgView.getContext()) + "/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, "Main Page", Uri.parse("http://host/path"),
                Uri.parse("android-app://" + AppUtil.getPackageName(mHsOtgView.getContext()) + "/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void start() {
        client = new GoogleApiClient.Builder(mHsOtgView.getContext()).addApi(AppIndex.API).build();

        Intent dsOtgIntent = new Intent(mHsOtgView.getContext(), HsOtgService.class);
        mHsOtgView.getContext().bindService(dsOtgIntent, connection, Service.BIND_AUTO_CREATE);
    }

    @Override
    public void finish() {
        hSinterface.unInit();
        mHsOtgView.getContext().unbindService(connection);
    }

    @Override
    public int init() {
        int code = hSinterface.init();
        mHsOtgView.init(code);
        return code;
    }

    @Override
    public int authenticate() {
        int code = hSinterface.Authenticate();
        mHsOtgView.authenticate(code);
        return code;
    }

    @Override
    public int readCard() {
        int code = hSinterface.ReadCard();
        mHsOtgView.readCard(code);
        return code;
    }

    @Override
    public void identityRead(PersonInfo info) {
        byte[] fp = new byte[1024];
        fp = HsOtgService.ic.getFpDate();
        String fristPFInfo = "";
        String secondPFInfo = "";

        if (fp[4] == (byte) 0x01) {
            fristPFInfo = String.format("指纹  信息：第一枚指纹注册成功。指位：%s。指纹质量：%d \n", getFPcode(fp[5]), fp[6]);
        } else {
            fristPFInfo = "身份证无指纹";
        }
        if (fp[512 + 4] == (byte) 0x01) {
            secondPFInfo = String.format("指纹  信息：第二枚指纹注册成功。指位：%s。指纹质量：%d \n", getFPcode(fp[512 + 5]), fp[512 + 6]);
        } else {
            secondPFInfo = "身份证无指纹";
        }

        info = new PersonInfo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");// 设置日期格式
        info.setIDCard(HsOtgService.ic.getIDCard());
        info.setName(HsOtgService.ic.getPeopleName());
        info.setGender(HsOtgService.ic.getSex());
        info.setFamily(HsOtgService.ic.getPeople());
        info.setBirth(sdf.format(HsOtgService.ic.getBirthDay()));
        info.setAddress(HsOtgService.ic.getAddr());
        info.setDepartment(HsOtgService.ic.getDepartment());
        info.setStrartDate(HsOtgService.ic.getStrartDate());
        info.setEndDate(HsOtgService.ic.getEndDate());
        info.setFristPFInfo(fristPFInfo);
        info.setSecondPFInfo(secondPFInfo);
        int ret = hSinterface.Unpack();// 照片解码
        if (ret == 0) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib/zp.bmp");
            if (file.exists()) {

                info.setHeadUrl(file.getAbsolutePath());
                Print.e(file.getAbsoluteFile());
                mHsOtgView.identityFinish(info);
            }
        } else {
            String msg = "照片解码失败";
            mHsOtgView.identityFail(msg);
        }
    }

    @Override
    public void authFail() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isHSinterface = false;
                init();
            }
        }, 2000);
    }

    @Override
    public void compareFail() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isHSinterface = false;
            }
        }, 2000);
    }

    public String getFPcode(int FPcode) {
        switch (FPcode) {
            case 11:
                return "右手拇指";
            case 12:
                return "右手食指";
            case 13:
                return "右手中指";
            case 14:
                return "右手环指";
            case 15:
                return "右手小指";
            case 16:
                return "左手拇指";
            case 17:
                return "左手食指";
            case 18:
                return "左手中指";
            case 19:
                return "左手环指";
            case 20:
                return "左手小指";
            case 97:
                return "右手不确定指位";
            case 98:
                return "左手不确定指位";
            case 99:
                return "其他不确定指位";
            default:
                return "未知";
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            hSinterface = (HSInterface) iBinder;
            if (!isHSinterface) {
                isHSinterface = true;
                init();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            connection = null;
            hSinterface = null;
        }
    };
}
