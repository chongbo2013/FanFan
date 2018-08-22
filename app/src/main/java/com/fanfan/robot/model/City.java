package com.fanfan.robot.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class City implements MultiItemEntity {

    public static final int TYPE_LEVEL_GROUP = 0;
    public static final int TYPE_LEVEL_COLUMN = 1;

    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFristPY;
    private String groupId;
    public int itemtype;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public void setFirstPY(String firstPY) {
        this.firstPY = firstPY;
    }

    public String getAllPY() {
        return allPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public String getAllFristPY() {
        return allFristPY;
    }

    public void setAllFristPY(String allFristPY) {
        this.allFristPY = allFristPY;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setItemtype(int itemtype) {
        this.itemtype = itemtype;
    }

    @Override
    public int getItemType() {
        return itemtype;
    }


}
