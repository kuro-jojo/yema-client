package com.kuro.yema.views;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.kuro.yema.R;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mNavLayout, mNavHome, mNavSearch, mNavWishlist, mNavProfile;

    private ImageView mNavHomeImage, mNavSearchImage, mNavWishlistImage, mNavProfileImage;
    private TextView mNavHomeTextView, mNavSearchTextView, mNavWishlistTextView, mNavProfileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavLayout = findViewById(R.id.nav_layout);

        mNavHome = findViewById(R.id.nav_home);
        mNavSearch = findViewById(R.id.nav_search);
        mNavWishlist = findViewById(R.id.nav_wishlist);
        mNavProfile = findViewById(R.id.nav_profile);

        mNavHomeImage = findViewById(R.id.nav_home_image);
        mNavSearchImage = findViewById(R.id.nav_search_image);
        mNavWishlistImage = findViewById(R.id.nav_wishlist_image);
        mNavProfileImage = findViewById(R.id.nav_profile_image);

        mNavHomeTextView = findViewById(R.id.nav_home_text);
        mNavSearchTextView = findViewById(R.id.nav_search_text);
        mNavWishlistTextView = findViewById(R.id.nav_wishlist_text);
        mNavProfileTextView = findViewById(R.id.nav_profile_text);

        setUpDynamicLink();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpDynamicLink();
    }

    private void setUpDynamicLink() {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Log.i("MainActivity", "We have a dynamic link");

                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.i("MainActivity", "The deep link is 1 :  " + deepLink);

                        }

                        if (deepLink != null) {
                            Log.i("MainActivity", "The deep link is :  " + deepLink.toString());

                            String houseId = deepLink.getQueryParameter("houseId");
                            Log.i("MainActivity", "id = " + houseId);
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("MainActivity", "No dynamic link");
                    }
                });
    }

    public LinearLayout getNavLayout() {
        return mNavLayout;
    }

    public void setNavHomeClickListener(View.OnClickListener onClickListener) {
        mNavHome.setOnClickListener(onClickListener);
    }

    public void setNavSearchClickListener(View.OnClickListener onClickListener) {
        mNavSearch.setOnClickListener(onClickListener);
    }

    public void setNavWishlistClickListener(View.OnClickListener onClickListener) {
        mNavWishlist.setOnClickListener(onClickListener);
    }

    public void setNavProfileClickListener(View.OnClickListener onClickListener) {
        mNavProfile.setOnClickListener(onClickListener);
    }

    /**
     * Reset navigation items (home, search, wishlist, profile)
     * by resetting the image and the text color
     */
    public void resetNavigationItems() {

        // resetting home image
        mNavHomeImage.setImageResource(R.drawable.home_24);
        mNavHomeTextView.setTextColor(getColor(R.color.text_unselected));

        // resetting search image
        mNavSearchImage.setImageResource(R.drawable.search_24);
        mNavSearchTextView.setTextColor(getColor(R.color.text_unselected));

        // resetting wishlist image
        mNavWishlistImage.setImageResource(R.drawable.wishlist_24);
        mNavWishlistTextView.setTextColor(getColor(R.color.text_unselected));

        // resetting profile image
        mNavProfileImage.setImageResource(R.drawable.profile_24);
        mNavProfileTextView.setTextColor(getColor(R.color.text_unselected));
    }

    public void setActiveNavigationItem(NavigationItems item) {
        resetNavigationItems();
        switch (item) {
            case HOME:
                // setting home as active tab
                mNavHomeImage.setImageResource(R.drawable.home_black_24);
                mNavHomeTextView.setTextColor(getColor(R.color.black));
                break;
            case SEARCH:
                // setting search as active tab
                mNavSearchImage.setImageResource(R.drawable.search_black_24);
                mNavSearchTextView.setTextColor(getColor(R.color.black));
                break;
            case WISHLIST:
                // setting wishlist as active tab
                mNavWishlistImage.setImageResource(R.drawable.wishlist_fill_24);
                mNavWishlistTextView.setTextColor(getColor(R.color.black));
                break;
            case PROFILE:
                // setting profile as active tab
                mNavProfileImage.setImageResource(R.drawable.profile_black_24);
                mNavProfileTextView.setTextColor(getColor(R.color.black));
        }
    }

    public enum NavigationItems {
        HOME,
        SEARCH,
        WISHLIST,
        PROFILE
    }
}