package com.kuro.yema.views.fragments.main.wishlist;

import static com.kuro.androidutils.sender.Sender.openWhatsapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yema.BuildConfig;
import com.kuro.yema.R;
import com.kuro.yema.views.MainActivity;
import com.kuro.yema.views.fragments.main.MainFragmentInterface;

public class WishlistGuestFragment extends Fragment implements MainFragmentInterface {
    private final String ASSISTANCE_PHONE_NUMBER = BuildConfig.ASSISTANCE_PHONE_NUMBER;
    private NavController mNavController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_wishlist_guest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View profileView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(profileView, savedInstanceState);
        MainActivity mainActivity = (MainActivity) requireActivity();

        mainActivity.setNavHomeClickListener(onNavHomeClicked());
        mainActivity.setNavSearchClickListener(onNavSearchClicked());
        mainActivity.setNavWishlistClickListener(onNavWishlistClicked());
        mainActivity.setNavProfileClickListener(onNavProfileClicked());

        // set the view as active tab
        mainActivity.setActiveNavigationItem(MainActivity.NavigationItems.WISHLIST);
        ImageButton assistanceBtn = profileView.findViewById(R.id.assistance_image_view);
        assistanceBtn.setOnClickListener(view -> {
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

        mNavController = Navigation.findNavController(profileView);

        Button signInBtn = profileView.findViewById(R.id.guest_sign_in_btn);
        signInBtn.setOnClickListener(view -> requireActivity().finish());
    }

    @Override
    public View.OnClickListener onNavHomeClicked() {
        return view -> {
            NavDirections navDirections = WishlistGuestFragmentDirections.actionWishlistGuestFragmentToHomeFragment();
            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavSearchClicked() {
        return view -> NavigationUtils.navigateSafe(mNavController, WishlistGuestFragmentDirections.actionWishlistGuestFragmentToSearchFragment());

    }

    @Override
    public View.OnClickListener onNavWishlistClicked() {
        return view -> {
        };
    }

    @Override
    public View.OnClickListener onNavProfileClicked() {
        return view -> {
            NavDirections navDirections = WishlistGuestFragmentDirections.actionWishlistGuestFragmentToProfileGuestFragment();
            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }
}
