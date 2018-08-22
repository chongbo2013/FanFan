package com.fanfan.robot.other.stragry.local;

import com.fanfan.robot.app.enums.SpecialType;
import com.fanfan.robot.listener.stragry.SpecialStrategy;
import com.fanfan.robot.other.stragry.Strategy;
import com.fanfan.robot.R;

/**
 * Created by android on 2018/2/6.
 */

public class FaceStrategy extends Strategy implements SpecialStrategy {

    @Override
    public SpecialType specialLocal(String speakTxt) {
        if (txtInTxt(speakTxt, R.string.Face_check_in)) {
            return SpecialType.Face_check_in;
        } else if (txtInTxt(speakTxt, R.string.Instagram)) {
            return SpecialType.Instagram;
        } else if (txtInTxt(speakTxt, R.string.Witness_contrast)) {
            return SpecialType.Witness_contrast;
        } else if (txtInTxt(speakTxt, R.string.Face_lifting_area)) {
            return SpecialType.Face_lifting_area;
        }
        return SpecialType.NoSpecial;
    }

}
