package com.fanfan.robot.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by android on 2018/1/11.
 */

@Entity
public class Dance {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "title")
    private String title;
    @Property(nameInDb = "time")
    private long time;
    @Property(nameInDb = "path")
    private String path;
    @Property(nameInDb = "coverPath")
    private String coverPath;
    @Property(nameInDb = "duration")
    private long duration;
    @Property(nameInDb = "order")
    private String order;
    @Property(nameInDb = "orderData")
    private String orderData;

    @Generated(hash = 1793381459)
    public Dance(Long id, String title, long time, String path, String coverPath,
                 long duration, String order, String orderData) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.path = path;
        this.coverPath = coverPath;
        this.duration = duration;
        this.order = order;
        this.orderData = orderData;
    }

    @Generated(hash = 597070551)
    public Dance() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCoverPath() {
        return this.coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderData() {
        return this.orderData;
    }

    public void setOrderData(String orderData) {
        this.orderData = orderData;
    }

}
