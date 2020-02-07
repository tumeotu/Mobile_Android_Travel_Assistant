package com.ygaps.travelapp.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ygaps.travelapp.Adapters.AdapterTour;
import com.ygaps.travelapp.Component.Constants;
import com.ygaps.travelapp.Component.Tour;
import com.ygaps.travelapp.Component.User;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;
import com.ygaps.travelapp.Retrofit.ResponseListTour;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class ListTour extends AppCompatActivity
{
    private ListView lvTour;
    private Button mBtnListTour, mBtnYourListTour, mBtnLocation, mBtnNotify, mBtnSetting;
    private TextView mTotalTextView;
    private SearchView mSearchView;
    private List<Tour> mTours;
    private AdapterTour mAdapter;
    private FloatingActionButton mBtnAddTour;
    private String mUserToken = "";
    private String mDeviceId = "";
    private MyAPIClient mRetrofit;

    private boolean isListTour = false;
    private boolean isUserListTour = false;

    private int preLast;
    private boolean isLoading = false;


    private Dialog mDialogTour;
    private Button Rating, Join;
    private TextView IDTour, NameTour, NameCreaterTour, IDCreaterTour, Child, Adult, MinCost, MaxCost, DateTime;
    private User account;


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
        // build retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        mRetrofit = retrofit.create(MyAPIClient.class);

        // get user token
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        mUserToken = sharedPreferences.getString("token", "");

        // create firebase service
        FirebaseApp.initializeApp(this);
        MyFireBaseService.UserToken = mUserToken;
        MyFireBaseService.DeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        this.startService(new Intent(this, MyFireBaseService.class));

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>()
        {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult)
            {
                MyFireBaseService.sendRegistrationToServer(instanceIdResult.getToken());
            }
        });

        setContentView(R.layout.activity_list_tour);
        init();
        Intent intent = getIntent();
        try
        {
            String nameActivity = intent.getStringExtra("NameActivity");
            getDataUser();
            if (nameActivity.isEmpty())
            {
                isListTour = true;
                isUserListTour = false;
                getData();
                View view = findViewById(R.id.btn_list_tour);
                Drawable img = view.getContext().getResources().getDrawable(R.drawable.listtour1);
                img.setBounds(0, 0, 100, 100);
                mBtnListTour.setCompoundDrawables(null, null, null, img);
                View view1 = findViewById(R.id.btn_you_list_tour);
                Drawable img1 = view1.getContext().getResources().getDrawable(R.drawable.yourlisttour);
                img1.setBounds(0, 0, 100, 100);
                mBtnYourListTour.setCompoundDrawables(null, null, null, img1);
            }
            if (nameActivity.equals("UserListTour"))
            {
                isUserListTour = true;
                isListTour = false;
                getDataOfUser();

                View view = findViewById(R.id.btn_list_tour);
                Drawable img = view.getContext().getResources().getDrawable(R.drawable.listour);
                img.setBounds(0, 0, 100, 100);
                mBtnListTour.setCompoundDrawables(null, null, null, img);
                View view1 = findViewById(R.id.btn_you_list_tour);
                Drawable img1 = view1.getContext().getResources().getDrawable(R.drawable.yourlisttour1);
                img1.setBounds(0, 0, 100, 100);
                mBtnYourListTour.setCompoundDrawables(null, null, null, img1);
            }
        }
        catch (Exception e)
        {
            getData();
        }

