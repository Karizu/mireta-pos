package com.boardinglabs.mireta.standalone.modul.expenditure;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.adapter.ExpenditureAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.TransactionAdapter;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoRegularTextView;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.Expenditure.ExpenditureResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionModel;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.Transactions;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.component.util.Scanner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenditureActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private List<ExpenditureResponse> transactionModels;
    private ExpenditureAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private long mTotal;
    private Dialog dialog;
    final int REQUEST_CODE = 564;
    final int REQUEST_SCANNER = 999;
    private boolean isFromCloseStore = false;
    private Context context;
    private String pathAll, pathHistory, pathStatus, pathSettle, dateToday, dateMonth, dateYear;

    @BindView(R.id.item_name)
    RobotoRegularTextView item_name;
    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.tvTotalPenjualan)
    TextView tvTotalPenjualan;
    @BindView(R.id.imgFilter)
    ImageView imgFilter;
    @BindView(R.id.imgScan)
    ImageView imgScan;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.laySearch)
    LinearLayout laySearch;
    @BindView(R.id.etSearch)
    EditText etSearch;

    @OnClick(R.id.btnTambah)
    void onClickBtnTambah(){
        Intent intent = new Intent(context, CreateExpenditureActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.imgScan)
    void onClickImgScan() {
        item_name.setVisibility(View.GONE);
        imgSearch.setVisibility(View.GONE);
        laySearch.setVisibility(View.VISIBLE);

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(ExpenditureActivity.this), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(ExpenditureActivity.this), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        } else {
            Intent intent = new Intent(ExpenditureActivity.this, Scanner.class);
            startActivityForResult(intent, REQUEST_SCANNER);
        }
    }

    @OnClick(R.id.imgClose)
    void onClickClosed() {
        etSearch.setText("");
        item_name.setVisibility(View.VISIBLE);
        imgSearch.setVisibility(View.VISIBLE);
        laySearch.setVisibility(View.GONE);
    }

    @OnClick(R.id.imgSearch)
    void onClickSearchs() {
        item_name.setVisibility(View.GONE);
        imgSearch.setVisibility(View.GONE);
        laySearch.setVisibility(View.VISIBLE);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    List<ExpenditureResponse> newWorker = new ArrayList<>();
                    String newTextLowerCase = etSearch.getText().toString().toLowerCase();
                    for (ExpenditureResponse user : transactionModels) {
                        if (user.getName().toLowerCase().contains(newTextLowerCase)) {
                            newWorker.add(user);
                        }
                    }
                    if (newWorker.size() >= 1) {
                        tvNoData.setVisibility(View.GONE);
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                    adapter.updateData(newWorker);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_expenditure;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("PENGELUARAN");
        imgFilter.setVisibility(View.VISIBLE);
        imgScan.setVisibility(View.GONE);
        transactionModels = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        refreshLayout = findViewById(R.id.pullToRefresh);
        context = ExpenditureActivity.this;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        dateToday = s.format(new Date());
        Log.d("DATE TODAY", dateToday);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s2 = new SimpleDateFormat("MM");
        dateMonth = s2.format(new Date());
        Log.d("DATE MONTH", dateMonth);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s3 = new SimpleDateFormat("yyyy");
        dateYear = s3.format(new Date());
        Log.d("DATE YEAR", dateYear);

        refreshLayout.setOnRefreshListener(() -> {
            mTotal = 0;
            transactionModels.clear();
            adapter.notifyDataSetChanged();
            getListExpenditure();
        });

        item_name.setText("Histori Pengeluaran Saya");
        getListExpenditure();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCANNER && resultCode == Activity.RESULT_OK) {
            String resultData = data.getStringExtra("scan_data");
            Log.d("Data Scan", resultData);
            etSearch.setText(resultData);
            try {
                List<ExpenditureResponse> newWorker = new ArrayList<>();
                String newTextLowerCase = resultData.toLowerCase();
                for (ExpenditureResponse user : transactionModels) {
                    if (user.getName().toLowerCase().contains(newTextLowerCase)) {
                        newWorker.add(user);
                    }
                }
                if (newWorker.size() >= 1) {
                    tvNoData.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                }
                adapter.updateData(newWorker);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.imgFilter)
    void onClickImgFilter() {
        showDialog();

        pathHistory = ""; pathStatus = ""; pathSettle = "";

        CheckedTextView ctvAll = dialog.findViewById(R.id.ctvAll);
        CheckedTextView ctvSemuaTransaksi = dialog.findViewById(R.id.ctvSemuaTransaksi);
        CheckedTextView ctvMonth = dialog.findViewById(R.id.ctvMy);
        CheckedTextView ctvDate = dialog.findViewById(R.id.ctvDate);
        CheckedTextView ctvBulanIni = dialog.findViewById(R.id.ctvBulanIni);
        CheckedTextView ctvStatusPending = dialog.findViewById(R.id.ctvStatusPending);
        CheckedTextView ctvStatusBatal = dialog.findViewById(R.id.ctvStatusBatal);
        CheckedTextView ctvSettleAll = dialog.findViewById(R.id.ctvSettleAll);
        CheckedTextView ctvSettleTrue = dialog.findViewById(R.id.ctvSettleTrue);
        CheckedTextView ctvSettleFalse = dialog.findViewById(R.id.ctvSettleFalse);

        ctvSemuaTransaksi.setText("Pengeluaran Hari ini");
        ctvMonth.setText("Pengeluaran Saya");

        ctvAll.setOnClickListener(v -> {
            ctvAll.setChecked(true);
            ctvSemuaTransaksi.setChecked(false);
            ctvMonth.setChecked(false);
            pathHistory = "";
        });

        ctvSemuaTransaksi.setOnClickListener(v -> {
            ctvAll.setChecked(false);
            ctvSemuaTransaksi.setChecked(true);
            ctvMonth.setChecked(false);
            pathHistory = "date=" + dateToday + "&";
        });

        ctvMonth.setOnClickListener(v -> {
            ctvAll.setChecked(false);
            ctvSemuaTransaksi.setChecked(false);
            ctvMonth.setChecked(true);
            pathHistory = "month=" + dateMonth + "&year=" + dateYear + "&";
        });

        ctvDate.setOnClickListener(v -> {
            ctvDate.setChecked(true);
            ctvBulanIni.setChecked(false);
            ctvStatusPending.setChecked(false);
            ctvStatusBatal.setChecked(false);
            pathStatus = "";
        });

        ctvBulanIni.setOnClickListener(v -> {
            ctvDate.setChecked(false);
            ctvBulanIni.setChecked(true);
            ctvStatusPending.setChecked(false);
            ctvStatusBatal.setChecked(false);
            pathStatus = "status=2&";
        });

        ctvStatusPending.setOnClickListener(v -> {
            ctvDate.setChecked(false);
            ctvBulanIni.setChecked(false);
            ctvStatusPending.setChecked(true);
            ctvStatusBatal.setChecked(false);
            pathStatus = "status=1&";
        });

        ctvStatusBatal.setOnClickListener(v -> {
            ctvDate.setChecked(false);
            ctvBulanIni.setChecked(false);
            ctvStatusPending.setChecked(false);
            ctvStatusBatal.setChecked(true);
            pathStatus = "status=3&";
        });

        ctvSettleAll.setOnClickListener(v -> {
            ctvSettleAll.setChecked(true);
            ctvSettleTrue.setChecked(false);
            ctvSettleFalse.setChecked(false);
            pathSettle = "";
        });

        ctvSettleTrue.setOnClickListener(v -> {
            ctvSettleAll.setChecked(false);
            ctvSettleTrue.setChecked(true);
            ctvSettleFalse.setChecked(false);
            pathSettle = "is_settled=1&";
        });

        ctvSettleFalse.setOnClickListener(v -> {
            ctvSettleAll.setChecked(false);
            ctvSettleTrue.setChecked(false);
            ctvSettleFalse.setChecked(true);
            pathSettle = "is_settled=0&";
        });

        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(v -> {
            dialog.dismiss();

            if (ctvMonth.isChecked()) {
                item_name.setText("Histori Pengeluaran Saya");
                getListExpenditure();
                refreshLayout.setOnRefreshListener(this::getListExpenditure);
            } else {
                item_name.setText("Histori Pengeluaran Hari ini");
                getListExpenditureToday();
                refreshLayout.setOnRefreshListener(this::getListExpenditureToday);
            }

        });

        Button btnResetFilter = dialog.findViewById(R.id.btnResetFilter);
        btnResetFilter.setOnClickListener(v -> {
            dialog.dismiss();

            item_name.setText("Histori Pengeluaran Saya");
            getListExpenditure();
        });
    }

    private void getListExpenditure() {
        refreshLayout.setRefreshing(true);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = s1.format(new Date());
        ApiLocal.apiInterface().getExpenditure(loginStockLocation.location_id, PreferenceManager.getOperationData().getId() + "", "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<ExpenditureResponse>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<ExpenditureResponse>>> call, Response<ApiResponse<List<ExpenditureResponse>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    if (response.isSuccessful()){
                        List<ExpenditureResponse> res = Objects.requireNonNull(response.body()).getData();
                        if (res.size() < 1) {
                            tvNoData.setVisibility(View.VISIBLE);
                        } else {
                            tvNoData.setVisibility(View.GONE);
                        }

                        transactionModels = res;

                        adapter = new ExpenditureAdapter(res, context);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                layoutManager.getOrientation());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(context, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ExpenditureResponse>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                Log.d("TAG", t.getMessage());
            }
        });
    }

    private void getListExpenditureToday() {
        refreshLayout.setRefreshing(true);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = s1.format(new Date());
        ApiLocal.apiInterface().getExpenditure(loginStockLocation.location_id, PreferenceManager.getOperationData().getId() + "", format1, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<ExpenditureResponse>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<ExpenditureResponse>>> call, Response<ApiResponse<List<ExpenditureResponse>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    if (response.isSuccessful()){
                        List<ExpenditureResponse> res = Objects.requireNonNull(response.body()).getData();
                        if (res.size() < 1) {
                            tvNoData.setVisibility(View.VISIBLE);
                        } else {
                            tvNoData.setVisibility(View.GONE);
                        }

                        transactionModels = res;

                        adapter = new ExpenditureAdapter(res, context);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                layoutManager.getOrientation());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(context, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ExpenditureResponse>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                Log.d("TAG", t.getMessage());
            }
        });
    }

    private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(ExpenditureActivity.this));
        //set content
        dialog.setContentView(R.layout.layout_filter_history);
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