package com.kuro.yema.views.fragments.main.profile;

import static com.kuro.androidutils.sender.Sender.openWhatsapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;
import com.kuro.yema.BuildConfig;
import com.kuro.yema.R;
import com.kuro.androidutils.ui.NavigationUtils;import com.kuro.yema.views.MainActivity;
import com.kuro.yema.views.fragments.main.MainFragmentInterface;

import java.util.Objects;

public class ProfileFragment extends Fragment implements MainFragmentInterface {
    private final String ASSISTANCE_PHONE_NUMBER = BuildConfig.ASSISTANCE_PHONE_NUMBER;
    private NavController mNavController;
    private FirebaseAuth mFirebaseAuth;
    private CountryCodePicker mCCP;
    private TextView mPhoneIndicator;
    private LinearLayout mFeedback;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFirebaseAuth = FirebaseAuth.getInstance();

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View profileView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(profileView, savedInstanceState);

        MainActivity mainActivity = (MainActivity) requireActivity();

        mainActivity.getNavLayout().setVisibility(View.VISIBLE);
        mainActivity.setNavHomeClickListener(onNavHomeClicked());
        mainActivity.setNavSearchClickListener(onNavSearchClicked());
        mainActivity.setNavWishlistClickListener(onNavWishlistClicked());
        mainActivity.setNavProfileClickListener(onNavProfileClicked());

        // set the view as active tab
        mainActivity.setActiveNavigationItem(MainActivity.NavigationItems.PROFILE);

        mNavController = Navigation.findNavController(profileView);

        mCCP = profileView.findViewById(R.id.ccp);
        mPhoneIndicator = profileView.findViewById(R.id.profile_phone_indicator);
        TextInputEditText mPhoneNumberEditText = profileView.findViewById(R.id.profile_phone_number);
        LinearLayout mSettings = profileView.findViewById(R.id.profile_settings);
        LinearLayout mPrivacyPolicy = profileView.findViewById(R.id.profile_privacy_policy);
        mFeedback = profileView.findViewById(R.id.profile_feedback);
        TextView mUserId = profileView.findViewById(R.id.user_id);
        ImageButton mAssistance = profileView.findViewById(R.id.assistance_image_view);

        mAssistance.setOnClickListener(view -> {
            // open whatsapp
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

        // Attach CarrierNumber editText to CCP
        // TODO : change the country flag to match the payment phone number

        mCCP.registerCarrierNumberEditText(mPhoneNumberEditText);
        mPhoneIndicator.setText(mCCP.getSelectedCountryCodeWithPlus());
        mCCP.setOnCountryChangeListener(() -> {
            mPhoneIndicator.setText(mCCP.getSelectedCountryCodeWithPlus());
        });
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {

            // TODO : change the country flag to match the payment phone number
            mCCP.setFullNumber(user.getPhoneNumber());
            mCCP.setNumberAutoFormattingEnabled(true);
            mCCP.setCountryForPhoneCode(Integer.parseInt(mCCP.getSelectedCountryCode()));
            mPhoneIndicator.setText(mCCP.getSelectedCountryCodeWithPlus());

            // TODO : on update of the phone number, change it in the database

            String phoneNumber = Objects.requireNonNull(user.getPhoneNumber()).replace(mCCP.getSelectedCountryCodeWithPlus(), "");
            mUserId.setText(String.format(getString(R.string.user_id), phoneNumber));
            // TODO : set this to the payment phone number and make it editable

            mPhoneNumberEditText.setText(phoneNumber);
        }

        mSettings.setOnClickListener(view -> {
            NavDirections navDirections = ProfileFragmentDirections.actionProfileFragmentToSettingFragment();
            NavigationUtils.navigateSafe(mNavController, navDirections);
        });

        mPrivacyPolicy.setOnClickListener(v -> {
            NavigationUtils.navigateSafe(mNavController, ProfileFragmentDirections.actionProfileFragmentToWebViewFragment2());
        });
    }

    @Override
    public View.OnClickListener onNavHomeClicked() {
        return view -> {
            NavDirections navDirections = ProfileFragmentDirections.actionProfileFragmentToHomeFragment();
            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavSearchClicked() {
        return view -> NavigationUtils.navigateSafe(mNavController, ProfileFragmentDirections.actionProfileFragmentToSearchFragment());

    }

    @Override
    public View.OnClickListener onNavWishlistClicked() {
        return view -> {
            NavDirections navDirections = ProfileFragmentDirections.actionProfileFragmentToWishlistFragment();
            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavProfileClicked() {
        return view -> {
        };
    }
}
