package com.boardinglabs.mireta.standalone.modul.damageditem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.DamagedStocks.DamagedStocks;
import com.boardinglabs.mireta.standalone.component.network.entities.Stocks.StockResponse;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Constant;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.akun.AkunActivity;
import com.boardinglabs.mireta.standalone.modul.expenditure.CreateExpenditureActivity;
import com.boardinglabs.mireta.standalone.modul.expenditure.ExpenditureActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.StokActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.model.ItemResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateDamagedItemActivity extends BaseActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    private final int REQEUST_CAMERA = 1, REQUEST_GALLERY = 2;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private String stockId, itemId;
    private Context context;
    private File imageCheck;
    private Bitmap selectedImage;
    private Bitmap photoImage;
    private RequestBody requestBody;
    private Dialog dialog;
    private Spinner spinnerSubKategori;

    @BindView(R.id.imgBarang)
    ImageView imgBarang;
    @BindView(R.id.spinnerItem)
    Spinner spinnerItem;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.etQty)
    EditText etQty;
    private ArrayList<String> itemNameList, itemIdList, stockIdList;

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.btnTambah)
    void createItem() {
        showDialog();
        TextView tvTitleBarang = dialog.findViewById(R.id.tvTitleBarang);
        TextView tvDesc = dialog.findViewById(R.id.etMasterKey);
        Button btnProses = dialog.findViewById(R.id.btnRelogin);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        tvTitleBarang.setText("PERHATIAN");
        tvDesc.setText("Apakah anda yakin ingin menambahkan barang rusak??");
        btnProses.setText("PROSES");
        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnProses.setOnClickListener(v -> doCreateItem());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_create_damaged_item;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Barang Rusak");

        context = this;
        setItemSpinner();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onCreateAtChild() {

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBackBtnPressed() {
        if (etDescription.getText().toString().equals("") && etQty.getText().toString().equals("")){
            onBackPressed();
        } else {
            showDialog();
            TextView tvTitleBarang = dialog.findViewById(R.id.tvTitleBarang);
            TextView tvDesc = dialog.findViewById(R.id.etMasterKey);
            Button btnProses = dialog.findViewById(R.id.btnRelogin);
            ImageView btnClose = dialog.findViewById(R.id.btnClose);
            tvTitleBarang.setText("PERINGATAN");
            tvDesc.setText("Data yang sudah anda input akan hilang, apakah anda yakin??");
            btnProses.setText("YAKIN");
            btnClose.setOnClickListener(v -> dialog.dismiss());

            btnProses.setOnClickListener(v -> doCreateItem());
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed() {
        if (etDescription.getText().toString().equals("") && etQty.getText().toString().equals("")){
            super.onBackPressed();
        } else {
            showDialog();
            TextView tvTitleBarang = dialog.findViewById(R.id.tvTitleBarang);
            TextView tvDesc = dialog.findViewById(R.id.etMasterKey);
            Button btnProses = dialog.findViewById(R.id.btnRelogin);
            ImageView btnClose = dialog.findViewById(R.id.btnClose);
            tvTitleBarang.setText("PERINGATAN");
            tvDesc.setText("Data yang sudah anda input akan hilang, apakah anda yakin??");
            btnProses.setText("YAKIN");
            btnClose.setOnClickListener(v -> dialog.dismiss());

            btnProses.setOnClickListener(v -> doCreateItem());
        }
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    @OnClick(R.id.imgBarang)
    void onClickImage() {
        selectImage();
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setTitle("Select Option");
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
            } else if (options[item].equals("Choose From Gallery")) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }

        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photoImage = (Bitmap) data.getExtras().get("data");
            photoImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            imgBarang.setImageBitmap(photoImage);
            try {
                File outputDir = getCacheDir();
                imageCheck = File.createTempFile("photo", "jpeg", outputDir);
                FileOutputStream outputStream = openFileOutput("photo.jpeg", Context.MODE_PRIVATE);
                outputStream.write(stream.toByteArray());
                outputStream.close();
                Log.d("Write File", "Success");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Write File", "Failed2");
            }
        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                imgBarang.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setItemSpinner() {
        Loading.show(context);
        itemNameList = new ArrayList<>();
        itemIdList = new ArrayList<>();
        stockIdList = new ArrayList<>();

        ApiLocal.apiInterface().getItemsList(loginStockLocation.location_id, "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<StockResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<StockResponse>>> call, Response<ApiResponse<List<StockResponse>>> response) {
                Loading.hide(context);
                try {
                    List<StockResponse> res = response.body().getData();
                    itemNameList.add("Pilih Barang");
                    itemIdList.add("KI");
                    stockIdList.add("KL");
                    for (int i = 0; i < res.size(); i++) {
                        StockResponse stockResponse = res.get(i);
                        itemNameList.add(stockResponse.getItem().getName());
                        stockIdList.add(stockResponse.getId()+"");
                        itemIdList.add(stockResponse.getItem().getId()+"");
                    }

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CreateDamagedItemActivity.this, R.layout.layout_spinner_text, itemNameList) {
                        @Override
                        public boolean isEnabled(int position) {
                            return position != 0;
                        }
                    };

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
                    // attaching data adapter to spinner
                    spinnerItem.setAdapter(dataAdapter);
                    spinnerItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position != 0) {
                                stockId = stockIdList.get(position);
                                itemId = itemIdList.get(position);
                            } else {
                                stockId = null;
                                itemId = null;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<StockResponse>>> call, Throwable t) {
                Log.d("TAG OnFailure selected", t.getMessage());
//                swipeRefresh.setRefreshing(false);
                Loading.hide(context);
            }
        });

    }

    private void doCreateItem(){
        if (etDescription.getText().toString().equals("") ||
                etQty.getText().toString().equals("") || itemId == null || stockId == null) {
            Toast.makeText(context, "Mohon lengkapi data", Toast.LENGTH_SHORT).show();
        } else {
            Loading.show(context);
            RequestBody requestBody;

            if (photoImage != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                photoImage.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("stock_id", stockId)
                        .addFormDataPart("item_id", itemId)
                        .addFormDataPart("location_id", loginStockLocation.location_id)
                        .addFormDataPart("location_operation_id", PreferenceManager.getOperationData().getId() + "")
                        .addFormDataPart("qty", etQty.getText().toString())
                        .addFormDataPart("description", etDescription.getText().toString())
                        .addFormDataPart("image", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                        .build();

            } else if (selectedImage != null) {
                File file = createTempFile(selectedImage);
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("stock_id", stockId)
                        .addFormDataPart("item_id", itemId)
                        .addFormDataPart("location_id", loginStockLocation.location_id)
                        .addFormDataPart("location_operation_id", PreferenceManager.getOperationData().getId() + "")
                        .addFormDataPart("qty", etQty.getText().toString())
                        .addFormDataPart("description", etDescription.getText().toString())
                        .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                        .build();
            } else {
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("stock_id", stockId)
                        .addFormDataPart("item_id", itemId)
                        .addFormDataPart("location_id", loginStockLocation.location_id)
                        .addFormDataPart("location_operation_id", PreferenceManager.getOperationData().getId() + "")
                        .addFormDataPart("qty", etQty.getText().toString())
                        .addFormDataPart("description", etDescription.getText().toString())
                        .build();
            }

            ApiLocal.apiInterface().createDamagedItem(requestBody, "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<DamagedStocks>>() {
                @Override
                public void onResponse(Call<ApiResponse<DamagedStocks>> call, Response<ApiResponse<DamagedStocks>> response) {
                    try {
                        Loading.hide(context);
                        if (response.isSuccessful()){
                            Toast.makeText(context, "Berhasil tambah barang rusak", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, DamagedItemActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<DamagedStocks>> call, Throwable t) {
                    Loading.hide(context);
                    Log.d("TAG", t.getMessage());
                }
            });
        }
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

    private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(CreateDamagedItemActivity.this));
        //set content
        dialog.setContentView(R.layout.dialog_session_expired);
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