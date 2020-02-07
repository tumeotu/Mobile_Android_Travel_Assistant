package com.ygaps.travelapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.ygaps.travelapp.Adapters.AdapterMembers;
import com.ygaps.travelapp.Adapters.ListCommentAdapter;
import com.ygaps.travelapp.Adapters.ViewPaperAdapter;
import com.ygaps.travelapp.Component.Comment;
import com.ygaps.travelapp.Component.Constants;
import com.ygaps.travelapp.Component.CoordinateMember;
import com.ygaps.travelapp.Component.FeedbackList;
import com.ygaps.travelapp.Component.NotiList;
import com.ygaps.travelapp.Component.RequestComment;
import com.ygaps.travelapp.Component.RequestLocation;
import com.ygaps.travelapp.Component.RequestNotifyOnRoad;
import com.ygaps.travelapp.Component.RequestSendText;
import com.ygaps.travelapp.Component.ResponNotifyOnRoad;
import com.ygaps.travelapp.Component.StopPoint;
import com.ygaps.travelapp.Component.Tour;
import com.ygaps.travelapp.Component.User;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;
import com.ygaps.travelapp.Retrofit.ResponseFeedBackStopPoint;
import com.ygaps.travelapp.Retrofit.ResponseFeedBackTour;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// classes needed to initialize map
// classes needed to add the location component
// classes needed to add a marker
// classes to calculate a route
// classes needed to launch navigation UI


public class TourDetailActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener
{
    // variables for adding location layer
    private static final String TAG = "DirectionsActivity";
    ListCommentAdapter AdapterComment;
    ListCommentAdapter AdapterReView;
    private String mToken;
    private String mTestTourId;
    private User account;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private Marker markerView;
    private List<Marker> markerList;
    private Point destinationPoint;
    private Point originPoint;
    private Tour tourInfo;
    private TextView NamebottomSheet;
    private List<FeedbackList> ListFeedbackTour;
    private List<FeedbackList> ListFeedbackStopPoint;
    private List<FeedbackList> ListCommentOfTour;
    private int Type = 0;
    private int Id = 0;
    private String Name;
    private boolean run = false;
    private TextView NamePlace;
    private MyAPIClient mAPI;
    private int mTotalReview, mNumberStar, mProcessFive = 0, mProcessFour = 0, mProcessThree = 0, mProcessTwo = 0, mProcessOne = 0;
    private int mStar;
    private StopPoint stopPoint;
    private boolean clickSetting = false;
    private Dialog mDialogAllUser;
    private Dialog mDialogRecordVoid;
    private Dialog mDialogSendText;
    private boolean isWaring = false;
    private Dialog mDialogWaring;
    private Button CancelWarning, AddWarning, CancelSendText, SendTextNotify;
    private RadioButton Speed, Police, Problem;
    private EditText ContentSpeed, ContentSendText;

    private int a = 0;

    private ListView ListViewAllUser;
    private AdapterMembers adapterMembers;
    private FloatingActionButton YourLocation;

    private List<NotiList> notiLists;
    private List<CoordinateMember> coordinateMemberList;
    private boolean mIsRecording = false;
    private boolean mIsPlaying = false;
    //component layout
    private TextView mName;
    private Button CancelStopPoint;
    private TextView mNamePlace;
    private int HeightActionBar;
    private LinearLayout Header;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayoutBSheet;
    private ToggleButton tbUpDown;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPaperAdapter viewPaperAdapter;
    private LinearLayout LayoutListCommentTour;
    private LinearLayout LayoutCommentOfUser;
    // component tour of stop Point information General
    private TextView TotalNumberRatingGeneral;
    private RatingBar TotalStarRatingGeneral;
    private TextView TotalReviewGeneral;
    private TextView NameTourStopPoint;
    private TextView DateTourStopPoint;
    private TextView PeopleTourStopPoint;
    private TextView CostTourStopPoint;
    private ProgressBar progressBarFiveStarGeneral;
    private ProgressBar progressBarFourStarGeneral;
    private ProgressBar progressBarThreeStarGeneral;
    private ProgressBar progressBarTwoStarGeneral;
    private ProgressBar progressBarOneStarGeneral;
    private ListView ListViewCommentTour;
    private EditText DetailComment;

    // component tour of stop Point information Review
    private Button SendComment;
    private ProgressBar progressBarFiveStarReview;
    private ProgressBar progressBarFourStarReview;
    private ProgressBar progressBarThreeStarReview;
    private ProgressBar progressBarTwoStarReview;
    private ProgressBar progressBarOneStarReview;
    private ListView ListViewReview;
    private TextView TotalNumberRatingReview;
    private RatingBar TotalStarRatingReview;
    private TextView TotalReviewReview;
    private RatingBar YourRating;
    private Button StartRecord, PlayReCord;

    private MediaRecorder mRecorder = new MediaRecorder();
    private MediaPlayer mPlayer = new MediaPlayer();
    private String mAudioPath;
    private boolean mAudioPermissionGranted = false;
    private boolean mAudioInitialized = false;
    private boolean mPlayerInitialized = false;

    //component setting tour
    private FloatingActionButton SettingTour, RecordVoid, AllMembers, SendText;

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

