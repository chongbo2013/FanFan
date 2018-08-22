package com.fanfan.robot.model;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dell on 2018/1/22.
 */
public class AirQuery {

    private Long id;
    private String airName;//航班信息
    private String airPlanTime;//计划起飞
    private String airActualTime;//实际起飞
    private String airStart;//出发地
    private String airPlanArriveTime;//计划到达

    private String airArrive;//到达地
    private String airOnTime;//准点率
    private String airStatus;//状态


    public AirQuery(String airName, String airPlanTime, String airActualTime,
                    String airStart, String airPlanArriveTime,
                    String airArrive, String airOnTime, String airStatus) {
        this.airName = airName;
        this.airPlanTime = airPlanTime;
        this.airActualTime = airActualTime;
        this.airStart = airStart;
        this.airPlanArriveTime = airPlanArriveTime;

        this.airArrive = airArrive;
        this.airOnTime = airOnTime;
        this.airStatus = airStatus;
    }


    @Generated(hash = 1472524479)
    public AirQuery(Long id, String airName, String airPlanTime,
                    String airActualTime, String airStart, String airPlanArriveTime,
                    String airArrive, String airOnTime, String airStatus) {
        this.id = id;
        this.airName = airName;
        this.airPlanTime = airPlanTime;
        this.airActualTime = airActualTime;
        this.airStart = airStart;
        this.airPlanArriveTime = airPlanArriveTime;
        this.airArrive = airArrive;
        this.airOnTime = airOnTime;
        this.airStatus = airStatus;
    }


    @Generated(hash = 348962291)
    public AirQuery() {
    }


    public String getAirName() {
        return this.airName;
    }

    public void setAirName(String airName) {
        this.airName = airName;
    }

    public String getAirPlanTime() {
        return this.airPlanTime;
    }

    public void setAirPlanTime(String airPlanTime) {
        this.airPlanTime = airPlanTime;
    }

    public String getAirActualTime() {
        return this.airActualTime;
    }

    public void setAirActualTime(String airActualTime) {
        this.airActualTime = airActualTime;
    }

    public String getAirStart() {
        return this.airStart;
    }

    public void setAirStart(String airStart) {
        this.airStart = airStart;
    }

    public String getAirPlanArriveTime() {
        return this.airPlanArriveTime;
    }

    public void setAirPlanArriveTime(String airPlanArriveTime) {
        this.airPlanArriveTime = airPlanArriveTime;
    }


    public String getAirArrive() {
        return this.airArrive;
    }

    public void setAirArrive(String airArrive) {
        this.airArrive = airArrive;
    }

    public String getAirOnTime() {
        return this.airOnTime;
    }

    public void setAirOnTime(String airOnTime) {
        this.airOnTime = airOnTime;
    }

    public String getAirStatus() {
        return this.airStatus;
    }

    public void setAirStatus(String airStatus) {
        this.airStatus = airStatus;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
