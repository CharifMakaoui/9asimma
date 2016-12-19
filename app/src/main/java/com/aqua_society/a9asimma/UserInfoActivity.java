package com.aqua_society.a9asimma;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aqua_society.a9asimma.Utils.KEYS;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Random;

public class UserInfoActivity extends AppCompatActivity {

    private String user_name, user_email, user_tele;
    private TextView text_userName;
    private ImageView img_rat;
    AdView mAdView;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Intent intent = getIntent();
        user_name = intent.getExtras().getString(KEYS.USER_NAME_INTENT);
        user_email = intent.getExtras().getString(KEYS.USER_EMAIL_INTENT);
        user_tele = intent.getExtras().getString(KEYS.USER_TELE_INTENT);

        initView();

        banner_AdsInit();
        interstitial_AddInit();
    }

    private void initView(){
        text_userName = (TextView) findViewById(R.id.text_userName);
        img_rat = (ImageView) findViewById(R.id.img_rat);

        Log.d("KeyHash",user_name);
        text_userName.setText(user_name);

        img_rat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id="+getBaseContext().getString(R.string.market_id)));

                try{
                    startActivity(intent);
                }
                catch(Exception e){
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="
                            +getBaseContext().getString(R.string.market_id)));
                }
                startActivity(intent);
            }
        });
    }

    private void banner_AdsInit(){
        View adContainer = findViewById(R.id.adMobView);

        mAdView = new AdView(getBaseContext());
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getBaseContext().getString(R.string.banner_ad_unit_id));
        ((RelativeLayout) adContainer).addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void interstitial_AddInit(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getBaseContext().getString(R.string.interstitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();
        ShowNext();
    }

    private void ShowNext() {
        final Handler handler = new Handler();
        Random rn = new Random();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    ShowNext();
                }
            }
        }, (rn.nextInt(20 - 10 + 1)) * 10000);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


}
