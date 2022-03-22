
package com.boardinglabs.mireta.standalone.component.network.entities.InquiryStatus;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClosedByUser implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("is_block")
    @Expose
    private Boolean isBlock;
    @SerializedName("is_pristine")
    @Expose
    private Boolean isPristine;
    @SerializedName("change_password")
    @Expose
    private Boolean changePassword;
    @SerializedName("last_login")
    @Expose
    private Object lastLogin;
    @SerializedName("remember_token")
    @Expose
    private Object rememberToken;
    @SerializedName("created_by")
    @Expose
    private Object createdBy;
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
    @SerializedName("ref_ardi")
    @Expose
    private Object refArdi;
    @SerializedName("version")
    @Expose
    private Object version;
    @SerializedName("role_id")
    @Expose
    private Integer roleId;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("location_id")
    @Expose
    private Object locationId;
    private final static long serialVersionUID = -720351742861845064L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsBlock() {
        return isBlock;
    }

    public void setIsBlock(Boolean isBlock) {
        this.isBlock = isBlock;
    }

    public Boolean getIsPristine() {
        return isPristine;
    }

    public void setIsPristine(Boolean isPristine) {
        this.isPristine = isPristine;
    }

    public Boolean getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(Boolean changePassword) {
        this.changePassword = changePassword;
    }

    public Object getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Object lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Object getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(Object rememberToken) {
        this.rememberToken = rememberToken;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
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

    public Object getRefArdi() {
        return refArdi;
    }

    public void setRefArdi(Object refArdi) {
        this.refArdi = refArdi;
    }

    public Object getVersion() {
        return version;
    }

    public void setVersion(Object version) {
        this.version = version;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Object getLocationId() {
        return locationId;
    }

    public void setLocationId(Object locationId) {
        this.locationId = locationId;
    }

}
