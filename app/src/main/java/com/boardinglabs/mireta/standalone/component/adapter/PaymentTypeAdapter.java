package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentTypeResponse;
import com.boardinglabs.mireta.standalone.modul.home.HomeActivity;
import com.boardinglabs.mireta.standalone.modul.pickitems.PickItemsActivity;
import com.boardinglabs.mireta.standalone.modul.transactions.items.ItemsActivity;
import com.bumptech.glide.Glide;

import java.util.List;

public class PaymentTypeAdapter extends RecyclerView.Adapter<PaymentTypeAdapter.ViewHolder> {
    private List<PaymentTypeResponse> paymentTypeResponse;
    private Context context;
    private Dialog dialog;

    public PaymentTypeAdapter(List<PaymentTypeResponse> PaymentTypeResponses, Context context){
        this.paymentTypeResponse = PaymentTypeResponses;
        this.context = context;
    }

    @NonNull
    @Override
    public PaymentTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_payment_type, parent, false);

        return new PaymentTypeAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull PaymentTypeAdapter.ViewHolder holder, int position){
        final PaymentTypeResponse accountResponse = paymentTypeResponse.get(position);

        final int id = accountResponse.getId();
        final String accountName = accountResponse.getName();
        final int spesificMethod = accountResponse.getSpecificPaymentMethodId()!=null?accountResponse.getSpecificPaymentMethodId():0;
        final int spesificItemTypeId = accountResponse.getSpecificItemTransactionTypeId();

        holder.button.setText(accountName);
        holder.button.setOnClickListener(v -> {
//            Intent intent = new Intent(context, ItemsActivity.class);
            Intent intent = new Intent(context, PickItemsActivity.class);
            intent.putExtra("transaction_type", id);
            intent.putExtra("specific_item_transaction_type_id", spesificItemTypeId);
            intent.putExtra("spesific_method", spesificMethod);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount(){
        return paymentTypeResponse.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        ViewHolder(View v){
            super(v);

            button = v.findViewById(R.id.btnPaymentType);
        }
    }
}
