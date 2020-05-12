package com.example.himalaya.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.himalaya.utils.FragementCreator;

public class MainContentAdapter extends FragmentPagerAdapter {
    public MainContentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FragementCreator.getFragement(position);
    }

    @Override
    public int getCount() {
        return FragementCreator.Pager_count;
    }
}
