package com.fanfan.robot.ui.auxiliary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.app.common.act.BarBaseActivity;
import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.presenter.LocalSoundPresenter;
import com.fanfan.robot.presenter.ipersenter.ILocalSoundPresenter;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.recycler.vr.VrImageAdapter;
import com.fanfan.robot.model.VrImage;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 全景地图页面
 */
public class PanoramicMapActivity extends BarBaseActivity implements
        ILocalSoundPresenter.ILocalSoundView {

    @BindView(R.id.vr_panorama_view)
    VrPanoramaView mVrPanoramaView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, PanoramicMapActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private VrPanoramaView.Options options;
    private Bitmap paNormalBitmap;

    private List<VrImage> vrImages = new ArrayList<>();

    private LocalSoundPresenter mSoundPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_panorama_detail;
    }

    @Override
    protected void initData() {
        //  切换VR模式
        //  有两个模式：1.VrWidgetView.DisplayMode.FULLSCREEN_STEREO（手机模式）
        //            2.VrWidgetView.DisplayMode.FULLSCREEN_MONO（默认模式）；
//        mVrPanoramaView.setDisplayMode(VrWidgetView.DisplayMode.FULLSCREEN_MONO);
        mVrPanoramaView.setPureTouchTracking(true);
        mVrPanoramaView.setInfoButtonEnabled(false);//信息按钮禁掉
        mVrPanoramaView.setStereoModeButtonEnabled(false);//眼镜模式按钮禁掉
        mVrPanoramaView.setFullscreenButtonEnabled(false);//全屏模式按钮禁掉
        mVrPanoramaView.setTouchTrackingEnabled(true); //开启手触模式

        options = new VrPanoramaView.Options();
        //设置图片类型为单通道图片
        options.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;

        paNormalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.andes);
//        mVrPanoramaView.loadImageFromBitmap(paNormalBitmap, options);
//        Glide.with(this)
//                .asBitmap()
//                .load("https://qcdn.veervr.tv/6aac80c6073f40f49d6d7044576cd7dd.png-video.vr.medium?sign=2614396c907222d4eabc68eb505a2dfe&t=5afcc600")
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        if(resource != null){
//                            mVrPanoramaView.loadImageFromBitmap(resource, options);
//                        }else{
//                            mVrPanoramaView.loadImageFromBitmap(paNormalBitmap, options);
//                        }
//                    }
//                });



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        VrImageAdapter vrImageAdapter = new VrImageAdapter(vrImages);
        recyclerView.setAdapter(vrImageAdapter);

        VrImage vrImage = new VrImage();
        vrImage.setName("andes");
        vrImage.setPath(R.drawable.andes);
        vrImages.add(vrImage);

        vrImageAdapter.replaceData(vrImages);

        vrImageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VrImage vrImage = vrImages.get(position);
                int path = (int) vrImage.getPath();
                paNormalBitmap = BitmapFactory.decodeResource(getResources(), path);
                mVrPanoramaView.loadImageFromBitmap(paNormalBitmap, options);
            }
        });

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVrPanoramaView.resumeRendering();

        mSoundPresenter.onResume();

        addSpeakAnswer("你好，这里是全景地图页面");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVrPanoramaView.pauseRendering();

        mSoundPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVrPanoramaView.shutdown();

        mSoundPresenter.finish();
    }

    @OnClick(R.id.vr_panorama_view)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vr_panorama_view:

                break;
        }
    }

    private void addSpeakAnswer(String messageContent) {
        mSoundPresenter.doAnswer(messageContent);
    }

    private void addSpeakAnswer(int res) {
        mSoundPresenter.doAnswer(getResources().getString(res));
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
        addSpeakAnswer("此页面暂不支持此功能");
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

    }

    @Override
    public void refLocalPage(String key1, String key2, String key3, String key4) {
        addSpeakAnswer(R.string.open_local);
    }
}
