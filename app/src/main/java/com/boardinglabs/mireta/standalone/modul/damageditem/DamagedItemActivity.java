package com.boardinglabs.mireta.standalone.modul.damageditem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.ApiLocal;
import com.boardinglabs.mireta.standalone.component.network.entities.DamagedStocks.DamagedStocks;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.TambahBarangActivity;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DamagedItemActivity extends BaseActivity {

    private List<DamagedStocks> katalogModels;
    private List<String> itemImages;
    private Context context;
    private DamagedItemAdapter adapter;
    private static final int CAMERA_REQUEST_CODE = 1;
    private final int REQEUST_CAMERA = 1, REQUEST_GALLERY = 2;
    private Bitmap selectedImage;
    private Bitmap photoImage;
    public String location_id, business_id;

    @BindView(R.id.btnTambah)
    LinearLayout btnTambah;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.laySearch)
    LinearLayout laySearch;
    @BindView(R.id.header_view)
    LinearLayout header_view;
    @BindView(R.id.etSearch)
    EditText etSearch;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_damaged_item;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("BARANG RUSAK");

        context = this;

        location_id = loginStockLocation.location_id;
        business_id = loginBusiness.id;

        katalogModels = new ArrayList<>();
        itemImages = new ArrayList<>();
        setListDamagedItem();

        swipeRefresh.setOnRefreshListener(() -> {
            katalogModels.clear();
            adapter.notifyDataSetChanged();
            setListDamagedItem();
        });

        btnTambah.setOnClickListener(v -> {
            startActivity(new Intent(DamagedItemActivity.this, CreateDamagedItemActivity.class));
        });
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

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photoImage = (Bitmap) data.getExtras().get("data");
            photoImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Toast.makeText(context, "Please click button refresh", Toast.LENGTH_LONG).show();
            try {
                File outputDir = getCacheDir();
                File imageCheck = File.createTempFile("photo", "jpeg", outputDir);
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
                Toast.makeText(context, "Please click button refresh", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    @OnClick(R.id.imgClose)
    void onCLickClose(){
        etSearch.setText("");
        header_view.setVisibility(View.VISIBLE);
        laySearch.setVisibility(View.GONE);
    }

    @OnClick(R.id.imgSearch)
    void onClickSearch(){
        header_view.setVisibility(View.GONE);
        laySearch.setVisibility(View.VISIBLE);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<DamagedStocks> katalogModels = new ArrayList<>();
                String newTextLowerCase = etSearch.getText().toString().toLowerCase();
                for (DamagedStocks katalog : DamagedItemActivity.this.katalogModels) {
                    if (katalog.getItem().getName().toLowerCase().contains(newTextLowerCase)) {
                        katalogModels.add(katalog);
                    }
                }
                if (katalogModels.size() >= 1){
                    adapter.updateData(katalogModels);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setListDamagedItem(){
        swipeRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getDamagedStocks(PreferenceManager.getOperationData().getId()+"", "Bearer "+ PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<DamagedStocks>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DamagedStocks>>> call, Response<ApiResponse<List<DamagedStocks>>> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    List<DamagedStocks> res = Objects.requireNonNull(response.body()).getData();
                    if (res.size() < 1){
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    adapter = new DamagedItemAdapter(res, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<DamagedStocks>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}