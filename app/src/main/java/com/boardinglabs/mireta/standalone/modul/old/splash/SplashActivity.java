package com.boardinglabs.mireta.standalone.modul.old.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import com.boardinglabs.mireta.standalone.modul.ardi.HomeArdiActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.boardinglabs.mireta.standalone.BuildConfig;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.home.HomeActivity;
import com.boardinglabs.mireta.standalone.modul.auth.login.LoginActivity;

import org.jsoup.Jsoup;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dhimas on 11/23/17.
 */

public class SplashActivity extends Activity {
    private String currentVersion;
    private ImageView splashOne;
    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        new PreferenceManager(this);
        splashOne = (ImageView) findViewById(R.id.img_splash_1);
        try {
            String image = PreferenceManager.getStockLocation().location.getBrand().getLogo_image_url();
            Glide.with(this)
                    .load(image)
                    .placeholder(R.drawable.mireta_logo_stacked)
                    .into(splashOne);
        } catch (Exception e){
            e.printStackTrace();
        }

        title = findViewById(R.id.title);
        try {
            title.setText(PreferenceManager.getStockLocation().business.name);
        } catch (Exception e){
            title.setText("Pempek Duo");
        }

//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();w
//
//        //To displaying token on logcat
//        Log.d("kunamTOKEN: ", refreshedToken);
//        Log.d("kunamTOKEN2: ", String.valueOf(FirebaseInstanceId.getInstance().getToken()));

//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                String newToken = instanceIdResult.getToken();
//                Log.d("kunamTOKEN: ", newToken);
//            }
//        });

        String uniqueID = UUID.randomUUID().toString();
        if (TextUtils.isEmpty(PreferenceManager.getImei())) {
//            PreferenceManager.setImei(uniqueID);
//            if(FirebaseInstanceId.getInstance().getToken().length() > 0){
//                PreferenceManager.setImei(refreshedToken);
//            }else{
                PreferenceManager.setImei(uniqueID);
//            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("promo");
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new GetVersionCode().execute();
    }

    private class GetVersionCode extends AsyncTask<Void, String, String>{
        @Override
        protected String doInBackground(Void... params) {
            String newVersion;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + SplashActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();

                return newVersion;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null) {
                String newVersion = onlineVersion;
                if (newVersion != null && !newVersion.isEmpty()) {
                    if (BuildConfig.FLAVOR.equalsIgnoreCase("production")
                            || BuildConfig.FLAVOR.equalsIgnoreCase("postrelease")
                            || BuildConfig.FLAVOR.equalsIgnoreCase("productionpampasy")) {
                        if (!newVersion.equalsIgnoreCase(currentVersion)) {
                            DialogPlus dialog = DialogPlus.newDialog(SplashActivity.this)
                                    .setContentHolder(new ViewHolder(R.layout.content_dialog))
                                    .setCancelable(false)
                                    .setGravity(Gravity.BOTTOM)
                                    .setExpanded(false)
                                    .setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(DialogPlus dialog, View view) {
                                            switch (view.getId()) {
                                                case R.id.token_pln:
                                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }
                                                    finish();
                                                    break;
                                                case R.id.tagihan_pln:
                                                    break;
                                            }
                                            dialog.dismiss();
                                        }
                                    })
                                    .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setOverlayBackgroundResource(R.color.starDust_opacity_90)
                                    .create();
                            View view = dialog.getHolderView();
                            TextView title = (TextView) view.findViewById(R.id.title);
                            Button first = (Button) view.findViewById(R.id.tagihan_pln);
                            Button second = (Button) view.findViewById(R.id.token_pln);

                            title.setText("Versi terbaru sudah tersedia, silahkan lakukan pembaharuan");
                            first.setVisibility(View.GONE);
                            second.setText("Ya");
                            dialog.show();
                        } else {
                            downTimer();
                        }
                    } else {
                        downTimer();
                    }

                } else {
                    downTimer();
                }
            } else {
                downTimer();
            }

        }
    }

    private void downTimer() {
        long futureMillis = TimeUnit.SECONDS.toMillis(2);
        new CountDownTimer(futureMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                if (seconds == 0) {
                    cancel();
                    onFinish();
                }
            }

            @Override
            public void onFinish() {
                if (PreferenceManager.isLogin()) {
                    gotoHomePage();
                } else {
                    if (PreferenceManager.isArdi()) {
                        gotoHomePageArdi();
                    } else {
                        gotoLoginPage();
                    }
                }
            }
        }.start();
    }

    private void gotoLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoHomePageArdi() {
        Intent intent = new Intent(this, HomeArdiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
