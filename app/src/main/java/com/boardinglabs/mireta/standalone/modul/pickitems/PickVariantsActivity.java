package com.boardinglabs.mireta.standalone.modul.pickitems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.adapter.ChildItemVariantsAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.ParentItemVariantsAdapter;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoRegularTextView;
import com.boardinglabs.mireta.standalone.component.listener.ItemActionListener;
import com.boardinglabs.mireta.standalone.component.listener.ItemChildListener;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.GroupItem;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.Variant;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PickVariantsActivity extends BaseActivity implements ItemChildListener {

    private ParentItemVariantsAdapter adapter;

    @BindView(R.id.item_name)
    RobotoRegularTextView item_name;
    @BindView(R.id.item_price)
    RobotoRegularTextView item_price;
    @BindView(R.id.qty_input)
    EditText qty_input;
    @BindView(R.id.add_button)
    ImageButton add_button;
    @BindView(R.id.minus_button)
    ImageButton minus_button;

    @OnClick(R.id.btnTambah)
    void onClickBtnTambah(){
        ItemVariants variants = adapter.getItemVariants();
        for (Variant variant: variants.getVariants()){
            if (variant.getIsCustomisable() == 1){
                if (variant.getSumQtySelected() < variant.getSumQty()) {
                    Toast.makeText(context, "Pilih " + variant.getSumQty() + " Varian", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        String json = new Gson().toJson(variants);
        Intent intent = new Intent();
        intent.putExtra("json", json);
        setResult(RESULT_OK, intent);
        finish();
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.add_button)
    void onClickAdd(){
        int qty;
        ItemVariants variants = adapter.getItemVariants();
        if (variants.getOrderQty()==null){
            variants.setOrderQty(1);
        } else {
            qty = variants.getOrderQty();
            qty+=1;
            variants.setOrderQty(qty);
        }

        int totalPrice = Integer.parseInt(variants.getPrice()) * variants.getOrderQty();

        qty_input.setText(variants.getOrderQty() + "");
        item_price.setText(MethodUtil.toCurrencyFormat(String.valueOf(totalPrice)));

        add_button.setEnabled(true);
        add_button.setBackground(context.getResources().getDrawable(R.drawable.border_round_gradient));

        minus_button.setEnabled(true);
        minus_button.setBackground(context.getResources().getDrawable(R.drawable.border_round_gradient));
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.minus_button)
    void onClickMinus(){
        ItemVariants variants = adapter.getItemVariants();
        int orderQty = variants.getOrderQty();
        orderQty -= 1;
        variants.setOrderQty(orderQty);
        if (variants.getOrderQty() <= 1) {
            minus_button.setEnabled(false);
            minus_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));
            qty_input.setText("1");
            item_price.setText(MethodUtil.toCurrencyFormat(variants.getPrice()));
        } else {
            int totalPrice = Integer.parseInt(variants.getPrice()) * variants.getOrderQty();

            qty_input.setText("" + variants.getOrderQty());
            item_price.setText(MethodUtil.toCurrencyFormat(String.valueOf(totalPrice)));
        }


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pick_variants;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Custom Pesanan");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Context context = PickVariantsActivity.this;

        if (getIntent()!=null){
            String json = getIntent().getStringExtra("json");
            ItemVariants variants = new Gson().fromJson(json, ItemVariants.class);
            variants.setOrderQty(1);

            //set default selected qty
            for (Variant variant : variants.getVariants()){
                if (variant.getIsCustomisable() == 1){
                    variant.setSumQtySelected(0);
                    for (GroupItem groupItem : variant.getGroupItems()){
                        groupItem.setSelected_qty(0);
                    }
                }
            }

            qty_input.setText(variants.getOrderQty()+"");
//            transactionModels = variants.getVariants();H

            Log.d("variants", new Gson().toJson(variants));
            adapter = new ParentItemVariantsAdapter(variants, context, this);
            recyclerView.setAdapter(adapter);

            item_name.setText(variants.getName());
            item_price.setText(MethodUtil.toCurrencyFormat(variants.getPrice()));

            minus_button.setEnabled(false);
            minus_button.setBackground(context.getResources().getDrawable(R.drawable.gray_button_background));
        }
    }

    @Override
    protected void onCreateAtChild() {

    }

    @Override
    protected void onBackBtnPressed() {
        ItemVariants variants = adapter.getItemVariants();
        variants.setOrderQty(0);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ItemVariants variants = adapter.getItemVariants();
        variants.setOrderQty(0);
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    @Override
    public void itemClicked(int position, ItemVariants itemVariants) {

    }

    @Override
    public void itemDeleted(int position, ItemVariants itemVariants) {

    }

    @Override
    public void itemAdd(int position, ItemVariants itemVariants) {
        adapter.updateData(itemVariants);
    }

    @Override
    public void itemMinus(int position, ItemVariants itemVariants) {
        adapter.updateData(itemVariants);
    }
}