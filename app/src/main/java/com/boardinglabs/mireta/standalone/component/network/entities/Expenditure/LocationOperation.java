
package com.boardinglabs.mireta.standalone.component.network.entities.Expenditure;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationOperation implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    @SerializedName("initial_cash")
    @Expose
    private String initialCash;
    @SerializedName("opened_by")
    @Expose
    private String openedBy;
    @SerializedName("opened_at")
    @Expose
    private String openedAt;
    @SerializedName("closed_by")
    @Expose
    private Object closedBy;
    @SerializedName("closed_at")
    @Expose
    private Object closedAt;
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
    @SerializedName("closing_cash")
    @Expose
    private String closingCash;
    @SerializedName("total_transaction_cash")
    @Expose
    private String totalTransactionCash;
    @SerializedName("total_transaction")
    @Expose
    private String totalTransaction;
    @SerializedName("total_expense")
    @Expose
    private String totalExpense;
    private final static long serialVersionUID = -5996826839464432349L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getInitialCash() {
        return initialCash;
    }

    public void setInitialCash(String initialCash) {
        this.initialCash = initialCash;
    }

    public String getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(String openedBy) {
        this.openedBy = openedBy;
    }

    public String getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(String openedAt) {
        this.openedAt = openedAt;
    }

    public Object getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(Object closedBy) {
        this.closedBy = closedBy;
    }

    public Object getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Object closedAt) {
        this.closedAt = closedAt;
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

    public String getClosingCash() {
        return closingCash;
    }

    public void setClosingCash(String closingCash) {
        this.closingCash = closingCash;
    }

    public String getTotalTransactionCash() {
        return totalTransactionCash;
    }

    public void setTotalTransactionCash(String totalTransactionCash) {
        this.totalTransactionCash = totalTransactionCash;
    }

    public String getTotalTransaction() {
        return totalTransaction;
    }

    public void setTotalTransaction(String totalTransaction) {
        this.totalTransaction = totalTransaction;
    }

    public String getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(String totalExpense) {
        this.totalExpense = totalExpense;
    }

}
