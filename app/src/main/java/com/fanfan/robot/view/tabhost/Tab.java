package com.fanfan.robot.view.tabhost;

/**
 * Created by Administrator on 2017-05-04.
 */

public class Tab {

    private int title;
    private int icon;
    private int background;
    private Class fragment;

    public Tab(Class fragment, int title, int icon, int background) {
        this.title = title;
        this.icon = icon;
        this.background = background;
        this.fragment = fragment;
    }
    public Tab(Class fragment, int title, int icon) {
        this.title = title;
        this.icon = icon;
        this.fragment = fragment;
    }
    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Class getFragment() {
        return fragment;
    }

    public void setFragment(Class fragment) {
        this.fragment = fragment;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }
}
