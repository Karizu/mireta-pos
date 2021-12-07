package com.boardinglabs.mireta.standalone.modul.transactions.items.pembayaran;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.network.Api;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.Members;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.util.Constant;
import com.boardinglabs.mireta.standalone.component.util.Loading;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.history.DetailTransactionActivity;
import com.boardinglabs.mireta.standalone.modul.home.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PembayaranSuksesActivity extends BaseActivity {

    private String order_no, total, whatToDo,sisaSaldo;
    private long nomBayar, mTotal;
    public int saldo = 0;
    private int PAYMENT_METHOD;
    private int QRIS_METHOD = 2;
    private String mNomBayar, totals, order_date, order_time, member_id, member_name, member_lulusan, member_angkatan, sKembalian;

    @BindView(R.id.btnSelesai)
    LinearLayout btnSelesai;
    @BindView(R.id.btnPrintStruk)
    LinearLayout btnPrintStruk;
    @BindView(R.id.tvKembalian)
    TextView tvKembalian;
    @BindView(R.id.tv_desc)
    TextView tv_desc;
    @BindView(R.id.tv_text_button)
    TextView tv_text_button;
    @BindView(R.id.btnBayar)
    LinearLayout btnBayar;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pembayaran_sukses;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Pembayaran Sukses");

        Intent intent = getIntent();
        PAYMENT_METHOD = intent.getIntExtra("payment_method", 0);
        order_no = intent.getStringExtra("order_no");
        order_date = intent.getStringExtra("order_date");
        sKembalian = intent.getStringExtra("kembalian");
        sisaSaldo = intent.getStringExtra("sisaSaldo");
        total = intent.getStringExtra("total");
        mNomBayar = intent.getStringExtra("nomBayar");
        totals = intent.getStringExtra("mTotal");
        order_time = intent.getStringExtra("order_time");
        member_name = intent.getStringExtra("member_name");
        member_lulusan = intent.getStringExtra("member_lulusan");
        member_angkatan = intent.getStringExtra("member_angkatan");
        whatToDo = null;
        try {
            whatToDo = intent.getStringExtra("whatToDo");
        } catch (Exception e){
            e.printStackTrace();
        }

        int noBayar = Integer.parseInt(mNomBayar);
        int nTotal = Integer.parseInt(totals);
        nomBayar = noBayar;
        mTotal = nTotal;
        tvKembalian.setText("Rp " + MethodUtil.toCurrencyFormat(sKembalian) + "");

        if (PAYMENT_METHOD == QRIS_METHOD) {
            tv_desc.setText("Transaksi anda menunggu pembayaran");
            tv_text_button.setText("Bayar Disini");
            btnBayar.setVisibility(View.VISIBLE);
            btnPrintStruk.setVisibility(View.GONE);
        }

//        if (nomBayar != 0){
//            try {
//                long total = mTotal;
//                total = (nomBayar - total);
//                if (total >= 0) {
//                    tvKembalian.setText("Rp " + MethodUtil.toCurrencyFormat(Long.toString(total)) + "");
//                } else {
//                    tvKembalian.setText("Rp "+MethodUtil.toCurrencyFormat("0") + "");
//                }
//            } catch (Exception e) {
//                tvKembalian.setText(" ");
//            }
//        }
    }

    public void getSisaSaldo(String member_id){
        Loading.show(context);
        Api.apiInterface().cekSaldo(member_id, Constant.PARTNER_ID_NURUL_FIKRI, "Bearer "+ PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<Members>>() {
            @Override
            public void onResponse(Call<ApiResponse<Members>> call, Response<ApiResponse<Members>> response) {
                Loading.hide(context);
                try {
                    Members members = response.body().getData();
                    saldo = Integer.parseInt(members.getBalance());
                    Log.d("SISA SALDO", saldo+"");
                } catch (Exception e){
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

    @Override
    protected void onCreateAtChild() {

    }

    @Override
    protected void onBackBtnPressed() {
        Intent intent = new Intent(PembayaranSuksesActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    @OnClick(R.id.btnPrintStruk)
    void onClickBtnPrintStruk(){
        Intent intent = new Intent(PembayaranSuksesActivity.this, DetailTransactionActivity.class);
        intent.putExtra("payment_method", PAYMENT_METHOD);
        intent.putExtra("order_no", order_no);
        intent.putExtra("total", total);
        intent.putExtra("nomBayar", mNomBayar);
        intent.putExtra("kembalian", sKembalian);
        intent.putExtra("sisaSaldo", sisaSaldo+"");
        intent.putExtra("order_date", order_date);
        intent.putExtra("order_time", order_time);
        intent.putExtra("member_name", member_name);
        intent.putExtra("member_lulusan", member_lulusan);
        intent.putExtra("member_angkatan", member_angkatan);
        intent.putExtra("whatToDo", Constant.DO_PRINT);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.btnBayar)
    void onClickBtnBayar(){
        Intent intent = new Intent(PembayaranSuksesActivity.this, DetailTransactionActivity.class);
        intent.putExtra("payment_method", PAYMENT_METHOD);
        intent.putExtra("order_no", order_no);
        intent.putExtra("total", total);
        intent.putExtra("nomBayar", mNomBayar);
        intent.putExtra("kembalian", sKembalian);
        intent.putExtra("sisaSaldo", sisaSaldo+"");
        intent.putExtra("order_date", order_date);
        intent.putExtra("order_time", order_time);
        intent.putExtra("member_name", member_name);
        intent.putExtra("member_lulusan", member_lulusan);
        intent.putExtra("member_angkatan", member_angkatan);
        intent.putExtra("whatToDo", "2");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.btnSelesai)
    void onClickBtnSelesai(){
        Intent intent = new Intent(PembayaranSuksesActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PembayaranSuksesActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}