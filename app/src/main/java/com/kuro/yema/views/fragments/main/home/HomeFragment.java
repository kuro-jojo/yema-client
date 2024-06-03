package com.kuro.yema.views.fragments.main.home;

import static com.kuro.androidutils.sender.Sender.openWhatsapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yema.BuildConfig;
import com.kuro.yema.R;
import com.kuro.yema.adapters.HomeViewPageAdapter;
import com.kuro.yema.utils.TabItem;
import com.kuro.yema.views.MainActivity;
import com.kuro.yema.views.fragments.main.MainFragmentInterface;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements MainFragmentInterface {
    private static final ArrayList<String> TAB_TEXTS = new ArrayList<>();
    private static final ArrayList<TabItem> TAB_FRAGMENTS = new ArrayList<>();
    private final String ASSISTANCE_PHONE_NUMBER = BuildConfig.ASSISTANCE_PHONE_NUMBER;
    private ViewPager2 mHomeViewPager;
    private TabLayout mTabLayout;
    private NavController mNavController;
    private FirebaseAuth mFirebaseAuth;
    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // adding the tab items title and TabItem element once
        if (TAB_FRAGMENTS.isEmpty() && TAB_TEXTS.isEmpty()) {
            TAB_TEXTS.add(getString(R.string.all_listing_tab));
            TAB_FRAGMENTS.add(TabItem.ALL);

            TAB_TEXTS.add(getString(R.string.new_tab));
            TAB_FRAGMENTS.add(TabItem.NEW);

            TAB_TEXTS.add(getString(R.string.studio_tab));
            TAB_FRAGMENTS.add(TabItem.STUDIO);

            TAB_TEXTS.add(getString(R.string.bedroom_3_tab));
            TAB_FRAGMENTS.add(TabItem.BEDROOM_3);

            TAB_TEXTS.add(getString(R.string.bedroom_2_tab));
            TAB_FRAGMENTS.add(TabItem.BEDROOM_2);
        }
        mFirebaseAuth = FirebaseAuth.getInstance();

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View homeView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(homeView, savedInstanceState);
        mainActivity = (MainActivity) requireActivity();

        mainActivity.setNavHomeClickListener(onNavHomeClicked());
        mainActivity.setNavSearchClickListener(onNavSearchClicked());
        mainActivity.setNavWishlistClickListener(onNavWishlistClicked());
        mainActivity.setNavProfileClickListener(onNavProfileClicked());
        // set the view as active tab
        mainActivity.setActiveNavigationItem(MainActivity.NavigationItems.HOME);

        mNavController = Navigation.findNavController(homeView);

        mTabLayout = homeView.findViewById(R.id.tab_layout);
        mHomeViewPager = homeView.findViewById(R.id.fragment_home_viewpager);
        ImageButton mAssistance = homeView.findViewById(R.id.assistance_image_view);
        CardView mLogoToHome = homeView.findViewById(R.id.logo_to_profile);

        RelativeLayout mSearchBar = homeView.findViewById(R.id.search_bar);
        mSearchBar.setOnClickListener(onNavSearchClicked());

        mLogoToHome.setOnClickListener(onNavProfileClicked());
        mAssistance.setOnClickListener(view -> {
            // open Whatsapp
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.open_whatsapp)
                    .setPositiveButton(R.string.open, (dialogInterface, i) -> openWhatsapp(requireActivity(), ASSISTANCE_PHONE_NUMBER, R.string.whatsapp_was_not_found, R.string.no_whatsapp_message))
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss()).create();

            alertDialog.setOnShowListener(dialog -> {
                // Get the buttons from the AlertDialog after it's shown
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                // Apply the custom style to the buttons
                if (positiveButton != null && negativeButton != null) {
                    negativeButton.setBackgroundColor(getResources().getColor(R.color.dark_grey, requireActivity().getTheme()));
                    negativeButton.setTextColor(getResources().getColor(R.color.white, requireActivity().getTheme()));

                    positiveButton.setBackgroundColor(getResources().getColor(R.color.primary, requireActivity().getTheme()));
                    positiveButton.setTextColor(getResources().getColor(R.color.black, requireActivity().getTheme()));

                }
            });
            alertDialog.show();
        });

        setupViewPager(homeView);
    }


    private void setupViewPager(View view) {
        mHomeViewPager.setAdapter(new HomeViewPageAdapter(requireActivity(), TAB_FRAGMENTS, Navigation.findNavController(view), mainActivity.getNavLayout()));
        mHomeViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
        // Adding the tab items and attaching the viewPager to the tabLayout
        new TabLayoutMediator(mTabLayout, mHomeViewPager, (tab, position) -> tab.setText(TAB_TEXTS.get(position))).attach();
    }

    @Override
    public View.OnClickListener onNavHomeClicked() {
        return view -> {
        };
    }

    @Override
    public View.OnClickListener onNavSearchClicked() {
        return view -> NavigationUtils.navigateSafe(mNavController, HomeFragmentDirections.actionHomeFragmentToSearchFragment());

    }

    @Override
    public View.OnClickListener onNavWishlistClicked() {
        return view -> {
            NavDirections navDirections;
            if (mFirebaseAuth.getCurrentUser() != null) {
                navDirections = HomeFragmentDirections.actionHomeFragmentToWishlistFragment();
            } else {
                navDirections = HomeFragmentDirections.actionHomeFragmentToWishlistGuestFragment();
            }
            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavProfileClicked() {
        return view -> {
            NavDirections navDirections;
            if (mFirebaseAuth.getCurrentUser() != null) {
                navDirections = HomeFragmentDirections.actionHomeFragmentToProfileFragment();
            } else {
                navDirections = HomeFragmentDirections.actionHomeFragmentToProfileGuestFragment();
            }
            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }
}
