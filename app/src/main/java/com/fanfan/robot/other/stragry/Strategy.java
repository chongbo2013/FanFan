package com.fanfan.robot.other.stragry;

import android.content.Context;
import android.content.res.Resources;

import com.fanfan.robot.app.NovelApp;

/**
 * Created by android on 2018/2/6.
 */

public class Strategy {

    protected boolean txtInTxt(String speakTxt, int res) {
        if (speakTxt.endsWith("。")) {
            speakTxt = speakTxt.substring(0, speakTxt.length() - 1);
        }
        return NovelApp.getInstance().getApplicationContext().getResources().getString(res).equals(speakTxt);
    }

}
