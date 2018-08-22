package com.fanfan.robot.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by android on 2018/2/23.
 */
@Entity
public class SiteBean {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "saveTime")
    private long saveTime;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "url")
    private String url;
    @Property(nameInDb = "avatar_url")
    private String avatar_url;
    @Generated(hash = 959577729)
    public SiteBean(Long id, long saveTime, String name, String url,
            String avatar_url) {
        this.id = id;
        this.saveTime = saveTime;
        this.name = name;
        this.url = url;
        this.avatar_url = avatar_url;
    }
    @Generated(hash = 1223506992)
    public SiteBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getSaveTime() {
        return this.saveTime;
    }
    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getAvatar_url() {
        return this.avatar_url;
    }
    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

}
