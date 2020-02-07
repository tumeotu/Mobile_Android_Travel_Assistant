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

import com.ygaps.travelapp.Activities.ListTour;
import com.ygaps.travelapp.Activities.TourDetailActivity;
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


public class AdapterTour extends BaseAdapter
{
    private ListTour context;
    private int layout;
    private List<Tour> noteList;
    private ArrayList<Tour> arrayList;

    public AdapterTour(ListTour context, int layout, List<Tour> alarmList)
    {
        this.context = context;
        this.layout = layout;
        this.noteList = alarmList;
        this.arrayList = new ArrayList<Tour>();
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
            holder.txtLocation = view.findViewById(R.id.location_tour);
            holder.txtTime = view.findViewById(R.id.time_tour);
            holder.txtPeople = view.findViewById(R.id.number_people_tour);
            holder.txtCost = view.findViewById(R.id.cost_tour);
            holder.image = view.findViewById(R.id.image_tour);
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
                context.TourDetail(noteList.get(i).Id);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                Log.i("AdapterTour", String.format("%s is long clicked", noteList.get(i).Id));
                context.DialogTourInfo(noteList.get(i));
                return true;
            }
        });

        final Tour tour = noteList.get(i);
        holder.txtLocation.setText(tour.Name);
        String startDate, endDate;
        DateFormat simple = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tour.StartDate);
        startDate = simple.format(calendar.getTime());
        calendar.setTimeInMillis(tour.EndDate);
        endDate = simple.format(calendar.getTime());

        holder.txtTime.setText(String.format(Locale.getDefault(), "%s - %s", startDate, endDate));
        holder.txtPeople.setText(String.format(Locale.getDefault(), "%d %s, %d %s", tour.Adults, context.getString(R.string.adults), tour.Children, context.getString(R.string.children)));
        holder.txtCost.setText(String.format(Locale.getDefault(), "%d - %d", tour.MinCost.intValue(), tour.MaxCost.intValue()));

        if (tour.Image != null)
        {
            URL url = null;
            try
            {
                url = new URL(tour.Avatar64.trim());
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                holder.image.setImageBitmap(bmp);
            }
            catch (MalformedURLException e)
            {
                Log.e("getView", String.format("MalformedURLException: %s", e.getMessage()));
            }
            catch (IOException e)
            {
                Log.e("getView", String.format("IOException: %s", e.getMessage()));
            }
        }
        //handle click image,
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
            for (Tour wp : arrayList)
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
        ImageView image;
    }

}
