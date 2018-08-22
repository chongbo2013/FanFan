package com.fanfan.robot.service;

import com.fanfan.robot.app.common.base.BaseService;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.other.event.ActivityToServiceEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.serial.HexUtils;
import com.fanfan.serial.SerialPortManager;
import com.fanfan.serial.listener.OnOpenSerialPortListener;
import com.fanfan.serial.listener.OnSerialPortDataListener;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by android on 2017/12/26.
 */

public class SerialService extends BaseService implements OnOpenSerialPortListener, OnSerialPortDataListener {

    public static final String DEV = "/dev/";

    //错误的话需要改这里
    public static final String devName = "ttyS4";
    public static final String voiceName = "ttyXRUSB3";//"ttyUSB1";
    public static final String cruiseName = "ttyUSB2";
    public static final String alarmName = "ttyS1";

    //控制机器人行走，动作
    public static final int DEV_BAUDRATE = 9600;
    //麦克风阵列。如你好fanfan
    public static final int VOICE_BAUDRATE = 115200;
    //导航的
    public static final int CRUISE_BAUDRATE = 57600;
    public static final int ALARM_BAUDRATE = 9600;

    private SerialPortManager mManager1;
    private SerialPortManager mManager2;
    private SerialPortManager mManager3;
    private SerialPortManager mManager4;

    @Override
    public void onCreate() {
        super.onCreate();

        mManager2 = init(mManager2, new File(DEV + devName), DEV_BAUDRATE);
        mManager1 = init(mManager1, new File(DEV + voiceName), VOICE_BAUDRATE);
        mManager3 = init(mManager3, new File(DEV + cruiseName), CRUISE_BAUDRATE);
        mManager4 = init(mManager4, new File(DEV + alarmName), ALARM_BAUDRATE);

    }

    private SerialPortManager init(SerialPortManager serialPortManager, File file, int baudRate) {
        serialPortManager = new SerialPortManager();
        serialPortManager.setOnOpenSerialPortListener(this)
                .setOnSerialPortDataListener(this)
                .openSerialPort(file, baudRate);
        return serialPortManager;
    }

    @Override
    public void onDestroy() {
        close(mManager1);
        close(mManager2);
        close(mManager3);
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onResultEvent(ActivityToServiceEvent event) {
        if (event.isOk()) {
            SerialBean serialBean = event.getBean();
            Print.i("activity发送到service中的数据 " + serialBean.toString());
            int iBaudRate = serialBean.getBaudRate();
            if (iBaudRate == DEV_BAUDRATE) {
                byte[] bOutArray = HexUtils.HexToByteArr(serialBean.getMotion());
                mManager2.sendBytes(bOutArray);
            } else if (iBaudRate == VOICE_BAUDRATE) {
//                byte[] bOutArray = HexUtils.HexToByteArr(serialBean.getMotion());
                mManager1.sendBytes(serialBean.getMotion().getBytes());
            } else if (iBaudRate == CRUISE_BAUDRATE) {
                mManager3.sendBytes(serialBean.getMotion().getBytes());
            }
        } else {
            Print.e("ReceiveEvent error");
        }
    }


    private void close(SerialPortManager serialPortManager) {
        if (null != serialPortManager) {
            serialPortManager.closeSerialPort();
        }
    }


    //*****************************打开
    @Override
    public void onSuccess(File device, int baudRate) {
        Print.e(String.format("串口 [%s] 打开成功   波特率 %s", device.getPath(), baudRate));
    }

    @Override
    public void onFail(File device, Status status) {
        switch (status) {
            case NO_READ_WRITE_PERMISSION:
                Print.e(device.getPath() + " 没有读写权限");
                break;
            case OPEN_FAIL:
            default:
                Print.e(device.getPath() + " 串口打开失败");
                break;
        }
    }

    //****************************收发数据
    @Override
    public void onDataReceived(String absolute, int baudRate, byte[] bytes) {

        StringBuilder sMsg = new StringBuilder();
        if (baudRate == VOICE_BAUDRATE) {
            //在十六进制转换为字符串后的得到的是Unicode编码,此时再将Unicode编码解码即可获取原始字符串
            sMsg.append(HexUtils.hexStringToString(HexUtils.byte2HexStr(bytes)));
        } else {
            sMsg.append(new String(bytes));
        }
        SerialBean serialBean = new SerialBean();
        serialBean.setAbsolute(absolute);
        serialBean.setBaudRate(baudRate);
        serialBean.setMotion(sMsg.toString());

        Print.e("service中接受到串口的数据" + serialBean.toString());

        ServiceToActivityEvent serviceToActivityEvent = new ServiceToActivityEvent("");
        serviceToActivityEvent.setEvent(200, serialBean);
        EventBus.getDefault().post(serviceToActivityEvent);
    }

    @Override
    public void onDataSent(String absolute, int baudRate, byte[] bytes) {
        Print.e("send success " + baudRate);
    }
}
