package com.fanfan.robot.ui.auxiliary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.model.Alarm;
import com.fanfan.robot.model.SerialBean;
import com.fanfan.robot.presenter.LocalSoundPresenter;
import com.fanfan.robot.presenter.SerialPresenter;
import com.fanfan.robot.presenter.ipersenter.ILocalSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ISerialPresenter;
import com.fanfan.robot.service.SerialService;
import com.fanfan.robot.other.event.ReceiveEvent;
import com.fanfan.robot.other.event.ServiceToActivityEvent;
import com.fanfan.robot.other.udp.SocketManager;
import com.fanfan.novel.utils.media.MediaFile;
import com.fanfan.novel.utils.PPTUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.recycler.ppt.PPTAdapter;
import com.fanfan.robot.adapter.recycler.ppt.PptTextAdapter;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by android on 2018/1/16.
 */

public class PPTActivity extends BarBaseActivity implements
        ILocalSoundPresenter.ILocalSoundView,
        ISerialPresenter.ISerialView {

    @BindView(R.id.recycler_title)
    RecyclerView recyclerTitle;
    @BindView(R.id.recycler_content)
    RecyclerView recyclerContent;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, PPTActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private LocalSoundPresenter mSoundPresenter;
    private SerialPresenter mSerialPresenter;

    private List<File> pptFiles = new ArrayList<>();
    private List<String> contentArray;

    private int curCount;

    private PPTAdapter pptAdapter;
    private PptTextAdapter pptTextAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ppt;
    }

    @Override
    protected void initView() {
        super.initView();

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();
        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();

        pptAdapter = new PPTAdapter(pptFiles);
        pptAdapter.openLoadAnimation();
        pptAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                refPPT(pptFiles.get(position), position);
            }
        });

        recyclerTitle.setAdapter(pptAdapter);
        recyclerTitle.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerTitle.setItemAnimator(new DefaultItemAnimator());

        pptTextAdapter = new PptTextAdapter(contentArray);
        pptTextAdapter.openLoadAnimation();
        recyclerContent.setAdapter(pptTextAdapter);
        recyclerContent.setLayoutManager(new LinearLayoutManager(this));
        recyclerContent.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    protected void initData() {

        List<File> files = loadFile(Constants.RES_DIR_NAME);
        if (files != null && files.size() > 0) {
            isNuEmpty();
            pptFiles = files;
            pptAdapter.replaceData(pptFiles);
            pptAdapter.notifyClick(0);
        } else {
            isEmpty();
        }
    }

    private List<File> loadFile(String dirName) {//Music
        List<File> pptFiles = new ArrayList<>();
        String dirPath = Environment.getExternalStorageDirectory() + File.separator + dirName;
        File dirFile = new File(dirPath);
        if (!dirFile.exists() || dirFile.isFile()) {
            dirFile.mkdirs();
            return null;
        }
        File[] files = dirFile.listFiles();
        if (files.length == 0) {
            return null;
        }
        for (File file : files) {
            if (MediaFile.isPPTFileType(file.getAbsolutePath())) {
                pptFiles.add(file);
            }
        }
        return pptFiles;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSoundPresenter.onResume();

        addSpeakAnswer("请点击要播放的ppt", false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAction();
        mSoundPresenter.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPresenter.finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ServiceToActivityEvent event) {
        if (event.isOk()) {
            SerialBean serialBean = event.getBean();
            mSerialPresenter.onDataReceiverd(serialBean);
        } else {
            Print.e("ReceiveEvent error");
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onResultEvent(ReceiveEvent event) {
        if (event.isOk()) {
            DatagramPacket packet = event.getBean();
            if (!SocketManager.getInstance().isGetTcpIp) {
                SocketManager.getInstance().setUdpIp(packet.getAddress().getHostAddress(), packet.getPort());
            }
            String recvStr = new String(packet.getData(), 0, packet.getLength());
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, recvStr);
            Print.e(recvStr);
        } else {
            Print.e("ReceiveEvent error");
        }
    }

    private void refPPT(File itemData, int position) {
        pptAdapter.notifyClick(position);
        stopAction();
        contentArray = PPTUtil.readPPT(itemData.getAbsolutePath());
        if (contentArray != null && contentArray.size() > 0) {
            pptTextAdapter.replaceData(contentArray);
            curCount = 0;
            recyclerContent.scrollToPosition(curCount);
            addSpeakAnswer(contentArray.get(curCount), true);
        }
    }

    private void stopAction() {
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.STOP_DANCE);
    }

    private void addSpeakAnswer(String messageContent, boolean isAction) {
        if (messageContent.length() > 0) {
            mSoundPresenter.doAnswer(messageContent);
            if (isAction) {
                speakingAddAction();
            }
        } else {
            onCompleted();
        }
    }

    private void addSpeakAnswer(int res) {
        mSoundPresenter.doAnswer(getResources().getString(res));
    }

    private void speakingAddAction() {
        Print.e("STOP_DANCE");
        mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, Constants.SPEAK_ACTION);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    @Override
    public void showMsg(int msg) {
        showToast(msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void spakeMove(SpecialType type, String result) {
        mSoundPresenter.onCompleted();
        switch (type) {
            case Forward:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038002AA");
                break;
            case Backoff:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038008AA");
                break;
            case Turnleft:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038004AA");
                break;
            case Turnright:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038006AA");
                break;
        }
    }

    @Override
    public void openMap() {
        addSpeakAnswer(R.string.open_map);
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void artificial() {
        addSpeakAnswer(R.string.open_artificial);
    }

    @Override
    public void face(SpecialType type, String result) {
        addSpeakAnswer(R.string.open_face);
    }

    @Override
    public void control(SpecialType type, String result) {
        addSpeakAnswer(R.string.open_control);
    }

    @Override
    public void refLocalPage(String result) {
        addSpeakAnswer(R.string.open_local);
    }

    @Override
    public void onCompleted() {
        if (contentArray != null && contentArray.size() > 0) {
            if (curCount < contentArray.size() - 1) {
                mHandler.postDelayed(runnable, 2000);
            } else if (curCount == contentArray.size() - 1) {
                curCount++;
                addSpeakAnswer("本次阅读完成", false);
            } else if (curCount == contentArray.size()) {
                stopAction();
            }
        } else {
            stopAction();
        }
    }

    @Override
    public void refLocalPage(String key1, String key2, String key3, String key4) {
        addSpeakAnswer(R.string.open_local);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C80E3AA");
            curCount++;
            Print.e("curCount : " + curCount);
            recyclerContent.scrollToPosition(curCount);
            addSpeakAnswer(contentArray.get(curCount), true);
        }
    };

    @Override
    public void stopAll() {
        super.stopAll();
        stopAction();
        mSoundPresenter.stopEvery();
    }

    @Override
    public void onMoveStop() {

    }

    @Override
    public void onMoveSpeak() {

    }

    @Override
    public void onAlarm(Alarm alarm) {

    }
}
