package com.fanfan.robot.model.xf;

import com.fanfan.robot.model.xf.translation.SlotsBean;

import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/11/16.
 */

public class Translation {

    /**
     * intent : TRANSLATION
     * slots : [{"name":"content","value":"苹果"},{"name":"source","value":"cn"},{"name":"target","value":"en"}]
     */

    private String intent;
    private List<SlotsBean> slots;

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<SlotsBean> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotsBean> slots) {
        this.slots = slots;
    }

}
