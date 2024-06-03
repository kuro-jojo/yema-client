package com.kuro.yema.views.fragments.main.home;

import static com.kuro.androidutils.sender.Sender.sendToWhatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.kuro.androidutils.layout.CustomSnackbar;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yema.BuildConfig;
import com.kuro.yema.R;
import com.kuro.yema.adapters.ImageSliderAdapter;
import com.kuro.yema.data.model.House;
import com.kuro.yema.viewModels.HouseViewModel;
import com.kuro.yema.viewModels.UserViewModel;
import com.kuro.yema.views.MainActivity;
import com.kuro.yema.views.fragments.main.MainFragmentInterface;

public class HouseDetailsFragment extends Fragment implements MainFragmentInterface {
    private final String ASSISTANCE_PHONE_NUMBER = BuildConfig.ASSISTANCE_PHONE_NUMBER;
    private HouseViewModel mHouseViewModel;
    private House mCurrentHouse;
    private UserViewModel mUserViewModel;
    private String mHouseId;
    private ImageButton mAddToFavorite;
    private ImageButton mAddToFavoriteSave;
    private ImageButton mShare;
    private ImageButton mMessage;
    private TextView mHouseItemTitle, mHouseItemPrice, mHouseItemImageIndex, mLeaveMessage;
    private TextView mHouseItemImageTotal, mHouseDescription, mHouseRequirements, mHouseItemPriceCurrency, mHouseItemRentalTerm;
    private RelativeLayout mShareLayout, mMessageLayout;
    private ScrollView mScrollView;
    private ViewPager2 mViewPager;
    private NavController mNavController;
    private FirebaseAuth mFirebaseAuth;
    private MainActivity mainActivity;
    private boolean isInFavorite = false;

    public HouseDetailsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        HouseViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mHouseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(HouseViewModel.class);

        UserViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mUserViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_house_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View detailView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(detailView, savedInstanceState);

        mainActivity = (MainActivity) requireActivity();

        mainActivity.getNavLayout().setVisibility(View.VISIBLE);
        mainActivity.setNavHomeClickListener(onNavHomeClicked());
        mainActivity.setNavSearchClickListener(onNavSearchClicked());
        mainActivity.setNavWishlistClickListener(onNavWishlistClicked());
        mainActivity.setNavProfileClickListener(onNavProfileClicked());

        // set the view as active tab
        mainActivity.setActiveNavigationItem(MainActivity.NavigationItems.HOME);
        if (getArguments() != null) {
            mHouseId = HouseDetailsFragmentArgs.fromBundle(getArguments()).getHouseId();
        }
        mNavController = Navigation.findNavController(detailView);

        mScrollView = detailView.findViewById(R.id.scrollView);
        mScrollView.setVisibility(View.GONE);

        ImageButton mBackHome = detailView.findViewById(R.id.back_to_previous_fragment);
        mViewPager = detailView.findViewById(R.id.detail_house_view_pager);
        mHouseItemTitle = detailView.findViewById(R.id.detail_house_title);
        mHouseItemPrice = detailView.findViewById(R.id.detail_house_price);
        mHouseItemImageIndex = detailView.findViewById(R.id.detail_house_image_index);
        mHouseItemImageTotal = detailView.findViewById(R.id.detail_house_image_total);
        mHouseDescription = detailView.findViewById(R.id.detail_description_content);
        mHouseRequirements = detailView.findViewById(R.id.detail_requirement_content);
        mHouseItemPriceCurrency = detailView.findViewById(R.id.detail_house_price_currency);
        mHouseItemRentalTerm = detailView.findViewById(R.id.detail_house_rental_term);
        mHouseItemImageIndex = detailView.findViewById(R.id.detail_house_image_index);
        mAddToFavorite = detailView.findViewById(R.id.house_item_favorite_image_btn);
        RelativeLayout mAddToFavoriteLayout = detailView.findViewById(R.id.detail_save);
        mAddToFavoriteSave = detailView.findViewById(R.id.detail_save_image);
        mShareLayout = detailView.findViewById(R.id.detail_share);
        mShare = detailView.findViewById(R.id.detail_share_image);
        mMessageLayout = detailView.findViewById(R.id.detail_message);
        mMessage = detailView.findViewById(R.id.detail_message_image);
        RelativeLayout mLeaveMessageLayout = detailView.findViewById(R.id.leave_whatsapp_message);
        mLeaveMessage = detailView.findViewById(R.id.leave_whatsapp_message_text_view);
        RelativeLayout mSendMessage = detailView.findViewById(R.id.detail_send_msg);

