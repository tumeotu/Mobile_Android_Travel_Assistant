package com.ygaps.travelapp.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ygaps.travelapp.Adapters.ViewPaperAdapter;
import com.ygaps.travelapp.R;

public class EvaluateTrip extends AppCompatActivity
{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPaperAdapter viewPaperAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_trip);

        tabLayout = findViewById(R.id.tabEvaluate);
        viewPager = findViewById(R.id.viewPaper);
        viewPaperAdapter = new ViewPaperAdapter(getSupportFragmentManager());
        // add fragment

        viewPaperAdapter.AddFragment(new com.ygaps.travelapp.Activities.Evaluate(), "GENERAL");
        viewPaperAdapter.AddFragment(new ListEvaluate(), "REVIEWS");

        viewPager.setAdapter(viewPaperAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }


}
