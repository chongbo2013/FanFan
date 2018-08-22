package com.fanfan.robot.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.model.xf.Telephone;
import com.fanfan.robot.model.xf.Cookbook;
import com.fanfan.robot.model.xf.Flight;
import com.fanfan.robot.model.xf.Joke;
import com.fanfan.robot.model.xf.News;
import com.fanfan.robot.model.xf.Poetry;
import com.fanfan.robot.model.xf.cmd.Slots;
import com.fanfan.robot.model.xf.constellation.Constellation;
import com.fanfan.robot.model.xf.constellation.Fortune;
import com.fanfan.robot.model.xf.englishEveryday.EnglishEveryday;
import com.fanfan.robot.model.xf.radio.Radio;
import com.fanfan.robot.model.xf.riddle.Riddle;
import com.fanfan.robot.model.xf.stock.Detail;
import com.fanfan.robot.model.xf.stock.Stock;
import com.fanfan.robot.model.xf.story.Story;
import com.fanfan.robot.model.xf.train.Train;
import com.fanfan.robot.model.xf.wordFinding.WordFinding;
import com.fanfan.robot.listener.base.AiuiListener;
import com.fanfan.novel.utils.media.AudioUtil;
import com.fanfan.novel.utils.FucUtil;
import com.fanfan.novel.utils.SpecialUtils;
import com.fanfan.novel.utils.music.MediaPlayerUtil2;
import com.fanfan.novel.utils.tele.TelNumMatch;
import com.fanfan.novel.utils.tele.TelePhoneUtils;
import com.fanfan.novel.utils.youdao.TranslateData;
import com.fanfan.robot.app.RobotInfo;
import com.fanfan.robot.presenter.ipersenter.ILineSoundPresenter;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIMessage;
import com.seabreeze.log.Print;
import com.youdao.sdk.app.Language;
import com.youdao.sdk.app.LanguageUtils;
import com.youdao.sdk.ydonlinetranslate.TranslateErrorCode;
import com.youdao.sdk.ydonlinetranslate.TranslateListener;
import com.youdao.sdk.ydonlinetranslate.TranslateParameters;
import com.youdao.sdk.ydonlinetranslate.Translator;
import com.youdao.sdk.ydtranslate.Translate;

import java.util.List;
import java.util.Random;

/**
 * Created by android on 2017/12/18.
 */

