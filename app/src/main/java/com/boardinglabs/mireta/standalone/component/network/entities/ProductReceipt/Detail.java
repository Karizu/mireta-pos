
package com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Detail implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
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
    @SerializedName("product_receipt_id")
    @Expose
    private Integer productReceiptId;
    @SerializedName("is_child_based_stock")
    @Expose
    private Integer isChildBasedStock;
    @SerializedName("location")
    @Expose
    private Location__1 location;
    @SerializedName("item")
    @Expose
    private Item item;
    private final static long serialVersionUID = -8516115824133474781L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
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

    public Integer getProductReceiptId() {
        return productReceiptId;
    }

    public void setProductReceiptId(Integer productReceiptId) {
        this.productReceiptId = productReceiptId;
    }

    public Integer getIsChildBasedStock() {
        return isChildBasedStock;
    }

    public void setIsChildBasedStock(Integer isChildBasedStock) {
        this.isChildBasedStock = isChildBasedStock;
    }

    public Location__1 getLocation() {
        return location;
    }

    public void setLocation(Location__1 location) {
        this.location = location;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
