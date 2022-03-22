package com.boardinglabs.mireta.standalone.component.network.entities.Report;

import java.util.List;

public class NewReportModels {
    private int category_id;
    private String category_name;
    private List<ChildReportModels> childReportModels;

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public List<ChildReportModels> getChildReportModels() {
        return childReportModels;
    }

    public void setChildReportModels(List<ChildReportModels> childReportModels) {
        this.childReportModels = childReportModels;
    }
}
