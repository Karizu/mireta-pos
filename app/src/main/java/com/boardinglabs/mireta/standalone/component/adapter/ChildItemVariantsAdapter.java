package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.listener.ItemActionListener;
import com.boardinglabs.mireta.standalone.component.listener.ItemChildListener;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.GroupItem;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.Item;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.Variant;
import com.boardinglabs.mireta.standalone.modul.pickitems.PickVariantsActivity;
import com.boardinglabs.mireta.standalone.modul.transactions.items.ItemsActivity;

import java.util.List;

public class ChildItemVariantsAdapter extends RecyclerView.Adapter<ChildItemVariantsAdapter.ViewHolder> {
    public ItemVariants itemVariants;
    private boolean isCustomisable = false;
    private Context context;
    private Dialog dialog;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private boolean isFixedQuantity;
    private int pos;
    private int sumSelectedQty = 0;
    private int sumQty;
    private ItemChildListener actionListener;

    public ChildItemVariantsAdapter(ItemVariants itemVariants, Context context, boolean isCustomisable, boolean isFixedQuantity, int pos){
        this.itemVariants = itemVariants;
        this.context = context;
        this.isCustomisable = isCustomisable;
        this.isFixedQuantity = isFixedQuantity;
        this.pos = pos;
    }

    @NonNull
    @Override
    public ChildItemVariantsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_child_item_variants, parent, false);

        return new ChildItemVariantsAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ChildItemVariantsAdapter.ViewHolder holder, int position){
        try {
            if (isCustomisable){
                final GroupItem item = itemVariants.getVariants().get(pos).getGroupItems().get(position);
                final String id = String.valueOf(item.getId());
                final String name = item.getName();
                final int selectedQty = item.getSelected_qty()!=null ? item.getSelected_qty() : 0;
                sumQty = itemVariants.getVariants().get(pos).getSumQty()!=null?itemVariants.getVariants().get(pos).getSumQty():itemVariants.getVariants().get(pos).getFixedQty();

                if (itemVariants.getVariants().get(pos).getSumQtySelected()==null){
                    itemVariants.getVariants().get(pos).setSumQtySelected(0);
                }

                holder.item_name.setText(name);

                if (isFixedQuantity) {
                    item.setSelected_qty(itemVariants.getVariants().get(pos).getFixedQty());
                    holder.add_button.setEnabled(false);
                    holder.add_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));

                    holder.minus_button.setEnabled(false);
                    holder.minus_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));
                }
                else {
                    if (selectedQty == 0) {
                        item.setSelected_qty(0);
                        holder.minus_button.setEnabled(false);
                        holder.minus_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));
                    }

                    holder.add_button.setOnClickListener(v -> {
                        GroupItem item12 = itemVariants.getVariants().get(pos).getGroupItems().get(position);
                        int totalQty;
                        totalQty = item12.getStock().getQty();
                        Log.d("TAG QTY", "" + totalQty);
                        if (totalQty > 0) {
                            if (item12.getSelected_qty() < totalQty) {
                                int orderQty = item12.getSelected_qty();
                                orderQty += 1;
                                int sumQtySelected = itemVariants.getVariants().get(pos).getSumQtySelected() + 1;
                                if (sumQtySelected <= sumQty) {
                                    itemVariants.getVariants().get(pos).setSumQtySelected(sumQtySelected);
                                    item12.setSelected_qty(orderQty);
                                    if (item12.getSelected_qty() <= totalQty) {
                                        holder.qty_input.setText("" + item.getSelected_qty());
                                    }

                                    holder.add_button.setEnabled(true);
                                    holder.add_button.setBackground(context.getResources().getDrawable(R.drawable.border_round_gradient));

                                    holder.minus_button.setEnabled(true);
                                    holder.minus_button.setBackground(context.getResources().getDrawable(R.drawable.border_round_gradient));

                                    actionListener.itemAdd(pos, itemVariants);
                                } else {
                                    Toast.makeText(context, "Maksimal " + sumQty, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            holder.add_button.setEnabled(false);
                            holder.add_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));

                            holder.minus_button.setEnabled(false);
                            holder.minus_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));
                        }
                    });

                    holder.minus_button.setOnClickListener(v -> {
                        GroupItem item12 = itemVariants.getVariants().get(pos).getGroupItems().get(position);
                        int orderQty = item12.getSelected_qty();
                        orderQty -= 1;
                        item12.setSelected_qty(orderQty);
                        int sumSelected = itemVariants.getVariants().get(pos).getSumQtySelected() - 1;
                        itemVariants.getVariants().get(pos).setSumQtySelected(sumSelected);
                        if (item12.getSelected_qty() <= 0) {
                            holder.minus_button.setEnabled(false);
                            holder.minus_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));
                            holder.qty_input.setText("0");
                        } else {
                            holder.qty_input.setText("" + item.getSelected_qty());
                        }

                        actionListener.itemMinus(pos, itemVariants);
                    });
                }

                holder.qty_input.setText(item.getSelected_qty()+"");

            }
            else {
                final Item item = itemVariants.getVariants().get(pos).getItem();
                final String id = String.valueOf(itemVariants.getId());
                final String name = item.getName();

                holder.item_name.setText(name);

                if (isFixedQuantity) {
                    item.setSelected_qty(itemVariants.getVariants().get(pos).getFixedQty());

                    holder.add_button.setEnabled(false);
                    holder.add_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));

                    holder.minus_button.setEnabled(false);
                    holder.minus_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));
                } else {
                    item.setSelected_qty(0);
                    holder.minus_button.setEnabled(false);
                    holder.minus_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));

                    holder.add_button.setOnClickListener(v -> {
                        Item item12 = itemVariants.getVariants().get(pos).getItem();
                        int totalQty;
                        totalQty = item12.getStock().getQty();
                        Log.d("TAG QTY", "" + totalQty);
                        if (totalQty > 0) {
                            if (item12.getSelected_qty() < totalQty) {

                                int orderQty = item12.getSelected_qty();
                                orderQty += 1;
                                if (isUpdateSumQty()) {
                                    item12.setSelected_qty(orderQty);
                                    if (item12.getSelected_qty() <= totalQty) {
                                        holder.qty_input.setText("" + item.getSelected_qty());
                                    }

                                    holder.add_button.setEnabled(true);
                                    holder.add_button.setBackground(context.getResources().getDrawable(R.drawable.border_round_gradient));

                                    holder.minus_button.setEnabled(true);
                                    holder.minus_button.setBackground(context.getResources().getDrawable(R.drawable.border_round_gradient));
                                } else {
                                    Toast.makeText(context, "Maksimal " + sumQty, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            holder.add_button.setEnabled(false);
                            holder.add_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));

                            holder.minus_button.setEnabled(false);
                            holder.minus_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));

                        }
                    });

                    holder.minus_button.setOnClickListener(v -> {
                        Item item12 = itemVariants.getVariants().get(pos).getItem();
                        int orderQty = item12.getSelected_qty();
                        orderQty -= 1;
                        item12.setSelected_qty(orderQty);
                        setMinUpdateSumQty();
                        if (item12.getSelected_qty() <= 0) {
                            holder.minus_button.setEnabled(false);
                            holder.minus_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));
                            holder.qty_input.setText("0");
                        } else {
                            holder.qty_input.setText("" + item.getSelected_qty());
                        }
                    });
                }

                holder.qty_input.setText(item.getSelected_qty()+"");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setListener(ItemChildListener listClicked) {
        this.actionListener = listClicked;
    }

    @SuppressLint("SetTextI18n")
    public boolean isUpdateSumQty(){
        sumSelectedQty += 1;
        if (sumSelectedQty <= sumQty) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("SetTextI18n")
    public void setMinUpdateSumQty(){
        sumSelectedQty = sumSelectedQty - 1;
    }

    @Override
    public int getItemCount(){
        if (isCustomisable){
            return itemVariants.getVariants().get(pos).getGroupItems().size();
        } else {
            return 1;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_name;
        ImageButton minus_button, add_button;
        EditText qty_input;

        ViewHolder(View v){
            super(v);

            item_name = v.findViewById(R.id.item_name);
            minus_button = v.findViewById(R.id.minus_button);
            add_button = v.findViewById(R.id.add_button);
            qty_input = v.findViewById(R.id.qty_input);
        }
    }
}
