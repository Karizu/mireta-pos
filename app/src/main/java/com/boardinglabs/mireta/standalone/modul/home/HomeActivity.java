package com.boardinglabs.mireta.standalone.modul.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import com.boardinglabs.mireta.standalone.component.adapter.RecyReportSummaryAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.RecyTransactionAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.TransactionAdapter;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoBoldTextView;
import com.boardinglabs.mireta.standalone.component.listener.ListActionListener;
import com.boardinglabs.mireta.standalone.component.network.Api;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.NetworkManager;
import com.boardinglabs.mireta.standalone.component.network.NetworkService;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.CheckMemberResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.Members;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.Topup;
import com.boardinglabs.mireta.standalone.component.network.entities.Business;
import com.boardinglabs.mireta.standalone.component.network.entities.Locations.DetailLocationResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.SecurityQuestions;
import com.boardinglabs.mireta.standalone.component.network.entities.SummaryReport;
import com.boardinglabs.mireta.standalone.component.network.entities.Transaction;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionModel;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.Transactions;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Constant;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.component.util.Utils;
import com.boardinglabs.mireta.standalone.modul.CommonInterface;
import com.boardinglabs.mireta.standalone.modul.akun.AkunActivity;
import com.boardinglabs.mireta.standalone.modul.akun.pengaturan.PengaturanAkunActivity;
import com.boardinglabs.mireta.standalone.modul.akun.rfid.TapCard;
import com.boardinglabs.mireta.standalone.modul.ardi.HomeArdiActivity;
import com.boardinglabs.mireta.standalone.modul.ardi.SaldoActivity;
import com.boardinglabs.mireta.standalone.modul.ardi.TopupSuksesActivity;
import com.boardinglabs.mireta.standalone.modul.ardi.freemeal.FreeMeal;
import com.boardinglabs.mireta.standalone.modul.ardi.registermember.RegisterMember;
import com.boardinglabs.mireta.standalone.modul.master.laporan.LaporanActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.StokActivity;
import com.boardinglabs.mireta.standalone.modul.transactions.items.ItemsActivity;
import com.boardinglabs.mireta.standalone.modul.transactions.items.pembayaran.pembayaranardi.PembayaranArdiActivity;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.paging.listview.PagingListView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.boardinglabs.mireta.standalone.component.util.Utils.IV;
import static com.boardinglabs.mireta.standalone.component.util.Utils.bytesToHex;
import static com.boardinglabs.mireta.standalone.component.util.Utils.decodeToBytes;
import static com.boardinglabs.mireta.standalone.component.util.Utils.decrypt;
import static com.boardinglabs.mireta.standalone.component.util.Utils.encodeToString;
import static com.boardinglabs.mireta.standalone.component.util.Utils.encrypt;
import static com.boardinglabs.mireta.standalone.component.util.Utils.hexStringToByteArray;
import static com.boardinglabs.mireta.standalone.component.util.Utils.stringToBytes;

public class HomeActivity extends BaseActivity implements HomeView, CommonInterface, RecyReportSummaryAdapter.OnClickItem, ListActionListener {
    private HomePresenter mPresenter;

