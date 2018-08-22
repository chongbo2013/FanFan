package com.fanfan.robot.model;

import android.support.annotation.NonNull;

import com.fanfan.robot.view.recyclerview.tree.base.BaseItemData;
import com.fanfan.youtu.api.face.bean.FaceIdentify;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by android on 2018/1/10.
 */

@Entity
public class CheckIn extends BaseItemData implements Comparable<CheckIn> {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "time")
    private long time;

    @Generated(hash = 1837196225)
    public CheckIn(Long id, String name, long time) {
        this.id = id;
        this.name = name;
        this.time = time;
    }

    @Generated(hash = 1821846413)
    public CheckIn() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int compareTo(@NonNull CheckIn in) {
        if (this.getTime() - in.getTime() >= 0) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "CheckIn{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time=" + time +
                '}';
    }
}