//        lvTour.setOnScrollListener(new AbsListView.OnScrollListener()
//        {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState)
//            {
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
//            {
//                if (lvTour.getAdapter() == null)
//                    return;
//                if (lvTour.getAdapter().getCount() == 0)
//                    return;
//
//                int l = visibleItemCount + firstVisibleItem;
//                if (l >= totalItemCount && !isLoading)
//                {
//                    // It is time to add new data. We call the listener
//                    isLoading = true;
//                    if (isListTour)
//                        getData();
//                    else
//                        getDataOfUser();
//                    mAdapter.notifyDataSetChanged();
//                    isLoading = false;
//                }
//            }
//        });
    }

    public void init()
    {
        lvTour = findViewById(R.id.lv_listtour);
        mTours = new ArrayList<>();
        mTotalTextView = findViewById(R.id.tv_total_tour);
        mBtnListTour = findViewById(R.id.btn_list_tour);
        mBtnYourListTour = findViewById(R.id.btn_you_list_tour);
        mBtnLocation = findViewById(R.id.btn_location_tour);
        mBtnNotify = findViewById(R.id.btn_notification);
        mBtnSetting = findViewById(R.id.btn_settting);

        mBtnAddTour = findViewById(R.id.add_tour);
        mBtnAddTour.setCustomSize(150);
        mBtnAddTour.setImageDrawable(getDrawable(R.drawable.logo_add));

        mDialogTour = new Dialog(this);
        mDialogTour.setContentView(R.layout.dialog_tour_info);

        Rating = mDialogTour.findViewById(R.id.btnRating);
        IDTour = mDialogTour.findViewById(R.id.IDTour);
        NameTour = mDialogTour.findViewById(R.id.NameTour);
        IDCreaterTour = mDialogTour.findViewById(R.id.IDCreatorTour);
        Child = mDialogTour.findViewById(R.id.ChildsTour);
        Adult = mDialogTour.findViewById(R.id.AdultsTour);
        MinCost = mDialogTour.findViewById(R.id.MinCostTour);
        MaxCost = mDialogTour.findViewById(R.id.MaxCostTour);
        IDTour = mDialogTour.findViewById(R.id.IDTour);
        DateTime = mDialogTour.findViewById(R.id.DateTimeTour);
    }

    public void TourDetail(String tourID)
    {
        if (isUserListTour)
        {
            Intent dialogIntent = new Intent(this, TourDetailActivity.class);
            dialogIntent.putExtra("TourId", tourID);
            this.startActivity(dialogIntent);
        }
    }

    public void DialogTourInfo(final Tour tour)
    {
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        mDialogTour.getWindow().setLayout((int) (displayRectangle.width() * 0.95f), (int) (displayRectangle.height() * 0.8f));
        mDialogTour.setCanceledOnTouchOutside(true);

        IDTour.setText(tour.Id.toString());
        NameTour.setText(tour.Name);
        IDCreaterTour.setText(tour.HostId);
        String startDate, endDate;
        DateFormat simple = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tour.StartDate);
        startDate = simple.format(calendar.getTime());
        calendar.setTimeInMillis(tour.EndDate);
        endDate = simple.format(calendar.getTime());

        DateTime.setText(String.format(Locale.getDefault(), "%s - %s", startDate, endDate));
        Child.setText(tour.Children.toString());
        Adult.setText(tour.Adults.toString());
        MinCost.setText(tour.MinCost.toString());
        MaxCost.setText(tour.MaxCost.toString());

        Rating.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent mainIntent = new Intent(ListTour.this, RatingTourStopPoint.class);
                Bundle dataBundle = new Bundle();
                dataBundle.putString("Type", String.valueOf(0));
                dataBundle.putString("Id", tour.Id);
                dataBundle.putString("Name", tour.Name);
                dataBundle.putString("Point", "0");
                mainIntent.putExtras(dataBundle);
                startActivity(mainIntent);
                mDialogTour.dismiss();
            }
        });
        mDialogTour.show();
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
                        Toast.makeText(ListTour.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    Toast.makeText(ListTour.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            @retrofit2.internal.EverythingIsNonNull
            public void onFailure(Call<User> call, Throwable t)
            {
                Toast.makeText(ListTour.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDataOfUser()
    {
        Call<ResponseListTour> call = mRetrofit.getListTourOfUser(mUserToken, 1, "100");

        call.enqueue(new Callback<ResponseListTour>()
        {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ResponseListTour> call, Response<ResponseListTour> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        mTours = response.body().Tours;
                        if (mTours.size() != response.body().Total)
                        {
                            Log.w("getDate", String.format("Mismatching tours count: Server send %d but received %d", response.body().Total, mTours.size()));
                        }
                        mTotalTextView.setText(String.format(Locale.getDefault(), "%d %s", mTours.size(), getString(R.string.trips)));
                        // some properties of tour maybe null, fetching task will ensure no property is null
                        fetchTourTask(mTours);
                    } else
                    {
                        Toast.makeText(ListTour.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    Toast.makeText(ListTour.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ResponseListTour> call, Throwable t)
            {
                Toast.makeText(ListTour.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getData()
    {
        Call<ResponseListTour> call = mRetrofit.getListTour(mUserToken, 100, 1);
        call.enqueue(new Callback<ResponseListTour>()
        {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ResponseListTour> call, Response<ResponseListTour> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        mTours = response.body().Tours;
                        if (mTours.size() != response.body().Total)
                        {
                            Log.w("getDate", String.format("Mismatching tours count: Server send %d but received %d", response.body().Total, mTours.size()));
                        }
                        mTotalTextView.setText(String.format(Locale.US, "%d trips", mTours.size()));

                        // some properties of tour maybe null, fetching task will ensure no property is null
                        fetchTourTask(mTours);

                    } else
                    {
                        Toast.makeText(ListTour.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    Toast.makeText(ListTour.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ResponseListTour> call, Throwable t)
            {
                Toast.makeText(ListTour.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void fetchTourTask(List<Tour> tours)
    {
        if (tours == null) return;
        final ArrayList<Tour> fetchedTours = new ArrayList<>();
        Tour[] params = new Tour[tours.size()];
        tours.toArray(params);
        new AsyncTask<Tour, Integer, Integer>()
        {
            @Override
            protected Integer doInBackground(Tour... tours)
            {
                if (tours == null) return 0;
                int i = 0;
                for (Tour tour : tours)
                {
                    if (tour.Status != Constants.STATUS_TOUR_CANCELED)
                    {
                        if (tour.StartDate == null) tour.StartDate = 0L;
                        if (tour.EndDate == null) tour.EndDate = 0L;
                        fetchedTours.add(tour);
                        publishProgress(++i);
                    }
                }
                return i;
            }

            @Override
            protected void onPostExecute(Integer result)
            {
                mAdapter = new AdapterTour(ListTour.this, R.layout.row_tour, fetchedTours);
                lvTour.setAdapter(mAdapter);
                super.onPostExecute(result);
            }

            @Override
            protected void onProgressUpdate(Integer... values)
            {
                mTotalTextView.setText(String.format(Locale.getDefault(), "%d %s", values[0], getString(R.string.trips)));
                super.onProgressUpdate(values);
            }
        }.execute(params);
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
                mAdapter.filter(s.trim());
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void onClickAddTour(View view)
    {
        Intent intent = new Intent(ListTour.this, AddTour.class);
        startActivityForResult(intent, Constants.REQUEST_ADD_TOUR);
    }

    public void onClickSetting(View view)
    {
        view = findViewById(R.id.btn_settting);

//        Drawable img1 = view.getContext().getResources().getDrawable(R.drawable.setting1);
//        img1.setBounds(0, 0, 100, 100);
//        mBtnSetting.setCompoundDrawables(null, null, null, img1);
//
        Drawable img2 = view.getContext().getResources().getDrawable(R.drawable.yourlisttour);
        img2.setBounds(0, 0, 100, 100);
        mBtnYourListTour.setCompoundDrawables(null, null, null, img2);

        Drawable img3 = view.getContext().getResources().getDrawable(R.drawable.listour);
        img3.setBounds(0, 0, 100, 100);
        mBtnListTour.setCompoundDrawables(null, null, null, img3);

        Intent intent = new Intent(ListTour.this, UserActivity.class);
        startActivity(intent);
    }

    public void onClickUserListTour(View view)
    {
        isListTour = false;
        isUserListTour = true;
        mTours.clear();
        getDataOfUser();
        Drawable img1 = view.getContext().getResources().getDrawable(R.drawable.yourlisttour1);
        img1.setBounds(0, 0, 100, 100);
        mBtnYourListTour.setCompoundDrawables(null, null, null, img1);
        Drawable img = view.getContext().getResources().getDrawable(R.drawable.listour);
        img.setBounds(0, 0, 100, 100);
        mBtnListTour.setCompoundDrawables(null, null, null, img);
    }

    public void onClickListTour(View view)
    {
        isListTour = true;
        isUserListTour = false;
        mTours.clear();
        getData();
        Drawable img = view.getContext().getResources().getDrawable(R.drawable.listtour1);
        img.setBounds(0, 0, 100, 100);
        mBtnListTour.setCompoundDrawables(null, null, null, img);

        Drawable img1 = view.getContext().getResources().getDrawable(R.drawable.yourlisttour);
        img1.setBounds(0, 0, 100, 100);
        mBtnYourListTour.setCompoundDrawables(null, null, null, img1);
    }

    public void OnClickNotifycation(View view)
    {
        view = findViewById(R.id.btn_notification);
        Drawable img2 = view.getContext().getResources().getDrawable(R.drawable.yourlisttour);
        img2.setBounds(0, 0, 100, 100);
        mBtnYourListTour.setCompoundDrawables(null, null, null, img2);

        Drawable img3 = view.getContext().getResources().getDrawable(R.drawable.listour);
        img3.setBounds(0, 0, 100, 100);
        mBtnListTour.setCompoundDrawables(null, null, null, img3);
        Intent intent = new Intent(ListTour.this, ActivityNotify.class);
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
                    Tour tour = (Tour) data.getSerializableExtra(Constants.EXTRA_TOUR);
                    Intent intent = new Intent(this, TourActivity.class);
                    intent.putExtra("tour", tour);
                    startActivity(intent);
                } else
                {
                    Log.e("ListTour", String.format("Failed to create new tour: %s", data.getStringExtra(Constants.EXTRA_ERROR)));
                    Toast.makeText(this, String.format("ListTour: %s", data.getStringExtra(Constants.EXTRA_ERROR)), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(isListTour)
            getData();
        else
            getDataUser();
    }
}
