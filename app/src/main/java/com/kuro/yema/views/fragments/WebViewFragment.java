package com.kuro.yema.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kuro.yema.R;
import com.kuro.androidutils.logging.Logger;
import com.kuro.yema.views.MainActivity;

public class WebViewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) requireActivity();

        mainActivity.getNavLayout().setVisibility(View.GONE);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String type = WebViewFragmentArgs.fromBundle(requireArguments()).getType();

        WebView privacyWebview = view.findViewById(R.id.privacy_webview);
        Logger.log("type " + type);
        if (type != null) {
            if (type.equals("terms")) {
                privacyWebview.loadUrl("https://www.google.com");
            } else if (type.equals("privacy")) {
                privacyWebview.loadUrl("https://developer.android.com/");
            } else {
                privacyWebview.loadUrl("https://github.com/");
            }
        }
    }
}