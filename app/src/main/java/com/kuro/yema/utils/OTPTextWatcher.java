package com.kuro.yema.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.kuro.yema.R;

public class OTPTextWatcher implements TextWatcher, View.OnKeyListener {
    public static EditText[] editTexts;
    public static Fragment fragment;
    public static String code;
    public static String confirmPasscode;
    public static boolean isInConfirmPasscodeFragment;
    private final EditText mCurrentField;
    private final EditText mNextField;
    private final EditText mPreviousField;

    private final Button mAttachedBtn;

    public OTPTextWatcher(EditText currentField, EditText nextField, EditText previousField, Button attachedButton) {
        this.mCurrentField = currentField;
        this.mNextField = nextField;
        this.mPreviousField = previousField;
        this.mAttachedBtn = attachedButton;
    }

    /**
     * Initialize the following class attributes
     *
     * @param editTexts List of the editTexts
     * @param fragment  The current fragment
     */
    public static void init(EditText[] editTexts, Fragment fragment) {
        OTPTextWatcher.editTexts = editTexts;
        OTPTextWatcher.fragment = fragment;
        OTPTextWatcher.code = "";
    }

    /**
     * Initialize the following class attributes
     *
     * @param editTexts           List of the editTexts
     * @param fragment            The current fragment
     * @param parentFragmentClass The class of the calling fragment
     */
    public static void init(EditText[] editTexts, Fragment fragment, Class<?> parentFragmentClass) {
        OTPTextWatcher.editTexts = editTexts;
        OTPTextWatcher.fragment = fragment;
        OTPTextWatcher.code = "";
        OTPTextWatcher.confirmPasscode = "";
        OTPTextWatcher.isInConfirmPasscodeFragment = false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    // TODO: UPDATE THIS TO HANDLE COPY/PASTE

    @Override
    public void afterTextChanged(Editable editable) {
        if (mNextField == null && checkOTP(OTPTextWatcher.editTexts)) {

            StringBuilder code = new StringBuilder();
            for (EditText editText : OTPTextWatcher.editTexts) {
                code.append(editText.getText().toString());
            }

            OTPTextWatcher.code = code.toString();
            mAttachedBtn.setEnabled(true);
            mAttachedBtn.setTextColor(OTPTextWatcher.fragment.getResources().getColor(R.color.text_primary, fragment.requireContext().getTheme()));
            return;
        } else if (mNextField != null) {
            if (editable.length() == 1) {
                mNextField.setEnabled(true);
                mNextField.requestFocus();
                mNextField.setSelection(mNextField.getText().length()); // Move cursor to the end
            }
        } else if (editable.length() == 0 && mPreviousField != null) {
            mPreviousField.requestFocus();
            mCurrentField.setEnabled(false);
        }
        mAttachedBtn.setEnabled(false);
        mAttachedBtn.setTextColor(fragment.getResources().getColor(R.color.white, fragment.requireContext().getTheme()));
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        // Allow to go to the next field after writing in the previous
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DEL && mPreviousField != null) {
                mPreviousField.setSelection(mPreviousField.getText().length()); // Move cursor to the end
                mPreviousField.requestFocus();
                mCurrentField.setEnabled(false);
                mCurrentField.setText("");
            } else if (keyCode != KeyEvent.KEYCODE_DEL && mNextField != null) {
                if (mCurrentField.getText().toString().length() != 0) {
                    mCurrentField.setText(String.valueOf(keyCodeToNumber(keyCode)));
                }
                mNextField.setEnabled(true);
                mNextField.requestFocus();
            }
        }
        return false;
    }

    private int keyCodeToNumber(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                return 0;
            case KeyEvent.KEYCODE_1:
                return 1;
            case KeyEvent.KEYCODE_2:
                return 2;
            case KeyEvent.KEYCODE_3:
                return 3;
            case KeyEvent.KEYCODE_4:
                return 4;
            case KeyEvent.KEYCODE_5:
                return 5;
            case KeyEvent.KEYCODE_6:
                return 6;
            case KeyEvent.KEYCODE_7:
                return 7;
            case KeyEvent.KEYCODE_8:
                return 8;
            case KeyEvent.KEYCODE_9:
                return 9;
            default:
                return -1; // Return -1 for non-numeric keys
        }
    }

    private boolean checkOTP(EditText[] editTexts) {
        for (EditText editText : editTexts) {
            if (editText.getText().length() != 1) {
                return false;
            }
        }
        return true;
    }
}
