package com.boardinglabs.mireta.standalone.modul.master.profil.toko;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.Locations.DetailLocationResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.StockLocation;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.akun.AkunActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilTokoActivity extends BaseActivity {

    private Context context;

    @BindView(R.id.etNamaToko)
    EditText etNamaToko;
    @BindView(R.id.etAlamatToko)
    EditText etAlamatToko;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etNoTelp)
    EditText etNoTelp;

    @OnClick(R.id.btnSimpan)
    void onClick(){
        if (etNoTelp.getText().toString().startsWith("08") || etNoTelp.getText().toString().startsWith("02")){
            doSaveProfil();
            return;
        }

        Toast.makeText(context, "Masukkan nomor telepon dengan benar", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profil_toko;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("PROFIL TOKO");
        context = this;
        getDetailProfil();

    }

    private void getDetailProfil(){
        Loading.show(context);
        ApiLocal.apiInterface().getDetailLocation(loginStockLocation.location_id, "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<DetailLocationResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DetailLocationResponse>> call, Response<ApiResponse<DetailLocationResponse>> response) {
                Loading.hide(context);
                try {
                    Log.d("on", "on");
                        DetailLocationResponse res = Objects.requireNonNull(response.body()).getData();
                        etNamaToko.setText(res.getName());
                        etAlamatToko.setText(res.getAddress());
                        etEmail.setText(res.getEmail());
                        etNoTelp.setText(res.getPhone());

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DetailLocationResponse>> call, Throwable t) {
                t.printStackTrace();
                Loading.hide(context);
            }
        });
    }

    private void doSaveProfil(){
        Loading.show(context);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", etNamaToko.getText().toString())
                .addFormDataPart("business_id", loginBusiness.id)
                .addFormDataPart("brand_id", loginStockLocation.brand_id)
                .addFormDataPart("email", etEmail.getText().toString())
                .addFormDataPart("phone", etNoTelp.getText().toString())
                .addFormDataPart("address", etAlamatToko.getText().toString())
                .build();

        ApiLocal.apiInterface().updateProfilToko(loginStockLocation.location_id, requestBody, "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        StockLocation stockLocation = PreferenceManager.getStockLocation();
                        stockLocation.name = etNamaToko.getText().toString();
                        stockLocation.address = etAlamatToko.getText().toString();
                        stockLocation.telp = etNoTelp.getText().toString();
                        PreferenceManager.saveStockLocation(stockLocation);

                        Toast.makeText(context, "Berhasil update profil", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, AkunActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
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

    private void getDetailTenant(){

    }
}
