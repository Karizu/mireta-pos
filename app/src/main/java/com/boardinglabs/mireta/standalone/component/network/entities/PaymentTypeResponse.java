
package com.boardinglabs.mireta.standalone.component.network.entities;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentTypeResponse implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("updated_at")
    @Expose
    private Object updatedAt;
    @SerializedName("updated_by")
    @Expose
    private Object updatedBy;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("specific_payment_method_id")
    @Expose
    private Integer specificPaymentMethodId;
    @SerializedName("specific_item_transaction_type_id")
    @Expose
    private Integer specificItemTransactionTypeId;
    private final static long serialVersionUID = 3572024302676073470L;

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getSpecificPaymentMethodId() {
        return specificPaymentMethodId;
    }

    public void setSpecificPaymentMethodId(Integer specificPaymentMethodId) {
        this.specificPaymentMethodId = specificPaymentMethodId;
    }

    public Integer getSpecificItemTransactionTypeId() {
        return specificItemTransactionTypeId;
    }

    public void setSpecificItemTransactionTypeId(Integer specificItemTransactionTypeId) {
        this.specificItemTransactionTypeId = specificItemTransactionTypeId;
    }
}
