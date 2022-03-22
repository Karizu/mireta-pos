
package com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stock__1 implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("qty")
    @Expose
    private Integer qty;
    private final static long serialVersionUID = 2724353843302227207L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

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

}
