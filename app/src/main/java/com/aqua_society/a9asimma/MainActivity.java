package com.aqua_society.a9asimma;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aqua_society.a9asimma.Utils.KEYS;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText inputName, inputEmail, inputTele;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutTele;
    private Button btnSignUp, btnNext;
    private LinearLayout Loading_area, user_info_collect;
    private ListView listVilles;
    private TextView click_signUp_message;
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    NativeExpressAdView native_adView;


    private int curent = -1;

    boolean  isValid = false;

    String villeList[] = {
            "الدار البيضاء",
            "الرباط",
            "فاس",
            "طنجة",
            "المحمدية",
            "بني ملال"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        facebookInit();

        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewInit();

        //Add Initialisation
        banner_AdsInit();
        interstitial_AddInit();
        native_AdsInit();
    }

    private void native_AdsInit(){
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        native_adView.loadAd(request);
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
                ShowNext();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private  void facebookInit(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
    }

    private void viewInit(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Loading_area = (LinearLayout) findViewById(R.id.Loading_area);
        user_info_collect = (LinearLayout) findViewById(R.id.user_info_collect);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutTele = (TextInputLayout) findViewById(R.id.input_layout_tele);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputTele = (EditText) findViewById(R.id.input_tele);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnNext = (Button) findViewById(R.id.btn_next);
        listVilles = (ListView) findViewById(R.id.listVilles);
        click_signUp_message = (TextView) findViewById(R.id.click_signUp_message);
        native_adView = (NativeExpressAdView) findViewById(R.id.adView_native);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputTele.addTextChangedListener(new MyTextWatcher(inputTele));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid){
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        ShowNext();
                    }
                }
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.listville_row, R.id.lists_ville_row, villeList);
        listVilles.setAdapter(arrayAdapter);
        listVilles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listVilles.setVisibility(View.GONE);
                btnSignUp.setVisibility(View.VISIBLE);
                click_signUp_message.setVisibility(View.VISIBLE);
                Toast.makeText(getBaseContext(),"Selected : "+villeList[position], Toast.LENGTH_LONG).show();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void ShowNext(){
        if(isValid){
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            curent++;
            switch (curent){
                case 0 :    inputLayoutName.setVisibility(View.GONE);
                    inputLayoutEmail.setVisibility(View.VISIBLE);
                    inputLayoutTele.setVisibility(View.GONE);
                    listVilles.setVisibility(View.GONE);
                    break;
                case 1 :    inputLayoutName.setVisibility(View.GONE);
                    inputLayoutEmail.setVisibility(View.GONE);
                    inputLayoutTele.setVisibility(View.VISIBLE);
                    listVilles.setVisibility(View.GONE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnSignUp.setVisibility(View.GONE);
                    break;
                case 2 :    inputLayoutName.setVisibility(View.GONE);
                    inputLayoutEmail.setVisibility(View.GONE);
                    inputLayoutTele.setVisibility(View.GONE);
                    listVilles.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.GONE);
                    btnSignUp.setVisibility(View.GONE);
                    break;

            }
        }
    }

    private void submitForm(){
        hideKeybord();
        user_info_collect.setVisibility(View.GONE);
        Loading_area.setVisibility(View.VISIBLE);


        final Handler handler = new Handler();
        Random rn = new Random();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent user_info = new Intent(MainActivity.this, UserInfoActivity.class);
                user_info.putExtra(KEYS.USER_NAME_INTENT, inputName.getText().toString());
                user_info.putExtra(KEYS.USER_EMAIL_INTENT, inputEmail.getText().toString());
                user_info.putExtra(KEYS.USER_TELE_INTENT, inputTele.getText().toString());
                MainActivity.this.startActivity(user_info);
            }
        }, (rn.nextInt(10 - 3 + 1)) * 500);
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            isValid = false;
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        isValid = true;
        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            isValid = false;
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        isValid = true;
        return true;
    }

    private boolean validateTele() {
        if (inputTele.getText().toString().trim().isEmpty()) {
            inputLayoutTele.setError(getString(R.string.err_msg_password));
            requestFocus(inputTele);
            isValid = false;
            return false;
        } else {
            inputLayoutTele.setErrorEnabled(false);
        }
        isValid = true;
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_tele:
                    validateTele();
                    break;
            }
        }
    }

    public void hideKeybord() {
        InputMethodManager imm = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(user_info_collect.getWindowToken(), 0);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        else{

            inputLayoutName.setVisibility(View.VISIBLE);
            inputLayoutEmail.setVisibility(View.GONE);
            inputLayoutTele.setVisibility(View.GONE);
            listVilles.setVisibility(View.GONE);
            listVilles.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.GONE);
            click_signUp_message.setVisibility(View.GONE);
            curent = -1;

        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
