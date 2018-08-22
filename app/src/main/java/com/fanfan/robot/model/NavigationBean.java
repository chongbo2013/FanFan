package com.fanfan.robot.model;

import com.fanfan.robot.view.recyclerview.tree.base.BaseItemData;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by android on 2017/12/20.
 */
@Entity
public class NavigationBean extends BaseItemData {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "saveTime")
    private long saveTime;
    @Property(nameInDb = "title")
    private String title;
    @Property(nameInDb = "guide")
    private String guide;
    @Property(nameInDb = "datail")
    private String datail;
    @Property(nameInDb = "posX")
    private int posX;
    @Property(nameInDb = "posY")
    private int posY;
    @Property(nameInDb = "imgUrl")
    private String imgUrl;
    @Property(nameInDb = "navigation")
    private String navigation;
    @Property(nameInDb = "navigationData")
    private String navigationData;
    @Generated(hash = 1829186153)
    public NavigationBean(Long id, long saveTime, String title, String guide,
            String datail, int posX, int posY, String imgUrl, String navigation,
            String navigationData) {
        this.id = id;
        this.saveTime = saveTime;
        this.title = title;
        this.guide = guide;
        this.datail = datail;
        this.posX = posX;
        this.posY = posY;
        this.imgUrl = imgUrl;
        this.navigation = navigation;
        this.navigationData = navigationData;
    }
    @Generated(hash = 270470942)
    public NavigationBean() {
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
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDatail() {
        return this.datail;
    }
    public void setDatail(String datail) {
        this.datail = datail;
    }
    public int getPosX() {
        return this.posX;
    }
    public void setPosX(int posX) {
        this.posX = posX;
    }
    public int getPosY() {
        return this.posY;
    }
    public void setPosY(int posY) {
        this.posY = posY;
    }
    public String getImgUrl() {
        return this.imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public String getNavigation() {
        return this.navigation;
    }
    public void setNavigation(String navigation) {
        this.navigation = navigation;
    }
    public String getNavigationData() {
        return this.navigationData;
    }
    public void setNavigationData(String navigationData) {
        this.navigationData = navigationData;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NavigationBean) {
            NavigationBean navigationBean= (NavigationBean) obj;
            return navigationBean.title.equals(getTitle());
        }
        return false;
    }

}
