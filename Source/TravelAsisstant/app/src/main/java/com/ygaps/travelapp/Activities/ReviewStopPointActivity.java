package com.ygaps.travelapp.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ygaps.travelapp.Component.StopPoint;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.JsonCoordSet;
import com.ygaps.travelapp.Retrofit.MyAPIClient;
import com.ygaps.travelapp.Retrofit.RequestCoordList;
import com.ygaps.travelapp.Retrofit.ResponseStopPoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewStopPointActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{

    private GoogleMap mMap;
    private String mUserToken = "";
    private MyAPIClient mRetrofit;
    private List<StopPoint> mStopPoints = new ArrayList<>();
    private List<Marker> mMarkers = new ArrayList<>();

    private Dialog mDialog;
    private RatingBar rbPoint;
    private TextView tvUserName;
    private EditText etReview;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_google_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get user token
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_login), MODE_PRIVATE);
        mUserToken = sharedPreferences.getString("token", "");

        // build retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        mRetrofit = retrofit.create(MyAPIClient.class);

        initDialog();

    }

    public void initDialog()
    {
        mDialog= new Dialog(this );
        mDialog.setContentView(R.layout.activity_review_stop_point);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        FusedLocationProviderClient locationProvider = LocationServices.getFusedLocationProviderClient(this);
        locationProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>()
        {
            @Override
            public void onSuccess(Location location)
            {
                if (location != null)
                {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 22);
                    mMap.moveCamera(update);
                    getSuggestDestinationsTask(latLng);
                }
            }
        });
    }
    public void getSuggestDestinationsTask(LatLng latLng)
    {
        if (latLng == null) return;
        latLng = new LatLng(10, 106);
        LatLng first = new LatLng(latLng.latitude - 0.1, latLng.longitude - 0.1);
        LatLng second = new LatLng(latLng.latitude + 0.1, latLng.longitude + 0.1);
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
                        fetchSuggestStopPoints();
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

    public void fetchSuggestStopPoints()
    {
        if (mStopPoints == null || mStopPoints.isEmpty()) return;

        // remove markers from google map
        for (Marker item : mMarkers) item.remove();
        mMarkers.clear();

        for (StopPoint item : mStopPoints)
        {
            // setup marker properties
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(item.Lat, item.Long));
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            options.title(item.Address);
            options.snippet(item.Name);

            // set the id of marker to the id of its stop Point
            // this id is used to update or remove marker in future
            Marker marker = mMap.addMarker(options);
            marker.setTag(item);
            mMarkers.add(marker);
        }
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

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        StopPoint stopPoint = (StopPoint) marker.getTag();

        Intent mainIntent = new Intent(this, RatingTourStopPoint.class);
        Bundle dataBundle = new Bundle();
        dataBundle.putString("Type", String.valueOf(1));
        dataBundle.putString("Id", String.valueOf(stopPoint.Id));
        dataBundle.putString("Name", stopPoint.Name);
        dataBundle.putString("Point", "0");
        mainIntent.putExtras(dataBundle);
        startActivity(mainIntent);

        return true;
    }
}
