package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentAccountResponse;
import com.bumptech.glide.Glide;

import java.util.List;

public class PaymentAccountAdapter extends RecyclerView.Adapter<PaymentAccountAdapter.ViewHolder> {
    private List<PaymentAccountResponse> paymentAccountResponse;
    private Context context;
    private Dialog dialog;

    public PaymentAccountAdapter(List<PaymentAccountResponse> PaymentAccountResponses, Context context){
        this.paymentAccountResponse = PaymentAccountResponses;
        this.context = context;
    }

    @NonNull
    @Override
    public PaymentAccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_payment_account, parent, false);

        return new PaymentAccountAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull PaymentAccountAdapter.ViewHolder holder, int position){
        final PaymentAccountResponse accountResponse = paymentAccountResponse.get(position);

        final String id = String.valueOf(accountResponse.getId());
        final String accountName = accountResponse.getAccountName();
        final String accountNumber = accountResponse.getAccountNumber();
        final String accountBankName = accountResponse.getAccountBankName();
        final String image = accountResponse.getImage().replace("index.php/", "");

        holder.tvAccountName.setText(accountName);
        holder.tvAccountNumber.setText(accountNumber);
        holder.tvBankName.setText(accountBankName);

        Glide.with(context)
                .load(image)
                .placeholder(R.drawable.qris_selada)
                .into(holder.img_qris);
    }

    @Override
    public int getItemCount(){
        return paymentAccountResponse.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccountName, tvAccountNumber, tvBankName;
        ImageView img_qris;

        ViewHolder(View v){
            super(v);

            tvAccountName = v.findViewById(R.id.tvAccountName);
            tvAccountNumber = v.findViewById(R.id.tvAccountNumber);
            tvBankName = v.findViewById(R.id.tvBankName);
            img_qris = v.findViewById(R.id.img_qris);
        }
    }
}
