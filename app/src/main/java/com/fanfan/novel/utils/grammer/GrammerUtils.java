package com.fanfan.novel.utils.grammer;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fanfan.novel.utils.FucUtil;
import com.fanfan.novel.utils.system.FileUtil;
import com.fanfan.robot.app.common.Constants;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;

import java.util.List;
import java.util.Set;

public class GrammerUtils {

    public static final int THRESHOLD = 30;

    public static final String GRAMMAR_BNF = "bnf";

    public static final String LOCAL_GRAMMAR_NAME = "call";

    public static final String GRAMMAR_LOCAL_FILE_NAME = "call.bnf";

    public static final String STANDARD_TEXT_ENCODING = "utf-8";

    public static final String RESULT_TYPE = "json";

    public static final String AUDIO_FORMAT = "wav";

    public static SpeechRecognizer getRecognizer(Context context, @NonNull SpeechRecognizer recognizer) {

        recognizer.setParameter(SpeechConstant.PARAMS, null);
        recognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        recognizer.setParameter(SpeechConstant.TEXT_ENCODING, STANDARD_TEXT_ENCODING);
        FileUtil.mkdir(Constants.GRM_PATH);
        // 设置语法构建路径
        recognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
        // 设置返回结果格式
        recognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置本地识别资源
        recognizer.setParameter(ResourceUtil.ASR_RES_PATH, FucUtil.getResAsrPath(context));
        // 设置本地识别使用语法id
        recognizer.setParameter(SpeechConstant.LOCAL_GRAMMAR, LOCAL_GRAMMAR_NAME);
        // 设置识别的门限值
        recognizer.setParameter(SpeechConstant.MIXED_THRESHOLD, "20");
        // 使用8k音频的时候请解开注释
//        recognizer.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        return recognizer;
    }

    public static String getLocalGrammar(Context context) {
        return FucUtil.readFile(context, GrammerUtils.GRAMMAR_LOCAL_FILE_NAME, STANDARD_TEXT_ENCODING);
    }

    /**
     * 获取L1的末尾位置
     *
     * @param content
     * @return
     */
    public static int getIndexL1(String content) {
        int indexStart = content.indexOf("<L2>:") - 3;
        if (indexStart <= 0) {
            throw new RuntimeException("L1 index error");
        }
        return indexStart;
    }

    public static int getIndexL1(StringBuilder content) {
        int indexStart = content.indexOf("<L2>:") - 3;
        if (indexStart <= 0) {
            throw new RuntimeException("start index error");
        }
        return indexStart;
    }

    /**
     * 获取L3的末尾位置
     *
     * @param content
     * @return
     */
    public static int getIndexL2(String content) {
        int indexContnet = content.indexOf("<L3>:") - 3;
        if (indexContnet <= 0) {
            throw new RuntimeException("contnet index error");
        }
        return indexContnet;
    }

    public static int getIndexL2(StringBuilder content) {
        int indexContnet = content.indexOf("<L3>:") - 3;
        if (indexContnet <= 0) {
            throw new RuntimeException("contnet index error");
        }
        return indexContnet;
    }

    /**
     * 获取L3的末尾位置
     *
     * @param content
     * @return
     */
    public static int getIndexL3(String content) {
        int indexContnet = content.indexOf("<L4>:") - 3;
        if (indexContnet <= 0) {
            throw new RuntimeException("contnet index error");
        }
        return indexContnet;
    }

    public static int getIndexL3(StringBuilder content) {
        int indexContnet = content.indexOf("<L4>:") - 3;
        if (indexContnet <= 0) {
            throw new RuntimeException("contnet index error");
        }
        return indexContnet;
    }

    /**
     * 获取end的末尾位置
     *
     * @param content
     * @return
     */
    public static int getIndexL4(String content) {
        int indexEnd = content.length() - 1;
        if (indexEnd <= 0) {
            throw new RuntimeException("end index error");
        }
        return indexEnd;
    }

    public static int getIndexL4(StringBuilder content) {
        int indexEnd = content.length() - 1;
        if (indexEnd <= 0) {
            throw new RuntimeException("end index error");
        }
        return indexEnd;
    }


    public static StringBuilder insertList(StringBuilder sb, List<String> locals, int index) {
        for (String local : locals) {
            sb.insert(index, "|" + local);
        }
        return sb;
    }

    public static StringBuilder insertSet(StringBuilder sb, Set<String> locals, int index) {
        for (String local : locals) {
            sb.insert(index, "|" + local);
        }
        return sb;
    }

    public static StringBuilder insertStr(StringBuilder sb, String local, int index) {
        return sb.insert(index, "|" + local);
    }
}
