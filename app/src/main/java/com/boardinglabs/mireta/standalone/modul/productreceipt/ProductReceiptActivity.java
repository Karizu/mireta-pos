package com.boardinglabs.mireta.standalone.modul.productreceipt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.adapter.ProductReceiptAdapter;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoRegularTextView;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt.ProductReceiptResponse;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductReceiptActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.imgFilter)
    ImageView imgFilter;
    @BindView(R.id.imgScan)
    ImageView imgScan;
    @BindView(R.id.item_name)
    RobotoRegularTextView item_name;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout refreshLayout;

    private String startDate, endDate;

    @OnClick(R.id.btnTambah)
    void onClickAdd(){
        Intent intent = new Intent(ProductReceiptActivity.this, CreateProductReceipt.class);
        startActivity(intent);
    }

    @OnClick(R.id.imgFilter)
    void onClickImgFilter(){
        @SuppressLint("SetTextI18n") SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                (view, yearStart, monthStart, dayStart, yearEnd, monthEnd, dayEnd) -> {
                    // grab the date range, do what you want
                    monthStart+=1;
                    monthEnd+=1;

                    String sY = yearStart + "";
                    String sM = String.valueOf(monthStart).length() > 1 ? String.valueOf(monthStart) : "0" + monthStart;
                    String sD = String.valueOf(dayStart).length() > 1 ? String.valueOf(dayStart) : "0" + dayStart;
                    startDate = sY + "-" + sM + "-" + sD;
                    String eY = yearEnd + "";
                    String eM = String.valueOf(monthEnd).length() > 1 ? String.valueOf(monthEnd) : "0" + monthEnd;
                    String eD = String.valueOf(dayEnd).length() > 1 ? String.valueOf(dayEnd) : "0" + dayEnd;
                    endDate = eY + "-" + eM + "-" + eD;

                    item_name.setText("Tanda Terima (" + startDate + " - " + endDate + ")");

                    getProductReceipt();
                });
        smoothDateRangePickerFragment.setAccentColor(getResources().getColor(R.color.colorAccent));
        smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_product_receipt;
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);

        setToolbarTitle("TANDA TERIMA BARANG");
        item_name.setText("Tanda Terima Hari Ini");
        imgFilter.setVisibility(View.VISIBLE);
        imgScan.setVisibility(View.GONE);

        startDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        endDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        refreshLayout.setOnRefreshListener(this::getProductReceipt);
        getProductReceipt();
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

    private void getProductReceipt() {
        Loading.show(ProductReceiptActivity.this);
        ApiLocal.apiInterface().getProductReceipt(PreferenceManager.getStockLocation().location_id, startDate, endDate, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<ProductReceiptResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductReceiptResponse>>> call, Response<ApiResponse<List<ProductReceiptResponse>>> response) {
                try {
                    Loading.hide(ProductReceiptActivity.this);
                    if (response.isSuccessful()) {
                        List<ProductReceiptResponse> productReceiptResponseList = Objects.requireNonNull(response.body()).getData();
                        if (productReceiptResponseList.size()>0){
                            recyclerView.setVisibility(View.VISIBLE);
                            tvNoData.setVisibility(View.GONE);
                            ProductReceiptAdapter adapter = new ProductReceiptAdapter(productReceiptResponseList, ProductReceiptActivity.this);
                            recyclerView.setAdapter(adapter);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductReceiptResponse>>> call, Throwable t) {
                t.printStackTrace();
                Loading.hide(ProductReceiptActivity.this);
            }
        });
    }
}