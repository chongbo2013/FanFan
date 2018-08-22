package com.fanfan.robot.ui.setting.fragment;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.base.BaseDialogFragment;
import com.fanfan.robot.db.manager.NavigationDBManager;
import com.fanfan.robot.db.manager.SiteDBManager;
import com.fanfan.robot.db.manager.VideoDBManager;
import com.fanfan.robot.db.manager.VoiceDBManager;
import com.fanfan.robot.model.NavigationBean;
import com.fanfan.robot.model.SiteBean;
import com.fanfan.robot.model.VideoBean;
import com.fanfan.robot.model.VoiceBean;
import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.novel.utils.bitmap.BitmapUtils;
import com.fanfan.novel.utils.system.FileUtil;
import com.fanfan.novel.utils.media.MediaFile;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.recycler.other.ImportAdapter;
import com.fanfan.robot.app.NovelApp;
import com.fanfan.robot.model.Channel;
import com.fanfan.robot.ui.setting.SettingActivity;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 一键导入页面
 */
@Deprecated
public class ImportFragment extends BaseDialogFragment {

    //    @BindView(R.id.voice_view)
//    RecyclerView voiceView;
//    @BindView(R.id.video_view)
//    RecyclerView videoView;
    @BindView(R.id.tv_titlebar_name)
    TextView mTvTitlebarName;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_import)
    Button btnImport;

    public static ImportFragment newInstance() {
        ImportFragment importFragment = new ImportFragment();
        Bundle bundle = new Bundle();
        importFragment.setArguments(bundle);
        return importFragment;
    }

    private List<File> videoFiles = new ArrayList<>();
    private List<File> imageFiles = new ArrayList<>();
    private List<String> navigationFiles = new ArrayList<>();

    private String[] action;
    private String[] actionOrder;

    private String[] expression;
    private String[] expressionData;

    private String[] navigation;
    private String[] navigationData;

    private String[] localVoiceQuestion;
    private String[] localVoiceAnswer;

    private String[] localSiteName;
    private String[] localSiteUrl;

    private String[] localNavigation;
    private String[] localNavigationDatail;

    private List<VoiceBean> voiceBeanList = new ArrayList<>();
    private List<VideoBean> videoBeanList = new ArrayList<>();
    private List<SiteBean> siteBeanList = new ArrayList<>();
    private List<NavigationBean> navigationBeanList = new ArrayList<>();

