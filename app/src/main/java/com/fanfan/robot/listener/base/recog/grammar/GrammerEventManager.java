package com.fanfan.robot.listener.base.recog.grammar;

import android.content.Context;

import com.fanfan.novel.utils.FucUtil;
import com.fanfan.novel.utils.grammer.GrammerUtils;
import com.fanfan.novel.utils.system.AppUtil;
import com.fanfan.novel.utils.system.FileUtil;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.listener.base.recog.lexicon.LexiconEventAdapter;
import com.fanfan.robot.listener.base.recog.lexicon.MyHotLexiconListener;
import com.fanfan.robot.model.hotword.HotWord;
import com.fanfan.robot.model.hotword.Userword;
import com.fanfan.youtu.utils.GsonUtil;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.seabreeze.log.Print;

import java.util.ArrayList;
import java.util.List;

public class GrammerEventManager implements GrammarListener {


//    private static final String GRAMMAR_BNF = "bnf";
    private static final String GRAMMAR_ABNF = "abnf";

//    public static final String LOCAL_GRAMMAR_NAME = "local";
//    private static final String GRAMMAR_LOCAL_FILE_NAME = "local.bnf";
    private static final String GRAMMAR_CLOUD_FILE_NAME = "abnf.abnf";

    public static final String STANDARD_TEXT_ENCODING = "utf-8";

    private Context context;
    private SpeechRecognizer recognizer;
    private FGrammarListener listener;

    private LexiconEventAdapter adapter;

    public GrammerEventManager(Context context, SpeechRecognizer recognizer, final FGrammarListener listener) {
        this.context = context;
        this.recognizer = recognizer;
        this.listener = listener;
        MyHotLexiconListener lexiconListener = new MyHotLexiconListener() {
            @Override
            public void onCloudLexiconUpdatedSuccess() {
                super.onCloudLexiconUpdatedSuccess();
                listener.onCloudGrammarBuildSuccess();
                structure();
            }

//            @Override
//            public void onLocalLexiconUpdatedSuccess() {
//                super.onLocalLexiconUpdatedSuccess();
//                listener.onLocalGrammarBuildSuccess();
//                structure();
//            }
        };
        adapter = new LexiconEventAdapter(lexiconListener);
    }

    public void structure() {
        String grammarType;
        String content;
        if (RobotInfo.getInstance().isCloudBuild()) {
            RobotInfo.getInstance().setEngineType(SpeechConstant.TYPE_LOCAL);
            if (!RobotInfo.getInstance().isLocalBuild()) {
//                recognizer.setParameter(SpeechConstant.PARAMS, null);
//                recognizer.setParameter(SpeechConstant.ENGINE_TYPE, RobotInfo.getInstance().getEngineType());
//                recognizer.setParameter(SpeechConstant.TEXT_ENCODING, STANDARD_TEXT_ENCODING);
//                FileUtil.mkdir(Constants.GRM_PATH);
//                recognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
//                recognizer.setParameter(ResourceUtil.ASR_RES_PATH, FucUtil.getResAsrPath(context));
//                recognizer.setParameter(SpeechConstant.LOCAL_GRAMMAR, LOCAL_GRAMMAR_NAME);
//                recognizer.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
//                content = FucUtil.readFile(context, GRAMMAR_LOCAL_FILE_NAME, STANDARD_TEXT_ENCODING);
//                grammarType = GRAMMAR_BNF;
//                recognizer.buildGrammar(grammarType, content, this);
                recognizer = GrammerUtils.getRecognizer(context, recognizer);

                String localGrammar = GrammerUtils.getLocalGrammar(context);

                StringBuilder sb = GrammerUtils.insertList(new StringBuilder(localGrammar), AppUtil.getLocalStrings(), GrammerUtils.getIndexL1(localGrammar));

                grammarType = GrammerUtils.GRAMMAR_BNF;
                Print.e(sb.toString());
                recognizer.buildGrammar(grammarType, sb.toString(), this);
            }

        } else {
            recognizer.setParameter(SpeechConstant.PARAMS, null);
            recognizer.setParameter(SpeechConstant.ENGINE_TYPE, RobotInfo.getInstance().getEngineType());
            recognizer.setParameter(SpeechConstant.TEXT_ENCODING, STANDARD_TEXT_ENCODING);
            content = FucUtil.readFile(context, GRAMMAR_CLOUD_FILE_NAME, STANDARD_TEXT_ENCODING);
            grammarType = GRAMMAR_ABNF;
            recognizer.buildGrammar(grammarType, content, this);
        }
        if (RobotInfo.getInstance().isCloudBuild() && RobotInfo.getInstance().isLocalBuild() &&
                RobotInfo.getInstance().isCloudUpdatelexicon()
//                && RobotInfo.getInstance().isLocalUpdatelexicon()
                ) {
            RobotInfo.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);
            RobotInfo.getInstance().setInitialization(true);
            if (RobotInfo.getInstance().isInitialization()) {
                listener.onGrammarBuildSuccess();
            } else {
                structure();
            }
        }
    }

    @Override
    public void onBuildFinish(String s, SpeechError error) {
        if (error == null) {
            if (RobotInfo.getInstance().getEngineType().equals(SpeechConstant.TYPE_LOCAL)) {
//                if (RobotInfo.getInstance().isLocalUpdatelexicon()) {
                    RobotInfo.getInstance().setLocalBuild();
                    listener.onLocalGrammarBuildSuccess();
                    // Add
                structure();
//                } else {
//                    String lexiconContents = AppUtil.words2Contents();
//                    updateLocation("voice", lexiconContents);
//                }
            } else if (RobotInfo.getInstance().getEngineType().equals(SpeechConstant.TYPE_CLOUD)) {

                if (!RobotInfo.getInstance().isCloudUpdatelexicon()) {
                    List<String> words = AppUtil.getLocalStrings();

                    Userword userword = new Userword();
                    userword.setName("userword");
                    userword.setWords(words);
                    List<Userword> userwordList = new ArrayList<>();
                    userwordList.add(userword);
                    HotWord hotWord = new HotWord(userwordList);

                    updateLocation("userword", GsonUtil.GsonString(hotWord));
                } else {
                    RobotInfo.getInstance().setCloudBuild();
                    listener.onCloudGrammarBuildSuccess();
                }
            }
        } else {
            listener.onGrammarBuildError(error.getErrorCode(), error.getErrorDescription());
        }
    }

    private void updateLocation(String lexiconName, String lexiconContents) {
        recognizer.setParameter(SpeechConstant.PARAMS, null);
//        if (RobotInfo.getInstance().getEngineType().equals(SpeechConstant.TYPE_CLOUD)) {
            recognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
//        } else if (RobotInfo.getInstance().getEngineType().equals(SpeechConstant.TYPE_LOCAL)) {
//            recognizer.setParameter(SpeechConstant.ENGINE_TYPE, RobotInfo.getInstance().getEngineType());
//            recognizer.setParameter(ResourceUtil.ASR_RES_PATH, FucUtil.getResAsrPath(context));
//            recognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
//            recognizer.setParameter(SpeechConstant.GRAMMAR_LIST, LOCAL_GRAMMAR_NAME);
//            recognizer.setParameter(SpeechConstant.TEXT_ENCODING, STANDARD_TEXT_ENCODING);
//        }
        recognizer.updateLexicon(lexiconName, lexiconContents, adapter);
    }
}
