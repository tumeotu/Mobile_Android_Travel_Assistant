package com.ygaps.travelapp.Adapters;

import android.app.Service;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.ygaps.travelapp.Activities.ActivityNotify;
import com.ygaps.travelapp.Component.NotifyTour;
import com.ygaps.travelapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterNotify extends BaseAdapter
{
    private ActivityNotify context;
    private int layout;
    private List<NotifyTour> notifyTours;

    public AdapterNotify(ActivityNotify context, int layout, List<NotifyTour> notifyTours)
    {
        this.context = context;
        this.layout = layout;
        this.notifyTours = notifyTours;
    }

    @Override
    public int getCount()
    {
        return notifyTours.size();
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
        AdapterNotify.ViewHolder holder;
        if (view == null)
        {
            holder = new AdapterNotify.ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder.txtContentNofity = view.findViewById(R.id.ContentNotify);
            holder.txtTime = view.findViewById(R.id.TimeNotify);
            holder.Accept=view.findViewById(R.id.btn_Accept);
            holder.Deny=view.findViewById(R.id.btn_Deny);
            view.setTag(holder);
        } else
        {
            holder = (AdapterNotify.ViewHolder) view.getTag();
        }

        String content = " " + context.getResources().getString(R.string.invite) + " ";
        final NotifyTour tour = notifyTours.get(i);
        holder.txtContentNofity.setText(tour.hostName + content + tour.name);
        String startDate, endDate;
        DateFormat simple = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tour.createdOn);
        startDate = simple.format(calendar.getTime());
        holder.txtTime.setText(startDate);
        holder.Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.Accept(notifyTours.get(i).id);
            }
        });
        holder.Deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.Deny(notifyTours.get(i).id);
            }
        });
        return view;
    }

    private class ViewHolder
    {
        TextView txtContentNofity, txtTime;
        Button Accept,Deny;
    }

}
