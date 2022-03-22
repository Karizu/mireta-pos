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
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.BuildConfig;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.BluetoothHandler;
import com.boardinglabs.mireta.standalone.component.DeviceActivity;
import com.boardinglabs.mireta.standalone.component.PrinterCommands;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.Report.ChildReportModels;
import com.boardinglabs.mireta.standalone.component.network.entities.Report.NewReportModels;
import com.boardinglabs.mireta.standalone.component.network.entities.Report.ReportModels;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Constant;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.component.util.Utils;
import com.boardinglabs.mireta.standalone.modul.master.laporan.adapter.ReportAdapter;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.model.KatalogModel;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.fastaccess.datetimepicker.DatePickerFragmentDialog;
import com.fastaccess.datetimepicker.callback.DatePickerCallback;
import com.google.gson.Gson;
import com.twigsntwines.daterangepicker.DatePickerDialog;
import com.twigsntwines.daterangepicker.DatePickerSpinner;
import com.twigsntwines.daterangepicker.DateRangePickedListener;
import com.zj.btsdk.BluetoothService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaporanPenjualan extends BaseActivity implements BluetoothHandler.HandlerInterface,
//        DateRangePickedListener
         DatePickerCallback {

    private List<KatalogModel> katalogModels;
    private ArrayList<ReportModels> reportModels;
    private ReportAdapter adapter;
    private List<String> itemImage = new ArrayList<>();
    private Context context;
    private Long grandTotal;
    private int mGrandTotal, mPrice, mQty;
    private String pathSettle, pathAll;
    private List<NewReportModels> newReportModelsList;

    public static final int RC_BLUETOOTH = 1010;
    public static final int RC_CONNECT_DEVICE = 1011;
    public static final int RC_ENABLE_BLUETOOTH = 1012;
    public BluetoothService mService = null;
    public boolean isPrinterReady = false;
    public BluetoothDevice mDevice;
    private boolean isButtonPrintClicked = false;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tvTotalPenjualan)
    TextView tvTotalPenjualan;
    @BindView(R.id.imgFilter2)
    ImageView imgFilter;
    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.imgOptions)
    ImageView imgOptions;
    @BindView(R.id.item_name)
    TextView item_name;

    private Dialog dialog;
    private PrinterDevice printerDevice;
    private Format format;
    private String str;
    private String versionName;
    private String dateToday;
    private String manufacturer;
    private CheckedTextView ctvFromToDate;
    private String pathDate = "";
