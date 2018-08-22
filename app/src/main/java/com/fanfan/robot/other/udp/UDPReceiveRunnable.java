package com.fanfan.robot.other.udp;

import android.support.annotation.NonNull;

import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.youtu.utils.UUIDGenerator;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class UDPReceiveRunnable implements Runnable {


    private DatagramSocket mServer;


    private boolean udpLife = true;     //udp生命线程

    private DatagramPacket dpRcv;
    private byte[] msgRcv = new byte[1024];

    public UDPReceiveRunnable(@NonNull DatagramSocket datagramSocket) {
        this.mServer = datagramSocket;
    }


    @Override
    public void run() {
        String uuid = UUIDGenerator.getUUID();
        ReceiveEvent event = new ReceiveEvent(uuid);
        try {
            dpRcv = new DatagramPacket(msgRcv, msgRcv.length);
            while (udpLife) {
                mServer.receive(dpRcv);

                EventBus.getDefault().post(event.setEvent(200, dpRcv));
            }

        } catch (Exception e) {
            Print.e("RecveviceThread start fail");
            e.printStackTrace();
            EventBus.getDefault().post(event.setEvent(-1, null));
            mServer.close();
        }
        Print.e("Thread.interrupted");
    }

}
