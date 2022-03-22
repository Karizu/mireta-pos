package com.boardinglabs.mireta.standalone.modul.akun;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.adapter.UserLocationAdapter;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.StockLocation;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.akun.pengaturan.PengaturanAkunActivity;
import com.boardinglabs.mireta.standalone.modul.akun.rfid.RfidActivity;
import com.boardinglabs.mireta.standalone.modul.ardi.HomeArdiActivity;
import com.boardinglabs.mireta.standalone.modul.home.HomeActivity;
import com.boardinglabs.mireta.standalone.modul.master.categories.CategoryActivity;
import com.boardinglabs.mireta.standalone.modul.master.katalog.KatalogActivity;
import com.boardinglabs.mireta.standalone.modul.master.profil.toko.ProfilTokoActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.StokActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.TambahBarangActivity;
import com.boardinglabs.mireta.standalone.modul.transactions.items.voids.VoidActivity;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AkunActivity extends BaseActivity implements KeyEvent.Callback {

    private String codeHolder = "";
    private Dialog dialog;
    private Context context;

    @BindView(R.id.btnVoidArdi)
    CardView btnVoidArdi;
    @BindView(R.id.switchOfflineMode)
    Switch switchOfflineMode;
    @BindView(R.id.btnOpenLocation)
    CardView btnOpenLocation;

    @OnClick(R.id.btnStock)
    void onClickStock() {
        Intent intent = new Intent(AkunActivity.this, StokActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnPengaturanAkun)
    void onClickbtnUbahAkun() {
        startActivity(new Intent(AkunActivity.this, PengaturanAkunActivity.class));
    }

    @OnClick(R.id.btnProfilToko)
    void onClickbtnProfilToko() {
        startActivity(new Intent(AkunActivity.this, ProfilTokoActivity.class));
    }

    @OnClick(R.id.btnKatalog)
    void onClickbtnKatalog() {
        startActivity(new Intent(AkunActivity.this, KatalogActivity.class));
    }

    @OnClick(R.id.btnKategori)
    void onClickbtnKategori() {
        startActivity(new Intent(AkunActivity.this, CategoryActivity.class));
    }

    @OnClick(R.id.btnTambahBarang)
    void onClickbtnTambahBarang() {
        startActivity(new Intent(AkunActivity.this, TambahBarangActivity.class));
    }

    @OnClick(R.id.btnVoidArdi)
    void onClickbtnVoidArdi() {
        startActivity(new Intent(AkunActivity.this, VoidActivity.class));
    }

    @OnClick(R.id.btnTestRFID)
    void onClickTestRFID() {
        startActivity(new Intent(AkunActivity.this, RfidActivity.class));
    }


    @OnClick(R.id.btnOpenLocation)
    void onClickOpenLocation() {
        openLocation();
    }

    @OnClick(R.id.btnKeluar)
    void onClickbtnKeluar(){
        goToLoginPage();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_akun;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Pengaturan");
        context = this;


        if (PreferenceManager.getRole()!=null) {
            if (PreferenceManager.getRole().getName().toLowerCase().contains("owner")){
                btnOpenLocation.setVisibility(View.VISIBLE);
            }
        }

        switchOfflineMode.setVisibility(View.GONE);
        boolean isOfflineMode = PreferenceManager.isOfflineMode();
        if (isOfflineMode) {
            switchOfflineMode.setChecked(true);
        } else {
            switchOfflineMode.setChecked(false);
        }

        switchOfflineMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                PreferenceManager.setIsOfflineMode(true);
            } else {
                PreferenceManager.setIsOfflineMode(false);
            }
        });
    }

    @Override
    protected void onCreateAtChild() {

    }

    @Override
    protected void onBackBtnPressed() {
        onBackPressed();
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    private void openLocation() {
        Loading.show(context);
        ApiLocal.apiInterface().getUserLocation(PreferenceManager.getBusiness().id, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<StockLocation>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<StockLocation>>> call, Response<ApiResponse<List<StockLocation>>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        List<StockLocation> stockLocationList = response.body().getData();
                        showDialogLayout(R.layout.dialog_open_region);
                        RecyclerView recyclerView = dialog.findViewById(R.id.rvOpenRegion);

                        UserLocationAdapter adapter = new UserLocationAdapter(stockLocationList, AkunActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        String msg = MethodUtil.getErrorResponse(response.errorBody().string());
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<StockLocation>>> call, Throwable t) {
                t.printStackTrace();
                Loading.hide(context);
            }
        });
    }

    public void setUserLocation(StockLocation stockLocation) {
        stockLocation.location_id = stockLocation.id;
        PreferenceManager.saveStockLocation(stockLocation);
        dialog.dismiss();
        Intent intent = new Intent(AkunActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void showDialogLayout(int layout) {
        dialog = new Dialog(Objects.requireNonNull(AkunActivity.this));
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.i("KEY", "Code : " + keyCode + ", Event : " + event);
        if (keyCode == 232) {
            return false;
        }
        if (codeHolder.length() >= 6) {
            codeHolder = "";
        }
        if (keyCode == 56) {
            codeHolder = "";
        }
        if (keyCode == 67) {
            if (codeHolder.length() > 0) {
                codeHolder = codeHolder.substring(0, codeHolder.length() - 1);
            }
        }
        if (keyCode > 6 && keyCode < 17) {
            codeHolder += String.valueOf(keyCode - 7);
        }
        if (codeHolder.equals("147258")) {
            btnVoidArdi.setVisibility(View.VISIBLE);
        }
        if (codeHolder.equals("8888")) {
            btnVoidArdi.setVisibility(View.GONE);
        }
        if (codeHolder.equals("258369")) {
            startActivity(new Intent(AkunActivity.this, HomeArdiActivity.class));
        }
        if (keyCode == 7) {
            Log.d("CD", codeHolder);
        }
        return super.onKeyDown(keyCode, event);
    }
}
