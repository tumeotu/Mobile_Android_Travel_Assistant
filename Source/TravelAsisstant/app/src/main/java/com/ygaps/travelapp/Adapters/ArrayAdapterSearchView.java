package com.ygaps.travelapp.Adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;

public class ArrayAdapterSearchView extends SearchView
{

    private AutoCompleteTextView mSearchAutoComplete;

    public ArrayAdapterSearchView(Context context)
    {
        super(context);
        initialize();
    }

    public ArrayAdapterSearchView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public void initialize()
    {
        mSearchAutoComplete = findViewById(getResources().getIdentifier("android:id/search_src_text", null, null));
        setAutoCompleSuggestionsAdapter(null);
        setOnItemClickListener(null);
    }

    @Override
    public void setSuggestionsAdapter(CursorAdapter adapter)
    {
        throw new UnsupportedOperationException("Please use setAutoCompleSuggestionsAdapter(ArrayAdapter<?> adapter) instead");
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener)
    {
        mSearchAutoComplete.setOnItemClickListener(listener);
    }

    public void setAutoCompleSuggestionsAdapter(ArrayAdapter<?> adapter)
    {
        mSearchAutoComplete.setAdapter(adapter);
    }

    public void setText(String text)
    {
        mSearchAutoComplete.setText(text);
    }

}
