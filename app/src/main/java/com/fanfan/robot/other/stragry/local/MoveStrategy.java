package com.fanfan.robot.other.stragry.local;

import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.listener.stragry.SpecialStrategy;
import com.fanfan.robot.other.stragry.Strategy;
import com.fanfan.robot.R;

/**
 * Created by android on 2018/2/6.
 */

public class MoveStrategy extends Strategy implements SpecialStrategy {

    @Override
    public SpecialType specialLocal(String speakTxt) {

        if (txtInTxt(speakTxt, R.string.Forward)) {
            return SpecialType.Forward;
        } else if (txtInTxt(speakTxt, R.string.Backoff)) {
            return SpecialType.Backoff;
        } else if (txtInTxt(speakTxt, R.string.Turnleft)) {
            return SpecialType.Turnleft;
        } else if (txtInTxt(speakTxt, R.string.Turnright)) {
            return SpecialType.Turnright;
        }
        return SpecialType.NoSpecial;
    }
}
