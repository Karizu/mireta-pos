package com.boardinglabs.mireta.standalone.modul.pickitems.payment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.adapter.DiscountDetailAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.PaymentMethodAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.PaymentTypeAdapter;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoLightTextView;
import com.boardinglabs.mireta.standalone.component.listener.ItemActionListener;
import com.boardinglabs.mireta.standalone.component.listener.ListActionListener;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.NetworkManager;
import com.boardinglabs.mireta.standalone.component.network.NetworkService;
import com.boardinglabs.mireta.standalone.component.network.entities.Item;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.GroupItem;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemPost;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.Variant;
import com.boardinglabs.mireta.standalone.component.network.entities.NewTransactionPost;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentAccountResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentMethodResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Transaction;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.TransactionResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.Transactions;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Constant;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.CommonInterface;
import com.boardinglabs.mireta.standalone.modul.home.HomeActivity;
import com.boardinglabs.mireta.standalone.modul.transactions.items.ItemsPresenter;
import com.boardinglabs.mireta.standalone.modul.transactions.items.ItemsView;
import com.boardinglabs.mireta.standalone.modul.transactions.items.pembayaran.PembayaranSuksesActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends BaseActivity implements ItemsView, CommonInterface, ItemActionListener, ListActionListener {

    private ItemsPresenter mPresenter;
    private NewTransactionPost transactionPost;
    private List<ItemPost> items;
    private int CASH_METHOD = 1;
    private int QRIS_METHOD = 2;
    private int TRANSFER_METHOD = 3;
    private int PAYMENT_METHOD = 0;
    private int PAYMENT_ACCOUNT = 0;
    private int PENDING = 1;
    private int SUCCESS = 2;
    private int STATUS = PENDING;
    private List<ItemVariants> orederditems;
    private long mTotal;
    private Dialog dialog;
    final int REQUEST_SCANNER = 999;
    final int LAUNCH_SECOND_ACTIVITY = 111;
    private long totalPrice;
    private int flag, saldo;
    private long nomBayar;
    private Context context;
    private Activity activity;
    private int transactionType, spesificMethod;
    private String paymentMethodId;

    @BindView(R.id.tvAmount)
    RobotoLightTextView tvAmount;
    @BindView(R.id.tvTotalAmount)
    TextView tvTotalAmount;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvKembalian)
    TextView tvKembalian;
    @BindView(R.id.tvNameTenant)
    TextView tvNameTenant;
    @BindView(R.id.tvBusinessAddress)
    TextView tvBusinessAddress;
    @BindView(R.id.tvOrderDate)
    TextView tvOrderDate;
    @BindView(R.id.btnBayar)
    Button btnBayar;
    @BindView(R.id.rvDiscountDetail)
    RecyclerView rvDiscountDetail;
    @BindView(R.id.layoutDiscount)
    LinearLayout layoutDiscount;
    @BindView(R.id.layoutTotalDiscount)
    LinearLayout layoutTotalDiscount;
    @BindView(R.id.tvTotalDiscount)
    TextView tvTotalDiscount;

    private long kembalian;
    private int jumlahLain = 0;

    @OnClick(R.id.btnPaymentMethod)
    void onClickPaymentMethod(){
//        getPaymentMethod();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ItemsPresenter(this, this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pembayaran;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("PEMBAYARAN");
        context = this;
        activity = this;

        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        transactionType = intent.getIntExtra("transaction_type", 0);
        spesificMethod = intent.getIntExtra("spesific_method", 0);
        mTotal = Long.parseLong(intent.getStringExtra("total"));
        tvTotalAmount.setText("Rp " + MethodUtil.toCurrencyFormat(Long.toString(mTotal)));

        tvNameTenant.setText(loginBusiness.name);
        tvBusinessAddress.setText(loginBusiness.address);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = s1.format(new Date());
        tvOrderDate.setText(format1);

        orederditems = new ArrayList<>();

        List<ItemVariants> itemVariantsList = new Gson().fromJson(json, new TypeToken<List<ItemVariants>>(){}.getType());
        orederditems.addAll(itemVariantsList);

        PaymentAdapter adapter = new PaymentAdapter(orederditems, context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        updateTotalBottom(true);
    }

    @Override
    protected void onCreateAtChild() {
        mPresenter = new ItemsPresenter(this, this);
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
            updateTotalBottom(false);
        }

        if (requestCode == LAUNCH_SECOND_ACTIVITY && resultCode == Activity.RESULT_OK) {
            paymentMethodId = data.getStringExtra("payment_method_id");

        }
    }

    @OnClick(R.id.btnBayar)
    void onClickButton() {
        if (spesificMethod > 0){
            STATUS = PENDING;
            btnBayar.setEnabled(true);
            PAYMENT_METHOD = spesificMethod;
            updateTotalBottom(false);
            createPayment();
            return;
        }

        setDialog(R.layout.dialog_payment_method);
        RecyclerView rvPaymentMethod = dialog.findViewById(R.id.rvPaymentMethod);
        Button btnSkip = dialog.findViewById(R.id.btnSkip);
        getPaymentMethod(rvPaymentMethod);

        btnSkip.setOnClickListener(v -> {
            sendToPayment(true);
        });
    }

    @SuppressLint("SetTextI18n")
    private void settingDialog(){
        setDialog(R.layout.layout_top_sheet);
        EditText etNominal = (EditText) dialog.findViewById(R.id.etNominalBayar);
        Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerPay);
        Button lanjut = (Button) dialog.findViewById(R.id.btnLanjut);
        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.layoutJumlahBayar);
        CheckedTextView checked1 = (CheckedTextView) dialog.findViewById(R.id.checked1);
        CheckedTextView checked2 = (CheckedTextView) dialog.findViewById(R.id.checked2);
        CheckedTextView checked3 = (CheckedTextView) dialog.findViewById(R.id.checked3);
        CheckedTextView checked4 = (CheckedTextView) dialog.findViewById(R.id.checked4);
        CheckedTextView checked5 = (CheckedTextView) dialog.findViewById(R.id.checked5);
        CheckedTextView checked6 = (CheckedTextView) dialog.findViewById(R.id.checked6);
        CheckedTextView checked7 = (CheckedTextView) dialog.findViewById(R.id.checked7);

        checked1.setOnClickListener(v -> {
            checked1.setChecked(true); checked4.setChecked(false); etNominal.setVisibility(View.GONE);
            checked2.setChecked(false); checked5.setChecked(false); flag = 1;
            checked3.setChecked(false); checked6.setChecked(false); checked7.setChecked(false);
        });

        checked2.setOnClickListener(v -> {
            checked1.setChecked(false); checked4.setChecked(false); etNominal.setVisibility(View.GONE);
            checked2.setChecked(true); checked5.setChecked(false); flag = 2;
            checked3.setChecked(false); checked6.setChecked(false); checked7.setChecked(false);
        });

        checked3.setOnClickListener(v -> {
            checked1.setChecked(false); checked4.setChecked(false); etNominal.setVisibility(View.GONE);
            checked2.setChecked(false); checked5.setChecked(false); flag = 3;
            checked3.setChecked(true); checked6.setChecked(false); checked7.setChecked(false);
        });

        checked4.setOnClickListener(v -> {
            checked1.setChecked(false); checked4.setChecked(true); etNominal.setVisibility(View.GONE);
            checked2.setChecked(false); checked5.setChecked(false); flag = 4;
            checked3.setChecked(false); checked6.setChecked(false); checked7.setChecked(false);
        });

        checked5.setOnClickListener(v -> {
            checked1.setChecked(false); checked4.setChecked(false); etNominal.setVisibility(View.GONE);
            checked2.setChecked(false); checked5.setChecked(true); flag = 5;
            checked3.setChecked(false); checked6.setChecked(false); checked7.setChecked(false);
        });

        checked6.setOnClickListener(v -> {
            checked1.setChecked(false); checked4.setChecked(false); etNominal.setVisibility(View.GONE);
            checked2.setChecked(false); checked5.setChecked(false); flag = 6;
            checked3.setChecked(false); checked6.setChecked(true); checked7.setChecked(false);
        });

        checked7.setOnClickListener(v -> {
            checked1.setChecked(false); checked4.setChecked(false); checked7.setChecked(true);
            checked2.setChecked(false); checked5.setChecked(false); etNominal.setVisibility(View.VISIBLE);
            checked3.setChecked(false); checked6.setChecked(false); flag = 7;
        });

        lanjut.setOnClickListener(v -> {
            nomBayar = 0;
            switch (flag){
                case 1:
                    nomBayar = mTotal;
                    break;
                case 2:
                    nomBayar = 5000;
                    break;
                case 3:
                    nomBayar = 10000;
                    break;
                case 4:
                    nomBayar = 20000;
                    break;
                case 5:
                    nomBayar = 50000;
                    break;
                case 6:
                    nomBayar = 100000;
                    break;
                case 7:
                    break;
            }

            if (nomBayar != 0){
                tvKembalian.setVisibility(View.VISIBLE);

                try {
                    long total = mTotal;
                    total = (nomBayar - total);
                    kembalian = total;
                    if (total >= 0) {
                        dialog.dismiss();
                        btnBayar.setEnabled(true);
                        tvKembalian.setText("Kembalian : Rp "+MethodUtil.toCurrencyFormat(Long.toString(total)) + "");
                    } else {
                        btnBayar.setEnabled(false);
                        Toast.makeText(context, "Nominal yang dibayarkan kurang dari total harga", Toast.LENGTH_LONG).show();
                        tvKembalian.setText("Kembalian : Rp "+MethodUtil.toCurrencyFormat("0") + "");
                    }
                } catch (Exception e) {
                    tvKembalian.setText(" ");
                }
            } else {

                jumlahLain = Integer.parseInt(etNominal.getText().toString().replace(".",""));

                if (jumlahLain >= mTotal) {
                    dialog.dismiss();
                    long total = mTotal;
                    total = (jumlahLain - total);
                    tvKembalian.setText("Kembalian : Rp "+MethodUtil.toCurrencyFormat(Long.toString(total)) + "");
                    return;
                }

                if (PAYMENT_METHOD != CASH_METHOD) {
                    dialog.dismiss();
                    btnBayar.setEnabled(true);
                    tvKembalian.setText("");
                    return;
                }

                Toast.makeText(context, "Masukan nominal pembayaran dengan benar", Toast.LENGTH_SHORT).show();
            }
        });

        getPaymentMethod(spinner, layout);
        MethodUtil.setCurrency(etNominal);