        mAudioPath = getApplicationInfo().dataDir + "/record";
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_login), MODE_PRIVATE);
        mToken = sharedPreferences.getString("token", "");

        mTestTourId = getString(R.string.test_tour_id);


        String tourId = getIntent().getStringExtra("TourId");
        if (tourId == null)
        {
            Log.e(TAG, "Missing TourId from intent's extra");
            finish();
        } else
        {
            tourInfo = new Tour();
            tourInfo.Id = tourId;
        }
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_tour_detail);
        init();
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void init()
    {
        // anh xa
        mapView = findViewById(R.id.mapView);
        this.linearLayoutBSheet = findViewById(R.id.bottomSheet);
        this.bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
        this.tbUpDown = findViewById(R.id.toggleButton);
        this.CancelStopPoint = findViewById(R.id.btn_Cancel2StopPoint);
        mNamePlace = findViewById(R.id.NamePlace);
        Header = findViewById(R.id.HeaderBottomSheet);
        NamebottomSheet = findViewById(R.id.NameInfor);
        NamePlace = findViewById(R.id.NamePlace);

        SettingTour = findViewById(R.id.SettingTour);
        SettingTour.setCustomSize(150);
        SettingTour.setImageDrawable(getDrawable(R.drawable.setting));

        YourLocation = findViewById(R.id.btnYouLocation);
        YourLocation.setCustomSize(152);
        YourLocation.setImageDrawable(getDrawable(R.drawable.yourlocation));


        RecordVoid = findViewById(R.id.btnReCordVoid);
        RecordVoid.setCustomSize(150);
        RecordVoid.setImageDrawable(getDrawable(R.drawable.record));

        AllMembers = findViewById(R.id.btnAllMember);
        AllMembers.setCustomSize(150);
        AllMembers.setImageDrawable(getDrawable(R.drawable.user));

        SendText = findViewById(R.id.btnSendText);
        SendText.setCustomSize(150);
        SendText.setImageDrawable(getDrawable(R.drawable.sentext));

        mDialogAllUser = new Dialog(this);
        mDialogAllUser.setContentView(R.layout.dialog_all_user);
        ListViewAllUser = mDialogAllUser.findViewById(R.id.ListMember);

        mDialogSendText = new Dialog(this);
        mDialogSendText.setContentView(R.layout.dialog_sendtext);
        CancelSendText = mDialogSendText.findViewById(R.id.btnCancelSendText);
        SendTextNotify = mDialogSendText.findViewById(R.id.btnSendTextNotify);
        ContentSendText = mDialogSendText.findViewById(R.id.txt_ContentSendText);

        mDialogRecordVoid = new Dialog(this);
        mDialogRecordVoid.setContentView(R.layout.dialog_record_voice);
        StartRecord = mDialogRecordVoid.findViewById(R.id.btn_startRecord);
        PlayReCord = mDialogRecordVoid.findViewById(R.id.btn_PlayRecord);


        mDialogWaring = new Dialog(this);
        mDialogWaring.setContentView(R.layout.dialog_warning);

        CancelWarning = mDialogWaring.findViewById(R.id.btnCanelWaring);
        AddWarning = mDialogWaring.findViewById(R.id.btnAddWaring);
        Speed = mDialogWaring.findViewById(R.id.rdi_Speed);
        Police = mDialogWaring.findViewById(R.id.rdi_Police);
        Problem = mDialogWaring.findViewById(R.id.rdi_Problem);
        ContentSpeed = mDialogWaring.findViewById(R.id.txt_ContentSpeed);

        notiLists = new ArrayList<NotiList>();
        coordinateMemberList = new ArrayList<>();
        markerList = new ArrayList<>();
        initFAB();
        // get height of actionbar
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            HeightActionBar = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        // set height for bottom sheet
        // create view for bottom sheet
        tabLayout = findViewById(R.id.tabEvaluate);
        viewPager = findViewById(R.id.viewPaper);
        viewPaperAdapter = new ViewPaperAdapter(getSupportFragmentManager());
        // add fragment
        viewPaperAdapter.AddFragment(new com.ygaps.travelapp.Activities.Evaluate(), "GENERAL");
        viewPaperAdapter.AddFragment(new ListEvaluate(), "REVIEWS");

        viewPager.setAdapter(viewPaperAdapter);
        tabLayout.setupWithViewPager(viewPager);

        ListFeedbackStopPoint = new ArrayList<>();
        ListFeedbackTour = new ArrayList<>();
        // init retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        mAPI = retrofit.create(MyAPIClient.class);
        //getFeedbackStopPoint();
    }

    public void initFAB()
    {
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 0);
        //SettingTour.setLayoutParams(params);
