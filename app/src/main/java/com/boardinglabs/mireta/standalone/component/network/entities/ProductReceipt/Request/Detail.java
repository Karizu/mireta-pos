
package com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt.Request;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Detail implements Serializable
{

    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("qty")
    @Expose
    private String qty;
    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    private final static long serialVersionUID = -5645572829930826434L;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

}
