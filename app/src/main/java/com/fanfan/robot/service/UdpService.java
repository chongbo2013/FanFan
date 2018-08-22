package com.fanfan.robot.service;

import com.fanfan.robot.app.common.base.BaseService;
import com.fanfan.robot.other.event.SendUdpEvent;
import com.fanfan.robot.other.udp.SocketManager;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class UdpService extends BaseService {

    @Override
    public void onCreate() {
        super.onCreate();
        SocketManager.getInstance().registerUdpServer();
    }

    @Override
    public void onDestroy() {
        SocketManager.getInstance().unregisterUdpServer();
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(SendUdpEvent event) {
        if (event.isOk()) {
            String sendMsg = event.getBean();
            SocketManager.getInstance().sendTextByUDP(sendMsg);
            Print.e(sendMsg);
        } else {
            Print.e("ReceiveEvent error");
        }
    }

}
