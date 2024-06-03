package com.kuro.yema.adapters;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kuro.yema.utils.TabItem;
import com.kuro.yema.views.fragments.main.home.HomesContentFragment;

import java.util.ArrayList;

public class HomeViewPageAdapter extends FragmentStateAdapter {

    private final ArrayList<TabItem> mTabType;
    private NavController mNavController;
    private LinearLayout mNavLayout;

    public HomeViewPageAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<TabItem> tabType, NavController navController, LinearLayout navLayout) {
        super(fragmentActivity);

        mTabType = tabType;
        mNavController = navController;
        mNavLayout = navLayout;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new HomesContentFragment(mTabType.get(position), mNavController, mNavLayout);
    }

    @Override
    public int getItemCount() {
        return mTabType.size();
    }


}
