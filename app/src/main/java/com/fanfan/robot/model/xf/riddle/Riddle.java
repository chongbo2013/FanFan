package com.fanfan.robot.model.xf.riddle;

/**
 * Created by zhangyuanyuan on 2017/12/6.
 */

public class Riddle {

    private String _id;
    private String _sourceName_;
    private String answer;
    private String category;
    private String priority;
    private String sourceName;
    private String tips;
    private String title;

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
