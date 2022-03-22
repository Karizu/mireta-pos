
package com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ItemPost implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sales_qty")
    @Expose
    private Integer orderQty;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("buy_price")
    @Expose
    private String buyPrice;
    @SerializedName("sales_price")
    @Expose
    private String salesPrice;
    @SerializedName("stock")
    @Expose
    private Stock stock;
    @SerializedName("variants")
    @Expose
    private List<Variant> variants;

    public ItemPost(Integer id, Integer orderQty, String name, String price, String buyPrice, String salesPrice, Stock stock, List<Variant> variants) {
        this.id = id;
        this.orderQty = orderQty;
        this.name = name;
        this.price = price;
        this.buyPrice = buyPrice;
        this.salesPrice = salesPrice;
        this.stock = stock;
        this.variants = variants;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
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
}
