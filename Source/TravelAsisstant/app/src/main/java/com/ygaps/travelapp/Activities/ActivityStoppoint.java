package com.ygaps.travelapp.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.maps.model.LatLng;
import com.ygaps.travelapp.Adapters.AdapterStopPoint;
import com.ygaps.travelapp.Component.StopPoint;
import com.ygaps.travelapp.Component.User;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.JsonCoordSet;
import com.ygaps.travelapp.Retrofit.MyAPIClient;
import com.ygaps.travelapp.Retrofit.RequestCoordList;
import com.ygaps.travelapp.Retrofit.ResponseStopPoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityStoppoint extends AppCompatActivity
{

    private AdapterStopPoint adapterStopPoint;
    private ListView listViewStoppoint;
    private List<StopPoint> mStopPoints;
    private Button mBtnListTour, mBtnYourListTour, mBtnLocation, mBtnNotify, mBtnSetting, Cancel, Rating;
    private TextView Name, TypeService, tvAddress, TimeArriverAt, DateAriveAt, TimeLeaveAt, DateLeaveAt, MinCost, MaxCost;
    private Dialog mDialogStopPoint;
    private TextView mTotalTextView;
    private MyAPIClient mRetrofit;
    private User account;
    private SearchView mSearchView;
    private String mUserToken;

    public static void hideKeyboard(Activity activity)
    {
        if (activity != null && activity.getWindow() != null)
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // get user token
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_login), MODE_PRIVATE);
        mUserToken = sharedPreferences.getString("token", "");

        // build retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        mRetrofit = retrofit.create(MyAPIClient.class);
        setContentView(R.layout.activity_stoppoint);
        init();
        getData();
        getDataUser();
    }

    void init()
    {
        listViewStoppoint = findViewById(R.id.lv_liststoppoint);
        mTotalTextView = findViewById(R.id.tv_total_stoppoint);
        mStopPoints = new ArrayList<>();
        mBtnListTour = findViewById(R.id.btn_list_tour);
        mBtnYourListTour = findViewById(R.id.btn_you_list_tour);
        mBtnLocation = findViewById(R.id.btn_location_tour);
        mBtnNotify = findViewById(R.id.btn_notification);
        mBtnSetting = findViewById(R.id.btn_settting);
        mDialogStopPoint = new Dialog(this);
        mDialogStopPoint.setContentView(R.layout.dialog_stopppoint);

        Name = mDialogStopPoint.findViewById(R.id.txt_StopPointName);
        TypeService = mDialogStopPoint.findViewById(R.id.txt_service_type);
        tvAddress = mDialogStopPoint.findViewById(R.id.txt_AdrresStop);
        TimeArriverAt = mDialogStopPoint.findViewById(R.id.txt_TimeArrive);
        DateAriveAt = mDialogStopPoint.findViewById(R.id.txt_DateArrive);
        TimeLeaveAt = mDialogStopPoint.findViewById(R.id.txt_TimeLeave);
        DateLeaveAt = mDialogStopPoint.findViewById(R.id.txt_DateLeave);
        MinCost = mDialogStopPoint.findViewById(R.id.txt_MinCostStop);
        MaxCost = mDialogStopPoint.findViewById(R.id.txt_MaxCostStop);
        Cancel = mDialogStopPoint.findViewById(R.id.btnCancelStoppoint);
        Rating = mDialogStopPoint.findViewById(R.id.btnRatingStopPoint);
    }

    private void getData()
    {
        LatLng latLng = new LatLng(10, 106);
        LatLng first = new LatLng(latLng.latitude - 1, latLng.longitude - 1);
        LatLng second = new LatLng(latLng.latitude + 1, latLng.longitude + 1);
        JsonCoordSet coordSet = new JsonCoordSet(first, second);
        RequestCoordList req = new RequestCoordList(Collections.singletonList(coordSet));
        Call<ResponseStopPoints> call = mRetrofit.getSuggestDestinations(mUserToken, req);
        call.enqueue(new Callback<ResponseStopPoints>()
        {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ResponseStopPoints> call, Response<ResponseStopPoints> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        mStopPoints = response.body().StopPoints;
                        Log.i("getSuggestDestinationsTask", "Receive " + mStopPoints.size() + " suggested destinations");
                        adapterStopPoint = new AdapterStopPoint(ActivityStoppoint.this, R.layout.row_stoppoint, mStopPoints);
                        listViewStoppoint.setAdapter(adapterStopPoint);
                        mTotalTextView.setText(String.valueOf(mStopPoints.size()));
                    } else
                    {
                        onRetrofitNullResponseBody("getSuggestDestinationsTask");
                    }

                } else
                {
                    onRetrofitNotSuccessful(response, "getSuggestDestinationsTask");
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ResponseStopPoints> call, Throwable t)
            {
                onRetrofitFailure(t, "getSuggestDestinationsTask");
            }
        });
    }

    public void getDataUser()
    {
        //get token in local
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        //call api
        Call<User> call = mRetrofit.getUserInfo(token);
        call.enqueue(new Callback<User>()
        {
            @Override
            @retrofit2.internal.EverythingIsNonNull
            public void onResponse(Call<User> call, Response<User> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        account = new User(response.body());
                    } else
                    {
                        Toast.makeText(ActivityStoppoint.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    Toast.makeText(ActivityStoppoint.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            @retrofit2.internal.EverythingIsNonNull
            public void onFailure(Call<User> call, Throwable t)
            {
                Toast.makeText(ActivityStoppoint.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void DialogStopPointInfo(final StopPoint stopPoint)
    {
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        mDialogStopPoint.getWindow().setLayout((int) (displayRectangle.width() * 0.95f), (int) (displayRectangle.height() * 0.8f));
        mDialogStopPoint.setCanceledOnTouchOutside(false);
        Name.setText(stopPoint.Name);
        if (stopPoint.ServiceTypeId == 1)
        {
            TypeService.setText(R.string.restaurant);
        } else if (stopPoint.ServiceTypeId == 2)
        {
            TypeService.setText(R.string.hotel);
        } else if (stopPoint.ServiceTypeId == 3)
        {
            TypeService.setText(R.string.rest_station);
        } else if (stopPoint.ServiceTypeId == 4)
        {
            TypeService.setText(R.string.other);
        }
        TimeArriverAt.setText(convertTime(stopPoint.ArrivalAt));
        DateAriveAt.setText(convertDate(stopPoint.ArrivalAt));
        TimeLeaveAt.setText(convertTime(stopPoint.LeaveAt));
        DateLeaveAt.setText(convertDate(stopPoint.LeaveAt));
        tvAddress.setText(stopPoint.Address);
        MinCost.setText(stopPoint.MinCost.toString());
        MaxCost.setText(stopPoint.MaxCost.toString());
        mDialogStopPoint.show();
        Cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mDialogStopPoint.dismiss();
            }
        });
        Rating.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent mainIntent = new Intent(ActivityStoppoint.this, RatingTourStopPoint.class);
                Bundle dataBundle = new Bundle();
                dataBundle.putString("Type", String.valueOf(1));
                dataBundle.putString("Id", stopPoint.Id.toString());
                dataBundle.putString("Name", stopPoint.Name);
                dataBundle.putString("Point", "0");
                mainIntent.putExtras(dataBundle);
                startActivity(mainIntent);
                mDialogStopPoint.dismiss();
            }
        });
    }

    public String convertDate(long time)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(time);
        return String.format(Locale.getDefault(), "%02d/%02d/%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    public String convertTime(long time)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(time);
        return String.format(Locale.getDefault(), "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Service.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                adapterStopPoint.filter(s.trim());
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public <T> void onRetrofitNullResponseBody(String tag)
    {
        Toast.makeText(this, "Response body is null", Toast.LENGTH_LONG).show();
        Log.w(tag, "Response body is null");
    }

    public <T> void onRetrofitNotSuccessful(Response<T> response, String tag)
    {
        Toast.makeText(this, response.message(), Toast.LENGTH_LONG).show();
        try
        {
            assert response.errorBody() != null;
            Log.e(tag, String.format("%s: %s", response.message(), response.errorBody().string()));
        }
        catch (IOException ignored)
        {
        }
    }

    public void onRetrofitFailure(Throwable t, String tag)
    {
        Log.e(tag, t.getMessage());
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void onClickListTour(View view) {
        Intent intent = new Intent(this, ListTour.class);
        intent.putExtra("NameActivity", "");
        startActivity(intent);
    }

    public void onClickUserListTour(View view) {
        Intent intent = new Intent(this, ListTour.class);
        intent.putExtra("NameActivity", "UserListTour");
        startActivity(intent);
    }

    public void OnClickNotifycation(View view) {
        Intent intent= new Intent(this,ActivityNotify.class);
        startActivity(intent);
    }

    public void onClickSetting(View view) {
        Intent intent= new Intent(this,UserActivity.class);
        startActivity(intent);
    }
}
