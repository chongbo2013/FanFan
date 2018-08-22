package com.fanfan.robot.model;

public class SpeakBean {

    private String problem;
    private String anwer;
    private long time;
    private boolean action;
    private boolean isUrl;

    public SpeakBean(String problem, String anwer, long time, boolean action, boolean isUrl) {
        this.problem = problem;
        this.anwer = anwer;
        this.time = time;
        this.action = action;
        this.isUrl = isUrl;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getAnwer() {
        return anwer;
    }

    public void setAnwer(String anwer) {
        this.anwer = anwer;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

    public boolean isUrl() {
        return isUrl;
    }

    public void setUrl(boolean url) {
        isUrl = url;
    }
}
