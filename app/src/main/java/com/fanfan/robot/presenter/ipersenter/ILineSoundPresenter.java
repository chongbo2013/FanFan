package com.fanfan.robot.presenter.ipersenter;

import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.app.common.presenter.BasePresenter;
import com.fanfan.robot.app.common.presenter.BaseView;
import com.fanfan.robot.model.VoiceBean;
import com.fanfan.robot.model.xf.Cookbook;
import com.fanfan.robot.model.xf.News;
import com.fanfan.robot.model.xf.Poetry;
import com.fanfan.robot.model.xf.englishEveryday.EnglishEveryday;
import com.fanfan.robot.model.xf.radio.Radio;
import com.fanfan.robot.model.xf.train.Train;

import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public abstract class ILineSoundPresenter implements BasePresenter {

    private ILineSoundView mBaseView;

    public ILineSoundPresenter(ILineSoundView baseView) {
        mBaseView = baseView;
    }

//    public abstract void initIat();

    public abstract void initAiui();

//    public abstract void buildIat();

//    public abstract void startRecognizerListener(boolean focus);

//    public abstract void stopRecognizerListener();

    public abstract void onlineResult(String result);

    public abstract void aiuiWriteText(String text);

    public abstract void stopVoice();

//    public abstract void setSpeech(boolean speech);

//    public abstract void setOpening(boolean isOpen);

    public abstract void playVoice(String url);

    public interface ILineSoundView extends BaseView {


        void aiuiForLocal(String result);

        void doAiuiAnwer(String problem, String anwer);

        void doAiuiUrl(String question, String url);

        void refHomePage(VoiceBean voiceBean);

        void refHomePage(String question, String finalText);

        void refHomePage(String question, String finalText, String url);

        void refHomePage(String question, News news);

        void refHomePage(String question, Radio radio);

        void refHomePage(String question, Poetry poetry);

        void refHomePage(String question, Cookbook cookbook);

        void refHomePage(String question, EnglishEveryday englishEveryday);

        void special(String result, SpecialType type);

        void doCallPhone(String value);

        void startPage(SpecialType specialType);

        void spakeMove(SpecialType specialType, String result);

        void openMap();

        void openVr();

        void spakeLogout();

        void onCompleted();

        void noAnswer(String question);

        void setSpeech(boolean b);
    }


}
