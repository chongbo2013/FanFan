package com.fanfan.robot.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/8/008.
 */

public class ImageBean implements Serializable {

    private String top;
    private String bottom;
    private String imgUrl;

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
