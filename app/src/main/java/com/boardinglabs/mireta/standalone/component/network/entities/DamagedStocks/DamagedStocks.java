
package com.boardinglabs.mireta.standalone.component.network.entities.DamagedStocks;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DamagedStocks implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("stock_id")
    @Expose
    private Integer stockId;
    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("location_operation_id")
    @Expose
    private Integer locationOperationId;
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("image")
    @Expose
    private String image;
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
    @SerializedName("total_price")
    @Expose
    private String totalPrice;
    @SerializedName("stock")
    @Expose
    private Stock stock;
    @SerializedName("item")
    @Expose
    private Item item;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("location_operation")
    @Expose
    private LocationOperation locationOperation;
    private final static long serialVersionUID = 9022022704328499059L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
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

    public Integer getLocationOperationId() {
        return locationOperationId;
    }

    public void setLocationOperationId(Integer locationOperationId) {
        this.locationOperationId = locationOperationId;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocationOperation getLocationOperation() {
        return locationOperation;
    }

    public void setLocationOperation(LocationOperation locationOperation) {
        this.locationOperation = locationOperation;
    }

}
