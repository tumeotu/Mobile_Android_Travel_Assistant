package com.ygaps.travelapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.Component.User;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class UserActivity extends AppCompatActivity
{

    Button btn_Language;
    TextView txt_NameOfUser;
    TextView txt_EditUser;

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
        setContentView(R.layout.activity_user);
        anhXa();
        getData();
    }

    public void anhXa()
    {
        btn_Language = findViewById(R.id.btn_Language);
        txt_NameOfUser = findViewById(R.id.txt_nameOfUser);
        txt_EditUser = findViewById(R.id.txt_editNameOfUser);
    }

    private void showMenuLanguage()
    {
        final PopupMenu popupMenu = new PopupMenu(this, btn_Language);
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(final MenuItem item)
            {
                final boolean isEnglishCurrentLocale = getSharedPreferences(getString(R.string.shared_preferences_login), MODE_PRIVATE).getString("language", "vi").equals("en");
                final boolean isEnglishSelected = item.getItemId() == R.id.btn_English;
                if ((isEnglishSelected && isEnglishCurrentLocale) || (!isEnglishCurrentLocale && !isEnglishSelected))
                    return true;
                String language = isEnglishSelected ? getString(R.string.language_english) : getString(R.string.language_vietnam);
                String title = getResources().getString(R.string.confirmation);
                String message = getResources().getString(R.string.message_language_change) + " " + language;
                String yesStr = getResources().getString(R.string.yes);
                String noStr = getResources().getString(R.string.no);
                new AlertDialog.Builder(UserActivity.this)
                        .setTitle(title)
                        .setMessage(message)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(yesStr, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_login), MODE_PRIVATE);
                                sharedPreferences.edit().putString("language", isEnglishSelected ? "en" : "vi").apply();
                                startActivity(new Intent(UserActivity.this, SplashActivity.class));
                            }
                        })
                        .setNegativeButton(noStr, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                popupMenu.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    public void onClickLanguage(View view)
    {
        view = findViewById(R.id.btn_Language);
        showMenuLanguage();
    }

    public void onClickLogOut(View view)
    {
        view = findViewById(R.id.btn_LogOut);

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", "");
        editor.putString("password", "");
        editor.putString("token", "");
        editor.commit();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void onlickListTour(View view)
    {
        view = findViewById(R.id.btn_list_tour);
        Intent intent = new Intent(this, ListTour.class);
        intent.putExtra("NameActivity", "");
        startActivity(intent);
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
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        //call api
        Call<User> call = user.getUserInfo(token);
        call.enqueue(new Callback<User>()
        {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<User> call, Response<User> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        txt_NameOfUser.setText(response.body().getFullName());
                    } else
                    {
                        Toast.makeText(UserActivity.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    Toast.makeText(UserActivity.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<User> call, Throwable t)
            {
                Toast.makeText(UserActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onClickUserDetail(View view)
    {
        view = findViewById(R.id.txt_editNameOfUser);

        Intent intent = new Intent(this, UserDetailActivity.class);
        startActivity(intent);
    }

    public void onClickListTourUser(View view)
    {
        view = findViewById(R.id.txt_editNameOfUser);
        Intent intent = new Intent(this, ListTour.class);
        intent.putExtra("NameActivity", "UserListTour");
        startActivity(intent);
    }
    public void OnClickNotifycation(View view)
    {
        view = findViewById(R.id.btn_notification);
        Intent intent = new Intent(this, ActivityNotify.class);
        startActivity(intent);
    }

    public void onLocationTourClicked(View view)
    {
        Intent intent = new Intent(this, ActivityStoppoint.class);
        startActivity(intent);
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
