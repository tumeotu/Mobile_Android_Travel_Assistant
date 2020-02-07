package com.ygaps.travelapp.Adapters;

import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.ygaps.travelapp.Component.FeedbackList;
import com.ygaps.travelapp.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ListCommentAdapter extends BaseAdapter
{
    private Context context;
    private int layout;
    private List<FeedbackList> commentList;

    public ListCommentAdapter(Context context, int layout, List<FeedbackList> reviews)
    {
        this.context = context;
        this.layout = layout;
        this.commentList = reviews;
    }

    @Override
    public int getCount()
    {
        return commentList.size();
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

    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
        }

        ViewHolder holder = new ListCommentAdapter.ViewHolder();
        holder.txtNameUserComment = view.findViewById(R.id.txt_nameUserComment);
        holder.comment = view.findViewById(R.id.comment);
        holder.ratingBar = view.findViewById(R.id.comment_rating_bar);
        holder.txtDataTimeComment = view.findViewById(R.id.datatimeComment);

        final FeedbackList reviews = commentList.get(i);
        holder.txtNameUserComment.setText(reviews.name);

        boolean emptyFeedback = isNullOrEmpty(reviews.feedback);
        boolean emptyReview = isNullOrEmpty(reviews.review);
        if (emptyFeedback && emptyReview)
            holder.comment.setText("");
        else if (emptyReview)
            holder.comment.setText(reviews.feedback);
        else
            holder.comment.setText(reviews.review);
        holder.ratingBar.setRating(reviews.point);

        // set date of comment
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(reviews.createdOn);
        String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
        holder.txtDataTimeComment.setText(date);

        //handle click image,
        return view;
    }

    boolean isNullOrEmpty(String s)
    {
        return s == null || s.isEmpty();
    }

    private class ViewHolder
    {
        TextView txtNameUserComment, comment;
        TextView txtDataTimeComment;
        RatingBar ratingBar;
    }
}
