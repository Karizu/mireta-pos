
package com.boardinglabs.mireta.standalone.component.network.entities.InquiryStatus;

import java.io.Serializable;
import java.util.List;

import com.boardinglabs.mireta.standalone.component.network.entities.Trx.Stock;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InquiryStatusOperations implements Serializable
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
    @SerializedName("total_price")
    @Expose
    private String totalPrice;
    @SerializedName("opened_by")
    @Expose
    private String openedBy;
    @SerializedName("opened_at")
    @Expose
    private String openedAt;
    @SerializedName("closed_by")
    @Expose
    private String closedBy;
    @SerializedName("closed_at")
    @Expose
    private String closedAt;
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
    @SerializedName("is_open")
    @Expose
    private Boolean isOpen;
    @SerializedName("current_cash")
    @Expose
    private Integer currentCash;
    @SerializedName("current_cash_trx_sum")
    @Expose
    private String currentCashTrxSum;
    @SerializedName("current_trx_sum")
    @Expose
    private String currentTrxSum;
    @SerializedName("current_trx_count")
    @Expose
    private Integer currentTrxCount;
    @SerializedName("today_trx_sum")
    @Expose
    private String todayTrxSum;
    @SerializedName("today_trx_count")
    @Expose
    private Integer todayTrxCount;
    @SerializedName("current_expense_sum")
    @Expose
    private String current_expense_sum;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("opened_by_user")
    @Expose
    private OpenedByUser openedByUser;
    @SerializedName("closed_by_user")
    @Expose
    private ClosedByUser closedByUser;
    @SerializedName("current_qris_trx_sum")
    @Expose
    private String currentQrisTrxSum;
    @SerializedName("current_qris_trx_count")
    @Expose
    private Integer currentQrisTrxCount;
    @SerializedName("current_bank_transfer_trx_sum")
    @Expose
    private Integer currentBankTransferTrxSum;
    @SerializedName("current_bank_transfer_trx_count")
    @Expose
    private Integer currentBankTransferTrxCount;
    @SerializedName("current_gopay_trx_sum")
    @Expose
    private Integer currentGopayTrxSum;
    @SerializedName("current_gopay_trx_count")
    @Expose
    private Integer currentGopayTrxCount;
    @SerializedName("current_ovo_trx_sum")
    @Expose
    private Integer currentOvoTrxSum;
    @SerializedName("current_ovo_trx_count")
    @Expose
    private Integer currentOvoTrxCount;
    @SerializedName("current_damaged_stocks_count")
    @Expose
    private Integer currentDamagedStocksCount;
    @SerializedName("current_damaged_stocks_amount")
    @Expose
    private String currentDamagedStocksAmount;
    @SerializedName("adjusted_closing_cash")
    @Expose
    private String adjustedClosingCash;
    @SerializedName("stock_report")
    @Expose
    private List<StockReport> stocks;

    private final static long serialVersionUID = 4457735694830825668L;

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

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
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

    public String getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
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

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Integer getCurrentCash() {
        return currentCash;
    }

    public void setCurrentCash(Integer currentCash) {
        this.currentCash = currentCash;
    }

    public String getCurrentCashTrxSum() {
        return currentCashTrxSum;
    }

    public void setCurrentCashTrxSum(String currentCashTrxSum) {
        this.currentCashTrxSum = currentCashTrxSum;
    }

    public String getCurrentTrxSum() {
        return currentTrxSum;
    }

    public void setCurrentTrxSum(String currentTrxSum) {
        this.currentTrxSum = currentTrxSum;
    }

    public Integer getCurrentTrxCount() {
        return currentTrxCount;
    }

    public void setCurrentTrxCount(Integer currentTrxCount) {
        this.currentTrxCount = currentTrxCount;
    }

    public String getTodayTrxSum() {
        return todayTrxSum;
    }

    public void setTodayTrxSum(String todayTrxSum) {
        this.todayTrxSum = todayTrxSum;
    }

    public Integer getTodayTrxCount() {
        return todayTrxCount;
    }

    public void setTodayTrxCount(Integer todayTrxCount) {
        this.todayTrxCount = todayTrxCount;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public OpenedByUser getOpenedByUser() {
        return openedByUser;
    }

    public void setOpenedByUser(OpenedByUser openedByUser) {
        this.openedByUser = openedByUser;
    }

    public ClosedByUser getClosedByUser() {
        return closedByUser;
    }

    public void setClosedByUser(ClosedByUser closedByUser) {
        this.closedByUser = closedByUser;
    }

    public String getCurrent_expense_sum() {
        return current_expense_sum;
    }

    public void setCurrent_expense_sum(String current_expense_sum) {
        this.current_expense_sum = current_expense_sum;
    }

    public String getCurrentQrisTrxSum() {
        return currentQrisTrxSum;
    }

    public void setCurrentQrisTrxSum(String currentQrisTrxSum) {
        this.currentQrisTrxSum = currentQrisTrxSum;
    }

    public Integer getCurrentQrisTrxCount() {
        return currentQrisTrxCount;
    }

    public void setCurrentQrisTrxCount(Integer currentQrisTrxCount) {
        this.currentQrisTrxCount = currentQrisTrxCount;
    }

    public Integer getCurrentBankTransferTrxSum() {
        return currentBankTransferTrxSum;
    }

    public void setCurrentBankTransferTrxSum(Integer currentBankTransferTrxSum) {
        this.currentBankTransferTrxSum = currentBankTransferTrxSum;
    }

    public Integer getCurrentBankTransferTrxCount() {
        return currentBankTransferTrxCount;
    }

    public void setCurrentBankTransferTrxCount(Integer currentBankTransferTrxCount) {
        this.currentBankTransferTrxCount = currentBankTransferTrxCount;
    }

    public Integer getCurrentGopayTrxSum() {
        return currentGopayTrxSum;
    }

    public void setCurrentGopayTrxSum(Integer currentGopayTrxSum) {
        this.currentGopayTrxSum = currentGopayTrxSum;
    }

    public Integer getCurrentGopayTrxCount() {
        return currentGopayTrxCount;
    }

    public void setCurrentGopayTrxCount(Integer currentGopayTrxCount) {
        this.currentGopayTrxCount = currentGopayTrxCount;
    }

    public Integer getCurrentOvoTrxSum() {
        return currentOvoTrxSum;
    }

    public void setCurrentOvoTrxSum(Integer currentOvoTrxSum) {
        this.currentOvoTrxSum = currentOvoTrxSum;
    }

    public Integer getCurrentOvoTrxCount() {
        return currentOvoTrxCount;
    }

    public void setCurrentOvoTrxCount(Integer currentOvoTrxCount) {
        this.currentOvoTrxCount = currentOvoTrxCount;
    }

    public Integer getCurrentDamagedStocksCount() {
        return currentDamagedStocksCount;
    }

    public void setCurrentDamagedStocksCount(Integer currentDamagedStocksCount) {
        this.currentDamagedStocksCount = currentDamagedStocksCount;
    }

    public String getCurrentDamagedStocksAmount() {
        return currentDamagedStocksAmount;
    }

    public void setCurrentDamagedStocksAmount(String currentDamagedStocksAmount) {
        this.currentDamagedStocksAmount = currentDamagedStocksAmount;
    }

    public List<StockReport> getStocks() {
        return stocks;
    }

    public void setStocks(List<StockReport> stocks) {
        this.stocks = stocks;
    }

    public String getAdjustedClosingCash() {
        return adjustedClosingCash;
    }

    public void setAdjustedClosingCash(String adjustedClosingCash) {
        this.adjustedClosingCash = adjustedClosingCash;
    }
}