//
//        FrameLayout.LayoutParams params1 = new  FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//        );
//        params1.setMargins(0, 0, 10, 10);
//        AllMembers.setLayoutParams(params1);
//
//
//        FrameLayout.LayoutParams params2 = new  FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//        );
//        params2.setMargins(0, 0, 10, 20);
//        RecordVoid.setLayoutParams(params2);
//
//
//        FrameLayout.LayoutParams params3 = new  FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//        );
//        params3.setMargins(0, 0, 10, 30);
//        SendText.setLayoutParams(params3);

    }

    public void initComponentView()
    {
        View General = viewPager.getChildAt(0);
        LayoutListCommentTour = General.findViewById(R.id.LayoutListCommentTour);
        LayoutCommentOfUser = General.findViewById(R.id.LayoutCommentOfUser);

        NameTourStopPoint = General.findViewById(R.id.NameTourOrStopPoint);
        DateTourStopPoint = General.findViewById(R.id.TimeOfTourOrStopPoint);
        PeopleTourStopPoint = General.findViewById(R.id.NumberPeopleOfTourOrStopPoint);
        CostTourStopPoint = General.findViewById(R.id.CostOfTourOrStopPoint);
        TotalReviewGeneral = General.findViewById(R.id.TotalReviewsOfTourOrStopPointGeneral);
        TotalStarRatingGeneral = General.findViewById(R.id.TotalStarRatingOfTourOrStopPointGeneral);
        TotalNumberRatingGeneral = General.findViewById(R.id.TotalNumberRatingOfTourOrStopPointGeneral);

        progressBarFiveStarGeneral = General.findViewById(R.id.processBarFiveStarGeneral);
        progressBarFourStarGeneral = General.findViewById(R.id.processBarFourStarGeneral);
        progressBarThreeStarGeneral = General.findViewById(R.id.processBarThreeStarGeneral);
        progressBarTwoStarGeneral = General.findViewById(R.id.processBarTwoStarGeneral);
        progressBarOneStarGeneral = General.findViewById(R.id.processBarOneStarGeneral);
        ListViewCommentTour = General.findViewById(R.id.ListCommentTour);
        DetailComment = General.findViewById(R.id.DetailComment);
        SendComment = General.findViewById(R.id.sendComment);

        View Review = viewPager.getChildAt(1);
        TotalReviewReview = Review.findViewById(R.id.TotalReviewsOfTourOrStopPoint);
        TotalStarRatingReview = Review.findViewById(R.id.TotalStarRatingOfTourOrStopPoint);
        TotalNumberRatingReview = Review.findViewById(R.id.TotalNumberRatingOfTourOrStopPoint);

        progressBarFiveStarReview = Review.findViewById(R.id.processBarFiveStar);
        progressBarFourStarReview = Review.findViewById(R.id.processBarFourStar);
        progressBarThreeStarReview = Review.findViewById(R.id.processBarThreeStar);
        progressBarTwoStarReview = Review.findViewById(R.id.processBarTwoStar);
        progressBarOneStarReview = Review.findViewById(R.id.processBarOneStar);
        ListViewReview = Review.findViewById(R.id.listViewReview);
        SetHeightBottomSheet(1);
        YourRating = Review.findViewById(R.id.YourRating);
    }

    public void getData()
    {
        // *** muốn test thì thay mTourInfo.Id thành mTestTourId *** //
        Call<Tour> call = mAPI.getTourInfo(mToken, tourInfo.Id);
        call.enqueue(new Callback<Tour>()
        {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response)
            {
                if (response.isSuccessful())
                {
                    // chua lay duoc thong tin thanh vien trong tour
                    tourInfo = response.body();
                    if (tourInfo.StartDate == null) tourInfo.StartDate = 0L;
                    if (tourInfo.EndDate == null) tourInfo.EndDate = 0L;
                    DrawMarker();
                    ShowTourInfo();

                } else
                {
                    Toast.makeText(TourDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<Tour> call, Throwable t)
            {
                Toast.makeText(TourDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getListNotiOnRoad()
    {
        Call<ResponNotifyOnRoad> call = mAPI.getListNotifyOnRoad(mToken, tourInfo.Id, 1, "100");
        call.enqueue(new Callback<ResponNotifyOnRoad>()
        {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ResponNotifyOnRoad> call, Response<ResponNotifyOnRoad> response)
            {
                if (response.isSuccessful())
                {
                    notiLists = response.body().getNotiList();
                    DrawListWarning();
                } else
                {
                    Toast.makeText(TourDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ResponNotifyOnRoad> call, Throwable t)
            {
                Toast.makeText(TourDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getFeedbackStopPoint()
    {
        //call api
        Call<ResponseFeedBackStopPoint> call = mAPI.getListFeedbackStopPoint(mToken, stopPoint.Id, 1, "100");
        call.enqueue(new Callback<ResponseFeedBackStopPoint>()
        {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ResponseFeedBackStopPoint> call, Response<ResponseFeedBackStopPoint> response)
            {
                if (response.isSuccessful())
                {
                    ListFeedbackStopPoint.clear();
                    ListFeedbackStopPoint.addAll(response.body().feedbackList);
                    mTotalReview = ListFeedbackStopPoint.size();
                    AdapterReView.notifyDataSetChanged();
                    ShowStopPointInfo(stopPoint);
                } else
                {
                    Toast.makeText(TourDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull

            @Override
            public void onFailure(Call<ResponseFeedBackStopPoint> call, Throwable t)
            {
                Toast.makeText(TourDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getFeedbackTour()
    {
        //call api
        Call<ResponseFeedBackTour> call = mAPI.getListFeedbackTour(mToken, tourInfo.Id, 1, "100");
        call.enqueue(new Callback<ResponseFeedBackTour>()
        {
            @EverythingIsNonNull

            @Override
            public void onResponse(Call<ResponseFeedBackTour> call, Response<ResponseFeedBackTour> response)
            {
                if (response.isSuccessful())
                {
                    ListFeedbackTour.clear();
                    ListFeedbackTour.addAll(response.body().feedbackList);
                    AdapterReView.notifyDataSetChanged();
                    ShowTourInfo();
                } else
                {
                    Toast.makeText(TourDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ResponseFeedBackTour> call, Throwable t)
            {
                Toast.makeText(TourDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ShowTourInfo()
    {
        Type = 0;
        Id = Integer.valueOf(tourInfo.Id);
        Name = tourInfo.Name;
        mProcessFive = mProcessFour = mProcessThree = mProcessTwo = mProcessOne = 0;

        mTotalReview = ListFeedbackTour.size();
        float sum = 0;
        for (int i = 0; i < ListFeedbackTour.size(); i++)
        {
            sum += ListFeedbackTour.get(i).point;
        }
        mStar = Math.round(sum / mTotalReview);

        String startDate, endDate;
        DateFormat simple = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tourInfo.StartDate);
        startDate = simple.format(calendar.getTime());
        calendar.setTimeInMillis(tourInfo.EndDate);
        endDate = simple.format(calendar.getTime());

        NamePlace.setText(tourInfo.Name);
        NamebottomSheet.setText(getString(R.string.tour_information));
        NameTourStopPoint.setText(tourInfo.Name);
        DateTourStopPoint.setText(String.format(Locale.getDefault(), "%s - %s", startDate, endDate));
        PeopleTourStopPoint.setText(String.format(Locale.getDefault(), "%d %s, %d %s", tourInfo.Adults, getString(R.string.adults), tourInfo.Children, getString(R.string.children)));
        CostTourStopPoint.setText(String.format(Locale.getDefault(), "%d - %d", tourInfo.MinCost.intValue(), tourInfo.MaxCost.intValue()));

        TotalNumberRatingGeneral.setText(String.valueOf(mStar));
        TotalStarRatingGeneral.setRating(mStar);
        TotalReviewGeneral.setText(String.format(Locale.getDefault(), "%d %s", ListFeedbackTour.size(), getString(R.string.reviews)));

        TotalNumberRatingReview.setText(String.valueOf(mStar));
        TotalStarRatingReview.setRating(mStar);
        TotalReviewReview.setText(String.format(Locale.getDefault(), "%d %s", ListFeedbackTour.size(), getString(R.string.reviews)));

        for (int i = 0; i < ListFeedbackTour.size(); i++)
        {
            switch (ListFeedbackTour.get(i).point)
            {
                case 1:
                    mProcessOne++;
                    break;
                case 2:
                    mProcessTwo++;
                    break;
                case 3:
                    mProcessThree++;
                    break;
                case 4:
                    mProcessFour++;
                    break;
                case 5:
                    mProcessFive++;
                    break;
            }
        }

        if (ListFeedbackTour.size() > 0)
        {
            progressBarOneStarReview.setProgress(((mProcessOne * 100 / ListFeedbackTour.size())));
            progressBarOneStarGeneral.setProgress((mProcessOne * 100 / ListFeedbackTour.size()));

            progressBarTwoStarReview.setProgress((mProcessTwo * 100 / ListFeedbackTour.size()));
            progressBarTwoStarGeneral.setProgress((mProcessTwo * 100 / ListFeedbackTour.size()));

            progressBarThreeStarReview.setProgress((mProcessThree * 100 / ListFeedbackTour.size()));
            progressBarThreeStarGeneral.setProgress((mProcessThree * 100 / ListFeedbackTour.size()));

            progressBarFourStarReview.setProgress((mProcessFour * 100 / ListFeedbackTour.size()));
            progressBarFourStarGeneral.setProgress((mProcessFour * 100 / ListFeedbackTour.size()));

            progressBarFiveStarReview.setProgress((mProcessFive * 100 / ListFeedbackTour.size()));
            progressBarFiveStarGeneral.setProgress((mProcessFive * 100 / ListFeedbackTour.size()));
        }


        ListCommentOfTour = new ArrayList<>();
        for (int i = 0; i < tourInfo.Commenents.size(); i++)
        {
            ListCommentOfTour.add(new FeedbackList(tourInfo.Commenents.get(i)));
        }
        AdapterComment = new ListCommentAdapter(this, R.layout.row_comment, ListCommentOfTour);
        ListViewCommentTour.setAdapter(AdapterComment);
        AdapterReView = new ListCommentAdapter(this, R.layout.row_comment, ListFeedbackTour);
        ListViewReview.setAdapter(AdapterReView);
        viewPager.setCurrentItem(0);
    }

    public void ShowStopPointInfo(StopPoint stopPoint)
    {
        Type = 1;
        Id = stopPoint.Id;
        Name = stopPoint.Name;
        mProcessFive = mProcessFour = mProcessThree = mProcessTwo = mProcessOne = 0;

        float sum = 0;
        for (int i = 0; i < ListFeedbackStopPoint.size(); i++)
        {
            sum += ListFeedbackStopPoint.get(i).point;
        }
        mStar = Math.round(sum / mTotalReview);

        String startDate, endDate;
        DateFormat simple = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(stopPoint.ArrivalAt);
        startDate = simple.format(calendar.getTime());
        calendar.setTimeInMillis(stopPoint.LeaveAt);
        endDate = simple.format(calendar.getTime());

        //set data General page
        NamePlace.setText(stopPoint.Name);
        NamebottomSheet.setText(getString(R.string.stop_point_information));
        NameTourStopPoint.setText(tourInfo.Name);
        DateTourStopPoint.setText(String.format(Locale.US, "%s - %s", startDate, endDate));
        PeopleTourStopPoint.setText(stopPoint.Address);
        CostTourStopPoint.setText(String.format(Locale.US, "%d - %d", stopPoint.MinCost.intValue(), stopPoint.MaxCost.intValue()));

        TotalNumberRatingGeneral.setText(String.valueOf(mStar));
        TotalStarRatingGeneral.setRating(mStar);
        TotalReviewGeneral.setText(String.format(Locale.getDefault(), "%d %s", ListFeedbackStopPoint.size(), getString(R.string.reviews)));
        TotalNumberRatingReview.setText(String.valueOf(mStar));
        TotalStarRatingReview.setRating(mStar);
        TotalReviewReview.setText(String.format(Locale.getDefault(), "%d %s", ListFeedbackStopPoint.size(), getString(R.string.reviews)));

        for (int i = 0; i < ListFeedbackStopPoint.size(); i++)
        {
            switch (ListFeedbackStopPoint.get(i).point)
            {
                case 1:
                    mProcessOne++;
                    break;
                case 2:
                    mProcessTwo++;
                    break;
                case 3:
                    mProcessThree++;
                    break;
                case 4:
                    mProcessFour++;
                    break;
                case 5:
                    mProcessFive++;
                    break;
            }
        }

        if (ListFeedbackStopPoint.size() > 0)
        {
            progressBarOneStarReview.setProgress(((mProcessOne * 100 / ListFeedbackStopPoint.size())));
            progressBarOneStarGeneral.setProgress((mProcessOne * 100 / ListFeedbackStopPoint.size()));

            progressBarTwoStarReview.setProgress((mProcessTwo * 100 / ListFeedbackStopPoint.size()));
            progressBarTwoStarGeneral.setProgress((mProcessTwo * 100 / ListFeedbackStopPoint.size()));

            progressBarThreeStarReview.setProgress((mProcessThree * 100 / ListFeedbackStopPoint.size()));
            progressBarThreeStarGeneral.setProgress((mProcessThree * 100 / ListFeedbackStopPoint.size()));

            progressBarFourStarReview.setProgress((mProcessFour * 100 / ListFeedbackStopPoint.size()));
            progressBarFourStarGeneral.setProgress((mProcessFour * 100 / ListFeedbackStopPoint.size()));

            progressBarFiveStarReview.setProgress((mProcessFive * 100 / ListFeedbackStopPoint.size()));
            progressBarFiveStarGeneral.setProgress((mProcessFive * 100 / ListFeedbackStopPoint.size()));
        }

        //set data Review page
        AdapterReView = new ListCommentAdapter(this, R.layout.row_comment, ListFeedbackStopPoint);
        ListViewReview.setAdapter(AdapterReView);
        viewPager.setCurrentItem(0);
    }

    public void DrawListWarning()
    {
        if (notiLists != null || !notiLists.isEmpty())
        {
            for (int i = 0; i < notiLists.size(); i++)
            {
                DrawWarning(notiLists.get(i));
            }
        } else
            return;
    }

    public void DrawListMember()
    {
        if (coordinateMemberList == null || coordinateMemberList.isEmpty())
            return;
        String title = "";
        // draw list member
        for (int i = 0; i < coordinateMemberList.size(); i++)
        {
            //title=coordinateMemberList.get(i).NameUser;
            markerView = mapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(coordinateMemberList.get(i).lat, coordinateMemberList.get(i)._long))
                    .setIcon(IconFactory.getInstance(this).fromResource(R.drawable.user1)));
            markerList.add(markerView);
        }
    }

    public void DeleteListMember()
    {
        if (coordinateMemberList == null || coordinateMemberList.isEmpty())
            return;
        // draw list member
        for (int i = 0; i < markerList.size(); i++)
        {
            mapboxMap.removeMarker(markerList.get(i));
        }
        markerList.clear();
    }

    public void DrawMarker()
    {
        if (tourInfo.StopPoints.isEmpty()) return;
        for (int i = 0; i < tourInfo.StopPoints.size(); i++)
        {
            try
            {

                int icon = 0;
                if (tourInfo.StopPoints.get(i).ServiceTypeId == 1)
                {
                    icon = R.drawable.restaurant;
                } else if (tourInfo.StopPoints.get(i).ServiceTypeId == 2)
                {
                    icon = R.drawable.hotel;
                } else if (tourInfo.StopPoints.get(i).ServiceTypeId == 3)
                {
                    icon = R.drawable.rest_station;
                } else if (tourInfo.StopPoints.get(i).ServiceTypeId == 4)
                {
                    icon = R.drawable.red_marker;
                }
                markerView = mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(tourInfo.StopPoints.get(i).getLat(), tourInfo.StopPoints.get(i).getLong()))
                        .setIcon(IconFactory.getInstance(this).fromResource(icon)));

            }
            catch (Exception e)
            {

            }
        }
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(tourInfo.StopPoints.get(0).getLat(), tourInfo.StopPoints.get(0).getLong())) // Sets the new camera position
                .zoom(17) // Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 7000);
    }

    public void SetHeightBottomSheet(final int value)
    {
        //set height for bottom sheet
        final CoordinatorLayout inner = findViewById(R.id.activityTourDetail);
        inner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                inner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //View hidden = inner.getChildAt(2);
                bottomSheetBehavior.setPeekHeight(HeightActionBar * 2);
            }
        });
        //set height for header of bottom sheet
        LinearLayout layout = findViewById(R.id.HeaderBottomSheet);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.height = HeightActionBar * 2;
        layout.setLayoutParams(params);

        //set height for header of header of bottom sheet


        LinearLayout HeaderOfHeader = findViewById(R.id.bodyOfHeader);
        LinearLayout.LayoutParams paramsHeaderOfHeader = (LinearLayout.LayoutParams)
                HeaderOfHeader.getLayoutParams();
        LinearLayout FooterOfHeader = findViewById(R.id.FooterOfHeader);
        LinearLayout.LayoutParams paramsFooterOofHeader = (LinearLayout.LayoutParams)
                FooterOfHeader.getLayoutParams();

        LinearLayout.LayoutParams paramsLayoutListComment = (LinearLayout.LayoutParams)
                LayoutListCommentTour.getLayoutParams();

        LinearLayout.LayoutParams paramsLayoutComment = (LinearLayout.LayoutParams)
                LayoutCommentOfUser.getLayoutParams();
        if (value == 1)
        {
            paramsLayoutListComment.weight = 8.0f;
            paramsLayoutComment.weight = 2.0f;
            paramsHeaderOfHeader.weight = 0.0f;
            paramsFooterOofHeader.weight = 1.0f;
        } else
        {
            paramsLayoutListComment.weight = 0.0f;
            paramsLayoutComment.weight = 0.0f;
            paramsHeaderOfHeader.weight = 1.0f;
            paramsFooterOofHeader.weight = 0.0f;
        }
        LayoutCommentOfUser.setLayoutParams(paramsLayoutComment);
        LayoutListCommentTour.setLayoutParams(paramsLayoutListComment);
        HeaderOfHeader.setLayoutParams(paramsHeaderOfHeader);
        FooterOfHeader.setLayoutParams(paramsFooterOofHeader);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap)
    {
        this.mapboxMap = mapboxMap;
        run = true;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded()
        {
            @Override
            public void onStyleLoaded(@NonNull Style style)
            {
                getData();
                getDataUser();
                getListNotiOnRoad();
                getFeedbackTour();
                enableLocationComponent(style);
                initComponentView();
                ShowTourInfo();
                mapboxMap.addOnMapClickListener(TourDetailActivity.this);
                YourRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
                {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
                    {
                        if (rating != 0)
                        {
                            Intent mainIntent = new Intent(TourDetailActivity.this, RatingTourStopPoint.class);
                            Bundle dataBundle = new Bundle();
                            dataBundle.putString("Type", String.valueOf(Type));
                            dataBundle.putString("Id", String.valueOf(Id));
                            dataBundle.putString("Name", Name);
                            dataBundle.putString("Point", String.valueOf(rating));
                            mainIntent.putExtras(dataBundle);
                            startActivity(mainIntent);
                        }
                    }
                });
                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener()
                {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker)
                    {
                        for (int i = 0; i < tourInfo.StopPoints.size(); i++)
                        {
                            if (tourInfo.StopPoints.get(i).getLat() == marker.getPosition().getLatitude() &&
                                    tourInfo.StopPoints.get(i).getLong() == marker.getPosition().getLongitude())
                            {
                                CameraPosition position = new CameraPosition.Builder()
                                        .target(new LatLng(tourInfo.StopPoints.get(i).getLat(), tourInfo.StopPoints.get(i).getLong())) // Sets the new camera position
                                        .zoom(17) // Sets the zoom
                                        .bearing(180) // Rotate the camera
                                        .tilt(30) // Set the camera tilt
                                        .build(); // Creates a CameraPosition from the builder
                                stopPoint = tourInfo.StopPoints.get(i);
                                getFeedbackStopPoint();
                                mapboxMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(position), 7000);
                                ShowStopPointInfo(tourInfo.StopPoints.get(i));
                                // load Review cua stoppoint
                                SetHeightBottomSheet(2);
                                // show stop Point info
                                break;
                            }
                        }
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                                CancelStopPoint.getLayoutParams();
                        params.weight = 2.0f;
                        CancelStopPoint.setLayoutParams(params);
                        return false;
                    }
                });
                tbUpDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                        if (isChecked)
                        {
                            // keo len
                            YourLocation.hide();
                            SettingTour.hide();
                            tbUpDown.setBackgroundDrawable(TourDetailActivity.this.getDrawable(R.drawable.up));
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else
                        {
                            // ha xuong
                            YourLocation.show();
                            SettingTour.show();
                            tbUpDown.setBackgroundDrawable(TourDetailActivity.this.getDrawable(R.drawable.down));
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }
                });
                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
                {
                    @Override
                    public void onStateChanged(View view, int newState)
                    {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED)
                        {

                            tbUpDown.setChecked(true);
                        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                        {
                            tbUpDown.setChecked(false);
                        }
                    }

                    @Override
                    public void onSlide(View view, float v)
                    {
                    }
                });
            }
        });
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point)
    {

        if (isWaring)
        {
            HandleWarning(point);
        }

//        if(destinationPoint!=null)
//            markerView.remove(

//        markerView = mapboxMap.addMarker(new MarkerOptions()
//                .position(Point)
//                .title("Eiffel Tower "+i)
//                .setIcon(IconFactory.getInstance(this).fromResource(R.drawable.rest_station)));
//        i++;
//
//        destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
//        originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
//                locationComponent.getLastKnownLocation().getLatitude());
//        getRoute(originPoint, destinationPoint);
        return true;
    }

    public void HandleWarning(@NonNull final LatLng point)
    {
        hideSetting();
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        mDialogWaring.getWindow().setLayout((int) (displayRectangle.width() * 0.95f), (int) (displayRectangle.height() * 0.8f));
        mDialogWaring.setCanceledOnTouchOutside(false);
        mDialogWaring.show();
        Speed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (!isChecked)
                {
                    ContentSpeed.setVisibility(View.INVISIBLE);
                } else
                {
                    ContentSpeed.setVisibility(View.VISIBLE);
                }
            }
        });
        AddWarning.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                final RequestNotifyOnRoad notifyOnRoad = new RequestNotifyOnRoad();
                notifyOnRoad.userId = account.id;
                notifyOnRoad.tourId = Integer.valueOf(tourInfo.Id);
                if (Speed.isChecked())
                {
                    notifyOnRoad.notificationType = 3;
                } else if (Police.isChecked())
                {
                    notifyOnRoad.notificationType = 1;
                } else
                    notifyOnRoad.notificationType = 2;
                notifyOnRoad.lat = point.getLatitude();
                notifyOnRoad._long = point.getLongitude();
                notifyOnRoad.speed = Integer.valueOf(ContentSpeed.getText().toString());

                if (!Police.isChecked() &&
                        !Problem.isChecked() && !Speed.isChecked())
                {
                    Toast.makeText(TourDetailActivity.this, R.string.message_fill_information, Toast.LENGTH_SHORT).show();
                } else if (Speed.isChecked())
                {
                    if (ContentSpeed.getText().toString().isEmpty())
                    {
                        Toast.makeText(TourDetailActivity.this, R.string.message_fill_information, Toast.LENGTH_SHORT).show();
                    } else
                    {
                        sendNotifyOnRoad(notifyOnRoad);
                    }
                } else
                {
                    sendNotifyOnRoad(notifyOnRoad);
                }
            }
        });
        CancelWarning.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mDialogWaring.dismiss();
                isWaring = false;
            }
        });
    }

    public void sendNotifyOnRoad(final RequestNotifyOnRoad notifyOnRoad)
    {
        isWaring = false;
        mDialogWaring.dismiss();
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        //call api
        Call<ResponseBody> call = mAPI.sendNotifyOnRoad(token, notifyOnRoad);
        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            @retrofit2.internal.EverythingIsNonNull
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        NotiList noti = new NotiList();
                        noti.setLat(notifyOnRoad.lat);
                        noti.setLong(notifyOnRoad._long);
                        noti.setNotificationType(notifyOnRoad.notificationType);
                        noti.setSpeed(notifyOnRoad.speed);
                        DrawWarning(noti);
                        new AlertDialog.Builder(TourDetailActivity.this)
                                .setTitle(getString(R.string.successful))
                                .show();

                    } else
                    {
                        Toast.makeText(TourDetailActivity.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    Toast.makeText(TourDetailActivity.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            @retrofit2.internal.EverythingIsNonNull
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Toast.makeText(TourDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DrawWarning(NotiList notiList)
    {
        if (notiList == null)
            return;
        int icon = 0;
        String title = "";
        if (notiList.getNotificationType() == 1)
        {
            icon = R.drawable.police;
        } else if (notiList.getNotificationType() == 2)
        {
            icon = R.drawable.problem;
        } else if (notiList.getNotificationType() == 3)
        {
            icon = R.drawable.limited_speed;
            title = String.valueOf(notiList.getSpeed());
        }
        markerView = mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(notiList.getLat(), notiList.getLong()))
                .title(title)
                .setIcon(IconFactory.getInstance(this).fromResource(icon)));
    }


    public void OnClickSettingTour(View view)
    {
        view = findViewById(R.id.SettingTour);

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
        AllMembers.show();
        SendText.show();
        RecordVoid.show();

    }

    public void hideSetting()
    {
        AllMembers.hide();
        SendText.hide();
        RecordVoid.hide();
    }

    public void OnClickAllUser(View view)
    {
        view = findViewById(R.id.btnAllMember);
        hideSetting();
        adapterMembers = new AdapterMembers(this, R.layout.row_members, tourInfo.Members);
        ListViewAllUser.setAdapter(adapterMembers);

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        mDialogAllUser.getWindow().setLayout((int) (displayRectangle.width() * 0.95f), (int) (displayRectangle.height() * 0.8f));

        mDialogAllUser.setCanceledOnTouchOutside(true);
        mDialogAllUser.show();
    }

    public void OnClickReCordVoice(View view)
    {
        view = findViewById(R.id.btnReCordVoid);
        hideSetting();

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        mDialogRecordVoid.getWindow().setLayout((int) (displayRectangle.width() * 0.95f), (int) (displayRectangle.height() * 0.4f));

        mDialogRecordVoid.setCanceledOnTouchOutside(true);
        mDialogRecordVoid.show();

        StartRecord.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAudioPermissionGranted = checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
                if (!mAudioPermissionGranted)
                {
                    String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
                    requestPermissions(permissions, Constants.REQUEST_AUDIO_RECORD);
                } else
                {
                    if (!mAudioInitialized) onAudioPermissionGranted();

                    if (mAudioInitialized)
                    {
                        mIsRecording = !mIsRecording;
                        if (mIsRecording)
                        {
                            Log.i("OnClickReCordVoice", "start recording");
                            StartRecord.setText(getString(R.string.stop));
                            mRecorder.start();
                        } else
                        {
                            Log.i("OnClickReCordVoice", "stop recording");
                            StartRecord.setText(getString(R.string.record));
                            mRecorder.stop();
                            mRecorder.reset();
                            mAudioInitialized = false;
                        }
                    }

                }
            }
        });
        PlayReCord.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!mPlayerInitialized)
                {
                    try
                    {
                        mPlayer.setDataSource(mAudioPath);
                        mPlayer.prepare();
                        mPlayerInitialized = true;
                    }
                    catch (Exception e)
                    {
                        Log.e("OnClickReCordVoice", e.getMessage());
                        mPlayerInitialized = false;
                    }
                }

                if (mPlayerInitialized)
                {
                    mIsPlaying = !mIsPlaying;
                    if (mIsPlaying)
                    {
                        Log.i("OnClickReCordVoice", "start playing record");
                        PlayReCord.setText(getString(R.string.stop));
                        mPlayer.start();
                    } else
                    {
                        Log.i("OnClickReCordVoice", "stop playing record");
                        PlayReCord.setText(getString(R.string.play));
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayerInitialized = false;
                    }
                }

            }
        });
    }


    public void OnClickSendText(View view)
    {
        view = findViewById(R.id.btnSendText);

        hideSetting();
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        mDialogSendText.getWindow().setLayout((int) (displayRectangle.width() * 0.95f), (int) (displayRectangle.height() * 0.8f));
        mDialogSendText.setCanceledOnTouchOutside(false);
        mDialogSendText.show();
        CancelSendText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mDialogSendText.dismiss();
            }
        });
        SendTextNotify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!ContentSendText.getText().toString().isEmpty())
                {
                    RequestSendText requestSendText = new RequestSendText();
                    requestSendText.userId = account.id;
                    requestSendText.tourId = Integer.valueOf(tourInfo.Id);
                    requestSendText.noti = ContentSendText.getText().toString();
                    SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
                    String token = sharedPreferences.getString("token", "");
                    //call api
                    Call<ResponseBody> call = mAPI.sendNotifyToATour(token, requestSendText);
                    call.enqueue(new Callback<ResponseBody>()
                    {
                        @Override
                        @retrofit2.internal.EverythingIsNonNull
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                        {
                            if (response.isSuccessful())
                            {
                                if (response.body() != null)
                                {
                                    mDialogSendText.dismiss();
                                    new AlertDialog.Builder(TourDetailActivity.this)
                                            .setTitle(getString(R.string.successful))
                                            .show();

                                } else
                                {
                                    Toast.makeText(TourDetailActivity.this, "Empty body", Toast.LENGTH_LONG).show();
                                }
                            } else
                            {
                                Toast.makeText(TourDetailActivity.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        @retrofit2.internal.EverythingIsNonNull
                        public void onFailure(Call<ResponseBody> call, Throwable t)
                        {
                            Toast.makeText(TourDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                {
                    Toast.makeText(TourDetailActivity.this, R.string.message_fill_information, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void OnClickGetRouterStopPoint(View view)
    {
        view = findViewById(R.id.btn_Router);
        destinationPoint = Point.fromLngLat(stopPoint.getLong(), stopPoint.getLat());
        getRoute(originPoint, destinationPoint);
    }

    public void OnClickMoveStopPoint(View view)
    {
        view = findViewById(R.id.btn_Move);
        if (currentRoute == null)
        {
            destinationPoint = Point.fromLngLat(stopPoint.getLong(), stopPoint.getLat());
            getRoute(originPoint, destinationPoint);
        }
        boolean simulateRoute = false;
        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(currentRoute)
                .shouldSimulateRoute(simulateRoute)
                .build();
        // Call this method with Context from within an Activity
        NavigationLauncher.startNavigation(TourDetailActivity.this, options);
    }

    public void OnClickWarning(View view)
    {
        isWaring = true;
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.Warning))
                .setMessage(getString(R.string.choose_location_warning))
                .show();
    }

    public void OnClickCancel2StopPoint(View view)
    {
        // set view cho tour
        ShowTourInfo();
        // thu nho layout
        SetHeightBottomSheet(1);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                CancelStopPoint.getLayoutParams();
        params.weight = 0.0f;
        CancelStopPoint.setLayoutParams(params);
    }

    public void OnClickYourLocation(View view)
    {

        originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(originPoint.latitude(), originPoint.longitude())) // Sets the new camera position
                .zoom(17) // Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 7000);
    }

    public void OnClickSenComment(View view)
    {
        View a = viewPager.getChildAt(0);
        view = a.findViewById(R.id.sendComment);

        EditText DetailComment;
        DetailComment = a.findViewById(R.id.DetailComment);
        final String Detail = DetailComment.getText().toString();
        if (!Detail.isEmpty())
        {
            RequestComment requestComment = new RequestComment();
            requestComment.comment = Detail;
            requestComment.tourId = tourInfo.Id;
            requestComment.userId = account.id;
            //call api
            Call<ResponseBody> call = mAPI.sendCommetTour(mToken, requestComment);
            call.enqueue(new Callback<ResponseBody>()
            {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    if (response.isSuccessful())
                    {
                        ListCommentOfTour.add(new FeedbackList(new Comment(account.fullName, Detail)));
                        AdapterComment.notifyDataSetChanged();
                        Toast.makeText(TourDetailActivity.this, getString(R.string.successful), Toast.LENGTH_SHORT).show();
                    } else
                    {
                        new AlertDialog.Builder(TourDetailActivity.this)
                                .setMessage(response.message())
                                .show();
                        //Toast.makeText(TourDetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Toast.makeText(TourDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        DetailComment.setText("");
    }


    public void OnClickDeleteTour(View view)
    {
        // xoa luon tour thoat man hinh
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.confirm))
                .setMessage(getString(R.string.message_confirm_delete))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        tourInfo.Status = Constants.STATUS_TOUR_CANCELED;
                        Call<ResponseBody> call = mAPI.updateTourInfo(mToken, tourInfo);
                        call.enqueue(new Callback<ResponseBody>()
                        {
                            @EverythingIsNonNull
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                            {
                                if (response.isSuccessful())
                                {
                                    finish();
                                } else
                                {
                                    new AlertDialog.Builder(TourDetailActivity.this)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle(getString(R.string.error))
                                            .setMessage(getString(R.string.message_update_error))
                                            .show();
                                }
                            }

                            @EverythingIsNonNull
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t)
                            {
                                new AlertDialog.Builder(TourDetailActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(getString(R.string.error))
                                        .setMessage(getString(R.string.message_update_error))
                                        .show();
                            }
                        });
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                })
                .show();

    }

    public void OnClickEditTourMapbox(View view)
    {
        Type = 3;
        Intent intent = new Intent(this, TourActivity.class);
        intent.putExtra(Constants.EXTRA_TOUR, tourInfo);
        startActivity(intent);
    }

    public void getDataUser()
    {
        //get token in local
        //call api
        Call<User> call = mAPI.getUserInfo(mToken);
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
                        Toast.makeText(TourDetailActivity.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    Toast.makeText(TourDetailActivity.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            @retrofit2.internal.EverythingIsNonNull
            public void onFailure(Call<User> call, Throwable t)
            {
                Toast.makeText(TourDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRoute(Point origin, Point destination)
    {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>()
                {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response)
                    {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null)
                        {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1)
                        {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null)
                        {
                            navigationMapRoute.removeRoute();
                        } else
                        {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable)
                    {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    private void UpdateLocation(Point point)
    {
        RequestLocation requestLocation = new RequestLocation();
        requestLocation.userId = account.id;
        requestLocation.tourId = Integer.valueOf(tourInfo.Id);
        requestLocation._long = point.longitude();
        requestLocation.lat = point.latitude();
        Call<List<CoordinateMember>> call = mAPI.updateUserLocation(mToken, requestLocation);
        call.enqueue(new Callback<List<CoordinateMember>>()
        {
            @Override
            @retrofit2.internal.EverythingIsNonNull
            public void onResponse(Call<List<CoordinateMember>> call, Response<List<CoordinateMember>> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        coordinateMemberList = response.body();
                    } else
                    {
                        Toast.makeText(TourDetailActivity.this, "Empty body", Toast.LENGTH_LONG).show();
                    }
                } else
                {
                    Toast.makeText(TourDetailActivity.this, "Failed to get list of tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            @retrofit2.internal.EverythingIsNonNull
            public void onFailure(Call<List<CoordinateMember>> call, Throwable t)
            {
                Toast.makeText(TourDetailActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle)
    {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this))
        {

            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // set location request
            LocationEngineRequest.Builder builder = new LocationEngineRequest.Builder(10 * 1000);
            builder.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            builder.setFastestInterval(10 * 1000);

            LocationEngineCallback<LocationEngineResult> callback = new LocationEngineCallback<LocationEngineResult>()
            {
                @Override
                public void onSuccess(LocationEngineResult result)
                {
                    Location location = result.getLastLocation();
                    if (location != null)
                    {
                        Log.i("Mapbox UpdateLocation", "Received new location");
                        originPoint = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                        DeleteListMember();
                        UpdateLocation(originPoint);
                        DrawListMember();
                    }
                }

                @Override
                public void onFailure(@NonNull Exception exception)
                {

                }
            };

            LocationEngine engine = LocationEngineProvider.getBestLocationEngine(this);
            engine.requestLocationUpdates(builder.build(), callback, Looper.getMainLooper());

            locationComponent.setLocationEngine(engine);
        } else
        {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    public void onAudioPermissionGranted()
    {
        // MediaRecorder
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mAudioPath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try
        {
//            mPlayer.setDataSource(mAudioPath);
            mRecorder.prepare();
            mAudioInitialized = true;
        }
        catch (IOException ignored)
        {
            Log.e("onAudioPermissionGranted", "Failed to prepare recorder: " + ignored.getMessage());
            mAudioInitialized = false;
        }
        catch (Exception e)
        {
            Log.e("onAudioPermissionGranted", e.getMessage());
            mAudioInitialized = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode == Constants.REQUEST_AUDIO_RECORD)
        {
            mAudioPermissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        } else
            permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain)
    {
        Toast.makeText(this, R.string.message_request_user_grant_permission, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted)
    {
        if (granted)
        {
            enableLocationComponent(mapboxMap.getStyle());
        } else
        {
            Toast.makeText(this, R.string.message_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (run)
        {
            if (Type == 1)
            {
                getFeedbackStopPoint();
            } else if (Type == 3)
            {
                getData();
            } else
            {
                getFeedbackTour();
            }
            YourRating.setRating(0);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        mapView.onResume();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
        mRecorder.release();
        mPlayer.release();
    }


    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
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