public class LineSoundPresenter extends ILineSoundPresenter implements
//        IatListener.RecognListener,
        AiuiListener.AiListener {

    private static final String ASSESTS_AIUI_CFG = "cfg/aiui_phone.cfg";

    private ILineSoundView mSoundView;

    private AIUIAgent mAIUIAgent;

    private AiuiListener aiuiListener;

    private boolean isTrans;

    private String mOtherText;

    public LineSoundPresenter(ILineSoundView baseView) {
        super(baseView);
        mSoundView = baseView;

        aiuiListener = new AiuiListener((Activity) mSoundView.getContext(), this);
    }

    @Override
    public void start() {
        initAiui();
    }

    @Override
    public void finish() {
        if (mAIUIAgent != null) {
            mAIUIAgent.destroy();
        }
    }

    @Override
    public void initAiui() {
        String params = FucUtil.readAssets(mSoundView.getContext(), ASSESTS_AIUI_CFG);
        mAIUIAgent = AIUIAgent.createAgent(mSoundView.getContext(), params, aiuiListener);
        AIUIMessage startMsg = new AIUIMessage(AIUIConstant.CMD_START, 0, 0, null, null);
        mAIUIAgent.sendMessage(startMsg);
    }

    @Override
    public void onlineResult(String result) {
        if (result.length() == 1) {
            mSoundView.onCompleted();
            return;
        }
        SpecialType specialType = SpecialUtils.doesExist(mSoundView.getContext().getResources(), result);
        if (specialType == SpecialType.NoSpecial) {
            aiuiWriteText(result);
        } else if (specialType == SpecialType.Music) {
            mSoundView.special(result, SpecialType.Music);
        } else if (specialType == SpecialType.Dance) {
            mSoundView.special(result, SpecialType.Dance);
        } else if (specialType == SpecialType.Hand) {
            mSoundView.special(result, SpecialType.Hand);
        } else if (specialType == SpecialType.Story) {
            aiuiWriteText(result);
        } else if (specialType == SpecialType.Joke) {
            aiuiWriteText(result);
        } else if (specialType == SpecialType.StopListener) {
            mSoundView.setSpeech(false);
        } else if (specialType == SpecialType.Fanfan || specialType == SpecialType.Video
                || specialType == SpecialType.Problem || specialType == SpecialType.Face
                || specialType == SpecialType.Seting_up || specialType == SpecialType.Public_num
                || specialType == SpecialType.Navigation || specialType == SpecialType.MultiMedia
                || specialType == SpecialType.TrainInquiry || specialType == SpecialType.PanoramicMap
                || specialType == SpecialType.TalkBack || specialType == SpecialType.StationService
                || specialType == SpecialType.InternalNavigation || specialType == SpecialType.TrafficTravel) {
            mSoundView.startPage(specialType);
        } else if (specialType == SpecialType.Forward || specialType == SpecialType.Backoff ||
                specialType == SpecialType.Turnleft || specialType == SpecialType.Turnright) {
            mSoundView.spakeMove(specialType, result);
        } else if (specialType == SpecialType.Map) {
            mSoundView.openMap();
        } else if (specialType == SpecialType.Vr) {
            mSoundView.openVr();
        } else if (specialType == SpecialType.Logout) {
            mSoundView.spakeLogout();
        }
    }

    @Override
    public void aiuiWriteText(String result) {
        String params = "data_type=text";
        AIUIMessage msgWakeup = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, null, null);
        mAIUIAgent.sendMessage(msgWakeup);
        AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, result.trim().getBytes());
        mAIUIAgent.sendMessage(msg);
    }


    @Override
    public void stopVoice() {
        MediaPlayerUtil2.getInstance().stopMusic();
    }

    @Override
    public void onDoAnswer(String question, String finalText) {
        if (finalText == null) {
            onCompleted();
            mSoundView.refHomePage(question, finalText);
        } else {
            if (RobotInfo.getInstance().isQueryLanage()) {
                if (isTrans) {
                    isTrans = false;
                    mSoundView.doAiuiAnwer(question, finalText);
                    mSoundView.refHomePage(question, finalText);
                } else {
                    query(finalText);
                }
            } else {
                mSoundView.doAiuiAnwer(question, finalText);
                mSoundView.refHomePage(question, finalText);
            }
        }
    }

    @Override
    public void onDoAnswer(String question, String text, News news) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                mSoundView.doAiuiUrl(question, news.getUrl());
                mSoundView.refHomePage(question, news);
            } else {
                query(news.getContent());
            }
        } else {
            mSoundView.doAiuiUrl(question, news.getUrl());
            mSoundView.refHomePage(question, news);
        }
    }

    @Override
    public void onDoAnswer(String question, String text, Cookbook cookbook) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                mSoundView.doAiuiAnwer(question, text + ", " + cookbook.getSteps());
                mSoundView.refHomePage(question, cookbook);
            } else {
                query(cookbook.getSteps());
            }
        } else {
            mSoundView.doAiuiAnwer(question, text + ", " + cookbook.getSteps());
            mSoundView.refHomePage(question, cookbook);
        }
    }

    @Override
    public void onDoAnswer(String question, Poetry poetry) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                mSoundView.doAiuiAnwer(question, poetry.getContent());
                mSoundView.refHomePage(question, poetry);
            } else {
                query(poetry.getContent());
            }
        } else {
            mSoundView.doAiuiAnwer(question, poetry.getContent());
            mSoundView.refHomePage(question, poetry);
        }
    }

    @Override
    public void onDoAnswer(String question, String finalText, Joke joke) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                if (TextUtils.isEmpty(joke.getMp3Url())) {
                    mSoundView.doAiuiAnwer(question, joke.getTitle() + " : " + joke.getContent());
                    mSoundView.refHomePage(question, joke.getTitle() + " : " + joke.getContent());
                } else {
                    mSoundView.refHomePage(question, finalText);
                    mSoundView.doAiuiUrl(question, joke.getMp3Url());
                }
            } else {
                query(finalText);
            }
        } else {
            if (TextUtils.isEmpty(joke.getMp3Url())) {
                mSoundView.doAiuiAnwer(question, joke.getTitle() + " : " + joke.getContent());
                mSoundView.refHomePage(question, joke.getTitle() + " : " + joke.getContent());
            } else {
                mSoundView.refHomePage(question, finalText);
                mSoundView.doAiuiUrl(question, joke.getMp3Url());
            }
        }
    }

    @Override
    public void onDoAnswer(String question, String finalText, Story story) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                mSoundView.refHomePage(question, finalText);
                mSoundView.doAiuiUrl(question, story.getPlayUrl());
            } else {
                query(finalText);
            }
        } else {
            mSoundView.refHomePage(question, finalText);
            mSoundView.doAiuiUrl(question, story.getPlayUrl());
        }
    }

    @Override
    public void onDoAnswer(String question, String finalText, List<Train> trains, Train train0) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                mSoundView.doAiuiAnwer(question, finalText);
                mSoundView.refHomePage(question, finalText);
                for (int i = 0; i < trains.size(); i++) {
                    Train train = trains.get(i);
                    mSoundView.refHomePage(null, train.getEndtime_for_voice() + "的" + train.getTrainType() + " " + train.getTrainNo() + "" +
                            " " + train.getOriginStation() + " -- " + train.getTerminalStation()
                            + " , 运行时间：" + train.getRunTime());
                }
            } else {
                query(finalText);
            }
        } else {
            mSoundView.doAiuiAnwer(question, finalText);
            mSoundView.refHomePage(question, finalText);
            for (int i = 0; i < trains.size(); i++) {
                Train train = trains.get(i);
                mSoundView.refHomePage(null, train.getEndtime_for_voice() + "的" + train.getTrainType() + " " + train.getTrainNo() + "" +
                        " " + train.getOriginStation() + " -- " + train.getTerminalStation()
                        + " , 运行时间：" + train.getRunTime());
            }
        }
    }

    @Override
    public void onDoAnswer(String question, String finalText, List<Flight> flights, Flight flight0) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                mSoundView.doAiuiAnwer(question, finalText);
                mSoundView.refHomePage(question, finalText);
                int total;
                if (flights.size() < 10) {
                    total = flights.size();
                } else {
                    total = 10;
                }
                for (int i = 0; i < total; i++) {
                    Flight flight = flights.get(i);
                    mSoundView.refHomePage(null, flight.getEndtime_for_voice() + "从" + flight.getDepartCity() + "出发， "
                            + flight.getEndtime_for_voice() + "到达" + flight.getArriveCity() + ", " +
                            flight.getCabinInfo() + "价格是：" + flight.getPrice());
                }
            } else {
                query(finalText);
            }
        } else {
            mSoundView.doAiuiAnwer(question, finalText);
            mSoundView.refHomePage(question, finalText);
            int total;
            if (flights.size() < 10) {
                total = flights.size();
            } else {
                total = 10;
            }
            for (int i = 0; i < total; i++) {
                Flight flight = flights.get(i);
                mSoundView.refHomePage(null, flight.getEndtime_for_voice() + "从" + flight.getDepartCity() + "出发， "
                        + flight.getEndtime_for_voice() + "到达" + flight.getArriveCity() + ", " +
                        flight.getCabinInfo() + "价格是：" + flight.getPrice());
            }
        }
    }

    @Override
    public void onNoAnswer(String question, String finalText, String otherText) {
        mOtherText = otherText;
        onDoAnswer(question, finalText);
    }

    @Override
    public void onDoAnswer(String question, String finalText, Radio radio) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                mSoundView.refHomePage(question, radio);
                mSoundView.doAiuiUrl(question, radio.getUrl());
            } else {
                query(finalText);
            }
        } else {
            mSoundView.refHomePage(question, radio);
            mSoundView.doAiuiUrl(question, radio.getUrl());
        }
    }

    @Override
    public void onMusic(String question, String finalText) {
        mOtherText = "音乐播放中...";
        onDoAnswer(question, finalText);
    }

    @Override
    public void onTranslation(String question, String value) {
        onDoAnswer(question, value);
        Print.e(value);
    }

    @Override
    public void onDoAnswer(String question, Slots slotsCmd) {
        int volume = AudioUtil.getInstance(mSoundView.getContext()).getMediaVolume();
        int maxVolume = AudioUtil.getInstance(mSoundView.getContext()).getMediaMaxVolume();
        int node = maxVolume / 5;
        String answer = "不支持此音量控制";
        if (slotsCmd.getName().equals("insType")) {
            switch (slotsCmd.getValue()) {
                case "volume_plus":
                    if (volume == maxVolume) {
                        answer = "当前已是最大音量了";
                    } else {
                        answer = "已增大音量";
                        volume = volume + node;
                        if (volume > maxVolume) {
                            volume = maxVolume;
                        }
                        AudioUtil.getInstance(mSoundView.getContext()).setMediaVolume(volume);
                    }
                    break;
                case "volume_minus":
                    if (volume == 0) {
                        answer = "当前已是最小音量了";
                    } else {
                        answer = "已减小音量";
                        volume = volume - node;
                        if (volume < 0) {
                            volume = 0;
                        }
                        AudioUtil.getInstance(mSoundView.getContext()).setMediaVolume(volume);
                    }
                    break;
                case "unmute":
                    answer = "您可以说 “增大音量” 或 “减小音量” ，我会帮您改变的";
                    break;
            }
        }

        onDoAnswer(question, answer);
    }

    @Override
    public void onDoAnswer(String question, String finalText, EnglishEveryday englishEveryday) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                mSoundView.doAiuiAnwer(question, englishEveryday.getContent());
                mSoundView.refHomePage(question, englishEveryday);
            } else {
                query(finalText);
            }
        } else {
            mSoundView.doAiuiAnwer(question, englishEveryday.getContent());
            mSoundView.refHomePage(question, englishEveryday);
        }
    }

    @Override
    public void onDoAnswer(String question, String finalText, Constellation constellation) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                StringBuilder sb = new StringBuilder();
                List<Fortune> fortunes = constellation.getFortune();
                sb.append(finalText);
                for (int i = 0; i < fortunes.size(); i++) {
                    Fortune fortune = fortunes.get(i);
                    sb.append(fortune.getName()).append(" : ").append(fortune.getDescription());
                }
                mSoundView.doAiuiAnwer(question, sb.toString());
                mSoundView.refHomePage(question, sb.toString());
            } else {
                query(finalText);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            List<Fortune> fortunes = constellation.getFortune();
            sb.append(finalText);
            for (int i = 0; i < fortunes.size(); i++) {
                Fortune fortune = fortunes.get(i);
                sb.append(fortune.getName()).append(" : ").append(fortune.getDescription());
            }
            mSoundView.doAiuiAnwer(question, sb.toString());
            mSoundView.refHomePage(question, sb.toString());
        }
    }

    @Override
    public void onDoAnswer(String question, String finalText, Stock stock) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                StringBuilder sb = new StringBuilder();
                sb.append(finalText);
                sb.append("\n截止到").append(stock.getUpdateDateTime()).append(", ").append(stock.getName()).append(" ")
                        .append(stock.getStockCode()).append(", 当前价格为 ： ").append(stock.getOpeningPrice()).append(", 上升率为 ： ")
                        .append(stock.getRiseRate()).append(" 详情请查看列表信息");
                mSoundView.doAiuiAnwer(question, sb.toString());

                sb.append("\n最高价 ： ").append(stock.getHighPrice());
                sb.append("  最低价 ： ").append(stock.getLowPrice());
                List<Detail> details = stock.getDetail();
                for (int i = 0; i < details.size(); i++) {
                    Detail detail = details.get(i);
                    sb.append("\n").append(detail.getCount()).append(" ").append(detail.getRole()).append(" ").append(detail.getPrice());
                }
                mSoundView.refHomePage(question, sb.toString());
            } else {
                query(finalText);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(finalText);
            sb.append("\n截止到").append(stock.getUpdateDateTime()).append(", ").append(stock.getName()).append(" ")
                    .append(stock.getStockCode()).append(", 当前价格为 ： ").append(stock.getOpeningPrice()).append(", 上升率为 ： ")
                    .append(stock.getRiseRate()).append(" 详情请查看列表信息");
            mSoundView.doAiuiAnwer(question, sb.toString());

            sb.append("\n最高价 ： ").append(stock.getHighPrice());
            sb.append("  最低价 ： ").append(stock.getLowPrice());
            List<Detail> details = stock.getDetail();
            for (int i = 0; i < details.size(); i++) {
                Detail detail = details.get(i);
                sb.append("\n").append(detail.getCount()).append(" ").append(detail.getRole()).append(" ").append(detail.getPrice());
            }
            mSoundView.refHomePage(question, sb.toString());
        }
    }

    @Override
    public void onDoAnswer(String question, String finalText, Riddle riddle) {
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                mSoundView.doAiuiAnwer(question, riddle.getTitle() + "\n谜底请查看列表");
                mSoundView.refHomePage(question, riddle.getTitle() + "\n\n" + riddle.getAnswer() + "\n");
            } else {
                query(finalText);
            }
        } else {
            mSoundView.doAiuiAnwer(question, riddle.getTitle() + "\n谜底请查看列表");
            mSoundView.refHomePage(question, riddle.getTitle() + "\n\n" + riddle.getAnswer() + "\n");
        }
    }

    @Override
    public void onDoAnswer(String question, String finalText, WordFinding wordFinding) {
        List<String> results;
        int count = 5;
        StringBuilder sb = new StringBuilder();
        if (finalText.contains("反义词")) {
            results = wordFinding.getAntonym();
        } else {
            results = wordFinding.getSynonym();
        }
        if (results.size() > count) {
            int random = new Random().nextInt(results.size() - count);
            for (int i = 0; i < count; i++) {
                sb.append("\n").append(results.get(random + i));
            }
        } else {
            for (int i = 0; i < results.size(); i++) {
                sb.append("\n").append(results.get(i));
            }
        }
        if (RobotInfo.getInstance().isQueryLanage()) {
            if (isTrans) {
                isTrans = false;
                mSoundView.doAiuiAnwer(question, sb.toString());
                mSoundView.refHomePage(question, sb.toString());
            } else {
                query(finalText);
            }
        } else {
            mSoundView.doAiuiAnwer(question, sb.toString());
            mSoundView.refHomePage(question, sb.toString());
        }
    }

    @Override
    public void onDoDial(String question, String value) {
        if (TelNumMatch.matchNum(value) == 5 || TelNumMatch.matchNum(value) == 4) {
            List<Telephone> telephones = TelePhoneUtils.queryContacts(mSoundView.getContext(), value);
            if (telephones != null && telephones.size() > 0) {
                if (telephones.size() == 1) {
                    List<String> phones = telephones.get(0).getPhone();
                    if (phones != null && phones.size() > 0) {
                        if (phones.size() == 1) {
                            String phoneNumber = phones.get(0);
                            mSoundView.doAiuiAnwer(question, "为您拨打 ： " + phoneNumber);
//                            mSoundView.refHomePage(question, "为您拨打 ： " + phoneNumber);
                            mSoundView.doCallPhone(phoneNumber);
                        } else {
                            mSoundView.doAiuiAnwer(question, "为您找到如下号码 ： ");
                            mSoundView.refHomePage(question, "为您找到如下号码 ： ");
                            for (String phone : phones) {
                                mSoundView.refHomePage(null, phone);
                            }
                        }
                    } else {
                        mSoundView.doAiuiAnwer(question, "暂无此名字电话号码");
                        mSoundView.refHomePage(question, "通讯录中暂无");
                    }
                } else {
                    mSoundView.doAiuiAnwer(question, "为您匹配到如下姓名 ： ");
                    mSoundView.refHomePage(question, "为您匹配到如下姓名 ： ");
                    for (Telephone telephone : telephones) {
                        mSoundView.refHomePage(null, telephone.getName());
                    }
                }
            } else {
                mSoundView.doAiuiAnwer(question, "通讯录中暂无" + value);
                mSoundView.refHomePage(question, "通讯录中暂无" + value);
            }
        } else {
            mSoundView.doAiuiAnwer(question, "为您拨打 ： " + value);
            mSoundView.doCallPhone(value);
        }
    }

    @Override
    public void onError() {
        initAiui();
    }

    @Override
    public void onAIUIDowm() {

    }

    @Override
    public void onNoAnswer(String question) {
        Print.e("noAnswer : " + question);
        mSoundView.noAnswer(question);
    }

    private void onCompleted() {
        mSoundView.onCompleted();
    }

    private void query(final String source) {
        Language langFrom = LanguageUtils.getLangByName("中文");
        Language langTo = LanguageUtils.getLangByName("英文");

        TranslateParameters tps = new TranslateParameters.Builder()
                .source("youdao").from(langFrom).to(langTo).timeout(3000).build();// appkey可以省略

        Translator translator = Translator.getInstance(tps);

        translator.lookup(source, new TranslateListener() {

            @Override
            public void onResult(Translate result, String input) {
                TranslateData translateData = new TranslateData(System.currentTimeMillis(), result);
                isTrans = true;
                onDoAnswer(source, translateData.translates());
            }

            @Override
            public void onError(TranslateErrorCode error) {
                onCompleted();
            }
        });
    }

    @Override
    public void playVoice(String url) {
        if (TextUtils.isEmpty(url))
            return;

        MediaPlayerUtil2.getInstance().setListener(new MediaPlayerUtil2.OnMusicCompletionListener() {
            @Override
            public void onFastForwarding() {
                onCompleted();
            }
        });
        MediaPlayerUtil2.getInstance().playMusic(url);
    }

}
