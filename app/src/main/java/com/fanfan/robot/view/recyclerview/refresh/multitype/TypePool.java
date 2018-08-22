package com.fanfan.robot.view.recyclerview.refresh.multitype;

import android.support.annotation.NonNull;

import com.fanfan.robot.view.recyclerview.refresh.base.BaseViewProvider;

import java.util.List;

/**
 * Created by android on 2017/12/19.
 */

public interface TypePool {

    void register(@NonNull Class<?> clazz, @NonNull BaseViewProvider provider);

    int indexOf(@NonNull final Class<?> clazz);

    List<BaseViewProvider> getProviders();

    BaseViewProvider getProviderByIndex(int index);

    <T extends BaseViewProvider> T getProviderByClass(@NonNull final Class<?> clazz);
}
