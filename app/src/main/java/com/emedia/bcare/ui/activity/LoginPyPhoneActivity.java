package com.emedia.bcare.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.emedia.bcare.Config.BCareApp;
import com.emedia.bcare.Config.ContextWrapper;
import com.emedia.bcare.R;
import com.emedia.bcare.cash.SharedUser;
import com.emedia.bcare.data.model.ErrorUtils;
import com.emedia.bcare.data.model.api_model.login.LoginErrorMain2;
import com.emedia.bcare.data.model.api_model.register.Registeration;
import com.emedia.bcare.interfaces.nework.ILogin;
import com.emedia.bcare.network.RequestSingletone;
import com.example.fontutil.EditTextCustomFont;
import com.google.gson.Gson;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedia.bcare.util.HelperMethod.intentTo;


public class LoginPyPhoneActivity extends AppCompatActivity {

    @BindView(R.id.progress_view)
    ProgressBar progress_view;
    @BindView(R.id.ET_LoginPyPhoneNumber)
    EditTextCustomFont ETLoginPyPhoneNumber;
    @BindView(R.id.ET_LoginPyPhonePassword)
    EditTextCustomFont ETLoginPyPhonePassword;

    @BindView(R.id.IV_BackToMainLogin)
    ImageView IVBackToMainLogin;

    //var
    private boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_py_phone);
        ButterKnife.bind(this);

        if (Locale.getDefault().getLanguage().equals("ar")) {
            IVBackToMainLogin.setRotationY(getResources().getInteger(R.integer.Image_Locale_RTL_Mood));
        } else {
            IVBackToMainLogin.setRotationY(getResources().getInteger(R.integer.Image_locale_LTR_Mood));
        }

        startTest();
    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ContextWrapper.wrap(newBase));
    }

    @OnClick({R.id.IV_BackToMainLogin,
            R.id.BTN_LoginPyPhone,
            R.id.TV_LoginDoNotHaveAccountLink,
            R.id.IV_LoginPyPhShowPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.IV_BackToMainLogin:
                intentTo(this, LoginMainActivity.class);
                break;
            case R.id.TV_LoginDoNotHaveAccountLink:
                intentTo(this, RegisterActivity.class);
                break;
            case R.id.IV_LoginPyPhShowPassword:
                togglePassVisibility(ETLoginPyPhonePassword);
                break;
            case R.id.BTN_LoginPyPhone:
                //TODO when the Login Button Clicked
                doLogin();
                break;
        }
    }

    protected void doLogin() {
        if (! ETLoginPyPhoneNumber.getText().toString().trim().equals("")
                && ! ETLoginPyPhonePassword.getText().toString().trim().equals("")) {
            //startActivity(new Intent(Login2Activity.this, SalonActivity.class));
            showLoading();
            RequestSingletone.getInstance().getClient()
                    .create(ILogin.class)
                    .doLogginByMobile(ETLoginPyPhoneNumber.getText().toString().trim(),
                            ETLoginPyPhonePassword.getText().toString().trim())
                    .enqueue(new Callback<Registeration>() {
                        @Override
                        public void onResponse(Call<Registeration> call, Response<Registeration> response) {
                            hideLoading();

                            if (response.isSuccessful()) {
                                System.out.println("ret rsponse : " + String.valueOf(response.body()));
                                System.out.println("ret rsponse : " + String.valueOf(new Gson().toJson(response.body())));
                                Toast.makeText(BCareApp.getInstance().getContext(),
                                        "Welcome " + response.body().getData().get(0).getName(), Toast.LENGTH_SHORT).show();

                                SharedUser.getSharedUser().setEmail(response.body().getData().get(0).getEmail());
                                SharedUser.getSharedUser().setName(response.body().getData().get(0).getName());
                                SharedUser.getSharedUser().setToken(response.body().getData().get(0).getUsersSocail().getAccessToken());
                                SharedUser.getSharedUser().setAddress("");
                                SharedUser.getSharedUser().setCityid(response.body().getData().get(0).getCityId());
                                SharedUser.getSharedUser().setCountryid(response.body().getData().get(0).getCountryId());
                                SharedUser.getSharedUser().setPhone(response.body().getData().get(0).getMobile());
                                SharedUser.getSharedUser().setPhoto(response.body().getData().get(0).getProfilePicture());

                                startActivity(new Intent(LoginPyPhoneActivity.this, HomeActivity.class));
                            } else {
                                Toast.makeText(BCareApp.getInstance().getContext(), getResources().getString(R.string.check_data), Toast.LENGTH_SHORT).show();
                                LoginErrorMain2 error = ErrorUtils.parseError(response);
                                if (error.getData() != null) {
                                    Toast.makeText(getApplicationContext(), error.getData(), Toast.LENGTH_SHORT).show();

                                    if (error.getData().equals("Email is not verified")) {
                                        startActivity(new Intent(LoginPyPhoneActivity.this, VerficationActivity.class));
                                    }

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Registeration> call, Throwable t) {
                            Toast.makeText(BCareApp.getInstance().getContext(), "Failure", Toast.LENGTH_SHORT).show();
                            hideLoading();
                            //startActivity(new Intent(LoginPyPhoneActivity.this, HomeActivity.class));
                        }
                    });
        }
        else
        {
            Toast.makeText(this, getResources().getString(R.string.check_data), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * hide and Shaw password
     */
    private void togglePassVisibility(EditTextCustomFont editText) {
        if (isPasswordVisible) {
            String pass = editText.getText().toString();
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setText(pass);
            editText.setSelection(pass.length());

        } else {
            String pass = editText.getText().toString();
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setText(pass);
            editText.setSelection(pass.length());
        }
        isPasswordVisible = !isPasswordVisible;
    }

    public void showLoading() {
        if(this != null)
            progress_view.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        if(this != null)
            progress_view.setVisibility(View.GONE);
    }


    public void startTest() {
        //ETLoginEmail.setText("mohamed0110@yahoo.com");
        ETLoginPyPhoneNumber.setText("01005220568");
        ETLoginPyPhonePassword.setText("12345678");
    }
}
