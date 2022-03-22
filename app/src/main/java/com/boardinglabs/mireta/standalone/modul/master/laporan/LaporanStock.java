package com.boardinglabs.mireta.standalone.modul.master.laporan;

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
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.BuildConfig;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.BluetoothHandler;
import com.boardinglabs.mireta.standalone.component.DeviceActivity;
import com.boardinglabs.mireta.standalone.component.PrinterCommands;
import com.boardinglabs.mireta.standalone.component.adapter.RecapStockAdapter;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.NetworkService;
import com.boardinglabs.mireta.standalone.component.network.entities.InquiryStatus.StockReport;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.component.util.Utils;
import com.boardinglabs.mireta.standalone.modul.history.DetailTransactionActivity;
import com.boardinglabs.mireta.standalone.modul.home.HomeActivity;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.fastaccess.datetimepicker.DatePickerFragmentDialog;
import com.fastaccess.datetimepicker.callback.DatePickerCallback;
import com.zj.btsdk.BluetoothService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class LaporanStock extends BaseActivity implements BluetoothHandler.HandlerInterface, DatePickerCallback {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.item_name)
    TextView item_name;
    @BindView(R.id.imgOptions)
    ImageView imgOptions;

    private List<StockReport> stockReportList;
    private String dateToday = "";
    private Dialog dialog;

    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;
    private boolean isPrinterReady = false;
    private BluetoothDevice mDevice;
    private BluetoothService mService = null;
    private boolean isButtonPrintClicked = false;

    private String str, manufacturer, model;
    private Handler handler = new Handler();

    private Runnable myRunnable = () -> {
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
    };
    private PrinterDevice printerDevice;
    private Format format;
    private Context context;
    private CheckedTextView ctvFromToDate, ctvToDate;
    private String startDate = "";
    private String endDate = "";
    private String filterStartDate = "";
    private String filterDate = "";
    private boolean isFromDate = false;

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.imgOptions)
    void onClickImgPrint() {

        View v1 = findViewById(R.id.imgOptions);
        PopupMenu pm = new PopupMenu(Objects.requireNonNull(context), v1);
        pm.getMenuInflater().inflate(R.menu.menu_laporan, pm.getMenu());
        pm.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_filter:
                    menuItem.setIcon(R.drawable.ic_filter_list_black_24dp);
                    showDialogLayout(R.layout.layout_filter_laporan);

                    CheckedTextView ctvSettleAll = dialog.findViewById(R.id.ctvSettleAll);
                    CheckedTextView transaksiSaya = dialog.findViewById(R.id.ctvSettleTrue);
                    CheckedTextView transaksiHariINi = dialog.findViewById(R.id.ctvSettleFalse);
                    ctvFromToDate = dialog.findViewById(R.id.ctvFromToDate);
                    ctvToDate = dialog.findViewById(R.id.ctvToDate);
                    TextView tvTitleDate = dialog.findViewById(R.id.tvTitleDate);
                    TextView tvLine = dialog.findViewById(R.id.tvLine);

                    tvLine.setVisibility(View.VISIBLE);
                    ctvToDate.setVisibility(View.VISIBLE);
                    tvTitleDate.setText("Rentang Tanggal");
                    ctvFromToDate.setText("Tanggal Awal");
                    ctvToDate.setText("Tanggal Akhir");
                    transaksiSaya.setText("Laporan Stok Saya");
                    transaksiHariINi.setText("Semua Laporan Stok");

                    transaksiSaya.setOnClickListener(v -> {
                        ctvSettleAll.setChecked(false);
                        transaksiSaya.setChecked(true);
                        transaksiHariINi.setChecked(false);
                    });

                    transaksiHariINi.setOnClickListener(v -> {
                        ctvSettleAll.setChecked(false);
                        transaksiSaya.setChecked(false);
                        transaksiHariINi.setChecked(true);
                    });

                    ctvFromToDate.setOnClickListener(v -> {
                        isFromDate = true;
//                        FragmentManager fragmentManager = getSupportFragmentManager(); //Initialize fragment manager
//                        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(); // Create datePickerDialog Instance
//                        datePickerDialog.show(fragmentManager,"Date Picker"); // Show DatePicker Dialog

                        DatePickerFragmentDialog.newInstance().show(getSupportFragmentManager(), "DatePickerFragmentDialog");
                    });

                    ctvToDate.setOnClickListener(v -> {
                        isFromDate = false;
                        if (ctvFromToDate.isChecked()) {
                            DatePickerFragmentDialog.newInstance().show(getSupportFragmentManager(), "DatePickerFragmentDialog");
                        } else {
                            Toast.makeText(context, "Silahkan pilih tanggal awal", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
                    btnSimpan.setOnClickListener(v -> {
                        dialog.dismiss();

                        if (transaksiSaya.isChecked()) {
                            item_name.setText("Laporan Stok Saya");
                            setDataMyReport();
                            swipeRefresh.setOnRefreshListener(this::setDataMyReport);

                            if (ctvFromToDate.isChecked()) {
                                String sEndDate = "- " + endDate;
                                item_name.setText("Laporan Stok Saya (" + startDate + sEndDate + ")");
                            }
                        }

                        if (transaksiHariINi.isChecked()) {
                            item_name.setText("Laporan Stok");
                            setData();
                            swipeRefresh.setOnRefreshListener(this::setData);

                            if (ctvFromToDate.isChecked()) {
                                String sEndDate = "- " + endDate;
                                item_name.setText("Laporan Stok (" + startDate + sEndDate + ")");
                            }
                        }
                    });

                    Button btnResetFilter = dialog.findViewById(R.id.btnResetFilter);
                    btnResetFilter.setOnClickListener(v -> {
                        dialog.dismiss();
                        setDataMyReport();
                    });
                    break;
                case R.id.navigation_print:
                    menuItem.setIcon(R.drawable.ic_print_blue_24dp);
                    if (stockReportList.size() < 1){
                        Toast.makeText(context, "Tidak ada data yang harus diprint", Toast.LENGTH_SHORT).show();
                    } else {
                        if (manufacturer.equals("wizarPOS")) {
                            closePrinter();
                            printStruk();
                        } else {
                            isButtonPrintClicked = true;
                            printText();
                        }
                    }
                    break;
            }
            return true;
        });
        pm.show();

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_laporan_stock;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("LAPORAN STOK BARANG");
        imgOptions.setVisibility(View.VISIBLE);

        printerDevice = (PrinterDevice) POSTerminal.getInstance(getApplicationContext()).getDevice(
                "cloudpos.device.printer");
        manufacturer = Build.MANUFACTURER;

        setDataMyReport();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        dateToday = s.format(new Date());

        startDate = PreferenceManager.getOperationData().getOpenedAt();
        endDate = PreferenceManager.getOperationData().getClosedAt();

        swipeRefresh.setOnRefreshListener(this::setDataMyReport);

        setupBluetooth();

        context = this;
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

    private void setData(){
        swipeRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getStockReport(loginStockLocation.location_id, startDate, endDate, "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<StockReport>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<StockReport>>> call, Response<ApiResponse<List<StockReport>>> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    stockReportList = response.body().getData();
                    RecapStockAdapter adapter = new RecapStockAdapter(stockReportList, context, PreferenceManager.getOperationData().getIsOpen());
                    recyclerView.setAdapter(adapter);

                } catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ApiResponse<List<StockReport>>> call, Throwable t) {
                t.printStackTrace();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    public void showAdjustmentDialog(boolean isFromStock, int stockId) {
        showDialogLayout(R.layout.dialog_adjustment);
        EditText etNominal = dialog.findViewById(R.id.etNominal);
        EditText etKaterangan = dialog.findViewById(R.id.etKaterangan);
        Button btnSimpan = dialog.findViewById(R.id.btnProses);

        etNominal.setHint("Masukan stok fisik yang tersisa");
        btnSimpan.setOnClickListener(v1 -> {
            if (etNominal.getText().toString().equals("") || etKaterangan.getText().toString().equals("")) {
                Toast.makeText(context, "Silahkan lengkapi form", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
            reportAdjustmentStock(stockId+"", etNominal.getText().toString().replace(".",""), etKaterangan.getText().toString());
        });
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
                        Intent intent = new Intent(LaporanStock.this, LaporanStock.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
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

    private void setDataMyReport(){
        swipeRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getMyStockReport(loginStockLocation.location_id, PreferenceManager.getOperationData().getId()+ "", startDate, endDate, "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<StockReport>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<StockReport>>> call, Response<ApiResponse<List<StockReport>>> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    stockReportList = response.body().getData();
                    RecapStockAdapter adapter = new RecapStockAdapter(stockReportList, context, PreferenceManager.getOperationData().getIsOpen());
                    recyclerView.setAdapter(adapter);

                } catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ApiResponse<List<StockReport>>> call, Throwable t) {
                t.printStackTrace();
                swipeRefresh.setRefreshing(false);
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

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, loginStockLocation.location.getName() + "\n");
                            printerDevice.printText(format, loginStockLocation.location.getAddress() + "\n");
                            printerDevice.printlnText(format, loginStockLocation.location.getPhone()!=null?loginStockLocation.location.getPhone()+"\n":"-\n");
                            printerDevice.printlnText(format, getPrintLabelValue("Operator:", loginUser.fullname, false, true));

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "--------------------------------");
                            printerDevice.printText(format, "Laporan Stok\n");
                            printerDevice.printText(format, "--------------------------------\n\n");

                            for (StockReport stock: stockReportList){
                                String totalAdjustment = stock.getTotalAdjusted()!=null?stock.getTotalAdjusted():"0";

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printlnText(format, "\n" + stock.getItemName()+"\n");
                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, getPrintLabelValue("      Stok Awal", stock.getQtyInitial()+"", false, true));
                                printerDevice.printText(format, getPrintLabelValue("      Stok Masuk", stock.getSumQtyIn()+"", false, true));
//                                printerDevice.printText(format, getPrintLabelValue("    Stok Keluar", stock.getSumQtyOut()+"", false, true));
                                printerDevice.printText(format, getPrintLabelValue("      Stok Terjual", stock.getTotalSold()+"", false, true));
                                printerDevice.printText(format, getPrintLabelValue("      Stok Rusak", String.valueOf(stock.getTotalDamaged()!=null?stock.getTotalDamaged():0), false, true));
                                printerDevice.printText(format, "    ----------------------------");
                                if (!totalAdjustment.equals("0")) printerDevice.printText(getPrintLabelValue("      Perbedaan Stok", totalAdjustment, false, true));
                                printerDevice.printlnText(format, getPrintLabelValue("      Sisa Stok", stock.getQtyEnd()+"", false, true));
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

            @SuppressLint("SimpleDateFormat") String printDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

            mService.sendMessage(loginStockLocation.location.getBrand().getName() , "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage(loginStockLocation.location.getName() , "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage(loginStockLocation.location.getAddress() , "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage(loginStockLocation.location.getPhone()!=null?loginStockLocation.location.getPhone():"-" , "");
            mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");
            mService.sendMessage(getPrintBTLabelValue("Operator:", loginUser.fullname, false, true) , "");
            mService.sendMessage(getPrintBTLabelValue("Filter By:", item_name.getText().toString(), false, true) , "");
            mService.sendMessage(getPrintBTLabelValue("Print Date:", printDate, false, true) , "");
            mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage("----------------------------------------" , "");
            mService.sendMessage("Laporan Stok" , "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage("----------------------------------------" , "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.write(PrinterCommands.LEFT_ALIGN);

            for (StockReport stock: stockReportList){
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
            mService.sendMessage(getPrintBTLabelValue("  ", "  ", false, true), "");
            mService.write(PrinterCommands.ESC_ENTER);

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(getResources().getString(R.string.footer_print) + "\n\n\n", "");

            isButtonPrintClicked = false;

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

    private void showDialogLayout(int layout) {
        dialog = new Dialog(Objects.requireNonNull(LaporanStock.this));
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
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.stop();
        }
        mService = null;
    }

    @AfterPermissionGranted(RC_BLUETOOTH)
    private void setupBluetooth() {
        String[] params = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
        if (!EasyPermissions.hasPermissions(this, params)) {
            EasyPermissions.requestPermissions(this, "You need bluetooth permission",
                    RC_BLUETOOTH, params);
            return;
        }
        mService = new BluetoothService(this, new BluetoothHandler(LaporanStock.this));
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
    public void onDateSet(long date) {
        Log.d("date", MethodUtil.getDateOnly(date));
        if (isFromDate){
            ctvFromToDate.setChecked(true);
            ctvFromToDate.setText(MethodUtil.getDateOnly(date));
            startDate = MethodUtil.getDateOnly(date);
        } else {
            ctvToDate.setChecked(true);
            ctvToDate.setText(MethodUtil.getDateOnly(date));
            endDate = MethodUtil.getDateOnly(date);
        }
    }
}
