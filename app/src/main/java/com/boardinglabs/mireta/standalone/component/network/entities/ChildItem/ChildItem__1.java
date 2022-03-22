
package com.boardinglabs.mireta.standalone.component.network.entities.ChildItem;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChildItem__1 implements Serializable
{

    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("item")
    @Expose
    private Item__1 item;
    private final static long serialVersionUID = 5410600340735981599L;

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

    public Item__1 getItem() {
        return item;
    }

    public void setItem(Item__1 item) {
        this.item = item;
    }

}
