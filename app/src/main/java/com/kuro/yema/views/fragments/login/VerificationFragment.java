package com.kuro.yema.views.fragments.login;

import static com.kuro.androidutils.sender.Sender.openWhatsapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kuro.androidutils.layout.GridLayoutUtil;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yema.BuildConfig;
import com.kuro.yema.R;
import com.kuro.yema.data.model.User;
import com.kuro.yema.utils.OTPTextWatcher;
import com.kuro.yema.utils.VerificationSMS;
import com.kuro.yema.viewModels.AuthViewModel;
import com.kuro.yema.viewModels.UserViewModel;
import com.kuro.yema.views.dialogs.VerifyDialogFragment;


public class VerificationFragment extends Fragment {
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final int RESEND_CODE_DEFAULT_DELAY = 60;
    private static int resendCodeRemainingSeconds = RESEND_CODE_DEFAULT_DELAY;
    private final String ASSISTANCE_PHONE_NUMBER = BuildConfig.ASSISTANCE_PHONE_NUMBER;
    private final Handler handler = new Handler();
    private NavController mNavController;
    private AuthViewModel mAuthViewModel;
    private UserViewModel mUserViewModel;
    private String mPhoneNumber, mOTPCode;
    private TextView mResendCodeCounterTextView, mCodeToSentMessageTextView;
    private LinearLayout mResendCodeLayout;
    private ImageView mResendCodeImageView;
    private ImageButton mBackToPreviousFragmentImageView;
    private android.widget.GridLayout mOPTGridLayout;
    private Button mVerificationContinueBtn;

