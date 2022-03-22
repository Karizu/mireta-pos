package com.boardinglabs.mireta.standalone.component.adapter;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.listener.ListActionListener;
import com.boardinglabs.mireta.standalone.component.network.entities.ChildItem.ItemsResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Item;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;
import com.boardinglabs.mireta.standalone.component.network.entities.User;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhimas on 12/20/17.
 */

public class RecyOverviewNewItemsAdapter extends BaseSwipeAdapter {
    public List<ItemVariants> itemList;
    private ListActionListener listActionListener;
    private User user;

    public RecyOverviewNewItemsAdapter() {

        user = PreferenceManager.getUser();
        itemList = new ArrayList<>();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.item_row_swipe_layout;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_overview, null);
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        final ItemVariants item = itemList.get(position);

        return v;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void fillValues(final int position, View convertView) {

        final ItemVariants item = itemList.get(position);
        final LinearLayout container = convertView.findViewById(R.id.container_item);
        final TextView item_name = convertView.findViewById(R.id.item_name);
        final TextView item_qty = convertView.findViewById(R.id.item_total);
        final TextView item_price = convertView.findViewById(R.id.item_total_price);
        final ImageButton delete_button = convertView.findViewById(R.id.delete_button);

        item_name.setText(item.getName());
        item_qty.setText(item.getOrderQty() + " Item");
        long total_price = (long) (Integer.parseInt(item.getPrice()) * item.getOrderQty());
        item_price.setText("Rp " + MethodUtil.toCurrencyFormat(Long.toString(total_price)));

        delete_button.setOnClickListener(view -> {
            deleteItem(position);
            listActionListener.itemClicked(position);
        });
    }

    private void deleteItem(int position){
//        itemList.remove(position);
//        notifyDataSetChanged();
    }

    public void setDataList(List<ItemVariants> transactionItems) {
        itemList = transactionItems;
        notifyDataSetChanged();
    }

    public void addDataList(List<ItemVariants> items) {
        if (itemList == null){
            itemList = new ArrayList<>();
        }
        itemList.addAll(items);
        notifyDataSetChanged();
    }
    public void setListener(ListActionListener listClicked) {
        this.listActionListener = listClicked;
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


}
