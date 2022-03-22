package com.boardinglabs.mireta.standalone.modul.productreceipt;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.androidtrip.plugins.searchablespinner.SearchableSpinner;
//import com.androidtrip.plugins.searchablespinner.interfaces.OnItemSelectedListener;
import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.adapter.AddProductReceiptAdapter;
//import com.boardinglabs.mireta.standalone.component.adapter.SimpleListAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.ItemProductReceiptAdapter;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.Item;
import com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt.Request.Detail;
import com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt.Request.RequestProductReceipt;
import com.boardinglabs.mireta.standalone.component.network.entities.Stocks.Location;
import com.boardinglabs.mireta.standalone.component.network.entities.Stocks.StockResponse;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.fastaccess.datetimepicker.DatePickerFragmentDialog;
import com.fastaccess.datetimepicker.callback.DatePickerCallback;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateProductReceipt extends BaseActivity implements DatePickerCallback {

    @BindView(R.id.tvDateReceipt)
    TextView tvDateReceipt;
    @BindView(R.id.spinnerLocation)
    Spinner spinnerLocation;
    @BindView(R.id.rvItem)
    RecyclerView rvItem;
    @BindView(R.id.tvDesc)
    TextView tvDesc;

    private AddProductReceiptAdapter addProductReceiptAdapter;
    private String receiptDate;
    private String locationId;
    private Dialog dialog, dialogItem;
    private ArrayList<String> mStrings = new ArrayList<>();
    private List<Detail> detailList = new ArrayList<>();
    private List<Detail> detailListForAdapter = new ArrayList<>();
    private Detail detailItemForRv;
    private TextView tvItem;
    private ItemProductReceiptAdapter adapter;

    @OnClick(R.id.btnAdd)
    void onClickAdd(){
        setDataItemList();
        showDialogItem();
        EditText etQty = dialog.findViewById(R.id.etQty);
        tvItem = dialog.findViewById(R.id.tvItem);
        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);

        tvItem.setOnClickListener(view -> {
            showListItemDialog();
            EditText etSearchItem = dialogItem.findViewById(R.id.etSearchItem);
            RecyclerView rvListItem = dialogItem.findViewById(R.id.rvListItem);

            rvListItem.setAdapter(adapter);

            etSearchItem.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<Detail> newWorker = new ArrayList<>();
                    String newTextLowerCase = etSearchItem.getText().toString().toLowerCase();
                    for (Detail user : detailList) {
                        if (user.getItemName().toLowerCase().contains(newTextLowerCase)) {
                            newWorker.add(user);
                        }
                    }
                    if (newWorker.size() >= 1){
                        adapter.updateData(newWorker);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        });

        btnSimpan.setOnClickListener(view -> {
            if (etQty.getText().toString().equals("")) {
                Toast.makeText(context, "Quantity harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            tvDesc.setVisibility(View.GONE);
            rvItem.setVisibility(View.VISIBLE);

            detailItemForRv.setQty(etQty.getText().toString());
            detailListForAdapter.add(detailItemForRv);
            addProductReceiptAdapter.updateData(detailListForAdapter);

            dialog.dismiss();
        });
    }

    public void setDetailItem(Detail detail){
        tvItem.setText(detail.getItemName());
        detailItemForRv = detail;
        dialogItem.dismiss();
    }


    @OnClick(R.id.btnTambah)
    void onClickTambah(){
        addProductReceipt();
    }

    @OnClick(R.id.tvDateReceipt)
    void onClickDate(){
        DatePickerFragmentDialog.newInstance().show(getSupportFragmentManager(), "DatePickerFragmentDialog");
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_create_product_receipt;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Tanda Terima Barang");

        context = this;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 0);
        }

        rvItem.setVisibility(View.GONE);
        tvDesc.setVisibility(View.VISIBLE);

        addProductReceiptAdapter = new AddProductReceiptAdapter(detailListForAdapter, context);
        rvItem.setAdapter(addProductReceiptAdapter);
        setSpinnerLocation();
    }

    private void addProductReceipt(){
        RequestProductReceipt requestProductReceipt = new RequestProductReceipt();
        requestProductReceipt.setBusinessId(PreferenceManager.getStockLocation().location.getBusinessId());
        requestProductReceipt.setLocationId(Integer.valueOf(PreferenceManager.getStockLocation().location_id));
        requestProductReceipt.setProductReceiptDate(receiptDate);
        requestProductReceipt.setDetails(detailListForAdapter);

        Loading.show(context);
        ApiLocal.apiInterface().addProductReceipt(requestProductReceipt, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        Toast.makeText(context, "Berhasil menambahkan", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, ProductReceiptActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        String msg = MethodUtil.getErrorResponse(response.errorBody().string());
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void setDataItemList() {
        Loading.show(context);
        detailList.clear();
        ApiLocal.apiInterface().getItemsList(loginStockLocation.location_id, "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<StockResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<StockResponse>>> call, Response<ApiResponse<List<StockResponse>>> response) {
                Loading.hide(context);
                try {
                    List<StockResponse> res = Objects.requireNonNull(response.body()).getData();
                    for (int i = 0; i < res.size(); i++) {
                        StockResponse stockResponse = res.get(i);
                        mStrings.add(stockResponse.getItem().getName());
                        Detail detail = new Detail();
                        detail.setItemId(Integer.valueOf(stockResponse.getItemId()));
                        detail.setItemName(stockResponse.getItem().getName());
                        detail.setLocationId(Integer.valueOf(stockResponse.getLocationId()));
                        detail.setQty(stockResponse.getQty());

                        detailList.add(detail);
                    }

                    Log.d("detailList", new Gson().toJson(detailList));
                    adapter = new ItemProductReceiptAdapter(detailList, CreateProductReceipt.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<StockResponse>>> call, Throwable t) {
                Log.d("TAG OnFailure selected", t.getMessage());
//                swipeRefresh.setRefreshing(false);
                Loading.hide(context);
            }
        });

    }

    private void setSpinnerLocation() {
        Loading.show(context);
        ApiLocal.apiInterface().getLocations(PreferenceManager.getStockLocation().location.getBusiness().id,"Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<Location>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Location>>> call, Response<ApiResponse<List<Location>>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        List<String> locationNameList = new ArrayList<>();
                        locationNameList.add("Pilih Lokasi");
                        List<String> locationIdList = new ArrayList<>();
                        locationIdList.add("0");
                        for (Location location : Objects.requireNonNull(response.body()).getData()){
                            if (PreferenceManager.getStockLocation() != null) {
                                if (PreferenceManager.getStockLocation().location_id.equals(location.getId()+"")) {
                                    locationNameList.add(location.getName());
                                    locationIdList.add(String.valueOf(location.getId()));
                                }
                            }
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_text, locationNameList) {
                            @Override
                            public boolean isEnabled(int position) {
                                return position != 0;
                            }
                        };

                        dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
                        spinnerLocation.setAdapter(dataAdapter);
                        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position != 0) {
                                    locationId = locationIdList.get(position);
                                } else {
                                    locationId = null;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Location>>> call, Throwable t) {
                t.printStackTrace();
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

    @Override
    public void onDateSet(long date) {
        Log.d("date", MethodUtil.getDateOnly(date));
        tvDateReceipt.setText(MethodUtil.getDateOnly(date));
        receiptDate = MethodUtil.getDateOnly(date);
    }

    private void showDialogItem() {
        dialog = new Dialog(Objects.requireNonNull(context));
        //set content
        dialog.setContentView(R.layout.dialog_add_product_receipt_item);
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

    private void showListItemDialog() {
        dialogItem = new Dialog(Objects.requireNonNull(context));
        //set content
        dialogItem.setContentView(R.layout.dialog_list_item);
        dialogItem.setCanceledOnTouchOutside(true);
        dialogItem.setCancelable(true);
        Objects.requireNonNull(dialogItem.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogItem.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogItem.show();
        dialogItem.getWindow().setAttributes(lp);
    }
}