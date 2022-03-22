package com.boardinglabs.mireta.standalone.modul.productreceipt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.adapter.DetailProductReceiptAdapter;
import com.boardinglabs.mireta.standalone.component.network.entities.ProductReceipt.ProductReceiptResponse;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailProductReceiptActivity extends BaseActivity {

    @BindView(R.id.tvOrderDate)
    TextView tvOrderDate;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvDateReceipt)
    TextView tvDateReceipt;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @OnClick(R.id.btnPrint)
    void onClickPrint(){

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_detail_product_receipt;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Tanda Terima Barang");

        context = this;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        String json = getIntent().getStringExtra("json");
        ProductReceiptResponse productReceiptResponse = new Gson().fromJson(json, ProductReceiptResponse.class);
        populateData(productReceiptResponse);
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

    private void populateData(ProductReceiptResponse productReceiptResponse) {
        tvOrderDate.setText(productReceiptResponse.getCreatedAt());
        tvLocation.setText(productReceiptResponse.getLocation().getName());
        tvDateReceipt.setText(productReceiptResponse.getProductReceiptDate());

        DetailProductReceiptAdapter adapter = new DetailProductReceiptAdapter(productReceiptResponse.getDetails(), DetailProductReceiptActivity.this);
        recyclerView.setAdapter(adapter);
    }
}