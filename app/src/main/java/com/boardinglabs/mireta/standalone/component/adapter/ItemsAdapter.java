package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoBoldTextView;
import com.boardinglabs.mireta.standalone.component.listener.ItemActionListener;
import com.boardinglabs.mireta.standalone.component.network.entities.Item;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionModel;
import com.boardinglabs.mireta.standalone.component.network.entities.User;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.history.DetailTransactionActivity;
import com.boardinglabs.mireta.standalone.modul.transactions.items.ItemsActivity;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    public List<Item> itemList;
    private ItemActionListener itemActionListener;
    private User user;
    private View v;
    private Context mContext;
    private boolean isGridLayout = false;

    public ItemsAdapter(boolean isGridLayout){
        user = PreferenceManager.getUser();
        itemList = new ArrayList<>();
        this.isGridLayout = isGridLayout;
    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        if (isGridLayout) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_grid, null);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, null);
        }

        return new ItemsAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position){
        final Item item = itemList.get(position);
        if (item.item != null){
            String image = item.item.getImage();
            image = image.replace("index.php/", "");
            final String name = item.item.getName();
            final String description = item.item.getDescription();
            final String qty = item.qty;
            final String price = item.item.getPrice();


            Glide.with(mContext).load(image)
                    .placeholder(R.drawable.resto_default).fitCenter().dontAnimate()
                    .into(holder.item_image);


            holder.item_name.setText(name);
            holder.item_desc.setText(description);

            if (qty != null) {
                if (!qty.equals("0")) {
                    holder.item_stok.setText("Stok : " + item.getQty());
                    holder.normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_round_gradient));
                    holder.item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.gray_primary_dark));
                    holder.normal_add_button.setEnabled(true);
                } else {
                    holder.item_stok.setText("Stok Kosong");
                    holder.normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_button_background));
                    holder.item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.red_alert));
                    holder.normal_add_button.setEnabled(false);
                }
            } else {
                holder.item_stok.setText("Stok Kosong");
                holder.normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_button_background));
                holder.item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.red_alert));
                holder.normal_add_button.setEnabled(false);
            }

            holder.item_price.setText("Rp " + MethodUtil.toCurrencyFormat(price));

            if (item.order_qty <= 0) {
                holder.add_button_container.setVisibility(View.VISIBLE);
                holder.add_minus_button_container.setVisibility(View.GONE);
                holder.delete_button.setVisibility(View.GONE);
            } else {
                holder.qty_input.setText("" + item.order_qty);
                holder.add_button_container.setVisibility(View.GONE);
                holder.add_minus_button_container.setVisibility(View.VISIBLE);
                holder.delete_button.setVisibility(View.VISIBLE);
            }

            holder.add_button.setOnClickListener(view -> {
                Item item12 = itemList.get(position);
                int totalQty;
                totalQty = Integer.parseInt(item12.getQty());
                Log.d("TAG QTY", "" + totalQty);
                if (totalQty > 0) {
                    if (item12.getOrder_qty() < totalQty) {
                        item12.order_qty += 1;
                        Log.d("TAG ORDER QTY", "" + item12.getOrder_qty());
                        itemActionListener.itemAdd(position);
                        addItem(position, holder.add_button_container, holder.add_minus_button_container, holder.qty_input);
                    } else {
                        if (mContext instanceof ItemsActivity) {
                            ((ItemsActivity) mContext).showSnackbarQty();
                        }
                    }
                } else {
                    if (mContext instanceof ItemsActivity) {
                        ((ItemsActivity) mContext).showSnackbar();
                    }
                }
            });

            holder.minus_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Item item = itemList.get(position);
                    item.order_qty -= 1;
                    itemActionListener.itemMinus(position);
                    minusItem(position, holder.add_button_container, holder.add_minus_button_container, holder.qty_input);
                }
            });

            holder.delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                Item item = itemList.get(position);
