package com.fanfan.robot.ui.setting.act.voice;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.fanfan.robot.R;
import com.fanfan.robot.app.NovelApp;
import com.hankcs.hanlp.HanLP;
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
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android on 2018/1/8.
 */

public class AddVoiceActivity extends BarBaseActivity {

    @BindView(R.id.et_question)
    EditText etQuestion;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_expression)
    TextView tvExpression;
    @BindView(R.id.tv_action)
    TextView tvAction;
    @BindView(R.id.tv_img)
    TextView tvImg;
    @BindView(R.id.img_voice)
    ImageView imgVoice;
    @BindView(R.id.card_view)
    CardView cardView;

    public static final String VOICE_ID = "voiceId";
    public static final String RESULT_CODE = "voice_title_result";
    public static final int ADD_VOICE_REQUESTCODE = 225;

    private static final int REQCODE_SELALBUM = 101;

    public static final int CHOOSE_PHOTO = 2;//选择相册
    public static final int PICTURE_CUT = 3;//剪切图片
    public static final int SELECT_NO_PICTURE = 4;//剪切图片

    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, AddVoiceActivity.class);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void newInstance(Activity context, long id, int requestCode) {
        Intent intent = new Intent(context, AddVoiceActivity.class);
        intent.putExtra(VOICE_ID, id);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private long saveLocalId;

    private VoiceDBManager mVoiceDBManager;

    private int curExpression;
    private int curAction;

    private String imagePath;//打开相册选择照片的路径
    private Uri outputUri;//裁剪万照片保存地址
    private boolean isClickCamera;//是否是拍照裁剪
    private String noOutputPath;

    private VoiceBean voiceBean;

    private SpeechRecognizer recognizer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_voice;
    }

    @Override
    protected void initData() {
        saveLocalId = getIntent().getLongExtra(VOICE_ID, -1);

        mVoiceDBManager = new VoiceDBManager();

        if (saveLocalId != -1) {

            voiceBean = mVoiceDBManager.selectByPrimaryKey(saveLocalId);
            etQuestion.setText(voiceBean.getShowTitle());
            etContent.setText(voiceBean.getVoiceAnswer());
            curExpression = AppUtil.valueForArray(R.array.expression_data, voiceBean.getExpressionData());
            curAction = AppUtil.valueForArray(R.array.action_order, voiceBean.getActionData());
            String savePath = voiceBean.getImgUrl();
            if (savePath != null) {
                if (new File(savePath).exists()) {
                    loadImgVoice(savePath);
                }
            }
        } else {
            voiceBean = new VoiceBean();
            curExpression = 0;
            curAction = 0;
        }

        if (curExpression < 0)
            curExpression = 0;
        if (curAction < 0)
            curAction = 0;

        tvExpression.setText(getResources().getStringArray(R.array.expression)[curExpression]);
        tvAction.setText(getResources().getStringArray(R.array.action)[curAction]);


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

    @OnClick({R.id.tv_img, R.id.tv_expression, R.id.tv_action})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_img:
                if (AppUtil.isEmpty(etQuestion)) {
                    showToast("输入不能为空！");
                } else {
                    selectFromAlbum();//打开相册
                }
                break;
            case R.id.tv_expression:
                DialogUtils.showLongListDialog(AddVoiceActivity.this, "面部表情", R.array.expression, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        curExpression = position;
                        tvExpression.setText(text);
                    }
                });
                break;
            case R.id.tv_action:
                DialogUtils.showLongListDialog(AddVoiceActivity.this, "执行动作", R.array.action, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        curAction = position;
                        tvAction.setText(text);
                    }
                });
                break;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.finish_white, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish:

                if (AppUtil.isEmpty(etQuestion)) {
                    showToast("问题不能为空！");
                    break;
                }
                if (AppUtil.isEmpty(etContent)) {
                    showToast("答案不能为空！");
                    break;
                }
                if (etQuestion.getText().toString().trim().length() > 20) {
                    showToast("输入 20 字以内");
                    break;
                }
                if (saveLocalId == -1) {//直接添加，判断是否存在
                    List<VoiceBean> been = mVoiceDBManager.queryVoiceByQuestion(etQuestion.getText().toString().trim());
                    if (!been.isEmpty()) {
                        showToast("请不要添加相同的问题！");
                        break;
                    }
                }
                voiceIsexit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_PHOTO://打开相册
                    // 判断手机系统版本号
                    if (data != null) {
                        Uri uri = data.getData();
                        if (Build.VERSION.SDK_INT >= 19) {
                            // 4.4及以上系统使用这个方法处理图片
                            imagePath = BitmapUtils.handleImageOnKitKat(this, uri);
                            outputUri = BitmapUtils.cropPhoto(this, uri, "imgvoice", etQuestion.getText().toString() + ".jpg", PICTURE_CUT);
                        } else {
                            // 4.4以下系统使用这个方法处理图片
                            imagePath = BitmapUtils.getImagePath(this, uri, null);
                            outputUri = BitmapUtils.cropPhoto(AddVoiceActivity.this, uri, "imgvoice", etQuestion.getText().toString() + ".jpg", PICTURE_CUT);
                        }
                    }
                    break;
                case PICTURE_CUT://裁剪完成
                    isClickCamera = true;
                    if (isClickCamera) {
                        loadImgVoice(outputUri);
                    } else {
                        loadImgVoice(outputUri);
                    }
                    break;
                case SELECT_NO_PICTURE:
                    if (data != null) {
                        Uri uri = data.getData();
                        if (Build.VERSION.SDK_INT >= 19) {
                            // 4.4及以上系统使用这个方法处理图片
                            noOutputPath = BitmapUtils.handleImageOnKitKat(this, uri);
                            loadImgVoice(noOutputPath);
                        } else {
                            // 4.4以下系统使用这个方法处理图片
                            noOutputPath = BitmapUtils.getImagePath(this, uri, null);
                            loadImgVoice(noOutputPath);
                        }
                    }
                    break;
            }
        }
    }

    private int calculateSampleSize(BitmapFactory.Options options) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        int destHeight = 1000;
        int destWidth = 1000;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        return sampleSize;
    }

    private void loadImgVoice(String path) {
        cardView.setVisibility(View.VISIBLE);
        ImageLoader.loadLargeImage(AddVoiceActivity.this, imgVoice, path, R.mipmap.video_image);
    }

    private void loadImgVoice(Uri uri) {
        cardView.setVisibility(View.VISIBLE);
        ImageLoader.loadLargeImage(AddVoiceActivity.this, imgVoice, uri, R.mipmap.video_image);
    }

    private void selectFromAlbum() {
        if (ContextCompat.checkSelfPermission(AddVoiceActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddVoiceActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQCODE_SELALBUM);
        } else {
//            openAlbum();
            selectPicture();
        }
    }

    /**
     * 裁剪
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    /**
     * 从相冊选择照片（不裁切）
     */
    private void selectPicture() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);//Pick an item from the data
        intent.setType("image/*");//从全部图片中进行选择
        startActivityForResult(intent, SELECT_NO_PICTURE);
    }


    private void voiceIsexit() {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {

                insertVoiceBean();

                String lexiconContents = LoadDataUtils.getLexiconContents();

                e.onNext(lexiconContents);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        recognizer = GrammerUtils.getRecognizer(AddVoiceActivity.this, recognizer);

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

    private void insertVoiceBean() {

        String question = AppUtil.getText(etQuestion);
        String content = AppUtil.getText(etContent);

        voiceBean.setSaveTime(System.currentTimeMillis());
        voiceBean.setShowTitle(question);
        voiceBean.setVoiceAnswer(content);
        voiceBean.setExpression(AppUtil.resArray(R.array.expression)[curExpression]);
        voiceBean.setExpressionData(AppUtil.resArray(R.array.expression_data)[curExpression]);
        voiceBean.setAction(AppUtil.resArray(R.array.action)[curAction]);
        voiceBean.setActionData(AppUtil.resArray(R.array.action_order)[curAction]);
        setVoiceimg(voiceBean);

        List<String> keywordList = HanLP.extractKeyword(question, 5);
        if (keywordList != null && keywordList.size() > 0) {
            Print.e(keywordList);

            if (keywordList.size() == 1) {
                voiceBean.setKey1(question);
            } else {
                for (int j = 0; j < keywordList.size(); j++) {
                    String key = keywordList.get(j);
                    if (j == 0) {
                        voiceBean.setKey1(key);
                    } else if (j == 1) {
                        voiceBean.setKey2(key);
                    } else if (j == 2) {
                        voiceBean.setKey3(key);
                    } else if (j == 4) {
                        voiceBean.setKey4(key);
                    }
                }
            }
        } else {
            voiceBean.setKey1(question);
        }

        if (saveLocalId == -1) {//直接添加
            saveLocalId = mVoiceDBManager.insertForId(voiceBean);
        } else {//更新
            voiceBean.setId(saveLocalId);
            mVoiceDBManager.update(voiceBean);
        }
    }


    private void setVoiceimg(VoiceBean bean) {
        if (outputUri != null) {
            String imagePath = BitmapUtils.getPathByUri4kitkat(AddVoiceActivity.this, outputUri);
            Print.e(imagePath);
            bean.setImgUrl(imagePath);
        } else {
            if (noOutputPath != null) {
                bean.setImgUrl(noOutputPath);
            }
        }
    }

}
