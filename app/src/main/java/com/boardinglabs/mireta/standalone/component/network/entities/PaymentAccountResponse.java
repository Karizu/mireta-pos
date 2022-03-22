package com.boardinglabs.mireta.standalone.component.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentAccountResponse implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("account_name")
    @Expose
    private String accountName;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("account_bank_name")
    @Expose
    private String accountBankName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("updated_by")
    @Expose
    private Object updatedBy;
    @SerializedName("payment_method_id")
    @Expose
    private Integer paymentMethodId;
    @SerializedName("account_fee_percentage")
    @Expose
    private String accountFeePercentage;
    @SerializedName("payment_method")
    @Expose
    private PaymentMethodResponse paymentMethod;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountBankName() {
        return accountBankName;
    }

    public void setAccountBankName(String accountBankName) {
        this.accountBankName = accountBankName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public Integer getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Integer paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getAccountFeePercentage() {
        return accountFeePercentage;
    }

    public void setAccountFeePercentage(String accountFeePercentage) {
        this.accountFeePercentage = accountFeePercentage;
    }

    public PaymentMethodResponse getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodResponse paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
