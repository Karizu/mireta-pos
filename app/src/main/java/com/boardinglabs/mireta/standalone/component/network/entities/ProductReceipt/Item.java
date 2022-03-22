
package com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Item implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("business_id")
    @Expose
    private String businessId;
    @SerializedName("brand_id")
    @Expose
    private Integer brandId;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("code")
    @Expose
    private Object code;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private Object image;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("is_daily_stock")
    @Expose
    private Boolean isDailyStock;
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
    @SerializedName("partner_item_id")
    @Expose
    private Object partnerItemId;
    @SerializedName("partner_item_expired")
    @Expose
    private Object partnerItemExpired;
    @SerializedName("is_grouped_item")
    @Expose
    private Integer isGroupedItem;
    @SerializedName("buy_price")
    @Expose
    private String buyPrice;
    @SerializedName("group_id")
    @Expose
    private Object groupId;
    @SerializedName("discount_price")
    @Expose
    private String discountPrice;
    @SerializedName("item_transaction_type_id")
    @Expose
    private Integer itemTransactionTypeId;
    @SerializedName("is_up_price")
    @Expose
    private Integer isUpPrice;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    private final static long serialVersionUID = 4987483831855367418L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getIsDailyStock() {
        return isDailyStock;
    }

    public void setIsDailyStock(Boolean isDailyStock) {
        this.isDailyStock = isDailyStock;
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

    public Object getPartnerItemId() {
        return partnerItemId;
    }

    public void setPartnerItemId(Object partnerItemId) {
        this.partnerItemId = partnerItemId;
    }

    public Object getPartnerItemExpired() {
        return partnerItemExpired;
    }

    public void setPartnerItemExpired(Object partnerItemExpired) {
        this.partnerItemExpired = partnerItemExpired;
    }

    public Integer getIsGroupedItem() {
        return isGroupedItem;
    }

    public void setIsGroupedItem(Integer isGroupedItem) {
        this.isGroupedItem = isGroupedItem;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(String buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Object getGroupId() {
        return groupId;
    }

    public void setGroupId(Object groupId) {
        this.groupId = groupId;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Integer getItemTransactionTypeId() {
        return itemTransactionTypeId;
    }

    public void setItemTransactionTypeId(Integer itemTransactionTypeId) {
        this.itemTransactionTypeId = itemTransactionTypeId;
    }

    public Integer getIsUpPrice() {
        return isUpPrice;
    }

    public void setIsUpPrice(Integer isUpPrice) {
        this.isUpPrice = isUpPrice;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}
