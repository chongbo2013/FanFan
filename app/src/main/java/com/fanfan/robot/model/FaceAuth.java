package com.fanfan.robot.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by android on 2018/1/9.
 */

@Entity
public class FaceAuth {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "saveTime")
    private long saveTime;
    @Property(nameInDb = "personId")
    private String personId;
    @Property(nameInDb = "authId")
    private String authId;
    @Property(nameInDb = "faceCount")
    private int faceCount;
    @Property(nameInDb = "job")
    private String job;
    @Property(nameInDb = "synopsis")
    private String synopsis;


    @Generated(hash = 66025072)
    public FaceAuth(Long id, long saveTime, String personId, String authId,
            int faceCount, String job, String synopsis) {
        this.id = id;
        this.saveTime = saveTime;
        this.personId = personId;
        this.authId = authId;
        this.faceCount = faceCount;
        this.job = job;
        this.synopsis = synopsis;
    }


    @Generated(hash = 1722875808)
    public FaceAuth() {
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else {
            if (obj instanceof FaceAuth) {
                FaceAuth faceAuth = (FaceAuth) obj;
                if (faceAuth.personId.equals(this.personId)) {
                    return true;
                }
            }
        }
        return false;
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


    public String getPersonId() {
        return this.personId;
    }


    public void setPersonId(String personId) {
        this.personId = personId;
    }


    public String getAuthId() {
        return this.authId;
    }


    public void setAuthId(String authId) {
        this.authId = authId;
    }


    public int getFaceCount() {
        return this.faceCount;
    }


    public void setFaceCount(int faceCount) {
        this.faceCount = faceCount;
    }


    public String getJob() {
        return this.job;
    }


    public void setJob(String job) {
        this.job = job;
    }


    public String getSynopsis() {
        return this.synopsis;
    }


    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
}
