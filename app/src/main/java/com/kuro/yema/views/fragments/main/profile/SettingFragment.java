package com.kuro.yema.views.fragments.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.kuro.yema.R;
import com.kuro.androidutils.ui.NavigationUtils;import com.kuro.yema.views.LoginActivity;
import com.kuro.yema.views.MainActivity;
import com.kuro.yema.views.dialogs.ConfirmationDialogFragment;
import com.kuro.yema.views.fragments.main.MainFragmentInterface;

public class SettingFragment extends Fragment implements MainFragmentInterface {
    private FirebaseAuth mFirebaseAuth;
    private NavController mNavController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View settingView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(settingView, savedInstanceState);
        mNavController = Navigation.findNavController(settingView);
        mFirebaseAuth = FirebaseAuth.getInstance();

        MainActivity mainActivity = (MainActivity) requireActivity();

        mainActivity.getNavLayout().setVisibility(View.GONE);
        mainActivity.setNavHomeClickListener(onNavHomeClicked());
        mainActivity.setNavSearchClickListener(onNavSearchClicked());
        mainActivity.setNavWishlistClickListener(onNavWishlistClicked());
        mainActivity.setNavProfileClickListener(onNavProfileClicked());

        // set the view as active tab
        mainActivity.setActiveNavigationItem(MainActivity.NavigationItems.PROFILE);
        LinearLayout mLogoutLayout = settingView.findViewById(R.id.profile_logout);
        ImageButton mBackProfile = settingView.findViewById(R.id.back_to_previous_fragment);

        mLogoutLayout.setOnClickListener(view -> {
            ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment(R.string.confirm_logout, R.string.logout);
            confirmationDialogFragment.setOnConfirmationListener(view1 -> {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                requireActivity().startActivity(intent);
                requireActivity().finish();
            });
            confirmationDialogFragment.show(getChildFragmentManager(), "DIALOG");
        });

        mBackProfile.setOnClickListener(view -> {
            NavDirections navDirections = SettingFragmentDirections.actionSettingFragmentToProfileFragment();
            NavigationUtils.navigateSafe(mNavController, navDirections);
        });

    }

    @Override
    public View.OnClickListener onNavHomeClicked() {
        return view -> {
            NavDirections navDirections = SettingFragmentDirections.actionSettingFragmentToHomeFragment();
            NavigationUtils.navigateSafe(mNavController,navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavSearchClicked() {
        return view -> NavigationUtils.navigateSafe(mNavController,SettingFragmentDirections.actionSettingFragmentToSearchFragment());

    }

    @Override
    public View.OnClickListener onNavWishlistClicked() {
        return view -> {
            NavDirections navDirections = SettingFragmentDirections.actionSettingFragmentToWishlistFragment();
            NavigationUtils.navigateSafe(mNavController,navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavProfileClicked() {
        return view -> {
            NavDirections navDirections = SettingFragmentDirections.actionSettingFragmentToProfileFragment();
            NavigationUtils.navigateSafe(mNavController,navDirections);
        };
    }
}
