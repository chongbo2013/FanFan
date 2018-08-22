package com.fanfan.robot.view.recyclerview.tree.factory;

import android.support.annotation.NonNull;

import com.fanfan.robot.view.recyclerview.tree.base.BaseItemData;
import com.fanfan.robot.view.recyclerview.tree.item.TreeItem;
import com.fanfan.robot.view.recyclerview.tree.item.TreeItemGroup;

import java.util.ArrayList;
import java.util.List;

public class ItemHelperFactory {

    public static List<TreeItem> createTreeItemList(List<? extends BaseItemData> list, TreeItemGroup treeParentItem) {
        if (null == list) {
            return null;
        }
        ArrayList<TreeItem> treeItemList = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            try {
                BaseItemData itemData = list.get(i);
                int viewItemType = itemData.getViewItemType();
                TreeItem treeItem;
                if (ItemConfig.getTreeViewHolderType(viewItemType) != null) {
                    Class<? extends TreeItem> treeItemClass = ItemConfig.getTreeViewHolderType(viewItemType);
                    treeItem = treeItemClass.newInstance();
                    treeItem.setData(itemData);
                    treeItem.setParentItem(treeParentItem);
                    treeItemList.add(treeItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return treeItemList;
    }


    @NonNull
    public static ArrayList<TreeItem> getChildItemsWithType(TreeItemGroup itemGroup) {
        ArrayList<TreeItem> baseItems = new ArrayList<>();
        List allChild = itemGroup.getChild();
        int childCount = itemGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TreeItem baseItem = (TreeItem) allChild.get(i);
            baseItems.add(baseItem);
            if (baseItem instanceof TreeItemGroup) {
                List list = ((TreeItemGroup) baseItem).getAllChilds();
                if (list != null && list.size() > 0) {
                    baseItems.addAll(list);
                }
            }
        }
        return baseItems;
    }

    @NonNull
    public static ArrayList<TreeItem> getChildItemsWithType(List<TreeItem> treeItems) {
        ArrayList<TreeItem> baseItems = new ArrayList<>();
        int childCount = treeItems.size();
        for (int i = 0; i < childCount; i++) {
            TreeItem treeItem = treeItems.get(i);
            baseItems.add(treeItem);
            if (treeItem instanceof TreeItemGroup) {
                ArrayList<TreeItem> childItems = getChildItemsWithType((TreeItemGroup) treeItem);
                if (!childItems.isEmpty()) {
                    baseItems.addAll(childItems);
                }
            }
        }
        return baseItems;
    }
}
