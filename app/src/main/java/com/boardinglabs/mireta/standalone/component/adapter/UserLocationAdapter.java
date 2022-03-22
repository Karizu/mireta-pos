package com.boardinglabs.mireta.standalone.component.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.entities.StockLocation;
import com.boardinglabs.mireta.standalone.modul.akun.AkunActivity;
import com.boardinglabs.mireta.standalone.modul.history.DetailTransactionActivity;
import com.boardinglabs.mireta.standalone.modul.home.HomeActivity;

import java.util.List;

public class UserLocationAdapter extends RecyclerView.Adapter<UserLocationAdapter.ViewHolder> {
    private List<StockLocation> stockLocationList;
    private Activity context;

    public UserLocationAdapter(List<StockLocation> stockLocationList, Activity context) {
        this.stockLocationList = stockLocationList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_open_region, parent, false);

        return new UserLocationAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull UserLocationAdapter.ViewHolder holder, int position) {
        final StockLocation stockLocation = stockLocationList.get(position);
        final String locationName = stockLocation.name;

        holder.button.setText(locationName);
        holder.button.setOnClickListener(v -> {
            if (context instanceof HomeActivity) {
                ((HomeActivity) context).setUserLocation(stockLocation);
            }

            if (context instanceof AkunActivity) {
                ((AkunActivity) context).setUserLocation(stockLocation);
            }
        });
    }


    @Override
    public int getItemCount() {
        return stockLocationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        ViewHolder(View v) {
            super(v);

            button = v.findViewById(R.id.btnRegion);
        }
    }
}
