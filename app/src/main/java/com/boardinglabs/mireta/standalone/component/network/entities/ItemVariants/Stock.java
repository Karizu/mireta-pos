
package com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stock implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_child_based_stock")
    @Expose
    private Integer isChildBasedStock;
    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("description")
    @Expose
    private String description;
    private final static long serialVersionUID = 4956580668389881390L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIsChildBasedStock() {
        return isChildBasedStock;
    }

    public void setIsChildBasedStock(Integer isChildBasedStock) {
        this.isChildBasedStock = isChildBasedStock;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
