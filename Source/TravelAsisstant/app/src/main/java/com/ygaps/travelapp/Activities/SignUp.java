package com.ygaps.travelapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.ygaps.travelapp.Component.Resister;
import com.ygaps.travelapp.Component.User;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUp extends AppCompatActivity
{

    EditText fullName, email, phone, passWork, conFirm;
    Button signUp;

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
        setContentView(R.layout.activity_sign_up);
        anhXa();
    }

    public void anhXa()
    {
        fullName = findViewById(R.id.signUpFullName);
        email = findViewById(R.id.signUpEmail);
        phone = findViewById(R.id.signUpPhone);
        passWork = findViewById(R.id.signUpPasswork);
        conFirm = findViewById(R.id.signUpConfirm);
        signUp = findViewById(R.id.btn_signUp);
    }

    //click button back
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

    public void onClickSignUp(View v)
    {
        v = findViewById(R.id.btn_signUp);
        //check input data
        if (fullName.getText().toString().isEmpty() || email.getText().toString().isEmpty() || phone.getText().toString().isEmpty()
                || passWork.getText().toString().isEmpty() || conFirm.getText().toString().isEmpty())
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.error)
                    .setMessage(R.string.message_fill_information)
                    .show();
        } else if (!isEmailValid(email.getText().toString().trim()))
        {
            new android.app.AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.error)
                    .setMessage(R.string.invalid_email)
                    .show();
        } else if (!passWork.getText().toString().trim().equals(conFirm.getText().toString().trim()))
        {
            new android.app.AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.message_confirm_password_not_matched))
                    .show();
        } else
        {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://35.197.153.192:3000")
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            MyAPIClient user = retrofit.create(MyAPIClient.class);

            Resister resister = new Resister();
            resister.fullName = fullName.getText().toString();
            resister.email = email.getText().toString();
            resister.password = passWork.getText().toString();
            resister.phone = phone.getText().toString();

            Call<User> call = user.createUser(resister);

            call.enqueue(new Callback<User>()
            {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<User> call, Response<User> response)
                {
                    if (response.isSuccessful())
                    {
                        finish();
                    } else
                    {
                        new android.app.AlertDialog.Builder(SignUp.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.message_register_failed))
                                .show();
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<User> call, Throwable t)
                {
                    Toast.makeText(SignUp.this, "Error :(", Toast.LENGTH_SHORT).show();
                }
            });
        }
        // conect to sever to create acount

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
