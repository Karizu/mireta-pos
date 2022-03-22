
package com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ProductReceiptResponse implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("product_receipt_date")
    @Expose
    private String productReceiptDate;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("business_id")
    @Expose
    private String businessId;
    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    @SerializedName("created_by_user")
    @Expose
    private CreatedByUser createdByUser;
    @SerializedName("last_updated_by_user")
    @Expose
    private String lastUpdatedByUser;
    @SerializedName("business")
    @Expose
    private Business business;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("details")
    @Expose
    private List<Detail> details = null;
    private final static long serialVersionUID = 6400965761955795234L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductReceiptDate() {
        return productReceiptDate;
    }

    public void setProductReceiptDate(String productReceiptDate) {
        this.productReceiptDate = productReceiptDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public CreatedByUser getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(CreatedByUser createdByUser) {
        this.createdByUser = createdByUser;
    }

    public String getLastUpdatedByUser() {
        return lastUpdatedByUser;
    }

    public void setLastUpdatedByUser(String lastUpdatedByUser) {
        this.lastUpdatedByUser = lastUpdatedByUser;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

}