//    private DatePickerDialog datePickerDialog;
    private String filterStartDate = "";
    private String filterEndDate = "";

    @OnClick(R.id.imgFilter2)
    void onClickFilter() {
        showDialog();

        pathSettle = "";

        CheckedTextView ctvSettleAll = dialog.findViewById(R.id.ctvSettleAll);
        CheckedTextView ctvSettleTrue = dialog.findViewById(R.id.ctvSettleTrue);
        CheckedTextView ctvSettleFalse = dialog.findViewById(R.id.ctvSettleFalse);
        ctvFromToDate = dialog.findViewById(R.id.ctvFromToDate);

        ctvSettleAll.setOnClickListener(v -> {
            ctvSettleAll.setChecked(true);
            ctvSettleTrue.setChecked(false);
            ctvSettleFalse.setChecked(false);
            pathSettle = "";
        });

//        ctvSettleTrue.setOnClickListener(v -> {
//            ctvSettleAll.setChecked(false);
//            ctvSettleTrue.setChecked(true);
//            ctvSettleFalse.setChecked(false);
//            pathSettle = "is_settled=1&";
//        });
//
//        ctvSettleFalse.setOnClickListener(v -> {
//            ctvSettleAll.setChecked(false);
//            ctvSettleTrue.setChecked(false);
//            ctvSettleFalse.setChecked(true);
//            pathSettle = "is_settled=0&";
//        });

        ctvSettleTrue.setOnClickListener(v -> {
            ctvSettleAll.setChecked(false);
            ctvSettleTrue.setChecked(true);
            ctvSettleFalse.setChecked(false);
            pathSettle = "location_operation_id="+ PreferenceManager.getOperationData().getId() + "&date="+ dateToday;
        });

        ctvSettleFalse.setOnClickListener(v -> {
            ctvSettleAll.setChecked(false);
            ctvSettleTrue.setChecked(false);
            ctvSettleFalse.setChecked(true);
            pathSettle = "date="+ dateToday +"&";
        });

        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(v -> {
            dialog.dismiss();
            pathAll = "transactions/report?" + pathSettle;
            Log.d("PATH", pathAll);
            mGrandTotal = 0;
            reportModels.clear();
            adapter.notifyDataSetChanged();
            getReportByFilter(pathAll);

            if (ctvSettleTrue.isChecked()) {
                item_name.setText("Laporan Penjualan Saya Hari Ini");
            }

            if (ctvSettleFalse.isChecked()) {
                item_name.setText("Laporan Penjualan Hari Ini");
            }

            swipeRefresh.setOnRefreshListener(() -> {
                mGrandTotal = 0;
                reportModels.clear();
                adapter.notifyDataSetChanged();
                getReportByFilter(pathAll);
            });
        });

        Button btnResetFilter = dialog.findViewById(R.id.btnResetFilter);
        btnResetFilter.setOnClickListener(v -> {
            dialog.dismiss();
            mGrandTotal = 0;
            reportModels.clear();
            adapter.notifyDataSetChanged();
            getReport();
        });
    }

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
                    showDialog();

                    pathSettle = "";

                    CheckedTextView ctvSettleAll = dialog.findViewById(R.id.ctvSettleAll);
                    CheckedTextView ctvSettleTrue = dialog.findViewById(R.id.ctvSettleTrue);
                    CheckedTextView ctvSettleFalse = dialog.findViewById(R.id.ctvSettleFalse);
                    ctvFromToDate = dialog.findViewById(R.id.ctvFromToDate);

                    ctvSettleTrue.setText("Penjualan Saya");
                    ctvSettleFalse.setText("Semua");

                    ctvSettleAll.setOnClickListener(v -> {
                        ctvSettleAll.setChecked(true);
                        ctvSettleTrue.setChecked(false);
                        ctvSettleFalse.setChecked(false);
                        pathSettle = "";
                    });

