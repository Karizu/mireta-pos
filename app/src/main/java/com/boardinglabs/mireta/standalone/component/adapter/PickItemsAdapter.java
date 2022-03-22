package com.boardinglabs.mireta.standalone.component.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.listener.ItemActionListener;
import com.boardinglabs.mireta.standalone.component.network.entities.ChildItem.ItemsResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Item;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;
import com.boardinglabs.mireta.standalone.component.network.entities.User;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.pickitems.PickItemsActivity;
import com.boardinglabs.mireta.standalone.modul.transactions.items.ItemsActivity;
import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

//import com.boardinglabs.mireta.standalone.component.util.CustomBottomSheetDialog;

/**
 * Created by Dhimas on 12/20/17.
 */

public class PickItemsAdapter extends BaseSwipeAdapter {
    public List<ItemVariants> itemList;
    private ItemActionListener itemActionListener;
    private User user;
    private View v;
    private Context mContext;
    private boolean isGridLayout = false;

    public PickItemsAdapter(boolean isGridLayout) {
        user = PreferenceManager.getUser();
        itemList = new ArrayList<>();
        this.isGridLayout = isGridLayout;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.item_row_swipe_layout;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        if (isGridLayout) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_grid, null);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, null);
        }

        final SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        return v;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void fillValues(final int position, View convertView) {

        final ItemVariants item = itemList.get(position);
        final LinearLayout container = convertView.findViewById(R.id.container_item);
        final ImageView item_image = convertView.findViewById(R.id.item_image);
        final TextView item_name = convertView.findViewById(R.id.item_name);
        final TextView item_desc = convertView.findViewById(R.id.item_desc);
        final TextView item_stok = convertView.findViewById(R.id.item_stok);
        final TextView item_price = convertView.findViewById(R.id.item_price);
        final TextView item_price_strike = convertView.findViewById(R.id.item_price_strike);
        final TextView qty_input = convertView.findViewById(R.id.qty_input);
        final ImageButton add_button = convertView.findViewById(R.id.add_button);
        final ImageButton minus_button = convertView.findViewById(R.id.minus_button);
        final Button normal_add_button = convertView.findViewById(R.id.normal_add_button);
        final ImageButton delete_button = convertView.findViewById(R.id.delete_button);
        final LinearLayout add_button_container = convertView.findViewById(R.id.add_button_container);
        final LinearLayout add_minus_button_container = convertView.findViewById(R.id.add_minus_container);

        String image = item.getImage();
        image = image.replace("index.php/", "");
        final String name = item.getName();
        final String description = item.getDescription();
        final String qty = item.getStock().getIsChildBasedStock()==0? String.valueOf(item.getStock().getQty()) :"-";
        final String price = item.getPrice();
        final int discount = Integer.parseInt(item.getDiscountPrice()!=null?item.getDiscountPrice():"0");

        int strikePrice = Integer.parseInt(price) - discount;

        if (item.getOrderQty() == null) {
            item.setOrderQty(0);
        }

        Glide.with(convertView.getContext()).load(image)
                .placeholder(R.drawable.resto_default).fitCenter().dontAnimate()
                .into(item_image);

        item_name.setText(name);
        item_desc.setText(description);
        item_price.setText("Rp " + MethodUtil.toCurrencyFormat(price));

        if (discount != 0) {
            item_price_strike.setVisibility(View.VISIBLE);
            item_price_strike.setText("Rp " + MethodUtil.toCurrencyFormat(price));
            item_price_strike.setPaintFlags(item_price_strike.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            item_price.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(strikePrice)));
        } else {
            item_price_strike.setVisibility(View.GONE);
        }

        checkItemSelected(position, add_button_container, add_minus_button_container, qty_input);

        if (qty != null) {
            if (!qty.equals("0")) {
                normal_add_button.setText("TAMBAH");
                normal_add_button.setTextColor(mContext.getResources().getColor(R.color.white));
                item_stok.setText("Stok : " + item.getStock().getQty());
                normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_round_gradient));
                item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.gray_primary_dark));
                normal_add_button.setEnabled(true);
            }
            else {
                item_stok.setText("Stok Kosong");
                normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_button_background));
                item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.red_alert));
                normal_add_button.setEnabled(false);
            }
        } else {
            item_stok.setText("Stok Kosong");
            normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_button_background));
            item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.red_alert));
            normal_add_button.setEnabled(false);
        }

        if (((PickItemsActivity)mContext).isItemOrdered(item) && item.getIsGroupedItem() == 1){
            normal_add_button.setText("Ubah");
            normal_add_button.setTextColor(mContext.getResources().getColor(R.color.dusty_orange));
            normal_add_button.setBackground(mContext.getResources().getDrawable(R.drawable.border_round_outline));
        }

        if (item.getOrderQty() <= 0) {
            add_button_container.setVisibility(View.VISIBLE);
            add_minus_button_container.setVisibility(View.GONE);
            delete_button.setVisibility(View.GONE);
        }
        else {
            qty_input.setText("" + item.getOrderQty());
            add_button_container.setVisibility(View.GONE);
            add_minus_button_container.setVisibility(View.VISIBLE);
            delete_button.setVisibility(View.VISIBLE);
        }

