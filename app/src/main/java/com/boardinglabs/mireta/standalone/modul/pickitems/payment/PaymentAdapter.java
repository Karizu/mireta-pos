package com.boardinglabs.mireta.standalone.modul.pickitems.payment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;
import com.boardinglabs.mireta.standalone.modul.transactions.items.pembayaran.Items;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    private List<ItemVariants> transactionDetailModels;
    private Context context;
    private Dialog dialog;

    public PaymentAdapter(List<ItemVariants> transactionDetailModels, Context context){
        this.transactionDetailModels = transactionDetailModels;
        this.context = context;
    }

    @NonNull
    @Override
    public PaymentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_item_pembayaran, parent, false);

        return new PaymentAdapter.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PaymentAdapter.ViewHolder holder, int position){
        final ItemVariants transactionDetailModel = transactionDetailModels.get(position);
//        final String id = katalogModel.getId();
        final String item_name = transactionDetailModel.getName();
        final String price = transactionDetailModel.getPrice();
        final String quantity = String.valueOf(transactionDetailModel.getOrderQty());

        int mAmount = Integer.parseInt(price) * Integer.parseInt(quantity);

        holder.tvItemName.setText(item_name);
        holder.tvQty.setText(quantity + " x");
        holder.tvAmount.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mAmount));

    }

    @Override
    public int getItemCount(){
        return transactionDetailModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        TextView tvAmount;
        TextView tvQty;

        ViewHolder(View v){
            super(v);

            tvItemName = v.findViewById(R.id.tvNameProduct);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvQty = v.findViewById(R.id.tvQty);
        }
    }

    private void showDialog(int layout, Context context) {
        dialog = new Dialog(Objects.requireNonNull(context));
        //set content
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
