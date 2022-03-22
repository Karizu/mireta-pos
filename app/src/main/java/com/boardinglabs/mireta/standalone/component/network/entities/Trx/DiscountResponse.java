
package com.boardinglabs.mireta.standalone.component.network.entities.Trx;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DiscountResponse implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("discount_description")
    @Expose
    private String discountDescription;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("transaction_id")
    @Expose
    private Integer transactionId;
    @SerializedName("transaction_detail_id")
    @Expose
    private Integer transactionDetailId;
    @SerializedName("discount_percentage")
    @Expose
    private String discountPercentage;
    @SerializedName("discount_amount")
    @Expose
    private String discountAmount;
    @SerializedName("total_discount")
    @Expose
    private String totalDiscount;
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
    private final static long serialVersionUID = -7152085840757451229L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public void setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getTransactionDetailId() {
        return transactionDetailId;
    }

    public void setTransactionDetailId(Integer transactionDetailId) {
        this.transactionDetailId = transactionDetailId;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
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

}
