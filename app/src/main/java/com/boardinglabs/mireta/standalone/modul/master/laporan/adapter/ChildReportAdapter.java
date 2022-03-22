package com.boardinglabs.mireta.standalone.modul.master.laporan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.entities.Report.ChildReportModels;
import com.boardinglabs.mireta.standalone.component.network.entities.Report.ReportModels;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class ChildReportAdapter extends RecyclerView.Adapter<ChildReportAdapter.ViewHolder> {
    private List<ChildReportModels> reportModels;
    private Context context;

    public ChildReportAdapter(List<ChildReportModels> reportModels, Context context) {
        this.reportModels = reportModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ChildReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_child_report, parent, false);

        return new ChildReportAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ChildReportAdapter.ViewHolder holder, int position) {
        try {
            ChildReportModels models = reportModels.get(position);
            holder.tvName.setText(models.getItem_name());
            int mHarga = Integer.parseInt(models.getItem_price());
            holder.tvAmount.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mHarga));
            holder.tvQty.setText(models.getItem_qty() + " pcs");
            int mTotal = mHarga*models.getItem_qty();
            holder.tvTotal.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mTotal));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reportModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAmount;
        TextView tvQty;
        TextView tvTotal;

        ViewHolder(View v) {
            super(v);

            tvName = v.findViewById(R.id.tvItemName);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvQty = v.findViewById(R.id.tvQty);
            tvTotal = v.findViewById(R.id.tvTotal);
        }
    }
}
