package com.fanfan.robot.other.stragry;

import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.listener.stragry.SpecialStrategy;

/**
 * Created by android on 2018/2/6.
 */

public class TranficCalculator {

    public SpecialType specialLocal(String speakTxt, SpecialStrategy mStrategy) {
        return mStrategy.specialLocal(speakTxt);
    }
}
