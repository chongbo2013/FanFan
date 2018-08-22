package com.fanfan.novel.utils.customtabs;

import android.content.Context;
import android.util.Log;

import com.seabreeze.log.Print;

/**
 * Created by android on 2018/2/23.
 */

public class IntentUtil {

    /**
     * 打开链接
     * 根据设置判断是用那种方式打开
     *
     * @param context 上下文
     * @param url     url
     */
    public static void openUrl(Context context, String url) {
        if (null == url || url.isEmpty()) {
            Print.e("Url地址错误");
            return;
        }
        CustomTabsHelper.openUrl(context, url);
    }

}
