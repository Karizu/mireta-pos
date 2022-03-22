
package com.boardinglabs.mireta.standalone.component.network.entities.Trx;

import java.io.Serializable;
import java.util.List;

import com.boardinglabs.mireta.standalone.component.network.entities.PaymentMethodResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transactions implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("location_id")
    @Expose
    private String locationId;
    @SerializedName("transaction_code")
    @Expose
    private String transactionCode;
    @SerializedName("payment_type")
    @Expose
    private Integer paymentType;
    @SerializedName("payment_method")
    @Expose
    private Integer paymentMethod;
    @SerializedName("payment_method_obj")
    @Expose
    private PaymentMethodResponse paymentMethodObj;
    @SerializedName("transaction_type_obj")
    @Expose
    private PaymentMethodResponse transactionTypeObj;
    @SerializedName("discounts")
    @Expose
    private List<DiscountResponse> discounts;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("total_price")
    @Expose
    private String totalPrice;
    @SerializedName("total_net_price")
    @Expose
    private String totalNetPrice;
    @SerializedName("total_discount")
    @Expose
    private String totalDiscount;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("updated_by")
    @Expose
    private String updatedBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("details")
    @Expose
    private List<Detail> details = null;
    private final static long serialVersionUID = -2235547445917282157L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethodResponse getPaymentMethodObj() {
        return paymentMethodObj;
    }

    public void setPaymentMethodObj(PaymentMethodResponse paymentMethodObj) {
        this.paymentMethodObj = paymentMethodObj;
    }

    public PaymentMethodResponse getTransactionTypeObj() {
        return transactionTypeObj;
    }

    public void setTransactionTypeObj(PaymentMethodResponse transactionTypeObj) {
        this.transactionTypeObj = transactionTypeObj;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(String totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
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

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
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

    public String getTotalNetPrice() {
        return totalNetPrice;
    }

    public void setTotalNetPrice(String totalNetPrice) {
        this.totalNetPrice = totalNetPrice;
    }

    public List<DiscountResponse> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<DiscountResponse> discounts) {
        this.discounts = discounts;
    }
}
