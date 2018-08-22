package com.fanfan.robot.adapter.recycler.chat;

import com.fanfan.robot.app.common.base.BaseRecyclerAdapter;
import com.fanfan.robot.view.recyclerview.manager.ItemManageImpl;
import com.fanfan.robot.view.recyclerview.manager.ItemManager;
import com.fanfan.robot.view.recyclerview.tree.ViewHolder;
import com.fanfan.robot.view.recyclerview.tree.factory.ItemHelperFactory;
import com.fanfan.robot.view.recyclerview.tree.item.TreeItem;
import com.fanfan.robot.view.recyclerview.tree.item.TreeItemGroup;

import java.util.ArrayList;
import java.util.List;

public class TreeRecyclerAdapter extends BaseRecyclerAdapter<TreeItem> {

    private ItemManager<TreeItem> mItemManager;

    @Override
    public void setDatas(List<TreeItem> items) {
        if (null == items) {
            return;
        }
        getDatas().clear();
        assembleItems(items);
    }

    private void assembleItems(List<TreeItem> items) {
        List<TreeItem> datas = getDatas();
        datas.addAll(ItemHelperFactory.getChildItemsWithType(items));
    }

    public ItemManager<TreeItem> getItemManager() {
        if (mItemManager == null) {
            mItemManager = new ItemManageImpl<>(this);
        }
        return mItemManager;
    }

    public void setItemManager(ItemManager<TreeItem> itemManage) {
        this.mItemManager = itemManage;
    }

    @Override
    public int getLayoutId(int position) {
        return getDatas().get(position).getLayoutId();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TreeItem t = getDatas().get(position);
        checkItemManage(t);
        t.onBindViewHolder(holder);
    }

    private void checkItemManage(TreeItem item) {
        if (item.getItemManager() == null) {
            item.setItemManager(getItemManager());
        }
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, TreeItem item, int position) {

    }

    private class TreeItemManageImpl extends ItemManager<TreeItem> {


        public TreeItemManageImpl(BaseRecyclerAdapter<TreeItem> adapter) {
            super(adapter);
        }

        @Override
        public void addItem(TreeItem item) {
            if (null == item) {
                return;
            }
            if (item instanceof TreeItemGroup) {
                getDatas().add(item);
            } else {
                TreeItemGroup itemParentItem = item.getParentItem();
                if (itemParentItem != null) {
                    List childs = itemParentItem.getChild();
                    if (childs != null) {
                        int i = getDatas().indexOf(itemParentItem);
                        getDatas().add(i + itemParentItem.getChild().size(), item);
                    } else {
                        childs = new ArrayList();
                        itemParentItem.setChild(childs);
                    }
                    childs.add(item);
                }
            }
            notifyDataChanged();
        }

        @Override
        public void addItem(int position, TreeItem item) {
            getDatas().add(position, item);
            if (item != null && item.getParentItem() != null) {
                item.getParentItem().getChild().add(item);
            }
            notifyDataChanged();
        }

        @Override
        public void addItems(List<TreeItem> items) {
            getDatas().addAll(items);
            notifyDataChanged();
        }

        @Override
        public void addItems(int position, List<TreeItem> items) {
            getDatas().addAll(position, items);
            notifyDataChanged();
        }

        @Override
        public void removeItem(TreeItem item) {
            if (null == item) {
                return;
            }
            getDatas().remove(item);
            TreeItemGroup itemParentItem = item.getParentItem();
            if (itemParentItem != null) {
                List childs = itemParentItem.getChild();
                if (childs != null) {
                    childs.remove(item);
                }
            }
            notifyDataChanged();
        }

        @Override
        public void removeItem(int position) {
            TreeItem t = getDatas().get(position);
            TreeItemGroup parentItem = t.getParentItem();
            if (parentItem != null && parentItem.getChild() != null) {
                parentItem.getChild().remove(t);
            }
            getDatas().remove(position);
            notifyDataChanged();
        }

        @Override
        public void removeItems(List<TreeItem> items) {
            getDatas().removeAll(items);
            notifyDataChanged();
        }

        @Override
        public void replaceItem(int position, TreeItem item) {
            TreeItem t = getDatas().get(position);
            if (t instanceof TreeItemGroup) {
                getDatas().set(position, item);
            } else {
                TreeItemGroup parentItem = t.getParentItem();
                if (parentItem != null && parentItem.getChild() != null) {
                    List childs = parentItem.getChild();
                    int i = childs.indexOf(t);
                    childs.set(i, item);
                }
                getDatas().set(position, item);
            }
            notifyDataChanged();
        }

        @Override
        public void replaceAllItem(List<TreeItem> items) {
            if (items != null) {
                setDatas(items);
                notifyDataChanged();
            }
        }

        @Override
        public TreeItem getItem(int position) {
            return getDatas().get(position);
        }

        @Override
        public int getItemPosition(TreeItem item) {
            return getDatas().indexOf(item);
        }
    }
}
