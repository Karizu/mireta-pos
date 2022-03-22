package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.entities.InquiryStatus.StockReport;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentTypeResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.Stock;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.modul.home.HomeActivity;
import com.boardinglabs.mireta.standalone.modul.master.laporan.LaporanStock;
import com.boardinglabs.mireta.standalone.modul.pickitems.PickItemsActivity;

import java.util.List;

public class RecapStockAdapter extends RecyclerView.Adapter<RecapStockAdapter.ViewHolder> {
    private List<StockReport> paymentTypeResponse;
    private Context context;
    private Dialog dialog;
    private boolean isOpen;

    public RecapStockAdapter(List<StockReport> PaymentTypeResponses, Context context, boolean isOpen){
        this.paymentTypeResponse = PaymentTypeResponses;
        this.context = context;
        this.isOpen = isOpen;
    }

    @NonNull
    @Override
    public RecapStockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recap_stok, parent, false);

        return new RecapStockAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull RecapStockAdapter.ViewHolder holder, int position){
        try {
            final StockReport stockReport = paymentTypeResponse.get(position);

            final int stockId = stockReport.getStockId();
            final String name = stockReport.getItemName();
            final int initStok = stockReport.getQtyInitial()!=null?stockReport.getQtyInitial():0;
            final int inStok = Integer.parseInt(stockReport.getSumQtyIn()!=null?stockReport.getSumQtyIn():"0");
            final int outStok = Integer.parseInt(stockReport.getSumQtyOut()!=null?stockReport.getSumQtyOut():"0");
            final int soldStok = Integer.parseInt(stockReport.getTotalSold()!=null?stockReport.getTotalSold():"0");
            final int damagedStok = stockReport.getTotalDamaged()!=null?stockReport.getTotalDamaged():0;
            final int endStok = stockReport.getQtyEnd()!=null?stockReport.getQtyEnd():0;
            String adjustStok = stockReport.getTotalAdjusted()!=null?stockReport.getTotalAdjusted():"0";

            holder.tvNameItem.setText(name);
            holder.tvInitStok.setText(MethodUtil.toCurrencyFormat(initStok+""));
            holder.tvInStok.setText(MethodUtil.toCurrencyFormat(inStok+""));
            holder.tvOutitStok.setText(MethodUtil.toCurrencyFormat(outStok+""));
            holder.tvSoldStok.setText(MethodUtil.toCurrencyFormat(soldStok+""));
            holder.tvDamagedStok.setText(MethodUtil.toCurrencyFormat(damagedStok+""));
            holder.tvEndStok.setText(MethodUtil.toCurrencyFormat(endStok+""));
            if (adjustStok.equals("0")) {
                holder.layoutAdjustment.setVisibility(View.GONE);
            } else {
                holder.layoutAdjustment.setVisibility(View.VISIBLE);
                holder.tvAdjustmentStock.setText(adjustStok);
            }

            if (isOpen) {
                holder.btnAdjustment.setVisibility(View.VISIBLE);
            } else {
                holder.btnAdjustment.setVisibility(View.GONE);
            }

            holder.btnAdjustment.setOnClickListener(v -> {
                if (context instanceof HomeActivity) {
                    ((HomeActivity)context).showAdjustmentDialog(true, stockId);
                }

                if (context instanceof LaporanStock) {
                    ((LaporanStock)context).showAdjustmentDialog(true, stockId);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount(){
        return paymentTypeResponse.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameItem, tvInitStok, tvInStok, tvOutitStok, tvSoldStok, tvDamagedStok, tvEndStok, tvAdjustmentStock;
        Button btnAdjustment;
        LinearLayout layoutAdjustment;

        ViewHolder(View v){
            super(v);
            tvNameItem = v.findViewById(R.id.item_name);
            tvInitStok = v.findViewById(R.id.tvInitStok);
            tvInStok = v.findViewById(R.id.tvInStok);
            tvOutitStok = v.findViewById(R.id.tvOutitStok);
            tvSoldStok = v.findViewById(R.id.tvSoldStok);
            tvDamagedStok = v.findViewById(R.id.tvDamagedStok);
            tvEndStok = v.findViewById(R.id.tvEndStok);
            tvAdjustmentStock = v.findViewById(R.id.tvAdjustmentStock);
            btnAdjustment = v.findViewById(R.id.btnAdjustment);
            layoutAdjustment = v.findViewById(R.id.layoutAdjustment);
        }
    }
}