    protected TextView greetingName;
    protected TextView greetingWords;
        protected Button createTrxButton;
    protected Button historyButton;
    protected Button accountButton;
    protected Button masterButton;
    protected LinearLayout restock, setting, report;
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

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.tvRecyEmpty)
    TextView recyclerViewEmpty;
    @BindView(R.id.greeting_name)
    RobotoBoldTextView greeting_name;

    AlertDialog alertTaps;
    LayoutInflater li;
    @SuppressLint("InflateParams")
    TapCard promptsView;
    AlertDialog.Builder alertDialogBuilder;

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

    @OnClick(R.id.registerMember)
    void onClickRegisterMember() {
        setting.performClick();
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
        getPenjualanPendapatan();
        loadTransactionsData();
        getDetailProfil();

        pullToRefresh.setOnRefreshListener(() -> {
            transactionModels.clear();
            getDetailProfil();
            loadTransactionsData();
            summaryList.clear();
            sum = 0;
            sumMonth = 0;
            getPenjualanPendapatan();
            pullToRefresh.setRefreshing(false);
        });
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
    private void initComponent(int amount, long rest, int amountMonth, long restMonth) {
        mPresenter = new HomePresenter(this, this);
        greetingWords = (TextView) findViewById(R.id.greeting_words);

//        createTrxButton = (Button) findViewById(R.id.create_trx_button);
//        historyButton = (Button) findViewById(R.id.history_button);
//        accountButton = (Button) findViewById(R.id.account_button);
//        masterButton = (Button) findViewById(R.id.master_button);

        restock = findViewById(R.id.restock);
        report = findViewById(R.id.report);
        setting = findViewById(R.id.setting);
        layoutTrx = findViewById(R.id.layoutTrx);
        restocks = findViewById(R.id.restocks);

//        RxView.clicks(restocks).subscribe(aVoid -> {
//            Intent intent = new Intent(HomeActivity.this, MasterActivity.class);
//            startActivity(intent);
//
//        });

        RxView.clicks(layoutTrx).subscribe(aVoid -> {
            Intent intent = new Intent(HomeActivity.this, ItemsActivity.class);
            startActivity(intent);

        });

        RxView.clicks(restock).subscribe(aVoid -> {
            Intent intent = new Intent(HomeActivity.this, StokActivity.class);
            startActivity(intent);

        });

        RxView.clicks(report).subscribe(aVoid -> {
            Intent intent = new Intent(HomeActivity.this, LaporanActivity.class);
            startActivity(intent);

        });

        RxView.clicks(setting).subscribe(aVoid -> {
            Intent intent = new Intent(HomeActivity.this, AkunActivity.class);
            startActivity(intent);

        });

//        RxView.clicks(createTrxButton).subscribe(aVoid -> {
//            Intent intent = new Intent(HomeActivity.this, ItemsActivity.class);
//            startActivity(intent);
//
//        });
//
//        RxView.clicks(historyButton).subscribe(aVoid -> startActivity(new Intent(HomeActivity.this, HistoryActivity.class)));
//
//        RxView.clicks(accountButton).subscribe(new Action1<Void>() {
//            @Override
//            public void call(Void aVoid) {
//                startActivity(new Intent(HomeActivity.this, AkunActivity.class));
//            }
//        });
//
//        RxView.clicks(masterButton).subscribe(new Action1<Void>() {
//            @Override
//            public void call(Void aVoid) {
//                Intent intent = new Intent(HomeActivity.this, MasterActivity.class);
//                startActivity(intent);
//            }
//        });

        //Get the time of day
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 10 && hour < 14) {
            greeting = "Selamat Siang";
        } else if (hour >= 14 && hour < 19) {
            greeting = "Selamat Sore";
        } else if (hour >= 19) {
            greeting = "Selamat Malam";
        } else {
            greeting = "Selamat Pagi";
        }
        greetingWords.setText(greeting);

        mAdapter = new RecyReportSummaryAdapter(this);
        reportRecyclerView = (RecyclerView) findViewById(R.id.report_recyclerView);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                horizontalLayoutManagaer.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        reportRecyclerView.setLayoutManager(horizontalLayoutManagaer);
        reportRecyclerView.setAdapter(mAdapter);

//        summaryList = new ArrayList();

//        SummaryReport summary1 = new SummaryReport();
//        SummaryReport summary2 = new SummaryReport();
//        SummaryReport summary3 = new SummaryReport();
//        SummaryReport summary4 = new SummaryReport();

//        Log.d("TAG Total", sum+ " "+sumMonth);

//        summary1.report_name = "Penjualan";
//        summary1.report_value = "Rp. " + MethodUtil.toCurrencyFormat(amount + "");
//        summary1.report_period = "Hari ini";
//
//        summary3.report_name = "Pendapatan";
//        summary3.report_value = "Rp. "+MethodUtil.toCurrencyFormat(rest + "");
//        summary3.report_period = "Hari ini";

//        summary2.report_name = "Penjualan";
//        summary2.report_value = "Rp. " + MethodUtil.toCurrencyFormat(amountMonth + "");
//        summary2.report_period = "Bulan ini";
//
//        summary4.report_name = "Pendapatan";
//        summary4.report_value = "Rp. "+MethodUtil.toCurrencyFormat(restMonth + "");
//        summary4.report_period = "Bulan ini";

//        summaryList.add(summary1);
//        summaryList.add(summary3);
//        summaryList.add(summary2);
//        summaryList.add(summary4);
//
//        mAdapter.setData(summaryList);
//
//        Log.d("TAG SUMMONTH", String.valueOf(sumMonth));

//        String amountToday = String.valueOf(sum);
//        String amountMonth = String.valueOf(sumMonth);
//        int totalPenjualanToday = Integer.parseInt(amountToday);
//        int totalPenjualanMonth = Integer.parseInt(amountMonth);

        total_post = 0;
        currentPage = 0;

//        transactionsAdapter = new RecyTransactionAdapter();
//        transactionsAdapter.setListener(this);
//
//        transactionsListView = (PagingListView) findViewById(R.id.today_transaction_list);
//        transactionsListView.setAdapter(transactionsAdapter);

//        transactionsListView.setPagingableListener(new PagingListView.Pagingable() {
//            @Override
//            public void onLoadMoreItems() {
//                transactionsListView.onFinishLoading(false, null);
//            }
//        });
//        transactionsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//                if (transactionsListView.getChildAt(0) != null) {
//                    pullToRefresh.setEnabled(transactionsListView.getFirstVisiblePosition() == 0 && transactionsListView.getChildAt(0).getTop() == 0);
//                }
//            }
//        });
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
                    if (response.isSuccessful()){
                        Members members = response.body().getData();
                        String member_id = members.getId();
                        String created_at = members.getCreatedAt();
                        int pinState = Integer.parseInt(members.getPin_state()!=null?members.getPin_state():"0");
                        switch (pinState){
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
                                    if (!etKonfirmasiPin.getText().toString().equals(etPin.getText().toString())){
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
                        } catch (Exception e){
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

    private void checkPIN(String member_id, String pin, Members members){
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
                            } else if (jObjError.getString("error").startsWith("Invalid Data: 01")){
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
        } catch (Exception e){
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HomeActivity.this, R.layout.layout_spinner_text, questionList){
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

    private void getDetailProfil(){
        pullToRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getDetailLocation(loginStockLocation.location_id, "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<DetailLocationResponse>>() {
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
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DetailLocationResponse>> call, Throwable t) {
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void getPenjualanPendapatan() {
        summaryList = new ArrayList();
        pullToRefresh.setRefreshing(true);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd");
        String format = s1.format(new Date());
        try {
            ApiLocal.apiInterface().getLastTransaction("2", loginStockLocation.location_id, format, "Bearer "+ PreferenceManager.getSessionToken()).
                    enqueue(new Callback<ApiResponse<List<Transactions>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                            pullToRefresh.setRefreshing(false);
                            try {
                                List<Transactions> res = response.body().getData();

                                for (int i = 0; i < res.size(); i++) {
                                    Transactions transaction = res.get(i);
                                    sum += Long.parseLong(transaction.getTotalPrice());
                                }

                                String mAmoun = String.valueOf(sum);
                                amount = Integer.parseInt(mAmoun);

                                long amounts = Long.parseLong(mAmoun);
//                            rest = (amounts / 100) * 90;
                                rest = amounts;

                                SummaryReport summary1 = new SummaryReport();
                                SummaryReport summary3 = new SummaryReport();

                                summary1.report_name = "Penjualan";
                                summary1.report_value = "Rp. " + MethodUtil.toCurrencyFormat(amount + "");
                                summary1.report_period = "Hari ini";

                                summary3.report_name = "Pendapatan";
                                summary3.report_value = "Rp. " + MethodUtil.toCurrencyFormat(rest + "");
                                summary3.report_period = "Hari ini";

                                summaryList.add(summary1);
                                summaryList.add(summary3);

                                mAdapter.setData(summaryList);

                                Log.d("TAG SUM", amount + " " + rest);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                            pullToRefresh.setRefreshing(false);
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }

        pullToRefresh.setRefreshing(true);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s11 = new SimpleDateFormat("MM");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s2 = new SimpleDateFormat("yyyy");
        String month = s11.format(new Date());
        String yeear = s2.format(new Date());

        try {
            ApiLocal.apiInterface().getMonthTransaction("2", loginStockLocation.location_id, month, yeear, "Bearer "+ PreferenceManager.getSessionToken()).
                    enqueue(new Callback<ApiResponse<List<Transactions>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                            pullToRefresh.setRefreshing(false);
                            try {
                                List<Transactions> res = response.body().getData();

                                for (int i = 0; i < res.size(); i++) {
                                    Transactions transaction = res.get(i);
                                    sumMonth += Long.parseLong(transaction.getTotalPrice());
                                }

                                String mAmoun = String.valueOf(sumMonth);
                                amountMonth = Integer.parseInt(mAmoun);

                                long amounts = Long.parseLong(mAmoun);
                                restMonth = amounts;
//                            restMonth = (amounts / 100) * 90;

                                SummaryReport summary2 = new SummaryReport();
                                SummaryReport summary4 = new SummaryReport();

                                summary2.report_name = "Penjualan";
                                summary2.report_value = "Rp. " + MethodUtil.toCurrencyFormat(amountMonth + "");
                                summary2.report_period = "Bulan ini";

                                summary4.report_name = "Pendapatan";
                                summary4.report_value = "Rp. " + MethodUtil.toCurrencyFormat(restMonth + "");
                                summary4.report_period = "Bulan ini";

                                summaryList.add(summary2);
                                summaryList.add(summary4);

                                mAdapter.setData(summaryList);

                                Log.d("TAG SUMMONTH", String.valueOf(sumMonth));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                            pullToRefresh.setRefreshing(false);
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }

        Log.d("TAG amount", "" + amount + " " + rest + " " + amountMonth + " " + restMonth);
        initComponent(amount, rest, amountMonth, restMonth);

    }

    private void loadTransactionsData() {
        transactionModels = new ArrayList<>();
        currentPage = 0;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = s1.format(new Date());
        Log.d("TAG", format1);

        try {
            ApiLocal.apiInterface().getLastTransaction("2", loginStockLocation.location_id, format1, "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<Transactions>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                    Log.d("TAG OnResponse", response.message());
                    try {
                        List<Transactions> res = response.body().getData();
                        if (res.size() < 1) {
                            recyclerViewEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            recyclerViewEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            for (int i = 0; i < res.size(); i++) {
                                Transactions transaction = res.get(i);
                                transactionModels.add(new TransactionModel(transaction.getId() + "",
                                        transaction.getLocationId(),
                                        transaction.getId() + "",
                                        transaction.getTransactionCode(),
                                        transaction.getCreatedAt(),
                                        transaction.getPaymentType() + "",
                                        transaction.getPaymentMethod() + "",
                                        transaction.getTotalPrice(),
                                        transaction.getTotalDiscount(),
                                        transaction.getStatus() + ""));
                            }

                            adapter = new TransactionAdapter(transactionModels, context);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                    layoutManager.getOrientation());

                            recyclerView.addItemDecoration(dividerItemDecoration);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);

                            transactionsAdapter.setDataList(transactionModels, context);
                            transactionsListView.setHasMoreItems(false);
                            setListViewHeightBasedOnChildren(transactionsListView);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                    Log.d("TAG OnFailure", t.getMessage());
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }

//        ArrayList<Transaction> dummyTransactions = new ArrayList<>();
//
//        Transaction transaction1 = new Transaction();
//        transaction1.transaction_code = "123456";
//        transaction1.stock_location = new StockLocation();
//        transaction1.stock_location.name = "Kantin RSHS Bandung";
//        transaction1.total_price = "Rp. 65.000";
//        transaction1.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction1);
//
//        Transaction transaction2 = new Transaction();
//        transaction2.transaction_code = "123456";
//        transaction2.stock_location = new StockLocation();
//        transaction2.stock_location.name = "Kantin RSHS Bandung";
//        transaction2.total_price = "Rp. 65.000";
//        transaction2.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction2);
//
//        Transaction transaction3 = new Transaction();
//        transaction3.transaction_code = "123456";
//        transaction3.stock_location = new StockLocation();
//        transaction3.stock_location.name = "Kantin RSHS Bandung";
//        transaction3.total_price = "Rp. 65.000";
//        transaction3.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction3);
//
//        Transaction transaction4 = new Transaction();
//        transaction4.transaction_code = "123456";
//        transaction4.stock_location = new StockLocation();
//        transaction4.stock_location.name = "Kantin RSHS Bandung";
//        transaction4.total_price = "Rp. 65.000";
//        transaction4.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction4);
//
//        Transaction transaction5 = new Transaction();
//        transaction5.transaction_code = "123456";
//        transaction5.stock_location = new StockLocation();
//        transaction5.stock_location.name = "Kantin RSHS Bandung";
//        transaction5.total_price = "Rp. 65.000";
//        transaction5.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction5);
//
//        Transaction transaction6 = new Transaction();
//        transaction6.transaction_code = "123456";
//        transaction6.stock_location = new StockLocation();
//        transaction6.stock_location.name = "Kantin RSHS Bandung";
//        transaction6.total_price = "Rp. 65.000";
//        transaction6.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction6);
//
//        transactionsAdapter.setDataList(dummyTransactions);
//        transactionsListView.setHasMoreItems(false);
//        setListViewHeightBasedOnChildren(transactionsListView);

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

        for (int i = 0; i < transactions.size(); i++) {
            TransactionResponse transaction1 = transactions.get(i);
            transactionModels.add(new TransactionModel(transaction1.getId(),
                    transaction1.getBusiness_id(),
                    transaction1.getTenant_id(),
                    transaction1.getOrder_no(),
                    transaction1.getOrder_date(),
                    transaction1.getPayment_type(),
                    transaction1.getPayment_method(),
                    transaction1.getAmount(),
                    transaction1.getDiscount(),
                    transaction1.getStatus()));
        }
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
}
