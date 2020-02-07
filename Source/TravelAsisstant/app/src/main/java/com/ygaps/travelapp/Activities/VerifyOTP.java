package com.ygaps.travelapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.ygaps.travelapp.Component.NewPassword;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;
import com.ygaps.travelapp.Retrofit.ResponRecoverPassword;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class VerifyOTP extends AppCompatActivity
{

    EditText txt_OTP, txt_NewPassword, txt_ConFirmPassword;
    ResponRecoverPassword responRecoverPassword;

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
        setContentView(R.layout.activity_verify_otp);
        anhXa();
        getData();
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

    public void anhXa()
    {
        txt_OTP = findViewById(R.id.input_OTP);
        txt_NewPassword = findViewById(R.id.input_NewPassword);
        txt_ConFirmPassword = findViewById(R.id.input_ConfirmNewPassword);
    }

    public ResponRecoverPassword getData()
    {
        Intent intent = getIntent();
        responRecoverPassword = (ResponRecoverPassword) intent.getSerializableExtra("MyClass");
        return responRecoverPassword;
    }

    public void onClickSubmitNewPassword(View view)
    {
        view = findViewById(R.id.btn_SubmitOTP);

        if (checkInputData())
        {
            //create library retrofit2
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://35.197.153.192:3000")
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            MyAPIClient user = retrofit.create(MyAPIClient.class);

            NewPassword newPassword = new NewPassword(responRecoverPassword.getUserId(),
                    txt_NewPassword.getText().toString().trim(),
                    txt_ConFirmPassword.getText().toString().trim());

            //call api
            Call<ResponseBody> call = user.verifyNewPassword(newPassword);
            call.enqueue(new Callback<ResponseBody>()
            {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    if (response.isSuccessful())
                    {
                        if (response.body() != null)
                        {
                            AlertDialog alertDialog = new AlertDialog.Builder(VerifyOTP.this)
                                    .setTitle("Successful")
                                    .setMessage("Your password has been changed!!!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            Intent intent = new Intent(VerifyOTP.this, Login.class);
                                            //intent.putExtra("MyClass", response.body());
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .show();
                        } else
                        {
                            Toast.makeText(VerifyOTP.this, "Empty body", Toast.LENGTH_LONG).show();
                        }
                    } else
                    {
                        Toast.makeText(VerifyOTP.this, response.message(), Toast.LENGTH_SHORT).show();

                        // Toast.makeText(ForgotPass.this, "Failed to recover password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Toast.makeText(VerifyOTP.this, "error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //Toast.makeText(this, responRecoverPassword.getUserId().toString(), Toast.LENGTH_SHORT).show();
    }

    public boolean checkInputData()
    {
        if (txt_ConFirmPassword.getText().toString().isEmpty() ||
                txt_OTP.getText().toString().isEmpty() || txt_ConFirmPassword.getText().toString().isEmpty())
        {
            AlertDialog alertDialog = new AlertDialog.Builder(VerifyOTP.this)
                    .setTitle("Fail")
                    .setMessage("You must enter full information!!!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            //set what would happen when positive button is clicked
                        }
                    })
                    .show();
            return false;
        } else if (!txt_ConFirmPassword.getText().toString().trim().equals(txt_NewPassword.getText().toString().trim()))
        {
            AlertDialog alertDialog = new AlertDialog.Builder(VerifyOTP.this)
                    .setTitle("Fail")
                    .setMessage("New password incorrect!!!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            //set what would happen when positive button is clicked
                        }
                    })
                    .show();
            return false;
        }
        return true;
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
