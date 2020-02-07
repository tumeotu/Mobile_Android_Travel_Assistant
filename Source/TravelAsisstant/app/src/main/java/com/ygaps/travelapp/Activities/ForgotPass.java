package com.ygaps.travelapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.ygaps.travelapp.Component.Email;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;
import com.ygaps.travelapp.Retrofit.ResponRecoverPassword;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class ForgotPass extends AppCompatActivity
{
    Button sms, email, submit;
    EditText input;
    boolean mIsEmailSelected = true;

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
        setContentView(R.layout.activity_forgot_pass);
        anhXa();
        onClickEmailPhone(null);
    }

    public void anhXa()
    {
        sms = findViewById(R.id.btn_forgot_sms);
        email = findViewById(R.id.btn_forgot_email);
        submit = findViewById(R.id.btn_forgot_submit);
        input = findViewById(R.id.ed_forgot_input);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickSms(View v)
    {
        /*
        email.setBackgroundResource(R.drawable.boderbuttonforgot2);
        email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.logolettersub, 0, 0, 0);
        sms.setBackgroundResource(R.drawable.boderbuttonforgot);
        sms.setCompoundDrawablesWithIntrinsicBounds(R.drawable.logosms, 0, 0, 0);
        input.setHint(R.string.input_forgot_sms);
        //user choose reset pass by phone
        mIsEmailSelected = false;
        */

        // hiện chưa dùng được khôi phục mật khẩu bằng number phone
        new android.app.AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.error))
                .setMessage(getString(R.string.message_unavailable_method))
                .show();
    }

    public void onClickEmailPhone(View v)
    {
        sms.setBackgroundResource(R.drawable.boderbuttonforgot2);
        sms.setCompoundDrawablesWithIntrinsicBounds(R.drawable.logosmssub, 0, 0, 0);
        email.setBackgroundResource(R.drawable.boderbuttonforgot);
        email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.logoletter, 0, 0, 0);
        input.setHint(R.string.message_enter_email);
        // user choose reset pass by email
        mIsEmailSelected = true;
    }

    // handle reset passwork for user when user click submit
    public void onClickSubmit(View v)
    {
        v.findViewById(R.id.btn_forgot_submit);
        //check input
        if (input.getText().toString().isEmpty())
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.message_enter_email))
                    .show();
            return;
        }

        if (mIsEmailSelected)
        {
            if (!isEmailValid(input.getText().toString().trim()))
            {
                new android.app.AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.error))
                        .setMessage(getString(R.string.invalid_email))
                        .show();
            } else
            {
                Email email = new Email("email", input.getText().toString());
                sendOTP(email);
            }
        }

        /*
        //check input number phone
        else if(input.getText().toString().isEmpty()&&chooseSms) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Data is empty")
                    .setMessage("You must enter your number phone")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what would happen when positive button is clicked
                        }
                    })
                    .show();
        }
         */
    }

    public void sendOTP(Email email)
    {
        //create library retrofit2
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        MyAPIClient user = retrofit.create(MyAPIClient.class);

        //call api
        Call<ResponRecoverPassword> call = user.recoverPassword(email);
        call.enqueue(new Callback<ResponRecoverPassword>()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ResponRecoverPassword> call, Response<ResponRecoverPassword> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        Intent intent = new Intent(ForgotPass.this, VerifyOTP.class);
                        intent.putExtra("MyClass", response.body());
                        startActivity(intent);
                    } else
                    {
                        Toast.makeText(ForgotPass.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    new android.app.AlertDialog.Builder(ForgotPass.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getString(R.string.error))
                            .setMessage(getString(R.string.message_request_email_invalid))
                            .show();
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ResponRecoverPassword> call, Throwable t)
            {
                Toast.makeText(ForgotPass.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
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
