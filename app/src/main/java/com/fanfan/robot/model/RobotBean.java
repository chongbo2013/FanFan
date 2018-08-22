package com.fanfan.robot.model;

import com.fanfan.robot.app.enums.RobotType;

/**
 * Created by android on 2017/12/26.
 */

public class RobotBean {

    private RobotType type;
    private String order;

    public RobotType getType() {
        return type;
    }

    public void setType(RobotType type) {
        this.type = type;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }


}
