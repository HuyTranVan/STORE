package com.lubsolution.store.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lubsolution.store.BuildConfig;
import com.lubsolution.store.R;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomCenterDialog;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.Transaction;

import java.util.List;


public class SplashScreenActivity extends BaseActivity {
    private ProgressBar progressBar;
    private TextView tvVersion;

    private int SPLASH_TIME_OUT = 500;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        progressBar = findViewById(R.id.splash_loading);
        tvVersion = findViewById(R.id.splash_version);
    }

    @Override
    public void initialData() {
        openUri();
        tvVersion.setText(String.format("Version %s", BuildConfig.VERSION_NAME));
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLogin(new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        progressBar.setVisibility(View.GONE);
                        if (result) {
                            Transaction.gotoHomeActivity();

                        } else {
                            gotoLoginScreen();

                        }
                    }
                });

            }

        }, SPLASH_TIME_OUT);


    }

    @Override
    public void addEvent() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void gotoLoginScreen() {
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intent);
        SplashScreenActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        CustomSQL.clear();
        CustomSQL.setInt(Constants.VERSION_CODE, BuildConfig.VERSION_CODE);

        finish();
    }

    private void checkLogin(CallbackBoolean listener) {
        BaseModel param = createGetParam(ApiUtil.CHECK_LOGIN(), false);

        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                if (result.getInt("version") > BuildConfig.VERSION_CODE) {
                    progressBar.setVisibility(View.GONE);
                    CustomCenterDialog.alertWithCancelButton("PHIÊN BẢN MỚI",
                            "Có 1 bản cập nhật mới trên Store \nVui lòng cập nhật",
                            "đồng ý",
                            "không", new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (!result) {
                                        finish();

                                    } else {
                                        Transaction.openGooglePlay();


                                    }
                                }
                            });


                } else {
                    if (result.getBoolean("success")) {
                        listener.onRespone(true);
                    } else {
                        listener.onRespone(false);
                    }

                }

            }

            @Override
            public void onError(String error) {

            }
        }, 0).execute();


    }


    private void openUri(){
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            if (data.getQuery().contains("id")){
                CustomSQL.setString(Constants.CUSTOMER_ID, data.getQueryParameter("id"));

            }
        }


    }


}