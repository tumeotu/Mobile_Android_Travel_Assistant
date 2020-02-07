package com.ygaps.travelapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.Adapters.ListCommentAdapter;
import com.ygaps.travelapp.Component.FeedbackList;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;
import com.ygaps.travelapp.Retrofit.ResponseFeedBackStopPoint;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListEvaluate extends Fragment
{

    List<FeedbackList> reviews;
    ListCommentAdapter adapter;
    ListView lvComment;

    View view;
    private int mServiceId = 1161;

    public ListEvaluate()
    {
    }

    public ListEvaluate(int serviceId)
    {
        mServiceId = serviceId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.list_evaluate_fragment, container, false);
        lvComment = view.findViewById(R.id.listViewReview);
        adapter = new ListCommentAdapter(getContext(), R.layout.row_comment, reviews);
        lvComment.setAdapter(adapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        reviews = new ArrayList<>();
        getFeedbackStopPoint();
    }

    public void getFeedbackStopPoint()
    {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        MyAPIClient user = retrofit.create(MyAPIClient.class);

        String token = getString(R.string.user_access_token);
        String tourId = getString(R.string.test_tour_id);

        //call api
        Call<ResponseFeedBackStopPoint> call = user.getListFeedbackStopPoint(token, mServiceId, 1, "5");
        call.enqueue(new Callback<ResponseFeedBackStopPoint>()
        {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ResponseFeedBackStopPoint> call, Response<ResponseFeedBackStopPoint> response)
            {
                if (response.isSuccessful())
                {
                    reviews.clear();
                    reviews.addAll(response.body().feedbackList);
                    adapter.notifyDataSetChanged();
                } else
                {
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ResponseFeedBackStopPoint> call, Throwable t)
            {
                Log.e("getFeedbackStopPoint", "Failed to receive feed back from service");
            }
        });

    }
}
