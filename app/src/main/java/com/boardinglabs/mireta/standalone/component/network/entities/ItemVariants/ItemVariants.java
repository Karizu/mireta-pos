
package com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemVariants implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sales_qty")
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
    @SerializedName("buy_price")
    @Expose
    private String buyPrice;
    @SerializedName("sales_price")
    @Expose
    private String salesPrice;
    @SerializedName("discount_price")
    @Expose
    private String discountPrice;
    @SerializedName("is_grouped_item")
    @Expose
    private Integer isGroupedItem;
    @SerializedName("stock")
    @Expose
    private Stock stock;
    @SerializedName("category")
    @Expose
    private Category category;
    @SerializedName("variants")
    @Expose
    private List<Variant> variants;
    @SerializedName("is_visible")
    @Expose
    private boolean isVisible = true;
    private final static long serialVersionUID = 442207755002129727L;

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

    public String getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(String buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Integer getIsGroupedItem() {
        return isGroupedItem;
    }

    public void setIsGroupedItem(Integer isGroupedItem) {
        this.isGroupedItem = isGroupedItem;
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

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    public String getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(String salesPrice) {
        this.salesPrice = salesPrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
