package com.fanfan.robot.other.stragry.local;

import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.listener.stragry.SpecialStrategy;
import com.fanfan.robot.other.stragry.Strategy;
import com.fanfan.robot.R;

/**
 * Created by android on 2018/2/6.
 */

public class BackStrategy extends Strategy implements SpecialStrategy {

    @Override
    public SpecialType specialLocal(String speakTxt) {
        return txtInTxt(speakTxt, R.string.Back) ? SpecialType.Back : SpecialType.NoSpecial;
    }
}
