package com.boardinglabs.mireta.standalone.modul.history;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
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
import com.boardinglabs.mireta.standalone.component.network.Api;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.NetworkService;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionDetailModel;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionHeader;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.Detail;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
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
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTransactionActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, BluetoothHandler.HandlerInterface {

    private static final int SUCCESS = 2;
    private List<TransactionDetailModel> transactionDetails;
    private DetailTransactionAdapter adapter;
    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;
    private BluetoothService mService = null;
    private boolean isPrinterReady = false;
    private BluetoothDevice mDevice;
    private TransactionHeader transactionHeader;
    private Transactions transaction;
    private List<Detail> res;

    private PrinterDevice printerDevice;
    private Format format;
    private TextView txt;
    private String str, manufacturer, model;
    private Context context = DetailTransactionActivity.this;
    private String whatToDo, sisaSaldo;
    private String order_date, order_time, member_name, member_lulusan, member_angkatan, sKembalian, mNomBayar = null;
    private int PAYMENT_METHOD;
    private int QRIS_METHOD = 2;
    private String order_no, total;

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
    @BindView(R.id.tvOrderDate)
    TextView tvOrderDate;
    @BindView(R.id.tvStatusOrder)
    TextView tvStatusOrder;
    @BindView(R.id.tvOrderNo)
    TextView tvOrderNo;
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
    @BindView(R.id.layout_qris)
    LinearLayout layout_qris;

    private Dialog dialog;
    private int loop = 0;

    @OnClick(R.id.btnKonfirmasi)
    void btnKonfirmasi(){
        updateTransaction(order_no, SUCCESS);
    }

    @OnClick(R.id.btnVoid)
    void onClickVoid() {
        showDialogLayout(R.layout.layout_input_password);
        EditText etPassword = dialog.findViewById(R.id.etPassword);
        Button btnProses = dialog.findViewById(R.id.btnProses);
        btnProses.setOnClickListener(v -> {
            if (etPassword.getText().toString().equals(PreferenceManager.getPassVoid())) {
                doCheckSettled();
                dialog.dismiss();
            } else {
                dialog.dismiss();
                showDialogLayout(R.layout.layout_wrong_pass);
                Button btnOk = dialog.findViewById(R.id.btnOK);
                btnOk.setOnClickListener(v1 -> dialog.dismiss());
            }
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
            tvAmount.setText("Rp. " + MethodUtil.toCurrencyFormat(Long.toString(mTotal)));
            tvNameTenant.setText(loginBusiness.name);
            tvBusinessAddress.setText(loginBusiness.address);
            tvBusinessPhone.setText(loginStockLocation.telp != null ? loginStockLocation.telp : "");
        }

        String finalOrder_no = order_no;
        refreshLayout.setOnRefreshListener(() -> {
//            transactionDetails.clear();
//            getDetailTransaction(finalOrder_no);
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
    protected void onSubmitBtnPressed() {

    }

    private void doCheckSettled(){
        Loading.show(context);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("transaction_code", transaction.getTransactionCode())
                .build();
        ApiLocal.apiInterface().doCheckSettled(requestBody, "Bearer "+PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                if (response.isSuccessful()){
                    Log.d("TAG", "MASUK CEK SETTLE");
                    doVoid();
                } else {
                    if (response.message().equals("Forbidden")){
                        Toast.makeText(context, "Transaksi yang sudah disettle tidak bisa dibatalkan", Toast.LENGTH_SHORT).show();
                    } else {
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
                                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_bjbs);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                printerDevice.printBitmap(format, bitmap);
                                printerDevice.printText("\n \n");
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                            printerDevice.printText(format, loginBusiness.name.toUpperCase() + "\n" +
                                    loginBusiness.address + "\n");
                            printerDevice.printText(format, loginStockLocation.telp != null ? loginStockLocation.telp + "\n": "-" + "\n");
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "--------------------------------\n");
                            printerDevice.printText(format, order_date + "                " + order_time + "\n");
                            format.setParameter("bold", "true");
                            printerDevice.printText(format, "Order No:    " + transaction.getTransactionCode() + "\n");
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
                                int mAmount = Integer.parseInt(detail.getSalesPrice());
                                int mQty = Integer.parseInt(detail.getQty());
                                int mGrandPrice = mAmount * mQty;

                                item = detail.getStock().getItem().getName();
                                String qty = detail.getQty() + " x ";
                                String price = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mAmount);
                                grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandPrice);

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, item + "\n" +
                                        qty + price + "\n");
                                format.clear();
                                format.setParameter("align", "right");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, grandPrice + "\n");
                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                            }

                            printerDevice.printText(format, "--------------------------------\n");

                            item = "TOTAL :";
                            grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mTotal);
                            printerDevice.printText(format, item + "\n");
                            format.clear();
                            format.setParameter("align", "right");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, grandPrice + "\n");


//                            int nomBayar = Integer.parseInt(mNomBayar);
//                            grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(nomBayar);
//                            format.setParameter("align", "left");
//                            format.setParameter("size", "medium");
//                            printerDevice.printText(format, "BAYAR DENGAN ARDI :" + "\n");
//                            format.clear();
//                            format.setParameter("align", "right");
//                            format.setParameter("size", "medium");
//                            printerDevice.printText(format, grandPrice + "\n");

