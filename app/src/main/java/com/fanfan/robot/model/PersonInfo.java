package com.fanfan.robot.model;

import java.io.Serializable;

/**
 * Created by android on 2017/12/22.
 */

public class PersonInfo implements Serializable {

    private String name;
    private String gender;
    private String family;
    private String birth;
    private String address;
    private String headUrl;
    private String IDCard;
    private String department;//签发机关
    private String strartDate;//有效日期
    private String endDate;//有效日期
    private String fristPFInfo;
    private String secondPFInfo;
    private String saveUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStrartDate() {
        return strartDate;
    }

    public void setStrartDate(String strartDate) {
        this.strartDate = strartDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFristPFInfo() {
        return fristPFInfo;
    }

    public void setFristPFInfo(String fristPFInfo) {
        this.fristPFInfo = fristPFInfo;
    }

    public String getSecondPFInfo() {
        return secondPFInfo;
    }

    public void setSecondPFInfo(String secondPFInfo) {
        this.secondPFInfo = secondPFInfo;
    }

    public String getSaveUrl() {
        return saveUrl;
    }

    public void setSaveUrl(String saveUrl) {
        this.saveUrl = saveUrl;
    }
}
