package com.kuro.yema.utils;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kuro.yema.R;
import com.kuro.yema.repositories.AuthRepository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public abstract class VerificationSMS {
    public static Fragment fragment;
    public static AuthRepository authRepository;
    public static String verificationId;
    public static String phoneNumber;

    public static long timeout = 60L;
    public static boolean isCodeSent = false;


    public static void init(Fragment frag, String ph, AuthRepository repo) {
        fragment = frag;
        phoneNumber = ph;
        authRepository = repo;
    }

    public static PhoneAuthOptions getVerificationOptions() {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("onVerificationCompleted", "onVerificationCompleted:" + credential);
                Toast.makeText(fragment.getActivity(), "Verifying code automatically", Toast.LENGTH_SHORT).show();

                isCodeSent = true;
                authRepository.logIn(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                isCodeSent = true;
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.e("onVerificationFailed ", Objects.requireNonNull(e.getMessage()));
                // TODO: update those errors
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.e("onVerificationFailed ", "Invalid request");

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.e("onVerificationFailed ", " The SMS quota for the project has been exceeded");
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    Log.e("onVerificationFailed ", " reCAPTCHA verification attempted with null Activity");
                    // reCAPTCHA verification attempted with null Activity
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                VerificationSMS.isCodeSent = true;
                VerificationSMS.verificationId = verificationId;

                Toast.makeText(fragment.getActivity(), fragment.getResources().getString(R.string.the_confirmation_code_was_sent), Toast.LENGTH_SHORT).show();
                Log.d("onCodeSent", "code sent :");
            }

        };
        return PhoneAuthOptions.newBuilder().setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(timeout, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(fragment.requireActivity())                 // (optional) Activity for callback binding
                // If no activity is passed, reCAPTCHA verification can not be used.
                .setCallbacks(mCallbacks)
                // OnVerificationStateChangedCallbacks
                .build();
    }
}
