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
import android.widget.ImageView;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt.Request.Detail;

import java.util.ArrayList;
import java.util.List;

public class AddProductReceiptAdapter extends RecyclerView.Adapter<AddProductReceiptAdapter.ViewHolder> {
    private List<Detail> productReceiptResponses;
    private Context context;
    private Dialog dialog;

    public AddProductReceiptAdapter(List<Detail> productReceiptResponses, Context context){
        this.productReceiptResponses = productReceiptResponses;
        this.context = context;
    }

    @NonNull
    @Override
    public AddProductReceiptAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_product_receipt, parent, false);

        return new AddProductReceiptAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull AddProductReceiptAdapter.ViewHolder holder, int position){
        try {
            final Detail productReceiptResponse = productReceiptResponses.get(position);
            final String name = productReceiptResponse.getItemName();
            final String qty = Integer.parseInt(productReceiptResponse.getQty()) > 1 ? productReceiptResponse.getQty() + " Items" : productReceiptResponse.getQty() + " Item";

            holder.tvName.setText(name);
            holder.tvQty.setText(qty);
            holder.btnItemDelete.setOnClickListener(view -> {
                productReceiptResponses.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, productReceiptResponses.size());
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount(){
        return productReceiptResponses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvQty;
        ImageView btnItemDelete;

        ViewHolder(View v){
            super(v);

            tvName = v.findViewById(R.id.tvItemName);
            tvQty = v.findViewById(R.id.tvQty);
            btnItemDelete = v.findViewById(R.id.btnItemDelete);
        }
    }

    public void updateData(List<Detail> newUser){
        productReceiptResponses = new ArrayList<>();
        productReceiptResponses.addAll(newUser);
        notifyDataSetChanged();
    }

}
