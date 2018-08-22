package com.fanfan.robot.app.common.base;

import android.support.multidex.MultiDexApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/15/015.
 */

public class BaseApplication extends MultiDexApplication {

    public static List<BaseActivity> activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void addActivity(BaseActivity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(BaseActivity activity) {
        activityList.remove(activity);
    }

    public static BaseActivity getTopAcitivity() {
        if (activityList.isEmpty()) {
            return null;
        }
        return activityList.get(activityList.size() - 1);
    }

}