        mLeaveMessageLayout.setOnClickListener(getLeaveMessageListener());
        mLeaveMessage.setOnClickListener(getLeaveMessageListener());

        mAddToFavorite.setOnClickListener(getAddToWishlistListener(detailView));
        mAddToFavoriteLayout.setOnClickListener(getAddToWishlistListener(detailView));
        mAddToFavoriteSave.setOnClickListener(getAddToWishlistListener(detailView));

        mSendMessage.setOnClickListener(getSendMessageBtnListener());
        mBackHome.setOnClickListener(view -> mNavController.popBackStack(R.id.houseDetailsFragment, true));

        updateView(detailView);

    }


    private void updateView(View view) {
        mHouseViewModel.getHouse(mHouseId);
        mHouseViewModel.getHouseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<House>() {
            @Override
            public void onChanged(House house) {
                if (house != null) {
                    mCurrentHouse = house;
                    ProgressBar mProgressBar = view.findViewById(R.id.progressBar);
                    mProgressBar.setVisibility(View.GONE);
                    mScrollView.setVisibility(View.VISIBLE);

                    String title = house.getTitle();
                    if (!house.getLocation().isEmpty()) {
                        title += " at " + house.getLocation();
                    }
                    mHouseItemTitle.setText(title);
                    // set images
                    ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(requireContext(), house.getImages());
                    mViewPager.setAdapter(imageSliderAdapter);
                    mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            mHouseItemImageIndex.setText(String.valueOf(position + 1));
                        }
                    });
                    mHouseItemPrice.setText(String.valueOf(house.getPrice()));
                    mHouseItemImageTotal.setText(String.valueOf(house.getNumberOfImages()));
                    mHouseDescription.setText(house.getDescription());
                    mHouseRequirements.setText(house.getRequirements());
                    mHouseItemPriceCurrency.setText(house.getPriceCurrency().name());
                    String rentalTerm = mHouseItemRentalTerm.getText().toString();
                    mHouseItemRentalTerm.setText(String.format(rentalTerm, house.getRentalTerm().name().toLowerCase()));

                    // TODO : remove sensitive data
                    View.OnClickListener onShareListener = getShareListener();
                    mShareLayout.setOnClickListener(onShareListener);
                    mShare.setOnClickListener(onShareListener);


                    View.OnClickListener onMessageListener = getMessageListener();
                    mMessageLayout.setOnClickListener(onMessageListener);
                    mMessage.setOnClickListener(onMessageListener);

                    setItemToFavoriteIfInUserWishlist();
                }
            }
        });
    }

    @NonNull
    private View.OnClickListener getSendMessageBtnListener() {
        return view -> {
            if (mFirebaseAuth.getCurrentUser() == null) {
                NavigationUtils.navigateSafe(mNavController, HouseDetailsFragmentDirections.actionHouseDetailsFragmentToProfileGuestFragment());
                return;
            }
            if (!mLeaveMessage.getText().toString().isEmpty()) {
                AlertDialog alertDialog = new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getResources().getString(R.string.send_the_message_with_whatsapp))
                        .setPositiveButton(getResources().getString(R.string.send), (dialogInterface, i) -> {
                            getDynamicLink().addOnSuccessListener((shortDynamicLink) -> {
                                Uri dynamicLinkUri = shortDynamicLink.getShortLink();
                                if (dynamicLinkUri != null) {
                                    String message = mLeaveMessage.getText() + "\n\n" + dynamicLinkUri;
                                    sendToWhatsapp(requireActivity(), ASSISTANCE_PHONE_NUMBER, R.string.default_whatsapp_message, message);
                                }
                            });
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss())
                        .create();


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
            }
        };
    }


    private View.OnClickListener getLeaveMessageListener() {
        return view -> {
            if (mFirebaseAuth.getCurrentUser() == null) {
                NavigationUtils.navigateSafe(mNavController, HouseDetailsFragmentDirections.actionHouseDetailsFragmentToProfileGuestFragment());
                return;
            }
            // show dialog the write message
            View view1 = LayoutInflater.from(requireContext()).inflate(R.layout.edit_view, null);
            EditText editText = view1.findViewById(R.id.edit_text);
            editText.setText(mLeaveMessage.getText());
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.leave_us_a_message))
                    .setView(view1)
                    .setPositiveButton("Ok", (dialogInterface, i) ->
                            mLeaveMessage.setText(editText.getText()))
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialogInterface, i) ->
                            dialogInterface.dismiss())
                    .create();

            alertDialog.setOnShowListener(dialog -> {
                // Get the buttons from the AlertDialog after it's shown
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                if (positiveButton != null && negativeButton != null) {
                    negativeButton.setBackgroundColor(getResources().getColor(R.color.dark_grey, requireActivity().getTheme()));
                    negativeButton.setTextColor(getResources().getColor(R.color.white, requireActivity().getTheme()));

                    positiveButton.setBackgroundColor(getResources().getColor(R.color.primary, requireActivity().getTheme()));
                    positiveButton.setTextColor(getResources().getColor(R.color.black, requireActivity().getTheme()));

                }
            });

            alertDialog.show();
        };
    }

    @NonNull
    private View.OnClickListener getShareListener() {
        return view -> {
            getDynamicLink().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    Uri dynamicLinkUri = task.getResult().getShortLink();

                    if (dynamicLinkUri != null) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");

                        String message = getSharingMessage(dynamicLinkUri);
                        intent.putExtra(Intent.EXTRA_TEXT, message);
                        requireActivity().startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
                    }
                }
            });
        };
    }

    @NonNull
    private View.OnClickListener getMessageListener() {
        return view -> {
            if (mFirebaseAuth.getCurrentUser() == null) {
                NavigationUtils.navigateSafe(mNavController, HouseDetailsFragmentDirections.actionHouseDetailsFragmentToProfileGuestFragment());
                return;
            }
            getDynamicLink().addOnSuccessListener((shortDynamicLink) -> {
                Uri dynamicLinkUri = shortDynamicLink.getShortLink();
                if (dynamicLinkUri != null) {
                    sendToWhatsapp(requireActivity(), ASSISTANCE_PHONE_NUMBER, R.string.default_whatsapp_message, dynamicLinkUri.toString());
                }
            });
        };
    }

    @NonNull
    private String getSharingMessage(Uri dynamicLinkUri) {
        return String.format(""
                        + getString(R.string.default_share_message)
                        + getString(R.string.default_share_message_title)
                        + getString(R.string.default_share_message_location)
                        + getString(R.string.default_share_message_price)
                        + getString(R.string.default_share_message_contact, ASSISTANCE_PHONE_NUMBER)
                        + "\n\n " + dynamicLinkUri,
                mCurrentHouse.getListingType().type,
                mCurrentHouse.getTitle(),
                mCurrentHouse.getLocation(),
                mCurrentHouse.getPrice(),
                mCurrentHouse.getPriceCurrency());
    }


    private void setItemToFavoriteIfInUserWishlist() {
        // check if this house is in the user wishlist and update the view
        if (mFirebaseAuth.getCurrentUser() != null) {
            mUserViewModel.getUser(mFirebaseAuth.getCurrentUser().getUid());
            mUserViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), (user) -> {
                if (user != null) {
                    isInFavorite = user.getListOfWishlistHouses().contains(mHouseId);
                    if (isInFavorite) {
                        mAddToFavoriteSave.setImageResource(R.drawable.wishlist_fill_blue_24);
                        mAddToFavorite.setImageResource(R.drawable.favorite_added_24);
                    } else {
                        mAddToFavoriteSave.setImageResource(R.drawable.wishlist_fill_24);
                        mAddToFavorite.setImageResource(R.drawable.favorite_24);
                    }
                }
            });
        }
    }

    private View.OnClickListener getAddToWishlistListener(View detailView) {
        if (mFirebaseAuth.getCurrentUser() == null) {
            return view -> {
                NavDirections navDirections = HouseDetailsFragmentDirections.actionHouseDetailsFragmentToWishlistGuestFragment();
                NavigationUtils.navigateSafe(mNavController, navDirections);
            };
        }

        return view -> {
            final CustomSnackbar snackbar = new com.kuro.androidutils.layout.CustomSnackbar(detailView, getLayoutInflater(), mainActivity.getNavLayout(), R.layout.snackbar_wishlist, R.id.item_saved_to_wishlist);

            mHouseViewModel.getHouse(mHouseId);
            House house = mHouseViewModel.getHouseMutableLiveData().getValue();
            if (house != null) {
                isInFavorite = !isInFavorite;
                mUserViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        if (isInFavorite) {
                            snackbar.setMessage(R.string.item_saved_to_wishlist);
                            mAddToFavoriteSave.setImageResource(R.drawable.wishlist_fill_blue_24);
                            mAddToFavorite.setImageResource(R.drawable.favorite_added_24);
                            mUserViewModel.updateUserWishlist(user, house.getHouseId());
                        } else {
                            snackbar.setMessage(R.string.item_removed_from_wishlist);
                            mAddToFavoriteSave.setImageResource(R.drawable.wishlist_fill_24);
                            mAddToFavorite.setImageResource(R.drawable.favorite_24);
                            mUserViewModel.removeHouseFromWishlist(user, house.getHouseId());
                        }
                        snackbar.show();
                    }
                });
                mUserViewModel.getUserMutableLiveData().removeObservers(getViewLifecycleOwner());
            }
        };
    }

    private Task<ShortDynamicLink> getDynamicLink() {

        String description = requireActivity().getString(R.string.default_dynamic_link_description);
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/yema-32964.appspot.com/o/houses%2FappartementF4.png?alt=media&token=8fcc18be-c1d8-458e-906a-c2591bef5a0f";
        String title = requireActivity().getString(R.string.default_dynamic_link_title);

        return FirebaseDynamicLinks.getInstance().createDynamicLink().setLink(Uri.parse("https://www.example.com/")).setDomainUriPrefix("https://yema.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle(title)
                        .setDescription(description)
                        .setImageUrl(Uri.parse(imageUrl))
                        .build())
                .buildShortDynamicLink();
    }

    @Override
    public View.OnClickListener onNavHomeClicked() {
        return view -> {
            NavDirections navDirections = HouseDetailsFragmentDirections.actionHouseDetailsFragmentToHomeFragment();
            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavSearchClicked() {
        return view -> {
            NavigationUtils.navigateSafe(mNavController, HouseDetailsFragmentDirections.actionHouseDetailsFragmentToSearchFragment());
        };

    }

    @Override
    public View.OnClickListener onNavWishlistClicked() {
        return view -> {
            NavDirections navDirections;
            if (mFirebaseAuth.getCurrentUser() != null) {
                navDirections = HouseDetailsFragmentDirections.actionHouseDetailsFragmentToWishlistFragment();
            } else {
                navDirections = HouseDetailsFragmentDirections.actionHouseDetailsFragmentToWishlistGuestFragment();
            }
            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavProfileClicked() {
        return view -> {
            NavDirections navDirections;
            if (mFirebaseAuth.getCurrentUser() != null) {
                navDirections = HouseDetailsFragmentDirections.actionHouseDetailsFragmentToProfileFragment();
            } else {
                navDirections = HouseDetailsFragmentDirections.actionHouseDetailsFragmentToProfileGuestFragment();
            }
            NavigationUtils.navigateSafe(mNavController, navDirections);
        };
    }
}
