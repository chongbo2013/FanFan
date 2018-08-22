package com.fanfan.robot.other.cache;

import android.content.Context;

import com.fanfan.robot.model.UserInfo;
import com.fanfan.novel.utils.ACache;

/**
 * Created by android on 2017/12/25.
 */

public class UserInfoCache {

    public static final String IDENTIFIER = "identifier";
    public static final String USERSIG = "userSig";
    public static final String NICKNAME = "nickName";
    public static final String USERPASS = "userPass";

    public static void saveCache(Context context) {
        UserInfo info = UserInfo.getInstance();
        if (info.getIdentifier() != null) {
            ACache.get(context).put(IDENTIFIER, info.getIdentifier());
        }
        if (info.getUserSig() != null) {
            ACache.get(context).put(USERSIG, info.getUserSig());
        }
        if (info.getNikeName() != null) {
            ACache.get(context).put(NICKNAME, info.getNikeName());
        }
        if (info.getUserPass() != null) {
            ACache.get(context).put(USERPASS, info.getUserPass());
        }
    }


    public static String getIdentifier(Context context) {
        return ACache.get(context).getAsString(IDENTIFIER);
    }

    public static String getUserSig(Context context) {
        return ACache.get(context).getAsString(USERSIG);
    }

    public static void getUser(Context context) {
        UserInfo.getInstance().setIdentifier(getIdentifier(context));
        UserInfo.getInstance().setUserSig(getUserSig(context));
        UserInfo.getInstance().setNikeName(ACache.get(context).getAsString(NICKNAME));
        UserInfo.getInstance().setUserPass(ACache.get(context).getAsString(USERPASS));
    }

    public static void clearCache(Context context) {
        ACache.get(context).remove(IDENTIFIER);
        ACache.get(context).remove(USERSIG);
        ACache.get(context).remove(NICKNAME);
        ACache.get(context).remove(USERPASS);
    }

}
