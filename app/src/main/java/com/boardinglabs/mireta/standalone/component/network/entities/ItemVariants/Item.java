
package com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Item implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("buy_price")
    @Expose
    private String buyPrice;
    @SerializedName("is_daily_stock")
    @Expose
    private Boolean isDailyStock;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("stock")
    @Expose
    private Stock__1 stock;
    @SerializedName("selected_qty")
    @Expose
    private Integer selected_qty;
    private final static long serialVersionUID = -2931571698391380943L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getIsDailyStock() {
        return isDailyStock;
    }

    public void setIsDailyStock(Boolean isDailyStock) {
        this.isDailyStock = isDailyStock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Stock__1 getStock() {
        return stock;
    }

    public void setStock(Stock__1 stock) {
        this.stock = stock;
    }

    public Integer getSelected_qty() {
        return selected_qty;
    }

    public void setSelected_qty(Integer selected_qty) {
        this.selected_qty = selected_qty;
    }
}
