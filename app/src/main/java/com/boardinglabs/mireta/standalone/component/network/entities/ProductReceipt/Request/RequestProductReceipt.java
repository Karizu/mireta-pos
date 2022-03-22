
package com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt.Request;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RequestProductReceipt implements Serializable
{

    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    @SerializedName("business_id")
    @Expose
    private String businessId;
    @SerializedName("product_receipt_date")
    @Expose
    private String productReceiptDate;
    @SerializedName("details")
    @Expose
    private List<Detail> details;
    private final static long serialVersionUID = 8641057201337240409L;

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getProductReceiptDate() {
        return productReceiptDate;
    }

    public void setProductReceiptDate(String productReceiptDate) {
        this.productReceiptDate = productReceiptDate;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

}