//        if (((PickItemsActivity)mContext).isItemOrdered(item) && item.getIsGroupedItem() == 0){
//            if (item.getOrderQty() > 0) {
//                addItem(position, add_button_container, add_minus_button_container, qty_input);
//            }
//        }

        add_button.setOnClickListener(view -> {
            if (item.getIsGroupedItem() == 1){
                if (mContext instanceof PickItemsActivity){
                    minus_button.setVisibility(View.GONE);
                    add_button.setVisibility(View.GONE);

                    ((PickItemsActivity)mContext).clickDetailVariant(item);
                    return;
                }
            }

            ItemVariants item12 = itemList.get(position);
            int totalQty;
            totalQty = Integer.parseInt(item12.getStock().getIsChildBasedStock() == 0 ? item12.getStock().getQty()+"":"1");
            Log.d("TAG QTY", "" + totalQty);
            if (totalQty > 0) {
                if (item12.getOrderQty() < totalQty) {
                    int orderQty = item12.getOrderQty();
                    orderQty += 1;
                    item12.setOrderQty(orderQty);
                    itemActionListener.itemAdd(position);
                    addItem(position, add_button_container, add_minus_button_container, qty_input);
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

        minus_button.setOnClickListener(view -> {
            ItemVariants item13 = itemList.get(position);
            int orderQty = item13.getOrderQty();
            orderQty -= 1;
            item13.setOrderQty(orderQty);
            itemActionListener.itemMinus(position);
            minusItem(position, add_button_container, add_minus_button_container, qty_input);
        });

        delete_button.setOnClickListener(view -> {
            ItemVariants item14 = itemList.get(position);
            item14.setOrderQty(0);
            itemActionListener.itemDeleted(position);
            deleteItem(position, add_button_container, add_minus_button_container, qty_input);
        });

        normal_add_button.setOnClickListener(view -> {
            if (item.getIsGroupedItem() == 1){
                if (mContext instanceof PickItemsActivity){
                    minus_button.setVisibility(View.GONE);
                    add_button.setVisibility(View.GONE);
                    qty_input.setEnabled(true);
                    qty_input.setOnClickListener(v1 -> ((PickItemsActivity)mContext).setDialogData(v1, item));
//                    ((PickItemsActivity)mContext).clickDetailVariant(item);
                    ((PickItemsActivity)mContext).setDialogData(view, item);
                    return;
                }
            }

            qty_input.setEnabled(false);
            ItemVariants item1 = itemList.get(position);

            if (item1.getStock().getIsChildBasedStock() == 0) {
                int totalQty = item1.getStock().getQty();
                if (totalQty > 0) {
                    int orderQty = item1.getOrderQty();
                    orderQty += 1;
                    item1.setOrderQty(orderQty);
                    if (item1.getOrderQty() <= totalQty) {
                        itemActionListener.itemAdd(position);
                        addItem(position, add_button_container, add_minus_button_container, qty_input);
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
        });
    }

    @SuppressLint("SetTextI18n")
    private void checkItemSelected(int position, LinearLayout add_button_container, LinearLayout add_minus_button_container, TextView qty_input) {
        ItemVariants item = itemList.get(position);
        if (item.getOrderQty() >= 1) {
            int totalQty;
            if (item.getIsGroupedItem() == 0){
                totalQty = item.getStock().getQty();
                if (item.getOrderQty() <= totalQty) {
                    add_button_container.setVisibility(View.GONE);
                    add_minus_button_container.setVisibility(View.VISIBLE);
                    qty_input.setText("" + item.getOrderQty());
                } else {
                    if (mContext instanceof PickItemsActivity) {
                        ((PickItemsActivity) mContext).showSnackbarQty();
                    }
                }
            } else {
                add_button_container.setVisibility(View.GONE);
                add_minus_button_container.setVisibility(View.VISIBLE);
                qty_input.setText("" + item.getOrderQty());
            }

        }
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void addItem(int position, View add_button_container, View add_minus_button_container, TextView qty_input) {
        ItemVariants item = itemList.get(position);
        if (item.getOrderQty() >= 1) {
            int totalQty;
            totalQty = Integer.parseInt(item.getStock().getIsChildBasedStock() == 0 ? item.getStock().getQty()+"" : "2");
            if (item.getOrderQty() <= totalQty) {
                add_button_container.setVisibility(View.GONE);
                add_minus_button_container.setVisibility(View.VISIBLE);
                qty_input.setText("" + item.getOrderQty());
            } else {
                if (mContext instanceof ItemsActivity) {
                    ((ItemsActivity) mContext).showSnackbarQty();
                }
            }
        }
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void minusItem(int position, View add_button_container, View add_minus_button_container, TextView qty_input) {
        ItemVariants item = itemList.get(position);
        if (item.getOrderQty() <= 0) {
            add_button_container.setVisibility(View.VISIBLE);
            add_minus_button_container.setVisibility(View.GONE);
            qty_input.setText("0");
        } else {
            add_button_container.setVisibility(View.GONE);
            add_minus_button_container.setVisibility(View.VISIBLE);
            qty_input.setText("" + item.getOrderQty());
        }
        notifyDataSetChanged();
    }

    private void deleteItem(int position, View add_button_container, View add_minus_button_container, TextView qty_input) {
        ItemVariants item = itemList.get(position);
        add_button_container.setVisibility(View.VISIBLE);
        add_minus_button_container.setVisibility(View.GONE);
        qty_input.setText("0");
        notifyDataSetChanged();
    }

    public void setListener(ItemActionListener listClicked) {
        this.itemActionListener = listClicked;
    }

    public void setDataList(List<ItemVariants> transactionItems, Context context) {
        itemList = transactionItems;
        mContext = context;
        notifyDataSetChanged();
    }

    public void addDataList(List<ItemVariants> items) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemList.addAll(items);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void updateData(List<ItemVariants> newUser) {
        itemList = new ArrayList<>();
        itemList.addAll(newUser);
        notifyDataSetChanged();
    }
}
