
package com.boardinglabs.mireta.standalone.component.network.entities;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpenStore implements Serializable
{

    @SerializedName("location_id")
    @Expose
    private String locationId;
    @SerializedName("initial_cash")
    @Expose
    private String initialCash;
    @SerializedName("opened_at")
    @Expose
    private String openedAt;
    @SerializedName("opened_by")
    @Expose
    private String openedBy;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("id")
    @Expose
    private Integer id;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getInitialCash() {
        return initialCash;
    }

    public void setInitialCash(String initialCash) {
        this.initialCash = initialCash;
    }

    public String getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(String openedAt) {
        this.openedAt = openedAt;
    }

    public String getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(String openedBy) {
        this.openedBy = openedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
