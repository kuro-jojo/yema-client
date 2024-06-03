package com.kuro.yema.views.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kuro.yema.R;

public class VerifyDialogFragment extends DialogFragment {
    private String mMessage;


    public VerifyDialogFragment(String mMessage) {
        this.mMessage = mMessage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_verify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the text of the TextView in the dialog
        TextView messageTextView = view.findViewById(R.id.dialog_message);
        messageTextView.setText(mMessage);

        // Set up click listener for the close button
        View closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());
    }


}
