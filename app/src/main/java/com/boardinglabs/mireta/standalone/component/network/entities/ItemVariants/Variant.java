
package com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Variant implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("parent_item_id")
    @Expose
    private Integer parentItemId;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("is_fixed_qty")
    @Expose
    private Integer isFixedQty;
    @SerializedName("is_sum_qty")
    @Expose
    private Integer isSumQty;
    @SerializedName("fixed_qty")
    @Expose
    private Integer fixedQty;
    @SerializedName("sum_qty")
    @Expose
    private Integer sumQty;
    @SerializedName("sum_qty_selected")
    @Expose
    private Integer sumQtySelected;
    @SerializedName("is_customisable")
    @Expose
    private Integer isCustomisable;
    @SerializedName("item")
    @Expose
    private Item item;
    @SerializedName("group_items")
    @Expose
    private List<GroupItem> groupItems = null;
    private final static long serialVersionUID = -4665506047867140890L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentItemId() {
        return parentItemId;
    }

    public void setParentItemId(Integer parentItemId) {
        this.parentItemId = parentItemId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsFixedQty() {
        return isFixedQty;
    }

    public void setIsFixedQty(Integer isFixedQty) {
        this.isFixedQty = isFixedQty;
    }

    public Integer getIsSumQty() {
        return isSumQty;
    }

    public void setIsSumQty(Integer isSumQty) {
        this.isSumQty = isSumQty;
    }

    public Integer getFixedQty() {
        return fixedQty;
    }

    public void setFixedQty(Integer fixedQty) {
        this.fixedQty = fixedQty;
    }

    public Integer getSumQty() {
        return sumQty;
    }

    public void setSumQty(Integer sumQty) {
        this.sumQty = sumQty;
    }

    public Integer getIsCustomisable() {
        return isCustomisable;
    }

    public void setIsCustomisable(Integer isCustomisable) {
        this.isCustomisable = isCustomisable;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<GroupItem> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(List<GroupItem> groupItems) {
        this.groupItems = groupItems;
    }

    public Integer getSumQtySelected() {
        return sumQtySelected;
    }

    public void setSumQtySelected(Integer sumQtySelected) {
        this.sumQtySelected = sumQtySelected;
    }
}
