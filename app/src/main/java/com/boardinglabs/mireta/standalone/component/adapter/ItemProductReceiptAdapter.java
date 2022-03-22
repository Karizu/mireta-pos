package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt.Request.Detail;
import com.boardinglabs.mireta.standalone.modul.productreceipt.CreateProductReceipt;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ItemProductReceiptAdapter extends RecyclerView.Adapter<ItemProductReceiptAdapter.ViewHolder> {
    private List<Detail> productReceiptResponses;
    private Context context;
    private Dialog dialog;

    public ItemProductReceiptAdapter(List<Detail> productReceiptResponses, Context context){
        this.productReceiptResponses = productReceiptResponses;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemProductReceiptAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_product_receipt, parent, false);

        return new ItemProductReceiptAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ItemProductReceiptAdapter.ViewHolder holder, int position){
        try {
            final Detail productReceiptResponse = productReceiptResponses.get(position);
            final String name = productReceiptResponse.getItemName();
            String qty = "0";
            qty = Integer.parseInt(productReceiptResponse.getQty()) > 1 ? productReceiptResponse.getQty() + " Items" : productReceiptResponse.getQty() + " Item";

            holder.tvName.setText(name);
            holder.qty.setText("Stok tersedia: " + qty);
            Glide.with(context).load("")
                    .placeholder(R.drawable.resto_default).fitCenter().dontAnimate()
                    .into(holder.imgItem);
            holder.layout.setOnClickListener(view -> {
                if (context instanceof CreateProductReceipt)
                ((CreateProductReceipt)context).setDetailItem(productReceiptResponse);
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
        TextView qty;
        ImageView imgItem;
        LinearLayout layout;

        ViewHolder(View v){
            super(v);

            tvName = v.findViewById(R.id.item_name);
            qty = v.findViewById(R.id.qty);
            imgItem = v.findViewById(R.id.imgItem);
            layout = v.findViewById(R.id.layout);
        }
    }

    public void updateData(List<Detail> newUser){
        productReceiptResponses = new ArrayList<>();
        productReceiptResponses.addAll(newUser);
        notifyDataSetChanged();
    }

}