//                item.order_qty -= 1;
//                itemActionListener.itemMinus(position);
//                minusItem(position, add_button_container, add_minus_button_container, qty_input);

                    Item item = itemList.get(position);
                    item.order_qty = 0;
                    itemActionListener.itemDeleted(position);
                    deleteItem(position, holder.add_button_container, holder.add_minus_button_container, holder.qty_input);
                }
            });

            holder.normal_add_button.setOnClickListener(view -> {
                Item item1 = itemList.get(position);

                if (item1.getQty() != null) {
                    int totalQty = Integer.parseInt(item1.getQty());
                    Log.d("TAG QTY", "" + totalQty);
                    if (totalQty > 0) {
                        item1.order_qty += 1;
                        Log.d("TAG ORDER QTY", "" + item1.getOrder_qty());
                        if (item1.getOrder_qty() <= totalQty) {
                            itemActionListener.itemAdd(position);
                            addItem(position, holder.add_button_container, holder.add_minus_button_container, holder.qty_input);
                        } else {
                            if (mContext instanceof ItemsActivity) {
                                ((ItemsActivity) mContext).showSnackbarQty();
                            }
                        }
                    } else {
                        if (mContext instanceof ItemsActivity) {
                            ((ItemsActivity) mContext).showSnackbar();
                        }
                    }
                }
                if (item1.getQty() == null) {
                    if (mContext instanceof ItemsActivity) {
                        ((ItemsActivity) mContext).showSnackbar();
                    }
                }

//                totalQty = item.getTotal_qty().total_qty;
//                Log.d("TAG QTY", totalQty);
//                item.order_qty += 1;
//                itemActionListener.itemAdd(position);
//                addItem(position, add_button_container, add_minus_button_container, qty_input);
            });

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                itemActionListener.itemClicked(position);
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void addItem(int position, View add_button_container, View add_minus_button_container, EditText qty_input) {
        Item item = itemList.get(position);
        if (item.order_qty >= 1) {
            int totalQty;
            totalQty = Integer.parseInt(item.getQty());
            Log.d("TAG QTY", "" + totalQty);
            if (item.getOrder_qty() <= totalQty) {
                add_button_container.setVisibility(View.GONE);
                add_minus_button_container.setVisibility(View.VISIBLE);
                qty_input.setText("" + item.order_qty);
            } else {
                if (mContext instanceof ItemsActivity) {
                    ((ItemsActivity) mContext).showSnackbarQty();
                }
            }
        }
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void minusItem(int position, View add_button_container, View add_minus_button_container, EditText qty_input) {
        Item item = itemList.get(position);
        if (item.order_qty <= 0) {
            add_button_container.setVisibility(View.VISIBLE);
            add_minus_button_container.setVisibility(View.GONE);
            qty_input.setText("0");
        } else {
            add_button_container.setVisibility(View.GONE);
            add_minus_button_container.setVisibility(View.VISIBLE);
            qty_input.setText("" + item.order_qty);
        }
        notifyDataSetChanged();
    }

    private void deleteItem(int position, View add_button_container, View add_minus_button_container, EditText qty_input) {
        Item item = itemList.get(position);
        add_button_container.setVisibility(View.VISIBLE);
        add_minus_button_container.setVisibility(View.GONE);
        qty_input.setText("0");
        notifyDataSetChanged();
    }

    public void setListener(ItemActionListener listClicked) {
        this.itemActionListener = listClicked;
    }

    public void setDataList(List<Item> transactionItems, Context context) {
        itemList = transactionItems;
        mContext = context;
        notifyDataSetChanged();
    }

    public void addDataList(List<Item> items) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        ImageView item_image;
        TextView item_name;
        TextView item_desc;
        TextView item_stok;
        TextView item_price;
        EditText qty_input;
        ImageButton add_button;
        ImageButton minus_button;
        Button normal_add_button;
        ImageButton delete_button;
        LinearLayout add_button_container;
        LinearLayout add_minus_button_container;


        ViewHolder(View v){
            super(v);
            container = v.findViewById(R.id.container_item);
            item_image = v.findViewById(R.id.item_image);
            item_name = v.findViewById(R.id.item_name);
            item_desc = v.findViewById(R.id.item_desc);
            item_stok = v.findViewById(R.id.item_stok);
            item_price = v.findViewById(R.id.item_price);
            qty_input = v.findViewById(R.id.qty_input);
            add_button = v.findViewById(R.id.add_button);
            minus_button = v.findViewById(R.id.minus_button);
            normal_add_button = v.findViewById(R.id.normal_add_button);
            delete_button = v.findViewById(R.id.delete_button);
            add_button_container = v.findViewById(R.id.add_button_container);
            add_minus_button_container = v.findViewById(R.id.add_minus_container);

        }
    }
}
