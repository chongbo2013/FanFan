package com.fanfan.robot.view.recyclerview.tree.factory;

import android.util.SparseArray;

import com.fanfan.robot.view.recyclerview.tree.item.TreeItem;

/**
 * Created by android on 2017/12/20.
 */

public class ItemConfig {

    private static SparseArray<Class<? extends TreeItem>> treeViewHolderTypes;

    static {
        treeViewHolderTypes = new SparseArray<>();
    }

    public static Class<? extends TreeItem> getTreeViewHolderType(int type) {
        return treeViewHolderTypes.get(type);
    }

    public static void addTreeHolderType(int type, Class<? extends TreeItem> clazz) {
        if (null == clazz) {
            return;
        }
        treeViewHolderTypes.put(type, clazz);
    }

}
