package com.ygaps.travelapp.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPaperAdapter extends FragmentPagerAdapter
{
    private final List<Fragment> list = new ArrayList<>();

    private final List<String> listTitles = new ArrayList<>();

    public ViewPaperAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public int getCount()
    {
        return listTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return listTitles.get(position);
    }

    public void AddFragment(Fragment fragment, String title)
    {
        list.add(fragment);
        listTitles.add(title);
    }
}
