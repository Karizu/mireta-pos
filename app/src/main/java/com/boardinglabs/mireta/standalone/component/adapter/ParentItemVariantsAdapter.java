package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoBoldTextView;
import com.boardinglabs.mireta.standalone.component.listener.ItemActionListener;
import com.boardinglabs.mireta.standalone.component.listener.ItemChildListener;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.Variant;

public class ParentItemVariantsAdapter extends RecyclerView.Adapter<ParentItemVariantsAdapter.ViewHolder>{
    public ItemVariants itemVariants;
    private Context context;
    private ChildItemVariantsAdapter childItemAdapter;
    private ItemActionListener listClicked;
    private Dialog dialog;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ViewHolder viewHolder;
    private int sumSelectedQty = 0;
    private int sumQty;
    private ItemChildListener actionListener;

    public ParentItemVariantsAdapter(ItemVariants itemVariantsList, Context context, ItemChildListener actionListener){
        this.itemVariants = itemVariantsList;
        this.context = context;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ParentItemVariantsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_parent_item_variants, parent, false);

        return new ParentItemVariantsAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ParentItemVariantsAdapter.ViewHolder holder, int position){
        final Variant item = this.itemVariants.getVariants().get(position);
        viewHolder = holder;
        final String id = String.valueOf(item.getId());
        final String title = item.getName();
        boolean isCustomisable = item.getIsCustomisable() != 0;
        boolean isFixedQuantity = item.getIsFixedQty() != 0;
        sumQty = item.getSumQty()!=null?item.getSumQty():item.getFixedQty();
        holder.parent_item_qty.setVisibility(View.VISIBLE);

        if (isFixedQuantity) {
            holder.parent_item_qty.setText(item.getFixedQty()+"");
        } else {
            int sumQtySelected = item.getSumQtySelected()!=null?item.getSumQtySelected():0;
            holder.parent_item_qty.setText(sumQtySelected+"");
        }

        childItemAdapter = new ChildItemVariantsAdapter(itemVariants, context, isCustomisable, isFixedQuantity, position);
        childItemAdapter.setListener(actionListener);

        holder.parent_item_title.setText(title + " (Maks. " + sumQty + ")");
        holder.child_recyclerview.setAdapter(childItemAdapter);
    }

    public ItemVariants getItemVariants(){
        return childItemAdapter.itemVariants;
    }

    @Override
    public int getItemCount(){
        return itemVariants.getVariants().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RobotoBoldTextView parent_item_title, parent_item_qty;
        RecyclerView child_recyclerview;

        ViewHolder(View v){
            super(v);
            parent_item_qty = v.findViewById(R.id.parent_item_qty);
            parent_item_title = v.findViewById(R.id.parent_item_title);
            child_recyclerview = v.findViewById(R.id.child_recyclerview);
        }
    }

    public void updateData(ItemVariants variants){
        itemVariants = variants;
        notifyDataSetChanged();
    }
}
