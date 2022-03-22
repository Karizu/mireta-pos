package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentAccountResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentMethodResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentTypeResponse;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.history.DetailTransactionActivity;
import com.boardinglabs.mireta.standalone.modul.pickitems.PickItemsActivity;
import com.boardinglabs.mireta.standalone.modul.pickitems.payment.PaymentActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder> {
    private List<PaymentMethodResponse> paymentTypeResponse;
    private Activity context;
    private Dialog dialog;
    private boolean isFromAccount;

    public PaymentMethodAdapter(List<PaymentMethodResponse> paymentMethodResponse, Activity context, boolean isFromAccount) {
        this.paymentTypeResponse = paymentMethodResponse;
        this.context = context;
        this.isFromAccount = isFromAccount;
    }

    @NonNull
    @Override
    public PaymentMethodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_payment_type, parent, false);

        return new PaymentMethodAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull PaymentMethodAdapter.ViewHolder holder, int position) {
        final PaymentMethodResponse accountResponse = paymentTypeResponse.get(position);

        final int id = accountResponse.getId();
        final String accountName = accountResponse.getName();

        holder.button.setText(accountName);
        holder.button.setOnClickListener(v -> {
            if (context instanceof PaymentActivity) {
                if (!isFromAccount) {
                    ((PaymentActivity) context).setPaymentMethod(id);
                    ((PaymentActivity) context).setDialogPaymentAccount(id);
                } else {
                    ((PaymentActivity) context).setPaymentAccount(id);
                    ((PaymentActivity) context).sendToPayment(false);
                }
            }

            if (context instanceof DetailTransactionActivity) {
                if (!isFromAccount) {
                    ((DetailTransactionActivity) context).setPaymentMethod(id);
                    ((DetailTransactionActivity) context).setDialogPaymentAccount(id);
                } else {
                    ((DetailTransactionActivity) context).setPaymentAccount(id);
                    ((DetailTransactionActivity) context).sendToPayment(false);
                }
            }


//            int LAUNCH_SECOND_ACTIVITY = 111;
//            Intent intent = new Intent(context, PaymentActivity.class);
//            intent.putExtra("payment_method_id", id);
//            context.startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
//            context.finish();
        });
    }


    @Override
    public int getItemCount() {
        return paymentTypeResponse.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        ViewHolder(View v) {
            super(v);

            button = v.findViewById(R.id.btnPaymentType);
        }
    }
}
