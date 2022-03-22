package com.boardinglabs.mireta.standalone.modul.master.laporan.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.entities.Report.NewReportModels;
import com.boardinglabs.mireta.standalone.component.network.entities.Report.ReportModels;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private LinkedHashMap<String, ArrayList<ReportModels>> itemCategory;
    private List<NewReportModels> reportModels;
    private Context context;

    public ReportAdapter(LinkedHashMap<String, ArrayList<ReportModels>> itemCategory, Context context) {
        this.itemCategory = itemCategory;
        this.context = context;
    }

    public ReportAdapter(List<NewReportModels> reportModels, Context context) {
        this.reportModels = reportModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_report, parent, false);

        return new ReportAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        try {
            NewReportModels models = reportModels.get(position);
            holder.tvCategory.setText(models.getCategory_name());

            ChildReportAdapter adapter = new ChildReportAdapter(models.getChildReportModels(), context);
            holder.childItem.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reportModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        TextView tvName;
        TextView tvAmount;
        TextView tvQty;
        TextView tvTotal;
        View line;
        LinearLayout layout;
        RecyclerView childItem;

        ViewHolder(View v) {
            super(v);

            tvCategory = v.findViewById(R.id.tvTitleCategory);
            tvName = v.findViewById(R.id.tvItemName);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvQty = v.findViewById(R.id.tvQty);
            tvTotal = v.findViewById(R.id.tvTotal);
            line = v.findViewById(R.id.viewLine);
            layout = v.findViewById(R.id.layoutPenjualan);
            childItem = v.findViewById(R.id.childItem);
        }
    }
}
