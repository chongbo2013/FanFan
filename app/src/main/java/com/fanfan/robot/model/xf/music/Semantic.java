package com.fanfan.robot.model.xf.music;

import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/11/13.
 */

public class Semantic {

    private String intent;
    private List<Slots> slots;

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<Slots> getSlots() {
        return slots;
    }

    public void setSlots(List<Slots> slots) {
        this.slots = slots;
    }
}
