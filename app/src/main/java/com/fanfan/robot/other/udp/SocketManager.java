package com.fanfan.robot.other.udp;

import com.fanfan.robot.app.common.Constants;
import com.seabreeze.log.Print;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class SocketManager {

    public final static int DEFAULT_UDPSERVER_PORT = 8985;

    private DatagramSocket mDatagramSocket;

    private ThreadPoolExecutor executorService;

    public boolean isGetTcpIp = false;

    private static SocketManager mInstance;

    public static SocketManager getInstance() {
        if (mInstance == null) {
            synchronized (SocketManager.class) {
                if (mInstance == null)
                    mInstance = new SocketManager();
            }
        }
        return mInstance;
    }

    private SocketManager() {
    }

    public void setUdpIp(String enemyIp, int enemyPort) {
        if (Constants.CONNECT_IP == null) {
            Constants.CONNECT_IP = enemyIp;
        }
        if (Constants.CONNECT_PORT == 0) {
            Constants.CONNECT_PORT = enemyPort;
        }
        isGetTcpIp = true;
    }


    public void registerUdpServer() {
        if (Constants.IP == null) {
            Print.e(" my ip is null");
            return;
        }
        if (mDatagramSocket == null) {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(Constants.IP, Constants.PORT);
                mDatagramSocket = new DatagramSocket(inetSocketAddress);
            } catch (SocketException e) {
                Print.e("mDatagramSocket is null try create DatagramSocket object");
                e.printStackTrace();
            }
        }
        if (mDatagramSocket == null)
            throw new RuntimeException("DatagramSocket is null");
        Print.e("启动 udp 监听...");
        Print.e(" ip : " + Constants.IP);
        getExecutorService().execute(new UDPReceiveRunnable(mDatagramSocket));
    }


    public void unregisterUdpServer() {
        if (mDatagramSocket != null)
            mDatagramSocket.close();
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    public synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 10, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        }
        return executorService;
    }

    /**
     * 通过UDP发送文本
     *
     * @param msg 发送的字符串
     */
    public void sendTextByUDP(String msg) {
        try {
            InetAddress host = InetAddress.getByName(Constants.CONNECT_IP);
            final DatagramPacket dpSend = new DatagramPacket(msg.getBytes(), msg.getBytes().length, host, Constants.CONNECT_PORT);

            if (mDatagramSocket == null)
                throw new RuntimeException("DatagramSocket is null");

            getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mDatagramSocket.send(dpSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
