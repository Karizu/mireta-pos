package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoBoldTextView;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt.Detail;
import com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt.ProductReceiptResponse;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.productreceipt.DetailProductReceiptActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProductReceiptAdapter extends RecyclerView.Adapter<DetailProductReceiptAdapter.ViewHolder> {
    private List<Detail> productReceiptResponses;
    private Context context;
    private Dialog dialog;

    public DetailProductReceiptAdapter(List<Detail> productReceiptResponses, Context context){
        this.productReceiptResponses = productReceiptResponses;
        this.context = context;
    }

    @NonNull
    @Override
    public DetailProductReceiptAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_product_receipt, parent, false);

        return new DetailProductReceiptAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull DetailProductReceiptAdapter.ViewHolder holder, int position){
        try {
            final Detail productReceiptResponse = productReceiptResponses.get(position);
            final String name = productReceiptResponse.getItemName();
            final String qty = productReceiptResponse.getQty() > 1 ? productReceiptResponse.getQty() + " Items" : productReceiptResponse.getQty() + " Item";

            holder.tvName.setText(name);
            holder.tvQty.setText(qty);
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

        ViewHolder(View v){
            super(v);

            tvName = v.findViewById(R.id.tvItemName);
            tvQty = v.findViewById(R.id.tvQty);
        }
    }

    public void updateData(List<Detail> newUser){
        productReceiptResponses = new ArrayList<>();
        productReceiptResponses.addAll(newUser);
        notifyDataSetChanged();
    }

}
