package com.fanfan.robot.presenter;

import android.os.Handler;

import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.other.event.ActivityToServiceEvent;
import com.fanfan.robot.R;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

/**
 * Created by android on 2017/12/26.
 */

public class SerialPresenter extends ISerialPresenter {

    private ISerialView mSerialView;

    private Handler mHandler;

    public SerialPresenter(ISerialView baseView) {
        super(baseView);
        this.mSerialView = baseView;

        mHandler = new Handler();
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void receiveMotion(final int type, final String motion) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SerialBean serialBean = new SerialBean();
                serialBean.setBaudRate(type);
                serialBean.setMotion(motion);
                ActivityToServiceEvent serialEvent = new ActivityToServiceEvent("");
                serialEvent.setEvent(200, serialBean);
                EventBus.getDefault().post(serialEvent);
            }
        }, 100);

    }


    @Override
    public void onDataReceiverd(SerialBean serialBean) {
        int iBaudRate = serialBean.getBaudRate();
        String motion = serialBean.getMotion();
        if (iBaudRate == 9600) {
            String absolute = serialBean.getAbsolute();
            if (absolute.endsWith(SerialService.devName)) {

            } else if (absolute.endsWith(SerialService.alarmName)) {
                String alarmStr = serialBean.getMotion();
//                Alarm alarm = GsonUtil.GsonToBean(alarmStr, Alarm.class);
                String tempAlarmStr = alarmStr;
                int length = tempAlarmStr.length();
                String replace = tempAlarmStr.replace(",", "");
                int replaceLength = replace.length();
                if (length - replaceLength == 4) {

                    String[] arr = alarmStr.split(",");
                    if (arr.length == 5) {
                        int fog = Integer.valueOf(arr[0]);
                        int flame = Integer.valueOf(arr[1]);
                        double dust = Double.valueOf(arr[2]);
                        double humidity = Double.valueOf(arr[3]);
                        double temperature = Double.valueOf(arr[4]);
                        Alarm alarm = new Alarm(fog, flame, dust, humidity, temperature);
                        mSerialView.onAlarm(alarm);
                    }
                }

            }

        } else if (iBaudRate == SerialService.VOICE_BAUDRATE) {
            if (motion.contains("WAKE UP!")) {

                mSerialView.stopAll();
                if (motion.contains("##### IFLYTEK")) {

                    String str;
                    if (motion.contains("score:")) {
                        str = motion.substring(motion.indexOf("angle:") + 6, motion.indexOf("score:"));
                    } else {
                        str = motion.substring(motion.indexOf("angle:") + 6, motion.indexOf("##### IFLYTEK"));
                    }
                    int angle = Integer.parseInt(str.trim());

                    Print.e("解析到应该旋转的角度 : " + angle);
                    if (0 <= angle && angle < 30) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A521821EAA");
                        receiveMotion(SerialService.VOICE_BAUDRATE, "BEAM 0\n\r");//0
                    } else if (30 <= angle && angle <= 60) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A521823CAA");
                        receiveMotion(SerialService.VOICE_BAUDRATE, "BEAM 0\n\r");//0
                    } else if (120 <= angle && angle <= 150) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A5218278AA");
                        receiveMotion(SerialService.VOICE_BAUDRATE, "BEAM 0\n\r");//0
                    } else if (150 < angle && angle <= 180) {
                        receiveMotion(SerialService.DEV_BAUDRATE, "A5218296AA");
                        receiveMotion(SerialService.VOICE_BAUDRATE, "BEAM 0\n\r");//0
                    }

                    receiveMotion(SerialService.VOICE_BAUDRATE, "BEAM 0\n\r");//0

                }
            }

        } else if (iBaudRate == SerialService.CRUISE_BAUDRATE) {
            if (Arrays.asList(mSerialView.getContext().getResources().getStringArray(R.array.navigation_data)).contains(motion.trim())) {

//                receiveMotion(SerialService.DEV_BAUDRATE, "A50C8001AA");
                mSerialView.onMoveStop();
            } else if (motion.trim().equals("stop")) {
                mSerialView.onMoveSpeak();
            }
        }
    }


}
