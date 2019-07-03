package com.emedia.bcare.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.emedia.bcare.Config.ContextWrapper;
import com.emedia.bcare.cash.SharedUser;
import com.emedia.bcare.data.model.api_model.register.Registeration;
import com.emedia.bcare.interfaces.nework.ILogin;
import com.emedia.bcare.network.RequestSingletone;
import com.facebook.HttpMethod;
import com.emedia.bcare.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.internal.gmsg.HttpClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.twitter.ParseTwitterUtils;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import oauth.signpost.http.HttpResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.emedia.bcare.util.HelperMethod.intentTo;

public class RegisterActivity extends AppCompatActivity {


    //    @BindView(R.id.ET_SignUpPhone_)
//    EditText ET_SignUpPhone_;
//    @BindView(R.id.BTN_SignUp_)
//    ButtonCustomFont BTN_SignUp_;




    @BindView(R.id.progress_view)
    ProgressBar progress_view;

    RegisterActivity act;
    private View mLayoutBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up);
        ButterKnife.bind(this);

        this.act = this;

        checkForInstagramData();

//        BTN_SignUp_.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!ET_SignUpPhone_.getText().toString().trim().equals("")) {
//                    Intent i = new Intent(act, RegisterByEmail2.class);
//                    i.putExtra("phone", ET_SignUpPhone_.getText().toString().trim());
//                    startActivity(i);
//                } else {
//                }
//
//            }
//        });

        mLayoutBottomSheet = findViewById(R.id.layout_bottom_sheet);

        mBottomSheetBehavior = BottomSheetBehavior.from(mLayoutBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_DRAGGING);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        initializeSdks();

    }


