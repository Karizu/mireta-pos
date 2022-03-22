package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionDetailModel;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DetailTransactionAdapter extends RecyclerView.Adapter<DetailTransactionAdapter.ViewHolder> {
    private List<TransactionDetailModel> transactionDetailModels;
    private Context context;
    private Dialog dialog;

    public DetailTransactionAdapter(List<TransactionDetailModel> transactionDetailModels, Context context){
        this.transactionDetailModels = transactionDetailModels;
        this.context = context;
    }

    @NonNull
    @Override
    public DetailTransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_item_detail_transaction, parent, false);

        return new DetailTransactionAdapter.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DetailTransactionAdapter.ViewHolder holder, int position){
        final TransactionDetailModel transactionDetailModel = transactionDetailModels.get(position);
//        final String id = katalogModel.getId();
        final String item_name = transactionDetailModel.getItem_name();
        final String discount = transactionDetailModel.getDiscount();
        final String price = transactionDetailModel.getPrice();
        final String quantity = transactionDetailModel.getQuantity();
        final String totalPrice = transactionDetailModel.getTotal_price();
        String desc = transactionDetailModel.getDesc()==null?"-":transactionDetailModel.getDesc();
        desc = desc.equals("")?desc:"(" + transactionDetailModel.getDesc()+ ")";

        int mDiscount = Integer.parseInt(discount) * Integer.parseInt(quantity);
        int mAmount = Integer.parseInt(price) * Integer.parseInt(quantity);
        int mPrice = Integer.parseInt(totalPrice) * Integer.parseInt(quantity);

        holder.tvItemName.setText(item_name);
        holder.tvQty.setText(quantity + " x ");
        holder.tvRealPrice.setText("" + NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(totalPrice)));

        holder.tvDiscount.setText("-" + NumberFormat.getNumberInstance(Locale.US).format(mDiscount));
        if (mDiscount==0){
            holder.layoutDiscount.setVisibility(View.GONE);
        }

        holder.tvAmount.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mPrice));
        holder.tvItemDesc.setText(desc);
        holder.tvTotal.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mAmount));
        holder.layoutDetailTransaksi.setOnClickListener(view -> {
//            View viewSheet = LayoutInflater.from(view.getContext()).inflate(R.layout.find_outreach_worker_bottom_sheet_dialog, null);
//            Log.d( "onClick: ",String.valueOf(viewSheet));
//            final BottomSheetDialog dialog = new BottomSheetDialog(view.getContext());
//            dialog.setContentView(viewSheet);
//            dialog.show();
        });
    }

    @Override
    public int getItemCount(){
        return transactionDetailModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemDesc, tvTotal, tvRealPrice;
        TextView tvAmount;
        TextView tvDiscount;
        TextView tvQty;
        LinearLayout layoutDetailTransaksi, layoutDiscount;

        ViewHolder(View v){
            super(v);
            tvItemDesc = v.findViewById(R.id.tvItemDesc);
            tvItemName = v.findViewById(R.id.tvItemName);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvDiscount = v.findViewById(R.id.tvDiscount);
            tvQty = v.findViewById(R.id.tvQty);
            tvTotal = v.findViewById(R.id.tvTotal);
            tvRealPrice = v.findViewById(R.id.tvRealPrice);
            layoutDetailTransaksi = v.findViewById(R.id.layoutDetailTransaksi);
            layoutDiscount = v.findViewById(R.id.layoutDiscount);
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
