package com.ygaps.travelapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.Adapters.AdapterNotify;
import com.ygaps.travelapp.Component.NotifyTour;
import com.ygaps.travelapp.Component.ResponseNotify;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;
import com.ygaps.travelapp.Retrofit.RequestAcceptInvitation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityNotify extends AppCompatActivity
{

    private TextView TotalNotify;
    private Button SortNotify;
    private ListView ListNotity;
    private MyAPIClient mAPI;
    private String mToken;
    private List<NotifyTour> notifyTourList;
    private ResponseNotify responseNotify;
    private AdapterNotify adapterNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        mToken = sharedPreferences.getString("token", "");
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        mAPI = retrofit.create(MyAPIClient.class);
        setContentView(R.layout.activity_notify);
        init();
        getData();
    }

    public void init()
    {
        TotalNotify = findViewById(R.id.ToTalNotify);
        SortNotify = findViewById(R.id.SortListNotify);
        ListNotity = findViewById(R.id.ListNotify);
        notifyTourList = new ArrayList<>();
    }
    public void getData()
    {
        //call api
        Call<ResponseNotify> call = mAPI.getListNotify(mToken, 1, "100");
        call.enqueue(new Callback<ResponseNotify>()
        {
            @Override
            @retrofit2.internal.EverythingIsNonNull
            public void onResponse(Call<ResponseNotify> call, Response<ResponseNotify> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        notifyTourList = response.body().tours;
                        Collections.sort(notifyTourList, new Comparator<NotifyTour>()
                        {
                            @Override
                            public int compare(NotifyTour o1, NotifyTour o2)
                            {
                                if (o1.createdOn < o2.createdOn) return 1;
                                else if (o1.createdOn > o2.createdOn) return -1;
                                return 0;
                            }
                        });

                        adapterNotify = new AdapterNotify(ActivityNotify.this, R.layout.row_notify, notifyTourList);
                        ListNotity.setAdapter(adapterNotify);
                        TotalNotify.setText(String.valueOf(response.body().tours.size()));
                        responseNotify = response.body();

                    } else
                    {
                        Toast.makeText(ActivityNotify.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    Log.e("abc", response.message());
                    Toast.makeText(ActivityNotify.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            @retrofit2.internal.EverythingIsNonNull
            public void onFailure(Call<ResponseNotify> call, Throwable t)
            {
                Toast.makeText(ActivityNotify.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Accept(int tourId)
    {
        sendInvitationResponse(tourId, true);
    }

    public void Deny(int tourId)
    {
        sendInvitationResponse(tourId, false);
    }

    public void sendInvitationResponse(final Integer tourId, Boolean isAccepted)
    {
        RequestAcceptInvitation req = new RequestAcceptInvitation();
        req.TourId = tourId;
        req.IsAccepted = isAccepted;
        Call<ResponseBody> call = mAPI.responseInvitation(mToken, req);
        call.enqueue(new Callback<ResponseBody>()
        {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                if (response.isSuccessful())
                {
                    Log.d("sendInvitationResponse", "Successfully accept invitation: %s");
                    new AlertDialog.Builder(ActivityNotify.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle(getString(R.string.successful))
                            .setMessage(getString(R.string.message_update_successfully))
                            .show();
                    for (int i = 0; i < notifyTourList.size(); ++i)
                    {
                        if (notifyTourList.get(i).id.equals(tourId))
                        {
                            notifyTourList.remove(i);
                            adapterNotify.notifyDataSetChanged();
                            TotalNotify.setText(String.valueOf(notifyTourList.size()));
                            break;
                        }
                    }
                } else
                {
                    try
                    {
                        Log.e("sendInvitationResponse", String.format("Failed to accept invitation: %s", response.errorBody().string()));
                    }
                    catch (IOException ignored)
                    {
                    }
                    new AlertDialog.Builder(ActivityNotify.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getString(R.string.error))
                            .setMessage(getString(R.string.message_error_happened))
                            .show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Log.e("sendInvitationResponse", String.format("Failed to accept invitation: %s", t.getMessage()));
                new AlertDialog.Builder(ActivityNotify.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.error))
                        .setMessage(getString(R.string.message_error_happened))
                        .show();
            }
        });
    }

    public void onClickListTour(View view)
    {
        Intent intent = new Intent(this, ListTour.class);
        intent.putExtra("NameActivity", "");
        startActivity(intent);
    }

    public void onClickUserListTour(View view)
    {
        Intent intent = new Intent(this, ListTour.class);
        intent.putExtra("NameActivity", "UserListTour");
        startActivity(intent);
    }

    public void onLocationTourClicked(View view)
    {
        Intent intent = new Intent(this, ActivityStoppoint.class);
        startActivity(intent);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    public void onClickSetting(View view)
    {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }
}
