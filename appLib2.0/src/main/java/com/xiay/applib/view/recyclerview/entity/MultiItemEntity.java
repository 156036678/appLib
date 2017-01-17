package com.xiay.applib.view.recyclerview.entity;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public abstract class MultiItemEntity {
    public int itemType;
    public MultiItemEntity(int itemType) {
        this.itemType = itemType;
    }
}
