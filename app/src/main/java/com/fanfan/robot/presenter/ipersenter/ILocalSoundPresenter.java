package com.fanfan.robot.presenter.ipersenter;

import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.app.common.presenter.BasePresenter;
import com.fanfan.robot.app.common.presenter.BaseView;

/**
 * Created by android on 2017/12/20.
 */

public abstract class ILocalSoundPresenter implements BasePresenter {

    private ILocalSoundView mBaseView;

    public ILocalSoundPresenter(ILocalSoundView baseView) {
        mBaseView = baseView;
    }

    public abstract void doAnswer(String answer);

    public abstract void onResume();

    public abstract void onPause();

    public abstract boolean isSpeaking();

    public abstract void stopEvery();

    public abstract void onCompleted();

    public interface ILocalSoundView extends BaseView {

        /**
         * 移动
         *
         * @param type
         * @param result
         */
        void spakeMove(SpecialType type, String result);

        /**
         * 退出登陆
         */
        void logout();

        /**
         * 打开地图
         */
        void openMap();

        /**
         * 停止监听
         */
//        void stopListener();

        /**
         * 返回
         */
        void back();

        /**
         * 人工客服
         */
        void artificial();

        /**
         * 人脸识别
         *
         * @param type
         * @param result
         */
        void face(SpecialType type, String result);

        /**
         * 上下页
         *
         * @param type
         * @param result
         */
        void control(SpecialType type, String result);

        /**
         * 普通
         *
         * @param result
         */
        void refLocalPage(String result);

        /**
         * 说话完成
         */
        void onCompleted();
        
        /**
         * 关键词
         *
         * @param key1
         * @param key2
         * @param key3
         * @param key4
         */
        void refLocalPage(String key1, String key2, String key3, String key4);

    }
}