//                    ctvSettleTrue.setOnClickListener(v -> {
//                        ctvSettleAll.setChecked(false);
//                        ctvSettleTrue.setChecked(true);
//                        ctvSettleFalse.setChecked(false);
//                        pathSettle = "is_settled=1&";
//                    });
//
//                    ctvSettleFalse.setOnClickListener(v -> {
//                        ctvSettleAll.setChecked(false);
//                        ctvSettleTrue.setChecked(false);
//                        ctvSettleFalse.setChecked(true);
//                        pathSettle = "is_settled=0&";
//                    });

                    ctvSettleTrue.setOnClickListener(v -> {
                        ctvSettleAll.setChecked(false);
                        ctvSettleTrue.setChecked(true);
                        ctvSettleFalse.setChecked(false);
                        pathSettle = "location_operation_id="+ PreferenceManager.getOperationData().getId();
                    });

                    ctvSettleFalse.setOnClickListener(v -> {
                        ctvSettleAll.setChecked(false);
                        ctvSettleTrue.setChecked(false);
                        ctvSettleFalse.setChecked(true);
                        pathSettle = "";
                    });

                    ctvFromToDate.setOnClickListener(v -> {
//                        FragmentManager fragmentManager = getSupportFragmentManager(); //Initialize fragment manager
//                        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(); // Create datePickerDialog Instance
//                        datePickerDialog.show(fragmentManager,"Date Picker"); // Show DatePicker Dialog

                        DatePickerFragmentDialog.newInstance().show(getSupportFragmentManager(), "DatePickerFragmentDialog");
                    });

                    Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
                    btnSimpan.setOnClickListener(v -> {
                        dialog.dismiss();
                        pathAll = "transactions/report?" + pathSettle + pathDate;
                        Log.d("PATH", pathAll);
                        mGrandTotal = 0;
                        reportModels.clear();
                        adapter.notifyDataSetChanged();
                        getReportByFilter(pathAll);

                        if (ctvSettleTrue.isChecked()) {
                            item_name.setText("Laporan Penjualan Saya");
                            if (ctvFromToDate.isChecked()){
                                item_name.setText("Laporan Penjualan Saya (" + filterStartDate + ")");
                            }
                        }

                        if (ctvSettleFalse.isChecked()) {
                            item_name.setText("Laporan Penjualan");
                            item_name.setText("Laporan Penjualan (" + filterStartDate + ")");
                        }

                        swipeRefresh.setOnRefreshListener(() -> {
                            mGrandTotal = 0;
                            reportModels.clear();
                            adapter.notifyDataSetChanged();
                            getReportByFilter(pathAll);
                        });
                    });

                    Button btnResetFilter = dialog.findViewById(R.id.btnResetFilter);
                    btnResetFilter.setOnClickListener(v -> {
                        dialog.dismiss();
                        mGrandTotal = 0;
                        reportModels.clear();
                        adapter.notifyDataSetChanged();
                        getReport();
                    });
                    break;
                case R.id.navigation_print:
                    menuItem.setIcon(R.drawable.ic_print_blue_24dp);
                    if (reportModels.size() < 1){
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

    private Handler handler = new Handler();

    private Runnable myRunnable = () -> {
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
    };

    public void printStruk()    {
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

                            printerDevice.printText(format, loginBusiness.name.toUpperCase() + "\n" +
                                    loginBusiness.address + "\n");
                            printerDevice.printText(format, loginStockLocation.telp != null ? loginStockLocation.telp + "\n" : "-" + "\n");
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, "--------------------------------");
                            printerDevice.printlnText(format, "Laporan Penjualan");
                            printerDevice.printlnText(format, "--------------------------------");

                            String item, grandPrice;

                            for (int i = 0; i < reportModels.size(); i++) {

                                ReportModels models = reportModels.get(i);
                                int mAmount = Integer.parseInt(models.getItem_price());
                                int mQty = models.getItem_qty();
                                int mGrandPrice = mAmount * mQty;

                                item = models.getItem_name();
                                String qty = models.getItem_qty() + " x ";
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
                            grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandTotal);

                            printerDevice.printText(format, item + "\n");
                            format.clear();
                            format.setParameter("align", "right");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, grandPrice + "\n");

                            printerDevice.printText(format, "--------------------------------\n\n");

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "small");
                            printerDevice.printlnText(format, "Mireta v"+versionName);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getReportByFilter(String pathAll) {
        swipeRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getReportByFilter(pathAll, loginStockLocation.location_id, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject((Map) response.body().getData());
                    parseJson(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_laporan_penjualan;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("LAPORAN PENJUALAN");
        context = this;
//        imgFilter.setVisibility(View.VISIBLE);
        imgOptions.setVisibility(View.VISIBLE);

        versionName = BuildConfig.VERSION_NAME;

        printerDevice = (PrinterDevice) POSTerminal.getInstance(getApplicationContext()).getDevice(
                "cloudpos.device.printer");
        manufacturer = Build.MANUFACTURER;
        getReport();
        swipeRefresh.setOnRefreshListener(() -> {
            mGrandTotal = 0;
            reportModels.clear();
            adapter.notifyDataSetChanged();
            getReport();
        });

        setupBaseBluetooth();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        dateToday = s.format(new Date());
        pathDate = "&date=" + dateToday;

//        pathDate = "&dateStart="+ dateToday + " 00:00:00" + "&dateEnd="+ dateToday + " 23:59:59";

//        datePickerDialog = new DatePickerDialog();
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

    private void getReport() {
        swipeRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getReport(PreferenceManager.getOperationData().getId()+"", loginStockLocation.location_id, "Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject((Map) response.body().getData());
                    parseJson(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void parseJson(JSONObject data) {

        try {
            JSONObject items = data.getJSONObject("items");
            Iterator<String> categories = items.keys();

            LinkedHashMap<String, ArrayList<ReportModels>> itemCategory = new LinkedHashMap<>();
            ArrayList<ReportModels> itemInsideCat = null;
            while (categories.hasNext()) {
                String key = categories.next();
                JSONObject category = items.getJSONObject(key);

                Iterator<String> itemKeys = category.keys();

                itemInsideCat = new ArrayList<>();

                while (itemKeys.hasNext()) {


                    String itemKey = itemKeys.next();
                    JSONObject item = category.getJSONObject(itemKey);

                    itemInsideCat.add(new ReportModels(item.getInt("item_id"),
                            item.getString("item_name"),
                            item.getString("item_price"),
                            item.getInt("item_qty"),
                            item.getInt("category_id"),
                            item.getString("category_name")));

                    mPrice = Integer.parseInt(item.getString("item_price"));
                    mQty = item.getInt("item_qty");
                    mGrandTotal += mPrice * mQty;
//                    grandTotal = (long) mGrandTotal;
                }

                Gson gson = new Gson();
                String json = gson.toJson(itemInsideCat);
                System.out.println(json);

                itemCategory.put(key, itemInsideCat);
            }


            tvTotalPenjualan.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandTotal));

            reportModels = new ArrayList<>();
            for (Map.Entry<String, ArrayList<ReportModels>> entry : itemCategory.entrySet()) {
                ArrayList<ReportModels> reportModel = itemCategory.get(entry.getKey());
                reportModels.addAll(reportModel);
            }

            newReportModelsList = new ArrayList<>();
            List<ChildReportModels> childReportModelsList;

            for (ReportModels reportModel: reportModels) {
                childReportModelsList = new ArrayList<>();
                NewReportModels models = new NewReportModels();
                models.setCategory_id(reportModel.getCategory_id());
                models.setCategory_name(reportModel.getCategory_name());

                for (ReportModels report: reportModels) {
                    if (reportModel.getCategory_id() == report.getCategory_id()) {
                        ChildReportModels childReport = new ChildReportModels();
                        childReport.setItem_qty(report.getItem_qty());
                        childReport.setItem_price(report.getItem_price());
                        childReport.setItem_name(report.getItem_name());
                        childReport.setItem_id(report.getItem_id());
                        childReport.setCategory_id(report.getCategory_id());

                        if (childReportModelsList.size() > 0) {
                            int count = -1;
                            for (ChildReportModels newReportModel : childReportModelsList) {
                                if (report.getItem_id() == newReportModel.getItem_id() || report.getCategory_id() != newReportModel.getCategory_id()) {
                                    count = 0;
                                    break;
                                }
                            }

                            if (count != 0) {
                                childReportModelsList.add(childReport);
                            }
                        } else {
                            childReportModelsList.add(childReport);
                        }
                    }
                }

                models.setChildReportModels(childReportModelsList);

                if (newReportModelsList.size() > 0) {
                    int count = -1;
                    for (NewReportModels newReportModel : newReportModelsList) {
                        if (reportModel.getCategory_id() == newReportModel.getCategory_id()) {
                            count = 0;
                            break;
                        }
                    }

                    if (count != 0) {
                        newReportModelsList.add(models);
                    }
                } else {
                    newReportModelsList.add(models);
                }
            }

            StringBuilder log = MethodUtil.printLog(new Gson().toJson(newReportModelsList));
            Log.d("New Report Json", new Gson().toJson(newReportModelsList) + "");

            if (reportModels.size() < 1) {
                tvNoData.setVisibility(View.VISIBLE);
            } else {
                tvNoData.setVisibility(View.GONE);
            }

            Gson gson = new Gson();
            String json = gson.toJson(reportModels);
            System.out.println(json);

            adapter = new ReportAdapter(newReportModelsList, context);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
//            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
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

            @SuppressLint("SimpleDateFormat") String printDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());


            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(loginStockLocation.location.getBrand().getName() , "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage(loginBusiness.name, "");
            mService.sendMessage(loginBusiness.address, "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage(loginStockLocation.location.getPhone()!=null?loginStockLocation.location.getPhone():"-", "");
            mService.sendMessage(MethodUtil.getPrintBTLabelValue("  ", "  ", false, true), "");
            mService.sendMessage(MethodUtil.getPrintBTLabelValue("Operator:", loginUser.fullname, false, true) , "");
            mService.sendMessage(MethodUtil.getPrintBTLabelValue("Filter By:", item_name.getText().toString(), false, true) , "");
            mService.sendMessage(MethodUtil.getPrintBTLabelValue("Print Date:", printDate, false, true) , "");
            mService.sendMessage("------------------------------------------", "");
            mService.write(PrinterCommands.ESC_ENTER);
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage("Laporan Penjualan", "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.write(PrinterCommands.ESC_ENTER);
            mService.sendMessage("------------------------------------------", "");
            mService.sendMessage(MethodUtil.getPrintBTLabelValue("   ", "   ", false, true) , "");

            String item, grandPrice, categoryName;

            for (int i = 0; i < newReportModelsList.size(); i++) {

                NewReportModels models = newReportModelsList.get(i);
                categoryName = models.getCategory_name();

                mService.write(PrinterCommands.LEFT_ALIGN);
                mService.sendMessage(MethodUtil.getPrintBTLabelValue("   ", "   ", false, true) , "");
                mService.sendMessage("   " + categoryName, "");
                mService.sendMessage("   ------------------------------------   ", "");

                int totalPerCat = 0;

                for (ChildReportModels childReportModels : models.getChildReportModels()) {
                    int mAmount = Integer.parseInt(childReportModels.getItem_price());
                    int mQty = childReportModels.getItem_qty();
                    int mGrandPrice = mAmount * mQty;

                    totalPerCat += mGrandPrice;

                    item = childReportModels.getItem_name();
                    String qty = childReportModels.getItem_qty() + " x ";
                    String price = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mAmount);
                    grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandPrice);

                    mService.sendMessage("   " + item, "");
                    mService.write(PrinterCommands.ESC_ENTER);
                    mService.write(PrinterCommands.LEFT_ALIGN);
                    mService.sendMessage(MethodUtil.getPrintBTLabelValue("   " + qty + price, grandPrice + "   ", false, true) , "");
                }
            }

            mService.sendMessage(MethodUtil.getPrintBTLabelValue("   ", "   ", false, true) , "");

            item = "------------------------------------------";

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(item, "");
            mService.write(PrinterCommands.ESC_ENTER);

            item = "GRAND TOTAL :";
            grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandTotal);

            mService.write(PrinterCommands.LEFT_ALIGN);
            mService.sendMessage(MethodUtil.getPrintBTLabelValue(item, grandPrice, false, true) , "");
            mService.write(PrinterCommands.ESC_ENTER);

            item = "------------------------------------------\n\n";

            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(item, "");
            mService.sendMessage(MethodUtil.getPrintBTLabelValue("  ", "  ", false, true), "");
            mService.write(PrinterCommands.CENTER_ALIGN);
            mService.sendMessage(getResources().getString(R.string.footer_print) + "\n\n\n", "");

            isButtonPrintClicked = false;

        } else {
            if (mService.isBTopen())
                startActivityForResult(new Intent(this, DeviceActivity.class), RC_CONNECT_DEVICE);
            else
                requestBaseBluetooth();
        }
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
    public void setupBaseBluetooth() {
        String[] params = {android.Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
        if (!EasyPermissions.hasPermissions(this, params)) {
            EasyPermissions.requestPermissions(this, "You need bluetooth permission",
                    RC_BLUETOOTH, params);
            return;
        }
        mService = new BluetoothService(this, new BluetoothHandler(LaporanPenjualan.this));
    }

    public void requestBaseBluetooth() {
        if (mService != null) {
            if (!mService.isBTopen()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
            }
        }
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
        Log.d("isPrinterReady", isPrinterReady+"");
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
        Log.d("isPrinterReady", isPrinterReady+"");
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

    private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(LaporanPenjualan.this));
        //set content
        dialog.setContentView(R.layout.layout_filter_laporan);
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
    public void onDateSet(long date) {
        Log.d("date", MethodUtil.getDateOnly(date));
        ctvFromToDate.setChecked(true);
        pathDate = "&date="+MethodUtil.getDateOnly(date);
        ctvFromToDate.setText(MethodUtil.getDateOnly(date));
        filterStartDate = new SimpleDateFormat("dd MMM yy").format(date);
    }

//    @SuppressLint("SetTextI18n")
//    @Override
//    public void OnDateRangePicked(Calendar fromDate, Calendar toDate) {
//        Date from_date = fromDate.getTime();
//        Date to_date = toDate.getTime();
//
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatFilter = new SimpleDateFormat("dd MMM yy");
//        Log.d("date", format.format(from_date) + " - " + format.format(to_date));
//
//        filterStartDate = formatFilter.format(from_date);
//        filterEndDate = formatFilter.format(to_date);
//
//        ctvFromToDate.setChecked(true);
//        ctvFromToDate.setText(format.format(from_date) + " - " + format.format(to_date));
//        pathDate = "&dateStart=" + format.format(from_date) + " 00:00:00" + "&dateEnd="+format.format(to_date) + " 23:59:59";
//    }
}
