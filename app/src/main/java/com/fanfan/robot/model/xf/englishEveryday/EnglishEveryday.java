package com.fanfan.robot.model.xf.englishEveryday;

import java.util.Date;

/**
 * Created by zhangyuanyuan on 2017/12/6.
 */

public class EnglishEveryday {

    private String _id;
    private String _sourceName_;
    private String caption;
    private String content;
    private Date date;
    private String imgUrl;
    private String note;
    private String origImgUrl;
    private String sourceName;
    private String translation;
    private String url;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_sourceName_() {
        return _sourceName_;
    }

    public void set_sourceName_(String _sourceName_) {
        this._sourceName_ = _sourceName_;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrigImgUrl() {
        return origImgUrl;
    }

    public void setOrigImgUrl(String origImgUrl) {
        this.origImgUrl = origImgUrl;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
