package com.ygaps.travelapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ygaps.travelapp.Component.LogInClient;
import com.ygaps.travelapp.Component.User;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;
import com.ygaps.travelapp.Retrofit.RequestLogin;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity
{
    CallbackManager mCallBack = CallbackManager.Factory.create();
    GoogleSignInClient mGoogleClient;
    TextView forgot;
    EditText userName, passWord;
    Button logIn;

    public static boolean isEmailValid(String email)
    {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void hideKeyboard(Activity activity)
    {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null)
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        anhXa();

        // Google
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.google_server_client_id))
                .build();
        mGoogleClient = GoogleSignIn.getClient(this, options);

        // Facebook
        LoginManager.getInstance().registerCallback(mCallBack, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                login(loginResult.getAccessToken().getToken(), true);
            }

            @Override
            public void onCancel()
            {
                Log.w("logInFaceBook", "Canceled login");
            }

            @Override
            public void onError(FacebookException error)
            {
                Log.e("logInFaceBook", "Failed to login: " + error.getMessage());
            }
        });
    }

    public void anhXa()
    {
        logIn = findViewById(R.id.btnLogIn);
        userName = findViewById(R.id.UserName);
        passWord = findViewById(R.id.passwork);
    }

    //click login
    public void OnClickLogIn(View v)
    {
        closeKeyBroad();
        v = findViewById(R.id.btnLogIn);
        //check input data
        if (userName.getText().toString().isEmpty() || passWord.getText().toString().isEmpty())
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.message_invalid_account))
                    .show();
        } else if (!isEmailValid(userName.getText().toString().trim()))
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.invalid_email))
                    .show();
        } else
        {
            // normal login
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://35.197.153.192:3000")
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            MyAPIClient user = retrofit.create(MyAPIClient.class);

            LogInClient login = new LogInClient(userName.getText().toString(), passWord.getText().toString());
            Call<User> call = user.login(login);

            call.enqueue(new Callback<User>()
            {
                @Override
                public void onResponse(Call<User> call, Response<User> response)
                {
                    if (response.isSuccessful())
                    {
                        saveAcount(userName.getText().toString().trim(), passWord.getText().toString().trim(), response.body().getToken().trim());
                        Intent dialogIntent = new Intent(Login.this, ListTour.class);
                        finish();
                        startActivity(dialogIntent);
                    } else
                    {
                        new AlertDialog.Builder(Login.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.message_invalid_account))
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t)
                {
                    Toast.makeText(Login.this, "Error :(", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // click forgot passwork
    public void OnClickForGot(View v)
    {
        closeKeyBroad();
        Intent dialogIntent = new Intent(Login.this, ForgotPass.class);
        startActivity(dialogIntent);
    }

    // click sign up
    public void OnClickSignUp(View v)
    {
        closeKeyBroad();
        Intent dialogIntent = new Intent(Login.this, SignUp.class);
        startActivity(dialogIntent);
    }

    //login with facebook
    public void logInFaceBook(View v)
    {
        v.findViewById(R.id.btn_logInFaceBook);
        //String token="EAAF6ZASTgyRgBACv1TBtWJXQKqZBwubYu9GuQ5lzDE0lvVnZB8T7pkwX5mgCTb7HG0dMUL2u5KreJuZCW5RVHhtGXVgsApKmXREBVyTeh2tS7pubETVmNQ8PBIrZB0uYt752EqM5jsPMFTheXvHDGP1YZBfCVwvExt8GKPlqn9u8mLZAlyBZBZCUWZCjkfVVx6uoRQuo12N4GzsQZDZD";
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null || accessToken.isExpired())
        {
            LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("public_profile"));
        } else login(accessToken.getToken(), true);
    }

    public void login(String token, boolean isLoginByFacebook)
    {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        MyAPIClient user = retrofit.create(MyAPIClient.class);

        //call api
        Call<User> call;
        if (isLoginByFacebook) call = user.logInByFacebook(new RequestLogin(token));
        else call = user.logInByGoogle(new RequestLogin(token));
        call.enqueue(new Callback<User>()
        {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<User> call, Response<User> response)
            {
                if (response.isSuccessful())
                {
                    saveAcount(response.body().getFull_name(), response.body().getToken(), response.body().getToken().trim());
                    Intent dialogIntent = new Intent(Login.this, ListTour.class);
                    finish();
                    startActivity(dialogIntent);
//                    Toast.makeText(Login.this, "get true", Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(Login.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<User> call, Throwable t)
            {
                Toast.makeText(Login.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //login with google
    public void logInGoogle(View v)
    {
        v.findViewById(R.id.btn_logInGoogle);
        // String token= "eyJhbGciOiJSUzI1NiIsImtpZCI6ImEwNjgyNGI3OWUzOTgyMzk0ZDVjZTdhYzc1YmY5MmNiYTMwYTJlMjUiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxNTQ3MTAyNTcwNzktZmJyZWg0c3M4dG81dTU0aWI4ZzhxaHJmaTBnMTV0ZHAuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxNTQ3MTAyNTcwNzktNGJ2cXQ4aWtlOHFjbzQxbHRyY2E1a2hjN2kyb3Nsb2kuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTcyNjQwNjE0NDg2NTg5MDYzMjAiLCJlbWFpbCI6InByb3BsYXlkb3RhMTIzNDVAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJBejNyIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BQXVFN21EbVBFbHlBcVlxYU1xaU1hRm1VaVhkYnNQZW1oaGh6SEVkcGpxbThBPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6IkF6M3IiLCJsb2NhbGUiOiJ2aSIsImlhdCI6MTU3MzM5MjczOSwiZXhwIjoxNTczMzk2MzM5fQ.BbHMRJwmSZyaYN1FliJmhSg1rQYKkTPNoc8Vx3AP_e_-OA_YtWJWmeHBTuWNY3ZFpWGl6FN_ZvmoiKlK62H8DBgXYDq4bP7MjHLt66nxyA4N8t3JHPN1u1-uLG8A317z2Xk-2edVUm0IwxC3t4cYYXJzXjEhtQbrbwb-BoV9mZXJn4LUroajnuousnlaPtMJepl2cQ-wkuPsQePS11pasoEOw2lx97uOLjDGz58bLDGJWqRpupR0SL20wwj2Hh3EAei8OSVduKmjjuumZ5A4Ji2DA7vLcgTFoQ17T04qZDAzN5aw5aOrgG_NETllPii1CWKWDeW2MH6One5W9DWQNw";
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null)
        {
            mGoogleClient.silentSignIn().addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>()
            {
                @Override
                public void onSuccess(GoogleSignInAccount googleSignInAccount)
                {
                    login(googleSignInAccount.getIdToken(), false);
                }
            });
        } else
        {
            login(account.getIdToken(), false);
        }
    }

    public void saveAcount(String userName, String password, String token)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.putString("password", password);
        editor.putString("token", token);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mCallBack.onActivityResult(requestCode, resultCode, data);
    }

    public void closeKeyBroad()
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit."))
        {
            int[] scrcoords = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }
}
