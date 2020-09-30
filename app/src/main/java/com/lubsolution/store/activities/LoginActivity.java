package com.lubsolution.store.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lubsolution.store.R;
import com.lubsolution.store.apiconnect.apiserver.FCMMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackString;
import com.lubsolution.store.libraries.FitScrollWithFullscreen;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomCenterDialog;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.Transaction;
import com.lubsolution.store.utils.Util;

/**
 * Created by macos on 9/13/17.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText edUsername, edPassword;
    private Button btnSubmit;
    private TextView btnKeyboard, tvLostPassword;
    private Boolean detectNumberKeyboard = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FitScrollWithFullscreen.assistActivity(this, 2);

    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_login;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        edUsername = (EditText) findViewById(R.id.login_username);
        edPassword = (EditText) findViewById(R.id.login_password);
        btnSubmit = (Button) findViewById(R.id.login_submit);
        btnKeyboard = findViewById(R.id.login_keyboard);
        tvLostPassword = findViewById(R.id.login_lost_password);

    }

    @Override
    public void initialData() {
        edUsername.setInputType(InputType.TYPE_CLASS_NUMBER);
        edUsername.setText(CustomSQL.getString(Constants.USER_USERNAME));

    }

    @Override
    public void addEvent() {
        btnSubmit.setOnClickListener(this);
        btnKeyboard.setOnClickListener(this);
        tvLostPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.login_submit:
                submitLogin();

                break;

            case R.id.login_keyboard:
                if (!detectNumberKeyboard) {
                    edUsername.setInputType(InputType.TYPE_CLASS_NUMBER);

                } else {
                    edUsername.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                detectNumberKeyboard = !detectNumberKeyboard;

                break;

            case R.id.login_lost_password:
                CustomCenterDialog.alertWithButtonCanceled("Quên mật khẩu!",
                        "Liên hệ Admin để cài đặt lại mật khẩu",
                        "ĐỒNG Ý", false, new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean result) {

                            }
                        });

                break;
        }

    }

    private void submitLogin() {
        if (Util.isEmpty(edUsername) || Util.isEmpty(edPassword)) {
            Util.showToast("Vui lòng nhập đủ thông tin");

        } else {
            FCMMethod.getFCMToken(new CallbackString() {
                @Override
                public void Result(String token) {
                    if (!token.equals("")) {
                        login(edUsername.getText().toString().trim(),
                                edPassword.getText().toString().trim(),
                                token,
                                new CallbackBoolean() {
                                    @Override
                                    public void onRespone(Boolean result) {
                                        if (result) {
                                            Util.showToast("Đăng nhập thành công");
                                            Transaction.gotoHomeActivity();
                                            CustomSQL.setBoolean(Constants.LOGIN_SUCCESS, true);

                                        }
                                    }
                                });

                    } else {
                        Util.showSnackbar("Đăng nhập thất bại", "Thử lại", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                submitLogin();
                            }

                        });
                    }
                }
            });

        }
    }

}
