
package com.boardinglabs.mireta.standalone.component.network.entities.InquiryStatus;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockReport implements Serializable
{

    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("stock_id")
    @Expose
    private Integer stockId;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("qty_initial")
    @Expose
    private Integer qtyInitial;
    @SerializedName("sum_qty_in")
    @Expose
    private String sumQtyIn;
    @SerializedName("sum_qty_out")
    @Expose
    private String sumQtyOut;
    @SerializedName("qty_end")
    @Expose
    private Integer qtyEnd;
    @SerializedName("total_damaged")
    @Expose
    private Integer totalDamaged;
    @SerializedName("total_adjusted")
    @Expose
    private String totalAdjusted;
    @SerializedName("total_sold")
    @Expose
    private String totalSold;
    private final static long serialVersionUID = 2039167234165883607L;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getQtyInitial() {
        return qtyInitial;
    }

    public void setQtyInitial(Integer qtyInitial) {
        this.qtyInitial = qtyInitial;
    }

    public String getSumQtyIn() {
        return sumQtyIn;
    }

    public void setSumQtyIn(String sumQtyIn) {
        this.sumQtyIn = sumQtyIn;
    }

    public String getSumQtyOut() {
        return sumQtyOut;
    }

    public void setSumQtyOut(String sumQtyOut) {
        this.sumQtyOut = sumQtyOut;
    }

    public Integer getQtyEnd() {
        return qtyEnd;
    }

    public void setQtyEnd(Integer qtyEnd) {
        this.qtyEnd = qtyEnd;
    }

    public Integer getTotalDamaged() {
        return totalDamaged;
    }

    public void setTotalDamaged(Integer totalDamaged) {
        this.totalDamaged = totalDamaged;
    }

    public String getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(String totalSold) {
        this.totalSold = totalSold;
    }

    public String getTotalAdjusted() {
        return totalAdjusted;
    }

    public void setTotalAdjusted(String totalAdjusted) {
        this.totalAdjusted = totalAdjusted;
    }
}
