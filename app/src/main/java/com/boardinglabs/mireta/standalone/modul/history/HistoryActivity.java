package com.boardinglabs.mireta.standalone.modul.history;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.adapter.TransactionAdapter;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoRegularTextView;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionModel;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.Transactions;
import com.boardinglabs.mireta.standalone.component.network.entities.User;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.network.response.LoginResponse;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.component.util.Scanner;
import com.fastaccess.datetimepicker.DatePickerFragmentDialog;
import com.fastaccess.datetimepicker.callback.DatePickerCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends BaseActivity implements DatePickerCallback {

    private static final int PENDING = 1;
    private RecyclerView recyclerView;
    private List<TransactionModel> transactionModels;
    private TransactionAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private long mTotal;
    private Dialog dialog;
    final int REQUEST_CODE = 564;
    final int REQUEST_SCANNER = 999;
    private boolean isFromCloseStore = false;
    private boolean isFromHome = false;
    private boolean isFromReport = false;
    private String pathAll, pathHistory, pathStatus, pathSettle, dateToday, dateMonth, dateYear;
    private Context context;
    private String filterStartDate = "";

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
    @BindView(R.id.btnSettlement)
    LinearLayout btnSettlement;
    @BindView(R.id.btnTambah)
    LinearLayout btnTambah;
    private CheckedTextView ctvDate;

    @OnClick(R.id.btnSettlement)
    void onClickSettlement(){
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

    @OnClick(R.id.imgScan)
    void onClickImgScan() {
        item_name.setVisibility(View.GONE);
        imgSearch.setVisibility(View.GONE);
        laySearch.setVisibility(View.VISIBLE);

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(HistoryActivity.this), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(HistoryActivity.this), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        } else {
            Intent intent = new Intent(HistoryActivity.this, Scanner.class);
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
                    List<TransactionModel> newWorker = new ArrayList<>();
                    String newTextLowerCase = etSearch.getText().toString().toLowerCase();
                    for (TransactionModel user : transactionModels) {
                        if (user.getOrder_no().toLowerCase().contains(newTextLowerCase)) {
                            newWorker.add(user);
                        }
                    }
                    if (newWorker.size() >= 1) {
                        tvNoData.setVisibility(View.GONE);
                        adapter.updateData(newWorker);
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                        adapter.updateData(newWorker);
                    }
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
        return R.layout.activity_history;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("History");
        imgFilter.setVisibility(View.VISIBLE);
        imgScan.setVisibility(View.VISIBLE);
        transactionModels = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        refreshLayout = findViewById(R.id.pullToRefresh);
        context = HistoryActivity.this;


        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        dateToday = s.format(new Date());
        Log.d("DATE TODAY", dateToday);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s2 = new SimpleDateFormat("MM");
        dateMonth = s2.format(new Date());
        Log.d("DATE MONTH", dateMonth);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s3 = new SimpleDateFormat("yyyy");
        dateYear = s3.format(new Date());
        Log.d("DATE YEAR", dateYear);

        isFromReport = getIntent().getBooleanExtra("isFromReport", false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent().getStringExtra("tag") != null) {
            refreshLayout.setOnRefreshListener(() -> {
                mTotal = 0;
                adapter.notifyDataSetChanged();
                getListHistory();
            });
            getListHistory();
        } else {
            isFromCloseStore = getIntent().getBooleanExtra("isFromCloseStore", false);
            isFromHome = getIntent().getBooleanExtra("isFromHome", false);

            if (isFromCloseStore) {
                btnSettlement.setVisibility(View.VISIBLE);
                btnTambah.setVisibility(View.GONE);
                refreshLayout.setOnRefreshListener(() -> {
                    mTotal = 0;
                    transactionModels.clear();
                    adapter.notifyDataSetChanged();
                    getListHistoryOperation();
                });

                getListHistoryOperation();
                item_name.setText("History Transaksi Saya");
                return;
            }

            if (isFromHome) {
                btnSettlement.setVisibility(View.GONE);
                refreshLayout.setOnRefreshListener(() -> {
                    mTotal = 0;
                    transactionModels.clear();
                    adapter.notifyDataSetChanged();
                    getListHistoryPending();
                });

                getListHistoryPending();
                item_name.setText("Pesanan Tertunda");
                return;
            }

            refreshLayout.setOnRefreshListener(() -> {
                mTotal = 0;
                adapter.notifyDataSetChanged();
                getListHistoryOperation();
            });

            item_name.setText("History Transaksi Saya");
            getListHistoryOperation();
        }
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
                List<TransactionModel> newWorker = new ArrayList<>();
                String newTextLowerCase = resultData.toLowerCase();
                for (TransactionModel user : transactionModels) {
                    if (user.getOrder_no().toLowerCase().contains(newTextLowerCase)) {
                        newWorker.add(user);
                    }
                }
                if (newWorker.size() >= 1) {
                    tvNoData.setVisibility(View.GONE);
                    adapter.updateData(newWorker);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    adapter.updateData(newWorker);
                }

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
//        CheckedTextView ctvMonth = dialog.findViewById(R.id.ctvMonth);
        CheckedTextView ctvMy = dialog.findViewById(R.id.ctvMy);
        ctvDate = dialog.findViewById(R.id.ctvDate);
        CheckedTextView ctvBulanIni = dialog.findViewById(R.id.ctvBulanIni);
        CheckedTextView ctvStatusPending = dialog.findViewById(R.id.ctvStatusPending);
        CheckedTextView ctvStatusBatal = dialog.findViewById(R.id.ctvStatusBatal);
        CheckedTextView ctvSettleAll = dialog.findViewById(R.id.ctvSettleAll);
        CheckedTextView ctvSettleTrue = dialog.findViewById(R.id.ctvSettleTrue);
        CheckedTextView ctvSettleFalse = dialog.findViewById(R.id.ctvSettleFalse);

        ctvAll.setOnClickListener(v -> {
            ctvAll.setChecked(true);
            ctvSemuaTransaksi.setChecked(false);
//            ctvMonth.setChecked(false);
            ctvMy.setChecked(false);
            pathHistory = "";
        });

        ctvSemuaTransaksi.setOnClickListener(v -> {
            ctvAll.setChecked(false);
            ctvSemuaTransaksi.setChecked(true);
//            ctvMonth.setChecked(false);
            ctvMy.setChecked(false);
            pathHistory = "";
        });

//        ctvMonth.setOnClickListener(v -> {
//            ctvAll.setChecked(false);
//            ctvToday.setChecked(false);
//            ctvMonth.setChecked(true);
//            ctvMy.setChecked(true);
//            pathHistory = "month=" + dateMonth + "&year=" + dateYear + "&";
//        });

        ctvMy.setOnClickListener(v -> {
            ctvAll.setChecked(false);
            ctvSemuaTransaksi.setChecked(false);
            ctvMy.setChecked(true);
            pathHistory = "location_operation_id=" + PreferenceManager.getOperationData().getId();
        });

        ctvDate.setOnClickListener(v -> {
            DatePickerFragmentDialog.newInstance().show(getSupportFragmentManager(), "DatePickerFragmentDialog");
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
            pathAll = "transactions?" + pathHistory + pathStatus + pathSettle;
            Log.d("PATH", pathAll);
            mTotal = 0;
            transactionModels.clear();
            adapter.notifyDataSetChanged();
            getHistoryByFilter(pathAll);

            if (ctvMy.isChecked()) {
                item_name.setText("Histori Transaksi Saya");
                if (ctvDate.isChecked()){
                    item_name.setText("Histori Transaksi Saya (" + filterStartDate + ")");
                }
            }

            if (ctvSemuaTransaksi.isChecked()) {
                item_name.setText("Histori Transaksi");
                if (ctvDate.isChecked()){
                    item_name.setText("Histori Transaksi (" + filterStartDate + ")");
                }
            }

            refreshLayout.setOnRefreshListener(() -> {
                mTotal = 0;
                transactionModels.clear();
                adapter.notifyDataSetChanged();
                getHistoryByFilter(pathAll);
            });
        });

        Button btnResetFilter = dialog.findViewById(R.id.btnResetFilter);
        btnResetFilter.setOnClickListener(v -> {
            dialog.dismiss();
            mTotal = 0;
            transactionModels.clear();
            adapter.notifyDataSetChanged();
            item_name.setText("History Transaksi Saya");
            getListHistoryOperation();
        });

//        LinearLayout btnAllHistory = dialog.findViewById(R.id.history_all);
//        btnAllHistory.setOnClickListener(v -> {
//            mTotal = 0;
//            transactionModels.clear();
//            getListHistory();
//
//            refreshLayout.setOnRefreshListener(() -> {
//                mTotal = 0;
//                transactionModels.clear();
//                getListHistory();
//            });
//            item_name.setText("History Transaksi");
//            dialog.dismiss();
//        });
//
//        LinearLayout btnTodayHistory = dialog.findViewById(R.id.history_today);
//        btnTodayHistory.setOnClickListener(v -> {
//            mTotal = 0;
//            transactionModels.clear();
//            getListHistoryToday();
//            refreshLayout.setOnRefreshListener(() -> {
//                mTotal = 0;
//                transactionModels.clear();
//                getListHistoryToday();
//            });
//            item_name.setText("History Transaksi Hari ini");
//            dialog.dismiss();
//        });

//        LinearLayout btnBerhasil = dialog.findViewById(R.id.trxBerhasil);
//        btnBerhasil.setOnClickListener(v -> {
//            refreshLayout.setRefreshing(true);
//            List<TransactionModel> newWorker = new ArrayList<>();
//            String newTextLowerCase = "2";
//            for (TransactionModel user : transactionModels) {
//                if (user.getStatus().toLowerCase().contains(newTextLowerCase)) {
//                    newWorker.add(user);
//                }
//            }
//            if (newWorker.size() >= 1){
//                refreshLayout.setRefreshing(false);
//                adapter.updateData(newWorker);
//                dialog.dismiss();
//            }
//        });
//
//        LinearLayout btnPending = dialog.findViewById(R.id.trxPending);
//        btnPending.setOnClickListener(v -> {
//            refreshLayout.setRefreshing(true);
//            List<TransactionModel> newWorker = new ArrayList<>();
//            String newTextLowerCase = "1";
//            for (TransactionModel user : transactionModels) {
//                if (user.getStatus().toLowerCase().contains(newTextLowerCase)) {
//                    newWorker.add(user);
//                }
//            }
//            if (newWorker.size() >= 1){
//                refreshLayout.setRefreshing(false);
//                adapter.updateData(newWorker);
//                dialog.dismiss();
//            }
//        });
    }

    private void getHistoryByFilter(String path) {
        refreshLayout.setRefreshing(true);
        ApiLocal.apiInterface().getHistoryWithFilter(path, loginStockLocation.location_id, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<Transactions>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    List<Transactions> res = Objects.requireNonNull(response.body()).getData();
                    if (res.size() < 1) {
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < res.size(); i++) {
                        Transactions transaction = res.get(i);
                        transactionModels.add(new TransactionModel(transaction.getId() + "",
                                transaction.getLocationId(),
                                transaction.getId() + "",
                                transaction.getTransactionCode(),
                                transaction.getCreatedAt(),
                                transaction.getPaymentType() + "",
                                transaction.getPaymentMethod(), transaction.getTransactionTypeObj(),
                                transaction.getTotalPrice(),
                                transaction.getTotalDiscount(),
                                transaction.getStatus() + ""));

                        mTotal += Long.parseLong(transaction.getTotalPrice());

                    }

                    Log.d("mTotalDay", mTotal + "");

                    tvTotalPenjualan.setText("Rp. " + MethodUtil.toCurrencyFormat(String.valueOf(mTotal)));

                    adapter = new TransactionAdapter(transactionModels, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    private void getListHistoryToday() {
        refreshLayout.setRefreshing(true);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = s1.format(new Date());
        ApiLocal.apiInterface().getHistoryToday(loginStockLocation.location_id, null, format1, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<Transactions>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    List<Transactions> res = Objects.requireNonNull(response.body()).getData();
                    if (res.size() < 1) {
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < res.size(); i++) {
                        Transactions transaction = res.get(i);
                        transactionModels.add(new TransactionModel(transaction.getId() + "",
                                transaction.getLocationId(),
                                transaction.getId() + "",
                                transaction.getTransactionCode(),
                                transaction.getCreatedAt(),
                                transaction.getPaymentType() + "",
                                transaction.getPaymentMethod(), transaction.getTransactionTypeObj(),
                                transaction.getTotalPrice(),
                                transaction.getTotalDiscount(),
                                transaction.getStatus() + ""));

                        if (transaction.getStatus() == 2) {
                            mTotal += Long.parseLong(transaction.getTotalPrice());
                        }
                    }

                    Log.d("mTotalDay", mTotal + "");

                    tvTotalPenjualan.setText("Rp. " + MethodUtil.toCurrencyFormat(String.valueOf(mTotal)));

                    adapter = new TransactionAdapter(transactionModels, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                Log.d("TAG", t.getMessage());
            }
        });
    }

    private void getListHistory() {
        refreshLayout.setRefreshing(true);
        ApiLocal.apiInterface().getHistory(loginStockLocation.location_id, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<Transactions>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    List<Transactions> res = Objects.requireNonNull(response.body()).getData();
                    if (res.size() < 1) {
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < res.size(); i++) {
                        Transactions transaction = res.get(i);
                        transactionModels.add(new TransactionModel(transaction.getId() + "",
                                transaction.getLocationId(),
                                transaction.getId() + "",
                                transaction.getTransactionCode(),
                                transaction.getCreatedAt(),
                                transaction.getPaymentType() + "",
                                transaction.getPaymentMethod(), transaction.getTransactionTypeObj(),
                                transaction.getTotalPrice(),
                                transaction.getTotalDiscount(),
                                transaction.getStatus() + ""));

                        if (transaction.getStatus().equals(2)) {
                            mTotal += Long.parseLong(transaction.getTotalPrice());
                        }
                    }

                    Log.d("mTotal", mTotal + "");

                    tvTotalPenjualan.setText("Rp. " + MethodUtil.toCurrencyFormat(String.valueOf(mTotal)));

                    adapter = new TransactionAdapter(transactionModels, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
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
                    btnOK.setOnClickListener(v -> {
                        if (isFromCloseStore){
                            dialog.dismiss();
                            onBackPressed();
                        }
                    });
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

    private void getListHistoryOperation() {
        refreshLayout.setRefreshing(true);
        transactionModels.clear();
        ApiLocal.apiInterface().getHistoryOperation(loginStockLocation.location_id, PreferenceManager.getOperationData().getId()+"","Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<Transactions>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    List<Transactions> res = Objects.requireNonNull(response.body()).getData();
                    if (res.size() < 1) {
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < res.size(); i++) {
                        Transactions transaction = res.get(i);
                        transactionModels.add(new TransactionModel(transaction.getId() + "",
                                transaction.getLocationId(),
                                transaction.getId() + "",
                                transaction.getTransactionCode(),
                                transaction.getCreatedAt(),
                                transaction.getPaymentType() + "",
                                transaction.getPaymentMethod(), transaction.getTransactionTypeObj(),
                                transaction.getTotalPrice(),
                                transaction.getTotalDiscount(),
                                transaction.getStatus() + ""));

                        if (transaction.getStatus().equals(2)) {
                            mTotal += Long.parseLong(transaction.getTotalPrice());
                        }
                    }

                    Log.d("mTotal", mTotal + "");

                    tvTotalPenjualan.setText("Rp. " + MethodUtil.toCurrencyFormat(String.valueOf(mTotal)));

                    adapter = new TransactionAdapter(transactionModels, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    private void getListHistoryPending() {
        refreshLayout.setRefreshing(true);
        transactionModels.clear();
        ApiLocal.apiInterface().getHistoryOperation(loginStockLocation.location_id, PreferenceManager.getOperationData().getId()+"", PENDING+"","Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<Transactions>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    List<Transactions> res = Objects.requireNonNull(response.body()).getData();
                    if (res.size() < 1) {
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < res.size(); i++) {
                        Transactions transaction = res.get(i);
                        transactionModels.add(new TransactionModel(transaction.getId() + "",
                                transaction.getLocationId(),
                                transaction.getId() + "",
                                transaction.getTransactionCode(),
                                transaction.getCreatedAt(),
                                transaction.getPaymentType() + "",
                                transaction.getPaymentMethod(), transaction.getTransactionTypeObj(),
                                transaction.getTotalPrice(),
                                transaction.getTotalDiscount(),
                                transaction.getStatus() + ""));

                        if (transaction.getStatus().equals(2)) {
                            mTotal += Long.parseLong(transaction.getTotalPrice());
                        }
                    }

                    Log.d("mTotal", mTotal + "");

                    tvTotalPenjualan.setText("Rp. " + MethodUtil.toCurrencyFormat(String.valueOf(mTotal)));

                    adapter = new TransactionAdapter(transactionModels, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(HistoryActivity.this));
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

    private void showDialogLayout(int layout) {
        dialog = new Dialog(Objects.requireNonNull(HistoryActivity.this));
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

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onDateSet(long date) {
        Log.d("date", MethodUtil.getDateOnly(date));

        ctvDate.setChecked(true);
        pathStatus = "&date="+MethodUtil.getDateOnly(date);
        ctvDate.setText(MethodUtil.getDateOnly(date));
        filterStartDate = new SimpleDateFormat("dd MMM yy").format(date);
    }
}
