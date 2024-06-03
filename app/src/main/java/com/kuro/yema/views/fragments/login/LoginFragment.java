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
import android.widget.Toast;

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

public class LoginFragment extends Fragment {
    private AuthViewModel mAuthViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(AuthViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View loginView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(loginView, savedInstanceState);
        NavController navController = Navigation.findNavController(loginView);

        TextInputEditText phoneNumberTextEdit = loginView.findViewById(R.id.login_phone_number);
        TextView newAccountTextView = loginView.findViewById(R.id.new_user_create_an_account);
        TextView continueAsGuestTextView = loginView.findViewById(R.id.continue_as_guest);
        TextView phoneIndicatorTextView = loginView.findViewById(R.id.phone_indicator);
        TextView termsTextView = loginView.findViewById(R.id.terms_text_view);
        CountryCodePicker mCcp = loginView.findViewById(R.id.ccp);
        Button mLoginBtn = loginView.findViewById(R.id.login_btn);

        View[] focusableViews = {mCcp, phoneNumberTextEdit};
        loginView.setOnTouchListener(SoftKeyboard.hideSoftKeyboardOnTouchEvent(focusableViews, requireContext()));

        mLoginBtn.setEnabled(false);

        phoneIndicatorTextView.setText(mCcp.getSelectedCountryCodeWithPlus());
        phoneNumberTextEdit.requestFocus();
        // Attach CarrierNumber editText to CCP
        mCcp.registerCarrierNumberEditText(phoneNumberTextEdit);
        mCcp.setOnCountryChangeListener(() -> phoneIndicatorTextView.setText(mCcp.getSelectedCountryCodeWithPlus()));
        mCcp.setPhoneNumberValidityChangeListener(isValidNumber -> {
            // enable the button when the phone number is valid
            mLoginBtn.setEnabled(mCcp.isValidFullNumber());
            if (mCcp.isValidFullNumber()) {
                mLoginBtn.setTextColor(getResources().getColor(R.color.text_primary, requireContext().getTheme()));
            } else {
                mLoginBtn.setTextColor(getResources().getColor(R.color.white, requireContext().getTheme()));
            }
        });

        newAccountTextView.setHighlightColor(Color.TRANSPARENT);
        continueAsGuestTextView.setHighlightColor(Color.TRANSPARENT);
        termsTextView.setHighlightColor(Color.TRANSPARENT);

        // set the "Register" text clickable
        CustomSpannableString.addSpannableString(newAccountTextView, "Register", new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                NavDirections action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();
                Navigation.findNavController(loginView).navigate(action);
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
                NavDirections action = LoginFragmentDirections.actionLoginFragmentToMainActivity();
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
                LoginFragmentDirections.ActionLoginFragmentToWebViewFragment action;
                action = LoginFragmentDirections.actionLoginFragmentToWebViewFragment();
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
                LoginFragmentDirections.ActionLoginFragmentToWebViewFragment action;
                action = LoginFragmentDirections.actionLoginFragmentToWebViewFragment();
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
        mLoginBtn.setOnClickListener(view -> {
            String phoneNumber = mCcp.getFormattedFullNumber();

            if (mCcp.isValidFullNumber()) {
                // if no sms was sent in the past 60s (default), we send a new one
                NavDirections action = LoginFragmentDirections.actionLoginFragmentToVerificationFragment(phoneNumber);
                NavigationUtils.navigateSafe(navController, action);
                if (!VerificationSMS.isCodeSent) {
                    mAuthViewModel.startVerification(fragment, phoneNumber);
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.invalid_phone_number_format), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