//                            int mKembalian = Integer.parseInt(sKembalian);
//                            grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mKembalian);
//                            format.setParameter("align", "left");
//                            format.setParameter("size", "medium");
//                            printerDevice.printText(format, "KEMBALI :" + "\n");
//                            format.clear();
//                            format.setParameter("align", "right");
//                            format.setParameter("size", "medium");
//                            printerDevice.printText(format, grandPrice + "\n");

                            try {
                                int mSisaSaldo = Integer.parseInt(sisaSaldo);
                                printerDevice.printText(format, "--------------------------------\n");

                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, "Sisa Saldo :" + "\n");
                                format.clear();
                                format.setParameter("align", "right");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mSisaSaldo) + "\n");

                            } catch (Exception e) {
                                e.printStackTrace();
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
            printText();
        }
//        printText();
//        doPrintStruk();
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

                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.pd_logo_black_white, o2);
//                Bitmap bmp = getBitmapFromURL(NetworkService.BASE_URL_IMAGE + PreferenceManager.getStockLocation().brand.getLogo_image_url());
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
            mService.sendMessage(loginBusiness.name, "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(loginBusiness.address, "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage("Bandung", "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage("----------------------------------------", "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage("Detail Transaksi", "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(order_date, "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage("Order No: " + transaction.getTransactionCode(), "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage("----------------------------------------", "");
            mService.write(PrinterCommands.ESC_ENTER);

            int mTotal = Integer.parseInt(transaction.getTotalPrice());
            String item, grandPrice;

            for (int i = 0; i < res.size(); i++) {

                Detail detail = res.get(i);
                int mAmount = Integer.parseInt(detail.getSalesPrice());
                int mQty = Integer.parseInt(detail.getQty());
                int mGrandPrice = mAmount * mQty;

                item = detail.getStock().getItem().getName();
                String qty = detail.getQty() + " x ";
                String price = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mAmount);
                grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandPrice);

                mService.write(PrinterCommands.LEFT_ALIGN);
                mService.sendMessage(item, "");
                mService.write(PrinterCommands.ESC_ENTER);
                mService.write(PrinterCommands.LEFT_ALIGN);
                mService.sendMessage(qty + price, "");
                mService.write(PrinterCommands.RIGHT_ALIGN);
                mService.sendMessage(grandPrice, "");
                mService.write(PrinterCommands.ESC_ENTER);
                mService.write(PrinterCommands.LEFT_ALIGN);
            }

            item = "----------------------------------------";

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(item, "");
            mService.write(PrinterCommands.ESC_ENTER);

            item = "TOTAL : ";
            grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mTotal);

            mService.write(PrinterCommands.LEFT_ALIGN);
            mService.sendMessage(item, "");
            mService.write(PrinterCommands.RIGHT_ALIGN);
            mService.sendMessage(grandPrice, "");
            mService.write(PrinterCommands.ESC_ENTER);

            item = "----------------------------------------\n\n";

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(item, "");
            mService.sendMessage("Terimakasih sudah berbelanja\n\n\n", "");

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
//                    transactionHeader = transaction.getTransaction_header();
                    res = transaction.getDetails();

                    for (int i = 0; i < res.size(); i++) {
                        Detail transactionDetail = res.get(i);
                        transactionDetails.add(new TransactionDetailModel(transactionDetail.getStock().getItem().getName(),
                                transactionDetail.getQty(),
                                transactionDetail.getSalesPrice(),
                                transactionDetail.getItemDiscount()));
                    }

                    Gson gson = new Gson();
                    String json = gson.toJson(transactionHeader);
                    Log.d("Json", json);

                    int mDiscount = Integer.parseInt(transaction.getTotalDiscount());
                    int mAmount = Integer.parseInt(transaction.getTotalPrice());
                    long amount = Long.parseLong(transaction.getTotalPrice());
                    int mStatus = Integer.parseInt(transaction.getStatus() + "");
                    tvOrderNo.setText(transaction.getTransactionCode());
                    tvAmount.setText("Rp. " + MethodUtil.toCurrencyFormat(Long.toString(amount)));
                    tvDiscount.setText("Rp. " + MethodUtil.toCurrencyFormat(String.valueOf(mDiscount)));
                    tvOrderDate.setText(order_date);
                    switch (mStatus) {
                        case 1:
                            tvStatusOrder.setText("PENDING");
                            //
                            tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Red));
                            btnBayar.setVisibility(View.GONE);
                            btnPrint.setVisibility(View.GONE);
                            btnVoid.setVisibility(View.GONE);
                            layout_qris.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            tvStatusOrder.setText("BERHASIL");
                            tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Green));
                            btnBayar.setVisibility(View.GONE);
                            btnPrint.setVisibility(View.VISIBLE);
                            btnVoid.setVisibility(View.GONE);
                            break;
                        case 3:
                            tvStatusOrder.setText("DIBATALKAN");
                            tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Red));
                            btnBayar.setVisibility(View.GONE);
                            btnPrint.setVisibility(View.GONE);
                            btnVoid.setVisibility(View.GONE);
                            break;
                    }

                    adapter = new DetailTransactionAdapter(transactionDetails, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                    if (whatToDo != null) {
                        if (whatToDo.equals(Constant.DO_PRINT)) {
                            if (manufacturer.equals("wizarPOS")) {
                                closePrinter();
                                printStruk();
                            } else {
                                printText();
                            }
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

    private void updateTransaction(String id, int status){
        Loading.show(context);
        ApiLocal.apiInterface().updateTransaction(id, status, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<TransactionResponse>>() {
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
                        Toast.makeText(context, "Terjadi kesalahan, konfirmasi pembayaran gagal", Toast.LENGTH_SHORT).show();
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