//    private VideoDataAdapter videoDataAdapter;
//    private VoiceDataAdapter voiceDataAdapter;

    private VideoDBManager mVideoDBManager;
    private VoiceDBManager mVoiceDBManager;
    private NavigationDBManager mNavigationDBManager;
    private SiteDBManager mSiteDBManager;

    private ImportAdapter mAdapter;
    private List<Channel> mDatas = new ArrayList<>();

    private SpeechRecognizer mIat;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_import;
    }

    @Override
    protected void initData() {
        mTvTitlebarName.setText("一键导入");
        loadFile(Constants.RES_DIR_NAME);

        action = resArray(R.array.action);
        actionOrder = resArray(R.array.action_order);

        expression = resArray(R.array.expression);
        expressionData = resArray(R.array.expression_data);

        navigation = resArray(R.array.navigation);
        navigationData = resArray(R.array.navigation_data);

        localVoiceQuestion = resArray(R.array.local_voice_question);
        localVoiceAnswer = resArray(R.array.local_voice_answer);

        localSiteName = resArray(R.array.local_site);
        localSiteUrl = resArray(R.array.local_site_url);

        localNavigation = resArray(R.array.local_navigation);
        localNavigationDatail = resArray(R.array.local_navigation_datail);

        loadNavigationImage();

        loadVoice();

        loadVideo();

        loadSite();

        loadNavigation();

        mVideoDBManager = new VideoDBManager();
        mVoiceDBManager = new VoiceDBManager();
        mSiteDBManager = new SiteDBManager();
        mNavigationDBManager = new NavigationDBManager();

        setAdapter();
    }


    @Override
    protected void setListener(View rootView) {

    }

    @Override
    public void onResume() {
        super.onResume();
        assert ((SettingActivity) getActivity()) != null;
        ((SettingActivity) getActivity()).dismissLoading();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mIat != null) {
            mIat.cancel();
        }
        assert ((SettingActivity) getActivity()) != null;
        ((SettingActivity) getActivity()).dismissLoading();
    }


    private void loadNavigationImage() {
        AssetManager manager = getResources().getAssets();
        try {
            String fileNames[] = manager.list("train");
            if (fileNames != null && fileNames.length > 0) {
                for (int i = 0; i < fileNames.length; i++) {
                    navigationFiles.add("file:///android_asset/train/" + fileNames[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadVoice() {
        for (int i = 0; i < localVoiceQuestion.length; i++) {
            VoiceBean voiceBean = new VoiceBean();

            int actionIndex = new Random().nextInt(action.length);
            int expressionIndex = new Random().nextInt(expression.length);

            voiceBean.setSaveTime(System.currentTimeMillis());
            voiceBean.setShowTitle(localVoiceQuestion[i]);
            voiceBean.setVoiceAnswer(localVoiceAnswer[i]);
            voiceBean.setExpression(expression[expressionIndex]);
            voiceBean.setExpressionData(expressionData[expressionIndex]);
            voiceBean.setAction(action[actionIndex]);
            voiceBean.setActionData(actionOrder[actionIndex]);

            if (imageFiles.size() > 0) {
                int imageIndex = new Random().nextInt(imageFiles.size());
                voiceBean.setImgUrl(imageFiles.get(imageIndex).getAbsolutePath());
            }
            voiceBeanList.add(voiceBean);
        }
    }

    private void loadVideo() {
        if (videoFiles == null || videoFiles.size() == 0) {
            return;
        }
        for (File file : videoFiles) {
            VideoBean videoBean = new VideoBean();

            String name = file.getName().substring(0, file.getName().indexOf("."));
            String showTitle = name.replace(" ", "").replace("-", "");
            long size = FileUtil.getFileSize(file);
            Bitmap bitmap = FileUtil.getVideoThumb(file.getAbsolutePath());
            String savePath = Constants.PROJECT_PATH + "video" + File.separator + name + ".jpg";
            BitmapUtils.saveBitmapToFile(bitmap, "video", name + ".jpg");

            videoBean.setSize(size);
            videoBean.setVideoName(name);
            videoBean.setVideoUrl(file.getAbsolutePath());
            videoBean.setVideoImage(savePath);
            videoBean.setShowTitle(showTitle);
            videoBean.setSaveTime(System.currentTimeMillis());
            videoBeanList.add(videoBean);
        }
    }

    private void loadSite() {
        for (int i = 0; i < localSiteName.length; i++) {
            SiteBean siteBean = new SiteBean();
            siteBean.setName(localSiteName[i]);
            siteBean.setUrl(localSiteUrl[i]);
            siteBean.setSaveTime(System.currentTimeMillis());
            siteBeanList.add(siteBean);
        }
    }


    private void loadNavigation() {
        for (int i = 0; i < localNavigation.length; i++) {
            NavigationBean navigationBean = new NavigationBean();

            int navigationIndex = new Random().nextInt(navigation.length);

            navigationBean.setSaveTime(System.currentTimeMillis());
            navigationBean.setTitle(localNavigation[i]);
            navigationBean.setGuide(localNavigationDatail[i]);
            navigationBean.setDatail(localNavigationDatail[i]);
            navigationBean.setNavigation(navigation[navigationIndex]);
            navigationBean.setNavigationData(navigationData[navigationIndex]);
            if (navigationFiles.size() > 0) {
                for (int j = 0; j < navigationFiles.size(); j++) {
                    if (navigationFiles.get(j).indexOf(localNavigation[i]) > 0) {
                        navigationBean.setImgUrl(navigationFiles.get(j));
                    }
                }
//                int imageIndex = new Random().nextInt(imageFiles.size());
//                navigationBean.setImgUrl(imageFiles.get(imageIndex).getAbsolutePath());
            }
            navigationBeanList.add(navigationBean);
        }
    }

    private void setAdapter() {

//        videoDataAdapter = new VideoDataAdapter(getActivity(), videoBeanList);
//        videoView.setAdapter(videoDataAdapter);
//        videoView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        videoView.setItemAnimator(new DefaultItemAnimator());
//        videoView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//
//        voiceDataAdapter = new VoiceDataAdapter(getActivity(), voiceBeanList);
//        voiceView.setAdapter(voiceDataAdapter);
//        voiceView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        voiceView.setItemAnimator(new DefaultItemAnimator());
//        voiceView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        Channel voiceChannel = new Channel();
        voiceChannel.setItemtype(Channel.TYPE_TITLE);
        voiceChannel.setChannelName("本地语音");
        mDatas.add(voiceChannel);

        if (voiceBeanList.size() > 0) {
            for (VoiceBean bean : voiceBeanList) {
                Channel channelList = new Channel();
                channelList.setItemtype(Channel.TYPE_CONTENT);
                channelList.setChannelName(bean.getShowTitle());
                mDatas.add(channelList);
            }
        } else {
            Channel channelList = new Channel();
            channelList.setItemtype(Channel.TYPE_CONTENT);
            channelList.setChannelName("请手动添加");
            mDatas.add(channelList);
        }

        Channel videoChannel = new Channel();
        videoChannel.setItemtype(Channel.TYPE_TITLE);
        videoChannel.setChannelName("本地视频");
        mDatas.add(videoChannel);

        if (videoBeanList.size() > 0) {
            for (VideoBean bean : videoBeanList) {
                Channel channelList = new Channel();
                channelList.setItemtype(Channel.TYPE_CONTENT);
                channelList.setChannelName(bean.getShowTitle());
                mDatas.add(channelList);
            }
        } else {
            Channel channelList = new Channel();
            channelList.setItemtype(Channel.TYPE_CONTENT);
            channelList.setChannelName("请手动添加");
            mDatas.add(channelList);
        }

        Channel navigationChannel = new Channel();
        navigationChannel.setItemtype(Channel.TYPE_TITLE);
        navigationChannel.setChannelName("导航");
        mDatas.add(navigationChannel);

        if (navigationBeanList.size() > 0) {
            for (NavigationBean bean : navigationBeanList) {
                Channel channelList = new Channel();
                channelList.setItemtype(Channel.TYPE_CONTENT);
                channelList.setChannelName(bean.getTitle());
                mDatas.add(channelList);
            }
        } else {
            Channel channelList = new Channel();
            channelList.setItemtype(Channel.TYPE_CONTENT);
            channelList.setChannelName("请手动添加");
            mDatas.add(channelList);
        }

        Channel siteChannel = new Channel();
        siteChannel.setItemtype(Channel.TYPE_TITLE);
        siteChannel.setChannelName("网址");
        mDatas.add(siteChannel);

        if (siteBeanList.size() > 0) {
            for (SiteBean bean : siteBeanList) {
                Channel channelList = new Channel();
                channelList.setItemtype(Channel.TYPE_CONTENT);
                channelList.setChannelName(bean.getName());
                mDatas.add(channelList);
            }
        } else {
            Channel channelList = new Channel();
            channelList.setItemtype(Channel.TYPE_CONTENT);
            channelList.setChannelName("请手动添加");
            mDatas.add(channelList);
        }

        mAdapter = new ImportAdapter(mDatas);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = mAdapter.getItemViewType(position);
                return itemViewType == Channel.TYPE_CONTENT ? 1 : 3;
            }
        });
    }


    @OnClick({R.id.tv_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_import:
                assert ((SettingActivity) getActivity()) != null;
                ((SettingActivity) getActivity()).showLoading();
                mVideoDBManager.deleteAll();
                mVideoDBManager.insertList(videoBeanList);
                mVoiceDBManager.deleteAll();
                mVoiceDBManager.insertList(voiceBeanList);
                mSiteDBManager.deleteAll();
                mSiteDBManager.insertList(siteBeanList);
                mNavigationDBManager.deleteAll();
                mNavigationDBManager.insertList(navigationBeanList);

                updateContents();
                break;
        }
    }


    private void loadFile(String dirName) {//Music

        String dirPath = Environment.getExternalStorageDirectory() + File.separator + dirName;
        File dirFile = new File(dirPath);
        if (!dirFile.exists() || dirFile.isFile()) {
            dirFile.mkdirs();
            return;
        }
        File[] files = dirFile.listFiles();
        if (files.length == 0) {
            return;
        }
        for (File file : files) {
            if (MediaFile.isVideoFileType(file.getAbsolutePath())) {
                videoFiles.add(file);
            }
            if (MediaFile.isImageFileType(file.getAbsolutePath())) {
                imageFiles.add(file);
            }
        }
    }

    /**
     * 更新所有
     */
    public void updateContents() {
        if (mVideoDBManager == null || mVoiceDBManager == null || mNavigationDBManager == null) {
            throw new NullPointerException("local loxicon unll");
        }
        StringBuilder lexiconContents = new StringBuilder();
        //本地语音
        List<VoiceBean> voiceBeanList = mVoiceDBManager.loadAll();
        for (VoiceBean voiceBean : voiceBeanList) {
            lexiconContents.append(voiceBean.getShowTitle()).append("\n");
        }
        //本地视频
        List<VideoBean> videoBeanList = mVideoDBManager.loadAll();
        for (VideoBean videoBean : videoBeanList) {
            lexiconContents.append(videoBean.getShowTitle()).append("\n");
        }
        //本地导航
        List<NavigationBean> navigationBeanList = mNavigationDBManager.loadAll();
        for (NavigationBean navigationBean : navigationBeanList) {
            lexiconContents.append(navigationBean.getTitle()).append("\n");
        }
        //本地网址
        List<SiteBean> siteBeanList = mSiteDBManager.loadAll();
        for (SiteBean siteBean : siteBeanList) {
            lexiconContents.append(siteBean.getName()).append("\n");
        }

        lexiconContents.append(AppUtil.words2Contents());
        updateLocation(lexiconContents.toString());
    }

    public void updateLocation(String lexiconContents) {
        mIat = SpeechRecognizer.createRecognizer(getActivity(), new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    Print.e("初始化失败，错误码：" + code);
                }
                Print.e("local initIat success");
            }
        });
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置引擎类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 指定资源路径
        mIat.setParameter(ResourceUtil.ASR_RES_PATH,
                ResourceUtil.generateResourcePath(NovelApp.getInstance().getApplicationContext(),
                        ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        // 指定语法路径
        mIat.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
        // 指定语法名字
        mIat.setParameter(SpeechConstant.GRAMMAR_LIST, "local");
        // 设置文本编码格式
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // lexiconName 为词典名字，lexiconContents 为词典内容，lexiconListener 为回调监听器
        int ret = mIat.updateLexicon("voice", lexiconContents, new LexiconListener() {
            @Override
            public void onLexiconUpdated(String lexiconId, SpeechError error) {
                if (error == null) {
                    Print.e("词典更新成功");
                    onLexiconSuccess();
                } else {
                    Print.e("词典更新失败,错误码：" + error.getErrorCode());
                    onLexiconError(error.getErrorDescription());
                }
            }
        });
        if (ret != ErrorCode.SUCCESS) {
            Print.e("更新词典失败,错误码：" + ret);
        }
    }

    public void onLexiconSuccess() {
        ((SettingActivity) getActivity()).showToast("一键导入完成");
        System.gc();
        dismiss();
    }

    public void onLexiconError(String error) {

    }

}
