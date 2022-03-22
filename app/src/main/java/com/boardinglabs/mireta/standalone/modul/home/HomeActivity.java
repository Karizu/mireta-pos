package com.boardinglabs.mireta.standalone.modul.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.BluetoothHandler;
import com.boardinglabs.mireta.standalone.component.DeviceActivity;
import com.boardinglabs.mireta.standalone.component.PrinterCommands;
import com.boardinglabs.mireta.standalone.component.adapter.PaymentTypeAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.RecapStockAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.RecyReportSummaryAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.RecyTransactionAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.TransactionAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.UserLocationAdapter;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoBoldTextView;
import com.boardinglabs.mireta.standalone.component.listener.ListActionListener;
import com.boardinglabs.mireta.standalone.component.network.Api;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.NetworkManager;
import com.boardinglabs.mireta.standalone.component.network.NetworkService;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.CheckMemberResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.Members;
import com.boardinglabs.mireta.standalone.component.network.entities.Business;
import com.boardinglabs.mireta.standalone.component.network.entities.InquiryStatus.InquiryStatusOperations;
import com.boardinglabs.mireta.standalone.component.network.entities.InquiryStatus.StockReport;
import com.boardinglabs.mireta.standalone.component.network.entities.Locations.DetailLocationResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.OpenStore;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentTypeResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.SecurityQuestions;
import com.boardinglabs.mireta.standalone.component.network.entities.StockLocation;
import com.boardinglabs.mireta.standalone.component.network.entities.SummaryReport;
import com.boardinglabs.mireta.standalone.component.network.entities.Transaction;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionModel;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.User;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.network.response.LoginResponse;
import com.boardinglabs.mireta.standalone.component.util.Constant;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.component.util.Utils;
import com.boardinglabs.mireta.standalone.modul.CommonInterface;
import com.boardinglabs.mireta.standalone.modul.akun.AkunActivity;
import com.boardinglabs.mireta.standalone.modul.akun.rfid.TapCard;
import com.boardinglabs.mireta.standalone.modul.ardi.SaldoActivity;
import com.boardinglabs.mireta.standalone.modul.ardi.freemeal.FreeMeal;
import com.boardinglabs.mireta.standalone.modul.ardi.registermember.RegisterMember;
import com.boardinglabs.mireta.standalone.modul.history.DetailTransactionActivity;
import com.boardinglabs.mireta.standalone.modul.history.HistoryActivity;
import com.boardinglabs.mireta.standalone.modul.master.laporan.LaporanActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.StokActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.adapter.StokAdapter;
import com.boardinglabs.mireta.standalone.modul.operasional.OperasionalActivity;
import com.bumptech.glide.Glide;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.paging.listview.PagingListView;
import com.zj.btsdk.BluetoothService;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements HomeView, CommonInterface, RecyReportSummaryAdapter.OnClickItem, ListActionListener, BluetoothHandler.HandlerInterface {
    private HomePresenter mPresenter;

    protected TextView greetingName;
    protected TextView greetingWords;
    protected Button createTrxButton;
    protected Button historyButton;
    protected Button accountButton;
    protected Button masterButton;
    protected LinearLayout btnStock, setting, report;
    protected LinearLayout layoutTrx;
    protected ImageView restocks;

    protected RecyclerView reportRecyclerView;
    private RecyReportSummaryAdapter mAdapter;

    private List<Transaction> transactions;
    private PagingListView transactionsListView;
    private RecyTransactionAdapter transactionsAdapter;
    private List<TransactionModel> transactionModels;
    private TransactionAdapter adapter;
    private int currentPage;
    private int total_post;
    private ArrayList<SummaryReport> summaryList;
    long sum = 0;
    long sumMonth = 0;
    int amount, amountMonth;
    long rest, restMonth;
    private Dialog dialog;
    private int flag;
    private String UID = "";
    private long nomBayar;
    private Context context;
    private List<String> questionList, questionIdList;
    private String spinnerQuestionId = "";
    private Spinner spinner;
    private boolean isOpen = false;
    private String transactionType;
    private boolean isFromLogin = false;

    TextView tvTransactionSum;
    TextView tvExpenditureTotal;
    TextView tvKasAwal;
    TextView tvGrandTotal;
    TextView tvSummaryGopay;
    TextView tvSummaryOvo;
    TextView tvSummaryQris;
    TextView tvSummaryTransfer;
    ImageView imgLogo;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.tvRecyEmpty)
    TextView recyclerViewEmpty;
    @BindView(R.id.greeting_name)
    RobotoBoldTextView greeting_name;
    @BindView(R.id.img_store_status)
    ImageView img_store_status;
    @BindView(R.id.tv_store_status)
    TextView tv_store_status;
    @BindView(R.id.btn_on_off_store)
    Button btn_on_off_store;
    @BindView(R.id.tv_petty_cash)
    TextView tv_petty_cash;
    @BindView(R.id.tv_expenditure)
    TextView tv_expenditure;
    @BindView(R.id.tvShowRekap)
    TextView tvShowRekap;
    @BindView(R.id.btnOperasional)
    LinearLayout btnPengeluaran;

    AlertDialog alertTaps;
    LayoutInflater li;
    @SuppressLint("InflateParams")
    TapCard promptsView;
    AlertDialog.Builder alertDialogBuilder;

    private String str, manufacturer, model;
    private Handler handler = new Handler();

    private Runnable myRunnable = () -> {
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
    };
    private PrinterDevice printerDevice;
    private Format format;

    @SuppressLint("HandlerLeak")
    public Handler handlerCekSaldo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            UID = bundle.getString("uid");
            Log.d("UID", UID);
            checkMember(UID, "");
            alertTaps.dismiss();
            promptsView.searchEnd();
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handlerRegisterMember = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            UID = bundle.getString("uid");
            Log.d("UID", UID);
            checkMember(UID, "register");
            alertTaps.dismiss();
            promptsView.searchEnd();
        }
    };

    private String greeting;
    private InquiryStatusOperations statusOperations;

    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;
    private boolean isPrinterReady = false;
    private BluetoothDevice mDevice;
    private BluetoothService mService = null;
    private boolean isButtonPrintClicked = false;
    private boolean isButtonProsesClicked = false;

    @AfterPermissionGranted(RC_BLUETOOTH)
    private void setupBluetooth() {
        String[] params = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
        if (!EasyPermissions.hasPermissions(this, params)) {
            EasyPermissions.requestPermissions(this, "You need bluetooth permission",
                    RC_BLUETOOTH, params);
            return;
        }
        mService = new BluetoothService(this, new BluetoothHandler(HomeActivity.this));
    }

    @OnClick(R.id.btnHistory)
    void onClickHistory(){
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putExtra("isFromHome", true);
        startActivity(intent);
    }

    @OnClick(R.id.tvShowRekap)
    void onClickShowRekap(){
        showRecap();
    }

    private String current = "";

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.btn_on_off_store)
    void onClickOnOffStore(){
        if (btn_on_off_store.getText().toString().equals("Tutup Toko")){
            showRecap();
        } else {
            showDialogLayout(R.layout.layout_open_store);
            Button btnProses = dialog.findViewById(R.id.btnProses);
            Button btnRecap = dialog.findViewById(R.id.btnRecap);
            CheckBox cbDesc = dialog.findViewById(R.id.etMasterKey);

            cbDesc.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (cbDesc.isChecked()) {
                    btnProses.setEnabled(true);
                    btnProses.setBackground(getResources().getDrawable(R.drawable.background_button));
                } else {
                    btnProses.setEnabled(false);
                    btnProses.setBackground(getResources().getDrawable(R.drawable.gray_button_background));
                }
            });

            btnProses.setOnClickListener(v -> {
                dialog.dismiss();
                doOpenStore("");
            });

            btnRecap.setOnClickListener(v -> {
                dialog.dismiss();
                showRecap();
            });
        }
    }

    private void reportAdjustmentStock(String id, String qty, String reason) {
        Loading.show(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("qty", qty)
                .addFormDataPart("location_operation_id", PreferenceManager.getOperationData().getId()+"")
                .addFormDataPart("description", reason)
                .build();

        ApiLocal.apiInterface().reportAdjustmentStock(id, requestBody, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        // save data
                        dialog.dismiss();
                        getInquiryStatusOperations(false);
                        Toast.makeText(context, "Laporan berhasil diproses", Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = MethodUtil.getErrorResponse(response.errorBody().string());
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                Loading.hide(context);
            }
        });
    }

    private void reportAdjustmentCash(String nominal, String reason) {
        Loading.show(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("adjusted_closing_cash", nominal)
                .addFormDataPart("adjusted_by", "")
                .addFormDataPart("adjusted_reason", reason)
                .build();

        ApiLocal.apiInterface().reportAdjustmentCash(PreferenceManager.getOperationData().getId()+"",requestBody, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        // save data
                        dialog.dismiss();
//                        showRecap();
                        Toast.makeText(context, "Laporan berhasil diproses", Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = MethodUtil.getErrorResponse(response.errorBody().string());
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                Loading.hide(context);
            }
        });
    }

    private void doOpenStore(String initialCash) {
        Loading.show(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("location_id", PreferenceManager.getStockLocation().location_id)
                .addFormDataPart("initial_cash", "0")
                .build();

        ApiLocal.apiInterface().openStore(requestBody, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<OpenStore>>() {
            @Override
            public void onResponse(Call<ApiResponse<OpenStore>> call, Response<ApiResponse<OpenStore>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        // save data
                        getInquiryStatusOperations(false);
                    } else {
                        String msg = MethodUtil.getErrorResponse(response.errorBody().string());
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OpenStore>> call, Throwable t) {
                t.printStackTrace();
                Loading.hide(context);
            }
        });
    }

    private void doCloseStore() {
        Loading.show(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("location_id", PreferenceManager.getStockLocation().location_id)
                .build();

        String location_operation_id = PreferenceManager.getOperationData().getId() + "";

        ApiLocal.apiInterface().closeStore(location_operation_id, requestBody, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<OpenStore>>() {
            @Override
            public void onResponse(Call<ApiResponse<OpenStore>> call, Response<ApiResponse<OpenStore>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        // save data
                        dialog.dismiss();
                        getInquiryStatusOperations(true);
                        Toast.makeText(context, "Berhasil menutup toko", Toast.LENGTH_SHORT).show();
                    } else {
                        if (mService != null) {
                            mService.stop();
                        }
                        mService = new BluetoothService(context, new BluetoothHandler(HomeActivity.this));
                        Toast.makeText(context, MethodUtil.getErrorResponse(Objects.requireNonNull(response.errorBody()).string()), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to parse response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OpenStore>> call, Throwable t) {
                t.printStackTrace();
                Loading.hide(context);
            }
        });
    }

    @OnClick(R.id.btnOperasional)
    void onClickBtnOperasional() {
        Intent intent = new Intent(context, OperasionalActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.cekSaldo)
    void onClickCekSaldos() {
        li = LayoutInflater.from(context);
        promptsView = (TapCard) li.inflate(R.layout.tap_card, null);

        alertDialogBuilder = new AlertDialog.Builder(context);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertTaps = alertDialogBuilder.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(alertTaps.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        try {
            promptsView.init(handlerCekSaldo);
            promptsView.searchEnd();
            promptsView.searchBegin();
            promptsView.setOkListener(v1 -> {
                alertTaps.dismiss();
                promptsView.searchEnd();
                ((Activity) context).finish();
            });

            alertTaps.show();
            alertTaps.getWindow().setAttributes(lp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new HomePresenter(this, this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);

        context = HomeActivity.this;

        if (PreferenceManager.getStockLocation() == null) {
            openLocation();
            return;
        }

        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;

        isFromLogin = getIntent().getBooleanExtra("isFromLogin", false);

        printerDevice = (PrinterDevice) POSTerminal.getInstance(getApplicationContext()).getDevice(
                "cloudpos.device.printer");

//        loadTransactionsData();
        initComponent();
        getDetailProfil();
        getInquiryStatusOperations(false);

        pullToRefresh.setOnRefreshListener(() -> {
//            transactionModels.clear();
            getDetailProfil();
//            loadTransactionsData();
            summaryList.clear();
            sum = 0;
            sumMonth = 0;
            getInquiryStatusOperations(false);
            pullToRefresh.setRefreshing(false);
        });

        setupBluetooth();

        if (isFromLogin) showRecapFromLogin();
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
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        RecyclerView recyclerView = dialog.findViewById(R.id.rvOpenRegion);

                        UserLocationAdapter adapter = new UserLocationAdapter(stockLocationList, HomeActivity.this);
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

    @SuppressLint("SetTextI18n")
    private void getInquiryStatusOperations(boolean isFromCloseStore) {
        pullToRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getInqStatusOperations(loginStockLocation.location_id, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<InquiryStatusOperations>>() {
            @Override
            public void onResponse(Call<ApiResponse<InquiryStatusOperations>> call, Response<ApiResponse<InquiryStatusOperations>> response) {
                pullToRefresh.setRefreshing(false);
                try {
                    if (response.isSuccessful()){
                        statusOperations = response.body().getData();
                        PreferenceManager.setOperationData(statusOperations);
                        boolean isOpen = statusOperations.getIsOpen();

                        String initCash = statusOperations.getCurrentCash()<0? "-Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentCash()+"") : "Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentCash()+"");
                        String operator = statusOperations.getOpenedByUser()!=null?statusOperations.getOpenedByUser().getFullname():"-";
                        String currentTrxSumByUser = statusOperations.getCurrentCashTrxSum();
                        String todaytrxSum = statusOperations.getCurrentTrxSum();
                        String expenditureTotal = statusOperations.getCurrent_expense_sum();

                        getPenjualanPendapatan(currentTrxSumByUser, todaytrxSum);
                        setStoreStatusComp(isOpen);

                        tv_petty_cash.setText(initCash);
                        greetingWords.setText(greeting + " " + PreferenceManager.getUser().fullname);
                        tv_expenditure.setText("Rp " + MethodUtil.toCurrencyFormat(expenditureTotal));

                        if (isFromCloseStore) {
                            printBtn();
                            goToLoginPage();
                        }

                    } else {
                        Toast.makeText(context, "Failed to parse response", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<InquiryStatusOperations>> call, Throwable t) {
                pullToRefresh.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showRecapFromLogin() {
        pullToRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getInqStatusOperations(loginStockLocation.location_id, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<InquiryStatusOperations>>() {
            @Override
            public void onResponse(Call<ApiResponse<InquiryStatusOperations>> call, Response<ApiResponse<InquiryStatusOperations>> response) {
                pullToRefresh.setRefreshing(false);
                try {
                    if (response.isSuccessful()){
                        isFromLogin = false;
                        statusOperations = response.body().getData();
                        PreferenceManager.setOperationData(statusOperations);
                        boolean isOpen = statusOperations.getIsOpen();
                        String initCash = statusOperations.getCurrentCash()<0? "-Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentCash()+"") : "Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentCash()+"");
                        String operator = statusOperations.getOpenedByUser()!=null?statusOperations.getOpenedByUser().getFullname():"-";
                        String currentTrxSumByUser = statusOperations.getCurrentCashTrxSum();
                        String todaytrxSum = statusOperations.getCurrentTrxSum();
                        String expenditureTotal = statusOperations.getCurrent_expense_sum();

                        getPenjualanPendapatan(currentTrxSumByUser, todaytrxSum);
                        setStoreStatusComp(isOpen);

                        tv_petty_cash.setText(initCash);
                        greetingWords.setText(greeting + " " + PreferenceManager.getUser().fullname);
                        tv_expenditure.setText("Rp " + MethodUtil.toCurrencyFormat(expenditureTotal));

                        showDialogLayout(R.layout.layout_close_store);
                        Button btnPrint = dialog.findViewById(R.id.btnPrint);
                        Button btnProses = dialog.findViewById(R.id.btnProses);
                        Button btnListTransaction = dialog.findViewById(R.id.btnListTransaction);
                        TextView tvLocationName = dialog.findViewById(R.id.tvLocationName);
                        TextView tvLocationAddress = dialog.findViewById(R.id.tvLocationAddress);
                        Button btnAdjustment = dialog.findViewById(R.id.btnAdjustment);
                        LinearLayout layoutAdjustment = dialog.findViewById(R.id.layoutAdjustment);
                        TextView tvAdjustmentCash = dialog.findViewById(R.id.tvAdjustmentCash);
                        TextView tvTitleBarang = dialog.findViewById(R.id.tvTitleBarang);

                        tvTitleBarang.setText("Rekapitulasi Sebelumnya");

                        imgLogo = dialog.findViewById(R.id.imgLogo);
                        tvTransactionSum = dialog.findViewById(R.id.tvTransactionSum);
                        tvExpenditureTotal = dialog.findViewById(R.id.tvExpenditureTotal);
                        tvKasAwal = dialog.findViewById(R.id.tvKasAwal);
                        tvGrandTotal = dialog.findViewById(R.id.tvGrandTotal);

                        RecyclerView rvStockItem = dialog.findViewById(R.id.rvStockItem);
                        TextView tvOperator = dialog.findViewById(R.id.tvOperator);
                        TextView tvOpenDate = dialog.findViewById(R.id.tvOpenDate);
                        TextView tvClosedDate = dialog.findViewById(R.id.tvClosedDate);

                        tvSummaryGopay = dialog.findViewById(R.id.tvSummaryGopay);
                        tvSummaryOvo = dialog.findViewById(R.id.tvSummaryOvo);
                        tvSummaryQris = dialog.findViewById(R.id.tvSummaryQris);
                        tvSummaryTransfer = dialog.findViewById(R.id.tvSummaryTransfer);

                        RecapStockAdapter adapter = new RecapStockAdapter(statusOperations.getStocks(), context, isOpen);
                        rvStockItem.setAdapter(adapter);

                        String imageLogo = "";
                        try { imageLogo = NetworkService.BASE_URL_IMAGE + loginStockLocation.location.getBrand().getLogo_image_url();} catch (Exception e){}
                        String locationName = statusOperations.getLocation().getName();
                        String locationAddress = statusOperations.getLocation().getAddress();
                        int trxSum = Integer.parseInt(statusOperations.getCurrentCashTrxSum());
                        int expenditureTotaIlnt = Integer.parseInt(statusOperations.getCurrent_expense_sum());
                        int grandTotal = statusOperations.getCurrentCash();
                        int summaryGopay = statusOperations.getCurrentGopayTrxSum();
                        int summaryOvo = statusOperations.getCurrentOvoTrxSum();
                        int summaryQris = Integer.parseInt(statusOperations.getCurrentQrisTrxSum());
                        int summaryTransfer = statusOperations.getCurrentBankTransferTrxSum();
                        String adjustedClosingCash = statusOperations.getAdjustedClosingCash()!=null?statusOperations.getAdjustedClosingCash():"0";
                        String operatorString = statusOperations.getOpenedByUser()!=null?statusOperations.getOpenedByUser().getFullname(): "-";

                        String jamTutup = "Jam Tutup: -";
                        String jamBuka = "";
                        try {
                            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id","ID"));

                            Date dateOpen = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id")).parse(statusOperations.getOpenedAt());
                            jamBuka = df.format(dateOpen);

                            if (statusOperations.getClosedAt()!=null) {
                                Date dateClosed = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id")).parse(statusOperations.getClosedAt());
                                jamTutup = "Jam Tutup: " + df.format(dateClosed);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (adjustedClosingCash.equals("0")) {
                            layoutAdjustment.setVisibility(View.GONE);
                        } else {
                            layoutAdjustment.setVisibility(View.VISIBLE);
                            int adjusmentTotal = grandTotal - Integer.parseInt(adjustedClosingCash);
                            tvAdjustmentCash.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(adjusmentTotal)) + "");
                        }

                        tvOpenDate.setText("Jam Buka: " + jamBuka);
                        tvClosedDate.setText(jamTutup);
                        imgLogo.destroyDrawingCache();
                        imgLogo.setDrawingCacheEnabled(true);
                        Glide.with(context)
                                .load(imageLogo)
                                .placeholder(R.drawable.pd_logo_black_white)
                                .into(imgLogo);

                        tvLocationName.setText(locationName);
                        tvLocationAddress.setText(locationAddress);
                        tvTransactionSum.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(trxSum)));
                        tvExpenditureTotal.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(expenditureTotaIlnt)));
                        tvGrandTotal.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(grandTotal)));

                        tvSummaryGopay.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(summaryGopay)));
                        tvSummaryOvo.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(summaryOvo)));
                        tvSummaryQris.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(summaryQris)));
                        tvSummaryTransfer.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(summaryTransfer)));

                        tvOperator.setText("Operator: " + operatorString);

                        btnAdjustment.setOnClickListener(v -> {
                            showAdjustmentDialog(false, 0);
                        });

                        if (statusOperations.getIsOpen()) {
                            btnAdjustment.setVisibility(View.VISIBLE);
                            btnPrint.setVisibility(View.GONE);
                            btnListTransaction.setOnClickListener(v -> {
                                Intent intent = new Intent(context, HistoryActivity.class);
                                intent.putExtra("isFromCloseStore", true);
                                startActivity(intent);
                            });

                            btnProses.setOnClickListener(v -> {
                                isButtonProsesClicked = true;
                                printBtn();
                            });
                        } else {
                            btnAdjustment.setVisibility(View.GONE);
                            btnProses.setVisibility(View.GONE);
                            btnListTransaction.setVisibility(View.GONE);
                            btnPrint.setVisibility(View.VISIBLE);
                        }

                        btnPrint.setOnClickListener(v -> {
                            isButtonPrintClicked = true;
                            printBtn();
                        });

                    } else {
                        Toast.makeText(context, "Failed to parse response", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<InquiryStatusOperations>> call, Throwable t) {
                pullToRefresh.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    private void setStoreStatusComp(boolean isOpen) {
        if (isOpen){
            pullToRefresh.setRefreshing(false);
            img_store_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
            tv_store_status.setText("Buka");

            btn_on_off_store.setBackground(getResources().getDrawable(R.drawable.bg_round_button_off));
            btn_on_off_store.setTextColor(getResources().getColor(R.color.mireta_red));
            btn_on_off_store.setText("Tutup Toko");

            tvShowRekap.setVisibility(View.GONE);
            btnPengeluaran.setEnabled(true);
            btnPengeluaran.setBackground(getResources().getDrawable(R.drawable.border_round_gradient_8dp));
            btnStock.setEnabled(true);
            btnStock.setBackground(getResources().getDrawable(R.drawable.border_round_gradient_8dp));
            layoutTrx.setEnabled(true);
            layoutTrx.setBackground(getResources().getDrawable(R.drawable.background_gradient));

            if (PreferenceManager.getRole()!=null) {
                if (PreferenceManager.getRole().getName().toLowerCase().contains("owner")){
                    disabledMenu();
                }
            }

        } else {

            img_store_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_closed));
            tv_store_status.setText("Tutup");

            btn_on_off_store.setBackground(getResources().getDrawable(R.drawable.bg_round_button_on));
            btn_on_off_store.setTextColor(getResources().getColor(R.color.mireta_blue));
            btn_on_off_store.setText("Buka Toko");

            tvShowRekap.setVisibility(View.VISIBLE);
            btnPengeluaran.setEnabled(false);
            btnPengeluaran.setBackground(getResources().getDrawable(R.drawable.gray_button_background));
//            btnStock.setEnabled(false);
//            btnStock.setBackground(getResources().getDrawable(R.drawable.gray_button_background));
            layoutTrx.setEnabled(false);
            layoutTrx.setBackground(getResources().getDrawable(R.drawable.gray_button_background));

            if (PreferenceManager.getRole()!=null) {
                if (PreferenceManager.getRole().getName().toLowerCase().contains("owner")){
                    disabledMenu();
                }
            }
        }
    }

    private void disabledMenu(){
        btnPengeluaran.setEnabled(false);
        btnPengeluaran.setBackground(getResources().getDrawable(R.drawable.gray_button_background));
//            btnStock.setEnabled(false);
//            btnStock.setBackground(getResources().getDrawable(R.drawable.gray_button_background));
        layoutTrx.setEnabled(false);
        layoutTrx.setBackground(getResources().getDrawable(R.drawable.gray_button_background));
        btnStock.setEnabled(false);
        btnStock.setBackground(getResources().getDrawable(R.drawable.gray_button_background));
        btn_on_off_store.setEnabled(false);
    }

    @Override
    protected void onCreateAtChild() {
        mPresenter = new HomePresenter(this, this);

    }

    @Override
    protected void onBackBtnPressed() {
//        alertTaps.dismiss();
//        promptsView.searchEnd();
        onBackPressed();
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    @SuppressLint("SetTextI18n")
    private void initComponent() {
        mPresenter = new HomePresenter(this, this);
        greetingWords = (TextView) findViewById(R.id.greeting_words);

        btnStock = findViewById(R.id.btnStock);
        report = findViewById(R.id.report);
        setting = findViewById(R.id.setting);
        layoutTrx = findViewById(R.id.layoutTrx);
        restocks = findViewById(R.id.restocks);

        RxView.clicks(layoutTrx).subscribe(aVoid -> {
            showDialogLayout(R.layout.dialog_transaction_type);
            RecyclerView rvPaymentType = dialog.findViewById(R.id.rvPaymentType);
            setRecyclerPeymentType(rvPaymentType);
        });

        RxView.clicks(btnStock).subscribe(aVoid -> setComponentDialog());

        RxView.clicks(report).subscribe(aVoid -> {
            Intent intent = new Intent(HomeActivity.this, LaporanActivity.class);
            startActivity(intent);

        });

        RxView.clicks(setting).subscribe(aVoid -> {
            Intent intent = new Intent(HomeActivity.this, AkunActivity.class);
            startActivity(intent);

        });

        //Get the time of day
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= 10 && hour < 14) {
            greeting = "Selamat Siang";
        } else if (hour >= 14 && hour < 19) {
            greeting = "Selamat Sore";
        } else if (hour >= 19) {
            greeting = "Selamat Malam";
        } else {
            greeting = "Selamat Pagi";
        }

        mAdapter = new RecyReportSummaryAdapter(this);
        reportRecyclerView = (RecyclerView) findViewById(R.id.report_recyclerView);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                horizontalLayoutManagaer.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        reportRecyclerView.setLayoutManager(horizontalLayoutManagaer);
        reportRecyclerView.setAdapter(mAdapter);
        total_post = 0;
        currentPage = 0;
    }

    private void setComponentDialog(){
        showDialogLayout(R.layout.layout_input_password);
        TextView tvTitleBarang = dialog.findViewById(R.id.tvTitleBarang);
        tvTitleBarang.setText("TAMBAH STOK");
        TextView etMasterKey = dialog.findViewById(R.id.etMasterKey);
        etMasterKey.setText("Silahkan masukan password akun anda");
        EditText etPassword = dialog.findViewById(R.id.etPassword);
        Button btnProses = dialog.findViewById(R.id.btnProses);
        btnProses.setOnClickListener(v1 -> {
            if (!etPassword.getText().toString().equals("")) {
                dialog.dismiss();
                checkPass(etPassword.getText().toString());
            } else {
                dialog.dismiss();
                Toast.makeText(context, "Silahkan isi password akun anda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecyclerPeymentType(RecyclerView rvPaymentType) {
        Loading.show(context);
        ApiLocal.apiInterface().getPaymentTypes("asc", "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<PaymentTypeResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PaymentTypeResponse>>> call, Response<ApiResponse<List<PaymentTypeResponse>>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        List<PaymentTypeResponse> paymentTypeResponseList = Objects.requireNonNull(response.body()).getData();
                        PreferenceManager.setTransactionType(paymentTypeResponseList);
                        PaymentTypeAdapter adapter = new PaymentTypeAdapter(paymentTypeResponseList, context);
                        rvPaymentType.setAdapter(adapter);
                    } else {
                        Toast.makeText(context, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PaymentTypeResponse>>> call, Throwable t) {
                t.printStackTrace();
                Loading.hide(context);

                if (PreferenceManager.isOfflineMode()) {
                    List<PaymentTypeResponse> paymentTypeResponseList = PreferenceManager.getTransactionType();
                    PaymentTypeAdapter adapter = new PaymentTypeAdapter(paymentTypeResponseList, context);
                    rvPaymentType.setAdapter(adapter);
                }
            }
        });
    }

    private void checkMember(String UID, String flag) {
        Loading.show(context);
        Api.apiInterface().checkMember(UID, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<CheckMemberResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CheckMemberResponse>> call, Response<ApiResponse<CheckMemberResponse>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()) {
                        CheckMemberResponse memberResponse = response.body().getData();
                        String member_id = memberResponse.getId();
                        String member_name = memberResponse.getFullname();
                        String member_lulusan = memberResponse.getOpt1();
                        String member_angkatan = memberResponse.getOpt2();
                        String created_at = memberResponse.getCreatedAt();

                        //coba encry

                        if (flag.equals("handleTopup")) {
//                            doTopup(member_id, member_name, member_lulusan, member_angkatan);
                        } else if (flag.equals("handleFreeMeal")) {
                            Intent intent = new Intent(HomeActivity.this, FreeMeal.class);
                            intent.putExtra("member_id", member_id);
                            intent.putExtra("member_name", member_name);
                            intent.putExtra("member_lulusan", member_lulusan);
                            intent.putExtra("member_angkatan", member_angkatan);
                            startActivity(intent);
                        } else if (flag.equals("register")) {
                            Toast.makeText(context, "Kartu anda telah terdaftar", Toast.LENGTH_SHORT).show();
                        } else {
                            cekSaldo(member_id, member_name, member_lulusan, member_angkatan);
                        }
                    } else {
                        if (response.message().equals("Unauthorized")) {
                            setDialog(R.layout.dialog_session_expired);
                            ImageView btnClose = dialog.findViewById(R.id.btnClose);
                            btnClose.setOnClickListener(v -> dialog.dismiss());
                            Button btnRelogin = dialog.findViewById(R.id.btnRelogin);
                            btnRelogin.setOnClickListener(v -> {
                                goToLoginPage();
                            });
                        }

                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        if (flag.equals("register")) {
                            if (jObjError.getString("error").startsWith("Member not")) {
                                Intent intent = new Intent(HomeActivity.this, RegisterMember.class);
                                intent.putExtra("UID", UID);
                                startActivity(intent);
                            } else {
                                try {
                                    Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CheckMemberResponse>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void cekSaldo(String memberId, String member_name, String member_lulusan, String member_angkatan) {
        Loading.show(context);
        Api.apiInterface().cekSaldo(memberId, Constant.PARTNER_ID_NURUL_FIKRI, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<Members>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<Members>> call, Response<ApiResponse<Members>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()) {
                        Members members = response.body().getData();
                        String member_id = members.getId();
                        String created_at = members.getCreatedAt();
                        int pinState = Integer.parseInt(members.getPin_state() != null ? members.getPin_state() : "0");
                        switch (pinState) {
                            case 0:
                                int saldo = Integer.parseInt(members.getBalance());
                                String no_va = members.getPartner_acc_no();
                                Intent intent = new Intent(HomeActivity.this, SaldoActivity.class);
                                intent.putExtra("sisaSaldo", saldo + "");
                                intent.putExtra("no_va", no_va);
                                intent.putExtra("member_name", member_name);
                                intent.putExtra("member_lulusan", member_lulusan);
                                intent.putExtra("member_angkatan", member_angkatan);
                                startActivity(intent);
                                break;
                            case 1:
                                setDialog(R.layout.layout_pin);
                                Button btnSubmitPin = dialog.findViewById(R.id.btnSubmit);
                                EditText etPinS = dialog.findViewById(R.id.etPin);
                                btnSubmitPin.setOnClickListener(v -> {
                                    dialog.dismiss();
                                    checkPIN(member_id, Utils.doActivatePin(etPinS.getText().toString(), member_id, created_at), members);
                                });
                                break;
                            case 2:
                                //Aktivasi PIN
                                setDialog(R.layout.layout_aktivasi_pin);
                                Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
                                spinner = dialog.findViewById(R.id.spinner_question);
                                getSecurityQuestion();
                                EditText etJawaban = dialog.findViewById(R.id.etJawaban);
                                EditText etPin = dialog.findViewById(R.id.etPin);
                                EditText etKonfirmasiPin = dialog.findViewById(R.id.etKonfirmasiPin);

                                btnSubmit.setOnClickListener(v1 -> {
                                    if (!etKonfirmasiPin.getText().toString().equals(etPin.getText().toString())) {
                                        Toast.makeText(context, "Silahkan periksa kembali pin yang anda masukan", Toast.LENGTH_SHORT).show();
                                    } else {
                                        dialog.dismiss();
                                        RequestBody requestBody = new MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("security_question_id", spinnerQuestionId)
                                                .addFormDataPart("security_question_answer", etJawaban.getText().toString())
                                                .addFormDataPart("new_pin", Utils.doActivatePin(etPin.getText().toString(), member_id, created_at))
                                                .addFormDataPart("m_id", member_id)
                                                .build();
                                        sendPinEncrypted(Utils.doActivatePin(etPin.getText().toString(), member_id, created_at), requestBody);
                                    }
                                });

                                break;
                            case 3:
                                Toast.makeText(context, "Silahkan hubungi customer service", Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                setDialog(R.layout.layout_pin);
                                EditText pin = dialog.findViewById(R.id.etPin);
                                Button btn = dialog.findViewById(R.id.btnSubmit);
                                btn.setOnClickListener(v -> {
                                    dialog.dismiss();
                                    checkPIN(member_id, Utils.doActivatePin(pin.getText().toString(), member_id, created_at), members);
                                });

//                                setDialog(R.layout.layout_wrong_pass);
//                                Button btnOK = dialog.findViewById(R.id.btnOK);
//                                TextView tvTitleBarang = dialog.findViewById(R.id.tvTitleBarang);
//                                tvTitleBarang.setText("PIN TERBLOKIR");
//                                TextView etMasterKey = dialog.findViewById(R.id.etMasterKey);
//                                etMasterKey.setText("Silahkan hubungi customer service");
//                                btnOK.setOnClickListener(v -> dialog.dismiss());
                                break;
                        }
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            Toast.makeText(context, jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                            if (response.message().equals("Unauthorized")) {
                                setDialog(R.layout.dialog_session_expired);
                                ImageView btnClose = dialog.findViewById(R.id.btnClose);
                                btnClose.setOnClickListener(v -> dialog.dismiss());
                                Button btnRelogin = dialog.findViewById(R.id.btnRelogin);
                                btnRelogin.setOnClickListener(v -> {
                                    goToLoginPage();
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Members>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void checkPIN(String member_id, String pin, Members members) {
        Loading.show(context);
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("m_id", member_id)
                    .addFormDataPart("sc", pin)
                    .build();
            Api.apiInterface().checkPIN(requestBody, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Loading.hide(context);
                    if (response.isSuccessful()) {
                        int saldo = Integer.parseInt(members.getBalance());
                        String no_va = members.getPartner_acc_no();
                        String member_name = members.getFullname();
                        String member_lulusan = members.getOpt1();
                        String member_angkatan = members.getOpt2();

                        Intent intent = new Intent(HomeActivity.this, SaldoActivity.class);
                        intent.putExtra("sisaSaldo", saldo + "");
                        intent.putExtra("no_va", no_va);
                        intent.putExtra("member_name", member_name);
                        intent.putExtra("member_lulusan", member_lulusan);
                        intent.putExtra("member_angkatan", member_angkatan);
                        startActivity(intent);
                    } else {
                        if (response.message().equals("Unauthorized")) {
                            setDialog(R.layout.dialog_session_expired);
                            ImageView btnClose = dialog.findViewById(R.id.btnClose);
                            btnClose.setOnClickListener(v -> dialog.dismiss());
                            Button btnRelogin = dialog.findViewById(R.id.btnRelogin);
                            btnRelogin.setOnClickListener(v -> {
                                goToLoginPage();
                            });
                        }

                        try {
                            setDialog(R.layout.layout_wrong_pass);
                            Button btnOK = dialog.findViewById(R.id.btnOK);
                            TextView tvTitleBarang = dialog.findViewById(R.id.tvTitleBarang);
                            TextView etMasterKey = dialog.findViewById(R.id.etMasterKey);
                            btnOK.setOnClickListener(v -> dialog.dismiss());

                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            if (jObjError.getString("error").startsWith("Balance not")) {
                                tvTitleBarang.setText("SALDO KURANG");
                                etMasterKey.setText("Saldo anda kurang, silahkan isi saldo anda terlebih dahulu");
                            } else if (jObjError.getString("error").startsWith("Invalid Data: 01")) {
                                tvTitleBarang.setText("PIN SALAH");
                                etMasterKey.setText("PIN anda salah, silahkan periksa kembali PIN anda");
                            } else {
                                tvTitleBarang.setText("WARNING");
                                etMasterKey.setText(jObjError.getString("error"));
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Loading.hide(context);
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPinEncrypted(String encryptedText, RequestBody requestBody) {
        if (encryptedText.equals("") || spinnerQuestionId.equals("")) {
            Toast.makeText(context, "Terjadi kesalahan pada saat memproses pin", Toast.LENGTH_SHORT).show();
        } else {
            Loading.show(context);
            try {
                Api.apiInterface().doActivatePin(requestBody, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Loading.hide(context);
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Aktivasi PIN berhasil", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "Terjadi kesalahan saat aktivasi", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Loading.hide(context);
                        t.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getSecurityQuestion() {
        Loading.show(context);

        questionList = new ArrayList<>();
        questionIdList = new ArrayList<>();
        questionList.add("Pilih pertanyaan keamanan");
        questionIdList.add("0");
        Api.apiInterface().getListQuestion("Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<List<SecurityQuestions>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SecurityQuestions>>> call, Response<ApiResponse<List<SecurityQuestions>>> response) {
                Loading.hide(context);
                try {
                    List<SecurityQuestions> res = response.body().getData();
                    for (int i = 0; i < res.size(); i++) {
                        SecurityQuestions users = res.get(i);
                        questionList.add(users.getQuestion());
                        questionIdList.add(users.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SecurityQuestions>>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HomeActivity.this, R.layout.layout_spinner_text, questionList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spinnerQuestionId = questionIdList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getDetailProfil() {
        pullToRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getDetailLocation(loginStockLocation.location_id, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<DetailLocationResponse>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<DetailLocationResponse>> call, Response<ApiResponse<DetailLocationResponse>> response) {
                pullToRefresh.setRefreshing(false);
                try {
                    DetailLocationResponse res = Objects.requireNonNull(response.body()).getData();
                    Log.d("GREETING NAME", res.getName());
                    greeting_name.setText(res.getName());
                    Business business = new Business();
                    business.name = res.getName();
                    business.address = res.getAddress();
                    business.id = res.getBusinessId();
                    business.brand_id = res.getBrandId();
                    PreferenceManager.saveBusiness(business);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DetailLocationResponse>> call, Throwable t) {
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void getPenjualanPendapatan(String currentTrxSumByUser, String todaytrxSum) {
        int sumNonTunai = Integer.parseInt(todaytrxSum) - Integer.parseInt(currentTrxSumByUser);

        summaryList = new ArrayList();
        SummaryReport summary1 = new SummaryReport();
        SummaryReport summary3 = new SummaryReport();

        summary1.report_name = "Penjualan Tunai";
        summary1.report_value = "Rp. " + MethodUtil.toCurrencyFormat(currentTrxSumByUser);
        summary1.report_period = "Hari ini";

        summary3.report_name = "Penjualan Non Tunai";
        summary3.report_value = "Rp. " + MethodUtil.toCurrencyFormat(String.valueOf(sumNonTunai));
        summary3.report_period = "Hari ini";

        summaryList.add(summary1);
        summaryList.add(summary3);

        mAdapter.setData(summaryList);

    }

    private void loadMorePostsData() {
//        mPresenter.fetchData(currentPage+1);
    }


    private void initEvent() {
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PreferenceManager.getStockLocation()!=null){
            getInquiryStatusOperations(false);
        }
//        mPresenter.lastTransactions(loginStockLocation.location_id);
    }


    @Override
    public void showProgressLoading() {
        progressBar.show(this, "", false, null);
    }

    @Override
    public void hideProgresLoading() {
        progressBar.getDialog().dismiss();
    }

    @Override
    public NetworkService getService() {
        return NetworkManager.getInstance();
    }

    @Override
    public void onFailureRequest(String msg) {
        MethodUtil.showCustomToast(this, msg, R.drawable.ic_error_login);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressBar != null && progressBar.getDialog() != null) {
            progressBar.getDialog().dismiss();
        }
    }

    @Override
    public void onSuccessGetLatestTransactions(List<Transaction> transactions) {
        transactionsAdapter.setDataList(transactions);
        setListViewHeightBasedOnChildren(transactionsListView);
        transactionsListView.setHasMoreItems(false);
    }

    @Override
    public void onSuccessGetLatestTransactionsNow(List<TransactionResponse> transactions) {
        Gson gson = new Gson();
        String json = gson.toJson(transactions);
        Log.d("TAG OnSuccess", json);

    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void itemClicked(int position) {

    }

    @Override
    public void itemDeleted(int position) {

    }

    void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
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
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(HomeActivity.this, StokActivity.class);
                        startActivity(intent);
                    } else {
                        showDialogLayout(R.layout.layout_wrong_pass);
                        TextView etMasterKey = dialog.findViewById(R.id.etMasterKey);
                        etMasterKey.setText("Silahkan periksa kembali password yang anda masukan");
                        Button btnOk = dialog.findViewById(R.id.btnOK);
                        btnOk.setOnClickListener(v1 -> dialog.dismiss());
                    }
                } catch (Exception e) {
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

    @SuppressLint("SetTextI18n")
    private void showRecap(){
        pullToRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getInqStatusOperations(loginStockLocation.location_id, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<InquiryStatusOperations>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<InquiryStatusOperations>> call, Response<ApiResponse<InquiryStatusOperations>> response) {
                pullToRefresh.setRefreshing(false);
                try {
                    if (response.isSuccessful()){
                        statusOperations = Objects.requireNonNull(response.body()).getData();
                        PreferenceManager.setOperationData(statusOperations);
                        boolean isOpen = statusOperations.getIsOpen();

                        String initCash = statusOperations.getCurrentCash()<0? "-Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentCash()+"") : "Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentCash()+"");
                        String operator = statusOperations.getOpenedByUser()!=null?statusOperations.getOpenedByUser().getFullname():"-";
                        String currentTrxSumByUser = statusOperations.getCurrentCashTrxSum();
                        String todaytrxSum = statusOperations.getCurrentTrxSum();
                        String expenditureTotal = statusOperations.getCurrent_expense_sum();

                        getPenjualanPendapatan(currentTrxSumByUser, todaytrxSum);
                        setStoreStatusComp(isOpen);

                        tv_petty_cash.setText(initCash);
                        greetingWords.setText(greeting + " " + PreferenceManager.getUser().fullname);
                        tv_expenditure.setText("Rp " + MethodUtil.toCurrencyFormat(expenditureTotal));

                        showDialogLayout(R.layout.layout_close_store);
                        Button btnPrint = dialog.findViewById(R.id.btnPrint);
                        Button btnProses = dialog.findViewById(R.id.btnProses);
                        Button btnListTransaction = dialog.findViewById(R.id.btnListTransaction);
                        TextView tvLocationName = dialog.findViewById(R.id.tvLocationName);
                        TextView tvLocationAddress = dialog.findViewById(R.id.tvLocationAddress);
                        Button btnAdjustment = dialog.findViewById(R.id.btnAdjustment);
                        LinearLayout layoutAdjustment = dialog.findViewById(R.id.layoutAdjustment);
                        TextView tvAdjustmentCash = dialog.findViewById(R.id.tvAdjustmentCash);

                        imgLogo = dialog.findViewById(R.id.imgLogo);
                        tvTransactionSum = dialog.findViewById(R.id.tvTransactionSum);
                        tvExpenditureTotal = dialog.findViewById(R.id.tvExpenditureTotal);
                        tvKasAwal = dialog.findViewById(R.id.tvKasAwal);
                        tvGrandTotal = dialog.findViewById(R.id.tvGrandTotal);

                        RecyclerView rvStockItem = dialog.findViewById(R.id.rvStockItem);
                        TextView tvOperator = dialog.findViewById(R.id.tvOperator);
                        TextView tvOpenDate = dialog.findViewById(R.id.tvOpenDate);
                        TextView tvClosedDate = dialog.findViewById(R.id.tvClosedDate);

                        tvSummaryGopay = dialog.findViewById(R.id.tvSummaryGopay);
                        tvSummaryOvo = dialog.findViewById(R.id.tvSummaryOvo);
                        tvSummaryQris = dialog.findViewById(R.id.tvSummaryQris);
                        tvSummaryTransfer = dialog.findViewById(R.id.tvSummaryTransfer);

                        RecapStockAdapter adapter = new RecapStockAdapter(statusOperations.getStocks(), context, isOpen);
                        rvStockItem.setAdapter(adapter);

                        String imageLogo = "";
                        try { imageLogo = NetworkService.BASE_URL_IMAGE + loginStockLocation.location.getBrand().getLogo_image_url();} catch (Exception e){}
                        String locationName = statusOperations.getLocation().getName();
                        String locationAddress = statusOperations.getLocation().getAddress();
                        int trxSum = Integer.parseInt(statusOperations.getCurrentCashTrxSum());
                        int grandTotal = statusOperations.getCurrentCash();
                        String grandTotalString = statusOperations.getCurrentCash()<0? "-Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentCash()+"") : "Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentCash()+"");
                        int summaryGopay = statusOperations.getCurrentGopayTrxSum();
                        int summaryOvo = statusOperations.getCurrentOvoTrxSum();
                        int summaryQris = Integer.parseInt(statusOperations.getCurrentQrisTrxSum());
                        int summaryTransfer = statusOperations.getCurrentBankTransferTrxSum();
                        String adjustedClosingCash = statusOperations.getAdjustedClosingCash()!=null?statusOperations.getAdjustedClosingCash():"0";

                        String jamTutup = "Jam Tutup: -";
                        String jamBuka = "";
                        try {
                            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id","ID"));

                            Date dateOpen = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id")).parse(statusOperations.getOpenedAt());
                            jamBuka = df.format(dateOpen);

                            if (statusOperations.getClosedAt()!=null) {
                                Date dateClosed = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id")).parse(statusOperations.getClosedAt());
                                jamTutup = "Jam Tutup: " + df.format(dateClosed);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (adjustedClosingCash.equals("0")) {
                            layoutAdjustment.setVisibility(View.GONE);
                        } else {
                            layoutAdjustment.setVisibility(View.VISIBLE);
                            int adjusmentTotal = grandTotal - Integer.parseInt(adjustedClosingCash);
                            tvAdjustmentCash.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(adjusmentTotal)) + "");
                        }

                        tvOpenDate.setText("Jam Buka: " + jamBuka);
                        tvClosedDate.setText(jamTutup);
                        imgLogo.destroyDrawingCache();
                        imgLogo.setDrawingCacheEnabled(true);
                        Glide.with(context)
                                .load(imageLogo)
                                .placeholder(R.drawable.pd_logo_black_white)
                                .into(imgLogo);

                        tvLocationName.setText(locationName);
                        tvLocationAddress.setText(locationAddress);
                        tvTransactionSum.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(trxSum)));
                        tvExpenditureTotal.setText("Rp " + MethodUtil.toCurrencyFormat(expenditureTotal));
                        tvGrandTotal.setText(grandTotalString);

                        tvSummaryGopay.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(summaryGopay)));
                        tvSummaryOvo.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(summaryOvo)));
                        tvSummaryQris.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(summaryQris)));
                        tvSummaryTransfer.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(summaryTransfer)));

                        tvOperator.setText("Operator: " + operator);

                        btnAdjustment.setOnClickListener(v -> {
                            showAdjustmentDialog(false, 0);
                        });

                        if (statusOperations.getIsOpen()) {
                            btnAdjustment.setVisibility(View.VISIBLE);
                            btnPrint.setVisibility(View.GONE);
                            btnListTransaction.setOnClickListener(v -> {
                                Intent intent = new Intent(context, HistoryActivity.class);
                                intent.putExtra("isFromCloseStore", true);
                                startActivity(intent);
                            });

                            btnProses.setOnClickListener(v -> {
                                isButtonProsesClicked = true;
                                printBtn();
                            });
                        } else {
                            btnAdjustment.setVisibility(View.GONE);
                            btnProses.setVisibility(View.GONE);
                            btnListTransaction.setVisibility(View.GONE);
                            btnPrint.setVisibility(View.VISIBLE);
                        }

                        btnPrint.setOnClickListener(v -> {
                            isButtonPrintClicked = true;
                            printBtn();
                        });
                    } else {
                        Toast.makeText(context, "Failed to parse response", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<InquiryStatusOperations>> call, Throwable t) {
                pullToRefresh.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    public void showAdjustmentDialog(boolean isFromStock, int stockId) {
        dialog.dismiss();
        showDialogLayout(R.layout.dialog_adjustment);
        EditText etNominal = dialog.findViewById(R.id.etNominal);
        EditText etKaterangan = dialog.findViewById(R.id.etKaterangan);
        Button btnSimpan = dialog.findViewById(R.id.btnProses);

        if (isFromStock) {
            etNominal.setHint("Masukan stok fisik yang tersisa");
            btnSimpan.setOnClickListener(v1 -> {
                if (etNominal.getText().toString().equals("") || etKaterangan.getText().toString().equals("")) {
                    Toast.makeText(context, "Silahkan lengkapi form", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                reportAdjustmentStock(stockId+"", etNominal.getText().toString().replace(".",""), etKaterangan.getText().toString());
            });
        } else {
            etNominal.setHint("Masukan Dana Kasir");
            MethodUtil.setCurrency(etNominal);
            btnSimpan.setOnClickListener(v1 -> {
                if (etNominal.getText().toString().equals("") || etKaterangan.getText().toString().equals("")) {
                    Toast.makeText(context, "Silahkan lengkapi form", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                reportAdjustmentCash(etNominal.getText().toString().replace(".",""), etKaterangan.getText().toString());
            });
        }


    }

    public void printStruk() {
        try {
            str = context.getString(R.string.openingPrint) + "\n";
            handler.post(myRunnable);
            printerDevice.open();
            str += context.getString(R.string.printerOpenSuc) + "\n";
            handler.post(myRunnable);
            format = new Format();
            try {
                if (printerDevice.queryStatus() == printerDevice.STATUS_OUT_OF_PAPER) {
                    str += context.getString(R.string.queryStatus) + "\n";
                    handler.post(myRunnable);
                    Toast.makeText(context, "The printer is short of paper", Toast.LENGTH_SHORT).show();
                } else if (printerDevice.queryStatus() == printerDevice.STATUS_PAPER_EXIST) {
                    str += context.getString(R.string.statusNor) + "\n";
                    handler.post(myRunnable);
                    Thread thread = new Thread(() -> {
                        // TODO Auto-generated method stub
                        try {

                            format.setParameter("align", "center");
                            format.setParameter("bold", "true");
                            format.setParameter("size", "medium");

                            Bitmap bitmap = null;

                            try {
                                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pd_logo_resi);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

//                                URL url = new URL(loginStockLocation.location.getBrand().getLogo_image_url());
//                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                                connection.setDoInput(true);
//                                connection.connect();
//                                InputStream input = connection.getInputStream();
//                                Bitmap myBitmap = BitmapFactory.decodeStream(input);
//                                ByteArrayOutputStream streamUri = new ByteArrayOutputStream();
//                                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, streamUri);

                                Bitmap myBitmap = getBitmapFromURL(NetworkService.BASE_URL_IMAGE + PreferenceManager.getStockLocation().location.getBrand().getLogo_image_url());
                                printerDevice.printBitmap(format, bitmap);
                                printerDevice.printText("\n \n");

                            } catch (Exception e){
                                e.printStackTrace();
                                printerDevice.printBitmap(format, bitmap);
                                printerDevice.printText("\n \n");
                            }

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id","ID"));
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm ");
                            String order_date = dateFormat.format(new Date());
                            String order_time = timeFormat.format(new Date());
                            String jamTutup = "-";

                            Date dateOpen = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id")).parse(statusOperations.getOpenedAt());
                            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id","ID"));
                            if (statusOperations.getClosedAt()!=null) {
                                Date dateClosed = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id")).parse(statusOperations.getClosedAt());
                                jamTutup = df.format(dateClosed);
                            }


                            String jamBuka = df.format(dateOpen);

                            printerDevice.printText(format, statusOperations.getLocation().getName() + "\n" +
                                    statusOperations.getLocation().getAddress() + "\n");

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("bold", "true");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, order_date + " " + order_time + "\n\n");

                            printerDevice.printText(format, getPrintLabelValue("Operator", statusOperations.getOpenedByUser().getFullname(),false,true));
                            printerDevice.printText(format, getPrintLabelValue("Jam Buka", jamBuka,false,true));
                            printerDevice.printText(format, getPrintLabelValue("Jam Tutup", jamTutup,false,true));
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "--------------------------------\n\n");

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("bold", "true");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "Rekap Keuangan Tunai" + "\n\n");

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, getPrintLabelValue("Modal Awal", tvKasAwal.getText().toString(), false, true));
                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, getPrintLabelValue("Pendapatan Tunai", tvTransactionSum.getText().toString(), false, true));
                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, getPrintLabelValue("Pengeluaran", tvExpenditureTotal.getText().toString(), false, true));

                            printerDevice.printText(format, "--------------------------------\n");

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("bold", "true");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, getPrintLabelValue("Grand Total", tvGrandTotal.getText().toString(), false, true));
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "--------------------------------\n\n");

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("bold", "true");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "Rekap Penjualan Non Tunai" + "\n\n");

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, getPrintLabelValue("Penjualan QRIS", "Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentQrisTrxSum()), false, true));
                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, getPrintLabelValue("Penjualan Transfer", "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(statusOperations.getCurrentBankTransferTrxSum())), false, true));
                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, getPrintLabelValue("Penjualan Gopay", "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(statusOperations.getCurrentGopayTrxSum())), false, true));
                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, getPrintLabelValue("Penjualan OVO", "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(statusOperations.getCurrentOvoTrxSum())), false, true));

                            printerDevice.printText(format, "--------------------------------\n");

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("bold", "true");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "Rekap Stok Barang" + "\n\n");


                            for (StockReport stock: statusOperations.getStocks()){
                                String totalAdjustment = stock.getTotalAdjusted()!=null?stock.getTotalAdjusted():"0";

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printlnText(format, stock.getItemName()+"\n");
                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printlnText(format, getPrintLabelValue("      Stok Awal", stock.getQtyInitial()+"", false, true));
                                printerDevice.printlnText(format, getPrintLabelValue("      Stok Masuk", stock.getSumQtyIn()+"", false, true));
//                                printerDevice.printlnText(format, getPrintLabelValue("    Stok Keluar", stock.getSumQtyOut()+"", false, true));
                                printerDevice.printlnText(format, getPrintLabelValue("      Stok Terjual", stock.getTotalSold()+"", false, true));
                                printerDevice.printlnText(format, getPrintLabelValue("      Stok Rusak", String.valueOf(stock.getTotalDamaged()!=null?stock.getTotalDamaged():0), false, true));
                                printerDevice.printlnText(format, "    ----------------------------");
                                printerDevice.printlnText(format, getPrintLabelValue("      Sisa Stok", stock.getQtyEnd()+"", false, true));
                                if (!totalAdjustment.equals("0")) printerDevice.printlnText(getPrintLabelValue("      Perbedaan Stok", totalAdjustment, false, true));
                            }

                            printerDevice.printText(format, "--------------------------------\n\n");

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "small");
                            printerDevice.printText(format, "Mireta by Selada\n\n");
                            printerDevice.printText(format, "  \n\n");
                            printerDevice.printText(format, "  .\n\n");


                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                } else {
                    Toast.makeText(context, "The printer is short of paper", Toast.LENGTH_SHORT).show();
                }
            } catch (DeviceException de) {
                str += context.getString(R.string.checkStatus) + "\n";
                handler.post(myRunnable);
                de.printStackTrace();
            }
        } catch (DeviceException de) {
            de.printStackTrace();
            str += context.getString(R.string.openFailed) + "\n";
            handler.post(myRunnable);
        }
    }

    private void closePrinter() {
        try {
            printerDevice.close();
            str += context.getString(R.string.closeSuc) + "\n";
            handler.post(myRunnable);
        } catch (DeviceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            str += context.getString(R.string.closeFailed) + "\n";
            handler.post(myRunnable);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void printBtn() {
        if (manufacturer.equals("wizarPOS")) {
            closePrinter();
            printStruk();
        } else {
            printText();
        }
    }

    public void printText() {
        if (!mService.isAvailable()) {
            Log.i("TAG", "printText: perangkat tidak support bluetooth");
            return;
        }

        if (isPrinterReady) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id","ID"));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm ");
            String order_date = dateFormat.format(new Date());
            String order_time = timeFormat.format(new Date());
            String item;
            mService.write(PrinterCommands.CENTER_ALIGN);
            try {

                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.pd_logo_black_white, o);

                //The new size we want to scale to
                final int REQUIRED_WIDTH=150;
                final int REQUIRED_HIGHT=75;
                //Find the correct scale value. It should be the power of 2.
                int scale=1;
                while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                    scale*=2;

                //Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize=scale;

//                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
//                        R.drawable.pd_logo_black_white, o2);
                Bitmap bmp = getBitmapFromURL(NetworkService.BASE_URL_IMAGE + PreferenceManager.getStockLocation().location.getBrand().getLogo_image_url());
                if(bmp!=null){
                    byte[] command = Utils.decodeBitmap(bmp);
                    mService.write(command);
                    mService.sendMessage("\n", "");
                }else{
                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }

            Date dateOpen = null;
            String dateClosedString = statusOperations.getClosedAt()!=null?statusOperations.getClosedAt():"";
            try {
                dateOpen = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id")).parse(statusOperations.getOpenedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date dateClosed = null;
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id","ID"));

            String jamTutup = "-";
            try {
                dateClosed = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id")).parse(dateClosedString);
                jamTutup = df.format(dateClosed);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String jamBuka = df.format(dateOpen);
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(loginStockLocation.location.getBrand().getName(), "");
            mService.sendMessage(statusOperations.getLocation().getName(), "");
            mService.sendMessage(statusOperations.getLocation().getAddress(), "");
            mService.sendMessage(getPrintBTLabelValue("   ", "   ", false, true), "");
            mService.sendMessage(getPrintBTLabelValue("Operator", statusOperations.getOpenedByUser().getFullname(), false,true), "");
            mService.sendMessage(getPrintBTLabelValue("Jam Buka", jamBuka, false,true), "");
            mService.sendMessage(getPrintBTLabelValue("Jam Tutup", jamTutup, false,true), "");
            mService.sendMessage("----------------------------------------", "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.write(PrinterCommands.ESC_ENTER);
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage("Rekap Keuangan Tunai", "");
            mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");
            mService.write(PrinterCommands.LEFT_ALIGN);
//            mService.sendMessage(getPrintBTLabelValue("Modal Awal", "Rp " + MethodUtil.toCurrencyFormat(statusOperations.getInitialCash()), false, true), "");
            mService.sendMessage(getPrintBTLabelValue("Pendapatan Tunai", "Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentCashTrxSum()), false, true), "");
            mService.sendMessage(getPrintBTLabelValue("Pengeluaran", "Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrent_expense_sum()), false, true), "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage("----------------------------------------", "");
            mService.write(PrinterCommands.LEFT_ALIGN);
            mService.sendMessage(getPrintBTLabelValue("Grand Total", statusOperations.getCurrentCash()<0? "-Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentCash()+"") : "Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentCash()+""), false, true), "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage("----------------------------------------", "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.write(PrinterCommands.ESC_ENTER);

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");
            mService.sendMessage("Rekap Penjualan Non Tunai", "");
            mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");

            mService.write(PrinterCommands.LEFT_ALIGN);
            mService.sendMessage(getPrintBTLabelValue("Penjualan QRIS", "Rp " + MethodUtil.toCurrencyFormat(statusOperations.getCurrentQrisTrxSum()), false, true), "");
            mService.sendMessage(getPrintBTLabelValue("Penjualan Transfer", "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(statusOperations.getCurrentBankTransferTrxSum())), false, true), "");
            mService.sendMessage(getPrintBTLabelValue("Penjualan Gopay", "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(statusOperations.getCurrentGopayTrxSum())), false, true), "");
            mService.sendMessage(getPrintBTLabelValue("Penjualan OVO", "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(statusOperations.getCurrentOvoTrxSum())), false, true), "");

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage("----------------------------------------", "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.write(PrinterCommands.ESC_ENTER);

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");
            mService.sendMessage("Rekap Stok Barang" , "");
            mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");

            mService.write(PrinterCommands.LEFT_ALIGN);
            for (StockReport stock: statusOperations.getStocks()){
                String totalAdjustment = stock.getTotalAdjusted()!=null?stock.getTotalAdjusted():"0";

                mService.sendMessage(stock.getItemName(), "");
                mService.sendMessage(getPrintBTLabelValue("    Stok Awal", MethodUtil.toCurrencyFormat(String.valueOf(stock.getQtyInitial())), false, true), "");
                mService.sendMessage(getPrintBTLabelValue("    Stok Masuk", MethodUtil.toCurrencyFormat(stock.getSumQtyIn()), false, true), "");
                mService.sendMessage(getPrintBTLabelValue("    Stok Keluar", MethodUtil.toCurrencyFormat(stock.getSumQtyOut()), false, true), "");
                mService.sendMessage("    ------------------------------------", "");
                mService.sendMessage(getPrintBTLabelValue("    Total Stok", MethodUtil.toCurrencyFormat(String.valueOf(stock.getQtyEnd())), false, true), "");
                if (!totalAdjustment.equals("0")) mService.sendMessage(getPrintBTLabelValue("         Penyesuaian Stok", totalAdjustment, false, true), "");
                mService.sendMessage(getPrintBTLabelValue("     - Stok Rusak", MethodUtil.toCurrencyFormat(String.valueOf(stock.getTotalDamaged()!=null?stock.getTotalDamaged():0)), false, true), "");
                mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");
            }

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(getResources().getString(R.string.footer_print) + "\n\n\n", "");

            isButtonPrintClicked = false;
            isButtonProsesClicked = false;
        } else {
            if (mService.isBTopen())
                startActivityForResult(new Intent(this, DeviceActivity.class), RC_CONNECT_DEVICE);
            else
                requestBluetooth();
        }
    }

    private void requestBluetooth() {
        if (mService != null) {
            if (!mService.isBTopen()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
            }
        }
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH=100;
            final int REQUIRED_HIGHT=50;
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            Bitmap myBitmap = BitmapFactory.decodeStream(input, null, o2);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private String getPrintLabelValue(String label, String value, boolean usingNextLine, boolean endWithNewLine){
        int lineCharCount = 32;
        int labelCharCount = label.length();
        int valueCharCount = value.length();

        if (labelCharCount + valueCharCount > (lineCharCount-2)){
            usingNextLine = true;
        }

        String output = "";
        if (!usingNextLine){
            output += label;
            for (int i = labelCharCount; i < lineCharCount-valueCharCount; i++){
                output += " ";
            }
            output += value;
        }
        else{
            output = label;
            for (int i = labelCharCount; i < lineCharCount; i++){
                output += " ";
            }
            output += "\n";
            for (int i = 0; i < valueCharCount; i++){
                output += " ";
            }
            output += value;
        }
        if (endWithNewLine){
            output += "\n";
        }
        return output;
    }

    private String getPrintBTLabelValue(String label, String value, boolean usingNextLine, boolean endWithNewLine){
        int lineCharCount = 42;
        int labelCharCount = label.length();
        int valueCharCount = value.length();

        if (labelCharCount + valueCharCount > (lineCharCount-2)){
            usingNextLine = true;
        }

        String output = "";
        if (!usingNextLine){
            output += label;
            for (int i = labelCharCount; i < lineCharCount-valueCharCount; i++){
                output += " ";
            }
        }
        else{
            output = label;
            for (int i = labelCharCount; i < lineCharCount; i++){
                output += " ";
            }
            output += "\n";
            for (int i = 0; i < valueCharCount; i++){
                output += " ";
            }
        }
        output += value;
        if (endWithNewLine){
            output += "";
        }
        return output;
    }

    private void setCurrency(final EditText edt) {
        edt.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    edt.removeTextChangedListener(this);

                    Locale local = new Locale("id", "id");
                    String replaceable = String.format("[Rp,.\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    String cleanString = s.toString().replaceAll(replaceable,
                            "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    NumberFormat formatter = NumberFormat
                            .getCurrencyInstance(local);
                    formatter.setMaximumFractionDigits(0);
                    formatter.setParseIntegerOnly(true);
                    String formatted = formatter.format((parsed));

                    String replace = String.format("[Rp\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    String clean = formatted.replaceAll(replace, "");

                    current = formatted;
                    edt.setText(clean);
                    edt.setSelection(clean.length());
                    edt.addTextChangedListener(this);
                }
            }
        });
    }

    private void setDialog(int layout) {

        dialog = new Dialog(Objects.requireNonNull(HomeActivity.this));
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

    private void showDialogLayout(int layout) {
        dialog = new Dialog(Objects.requireNonNull(HomeActivity.this));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    Log.i("TAG", "onActivityResult: bluetooth aktif");
                } else {
                    Log.i("TAG", "onActivityResult: bluetooth harus aktif untuk menggunakan fitur ini");
                }
                break;
            case RC_CONNECT_DEVICE:
                if (resultCode == RESULT_OK) {
                    String address = Objects.requireNonNull(data.getExtras()).getString(DeviceActivity.EXTRA_DEVICE_ADDRESS);
                    mDevice = mService.getDevByMac(address);
                    mService.connect(mDevice);
                }
                break;
        }
    }

    @Override
    public void onDeviceConnected() {
        isPrinterReady = true;
        Loading.hide(context);
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Terhubung dengan perangkat", Snackbar.LENGTH_LONG);
        snackbar.show();

        if (isButtonProsesClicked) {
            doCloseStore();
        }

        if (isButtonPrintClicked) {
            printText();
        }
    }

    @Override
    public void onDeviceConnecting() {
        Loading.show(context);
    }

    @Override
    public void onDeviceConnectionLost() {
        isPrinterReady = false;
        Loading.hide(context);
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Koneksi perangkat terputus", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onDeviceUnableToConnect() {
        Loading.hide(context);
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Tidak dapat terhubung ke perangkat", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.stop();
        }
        mService = null;
    }

    public void setUserLocation(StockLocation stockLocation) {
        stockLocation.location_id = stockLocation.id;
        PreferenceManager.saveStockLocation(stockLocation);
        dialog.dismiss();
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
