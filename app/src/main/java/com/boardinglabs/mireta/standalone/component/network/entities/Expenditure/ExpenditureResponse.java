
package com.boardinglabs.mireta.standalone.component.network.entities.Expenditure;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpenditureResponse implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("nominal")
    @Expose
    private String nominal;
    @SerializedName("image")
    @Expose
    private String image;
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
    @SerializedName("location_operation_id")
    @Expose
    private Integer locationOperationId;
    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("location_operation")
    @Expose
    private LocationOperation locationOperation;
    private final static long serialVersionUID = -7067616800044635814L;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public Integer getLocationOperationId() {
        return locationOperationId;
    }

    public void setLocationOperationId(Integer locationOperationId) {
        this.locationOperationId = locationOperationId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocationOperation getLocationOperation() {
        return locationOperation;
    }

    public void setLocationOperation(LocationOperation locationOperation) {
        this.locationOperation = locationOperation;
    }

}
