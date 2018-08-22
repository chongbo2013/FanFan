package com.fanfan.robot.model;

import com.fanfan.robot.app.common.ChatConst;
import com.fanfan.robot.view.recyclerview.tree.base.BaseItemData;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by android on 2017/12/20.
 */
@Entity
public class VideoBean extends BaseItemData {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "localType")
    @ChatConst.LocalType
    private int localType;
    @Property(nameInDb = "saveTime")
    private long saveTime;
    @Property(nameInDb = "showTitle")
    private String showTitle;
    @Property(nameInDb = "videoName")
    private String videoName;
    @Property(nameInDb = "size")
    private long size;
    @Property(nameInDb = "videoUrl")
    private String videoUrl;
    @Property(nameInDb = "videoImage")
    private String videoImage;
    @Generated(hash = 2079342944)
    public VideoBean(Long id, int localType, long saveTime, String showTitle,
            String videoName, long size, String videoUrl, String videoImage) {
        this.id = id;
        this.localType = localType;
        this.saveTime = saveTime;
        this.showTitle = showTitle;
        this.videoName = videoName;
        this.size = size;
        this.videoUrl = videoUrl;
        this.videoImage = videoImage;
    }
    @Generated(hash = 2024490299)
    public VideoBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getLocalType() {
        return this.localType;
    }
    public void setLocalType(int localType) {
        this.localType = localType;
    }
    public long getSaveTime() {
        return this.saveTime;
    }
    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }
    public String getShowTitle() {
        return this.showTitle;
    }
    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }
    public String getVideoName() {
        return this.videoName;
    }
    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
    public long getSize() {
        return this.size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public String getVideoUrl() {
        return this.videoUrl;
    }
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    public String getVideoImage() {
        return this.videoImage;
    }
    public void setVideoImage(String videoImage) {
        this.videoImage = videoImage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else {
            if (obj instanceof VideoBean) {
                VideoBean videoBean = (VideoBean) obj;
                if (videoBean.id == (this.id)) {
                    return true;
                }
            }
        }
        return false;
    }

}
