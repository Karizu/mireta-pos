package com.boardinglabs.mireta.standalone.modul.history;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.BluetoothHandler;
import com.boardinglabs.mireta.standalone.component.DeviceActivity;
import com.boardinglabs.mireta.standalone.component.PrinterCommands;
import com.boardinglabs.mireta.standalone.component.adapter.DetailTransactionAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.DiscountDetailAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.PaymentAccountAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.PaymentMethodAdapter;
import com.boardinglabs.mireta.standalone.component.network.Api;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.NetworkService;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentAccountResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentMethodResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionDetailModel;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionHeader;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.Detail;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.DiscountResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.TransactionResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.Transactions;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Constant;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.component.util.Utils;
import com.boardinglabs.mireta.standalone.modul.home.HomeActivity;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.wizarpos.apidemo.printer.ESCPOSApi;
import com.wizarpos.apidemo.printer.FontSize;
import com.wizarpos.apidemo.printer.PrintSize;
import com.zj.btsdk.BluetoothService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTransactionActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, BluetoothHandler.HandlerInterface {

    private static final int SUCCESS = 2;
    private static final int PENDING = 1;
    private static final int FAILED = 3;
    private List<TransactionDetailModel> transactionDetails;
    private DetailTransactionAdapter adapter;
    private PaymentAccountAdapter accountAdapter;
    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;
    private boolean isPrinterReady = false;
    private BluetoothDevice mDevice;
    private BluetoothService mService = null;
    private TransactionHeader transactionHeader;
    private Transactions transaction;
    private List<Detail> res;
    private boolean isFromReport = false;

    private Bitmap selectedImage;
    private Bitmap photoImage;
    private static final int CAMERA_REQUEST_CODE = 1111;
    private final int REQEUST_CAMERA = 1, REQUEST_GALLERY = 2222;
    private File imageCheck;

    private PrinterDevice printerDevice;
    private Format format;
    private TextView txt;
    private String str, manufacturer, model;
    private Context context = DetailTransactionActivity.this;
    private String whatToDo, sisaSaldo;
    private String order_date, order_time, member_name, member_lulusan, member_angkatan, sKembalian, mNomBayar = null;
    private int PAYMENT_METHOD = 0;
    private int PAYMENT_ACCOUNT = 0;
    private int CASH_METHOD = 1;
    private int QRIS_METHOD = 2;
    private int TRANSFER_METHOD = 3;
    private String order_no, total;
    private Activity activity = DetailTransactionActivity.this;

    @BindView(R.id.tvNameTenant)
    TextView tvNameTenant;
    @BindView(R.id.tvBusinessPhone)
    TextView tvBusinessPhone;
    @BindView(R.id.tvAmounts)
    TextView tvAmount;
    @BindView(R.id.tvBusinessAddress)
    TextView tvBusinessAddress;
    @BindView(R.id.tvDiscount)
    TextView tvDiscount;
    @BindView(R.id.layoutDiscount)
    LinearLayout layoutDiscount;
    @BindView(R.id.tvOrderDate)
    TextView tvOrderDate;
    @BindView(R.id.tvStatusOrder)
    TextView tvStatusOrder;
    @BindView(R.id.tvOrderNo)
    TextView tvOrderNo;
    @BindView(R.id.tvSubTotal)
    TextView tvSubTotal;
    @BindView(R.id.btnBayar)
    Button btnBayar;
    @BindView(R.id.btnPrintCopy)
    Button btnPrintCopy;
    @BindView(R.id.btnPrint)
    Button btnPrint;
    @BindView(R.id.btnVoid)
    Button btnVoid;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.tvPaymentMethod)
    TextView tvPaymentMethod;
    @BindView(R.id.rvPaymentMethod)
    RecyclerView rvPaymentMethod;
    @BindView(R.id.titleMethodPayment)
    TextView titleMethodPayment;
    @BindView(R.id.btnKonfirmasi)
    Button btnKonfirmasi;
    @BindView(R.id.tvPaymentMethodName)
    TextView tvPaymentMethodName;
    @BindView(R.id.tvPaymentType)
    TextView tvPaymentType;
    @BindView(R.id.btnUploadImage)
    Button btnUploadImage;
    @BindView(R.id.rvDiscountDetail)
    RecyclerView rvDiscountDetail;

    private Dialog dialog;
    private int loop = 0;
    private boolean isButtonPrintClicked = false;

    @OnClick(R.id.btnUpdateNetPrice)
    void onClickBtnUpdateNetPrice(){
        showDialogLayout(R.layout.dialog_update_price);
        EditText etNominal = dialog.findViewById(R.id.etNominal);
        Button btnProses = dialog.findViewById(R.id.btnProses);

        MethodUtil.setCurrency(etNominal);

        btnProses.setOnClickListener(v -> {
            int nominal = Integer.parseInt(etNominal.getText().toString().replace(".", ""));
            int total_price = Integer.parseInt(transaction.getTotalPrice());
            int total_discount = Integer.parseInt(transaction.getTotalDiscount());

            int thirdPartyFeeTax = total_price - total_discount - nominal;

            updateNetPriceTransaction(order_no, etNominal.getText().toString(), String.valueOf(thirdPartyFeeTax));
            dialog.dismiss();
        });
    }

    @OnClick(R.id.btnUploadImage)
    void onClickUploadImage(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    @OnClick(R.id.btnKonfirmasi)
    void btnKonfirmasi(){
//        selectImage();
        if (PAYMENT_METHOD == TRANSFER_METHOD || PAYMENT_METHOD == QRIS_METHOD) {
            selectImage();
        } else if (PAYMENT_METHOD == 0) {
            showDialogLayout(R.layout.dialog_payment_method);
            RecyclerView rvPaymentMethod = dialog.findViewById(R.id.rvPaymentMethod);
            Button btnSkip = dialog.findViewById(R.id.btnSkip);
            LinearLayout layoutOr = dialog.findViewById(R.id.layoutOr);
            getPaymentMethod(rvPaymentMethod);

            btnSkip.setVisibility(View.GONE);
            layoutOr.setVisibility(View.GONE);
        } else {
            updateTransaction(order_no, SUCCESS + "");
        }
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.btnVoid)
    void onClickVoid() {
        showDialogLayout(R.layout.layout_input_password);
        TextView tvDesc = dialog.findViewById(R.id.etMasterKey);
        EditText etPassword = dialog.findViewById(R.id.etPassword);
        Button btnProses = dialog.findViewById(R.id.btnProses);
        etPassword.setVisibility(View.GONE);
        tvDesc.setText("Apakah anda yakin ingin membatalkan pesanan?");
        btnProses.setOnClickListener(v -> {
            dialog.dismiss();
            updateTransaction(order_no, FAILED+"");
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_detail_transaction;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setToolbarTitle("Detail Transaksi");
        isFromReport = getIntent().getBooleanExtra("isFromReport", false);
        try {
            System.out.println(PreferenceManager.getBitmapHeader());
        } catch (Exception e){
            e.printStackTrace();
        }

        transactionDetails = new ArrayList<>();

        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;

        printerDevice = (PrinterDevice) POSTerminal.getInstance(getApplicationContext()).getDevice(
                "cloudpos.device.printer");

        Intent intent = getIntent();
        order_no = null;
        total = null;
        try {
            PAYMENT_METHOD = intent.getIntExtra("payment_method", 0);
            order_date = intent.getStringExtra("order_date");
            order_time = intent.getStringExtra("order_time");
            sKembalian = intent.getStringExtra("kembalian");
            mNomBayar = intent.getStringExtra("nomBayar");
            member_name = intent.getStringExtra("member_name");
            member_lulusan = intent.getStringExtra("member_lulusan");
            member_angkatan = intent.getStringExtra("member_angkatan");
            order_no = intent.getStringExtra("order_no");
            total = intent.getStringExtra("total");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            sisaSaldo = intent.getStringExtra("sisaSaldo");
        } catch (Exception e) {
            e.printStackTrace();
        }

        whatToDo = null;
        try {
            whatToDo = intent.getStringExtra("whatToDo");
        } catch (Exception e) {
            e.printStackTrace();
        }

        long mTotal = Long.parseLong(total);
        if (order_no != null) {
            refreshLayout.setRefreshing(true);
            getDetailTransaction(order_no);
            tvNameTenant.setText(loginBusiness.name);
            tvBusinessAddress.setText(loginBusiness.address);
            tvBusinessPhone.setText(loginStockLocation.location.getPhone() != null ? loginStockLocation.location.getPhone() : "-");
        }

        String finalOrder_no = order_no;
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
        });
        setupBluetooth();
    }

    private Handler handler = new Handler();

    private Runnable myRunnable = () -> {
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
    };

    @Override
    protected void onCreateAtChild() {

    }

    @Override
    protected void onBackBtnPressed() {
        if (whatToDo != null) {
            Intent intent = new Intent(DetailTransactionActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromReport) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(DetailTransactionActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onSubmitBtnPressed() {

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
        showDialogLayout(R.layout.dialog_payment_method);
        RecyclerView rvPaymentMethod = dialog.findViewById(R.id.rvPaymentMethod);
        Button btnSkip = dialog.findViewById(R.id.btnSkip);
        LinearLayout layoutOr = dialog.findViewById(R.id.layoutOr);
        getPaymentAccountFromDialog(id, rvPaymentMethod);

        btnSkip.setVisibility(View.GONE);
        layoutOr.setVisibility(View.GONE);
    }

    public void setPaymentMethod(int paymentMethod) {
        this.PAYMENT_METHOD = paymentMethod;
    }

    public void setPaymentAccount(int paymentAccount) {
        this.PAYMENT_ACCOUNT = paymentAccount;
    }

    public void sendToPayment(boolean thereIsNoPaymentAccount) {
        if (thereIsNoPaymentAccount) {
            PAYMENT_ACCOUNT = 0;
            updateTransactionWithPaymentMethod(order_no, SUCCESS + "");
        } else {
            updateTransactionWithPaymentMethod(order_no,  PENDING + "");
//            selectImage();
        }
    }

    private void getPaymentAccountFromDialog(int id, RecyclerView rvPaymentMethod) {
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
                            sendToPayment(true);
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

    private void getPaymentAccounts(int PAYMENT_METHOD, int mStatus){
        Loading.show(context);
        ApiLocal.apiInterface().getPaymentAccounts(PAYMENT_METHOD + "", "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<PaymentAccountResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PaymentAccountResponse>>> call, Response<ApiResponse<List<PaymentAccountResponse>>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        List<PaymentAccountResponse> responseList = Objects.requireNonNull(response.body()).getData();
                        if (responseList.size() > 0) {
                            String paymentMethod = responseList.get(0).getPaymentMethod().getName();

                            tvPaymentMethod.setText(paymentMethod);
                            accountAdapter = new PaymentAccountAdapter(responseList, context);
                            rvPaymentMethod.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                            rvPaymentMethod.setAdapter(accountAdapter);

                            tvPaymentMethod.setVisibility(View.VISIBLE);
                            titleMethodPayment.setVisibility(View.VISIBLE);
                            btnKonfirmasi.setVisibility(View.GONE);

                            if (mStatus == PENDING){
                                btnUploadImage.setVisibility(View.VISIBLE);

                                showDialogLayout(R.layout.dialog_upload_image);
                                Button btnUpload = dialog.findViewById(R.id.btnUpload);
                                Button btnSkip = dialog.findViewById(R.id.btnSkip);

                                btnUpload.setOnClickListener(v -> {
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                            == PackageManager.PERMISSION_DENIED) {
                                        ActivityCompat.requestPermissions(activity,
                                                new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                                    } else {
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
                                    }
                                });
                                btnSkip.setOnClickListener(v -> dialog.dismiss());
                            }
                        }
                    } else {
                        Toast.makeText(context, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
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

    private void reLogin(){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "merchant")
                .addFormDataPart("password", "123456")
                .build();

        Api.apiInterface().loginArdi(requestBody).enqueue(new Callback<ApiResponse<com.boardinglabs.mireta.standalone.component.network.entities.LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.boardinglabs.mireta.standalone.component.network.entities.LoginResponse>> call, Response<ApiResponse<com.boardinglabs.mireta.standalone.component.network.entities.LoginResponse>> response) {
                if (response.isSuccessful()){
                    PreferenceManager.setSessionTokenArdi(Objects.requireNonNull(response.body()).getAccess_token());
                    PreferenceManager.setUserIdArdi(response.body().getData().getId());
                    Log.d("TAG", "MASUK LOGIN ARDI");
                    doVoidArdi();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.boardinglabs.mireta.standalone.component.network.entities.LoginResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void doVoid() {
        Loading.show(context);
        Log.d("TRX ID", transaction.getId()+"");
        ApiLocal.apiInterface().doVoid(transaction.getId()+"", "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    doVoidArdi();
                    Log.d("TAG", "VOID MIRETA SUKSES");
                    Toast.makeText(context, "Berhasil membatalkan transaksi", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailTransactionActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);
                } else {
                    try {
                        Toast.makeText(context, Objects.requireNonNull(response.body()).getError() != null ? response.body().getError() : response.message(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void doVoidArdi() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = s1.format(new Date());

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("transaction_code", transaction.getTransactionCode())
                .addFormDataPart("user_id", PreferenceManager.getUserIdArdi())
                .addFormDataPart("date", date)
                .build();

        Api.apiInterface().doVoidArdi(requestBody, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Loading.hide(context);
                    Toast.makeText(context, "Berhasil membatalkan transaksi", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailTransactionActivity.this, HistoryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);
                } else {
                    loop++;
                    Log.d("TAG", "MASUK LOOPING VOID ARDI");
                    if (loop == 5){
                        try {
                            Loading.hide(context);
                            if (response.message().equals("Unauthorized")){
                                reLogin();
                                Toast.makeText(context, Objects.requireNonNull(response.body()).getError() != null ? response.body().getError() : response.message(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e){
                            Loading.hide(context);
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        doVoidArdi();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
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

                            QRCodeWriter writer = new QRCodeWriter();
                            Bitmap bmp = null;
                            try {
                                BitMatrix bitMatrix = writer.encode(transaction.getTransactionCode(), BarcodeFormat.QR_CODE, 300, 300);
                                int width = bitMatrix.getWidth();
                                int height = bitMatrix.getHeight();
                                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                                for (int x = 0; x < width; x++) {
                                    for (int y = 0; y < height; y++) {
                                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                    }
                                }
                                Log.d("TAG BITMAP", "MASUK BITMAP");

                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                            format.setParameter("align", "center");
                            format.setParameter("bold", "true");
                            format.setParameter("size", "medium");

                            try {
                                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pd_logo_resi);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                printerDevice.printBitmap(format, bitmap);
                                printerDevice.printText("\n \n");
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                            String metodeBayar = transaction.getPaymentMethodObj()!=null?transaction.getPaymentMethodObj().getName():"-";
                            String tipeTransaksi = transaction.getTransactionTypeObj()!=null?transaction.getTransactionTypeObj().getName():"-";

                            printerDevice.printText(format, loginBusiness.name.toUpperCase() + "\n" +
                                    loginBusiness.address + "\n");
                            printerDevice.printlnText(format, loginStockLocation.telp != null ? loginStockLocation.telp + "\n": "-" + "\n");
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, getPrintLabelValue("Kasir", loginUser.fullname, false, true));
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "--------------------------------\n");
                            printerDevice.printlnText(format, getPrintLabelValue(order_date, order_time, false, true));
                            printerDevice.printText(format, getPrintLabelValue("Status:", tvStatusOrder.getText().toString(), false, true));
                            printerDevice.printText(format, getPrintLabelValue("Order No:", transaction.getTransactionCode(), false, true));
                            printerDevice.printText(format, getPrintLabelValue("Metode Bayar:", metodeBayar, false, true));
                            printerDevice.printText(format, getPrintLabelValue("Tipe Transaksi:", tipeTransaksi, false, true));
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "--------------------------------\n");
                            try {
                                printerDevice.printlnText(format, member_name);
                                printerDevice.printlnText(format, member_lulusan);
                                printerDevice.printlnText(format, member_angkatan);
                                printerDevice.printText(format, "--------------------------------\n");
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                            int mTotal = Integer.parseInt(transaction.getTotalPrice());
                            String item, grandPrice;

                            for (int i = 0; i < res.size(); i++) {

                                Detail detail = res.get(i);
                                int mAmount = Integer.parseInt(detail.getTotalPrice());
                                int mQty = Integer.parseInt(detail.getQty());
                                int mGrandPrice = mAmount * mQty;
                                int mDiskon = Integer.parseInt(detail.getItemDiscount()) * mQty;
                                int netPrice = Integer.parseInt(detail.getSalesPrice()) * mQty;
                                String detailItem = detail.getDescription();

                                item = detail.getStock().getItem().getName();
                                String qty = detail.getQty() + "x";
                                String price = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mAmount);
                                grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandPrice);


                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, getPrintLabelValue(item + " " + qty, grandPrice, false, true));

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                if (mDiskon != 0) printerDevice.printText(format, getPrintLabelValue("Hemat -Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mDiskon), "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(netPrice), false, true));

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                if (!detailItem.equals("")) printerDevice.printlnText(format, "Detail:");
                                if (!detailItem.equals("")) printerDevice.printlnText(format, detailItem + "\n");
                            }

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");

                            printerDevice.printText(format, "--------------------------------\n");
                            printerDevice.printText(format, getPrintLabelValue("TOTAL :", "Rp. " + MethodUtil.toCurrencyFormat(transaction.getTotalNetPrice()), false, true));
                            if (!transaction.getTotalDiscount().equals("0")){
                                printerDevice.printText(format, "--------------------------------\n");
                                printerDevice.printText(format, getPrintLabelValue("Total Hemat", "-Rp. " + MethodUtil.toCurrencyFormat(transaction.getTotalDiscount()), false, true));
                            }
                            printerDevice.printText(format, "--------------------------------\n\n");

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, "Terimakasih sudah berbelanja");
                            try {
                                printerDevice.printlnText(format, member_name);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            printerDevice.printBitmap(format, bmp);
                            printerDevice.printlnText(format, "\n");
                            printerDevice.printlnText(format, "\n");

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
            output += "";
        }
        return output;
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

    @AfterPermissionGranted(RC_BLUETOOTH)
    private void setupBluetooth() {
        String[] params = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
        if (!EasyPermissions.hasPermissions(this, params)) {
            EasyPermissions.requestPermissions(this, "You need bluetooth permission",
                    RC_BLUETOOTH, params);
            return;
        }
        mService = new BluetoothService(this, new BluetoothHandler(this));
    }

    @OnClick(R.id.btnPrint)
    void printBtn() {
        if (manufacturer.equals("wizarPOS")) {
            closePrinter();
            printStruk();
        } else {
            isButtonPrintClicked = true;
            printText();
        }
    }

    public void printText() {
        if (!mService.isAvailable()) {
            Log.i("TAG", "printText: perangkat tidak support bluetooth");
            return;
        }

        if (isPrinterReady) {
            mService.write(PrinterCommands.CENTER_ALIGN);
            try {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.pd_logo_black_white, o);

                final int REQUIRED_WIDTH=150;
                final int REQUIRED_HIGHT=75;
                int scale=1;
                while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                    scale*=2;

                //Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize=scale;

//                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
//                        R.drawable.pd_logo_black_white, o2);
                Bitmap bmp = getBitmapFromURL(NetworkService.BASE_URL_IMAGE + PreferenceManager.getStockLocation().location.getBrand().getLogo_image_url());;
                if(bmp!=null){
                    byte[] command = Utils.decodeBitmap(bmp);
                    Log.d("byte", Arrays.toString(command) +"");
                    mService.write(command);
                    mService.sendMessage("\n", "");
                }else{
                    Log.e("Print Photo error", "the file isn't exists");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }

            String brandName = loginStockLocation.location.getBrand().getName();
            String locationName = loginStockLocation.location.getName();
            String locationAddress = loginStockLocation.location.getAddress();
            String locationPhone = loginStockLocation.location.getPhone()!=null?loginStockLocation.location.getPhone():"-";
            String cashierName = loginUser.fullname;
            String orderStatus = tvStatusOrder.getText().toString();
            String orderNo = transaction.getTransactionCode();
            String metodeBayar = transaction.getPaymentMethodObj()!=null?transaction.getPaymentMethodObj().getName():"-";
            String tipeTransaksi = transaction.getTransactionTypeObj()!=null?transaction.getTransactionTypeObj().getName():"-";

            mService.sendMessage(brandName, "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(locationName, "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(locationAddress, "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(locationPhone, "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage(getPrintBTLabelValue("Kasir", cashierName, false, true), "");
            mService.sendMessage("------------------------------------------", "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage(getPrintBTLabelValue(order_date, order_time, false, true), "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage(getPrintBTLabelValue("Status: ", orderStatus, false, true), "");
            mService.sendMessage(getPrintBTLabelValue("Order No: ", orderNo, false, true), "");
            mService.sendMessage(getPrintBTLabelValue("Metode Bayar:", metodeBayar, false, true), "");
            mService.sendMessage(getPrintBTLabelValue("Tipe Transaksi:", tipeTransaksi, false, true), "");
            mService.sendMessage("------------------------------------------", "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.write(PrinterCommands.ESC_ENTER);
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage("Detail Transaksi", "");
            mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");
            mService.write(PrinterCommands.LEFT_ALIGN);

            String item, grandPrice;

            for (int i = 0; i < res.size(); i++) {

                Detail detail = res.get(i);

                int mAmount = Integer.parseInt(detail.getTotalPrice());
                int mQty = Integer.parseInt(detail.getQty());
                int mGrandPrice = mAmount * mQty;
                int mDiskon = Integer.parseInt(detail.getItemDiscount()) * mQty;
                int netPrice = Integer.parseInt(detail.getSalesPrice()) * mQty;
                String detailItem = detail.getDescription();

                item = detail.getStock().getItem().getName();
                String qty = detail.getQty() + " x ";
                String price = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mAmount);
                grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandPrice);

                mService.write(PrinterCommands.LEFT_ALIGN);
                mService.sendMessage(item, ""); mService.write(PrinterCommands.ESC_ENTER);
                mService.sendMessage(getPrintBTLabelValue(qty + price, grandPrice, false, true), "");
                if (mDiskon != 0) mService.sendMessage(getPrintBTLabelValue("Hemat -Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mDiskon), "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(netPrice), false, true), "");

                if (detail.getStock().getItem().getIsUpPrice() == 0) {
                    if (!detailItem.equals("")){
                        mService.sendMessage("(" + detailItem + ")", "");
                    }
                }

                mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");
                mService.write(PrinterCommands.LEFT_ALIGN);
            }

            item = "------------------------------------------";

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(item, "");
            mService.write(PrinterCommands.ESC_ENTER);

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(getPrintBTLabelValue("TOTAL : ", "Rp. " + MethodUtil.toCurrencyFormat(transaction.getTotalNetPrice()), false, true), "");
            if (!transaction.getTotalDiscount().equals("0")){
                mService.sendMessage("------------------------------------------", "");
                mService.sendMessage(getPrintBTLabelValue("Total Hemat", "-Rp. " + MethodUtil.toCurrencyFormat(transaction.getTotalDiscount()), false, true), "");
                for(DiscountResponse response : transaction.getDiscounts()) {
                    mService.sendMessage(getPrintBTLabelValue("    " + response.getDiscountDescription(), "-Rp. " + MethodUtil.toCurrencyFormat(response.getTotalDiscount()), false, true), "");
                }
            }

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage("------------------------------------------", "");
            mService.sendMessage(getPrintBTLabelValue("   ", "   ", false, true), "");
            mService.sendMessage(getPrintBTLabelValue("   ", "   ", false, true), "");
            mService.sendMessage("Terimakasih sudah berbelanja\n\n", "");
            mService.sendMessage(getResources().getString(R.string.footer_print) + "\n\n\n", "");

            isButtonPrintClicked = false;
        } else {
            if (mService.isBTopen())
                startActivityForResult(new Intent(this, DeviceActivity.class), RC_CONNECT_DEVICE);
            else
                requestBluetooth();
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

    private void requestBluetooth() {
        if (mService != null) {
            if (!mService.isBTopen()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
            }
        }
    }

    private void getDetailTransaction(String transaction_id) {

        ApiLocal.apiInterface().getDetailTransaction(transaction_id, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new retrofit2.Callback<ApiResponse<Transactions>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<Transactions>> call, Response<ApiResponse<Transactions>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    transaction = response.body().getData();
                    res = transaction.getDetails();

                    for (int i = 0; i < res.size(); i++) {
                        Detail transactionDetail = res.get(i);
                        transactionDetails.add(new TransactionDetailModel(transactionDetail.getStock().getItem().getName(),
                                transactionDetail.getQty(),
                                transactionDetail.getSalesPrice(),
                                transactionDetail.getTotalPrice(),
                                transactionDetail.getItemDiscount(),
                                transactionDetail.getDescription()));
                    }

                    if (transaction.getDiscounts().size() > 0) {
                        DiscountDetailAdapter adapter = new DiscountDetailAdapter(transaction.getDiscounts(), context);
                        rvDiscountDetail.setAdapter(adapter);
                    }

                    Gson gson = new Gson();
                    String json = gson.toJson(transactionHeader);
                    Log.d("Json", json);

                    int mDiscount = Integer.parseInt(transaction.getTotalDiscount());
                    int netPrice = Integer.parseInt(transaction.getTotalNetPrice());
                    long amount = Long.parseLong(transaction.getTotalPrice());
                    int mStatus = Integer.parseInt(transaction.getStatus() + "");
                    PAYMENT_METHOD = transaction.getPaymentMethod()!=null?transaction.getPaymentMethod():0;

                    tvOrderNo.setText(transaction.getTransactionCode());
                    tvSubTotal.setText("Rp. " + MethodUtil.toCurrencyFormat(Long.toString(amount)));
                    tvAmount.setText("Rp. " + MethodUtil.toCurrencyFormat(String.valueOf(netPrice)));
                    if (mDiscount!=0){
                        tvDiscount.setText("-Rp. " + MethodUtil.toCurrencyFormat(String.valueOf(mDiscount)));
                    } else {
                        layoutDiscount.setVisibility(View.GONE);
                    }
                    tvOrderDate.setText(order_date + " " + order_time);
                    tvPaymentMethodName.setText(transaction.getPaymentMethodObj()!=null?transaction.getPaymentMethodObj().getName():"-");
                    tvPaymentType.setText(transaction.getTransactionTypeObj()!=null?transaction.getTransactionTypeObj().getName():"-");
                    switch (mStatus) {
                        case 1:
                            tvStatusOrder.setText("PENDING");
                            //
                            tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.RED));
                            btnBayar.setVisibility(View.GONE);
                            btnVoid.setVisibility(View.VISIBLE);
                            btnPrint.setVisibility(View.VISIBLE);
                            btnKonfirmasi.setVisibility(View.VISIBLE);
                            btnPrint.setText("Print Struk Pesanan");
                            getPaymentAccounts(PAYMENT_METHOD, mStatus);
                            break;
                        case 2:
                            getPaymentAccounts(PAYMENT_METHOD, mStatus);

                            tvStatusOrder.setText("BERHASIL");
                            tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Green));
                            btnBayar.setVisibility(View.GONE);
                            btnPrint.setVisibility(View.VISIBLE);
                            btnVoid.setVisibility(View.GONE);
                            btnUploadImage.setVisibility(View.GONE);

                            btnPrint.setText("Print Struk Pembayaran");
                            tvPaymentMethod.setVisibility(View.GONE);
                            titleMethodPayment.setVisibility(View.GONE);
                            btnKonfirmasi.setVisibility(View.GONE);
                            break;
                        case 3:
                            tvStatusOrder.setText("DIBATALKAN");
                            tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Red));
                            btnBayar.setVisibility(View.GONE);
                            btnPrint.setVisibility(View.GONE);
                            btnVoid.setVisibility(View.GONE);
                            btnUploadImage.setVisibility(View.GONE);

                            tvPaymentMethod.setVisibility(View.GONE);
                            titleMethodPayment.setVisibility(View.GONE);
                            btnKonfirmasi.setVisibility(View.GONE);
                            break;
                    }

                    adapter = new DetailTransactionAdapter(transactionDetails, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                    if (whatToDo != null) {
                        if (whatToDo.equals(Constant.DO_PRINT)) {
                            printBtn();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Transactions>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                Log.d("TAG onFailure", t.getMessage());
            }
        });
    }

    private void updateTransactionWithPaymentMethod(String id, String status){
        RequestBody requestBody;

        if (photoImage != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photoImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("status", status)
                    .addFormDataPart("payment_method", PAYMENT_METHOD+"")
                    .addFormDataPart("payment_account", PAYMENT_ACCOUNT+"")
                    .addFormDataPart("image", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                    .build();

        } else if (selectedImage != null) {
            File file = createTempFile(selectedImage);
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("status", status)
                    .addFormDataPart("payment_method", PAYMENT_METHOD+"")
                    .addFormDataPart("payment_account", PAYMENT_ACCOUNT+"")
                    .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                    .build();
        } else {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("status", status)
                    .addFormDataPart("payment_method", PAYMENT_METHOD+"")
                    .addFormDataPart("payment_account", PAYMENT_ACCOUNT+"")
                    .build();
        }

        Loading.show(context);
        ApiLocal.apiInterface().updateTransaction(id, requestBody, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<TransactionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TransactionResponse>> call, Response<ApiResponse<TransactionResponse>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        Intent intent = new Intent(DetailTransactionActivity.this, DetailTransactionActivity.class);
                        intent.putExtra("order_no", id);
                        intent.putExtra("total", total);
                        intent.putExtra("order_date", order_date);
                        intent.putExtra("order_time", order_time);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        String msg = MethodUtil.getErrorResponse(response.errorBody().string());
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan, silahkan periksa koneksi internet anda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TransactionResponse>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void updateNetPriceTransaction(String id, String totalNetPrice, String thirdPartyFeeTax) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("third_party_fee_tax", thirdPartyFeeTax)
                .addFormDataPart("total_net_price", totalNetPrice)
                .build();

        Loading.show(context);
        ApiLocal.apiInterface().updateTransaction(id, requestBody, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<TransactionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TransactionResponse>> call, Response<ApiResponse<TransactionResponse>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        TransactionResponse transactionModel = response.body().getData();

                        Intent intent = new Intent(DetailTransactionActivity.this, DetailTransactionActivity.class);
                        intent.putExtra("order_no", id);
                        intent.putExtra("total", total);
                        intent.putExtra("order_date", order_date);
                        intent.putExtra("order_time", order_time);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        String msg = MethodUtil.getErrorResponse(response.errorBody().string());
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan, silahkan periksa koneksi internet anda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TransactionResponse>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void updateTransaction(String id, String status){
        RequestBody requestBody;

        if (photoImage != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photoImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("status", status)
                    .addFormDataPart("image", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                    .build();

        } else if (selectedImage != null) {
            File file = createTempFile(selectedImage);
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("status", status)
                    .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                    .build();
        } else {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("status", status)
                    .build();
        }

        Loading.show(context);
        ApiLocal.apiInterface().updateTransaction(id, requestBody, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<TransactionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TransactionResponse>> call, Response<ApiResponse<TransactionResponse>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        TransactionResponse transactionModel = response.body().getData();

                        Intent intent = new Intent(DetailTransactionActivity.this, DetailTransactionActivity.class);
                        intent.putExtra("order_no", id);
                        intent.putExtra("total", total);
                        intent.putExtra("order_date", order_date);
                        intent.putExtra("order_time", order_time);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        String msg = MethodUtil.getErrorResponse(response.errorBody().string());
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan, silahkan periksa koneksi internet anda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TransactionResponse>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void doPrintStruk() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mireta_bg);
        List<PrintSize> data = new ArrayList<>();

        String align = "START_FOOTER";
        data.add(new PrintSize(FontSize.NORMAL, align));

        data.add(new PrintSize(FontSize.NORMAL, "Detail Transaksi" + "\n"));
        data.add(new PrintSize(FontSize.NORMAL, "31-07-2019" + "\n"));
        data.add(new PrintSize(FontSize.EMPTY, "\n"));


        FontSize size = FontSize.NORMAL;

        align = "STOP_ALIGN";
        data.add(new PrintSize(size, align));

//        TransactionHeader transactionHeader = transaction.getTransaction_header();
//        List<TransactionDetail> res = transaction.getTransaction_detail();

//        int mTotal = Integer.parseInt(transactionHeader.getTotal_amount());
        String item, grandPrice;

        for (int i = 0; i < 1; i++) {

//            TransactionDetail detail = res.get(i);
//            int mAmount = Integer.parseInt(detail.getPrice());
//            int mQty = Integer.parseInt(detail.getQuantity());
//            int mGrandPrice = mAmount * mQty;

//            item = detail.getItem_name();
//            String qty = detail.getQuantity() + " x ";
//            String price = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mAmount);
//            grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandPrice);

            data.add(new PrintSize(FontSize.BOLD, "Ayam Bakar" + "\n"));
            data.add(new PrintSize(size, "1x"));
            data.add(new PrintSize(size, "Rp. 25,000" + "\n"));

            align = "RIGHT_ALIGN";
            data.add(new PrintSize(size, align));
            data.add(new PrintSize(size, "Rp. 25,000" + "\n"));

            align = "STOP_ALIGN";
            data.add(new PrintSize(size, align));
        }

        item = "-----------------------------------------\n";
        data.add(new PrintSize(size, item));

        align = "STOP_ALIGN";
        data.add(new PrintSize(size, align));

        item = "TOTAL : \n";
//        grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mTotal);
        data.add(new PrintSize(FontSize.BOLD, item));

        align = "RIGHT_ALIGN";
        data.add(new PrintSize(size, align));
        data.add(new PrintSize(FontSize.BOLD, "Rp. 25,000" + "\n"));

        item = "-----------------------------------------\n";
        data.add(new PrintSize(size, item + "\n"));

        data.add(new PrintSize(FontSize.EMPTY, "\n"));
        ESCPOSApi.printStruk(bitmap, data);
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setTitle("Upload Bukti Pembayaran");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            }
//            else if (options[item].equals("Choose From Gallery")) {
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                photoPickerIntent.setType("image/*");
//                startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
//            }
            else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }

        });
        builder.show();
    }

    private File createTempFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , System.currentTimeMillis() + "_image.webp");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.WEBP, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
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

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photoImage = (Bitmap) data.getExtras().get("data");
            photoImage.compress(Bitmap.CompressFormat.PNG, 100, stream);

            try {
                File outputDir = getCacheDir();
                imageCheck = File.createTempFile("photo", "jpeg", outputDir);
                FileOutputStream outputStream = openFileOutput("photo.jpeg", Context.MODE_PRIVATE);
                outputStream.write(stream.toByteArray());
                outputStream.close();
                Log.d("Write File", "Success");

                if (PAYMENT_ACCOUNT != 0) {
                    updateTransactionWithPaymentMethod(order_no, SUCCESS + "");
                } else {
                    updateTransaction(order_no, SUCCESS + "");
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Write File", "Failed2");
            }
        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);

                if (PAYMENT_ACCOUNT != 0) {
                    updateTransactionWithPaymentMethod(order_no, SUCCESS + "");
                } else {
                    updateTransaction(order_no, SUCCESS + "");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDeviceConnected() {
        isPrinterReady = true;
        Loading.hide(context);
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Terhubung dengan perangkat", Snackbar.LENGTH_LONG);
        snackbar.show();

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
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.stop();
        }
        mService = null;
    }

    private void showDialogLayout(int layout) {
        dialog = new Dialog(Objects.requireNonNull(DetailTransactionActivity.this));
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
