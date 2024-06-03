package com.kuro.yema.views.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kuro.yema.R;

public class ConfirmationDialogFragment extends DialogFragment {

    private int mMessageId = -1, mBtnMessageId = -1;
    private View.OnClickListener mOnConfirmationListener;

    public ConfirmationDialogFragment(int message, int btnMessage) {
        mMessageId = message;
        mBtnMessageId = btnMessage;
    }

    public void setOnConfirmationListener(View.OnClickListener mOnConfirmationListener) {
        this.mOnConfirmationListener = mOnConfirmationListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button confirmationBtn = view.findViewById(R.id.logout_btn);
        TextView dialogText = view.findViewById(R.id.dialog_message);

        if (mMessageId > 0) {
            dialogText.setText(getResources().getString(mMessageId));
        }
        if (mBtnMessageId > 0) {
            confirmationBtn.setText(getResources().getString(mBtnMessageId));
        }
        confirmationBtn.setOnClickListener(mOnConfirmationListener);

        // Set up click listener for the close button
        View closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
