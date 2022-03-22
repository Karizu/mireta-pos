package com.boardinglabs.mireta.standalone.modul.expenditure;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Constant;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.akun.AkunActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.TambahBarangActivity;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.model.ItemResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class CreateExpenditureActivity extends BaseActivity {

    private static final int CAMERA_REQUEST_CODE = 111;
    private final int REQEUST_CAMERA = 1, REQUEST_GALLERY = 2;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private String subKategoriesId, kategoriesId, isDaily;
    private Context context;
    private File imageCheck;
    private Bitmap selectedImage;
    private Bitmap photoImage;
    private RequestBody requestBody;
    private Dialog dialog;
    private Spinner spinnerSubKategori;
    private File fileFromCamera;

    @BindView(R.id.imgBarang)
    ImageView imgBarang;
    @BindView(R.id.etExpenditureName)
    EditText etExpenditureName;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.etNominal)
    EditText etNominal;

    @OnClick(R.id.btnTambah)
    void createItem() {
        doCreateItem();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_create_expenditure;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Pengeluaran");

        context = this;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 0);
        }

//        switchStok.setOnCheckedChangeListener((buttonView, isChecked) -> isDaily = ""+isChecked);
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
            photoImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

            try {
//                fileFromCamera = createTempFile(photoImage);
                Uri tempUri = getImageUri(getApplicationContext(), photoImage);
//                fileFromCamera = new File(getRealPathFromURI(tempUri));
                final InputStream imageStream = getContentResolver().openInputStream(tempUri);
//                photoImage = BitmapFactory.decodeStream(imageStream);
                photoImage = MethodUtil.bitmapPhoto(imgBarang, getRealPathFromURI(tempUri));
                fileFromCamera = savebitmap(photoImage);
                imgBarang.setImageBitmap(photoImage);
            } catch (Exception e){
                e.printStackTrace();
            }

//            imgBarang.setImageBitmap(photoImage);

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

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        // String temp = null;
        File file = new File(extStorageDirectory, "temp.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "temp.png");

        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void doCreateItem(){
        if (etExpenditureName.getText().toString().equals("") ||
                etNominal.getText().toString().equals("")) {
            Toast.makeText(context, "Mohon lengkapi data", Toast.LENGTH_SHORT).show();
        } else {
            Loading.show(context);
            RequestBody requestBody;

            if (photoImage != null) {
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name", etExpenditureName.getText().toString())
                        .addFormDataPart("description", etDescription.getText().toString())
                        .addFormDataPart("location_id", loginStockLocation.location_id)
                        .addFormDataPart("location_operation_id", PreferenceManager.getOperationData().getId() + "")
                        .addFormDataPart("nominal", etNominal.getText().toString())
                        .addFormDataPart("image", fileFromCamera.getName(), RequestBody.create(MediaType.parse("image/*"), fileFromCamera))
                        .build();

            } else if (selectedImage != null) {
                File file = createTempFile(selectedImage);
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name", etExpenditureName.getText().toString())
                        .addFormDataPart("description", etDescription.getText().toString())
                        .addFormDataPart("location_id", loginStockLocation.location_id)
                        .addFormDataPart("location_operation_id", PreferenceManager.getOperationData().getId() + "")
                        .addFormDataPart("nominal", etNominal.getText().toString())
                        .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                        .build();

            } else {
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name", etExpenditureName.getText().toString())
                        .addFormDataPart("description", etDescription.getText().toString())
                        .addFormDataPart("location_id", loginStockLocation.location_id)
                        .addFormDataPart("location_operation_id", PreferenceManager.getOperationData().getId() + "")
                        .addFormDataPart("nominal", etNominal.getText().toString())
                        .build();
            }

            ApiLocal.apiInterface().addExpenditure(requestBody, "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    try {
                        Loading.hide(context);
                        if (response.isSuccessful()){
                            Toast.makeText(context, "Berhasil tambah barang", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, ExpenditureActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            String msg = MethodUtil.getErrorResponse(response.errorBody().string());
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
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

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(CreateExpenditureActivity.this));
        //set content
        dialog.setContentView(R.layout.layout_tambah_kategori);
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