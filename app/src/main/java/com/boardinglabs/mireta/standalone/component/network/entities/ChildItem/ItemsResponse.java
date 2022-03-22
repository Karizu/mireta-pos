
package com.boardinglabs.mireta.standalone.component.network.entities.ChildItem;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemsResponse implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_qty")
    @Expose
    private Integer orderQty;
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
    private String code;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("is_daily_stock")
    @Expose
    private Boolean isDailyStock;
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
    private Object updatedAt;
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
    @SerializedName("is_grouped_single_option")
    @Expose
    private Integer isGroupedSingleOption;
    @SerializedName("is_grouped_multiple_options")
    @Expose
    private Integer isGroupedMultipleOptions;
    @SerializedName("is_hidden_from_trx_menu_list")
    @Expose
    private Integer isHiddenFromTrxMenuList;
    @SerializedName("is_grouped_single_fixed_option")
    @Expose
    private Integer isGroupedSingleFixedOption;
    @SerializedName("child_items")
    @Expose
    private List<ChildItem> childItems = null;
    @SerializedName("stock")
    @Expose
    private Stock stock;
    @SerializedName("category")
    @Expose
    private Category category;
    @SerializedName("business")
    @Expose
    private Business business;
    @SerializedName("brand")
    @Expose
    private Brand brand;
    private final static long serialVersionUID = 8331461086379097697L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Integer orderQty) {
        this.orderQty = orderQty;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
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

    public Integer getIsGroupedSingleOption() {
        return isGroupedSingleOption;
    }

    public void setIsGroupedSingleOption(Integer isGroupedSingleOption) {
        this.isGroupedSingleOption = isGroupedSingleOption;
    }

    public Integer getIsGroupedMultipleOptions() {
        return isGroupedMultipleOptions;
    }

    public void setIsGroupedMultipleOptions(Integer isGroupedMultipleOptions) {
        this.isGroupedMultipleOptions = isGroupedMultipleOptions;
    }

    public Integer getIsHiddenFromTrxMenuList() {
        return isHiddenFromTrxMenuList;
    }

    public void setIsHiddenFromTrxMenuList(Integer isHiddenFromTrxMenuList) {
        this.isHiddenFromTrxMenuList = isHiddenFromTrxMenuList;
    }

    public Integer getIsGroupedSingleFixedOption() {
        return isGroupedSingleFixedOption;
    }

    public void setIsGroupedSingleFixedOption(Integer isGroupedSingleFixedOption) {
        this.isGroupedSingleFixedOption = isGroupedSingleFixedOption;
    }

    public List<ChildItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<ChildItem> childItems) {
        this.childItems = childItems;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

}
