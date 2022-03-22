package com.boardinglabs.mireta.standalone.modul.damageditem;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.DamagedStocks.DamagedStocks;
import com.boardinglabs.mireta.standalone.component.network.entities.Stocks.StockResponse;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.master.brand.model.CategoryModel;
import com.boardinglabs.mireta.standalone.modul.master.katalog.KatalogActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.model.ItemResponse;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.model.KatalogModel;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DamagedItemAdapter extends RecyclerView.Adapter<DamagedItemAdapter.ViewHolder> {
    private List<DamagedStocks> transactionModels;
    private Context context;
    private Dialog dialog;

    public DamagedItemAdapter(List<DamagedStocks> transactionModels, Context context){
        this.transactionModels = transactionModels;
        this.context = context;
    }

    @NonNull
    @Override
    public DamagedItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_restock, parent, false);

        return new DamagedItemAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull DamagedItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position){
        try {
            final DamagedStocks transactionModel = transactionModels.get(position);
            final String id = String.valueOf(transactionModel.getId());
            final String name = transactionModel.getItem().getName();
            final String description = transactionModel.getDescription();
            final String date = transactionModel.getCreatedAt();
            String qty = transactionModel.getQty()!=1? transactionModel.getQty() + " Pcs" : transactionModel.getQty() + " Pc";
            String image = transactionModel.getImage();
            image = image.replace("index.php/", "");
            final String harga = transactionModel.getTotalPrice();

            Date d = null;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                d = sdf.parse(date);
            } catch (ParseException ex) {
                Log.v("Exception", ex.getLocalizedMessage());
            }

            sdf.applyPattern("dd-MM-yyyy HH:mm");

            holder.textViewStok.setText("Rp " + MethodUtil.toCurrencyFormat(harga));
            holder.tvDate.setText(sdf.format(d));
            holder.tvName.setText(name);
            holder.tvDescription.setText(description);
            holder.tvHarga.setText("Rp "+ MethodUtil.toCurrencyFormat(harga) + "");
            Glide.with(context).load(image)
                    .placeholder(R.drawable.resto_default).fitCenter().dontAnimate()
                    .into(holder.imgBarang);

            holder.imgButton.setVisibility(View.GONE);
            holder.qtyInput.setText(qty);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount(){
        return transactionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDescription;
        TextView tvHarga;
        ImageView imgBarang;
        LinearLayout layout, footer;
        ImageButton imgButton;
        TextView qtyInput;
        TextView tvDate, textViewStok;
        FrameLayout frameLayout;

        ViewHolder(View v){
            super(v);

            tvName = v.findViewById(R.id.tvName);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvHarga = v.findViewById(R.id.tvHarga);
            textViewStok = v.findViewById(R.id.textViewStok);
            imgButton = v.findViewById(R.id.add_button);
            tvDate = v.findViewById(R.id.tvDate);
            qtyInput = v.findViewById(R.id.qty_input);
            imgBarang = v.findViewById(R.id.imgBarang);
            layout = v.findViewById(R.id.layoutKatalog);
            footer = v.findViewById(R.id.footer);
            frameLayout = v.findViewById(R.id.user_feed_row_bottom);
        }
    }

    public void updateData(List<DamagedStocks> newUser){
        transactionModels = new ArrayList<>();
        transactionModels.addAll(newUser);
        notifyDataSetChanged();
    }
}
