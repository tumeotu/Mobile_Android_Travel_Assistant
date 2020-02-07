package com.ygaps.travelapp.Adapters;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.ygaps.travelapp.Activities.ActivityStoppoint;
import com.ygaps.travelapp.Activities.ListTour;
import com.ygaps.travelapp.Activities.TourDetailActivity;
import com.ygaps.travelapp.Component.StopPoint;
import com.ygaps.travelapp.Component.Tour;
import com.ygaps.travelapp.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AdapterStopPoint extends BaseAdapter
{
    private ActivityStoppoint context;
    private int layout;
    private List<StopPoint> noteList;
    private ArrayList<StopPoint> arrayList;

    public AdapterStopPoint(ActivityStoppoint context, int layout, List<StopPoint> alarmList)
    {
        this.context = context;
        this.layout = layout;
        this.noteList = alarmList;
        this.arrayList = new ArrayList<StopPoint>();
        this.arrayList.addAll(alarmList);
    }

    @Override
    public int getCount()
    {
        return noteList.size();
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override

    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        ViewHolder holder;
        if (view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder.txtLocation = view.findViewById(R.id.location_stoppoint);
            holder.txtTime = view.findViewById(R.id.time_stoppoint);
            holder.txtPeople = view.findViewById(R.id.number_people_stoppoint);
            holder.txtCost = view.findViewById(R.id.cost_stoppoint);
            view.setTag(holder);
        } else
        {
            holder = (ViewHolder) view.getTag();
        }
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("AdapterTour", String.format("%s is clicked", noteList.get(i).Id));
                context.DialogStopPointInfo(noteList.get(i));
            }
        });
        final StopPoint tour = noteList.get(i);
        holder.txtLocation.setText(tour.Name);
        String startDate, endDate;
        DateFormat simple = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tour.getArrivalAt());
        startDate = simple.format(calendar.getTime());
        calendar.setTimeInMillis(tour.getLeaveAt());
        endDate = simple.format(calendar.getTime());
        holder.txtTime.setText(String.format(Locale.getDefault(), "%s - %s", startDate, endDate));
        holder.txtPeople.setText(tour.Address);
        holder.txtCost.setText(String.format(Locale.getDefault(), "%d - %d", tour.MinCost.intValue(), tour.MaxCost.intValue()));

        return view;
    }
    public void filter(String charText)
    {
        Log.i(null, charText);
        charText = charText.toLowerCase(Locale.getDefault());
        noteList.clear();
        if (charText.length() == 0)
        {
            noteList.addAll(arrayList);
        } else
        {
            for (StopPoint wp : arrayList)
            {
                if(wp.Name!=null&&!wp.Name.isEmpty())
                {
                    if (wp.Name.toLowerCase(Locale.getDefault())
                            .contains(charText))
                    {
                        noteList.add(wp);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private class ViewHolder
    {
        TextView txtLocation, txtTime, txtPeople, txtCost;
    }

}
