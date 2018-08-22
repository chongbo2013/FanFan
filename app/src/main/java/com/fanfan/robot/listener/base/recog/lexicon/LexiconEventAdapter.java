package com.fanfan.robot.listener.base.recog.lexicon;

import com.fanfan.robot.app.RobotInfo;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.seabreeze.log.Print;

public class LexiconEventAdapter implements LexiconListener {

    private HotLexiconListener listener;

    public LexiconEventAdapter(HotLexiconListener listener) {
        this.listener = listener;
    }

    @Override
    public void onLexiconUpdated(String s, SpeechError error) {
        if (error == null) {

//            if (RobotInfo.getInstance().getEngineType().equals(SpeechConstant.TYPE_CLOUD)) {
                RobotInfo.getInstance().setCloudBuild();
                RobotInfo.getInstance().setCloudUpdatelexicon();
                listener.onCloudLexiconUpdatedSuccess();
//            } else if (RobotInfo.getInstance().getEngineType().equals(SpeechConstant.TYPE_LOCAL)) {
//                RobotInfo.getInstance().setLocalBuild();
//                RobotInfo.getInstance().setLocalUpdatelexicon();
//                listener.onLocalLexiconUpdatedSuccess();
//            }
        } else {
            Print.e("词典更新失败,错误码：" + error.getErrorCode() + error.getErrorDescription());
            listener.onLexiconUpdatedError(error.getErrorCode(), error.getErrorDescription());
        }
    }
}
