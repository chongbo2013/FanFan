package com.fanfan.robot.view.recyclerview.tree.item;

import com.fanfan.robot.view.recyclerview.tree.factory.ItemHelperFactory;

import java.util.List;

public abstract class TreeItemGroup<D> extends TreeItem<D> {

    private List<TreeItem> child;

    public void setData(D data) {
        super.setData(data);
        child = initChildList(data);
    }

    public List<TreeItem> getAllChilds() {
        if (getChild() == null) {
            return null;
        }
        return ItemHelperFactory.getChildItemsWithType(this);
    }

    public List<TreeItem> getChild() {
        return child;
    }

    public void setChild(List<TreeItem> child) {
        this.child = child;
    }

    public int getChildCount() {
        return child == null ? 0 : child.size();
    }

    protected abstract List<TreeItem> initChildList(D data);

}
