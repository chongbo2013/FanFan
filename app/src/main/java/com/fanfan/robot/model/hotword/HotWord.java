package com.fanfan.robot.model.hotword;

import java.util.List;

/**
 * Created by android on 2017/12/29.
 */

public class HotWord {

    private List<Userword> userword;

    public HotWord(List<Userword> userword) {
        this.userword = userword;
    }

    public void setUserword(List<Userword> userword) {
        this.userword = userword;
    }

    public List<Userword> getUserword() {
        return userword;
    }


}