    public VerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPhoneNumber = getArguments().getString(PHONE_NUMBER);
        }
        mAuthViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(AuthViewModel.class);

        mUserViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification, container, false);
    }

    private void initializeViews(View verificationView) {
        mBackToPreviousFragmentImageView = verificationView.findViewById(R.id.back_to_previous_fragment);
        mCodeToSentMessageTextView = verificationView.findViewById(R.id.code_to_sent_message);
        mResendCodeCounterTextView = verificationView.findViewById(R.id.resend_code_counter);
        mResendCodeLayout = verificationView.findViewById(R.id.resend_code);
        mResendCodeImageView = verificationView.findViewById(R.id.resend_code_image);
        mOPTGridLayout = verificationView.findViewById(R.id.opt_grid_layout);
        mVerificationContinueBtn = verificationView.findViewById(R.id.verify_continue_btn);

        mVerificationContinueBtn.setEnabled(false);
        mVerificationContinueBtn.setTextColor(getResources().getColor(R.color.white, requireContext().getTheme()));
    }

    @Override
    public void onViewCreated(@NonNull View verificationView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(verificationView, savedInstanceState);

        mNavController = Navigation.findNavController(verificationView);
        ImageButton mAssistance = verificationView.findViewById(R.id.assistance_image_view);
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
        initializeViews(verificationView);
        resetResendText();

        mBackToPreviousFragmentImageView.setOnClickListener(view -> {

            mNavController.popBackStack(R.id.verificationFragment, true);
            mNavController.popBackStack(R.id.verificationFragment, true);

        });
        String code_to_sent_message = getString(R.string.code_to_sent_message);
        code_to_sent_message = String.format(code_to_sent_message, "<b>" + mPhoneNumber + "</b><br>");
        mCodeToSentMessageTextView.setText(Html.fromHtml(code_to_sent_message, 0));


        EditText mOTPCodeEditText1 = verificationView.findViewById(R.id.opt_code_1);
        EditText mOTPCodeEditText2 = verificationView.findViewById(R.id.opt_code_2);
        EditText mOTPCodeEditText3 = verificationView.findViewById(R.id.opt_code_3);
        EditText mOTPCodeEditText4 = verificationView.findViewById(R.id.opt_code_4);
        EditText mOTPCodeEditText5 = verificationView.findViewById(R.id.opt_code_5);
        EditText mOTPCodeEditText6 = verificationView.findViewById(R.id.opt_code_6);


        EditText[] editTexts = new EditText[]{mOTPCodeEditText1, mOTPCodeEditText2, mOTPCodeEditText3, mOTPCodeEditText4, mOTPCodeEditText5, mOTPCodeEditText6};
        OTPTextWatcher.init(editTexts, this);


        OTPTextWatcher textWatcher1 = new OTPTextWatcher(mOTPCodeEditText1, mOTPCodeEditText2, null, mVerificationContinueBtn);
        OTPTextWatcher textWatcher2 = new OTPTextWatcher(mOTPCodeEditText2, mOTPCodeEditText3, mOTPCodeEditText1, mVerificationContinueBtn);
        OTPTextWatcher textWatcher3 = new OTPTextWatcher(mOTPCodeEditText3, mOTPCodeEditText4, mOTPCodeEditText2, mVerificationContinueBtn);
        OTPTextWatcher textWatcher4 = new OTPTextWatcher(mOTPCodeEditText4, mOTPCodeEditText5, mOTPCodeEditText3, mVerificationContinueBtn);
        OTPTextWatcher textWatcher5 = new OTPTextWatcher(mOTPCodeEditText5, mOTPCodeEditText6, mOTPCodeEditText4, mVerificationContinueBtn);
        OTPTextWatcher textWatcher6 = new OTPTextWatcher(mOTPCodeEditText6, null, mOTPCodeEditText5, mVerificationContinueBtn);

        mOTPCodeEditText1.setOnKeyListener(textWatcher1);
        mOTPCodeEditText2.setOnKeyListener(textWatcher2);
        mOTPCodeEditText3.setOnKeyListener(textWatcher3);
        mOTPCodeEditText4.setOnKeyListener(textWatcher4);
        mOTPCodeEditText5.setOnKeyListener(textWatcher5);
        mOTPCodeEditText6.setOnKeyListener(textWatcher6);

        mOTPCodeEditText1.addTextChangedListener(textWatcher1);
        mOTPCodeEditText2.addTextChangedListener(textWatcher2);
        mOTPCodeEditText3.addTextChangedListener(textWatcher3);
        mOTPCodeEditText4.addTextChangedListener(textWatcher4);
        mOTPCodeEditText5.addTextChangedListener(textWatcher5);
        mOTPCodeEditText6.addTextChangedListener(textWatcher6);

        mVerificationContinueBtn.setOnClickListener(view -> {
            mOTPCode = OTPTextWatcher.code;

            if (TextUtils.isEmpty(VerificationSMS.verificationId)) {
                Toast.makeText(getContext(), getString(R.string.code_not_sent_yet), Toast.LENGTH_SHORT).show();
                mAuthViewModel.startVerification(getParentFragment(), mPhoneNumber);
            } else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationSMS.verificationId, mOTPCode);
                mAuthViewModel.logIn(credential);

                mAuthViewModel.getIsVerificationFailed().observe(getViewLifecycleOwner(), aBoolean -> {
                    if (Boolean.TRUE.equals(mAuthViewModel.getIsVerificationFailed().getValue()) && mAuthViewModel.getCurrentUser() == null) {
                        VerifyDialogFragment verifyDialogFragment = new VerifyDialogFragment("The code you entered is incorrect!");
                        verifyDialogFragment.show(getChildFragmentManager(), "DIALOG");
                        GridLayoutUtil.resetGridLayout(mOPTGridLayout);

                        mAuthViewModel.getIsVerificationFailed().removeObservers(getViewLifecycleOwner());
                    }
                });
                // The user is authenticated or created
                mAuthViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {

                    if (firebaseUser != null) {
                        Toast.makeText(requireContext(), "Authentication succeeded", Toast.LENGTH_SHORT).show();

                        NavDirections action = VerificationFragmentDirections.actionVerificationFragmentToMainActivity();
                        NavigationUtils.navigateSafe(mNavController, action);
                        mAuthViewModel.getUserMutableLiveData().removeObservers(getViewLifecycleOwner());
                        mUserViewModel.getUser(firebaseUser.getUid());
                        mUserViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
                            @Override
                            public void onChanged(User user) {
                                if (user == null || user.getPhoneNumber() == null) {
                                    mUserViewModel.addUser(firebaseUser.getUid(), mPhoneNumber);
                                }
                            }
                        });
                        requireActivity().finish();
                    }
                });
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove the Runnable to avoid memory leaks when the activity is destroyed
        handler.removeCallbacks(updateCounterRunnable);
    }

    private Runnable getCounterRunnable(Fragment fragment) {
        return new Runnable() {
            @Override
            public void run() {
                String resend_code = getString(R.string.resend_code_in_s);
                if (resendCodeRemainingSeconds > 0) {
                    countDown(this, resend_code);
                } else {
                    VerificationSMS.isCodeSent = false;
                    // After 1 minute, make the TextView clickable
                    resend_code = resend_code.replace("in %s", "");
                    mResendCodeCounterTextView.setText(resend_code);
                    mResendCodeCounterTextView.setTextColor(getResources().getColor(R.color.text_red_dark, requireContext().getTheme()));
                    mResendCodeImageView.setImageResource(R.drawable.retry_red_24);

                    mResendCodeLayout.setClickable(true);
                    mResendCodeLayout.setOnClickListener(view -> {
                        resetResendText();
                        // Resend the verification code
                        mAuthViewModel.startVerification(fragment, mPhoneNumber);
                    });
                }
            }

        };
    }

    private void resetResendText() {
        resendCodeRemainingSeconds = RESEND_CODE_DEFAULT_DELAY;
        mResendCodeLayout.setClickable(false);
        mResendCodeCounterTextView.setTextColor(getResources().getColor(R.color.text_secondary, requireContext().getTheme()));
        mResendCodeImageView.setImageResource(R.drawable.retry_24);
        handler.post(updateCounterRunnable);
    }

    private void countDown(Runnable runnable, String resend_code) {
        resendCodeRemainingSeconds--;
        resend_code = String.format(resend_code, resendCodeRemainingSeconds + "s");
        mResendCodeCounterTextView.setText(resend_code);
        handler.postDelayed(runnable, 1000); // Run every second (1000 milliseconds)
    }

    private final Runnable updateCounterRunnable = getCounterRunnable(this);


}