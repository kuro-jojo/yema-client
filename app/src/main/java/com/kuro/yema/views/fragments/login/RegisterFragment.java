package com.kuro.yema.views.fragments.login;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.kuro.yema.R;
import com.kuro.androidutils.string.CustomSpannableString;
import com.kuro.androidutils.ui.NavigationUtils;import com.kuro.yema.utils.VerificationSMS;
import com.kuro.androidutils.ui.SoftKeyboard;
import com.kuro.yema.viewModels.AuthViewModel;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private AuthViewModel mAuthViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory
                        .getInstance(requireActivity().getApplication())).get(AuthViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View registerView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(registerView, savedInstanceState);
        NavController navController = Navigation.findNavController(registerView);

        TextInputEditText phoneNumberTextEdit = registerView.findViewById(R.id.register_phone_number);
        TextView alreadyAccount = registerView.findViewById(R.id.already_have_an_account);
        TextView continueAsGuestTextView = registerView.findViewById(R.id.continue_as_guest);
        TextView phoneIndicatorTextView = registerView.findViewById(R.id.phone_indicator);
        TextView termsTextView = registerView.findViewById(R.id.terms_text_view);
        CountryCodePicker mCcp = registerView.findViewById(R.id.register_ccp);
        Button registerContinueBtn = registerView.findViewById(R.id.register_continue_btn);


        View[] focusableViews = {mCcp, phoneNumberTextEdit};
        registerView.setOnTouchListener(SoftKeyboard.hideSoftKeyboardOnTouchEvent(focusableViews, requireContext()));

        registerContinueBtn.setEnabled(false);

        phoneIndicatorTextView.setText(mCcp.getSelectedCountryCodeWithPlus());
        phoneNumberTextEdit.requestFocus();
        mCcp.registerCarrierNumberEditText(phoneNumberTextEdit);
        mCcp.setOnCountryChangeListener(() -> phoneIndicatorTextView.setText(mCcp.getSelectedCountryCodeWithPlus()));

        mCcp.setPhoneNumberValidityChangeListener(isValidNumber -> {
            // enable the button when the phone number is valid
            registerContinueBtn.setEnabled(mCcp.isValidFullNumber());
            if (mCcp.isValidFullNumber()) {
                registerContinueBtn.setTextColor(getResources().getColor(R.color.text_primary, requireContext().getTheme()));
            } else {
                registerContinueBtn.setTextColor(getResources().getColor(R.color.white, requireContext().getTheme()));
            }
        });

        // Remove highlight when the text is selected
        alreadyAccount.setHighlightColor(Color.TRANSPARENT);
        continueAsGuestTextView.setHighlightColor(Color.TRANSPARENT);
        termsTextView.setHighlightColor(Color.TRANSPARENT);

        // set the text "login" clickable
        CustomSpannableString.addSpannableString(alreadyAccount, "Login", new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                NavDirections action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
                NavigationUtils.navigateSafe(navController, action);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(requireContext().getColor(R.color.blue_link_bold));
                ds.setUnderlineText(false);
            }
        }, new StyleSpan(Typeface.BOLD));

        CustomSpannableString.addSpannableString(continueAsGuestTextView, continueAsGuestTextView.getText().toString(), new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                // TODO: launch main activity
                NavDirections action = RegisterFragmentDirections.actionRegisterFragmentToMainActivity();
                NavigationUtils.navigateSafe(navController, action);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(requireContext().getColor(R.color.light_black));
                ds.setUnderlineText(true);
            }
        });

        // Define the text to be linked (e.g., "Terms of service" and "Privacy Policy")
        String[] linkedTexts = {"Terms of service", "Privacy Policy"};

        ClickableSpan termsClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Open a WebView or perform the desired action for Terms of Service
                // TODO: add open terms webpage
                RegisterFragmentDirections.ActionRegisterFragmentToWebViewFragment action;
                action = RegisterFragmentDirections.actionRegisterFragmentToWebViewFragment();
                action.setType("terms");
                NavigationUtils.navigateSafe(navController, action);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(requireContext().getColor(R.color.blue_link_bold));
                ds.setUnderlineText(true);
            }
        };

        ClickableSpan privacyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Open a WebView or perform the desired action for Privacy Policy
                // TODO: add open privacy webpage
                RegisterFragmentDirections.ActionRegisterFragmentToWebViewFragment action;
                action = RegisterFragmentDirections.actionRegisterFragmentToWebViewFragment();
                action.setType("privacy");
                NavigationUtils.navigateSafe(navController, action);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(requireContext().getColor(R.color.blue_link_bold));
                ds.setUnderlineText(true);
            }
        };

        // configure the spannable string
        try {
            CustomSpannableString.addSpannableString(termsTextView, linkedTexts, new ClickableSpan[]{termsClickableSpan, privacyClickableSpan});
        } catch (Exception e) {
            Log.e("SPANNABLE", Objects.requireNonNull(e.getMessage()));
            throw new RuntimeException(e);
        }

        Fragment fragment = this;
        registerContinueBtn.setOnClickListener(view -> {
            // get phone number in the following format : +221 77 xxx xx xx
            String phoneNumber = mCcp.getFormattedFullNumber();
            NavDirections action = RegisterFragmentDirections.actionRegisterFragmentToVerificationFragment(phoneNumber);
            NavigationUtils.navigateSafe(navController, action);
            // if no sms was sent in the past 60s (default), we send a new one
            if (!VerificationSMS.isCodeSent) {
                mAuthViewModel.startVerification(fragment, phoneNumber);
            }

        });
    }
}
