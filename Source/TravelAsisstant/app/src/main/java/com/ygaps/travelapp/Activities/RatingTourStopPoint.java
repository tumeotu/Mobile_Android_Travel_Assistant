package com.ygaps.travelapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.Component.ReviewStopPoint;
import com.ygaps.travelapp.Component.ReviewTour;
import com.ygaps.travelapp.Component.User;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class RatingTourStopPoint extends AppCompatActivity
{
    private int Type;
    private int Id;
    private float Point;
    private String Name;
    private boolean successful = false;

    private String mToken;
    private RatingBar Rating;
    private EditText ContentRating;
    private TextView NamePlace;
    private TextView NameUser;
    private MyAPIClient mAPI;

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
        setContentView(R.layout.activity_review_stop_point);
        init();
        Bundle extras = getIntent().getExtras();
        Type = Integer.valueOf(extras.getString("Type"));
        Id = Integer.valueOf(extras.getString("Id"));
        Point = Float.valueOf(extras.getString("Point"));
        Name = extras.getString("Name");
        getData();
        Rating.setRating(Point);
        NamePlace.setText(Name);
    }

    public void init()
    {
        Rating = findViewById(R.id.YourRating);
        NamePlace = findViewById(R.id.NamePlaceRating);
        ContentRating = findViewById(R.id.ContentRating);
        NameUser = findViewById(R.id.NameUserRating);
        // init retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        mAPI = retrofit.create(MyAPIClient.class);

        //get token in local
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        mToken = sharedPreferences.getString("token", "");
    }

    public void getData()
    {
        //call api
        Call<User> call = mAPI.getUserInfo(mToken);
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
                        NameUser.setText(response.body().getFullName());
                    } else
                    {
                        Toast.makeText(RatingTourStopPoint.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    Toast.makeText(RatingTourStopPoint.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<User> call, Throwable t)
            {
                Toast.makeText(RatingTourStopPoint.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void OnClickPostRating(View view)
    {
        if (Type == 0)
        {
            ReviewTour reviewTour = new ReviewTour();
            reviewTour.TourId = Id;
            reviewTour.Point = Math.round(Rating.getRating());
            if(reviewTour.Point<=1)
            {
                reviewTour.Point=2;
            }
            reviewTour.Review = ContentRating.getText().toString();
            //call api
            Call<ResponseBody> call = mAPI.reviewTour(mToken, reviewTour);
            call.enqueue(new Callback<ResponseBody>()
            {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    if (response.isSuccessful())
                    {
                        new AlertDialog.Builder(RatingTourStopPoint.this)
                                .setIcon(android.R.drawable.ic_input_add)
                                .setTitle(getString(R.string.successful))
                                .setMessage(getString(R.string.message_response_send_successfully))
                                .show();
                        finish();
                    } else
                    {
                        Toast.makeText(RatingTourStopPoint.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Toast.makeText(RatingTourStopPoint.this, "error", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (Type == 1)
        {
            ReviewStopPoint reviewStopPoint = new ReviewStopPoint();
            reviewStopPoint.ServiceId = Id;
            reviewStopPoint.Point = Math.round(Rating.getRating());
            reviewStopPoint.Feedback = ContentRating.getText().toString();
            //call api
            Call<ResponseBody> call = mAPI.reviewStopPoint(mToken, reviewStopPoint);
            call.enqueue(new Callback<ResponseBody>()
            {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    if (response.isSuccessful())
                    {
                        new AlertDialog.Builder(RatingTourStopPoint.this)
                                .setIcon(android.R.drawable.ic_input_add)
                                .setTitle(getString(R.string.successful))
                                .setMessage(getString(R.string.message_response_send_successfully))
                                .show();
                        finish();
                    } else
                    {
                        Toast.makeText(RatingTourStopPoint.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Toast.makeText(RatingTourStopPoint.this, "error", Toast.LENGTH_SHORT).show();
                }
            });
        } else
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.message_error_happened))
                    .show();
        }
    }

    public void OnClickCancelRating(View view)
    {
        if (!successful)
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.message_confirm_cancel, getString(R.string.reviews)))
                    .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
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
