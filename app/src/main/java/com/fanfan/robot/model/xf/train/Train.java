package com.fanfan.robot.model.xf.train;

import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/11/13.
 */

public class Train {

    private String arrivalTime;
    private String endtime_for_voice;
    private int endtimestamp;
    private String originStation;
    private List<Price> price;
    private String runTime;
    private String startTime;
    private String startTimeStamp;
    private String starttime_for_voice;
    private int starttimestamp;
    private String terminalStation;
    private String trainNo;
    private String trainType;

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getEndtime_for_voice() {
        return endtime_for_voice;
    }

    public void setEndtime_for_voice(String endtime_for_voice) {
        this.endtime_for_voice = endtime_for_voice;
    }

    public int getEndtimestamp() {
        return endtimestamp;
    }

    public void setEndtimestamp(int endtimestamp) {
        this.endtimestamp = endtimestamp;
    }

    public String getOriginStation() {
        return originStation;
    }

    public void setOriginStation(String originStation) {
        this.originStation = originStation;
    }

    public List<Price> getPrice() {
        return price;
    }

    public void setPrice(List<Price> price) {
        this.price = price;
    }

    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(String startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public String getStarttime_for_voice() {
        return starttime_for_voice;
    }

    public void setStarttime_for_voice(String starttime_for_voice) {
        this.starttime_for_voice = starttime_for_voice;
    }

    public int getStarttimestamp() {
        return starttimestamp;
    }

    public void setStarttimestamp(int starttimestamp) {
        this.starttimestamp = starttimestamp;
    }

    public String getTerminalStation() {
        return terminalStation;
    }

    public void setTerminalStation(String terminalStation) {
        this.terminalStation = terminalStation;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }
}