//    @OnClick(R.id.BTN_SignUp_)
//    public void goToSignUpStep2() {
//    }

    public void showLoading()
    {
        if(this != null)
            progress_view.setVisibility(View.VISIBLE);
    }

    public void hideLoading()
    {
        if(this != null)
            progress_view.setVisibility(View.GONE);
    }

    @OnClick({R.id.BTN_BottomSheet, R.id.IV_cancel, R.id.IV_FacebookSignUp, R.id.IV_TwitterSignUp,
            R.id.IV_InstagramSignUp, R.id.IV_SnapshatSignUp, R.id.IV_GoogleSignUp, R.id.BTN_SignUpPyPhoneNumber, R.id.BTN_SignUpPyEmile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.BTN_BottomSheet:
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.IV_cancel:
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.IV_FacebookSignUp:
                doFacebookLogin();
                break;
            case R.id.IV_TwitterSignUp:
                doLoginTwitter();
                break;
            case R.id.IV_InstagramSignUp:
                doInstagramLogin();
                break;
            case R.id.IV_SnapshatSignUp:
                break;
            case R.id.IV_GoogleSignUp:
                break;
            case R.id.BTN_SignUpPyPhoneNumber:
                intentTo(this, RegisterByPhone1.class);
                break;
            case R.id.BTN_SignUpPyEmile:
                intentTo(this, RegisterByEmail1.class);
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ContextWrapper.wrap(newBase));
    }
    protected void initializeSdks() {
        Parse.enableLocalDatastore(getApplicationContext());

        // Register any ParseObject subclass. Must be done before calling Parse.initialize()
        //ParseObject.registerSubclass(YourClass.class);


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        ParseTwitterUtils.initialize("sXYlasO1Jn3W8q3f8kWdFYBkW",
                "COL9iTg6Hc5ARVea6XrrJBA9TJ4PFDOloJCHWzppXKqqZ0zbpw");


        //=================== FaceBook
        //=================== FaceBook
        //=================== FaceBook
        //=================== FaceBook
        FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);
        mFacebookCallbackManager = CallbackManager.Factory.create();

        mFacebookSignInButton = (LoginButton) findViewById(R.id.facebook_sign_in_button);
        mFacebookSignInButton.setReadPermissions(Arrays.asList("email"));

        mFacebookSignInButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {



                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code

                                        System.out.println("Success");
                                        String jsonresult = String.valueOf(object);
                                        System.out.println("JSON Result" + jsonresult);

                                        String fbUserId = object.optString("id");
                                        name = object.optString("name");
                                        email = object.optString("email");

                                        handleSignInResult(loginResult.getAccessToken().getToken(), name, email, new Callable<Void>() {
                                            @Override
                                            public Void call() throws Exception {
                                                LoginManager.getInstance().logOut();
                                                return null;
                                            }
                                        });
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        handleSignInResult("","", "",null);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(LoginActivity.class.getCanonicalName(), error.getMessage());
                        handleSignInResult("","", "", null);
                    }
                }
        );


        /*
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        mFacebookCallbackManager = CallbackManager.Factory.create();

        mFacebookSignInButton = (LoginButton) findViewById(R.id.facebook_sign_in_button);
        mFacebookSignInButton.setReadPermissions(Arrays.asList("email"));

        mFacebookSignInButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());
                                        // Application code
                                        System.out.println("Success");
                                        String jsonresult = String.valueOf(object);
                                        System.out.println("ret JSON Result " + jsonresult);
                                        System.out.println("ret Access Token " + AccessToken.getCurrentAccessToken());

                                        //String fbUserId = object.optString("id");
                                        name = object.optString("name");
                                        email = object.optString("email");
                                        handleSignInResult(name, email, new Callable<Void>() {
                                            @Override
                                            public Void call() throws Exception {
                                                LoginManager.getInstance().logOut();
                                                return null;
                                            }
                                        });
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        handleSignInResult("", "", null);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(LoginActivity.class.getCanonicalName(), error.getMessage());
                        handleSignInResult("", "", null);
                    }
                }
        );*/


        //================ Google
        //============ Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();


        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.disconnect();
                }

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestProfile()
                        .requestId()
                        .build();
                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    public void doLoginTwitter() {
        ParseTwitterUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Twitter!");
                } else {
                    Log.d("MyApp", "User logged in through Twitter!");
                }
            }
        });
        /*
        ParseTwitterUtils.logIn(RegisterActivity.this, new LogInCallback() {

            @Override
            public void done(final ParseUser user, ParseException err) {
                if (err != null) {
                    //dlg.dismiss();
                    ParseUser.logOut();
                    Log.e("err", "err", err);
                }
                if (user == null) {
                    //dlg.dismiss();
                    ParseUser.logOut();
                    Toast.makeText(RegisterActivity.this, "The user cancelled the Twitter login.", Toast.LENGTH_LONG).show();
                    Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                } else if (user.isNew()) {
                    //dlg.dismiss();
                    Toast.makeText(RegisterActivity.this, "User signed/logged : " + user.getUsername() , Toast.LENGTH_LONG).show();
                    Log.d("MyApp", "User signed up and logged in through Twitter!");
                    user.setUsername(ParseTwitterUtils.getTwitter().getScreenName());
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (null == e) {
                                alertDisplayer("First tome login!", "Welcome!");
                            } else {
                                ParseUser.logOut();
                                Toast.makeText(RegisterActivity.this, "It was not possible to save your username.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    //dlg.dismiss();
                    Toast.makeText(RegisterActivity.this, "User logged in through Twitter.", Toast.LENGTH_LONG).show();
                    Log.d("MyApp", "User logged in through Twitter!");
                    alertDisplayer("Oh, you!","Welcome back!");
                }
            }
        });
        */
    }


    private CallbackManager mFacebookCallbackManager;
    LoginButton mFacebookSignInButton;
    String name;
    String email;

    public void onClickFacebookButton(View view) {
        mFacebookSignInButton.performClick();
    }


    protected void doFacebookLogin() {

    }

    private void handleSignInResult(String accessTokenFacebook, final String name, String email, Callable<Void> logout) {

        RequestSingletone.getInstance().getClient().create(ILogin.class)
                .doLogginSocial(accessTokenFacebook, "facebook", name)
                .enqueue(new Callback<Registeration>() {
                    @Override
                    public void onResponse(Call<Registeration> call, Response<Registeration> response) {
                        if(response.body().getCode().equals("200"))
                        {
                            Toast.makeText(getApplicationContext(), "Welcome : " + name,  Toast.LENGTH_SHORT).show();
                            SharedUser.getSharedUser().setToken(response.body().getData().get(0).getUsersSocail().getAccessToken());
                            SharedUser.getSharedUser().setName(response.body().getData().get(0).getName());
                            SharedUser.getSharedUser().setEmail(response.body().getData().get(0).getEmail());

                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class) );
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Error in your account", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Registeration> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Failure in your account", Toast.LENGTH_SHORT).show();
                    }
                });

        if (logout == null) {
            /*------------------------------------ Login error ---------------------------------*/
            Toast.makeText(getApplicationContext(), "Error, Try Again", Toast.LENGTH_SHORT).show();
            //AccessToken.setCurrentAccessToken(null);
            //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
        } else {
            /* Login success */
            //BCareApp.getInstance().setLogoutCallable(logout);
            setLogoutCallable(logout);
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            System.out.println("ret facebook token : " + accessToken.getToken() + " ret facebook email : " + email);
            //Here Make Call To The Server MEdical Vision
            loginInServer(name, email, accessToken.getToken());
            //boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            //startActivity(new Intent(this, LoggedInActivity.class));
        }
    }

    public void loginInServer(final String name, final String email, String _token) {

        Toast.makeText(this, "Login server facebook", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
//        showLoading();
//        RequestSingltone.getInstance().getClient().create(ILogin.class).
//                doLogginSocial(email,
//                        "facebook",
//                        _token
//                ).
//                enqueue(new Callback<LoginSocialMain>() {
//                    @Override
//                    public void onResponse(Call<LoginSocialMain> call, Response<LoginSocialMain> response) {
//                        hideLoading();
//                        if(response.isSuccessful())
//                        {
//
//                            if(response.body().getCurrentUser() != null)
//                            {
//                                System.out.println("ret facebook register " + response.body().getToken() + " " + response.body().getCurrentUser().getName());
//                                MedicalApp.getInstance().updateToken(response.body().getToken());
//                                SharedUser.getSharedUser().setId(String.valueOf(response.body().getCurrentUser().getId()));
//                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                            }
//                            else
//                            {
//                                Intent i = new Intent(_act, SignUpActivity.class);
//                                i.putExtra("email", email);
//                                i.putExtra("name", name);
//                                startActivity(i);
//                            }
//                        }
//                        else
//                        {
//                            ErrorSignUpMain error = ErrorUtils.parseErrorSignUp(response);
//                            if(error.getErrors() != null)
//                            {
//                                MedicalApp.getInstance().makeToast("Please, check your data !", _act);
//
//                                if(error.getErrors().get(0).getEmail() != null)
//                                    MedicalApp.getInstance().makeToast(error.getErrors().get(0).getEmail(), _act);
//                                if(error.getErrors().get(0).getName() != null)
//                                    MedicalApp.getInstance().makeToast(error.getErrors().get(0).getName(), _act);
//                                if(error.getErrors().get(0).getPhone() != null)
//                                    MedicalApp.getInstance().makeToast(error.getErrors().get(0).getPhone(), _act);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<LoginSocialMain> call, Throwable t) {
//                        Log.i("ret", "failure.");
//                        MedicalApp.getInstance().makeToast("Login Failed !", _act);
//                    }
//                });

    }

    private Callable<Void> mLogoutCallable;

    public void setLogoutCallable(Callable<Void> callable) {
        mLogoutCallable = callable;
        System.out.println("ret faceebook logout");
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                System.out.println("ret faceebook logout 2");
                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    public void onClickGoogleButton(View view) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void alertDisplayer(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        // don't forget to change the line below with the names of your Activities
                        ParseUser.logOut();
                        alertDisplayer("So, you're going...", "Ok...Bye-bye then");
                        //Intent intent = new Intent(RegisterActivity.this, LogoutActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        //startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    @OnClick(R.id.TV_SignUP_HaveAccountLink)
    protected void signup() {
        startActivity(new Intent(this, LoginMainActivity.class));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess()) {
                final GoogleApiClient client = mGoogleApiClient;
                handleSignInLoginResult(result.getSignInAccount().getEmail(), result.getSignInAccount().getDisplayName(), result.getSignInAccount());
                //handleSignInResult(...)
            } else {
                //handleSignInResult(...);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void handleSignInLoginResult(final String email, final String name, GoogleSignInAccount _result)
    {
        System.out.println("ret google data "+  _result.getId() + "-" + _result.getIdToken() + "-" + _result.getEmail() + " name " + _result.getFamilyName() + " phone " + _result.getDisplayName());

        showLoading();
        //RequestSingltone.getInstance().getClient().create(ILogin.class).
        //        doLogginSocial(
        //                email,
        //                "google",
        //                _result.getId()
        //        ).
        //        enqueue(new Callback<LoginSocialMain>() {
        //            @Override
        //            public void onResponse(Call<LoginSocialMain> call, Response<LoginSocialMain> response) {
        //                hideLoading();
        //                if(response.isSuccessful())
        //                {
        //                    if(response.body().getCurrentUser() != null)
        //                    {
        //                        System.out.println("ret facebook register " + response.body().getToken() + " " + response.body().getCurrentUser().getName());
        //                        MedicalApp.getInstance().updateToken(response.body().getToken());
        //                        SharedUser.getSharedUser().setId(String.valueOf(response.body().getCurrentUser().getId()));
        //                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        //                    }
        //                    else
        //                    {
        //                        Intent i = new Intent(_act, SignUpActivity.class);
        //                        i.putExtra("email", email);
        //                        i.putExtra("name", name);
        //                        startActivity(i);
        //                    }
        //                }
        //                else
        //                {
        //                    ErrorSignUpMain error = ErrorUtils.parseErrorSignUp(response);
        //                    if(error.getErrors() != null)
        //                    {
        //                        MedicalApp.getInstance().makeToast("Please, check your data !", _act);
//
        //                        if(error.getErrors().get(0).getEmail() != null)
        //                            MedicalApp.getInstance().makeToast(error.getErrors().get(0).getEmail(), _act);
        //                        if(error.getErrors().get(0).getName() != null)
        //                            MedicalApp.getInstance().makeToast(error.getErrors().get(0).getName(), _act);
        //                        if(error.getErrors().get(0).getPhone() != null)
        //                            MedicalApp.getInstance().makeToast(error.getErrors().get(0).getPhone(), _act);
        //                    }
        //                }
        //            }
//
        //            @Override
        //            public void onFailure(Call<LoginSocialMain> call, Throwable t) {
        //                MedicalApp.getInstance().makeToast("Login Failed !", _act);
        //            }
        //        });
    }


    public void doInstagramLogin()
    {
        //authenticationDialog = new AuthenticationDialog(this, this);
        //authenticationDialog.setCancelable(true);
        //authenticationDialog.show();

        final Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https")
                .authority("api.instagram.com")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", "7713d32b89684648909f4cfb6f94dd68")
                .appendQueryParameter("redirect_uri", "sociallogin://redirect")
                .appendQueryParameter("response_type", "token");

        final Intent browser = new Intent(Intent.ACTION_VIEW, uriBuilder.build());

        startActivity(browser);
    }



    private void checkForInstagramData() {
        final Uri data = this.getIntent().getData();
        if(data != null && data.getScheme().equals("sociallogin") && data.getFragment() != null) {
            final String accessTokenInsta = data.getFragment().replaceFirst("access_token=", "");
            if (accessTokenInsta != null) {
                handleSignInInstaResult(true);
            } else {
                handleSignInInstaResult(false);
            }
        }
    }

    public void handleSignInInstaResult(boolean success)
    {
        Toast.makeText(this, "insta: " + success, Toast.LENGTH_SHORT).show();
    }


   // private class RequestInstagramAPI extends AsyncTask<Void, String, String> {
//
   //     @Override
   //     protected String doInBackground(Void... params) {
   //         HttpClient httpClient = new DefaultHttpClient();
   //         HttpGet httpGet = new HttpGet(getResources().getString(R.string.get_user_info_url) + token);
   //         try {
   //             HttpResponse response = httpClient.execute(httpGet);
   //             HttpEntity httpEntity = response.getEntity();
   //             return EntityUtils.toString(httpEntity);
   //         } catch (IOException e) {
   //             e.printStackTrace();
   //         }
   //         return null;
   //     }
//
   //     @Override
   //     protected void onPostExecute(String response) {
   //         super.onPostExecute(response);
   //         if (response != null) {
   //             try {
   //                 JSONObject jsonObject = new JSONObject(response);
   //                 Log.e("response", jsonObject.toString());
   //                 JSONObject jsonData = jsonObject.getJSONObject("data");
   //                 if (jsonData.has("id")) {
   //                     //сохранение данных пользователя
   //                     appPreferences.putString(AppPreferences.USER_ID, jsonData.getString("id"));
   //                     appPreferences.putString(AppPreferences.USER_NAME, jsonData.getString("username"));
   //                     appPreferences.putString(AppPreferences.PROFILE_PIC, jsonData.getString("profile_picture"));
//
   //                     //TODO: сохранить еще данные
   //                     login();
   //                 }
   //             } catch (JSONException e) {
   //                 e.printStackTrace();
   //             }
//
   //         } else {
   //             Toast toast = Toast.makeText(getApplicationContext(),"Ошибка входа!",Toast.LENGTH_LONG);
   //             toast.show();
   //         }
   //     }
   // }
}
