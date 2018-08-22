package com.fanfan.robot.model;


import com.fanfan.robot.view.recyclerview.tree.base.BaseItemData;

import java.util.List;

/**
 * Created by android on 2018/1/10.
 */

public class Data<T> extends BaseItemData {

    private long data;

    private List<T> singleBeen;

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public List<T> getSingleBeen() {
        return singleBeen;
    }

    public void setSingleBeen(List<T> singleBeen) {
        this.singleBeen = singleBeen;
    }

}
