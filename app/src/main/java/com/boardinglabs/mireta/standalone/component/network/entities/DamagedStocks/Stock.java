
package com.boardinglabs.mireta.standalone.component.network.entities.DamagedStocks;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stock implements Serializable
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
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("created_by")
    @Expose
    private Object createdBy;
    @SerializedName("updated_by")
    @Expose
    private Object updatedBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("is_child_based_stock")
    @Expose
    private Integer isChildBasedStock;
    private final static long serialVersionUID = -1045696643875445390L;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getIsChildBasedStock() {
        return isChildBasedStock;
    }

    public void setIsChildBasedStock(Integer isChildBasedStock) {
        this.isChildBasedStock = isChildBasedStock;
    }

}
