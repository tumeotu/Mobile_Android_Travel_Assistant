package com.ygaps.travelapp.Adapters;

import android.app.Service;
import android.content.Context;
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
import com.ygaps.travelapp.Component.Member;
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


public class AdapterMembers extends BaseAdapter
{
    private Context context;
    private int layout;
    private List<Member> members;

    public AdapterMembers(Context context, int layout, List<Member> members)
    {
        this.context = context;
        this.layout = layout;
        this.members = members;
    }

    @Override
    public int getCount()
    {
        return members.size();
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
            holder.txtUserName = view.findViewById(R.id.NameOfUser);
            holder.txtID = view.findViewById(R.id.IdOfUser);
            holder.txtPhone = view.findViewById(R.id.PhoneOfUser);
            view.setTag(holder);
        } else
        {
            holder = (ViewHolder) view.getTag();
        }

        final Member tour = members.get(i);
        holder.txtUserName.setText(": "+tour.name);
        holder.txtID.setText(": "+tour.id);
        holder.txtPhone.setText(": "+tour.phone);
        //handle click image,
        return view;
    }

    private class ViewHolder
    {
        TextView txtUserName, txtID, txtPhone;
    }

}
