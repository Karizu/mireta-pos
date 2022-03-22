package com.boardinglabs.mireta.standalone.modul.operasional;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.User;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.network.response.LoginResponse;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.akun.pengaturan.PengaturanAkunActivity;
import com.boardinglabs.mireta.standalone.modul.akun.rfid.RfidActivity;
import com.boardinglabs.mireta.standalone.modul.ardi.HomeArdiActivity;
import com.boardinglabs.mireta.standalone.modul.damageditem.DamagedItemActivity;
import com.boardinglabs.mireta.standalone.modul.expenditure.ExpenditureActivity;
import com.boardinglabs.mireta.standalone.modul.master.categories.CategoryActivity;
import com.boardinglabs.mireta.standalone.modul.master.katalog.KatalogActivity;
import com.boardinglabs.mireta.standalone.modul.master.laporan.LaporanActivity;
import com.boardinglabs.mireta.standalone.modul.master.profil.toko.ProfilTokoActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.StokActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.TambahBarangActivity;
import com.boardinglabs.mireta.standalone.modul.productreceipt.ProductReceiptActivity;
import com.boardinglabs.mireta.standalone.modul.transactions.items.voids.VoidActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OperasionalActivity extends BaseActivity implements KeyEvent.Callback {

    private String codeHolder = "";
    private Dialog dialog;
    private Context context;

    @OnClick(R.id.btnExpenditure)
    void onClickExpenditure() {
        Intent intent = new Intent(OperasionalActivity.this, ExpenditureActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnDamagedItem)
    void onClickbtnDamagedItem() {
        startActivity(new Intent(OperasionalActivity.this, DamagedItemActivity.class));
    }

    @OnClick(R.id.btnProductReceipt)
    void onClickProduckReceipt(){
        startActivity(new Intent(OperasionalActivity.this, ProductReceiptActivity.class));
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.btnSettlement)
    void onClickbtnSettlement() {
        showDialogLayout(R.layout.layout_input_password);
        TextView tvTitleBarang = dialog.findViewById(R.id.tvTitleBarang);
        tvTitleBarang.setText("Settlement");
        TextView etMasterKey = dialog.findViewById(R.id.etMasterKey);
        etMasterKey.setText("Silahkan masukan password akun anda");
        EditText etPassword = dialog.findViewById(R.id.etPassword);
        Button btnProses = dialog.findViewById(R.id.btnProses);
        btnProses.setOnClickListener(v -> {
            if (!etPassword.getText().toString().equals("")) {
                dialog.dismiss();
                checkPass(etPassword.getText().toString());
            } else {
                dialog.dismiss();
                Toast.makeText(context, "Silahkan isi password akun anda", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_operasional;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Operasional");
        context = this;

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

    private void doSettlement() {
        Loading.show(context);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("location_id", loginStockLocation.location_id)
                .build();

        ApiLocal.apiInterface().doSettlement(requestBody, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                if (response.isSuccessful()) {
                    Log.d("TAG", "MASUK SETTLE");
                    showDialogLayout(R.layout.layout_wrong_pass);
                    TextView tvTitleBarang = dialog.findViewById(R.id.tvTitleBarang);
                    TextView etMasterKey = dialog.findViewById(R.id.etMasterKey);
                    Button btnOK = dialog.findViewById(R.id.btnOK);
                    tvTitleBarang.setText("SETTLEMENT BERHASIL");
                    tvTitleBarang.setTextColor(getResources().getColor(R.color.green_ligth));
                    etMasterKey.setText("Berhasil melakukan settlemnet");
                    btnOK.setOnClickListener(v -> dialog.dismiss());
                } else {
                    Toast.makeText(context, "Tidak ada transaksi yang harus di settlement", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void checkPass(String pass) {
        Loading.show(context);
        User user = PreferenceManager.getUser();
        ApiLocal.apiInterface().checkPass(user.username, pass).enqueue(new Callback<LoginResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        doSettlement();
                    } else {
                        showDialogLayout(R.layout.layout_wrong_pass);
                        TextView etMasterKey = dialog.findViewById(R.id.etMasterKey);
                        etMasterKey.setText("Silahkan periksa kembali password yang anda masukan");
                        Button btnOk = dialog.findViewById(R.id.btnOK);
                        btnOk.setOnClickListener(v1 -> dialog.dismiss());
                    }
                } catch (Exception e){
                    Toast.makeText(context, "Terjadi kesalahan pada sistem", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
                Toast.makeText(context, "Terjadi kesalahan pada sistem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogLayout(int layout) {
        dialog = new Dialog(Objects.requireNonNull(OperasionalActivity.this));
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
