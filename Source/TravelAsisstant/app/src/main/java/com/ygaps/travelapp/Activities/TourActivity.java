package com.ygaps.travelapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ygaps.travelapp.Component.Constants;
import com.ygaps.travelapp.Component.StopPoint;
import com.ygaps.travelapp.Component.Tour;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.JsonCoordSet;
import com.ygaps.travelapp.Retrofit.MyAPIClient;
import com.ygaps.travelapp.Retrofit.RequestAddStopPoints;
import com.ygaps.travelapp.Retrofit.RequestCoordList;
import com.ygaps.travelapp.Retrofit.RequestInviteMember;
import com.ygaps.travelapp.Retrofit.RequestSearchDestination;
import com.ygaps.travelapp.Retrofit.ResponseStopPoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import okhttp3.ResponseBody;
import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TourActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnPoiClickListener,
        GoogleMap.OnCameraIdleListener
{
    private final int IDLE_MODE = 1;
    private final int TRAVEL_MODE = 2;
    private final int REQUEST_CODE = 5000;
    private final int MAX_LOCATIONS = 10;
    private final float DISTANCE_OFFSET = 5000.0f;
    private final HashMap<String, Integer> mProvinceIds = new HashMap<>();
    private final List<Place.Field> mFields = new ArrayList<>();
    private final HashMap<Integer, String> mServiceIds = new HashMap<>();
    private boolean mIsFirstTimeZoom = true;
    private boolean mPermissionGranted;
    private boolean mIsLocationAvailable;
    private LocationCallback mLocationCallBack;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient mPlacesClient;
    private LocationRequest mLocationRequest;
    private MyAPIClient mRetrofit;
    private String mUserToken = "";

    private LatLng mPreviousSuggestLocation;
    private LatLng mCameraStop;

    private Location mCurrentLocation;
    private String mCurrentCity = "";
    private Integer mCurrentCityId = 0;

    private boolean mIsEditMode = false;    // change between editing current stop Point or add a new one
    private boolean mIsDeleteTour = false;
    private SearchView mSearchView;
    private Dialog mDialog;
    private EditText mNameStopEditText, mStopTypeEditText, mMinCostEditText, mMaxCostEditText;
    private TextView mTimeArriveTextView, mTimeLeaveTextView, mDateArriveTextView, mDateLeaveTextView, mAddressStopView;
    private Button mLeftButton, mRightButton;
    private Tour mTour;
    private ArrayList<Marker> mStopPointMarkers = new ArrayList<>();
    private ArrayList<Marker> mSuggestMarkers = new ArrayList<>();
    private StopPoint mSelectedStopPoint = new StopPoint();
    private List<StopPoint> mSuggestStopPoints = new ArrayList<>();
    private FloatingActionButton mBtnEdit, mBtnAddUser, mBtnEditTour;
    private boolean clickSetting = false;

    // date time
    private Calendar mArriveCalendar;
    private Calendar mLeaveCalendar;

    // thread handler
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Timer mGetSuggestionTimer = new Timer();
    private TimerTask mGetSuggestionTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        // get user token
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        mUserToken = sharedPreferences.getString("token", "");

        //get tour
        Intent intent = this.getIntent();
        mTour = (Tour) intent.getSerializableExtra(Constants.EXTRA_TOUR);
        if (mTour == null) mTour = new Tour();

        // retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        mRetrofit = retrofit.create(MyAPIClient.class);

        // anh xa ui elements
        anhXa();

        // ---- constant initialization ---- //
        initProvinceIds();
        initServiceIds();
        mFields.add(Place.Field.TYPES);
        mFields.add(Place.Field.ADDRESS);
        mFields.add(Place.Field.NAME);
        mFields.add(Place.Field.LAT_LNG);
        mFields.add(Place.Field.ID);

        // ---- custom variables ---- //
        mArriveCalendar = new GregorianCalendar(TimeZone.getDefault());
        mLeaveCalendar = new GregorianCalendar(TimeZone.getDefault());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // ---- google services initialization ---- //
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(this, getString(R.string.google_maps_key));
        mPlacesClient = Places.createClient(this);

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mFusedLocationProviderClient != null && mLocationCallBack != null)
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallBack);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (mFusedLocationProviderClient != null)
            mFusedLocationProviderClient.flushLocations();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mFusedLocationProviderClient != null && mLocationCallBack != null && mLocationRequest != null)
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                //search here
                Log.d("onQueryTextSubmit", "Submitting searched string: " + s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {

//                String data[]={"Emmanuel", "Olayemi", "Henrry", "Mark", "Steve", "Ayomide", "David", "Anthony", "Adekola", "Adenuga"};
//                SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)
//                        mSearchView.findViewById(R.id.search_src_text);
//                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TourActivity.this, android.R.layout.simple_spinner_dropdown_item, data);
//                searchAutoComplete.setAdapter(dataAdapter);

                Log.d("onQueryTextChange", "Searching using string: " + s);

                RequestSearchDestination req = new RequestSearchDestination();
                req.Keyword = s;
                req.PageIndex = 1;
                req.PageSize = 5;
                req.ProvinceId = mCurrentCityId;
                req.ProvinceName = mCurrentCity;

                Call<RequestAddStopPoints> call = mRetrofit.searchDestination(mUserToken, req.Keyword, req.ProvinceId, req.ProvinceName, req.PageIndex, req.PageSize);
                call.enqueue(new Callback<RequestAddStopPoints>()
                {
                    @EverythingIsNonNull
                    @Override
                    public void onResponse(Call<RequestAddStopPoints> call, Response<RequestAddStopPoints> response)
                    {
                        if (response.isSuccessful())
                        {
                            if (response.body() != null)
                            {
                                StopPoint stopPoint = response.body().StopPoints.get(0);
                                /*-------------------------------------*/
                                /* Hiện danh sách các stop Point ở đây */
                                /*-------------------------------------*/
                            } else
                            {
                                Log.w("onQueryTextChange", "response's body is null");
                            }

                        } else
                        {
                            Toast.makeText(TourActivity.this, response.message(), Toast.LENGTH_LONG).show();
                            try
                            {
                                assert response.errorBody() != null;
                                Log.e("onQueryTextChange", String.format("%s: %s", response.message(), response.errorBody().string()));
                            }
                            catch (IOException ignored)
                            {
                            }
                        }
                    }

                    @EverythingIsNonNull
                    @Override
                    public void onFailure(Call<RequestAddStopPoints> call, Throwable t)
                    {
                        Log.w("onQueryTextChange", String.format("Search Destination failed: %s", t.getMessage()));
                        Toast.makeText(TourActivity.this, "Search Destination failed", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;


            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void initServiceIds()
    {
        int i = 0;
        mServiceIds.put(++i, getResources().getString(R.string.restaurant));
        mServiceIds.put(++i, getResources().getString(R.string.hotel));
        mServiceIds.put(++i, getResources().getString(R.string.rest_station));
        mServiceIds.put(++i, getResources().getString(R.string.other));
    }

    public void initProvinceIds()
    {
        int i = 0;
        mProvinceIds.put("Hồ Chí Minh", ++i);
        mProvinceIds.put("Hà Nội", ++i);
        mProvinceIds.put("Đà Nẵng", ++i);
        mProvinceIds.put("Bình Dương", ++i);
        mProvinceIds.put("Đồng Nai", ++i);
        mProvinceIds.put("Khánh Hòa", ++i);
        mProvinceIds.put("Hải Phòng", ++i);
        mProvinceIds.put("Long An", ++i);
        mProvinceIds.put("Quảng Nam", ++i);
        mProvinceIds.put("Bà Rịa Vũng Tàu", ++i);
        mProvinceIds.put("Đắk Lắk", ++i);
        mProvinceIds.put("Cần Thơ", ++i);
        mProvinceIds.put("Bình Thuận", ++i);
        mProvinceIds.put("Lâm Đồng", ++i);
        mProvinceIds.put("Thừa Thiên Huế", ++i);
        mProvinceIds.put("Kiên Giang", ++i);
        mProvinceIds.put("Bắc Ninh", ++i);
        mProvinceIds.put("Quảng Ninh", ++i);
        mProvinceIds.put("Thanh Hóa", ++i);
        mProvinceIds.put("Nghệ An", ++i);
        mProvinceIds.put("Hải Dương", ++i);
        mProvinceIds.put("Gia Lai", ++i);
        mProvinceIds.put("Bình Phước", ++i);
        mProvinceIds.put("Hưng Yên", ++i);
        mProvinceIds.put("Bình Định", ++i);
        mProvinceIds.put("Tiền Giang", ++i);
        mProvinceIds.put("Thái Bình", ++i);
        mProvinceIds.put("Bắc Giang", ++i);
        mProvinceIds.put("Hòa Bình", ++i);
        mProvinceIds.put("An Giang", ++i);
        mProvinceIds.put("Vĩnh Phúc", ++i);
        mProvinceIds.put("Tây Ninh", ++i);
        mProvinceIds.put("Thái Nguyên", ++i);
        mProvinceIds.put("Lào Cai", ++i);
        mProvinceIds.put("Nam Định", ++i);
        mProvinceIds.put("Quảng Ngãi", ++i);
        mProvinceIds.put("Bến Tre", ++i);
        mProvinceIds.put("Đắk Nông", ++i);
        mProvinceIds.put("Cà Mau", ++i);
        mProvinceIds.put("Vĩnh Long", ++i);
        mProvinceIds.put("Ninh Bình", ++i);
        mProvinceIds.put("Phú Thọ", ++i);
        mProvinceIds.put("Ninh Thuận", ++i);
        mProvinceIds.put("Phú Yên", ++i);
        mProvinceIds.put("Hà Nam", ++i);
        mProvinceIds.put("Hà Tĩnh", ++i);
        mProvinceIds.put("Đồng Tháp", ++i);
        mProvinceIds.put("Sóc Trăng", ++i);
        mProvinceIds.put("Kon Tum", ++i);
        mProvinceIds.put("Quảng Bình", ++i);
        mProvinceIds.put("Quảng Trị", ++i);
        mProvinceIds.put("Trà Vinh", ++i);
        mProvinceIds.put("Hậu Giang", ++i);
        mProvinceIds.put("Sơn La", ++i);
        mProvinceIds.put("Bạc Liêu", ++i);
        mProvinceIds.put("Yên Bái", ++i);
        mProvinceIds.put("Tuyên Quang", ++i);
        mProvinceIds.put("Điện Biên", ++i);
        mProvinceIds.put("Lai Châu", ++i);
        mProvinceIds.put("Lạng Sơn", ++i);
        mProvinceIds.put("Hà Giang", ++i);
        mProvinceIds.put("Bắc Kạn", ++i);
        mProvinceIds.put("Cao Bằng", ++i);
    }

    public void anhXa()
    {
        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.dialog_create_stop_point);

        mNameStopEditText = mDialog.findViewById(R.id.input_StopName);
        mAddressStopView = mDialog.findViewById(R.id.input_AdrresStop);
        mStopTypeEditText = mDialog.findViewById(R.id.input_service_type);
        mTimeArriveTextView = mDialog.findViewById(R.id.input_TimeArrive);
        mTimeLeaveTextView = mDialog.findViewById(R.id.input_TimeLeave);
        mDateArriveTextView = mDialog.findViewById(R.id.input_DateArrive);
        mDateLeaveTextView = mDialog.findViewById(R.id.input_DateLeave);
        mMaxCostEditText = mDialog.findViewById(R.id.input_MaxCostStop);
        mMinCostEditText = mDialog.findViewById(R.id.input_MinCostStop);
        mRightButton = mDialog.findViewById(R.id.btnRight);
        mLeftButton = mDialog.findViewById(R.id.btnLeft);

        mBtnEdit = findViewById(R.id.btnEdit);
        mBtnEdit.setCustomSize(150);
        mBtnEdit.setImageDrawable(getDrawable(R.drawable.setting));

        mBtnAddUser = findViewById(R.id.btnAddUser);
        mBtnAddUser.setCustomSize(150);
        mBtnAddUser.setImageDrawable(getDrawable(R.drawable.user));

        mBtnEditTour = findViewById(R.id.btnEditTour);
        mBtnEditTour.setCustomSize(150);
        mBtnEditTour.setImageDrawable(getDrawable(R.drawable.edittour));
    }

    public void requestPermissions()
    {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // Permissions are not granted
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissions, REQUEST_CODE);
        } else
        {
            // Permission have been granted
            mPermissionGranted = true;
            onPermissionGranted();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnPoiClickListener(this);
        mMap.setOnCameraIdleListener(this);

        fetchStopPoints();
        requestPermissions();

        // ---- schedule a fetching suggested destinations ---- //
        mGetSuggestionTask = new TimerTask()
        {
            @Override
            public void run()
            {
                Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        LatLng latLng = mMap.getCameraPosition().target;
                        mPreviousSuggestLocation = latLng;
                        getSuggestDestinationsTask(latLng);
                    }
                };
                mHandler.post(runnable);
            }
        };
        mGetSuggestionTimer.schedule(mGetSuggestionTask, 10 * 1000, 60 * 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // permissions are granted
                mPermissionGranted = true;
                onPermissionGranted();
            } else
            {
                // permissions are denied, close activity
                finish();
            }
        }
    }

    private void onPermissionGranted()
    {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);

        mLocationRequest = createLocationRequest(true);
        LocationCallback callback = new LocationCallback()
        {
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability)
            {
                mIsLocationAvailable = locationAvailability.isLocationAvailable();
            }

            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                if (mIsLocationAvailable)
                {
                    List<Location> locations = locationResult.getLocations();
                    Log.d("onLocationResult", String.format("Receive %d location results", locations.size()));
                    mCurrentLocation = locations.get(locations.size() - 1);

                    if (mIsFirstTimeZoom)
                    {
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 22);
                        mMap.animateCamera(update);
                        mIsFirstTimeZoom = false;
                    }

                    // update user's current location: city and its id
                    updateLocationTask(mCurrentLocation);
                }
            }
        };
        mLocationCallBack = callback;
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, callback, Looper.getMainLooper());
    }

    public LocationRequest createLocationRequest(boolean idleMode)
    {
        LocationRequest request = new LocationRequest();
        if (idleMode)
        {
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setInterval(1000 * 30);
            request.setFastestInterval(1000 * 30);
        } else
        {
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setInterval(1000 * 10);
            request.setFastestInterval(1000 * 5);
        }
        return request;
    }

    public void changeLocationRequest(boolean idleMode)
    {
        if (mFusedLocationProviderClient != null && mLocationCallBack != null && mLocationRequest != null)
        {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallBack);
            mLocationRequest = createLocationRequest(idleMode);
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.getMainLooper());
        }
    }

    public void getSuggestDestinationsTask(LatLng latLng)
    {
        if (latLng == null) return;
        LatLng first = new LatLng(latLng.latitude - 0.03, latLng.longitude - 0.03);
        LatLng second = new LatLng(latLng.latitude + 0.03, latLng.longitude + 0.03);
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
                        mSuggestStopPoints = response.body().StopPoints;
                        Log.i("getSuggestDestinationsTask", "Receive " + mSuggestStopPoints.size() + " suggested destinations");
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

    @SuppressLint("StaticFieldLeak")
    public void findAddressAsync(final LatLng target, final Consumer<List<Address>> callback)
    {
        new AsyncTask<LatLng, Integer, List<Address>>()
        {
            @Override
            protected void onPostExecute(List<Address> collection)
            {
                callback.accept(collection);
            }

            @Override
            protected List<Address> doInBackground(LatLng... latLngs)
            {
                Geocoder geocoder = new Geocoder(TourActivity.this, Locale.getDefault());
                try
                {
                    List<Address> collection = geocoder.getFromLocation(latLngs[0].latitude, latLngs[0].longitude, MAX_LOCATIONS);
                    return collection;
                }
                catch (IOException e)
                {
                    Log.e("findAddressAsync", e.getMessage());
                    return new ArrayList<>();
                }
            }
        }.execute(target);
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        Log.d("onMapClick", String.format("latLng %s", latLng.toString()));

        Consumer<List<Address>> consumer = new Consumer<List<Address>>()
        {
            @Override
            public void accept(List<Address> addresses)
            {
                if (isNullOrEmpty(addresses)) return;
                Address address = addresses.get(0);

                // move camera to location
                LatLng zoomTo = new LatLng(address.getLatitude(), address.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(zoomTo, 22);
                mMap.animateCamera(update);

                // create a stop Point
                mSelectedStopPoint = new StopPoint();
                mSelectedStopPoint.ProvinceId = mProvinceIds.getOrDefault(address.getAdminArea(), mProvinceIds.get("Hồ Chí Minh"));
                mSelectedStopPoint.Name = address.getAddressLine(0);
                mSelectedStopPoint.Address = address.getAddressLine(0);
                mSelectedStopPoint.ServiceTypeId = mServiceIds.size();
                mSelectedStopPoint.Lat = zoomTo.latitude;
                mSelectedStopPoint.Long = zoomTo.longitude;

                // update UI
                mNameStopEditText.setText(mSelectedStopPoint.Name);
                mAddressStopView.setText(mSelectedStopPoint.Address);
                mStopTypeEditText.setText(R.string.other);
                openStopPointDialog();
            }
        };
        findAddressAsync(latLng, consumer);
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
    public boolean onMarkerClick(Marker marker)
    {
        if (marker.getTag() == null) return true;

        try
        {
            // assume this marker's tag is stop Point id
            int tourId = Integer.parseInt(marker.getTag().toString());
            mSelectedStopPoint = null;
            for (StopPoint stopPoint : mTour.StopPoints)
            {
                if (stopPoint.Id.equals(tourId))
                {
                    // update UI
                    mIsEditMode = true;
                    setStopPointDialogue(stopPoint);
                    mSelectedStopPoint = stopPoint;
                    openStopPointDialog();
                    break;
                }
            }

        }
        catch (NumberFormatException ignored)
        {
            // this marker's tag is a stop Point from suggested destinations
            mSelectedStopPoint = (StopPoint) marker.getTag();
            mIsEditMode = false;
            setStopPointDialogue(mSelectedStopPoint);
            openStopPointDialog();
        }
        return true;
    }


    public void setStopPointDialogue(StopPoint stopPoint)
    {
        if (stopPoint == null) return;
        mStopTypeEditText.setText(mServiceIds.getOrDefault(stopPoint.ServiceTypeId, getResources().getString(R.string.other)));
        mNameStopEditText.setText(stopPoint.Name);
        mAddressStopView.setText(stopPoint.Address);
        mDateArriveTextView.setText(convertDate(stopPoint.ArrivalAt));
        mTimeArriveTextView.setText(convertTime(stopPoint.ArrivalAt));
        mDateLeaveTextView.setText(convertDate(stopPoint.LeaveAt));
        mTimeLeaveTextView.setText(convertTime(stopPoint.LeaveAt));
        mMinCostEditText.setText(stopPoint.MinCost.toString());
        mMaxCostEditText.setText(stopPoint.MaxCost.toString());
    }

    @Override
    public boolean onMyLocationButtonClick()
    {
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>()
        {
            @Override
            public void onSuccess(Location location)
            {
                if (location != null)
                {
                    mCurrentLocation = location;
                    updateLocationTask(mCurrentLocation);
                    // move camera to user's location
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 22);
                    mMap.animateCamera(update);
                    mIsFirstTimeZoom = false;
                }
            }
        });
        return true;
    }

    @Override
    public void onPoiClick(final PointOfInterest pointOfInterest)
    {
        Log.d("onPoiClick", String.format("POI %s", pointOfInterest.name));
        Consumer<List<Address>> consumer = new Consumer<List<Address>>()
        {
            @Override
            public void accept(List<Address> addresses)
            {
                if (isNullOrEmpty(addresses)) return;
                Address address = addresses.get(0);

                // move camera to location
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(pointOfInterest.latLng, 22);
                mMap.animateCamera(update);

                // create a stop Point
                mSelectedStopPoint = new StopPoint();
                mSelectedStopPoint.ProvinceId = mProvinceIds.getOrDefault(address.getAdminArea(), mProvinceIds.get("Hồ Chí Minh"));

                // fetching place service type
                FetchPlaceRequest request = FetchPlaceRequest.newInstance(pointOfInterest.placeId, mFields);
                Task<FetchPlaceResponse> responseTask = mPlacesClient.fetchPlace(request);
                responseTask.addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(TourActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("onPoiClick", e.getMessage());
                    }
                });
                responseTask.addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>()
                {
                    @Override
                    public void onSuccess(FetchPlaceResponse response)
                    {
                        Place place = response.getPlace();
                        Place.Type type = place.getTypes().get(0);
                        String typeName = type.name().replace('_', ' ').toLowerCase();

                        // setup stop Point
                        for (Integer id : mServiceIds.keySet())
                        {
                            if (mServiceIds.get(id).equals(typeName))
                            {
                                mSelectedStopPoint.ServiceTypeId = id;
                                break;
                            }
                        }
                        if (mSelectedStopPoint.ServiceTypeId <= 0)
                            mSelectedStopPoint.ServiceTypeId = mServiceIds.size();
                        mSelectedStopPoint.Name = place.getName();
                        mSelectedStopPoint.Address = place.getAddress();
                        if (place.getLatLng() != null)
                        {
                            mSelectedStopPoint.Lat = place.getLatLng().latitude;
                            mSelectedStopPoint.Long = place.getLatLng().longitude;
                        } else
                        {
                            mSelectedStopPoint.Lat = pointOfInterest.latLng.latitude;
                            mSelectedStopPoint.Long = pointOfInterest.latLng.longitude;
                        }

                        // update UI
                        mIsEditMode = false;
                        mNameStopEditText.setText(mSelectedStopPoint.Name);
                        mAddressStopView.setText(mSelectedStopPoint.Address);
                        mStopTypeEditText.setText(typeName);
                        openStopPointDialog();
                    }
                });
            }
        };
        findAddressAsync(pointOfInterest.latLng, consumer);
    }

    public void openStopPointDialog()
    {
        if (mIsEditMode)
        {
            mLeftButton.setText(R.string.remove);
            mRightButton.setText(R.string.update);
        } else
        {
            mLeftButton.setText(R.string.cancel);
            mRightButton.setText(R.string.add);
        }
        mDialog.setTitle("Choose method");
        //mDialog.getWindow().setLayout(1500,2000);

        Rect displayRectangle = new Rect();
        Window window = getWindow();

        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        mDialog.getWindow().setLayout((int) (displayRectangle.width() * 0.95f), (int) (displayRectangle.height() * 0.98f));
        mDialog.show();
    }

    public void onClickLeftButton(View view)
    {
        if (mIsEditMode)
        {
            // Remove stop Point as well as its marker
            Call<ResponseBody> call = mRetrofit.removeStopPoint(mUserToken, mSelectedStopPoint.Id);
            call.enqueue(new Callback<ResponseBody>()
            {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    if (response.isSuccessful())
                    {
                        fetchTourInfoFromServer(true);
                    } else onRetrofitNotSuccessful(response, "onClickLeftButton");
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    onRetrofitFailure(t, "onClickLeftButton");
                }
            });
        }
        mDialog.dismiss();
    }

    public void addStopPoint(String auth, final RequestAddStopPoints request)
    {
        //call api
        Call<ResponseBody> call = mRetrofit.addStopPoints(auth, request);
        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
            {
                if (response.isSuccessful())
                {
                    Toast.makeText(TourActivity.this, response.message(), Toast.LENGTH_SHORT).show();

                    if (response.body() != null)
                    {
                        // update stop points list
                        fetchTourInfoFromServer(true);
                    } else
                    {
                        onRetrofitNullResponseBody("addStopPoint");
                    }

                } else
                {
                    onRetrofitNotSuccessful(response, "addStopPoint");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                onRetrofitFailure(t, "addStopPoint");
            }
        });
    }

    public void fetchTourInfoFromServer(final boolean isFetchStopPoints)
    {
        Call<Tour> call = mRetrofit.getTourInfo(mUserToken, mTour.Id);
        call.enqueue(new Callback<Tour>()
        {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        mTour = response.body();
                        if (isFetchStopPoints) fetchStopPoints();
                    } else
                    {
                        onRetrofitNullResponseBody("fetchTourInfoFromServer");
                    }

                } else
                {
                    onRetrofitNotSuccessful(response, "fetchTourInfoFromServer");
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<Tour> call, Throwable t)
            {
                onRetrofitFailure(t, "fetchTourInfoFromServer");
            }
        });
    }

    public void fetchSuggestStopPoints()
    {
        if (mSuggestStopPoints == null || mSuggestStopPoints.isEmpty()) return;

        // remove suggested stop points' markers from google map
        for (Marker item : mSuggestMarkers) item.remove();
        mSuggestMarkers.clear();

        for (StopPoint item : mSuggestStopPoints)
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
            mSuggestMarkers.add(marker);
        }
    }

    public void fetchStopPoints()
    {
        if (mStopPointMarkers == null) return;

        // remove markers from google map
        for (Marker item : mStopPointMarkers)
        {
            item.remove();
        }
        mStopPointMarkers.clear();

        for (StopPoint item : mTour.StopPoints)
        {
            // setup marker properties
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(item.Lat, item.Long));
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            options.title(item.Address);
            options.snippet(item.Name);

            // set the id of marker to the id of its stop Point
            // this id is used to update or remove marker in future
            Marker marker = mMap.addMarker(options);
            marker.setZIndex(100);
            marker.setTag(item.Id.toString());
            mStopPointMarkers.add(marker);
        }
    }

    public void onClickRightButton(View view)
    {
        if (mNameStopEditText.getText().toString().isEmpty() || mAddressStopView.getText().toString().isEmpty()
                || mStopTypeEditText.getText().toString().isEmpty() || mTimeLeaveTextView.getText().toString().isEmpty() ||
                mDateLeaveTextView.getText().toString().isEmpty() || mTimeArriveTextView.getText().toString().isEmpty() ||
                mDateArriveTextView.getText().toString().isEmpty() || mMinCostEditText.getText().toString().isEmpty() ||
                mMaxCostEditText.getText().toString().isEmpty())
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.message_fill_information))
                    .show();
        } else
        {
            String minStr = mMinCostEditText.getText().toString().trim();
            String maxStr = mMaxCostEditText.getText().toString().trim();
            Long min = Long.valueOf(minStr, 10);
            Long max = Long.valueOf(maxStr, 10);

            // update properties of stop Point
            mSelectedStopPoint.MaxCost = max;
            mSelectedStopPoint.MinCost = min;
            mSelectedStopPoint.ArrivalAt = mArriveCalendar.getTime().getTime();
            mSelectedStopPoint.LeaveAt = mLeaveCalendar.getTime().getTime();
            mSelectedStopPoint.Name = mNameStopEditText.getText().toString();

            if (mIsEditMode)
            {
                // change existed stop Point
                RequestAddStopPoints req = new RequestAddStopPoints(mTour.Id, Collections.singletonList(mSelectedStopPoint));
                Call<ResponseBody> call = mRetrofit.addStopPoints(mUserToken, req);
                call.enqueue(new Callback<ResponseBody>()
                {
                    @EverythingIsNonNull
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        if (response.isSuccessful())
                        {
                            fetchTourInfoFromServer(false);
                        } else
                        {
                            onRetrofitNotSuccessful(response, "onClickRightButton");
                        }
                    }

                    @EverythingIsNonNull
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
                    {
                        onRetrofitFailure(t, "onClickRightButton");
                    }
                });
            } else
            {
                // Add new stop Point
                mSelectedStopPoint.Id = null;
                RequestAddStopPoints req = new RequestAddStopPoints(mTour.Id, Collections.singletonList(mSelectedStopPoint));
                addStopPoint(mUserToken, req);
            }
        }
        mDialog.cancel();
    }

    public void onClickChooseArriveTime(View view)
    {
        // set time to current time
        int hour = mArriveCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mArriveCalendar.get(Calendar.MINUTE);
        mTimeArriveTextView.setText(String.format(Locale.US, "%d:%d", hour, minute));

        // init mDialog
        TimePickerDialog picker;
        picker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                mArriveCalendar.set(Calendar.HOUR, selectedHour);
                mArriveCalendar.set(Calendar.MINUTE, selectedMinute);
                mTimeArriveTextView.setText(String.format(Locale.US, "%d:%d", selectedHour, selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time
        picker.setTitle("Arrive Time");
        picker.show();
    }

    public void onClickChooseLeaveTime(View view)
    {
        // set time to current time
        int hour = mLeaveCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mLeaveCalendar.get(Calendar.MINUTE);
        mTimeLeaveTextView.setText(String.format(Locale.US, "%d:%d", hour, minute));

        // init mDialog
        TimePickerDialog picker;
        picker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                mLeaveCalendar.set(Calendar.HOUR, selectedHour);
                mLeaveCalendar.set(Calendar.MINUTE, selectedMinute);
                mTimeLeaveTextView.setText(String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time
        picker.setTitle("Leave Time");
        picker.show();
    }

    public void onClickChooseArriveDate(View view)
    {
        int date = mArriveCalendar.get(Calendar.DATE);
        int month = mArriveCalendar.get(Calendar.MONTH);
        int year = mArriveCalendar.get(Calendar.YEAR);
        mDateArriveTextView.setText(String.format(Locale.US, "%02d/%02d/%02d", date, month + 1, year));

        DatePickerDialog picker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                mArriveCalendar.set(year, month, dayOfMonth);
                mDateArriveTextView.setText(String.format(Locale.US, "%02d/%02d/%02d", dayOfMonth, month + 1, year));
            }
        }, year, month, date);
        picker.setTitle("Arrive Date");
        picker.show();
    }


    public void OnClickSettingTour(View view)
    {
        view = findViewById(R.id.btnEdit);
        if (!clickSetting)
        {
            showSetting();
            clickSetting = true;
        } else
        {
            hideSetting();
            clickSetting = false;
        }
    }

    public void showSetting()
    {
        mBtnEditTour.show();
        mBtnAddUser.show();
    }

    public void hideSetting()
    {
        mBtnEditTour.hide();
        mBtnAddUser.hide();
    }


    public void OnClickAddUser(View view)
    {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_adduser);
        Button btnAdd = dialog.findViewById(R.id.btn_adduser_add);
        Button btnCancel = dialog.findViewById(R.id.btn_adduser_cancel);
        final EditText editor = dialog.findViewById(R.id.et_adduser);
        editor.selectAll();
        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String userId = editor.getText().toString();

                RequestInviteMember req = new RequestInviteMember();
                req.TourId = mTour.Id;
                req.InvitedUserId = userId;
                req.IsInvited = mTour.IsPrivate;
                Call<ResponseBody> call = mRetrofit.inviteMember(mUserToken, req);
                call.enqueue(new Callback<ResponseBody>()
                {
                    @EverythingIsNonNull
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        if (response.isSuccessful())
                        {
                            new AlertDialog.Builder(TourActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setTitle(getString(R.string.successful))
                                    .setMessage(getString(R.string.message_invite_success))
                                    .show();
                        } else
                        {
                            try
                            {
                                Log.e("inviteUser", response.errorBody().string());
                                new AlertDialog.Builder(TourActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(getString(R.string.error))
                                        .setMessage(getString(R.string.message_fail_invite_user))
                                        .show();
                                dialog.dismiss();
                            }
                            catch (Exception ignored)
                            {
                            }
                        }
                    }

                    @EverythingIsNonNull
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
                    {
                        Log.e("inviteUser", String.format("Failed to invite member: %s", t.getMessage()));
                        new AlertDialog.Builder(TourActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(getString(R.string.error))
                                .setMessage(getString(R.string.message_fail_invite_user))
                                .show();
                        dialog.dismiss();
                    }
                });
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void OnClickEditTour(View view)
    {
        Intent intent = new Intent(this, AddTour.class);
        intent.putExtra(Constants.EXTRA_TOUR, mTour);
        intent.putExtra(Constants.EXTRA_DELETE_TOUR, true);
        startActivityForResult(intent, Constants.REQUEST_ADD_TOUR);
        mIsDeleteTour = true;
    }


    public void onClickChooseLeaveDate(View view)
    {
        int date = mLeaveCalendar.get(Calendar.DATE);
        int month = mLeaveCalendar.get(Calendar.MONTH);
        int year = mLeaveCalendar.get(Calendar.YEAR);
        mDateLeaveTextView.setText(String.format(Locale.US, "%02d/%02d/%02d", date, month + 1, year));

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                mLeaveCalendar.set(year, month, dayOfMonth);
                mDateLeaveTextView.setText(String.format(Locale.US, "%02d/%02d/%02d", dayOfMonth, month + 1, year));
            }
        }, year, month, date);
        datePickerDialog.setTitle("Leave Date");
        datePickerDialog.show();
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
        Toast.makeText(TourActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
    }

    @SuppressLint("StaticFieldLeak")
    public void updateLocationTask(Location location)
    {
        if (location == null) return;

        Consumer<List<Address>> consumer = new Consumer<List<Address>>()
        {
            @Override
            public void accept(List<Address> addresses)
            {
                if (isNullOrEmpty(addresses)) return;
                Address address = addresses.get(0);
                mCurrentCity = "Hồ Chí Minh";
                mCurrentCityId = mProvinceIds.get(mCurrentCity);
                for (String city : mProvinceIds.keySet())
                {
                    if (city.equals(address.getAdminArea()))
                    {
                        mCurrentCity = city;
                        mCurrentCityId = mProvinceIds.get(city);
                        break;
                    }
                }
            }
        };

        LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
        findAddressAsync(target, consumer);
    }

    public boolean isNullOrEmpty(List collection)
    {
        return collection == null || collection.isEmpty();
    }

    @Override
    public void onCameraIdle()
    {
        Log.d("onCameraIdle", String.format("Camera stops at %s", mMap.getCameraPosition().target));
        if (mPreviousSuggestLocation == null) return;
        LatLng from = mPreviousSuggestLocation;
        LatLng to = mMap.getCameraPosition().target;
        float[] results = new float[3];
        Location.distanceBetween(from.latitude, from.longitude, to.latitude, to.longitude, results);

        if (results[0] >= DISTANCE_OFFSET)
        {
            getSuggestDestinationsTask(mMap.getCameraPosition().target);
            mPreviousSuggestLocation = mMap.getCameraPosition().target;
        }

        Log.i("onCameraIdle", String.format("Distance between %s - %s is %f", from, to, results[0]));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_ADD_TOUR)
        {
            if (data != null)
            {
                if (resultCode == Constants.RESULT_SUCCESS)
                {
                    if (mIsDeleteTour)
                    {
                        mTour.Status = Constants.STATUS_TOUR_CANCELED;
                        Call<ResponseBody> call = mRetrofit.updateTourInfo(mUserToken, mTour);
                        call.enqueue(new Callback<ResponseBody>()
                        {
                            @EverythingIsNonNull
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                            {
                                finish();
                            }

                            @EverythingIsNonNull
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t)
                            {
                                finish();
                            }
                        });
                    }
                    else if (data.hasExtra(Constants.EXTRA_TOUR))
                    {
                        mTour = (Tour) data.getSerializableExtra(Constants.EXTRA_TOUR);
                        new AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle(getString(R.string.successful))
                                .setMessage(getString(R.string.message_update_successfully))
                                .show();
                    }

                } else
                {
                    Log.e("TourActivity", String.format("Failed to update tour: %s", data.getStringExtra(Constants.EXTRA_ERROR)));
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getString(R.string.error))
                            .setMessage(getString(R.string.message_update_error))
                            .show();
                }
            }
        }
    }

    public void onClickCanCel(View view)
    {
        mDialog.dismiss();
    }
}
