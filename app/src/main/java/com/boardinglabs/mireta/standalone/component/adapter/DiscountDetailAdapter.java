package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import com.boardinglabs.mireta.standalone.component.network.entities.Expenditure.ExpenditureResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.DiscountResponse;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscountDetailAdapter extends RecyclerView.Adapter<DiscountDetailAdapter.ViewHolder> {
    private List<DiscountResponse> expenditureResponses;
    private Context context;
    private Dialog dialog;

    public DiscountDetailAdapter(List<DiscountResponse> expenditureResponses, Context context){
        this.expenditureResponses = expenditureResponses;
        this.context = context;
    }

    @NonNull
    @Override
    public DiscountDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_discount_detail, parent, false);

        return new DiscountDetailAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull DiscountDetailAdapter.ViewHolder holder, int position){
        try {
            DiscountResponse expenditureResponse = expenditureResponses.get(position);
            final String name = expenditureResponse.getDiscountDescription();
            final String nominal = expenditureResponse.getTotalDiscount();

            holder.tvDescription.setText(name);
            holder.tvDiscountPrice.setText("-Rp " + MethodUtil.toCurrencyFormat(nominal));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount(){
        return expenditureResponses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvDiscountPrice;

        ViewHolder(View v){
            super(v);

            tvDescription = v.findViewById(R.id.tvDiscountDesc);
            tvDiscountPrice = v.findViewById(R.id.tvDiscountPrice);
        }
    }
}
