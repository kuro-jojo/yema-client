package com.kuro.yema.views.fragments.main.wishlist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.kuro.androidutils.layout.CustomSnackbar;
import com.kuro.yema.R;
import com.kuro.yema.adapters.WishlistAdapter;
import com.kuro.yema.data.model.House;
import com.kuro.yema.data.model.User;
import com.kuro.androidutils.string.CustomSpannableString;
import com.kuro.androidutils.ui.NavigationUtils;import com.kuro.yema.viewModels.HouseViewModel;
import com.kuro.yema.viewModels.UserViewModel;
import com.kuro.yema.views.MainActivity;
import com.kuro.yema.views.dialogs.ConfirmationDialogFragment;
import com.kuro.yema.views.fragments.main.MainFragmentInterface;

import java.util.ArrayList;
import java.util.Objects;

public class WishlistFragment extends Fragment implements MainFragmentInterface {
    private NavController mNavController;
    private UserViewModel mUserViewModel;
    private HouseViewModel mHouseViewModel;
    private ImageButton mRemoveWishlist, mBackWishlist;
    private MainActivity mainActivity;
    private TextView mEditWishlist, mEmptyWishlist;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private boolean isEditWishlistActivated = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mUserViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(UserViewModel.class);
        mHouseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(HouseViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View wishlistView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(wishlistView, savedInstanceState);

        mNavController = Navigation.findNavController(wishlistView);

        mainActivity = (MainActivity) requireActivity();

        mainActivity.getNavLayout().setVisibility(View.VISIBLE);
        mainActivity.setNavHomeClickListener(onNavHomeClicked());
        mainActivity.setNavSearchClickListener(onNavSearchClicked());
        mainActivity.setNavWishlistClickListener(onNavWishlistClicked());
        mainActivity.setNavProfileClickListener(onNavProfileClicked());

        // set the view as active tab
        mainActivity.setActiveNavigationItem(MainActivity.NavigationItems.WISHLIST);

        mRecyclerView = wishlistView.findViewById(R.id.wishlist_recycler_view);
        mRemoveWishlist = wishlistView.findViewById(R.id.remove_wishlist);
        mEditWishlist = wishlistView.findViewById(R.id.edit_wishlist);
        mEmptyWishlist = wishlistView.findViewById(R.id.empty_wishlist);
        mBackWishlist = wishlistView.findViewById(R.id.back_to_normal_mode);
        mProgressBar = wishlistView.findViewById(R.id.wishlist_progressbar);

        WishlistAdapter wishlistAdapter = new WishlistAdapter(mNavController, requireContext());
        mRecyclerView.setAdapter(wishlistAdapter);

        mUserViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                setWishlistItems(user, wishlistAdapter);
            }
        });

        mBackWishlist.setOnClickListener(view -> {
            isEditWishlistActivated = !isEditWishlistActivated;
            wishlistAdapter.setEditWishlistActivated(isEditWishlistActivated);
            mEditWishlist.setVisibility(View.VISIBLE);
            mRemoveWishlist.setVisibility(View.GONE);
            mBackWishlist.setVisibility(View.INVISIBLE);
        });
        mRemoveWishlist.setOnClickListener(view -> {
            ConfirmationDialogFragment confirmationDialogFragment;
            if (!wishlistAdapter.getListOfSelectedHouses().isEmpty()) {
                confirmationDialogFragment = new ConfirmationDialogFragment(R.string.confirm_remove_from_wishlist, R.string.yes_delete);

                @SuppressLint("NotifyDataSetChanged") View.OnClickListener onConfirmationListener = view1 -> {
                    for (House house : wishlistAdapter.getListOfSelectedHouses()) {
                        mUserViewModel.removeHouseFromWishlist(mUserViewModel.getUserMutableLiveData().getValue(), house.getHouseId());
                    }

                    setWishlistItems(Objects.requireNonNull(mUserViewModel.getUserMutableLiveData().getValue()), wishlistAdapter);
                    wishlistAdapter.notifyDataSetChanged();

                    final CustomSnackbar snackbar = new com.kuro.androidutils.layout.CustomSnackbar(wishlistView, getLayoutInflater(), mainActivity.getNavLayout(), R.layout.snackbar_wishlist, R.id.item_saved_to_wishlist);
                    snackbar.setMessage(R.string.items_removed_from_wishlist);
                    snackbar.show();

                    isEditWishlistActivated = !isEditWishlistActivated;
                    wishlistAdapter.setEditWishlistActivated(isEditWishlistActivated);
                    mEditWishlist.setVisibility(View.VISIBLE);
                    mRemoveWishlist.setVisibility(View.GONE);
                    mBackWishlist.setVisibility(View.INVISIBLE);
                    confirmationDialogFragment.dismiss();
                };
                confirmationDialogFragment.setOnConfirmationListener(onConfirmationListener);
                confirmationDialogFragment.show(getChildFragmentManager(), "DIALOG");
            }
        });

        mEditWishlist.setOnClickListener(view -> {
            if (wishlistAdapter.getListOfHouses() != null && !wishlistAdapter.getListOfHouses().isEmpty()) {
                isEditWishlistActivated = !isEditWishlistActivated;
                wishlistAdapter.setEditWishlistActivated(isEditWishlistActivated);
                mEditWishlist.setVisibility(View.GONE);
                mRemoveWishlist.setVisibility(View.VISIBLE);
                mBackWishlist.setVisibility(View.VISIBLE);
            }
        });

        CustomSpannableString.addNonClickableSpannableString(mEditWishlist, mEditWishlist.getText().toString(), new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                // Toggle edit mode
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(requireContext().getColor(R.color.light_black));
                ds.setUnderlineText(true);
            }
        });

    }

    private void setWishlistItems(User user, WishlistAdapter wishlistAdapter) {
        ArrayList<String> houseIds = user.getListOfWishlistHouses();

        if (houseIds != null && !houseIds.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyWishlist.setVisibility(View.GONE);
            mHouseViewModel.getHousesInListOfIds(houseIds);
            mHouseViewModel.getHouseListMutableLiveData().observe(getViewLifecycleOwner(), houses -> {
                mProgressBar.setVisibility(View.GONE);
                wishlistAdapter.setListOfHouses((ArrayList<House>) houses);
            });
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyWishlist.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public View.OnClickListener onNavHomeClicked() {
        return view -> {
            NavDirections navDirections = WishlistFragmentDirections.actionWishlistFragmentToHomeFragment();
            NavigationUtils.navigateSafe(mNavController,navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavSearchClicked() {
        return view -> NavigationUtils.navigateSafe(mNavController,WishlistFragmentDirections.actionWishlistFragmentToSearchFragment());

    }

    @Override
    public View.OnClickListener onNavWishlistClicked() {
        return view -> {
        };
    }

    @Override
    public View.OnClickListener onNavProfileClicked() {
        return view -> {
            NavDirections navDirections = WishlistFragmentDirections.actionWishlistFragmentToProfileFragment();
            NavigationUtils.navigateSafe(mNavController,navDirections);
        };
    }
}
