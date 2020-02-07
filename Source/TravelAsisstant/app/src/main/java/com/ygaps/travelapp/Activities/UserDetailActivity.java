package com.ygaps.travelapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.Component.User;
import com.ygaps.travelapp.Component.UserRequest;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class UserDetailActivity extends AppCompatActivity
{
    EditText txt_FullName, txt_Phone, txt_Email, txt_Gender;
    TextView txt_DOB;

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
        setContentView(R.layout.activity_user_detail);
        anhXa();
        getData();
    }

    public void anhXa()
    {
        txt_DOB = findViewById(R.id.EditDOB);
        txt_Email = findViewById(R.id.editEmail);
        txt_FullName = findViewById(R.id.editFullName);
        txt_Phone = findViewById(R.id.editPhone);
        txt_Gender = findViewById(R.id.editGender);
    }

    public void getData()
    {
        //create library retrofit2
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        MyAPIClient user = retrofit.create(MyAPIClient.class);

        //get token in local
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        //call api
        Call<User> call = user.getUserInfo(token);
        call.enqueue(new Callback<User>()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<User> call, Response<User> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        txt_FullName.setText(response.body().getFullName());
                        txt_Email.setText(response.body().getEmail());
                        txt_Phone.setText(response.body().getPhone());
                        if (response.body().getGender() == 1)
                        {
                            txt_Gender.setText("Nam");
                        } else
                        {
                            txt_Gender.setText("Nữ");
                        }
                        if (response.body().getDob() != null)
                        {
                            Toast.makeText(UserDetailActivity.this, response.body().getDob(), Toast.LENGTH_SHORT).show();

                            String ds2 = "";
                            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                            try
                            {
                                ds2 = sdf2.format(sdf1.parse(response.body().getDob()));
                            }
                            catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                            txt_DOB.setText(ds2);
                        }

                    } else
                    {
                        Toast.makeText(UserDetailActivity.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    Toast.makeText(UserDetailActivity.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<User> call, Throwable t)
            {
                Toast.makeText(UserDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickChooseDOB(View view)
    {
        view = findViewById(R.id.EditDOB);
        final Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                txt_DOB.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, year, month, date);
        datePickerDialog.show();
    }

    public void onClickUpdateUserInfo(View view)
    {
        view = findViewById(R.id.btn_UpDateUserInfo);

        if (txt_FullName.getText().toString().isEmpty() ||
                txt_Phone.getText().toString().isEmpty() ||
                txt_Email.getText().toString().isEmpty() ||
                txt_DOB.getText().toString().isEmpty() ||
                txt_Gender.getText().toString().isEmpty())
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.message_fill_information))
                    .show();
        } else if (!isEmailValid(txt_Email.getText().toString()))
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.invalid_email))
                    .show();
        } else
        {
            int gender;
            if (txt_Gender.getText().toString().equals("Nam"))
                gender = 1;
            else
                gender = 0;

            String ds2 = "";
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            try
            {
                ds2 = sdf2.format(sdf1.parse(txt_DOB.getText().toString()));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            UserRequest user = new UserRequest(txt_FullName.getText().toString(),
                    txt_Email.getText().toString(),
                    txt_Phone.getText().toString(),
                    ds2,
                    gender);
            updateUserInfo(user);
        }
    }

    public void updateUserInfo(UserRequest user)
    {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        MyAPIClient myAPIClient = retrofit.create(MyAPIClient.class);

        //get token in local
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        //call api
        Call<User> call = myAPIClient.updateUser(token, user);
        call.enqueue(new Callback<User>()
        {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<User> call, Response<User> response)
            {
                if (response.isSuccessful())
                {
                    new AlertDialog.Builder(UserDetailActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle(getString(R.string.successful))
                            .setMessage(getString(R.string.message_update_account_success))
                            .show();
                } else
                {
                    new AlertDialog.Builder(UserDetailActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getString(R.string.error))
                            .setMessage(getString(R.string.message_update_account_failed))
                            .show();
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<User> call, Throwable t)
            {
                new AlertDialog.Builder(UserDetailActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.error))
                        .setMessage(getString(R.string.message_update_account_failed))
                        .show();
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