//        etNominal.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                try {
//                    long total = mTotal;
//                    total = (Integer.parseInt(etNominal.getText().toString()) - total);
//                    if (total >= 0) {
//                        jumlahLain = Integer.parseInt(etNominal.getText().toString());
//                        btnBayar.setEnabled(true);
//                        tvKembalian.setText("Kembalian : Rp "+MethodUtil.toCurrencyFormat(Long.toString(total)) + "");
//                    } else {
//                        btnBayar.setEnabled(false);
//                        tvKembalian.setText("Kembalian : Rp "+MethodUtil.toCurrencyFormat("0") + "");
//                    }
//                } catch (Exception e) {
//                    tvKembalian.setText(" ");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    private void getPaymentMethod(Spinner spinner, LinearLayout layout){
        Loading.show(context);
        ApiLocal.apiInterface().getPaymentMethods("asc", "id", loginStockLocation.location_id, PreferenceManager.getOperationData().getId() + "", "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<PaymentMethodResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PaymentMethodResponse>>> call, Response<ApiResponse<List<PaymentMethodResponse>>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        // Spinner Drop down elements
                        List<String> method = new ArrayList<String>();
                        List<Integer> idList = new ArrayList<>();
                        List<PaymentMethodResponse> methodResponse = Objects.requireNonNull(response.body()).getData();
                        for (PaymentMethodResponse data: methodResponse){
                            method.add(data.getName());
                            idList.add(data.getId());
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PaymentActivity.this, R.layout.layout_spinner_text, method);
                        dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
                        spinner.setAdapter(dataAdapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String itemMethod = spinner.getItemAtPosition(position).toString();
                                if (itemMethod.equals("Tunai")){
                                    PAYMENT_METHOD = idList.get(position);
                                    STATUS = SUCCESS;
                                    layout.setVisibility(View.VISIBLE);
                                } else if (itemMethod.equals("QRIS") || itemMethod.contains("Transfer")){
                                    PAYMENT_METHOD = idList.get(position);
                                    STATUS = PENDING;
                                    layout.setVisibility(View.GONE);
                                    nomBayar = mTotal;
                                } else {
                                    PAYMENT_METHOD = idList.get(position);
                                    STATUS = SUCCESS;
                                    layout.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        Toast.makeText(context, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PaymentMethodResponse>>> call, Throwable t) {
                Loading.hide(context);
            }
        });
    }

    private void getPaymentMethod(RecyclerView rvPaymentMethod){
        Loading.show(context);
        ApiLocal.apiInterface().getPaymentMethods("asc", "id", loginStockLocation.location_id, PreferenceManager.getOperationData().getId() + "", "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<PaymentMethodResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PaymentMethodResponse>>> call, Response<ApiResponse<List<PaymentMethodResponse>>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        List<PaymentMethodResponse> methodResponseList = response.body().getData();
                        PaymentMethodAdapter adapter = new PaymentMethodAdapter(methodResponseList, activity, false);
                        rvPaymentMethod.setAdapter(adapter);
                    } else {
                        Toast.makeText(context, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PaymentMethodResponse>>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    public void setDialogPaymentAccount(int id){
        setDialog(R.layout.dialog_payment_method);
        RecyclerView rvPaymentMethod = dialog.findViewById(R.id.rvPaymentMethod);
        Button btnSkip = dialog.findViewById(R.id.btnSkip);
        LinearLayout layoutOr = dialog.findViewById(R.id.layoutOr);
        getPaymentAccount(id, rvPaymentMethod);

        btnSkip.setVisibility(View.GONE);
        layoutOr.setVisibility(View.GONE);
    }

    private void getPaymentAccount(int id, RecyclerView rvPaymentMethod) {
        Loading.show(context);
        ApiLocal.apiInterface().getPaymentAccounts(id+"", "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<PaymentAccountResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PaymentAccountResponse>>> call, Response<ApiResponse<List<PaymentAccountResponse>>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()) {
                        List<PaymentAccountResponse> accountResponseList = response.body().getData();
                        if (accountResponseList.size() > 0) {
                            List<PaymentMethodResponse> methodResponseList = new ArrayList<>();

                            for (PaymentAccountResponse accountResponse : accountResponseList) {
                                PaymentMethodResponse methodResponse = new PaymentMethodResponse();
                                methodResponse.setId(accountResponse.getId());
                                methodResponse.setName(accountResponse.getAccountBankName());
                                methodResponse.setCreatedAt(accountResponse.getCreatedAt());
                                methodResponseList.add(methodResponse);
                            }

                            PaymentMethodAdapter adapter = new PaymentMethodAdapter(methodResponseList, activity, true);
                            rvPaymentMethod.setAdapter(adapter);

                        } else {
                            sendToPayment(false);
                        }
                    } else {
                        Toast.makeText(context, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PaymentAccountResponse>>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    public void setPaymentMethod(int paymentMethod) {
        this.PAYMENT_METHOD = paymentMethod;
    }

    public void setPaymentAccount(int paymentAccount) {
        this.PAYMENT_ACCOUNT = paymentAccount;
    }

    public void sendToPayment(boolean isSkip) {
        if (isSkip) {
            PAYMENT_METHOD = 0;
        }

        updateTotalBottom(false);
        createPayment();
    }

    private void updateTotalBottom(boolean isCallInq) {
        long totalQty = 0;
        items = new ArrayList<>();
        totalPrice = 0;
        for (int i = 0; i < orederditems.size(); i++) {
            ItemVariants variants = orederditems.get(i);
            List<GroupItem> groupItemList = new ArrayList<>();

            List<Variant> variantList = variants.getVariants();
            for (Variant variant : variantList) {
                if (variant.getIsFixedQty() == 1) {
                    variant.setSumQty(0);
                }
                if (variant.getIsSumQty() == 1) {
                    variant.setFixedQty(0);
                }
                if (variant.getIsCustomisable() == 1){
                    variant.setItem(new com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.Item());
                } else {
                    variant.setGroupItems(groupItemList);
                }
            }
            int salesPrice = Integer.parseInt(variants.getPrice()) - Integer.parseInt(variants.getDiscountPrice());
            items.add(new ItemPost(
                    variants.getId(),
                    variants.getOrderQty(),
                    variants.getName(),
                    variants.getPrice(),
                    variants.getBuyPrice(),
                    salesPrice + "",
                    variants.getStock(),
                    variantList
                    ));
            totalQty += orederditems.get(i).getOrderQty();
            long total_price = (long) (Integer.parseInt(orederditems.get(i).getPrice()) * orederditems.get(i).getOrderQty());
            totalPrice += total_price;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("yyMMddHHmmss");
        String format = s.format(new Date());

        final int min = 10000;
        final int max = 99999;
        final int random = new Random().nextInt((max - min) + 1) + min;

        Log.d("NUM RANDOM", random + "");
        format = "M" + format + random;

        totalPrice = mTotal;

        String location_operation_id = PreferenceManager.getOperationData().getId() + "";
        Log.d("LocationId", location_operation_id);

        transactionPost = new NewTransactionPost(loginStockLocation.location_id,
                format+"",
                totalQty + 0,
                0,
                totalPrice+"",
                1,
                PAYMENT_METHOD==0? "":PAYMENT_METHOD+"",
                STATUS + 0,
                items,
                location_operation_id + "",
                transactionType+"",
                PAYMENT_ACCOUNT == 0 ? "" : PAYMENT_ACCOUNT+""
        );

        if (isCallInq) getInquiryTransaction();
    }

    private void getInquiryTransaction(){
        Loading.show(context);
        ApiLocal.apiInterface().getInqTransactions(transactionPost, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<Transactions>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<Transactions>> call, Response<ApiResponse<Transactions>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()) {
                        Transactions transaction = response.body().getData();
                        if (transaction.getDiscounts().size() > 0) {
                            tvTotalDiscount.setText("-Rp " + MethodUtil.toCurrencyFormat(transaction.getTotalDiscount()));
                            DiscountDetailAdapter adapter = new DiscountDetailAdapter(transaction.getDiscounts(), context);
                            rvDiscountDetail.setAdapter(adapter);
                        } else {
                            layoutDiscount.setVisibility(View.GONE);
                            layoutTotalDiscount.setVisibility(View.GONE);
                        }
                    } else {
                        String msg = MethodUtil.getErrorResponse(response.errorBody().string());
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        layoutDiscount.setVisibility(View.GONE);
                        layoutTotalDiscount.setVisibility(View.GONE);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Transactions>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
                layoutDiscount.setVisibility(View.GONE);
                layoutTotalDiscount.setVisibility(View.GONE);
            }
        });
    }

    private void createPayment() {
        Loading.show(PaymentActivity.this);
        ApiLocal.apiInterface().createTransactions(transactionPost, "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<TransactionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TransactionResponse>> call, Response<ApiResponse<TransactionResponse>> response) {
                Loading.hide(PaymentActivity.this);
                try {
                    if (response.isSuccessful()){
                        TransactionResponse apiResponse = response.body().getData();

                        Date d = null;
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            d = sdf.parse(apiResponse.getCreatedAt());
                        } catch (ParseException ex) {
                            Log.v("Exception", ex.getLocalizedMessage());
                        }
                        sdf.applyPattern("yyyy-MM-dd");
                        sd.applyPattern("HH:mm");

                        String order_no = apiResponse.getId()+"";
                        String total = totalPrice + "";
                        Log.d("DATA TRX", order_no + " " + total);
                        Intent intent = new Intent(PaymentActivity.this, PembayaranSuksesActivity.class);
                        intent.putExtra("payment_method", PAYMENT_METHOD);
                        intent.putExtra("order_no", order_no);
                        intent.putExtra("order_date", sdf.format(d));
                        intent.putExtra("order_time", sd.format(d));
                        intent.putExtra("total", total);
                        intent.putExtra("nomBayar", nomBayar+"");
                        intent.putExtra("mTotal", mTotal+"");
                        intent.putExtra("kembalian", kembalian+"");
                        intent.putExtra("whatToDo", Constant.DO_PRINT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        String errorMessage = MethodUtil.getErrorResponse(response.errorBody().string());
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TransactionResponse>> call, Throwable t) {
                Log.d("TAG onFailure POS", t.getMessage());
                Loading.hide(PaymentActivity.this);
            }
        });
    }

    @Override
    public void itemClicked(int position) {

    }

    @Override
    public void itemDeleted(int position) {

    }

    @Override
    public void itemAdd(int position) {

    }

    @Override
    public void itemMinus(int position) {

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
    public void onSuccessGetItems(List<Item> transactionItems) {

    }

    @Override
    public void onSuccessGetNewItems(List<ItemVariants> transactionItems) {

    }

    @Override
    public void onSuccessCreateTransaction(ResponseBody responseBody) {
        Log.d("TAG SUKSES", String.valueOf(responseBody));
        Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setDialog(int layout) {

        dialog = new Dialog(Objects.requireNonNull(PaymentActivity.this));
        //set content
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }
}
