package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.GroupItem;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.Variant;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.Stock;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.modul.pickitems.PickItemsActivity;

import java.util.ArrayList;
import java.util.List;

public class OverviewItemVarianAdapter extends RecyclerView.Adapter<OverviewItemVarianAdapter.ViewHolder> {
    private List<ItemVariants> itemVariantList;
    private Context context;
    private Dialog dialog;

    public OverviewItemVarianAdapter(List<ItemVariants> itemVariantList, Context context){
        this.itemVariantList = itemVariantList;
        this.context = context;
    }

    @NonNull
    @Override
    public OverviewItemVarianAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_overview_variant, parent, false);

        return new OverviewItemVarianAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull OverviewItemVarianAdapter.ViewHolder holder, int position){
        final ItemVariants variants = itemVariantList.get(position);
        final String price = variants.getPrice();
        int qty = variants.getOrderQty();

        StringBuilder detailName = new StringBuilder();
        for (Variant variant : variants.getVariants()){
            if (variant.getIsCustomisable() == 1){
                for (GroupItem groupItem : variant.getGroupItems()){
                    if (groupItem.getSelected_qty()>0) detailName.append(groupItem.getName()).append(" (").append(groupItem.getSelected_qty()).append("), ");
                }
            } else {
                detailName.append(variant.getItem().getName()).append(" (").append(variant.getItem().getSelected_qty()).append("), ");
            }
        }

        String qtyText;
        qtyText = qty + "x";

        int mPrice = Integer.parseInt(price) * qty;

        holder.itemDetailName.setText(detailName.substring(0, detailName.length() - 2));
        holder.itemQty.setText(qtyText);
        holder.itemPrice.setText(MethodUtil.toCurrencyFormat(String.valueOf(mPrice)));
        holder.deleteButton.setOnClickListener(v -> {
            if (context instanceof PickItemsActivity) {
                ((PickItemsActivity)context).itemVarianDeleted(variants);
                itemVariantList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount(){
        return itemVariantList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemPrice, itemDetailName, itemQty;
        ImageButton deleteButton;

        ViewHolder(View v){
            super(v);
            itemPrice = v.findViewById(R.id.item_price);
            itemDetailName = v.findViewById(R.id.item_detail_name);
            itemQty = v.findViewById(R.id.item_qty);
            deleteButton = v.findViewById(R.id.delete_button);
        }
    }
}
