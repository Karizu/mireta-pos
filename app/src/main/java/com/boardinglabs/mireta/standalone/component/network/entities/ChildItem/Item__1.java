
package com.boardinglabs.mireta.standalone.component.network.entities.ChildItem;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item__1 implements Serializable
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
    private final static long serialVersionUID = 1301466222714157881L;

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

}
