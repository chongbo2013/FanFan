package com.fanfan.robot.ui.setting.act.video;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.novel.utils.grammer.GrammerUtils;
import com.fanfan.novel.utils.grammer.LoadDataUtils;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.act.BarBaseActivity;
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
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.media.MediaFile;
import com.fanfan.robot.R;
import com.fanfan.robot.app.NovelApp;
import com.fanfan.robot.ui.setting.act.video.recoder.CameraRecoderActivity;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.fanfan.robot.app.common.Constants.unusual;

/**
 * Created by android on 2018/1/6.
 */

public class AddVideoActivity extends BarBaseActivity {

    @BindView(R.id.img_video)
    ImageView imgVideo;
    @BindView(R.id.et_video_shart)
    TextView etVideoShart;
    @BindView(R.id.tv_video)
    TextView tvVideo;

    public static final String VIDEO_ID = "videoId";
    public static final String RESULT_CODE = "video_title_result";
    public static final int ADD_VIDEO_REQUESTCODE = 224;

    public static final int CHOOSE_VIDEO = 4;//选择视频
    public static final int RECORD_VIDEO = 5;//录制视频

    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, AddVideoActivity.class);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void newInstance(Activity context, long id, int requestCode) {
        Intent intent = new Intent(context, AddVideoActivity.class);
        intent.putExtra(VIDEO_ID, id);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private long saveLocalId;

    private VideoDBManager mVideoDBManager;

    private VideoBean videoBean;

    private SpeechRecognizer recognizer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_video;
    }

    @Override
    protected void initData() {
        saveLocalId = getIntent().getLongExtra(VIDEO_ID, -1);

        mVideoDBManager = new VideoDBManager();

        if (saveLocalId != -1) {
            videoBean = mVideoDBManager.selectByPrimaryKey(saveLocalId);

            String savePath = videoBean.getVideoImage();
            if (savePath != null) {
                if (new File(savePath).exists()) {
                    loadImgVideo(savePath);
                }
            }
            etVideoShart.setText(videoBean.getShowTitle());

        }

        recognizer = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code == ErrorCode.SUCCESS) {
                    Print.e("初始化成功，错误码：" + code);
                }
                Print.e("初始化失败，错误码：" + code);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (recognizer != null) {
            recognizer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.finish_black, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish:

                if (videoBean == null) {
                    showToast("请选择视频！");
                    break;
                }
                if (AppUtil.isEmpty(etVideoShart)) {
                    showToast("视频名称不能为空！");
                    break;
                }
                if (etVideoShart.getText().toString().trim().length() > 20) {
                    showToast("输入 20 字以内");
                    break;
                }
                if (saveLocalId == -1) {
                    List<VideoBean> been = mVideoDBManager.queryVideoByQuestion(etVideoShart.getText().toString().trim());
                    if (!been.isEmpty()) {
                        showToast("请不要添加相同的视频！");
                        break;
                    }
                }
                videoIsexit();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.tv_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_video:
                if (etVideoShart.getText().toString().trim().equals("")) {
                    showToast("输入不能为空！");
                } else {

                    if (unusual) {
                        DialogUtils.showBasicNoTitleDialog(this, "选择导入方法", "本地视频", "视频录制",
                                new DialogUtils.OnNiftyDialogListener() {
                                    @Override
                                    public void onClickLeft() {
                                        addLocalVideo();
                                    }

                                    @Override
                                    public void onClickRight() {
                                        videoRecording();
                                    }
                                });
                    } else {
                        addLocalVideo();
                    }
                }
                break;
        }
    }

    private void addLocalVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHOOSE_VIDEO);
    }

    private void videoRecording() {
        CameraRecoderActivity.newInstance(this, RECORD_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_VIDEO:
                if (resultCode == RESULT_OK) {
                    if (null != data) {
                        Uri uri = data.getData();
                        if (uri == null) {
                            return;
                        }
                        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));//// 视频路径
                                Print.e("视频路径 ： " + videoPath);

                                if (MediaFile.isVideoFileType(videoPath)) {
                                    int videoId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));//// 视频名称
                                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));//// 视频大小

                                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));//// 视频缩略图路径
                                    // 方法二 ThumbnailUtils 利用createVideoThumbnail 通过路径得到缩略图，保持为视频的默认比例
                                    // 第一个参数为 视频/缩略图的位置，第二个依旧是分辨率相关的kind
                                    Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(imagePath, MediaStore.Video.Thumbnails.MINI_KIND);
                                    String savePath = Constants.PROJECT_PATH + "video" + File.separator + title + ".jpg";
                                    BitmapUtils.saveBitmapToFile(bitmap2, "video", title + ".jpg");

                                    loadImgVideo(bitmap2);

                                    videoBean = new VideoBean();
                                    videoBean.setSize(size);
                                    videoBean.setVideoName(title);
                                    videoBean.setVideoUrl(videoPath);
                                    videoBean.setVideoImage(savePath);
                                } else {
                                    showToast("请选择视频文件");
                                }
                            }
                        }
                    } else {
                        videoBean = null;
                    }
                }
                break;
            case RECORD_VIDEO:
                if (resultCode == RESULT_OK) {
                    if (null != data) {
                        String imagePath = data.getStringExtra(CameraRecoderActivity.FIRST_FRAME);
                        String videoPath = data.getStringExtra(CameraRecoderActivity.VIDEO_URL);

                        loadImgVideo(imagePath);
                        File videoFile = new File(videoPath);

                        videoBean = new VideoBean();
                        videoBean.setSize(videoFile.length());
                        videoBean.setVideoName(videoFile.getName());
                        videoBean.setVideoUrl(videoPath);
                        videoBean.setVideoImage(imagePath);
                    }
                }
                break;
        }
    }

    private void loadImgVideo(Bitmap bitmap) {
        tvVideo.setText("更换视频");
        imgVideo.setVisibility(View.VISIBLE);
        imgVideo.setImageBitmap(bitmap);
    }

    private void loadImgVideo(String path) {
        tvVideo.setText("更换视频");
        imgVideo.setVisibility(View.VISIBLE);
        ImageLoader.loadImage(AddVideoActivity.this, imgVideo, path, R.mipmap.ic_logo);
    }

    private void videoIsexit() {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {

                insertVideoBean();

                String lexiconContents = LoadDataUtils.getLexiconContents();

                e.onNext(lexiconContents);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        recognizer = GrammerUtils.getRecognizer(AddVideoActivity.this, recognizer);

                        Print.e(s);
                        recognizer.buildGrammar(GrammerUtils.GRAMMAR_BNF, s, new GrammarListener() {
                            @Override
                            public void onBuildFinish(String s, SpeechError speechError) {
                                if (speechError == null) {
                                    Intent intent = new Intent();
                                    intent.putExtra(RESULT_CODE, saveLocalId);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    showToast(speechError.getErrorDescription());
                                }
                            }
                        });
                    }
                });
    }


    private void insertVideoBean() {
        videoBean.setShowTitle(AppUtil.getText(etVideoShart));
        videoBean.setSaveTime(System.currentTimeMillis());
        if (saveLocalId == -1) {
            saveLocalId = mVideoDBManager.insertForId(videoBean);
        } else {
            videoBean.setId(saveLocalId);
            mVideoDBManager.update(videoBean);
        }
    }

}
