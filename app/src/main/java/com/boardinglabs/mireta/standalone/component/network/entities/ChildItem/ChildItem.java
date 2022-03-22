
package com.boardinglabs.mireta.standalone.component.network.entities.ChildItem;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChildItem implements Serializable
{

    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("item")
    @Expose
    private Item item;
    @SerializedName("child_items")
    @Expose
    private List<ChildItem__1> childItems = null;
    private final static long serialVersionUID = -5217400585356344829L;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<ChildItem__1> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<ChildItem__1> childItems) {
        this.childItems = childItems;
    }

}
