package com.kuro.yema.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kuro.androidutils.layout.CustomSnackbar;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yema.R;
import com.kuro.yema.data.model.House;
import com.kuro.yema.data.model.User;
import com.kuro.yema.viewModels.UserViewModel;
import com.kuro.yema.views.fragments.main.home.HomeFragmentDirections;
import com.kuro.yema.views.fragments.main.search.SearchFragmentDirections;

import java.util.ArrayList;
import java.util.List;

public class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.ViewHolder> {
    private final NavController mNavController;
    private final Context mContext;
    private final LinearLayout mNavLayout;
    private final UserViewModel mUserViewModel;
    private final LayoutInflater mLayoutInflater;
    private boolean isFromSearchFragment;
    private boolean isFromHomeFragment;
    private ArrayList<String> mHouseIds = new ArrayList<>();
    private List<House> mListOfHouses;


    public HouseAdapter(NavController mNavController, Context context, UserViewModel userViewModel, LayoutInflater layoutInflater, LinearLayout navLayout) {

        this.mNavController = mNavController;
        this.mContext = context;
        this.mUserViewModel = userViewModel;
        this.mLayoutInflater = layoutInflater;
        this.mNavLayout = navLayout;
    }

    public void setFromSearchFragment() {
        isFromSearchFragment = true;
        isFromHomeFragment = false;
    }

    public void setFromHomeFragment() {
        isFromSearchFragment = false;
        isFromHomeFragment = true;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setHouseIds(ArrayList<String> mHouseIds) {
        this.mHouseIds = mHouseIds;
        notifyDataSetChanged();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListOfHouses(List<House> mListOfHouses) {
        this.mListOfHouses = mListOfHouses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_house, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseAdapter.ViewHolder holder, int position) {
        House house = mListOfHouses.get(position);
        String title = house.getTitle();
        if (!house.getLocation().isEmpty()) {
            title += " at " + house.getLocation();
        }

        holder.mHouseItemTitle.setText(title);
        holder.mHouseItemPrice.setText(String.valueOf(house.getPrice()));
        // TODO: use Glide to show images
        Glide.with(holder.itemView).load(house.getImages().get(0))
//              .placeholder(R.drawable.template_house_item)
                .into(holder.mHouseItemImage);
        holder.mHouseItemImageTotal.setText(String.valueOf(house.getNumberOfImages()));
        int i = 0;
        if (house.getNumberOfImages() > 0) {
            i = 1;
        }
        holder.mHouseItemImageIndex.setText(String.valueOf(i));
        holder.mHouseItemPriceCurrency.setText(house.getPriceCurrency().name());
        String rentalTerm = mContext.getString(R.string.house_rental_term);

        holder.mHouseItemRentalTerm.setText(String.format(rentalTerm, house.getRentalTerm().name().toLowerCase()));

        View.OnClickListener onClickListener = view -> {
            if (isFromHomeFragment) {
                HomeFragmentDirections.ActionHomeFragmentToHouseDetailsFragment action;
                action = HomeFragmentDirections.actionHomeFragmentToHouseDetailsFragment();
                action.setHouseId(house.getHouseId());
                NavigationUtils.navigateSafe(mNavController, action);
            } else if (isFromSearchFragment) {
                SearchFragmentDirections.ActionSearchFragmentToHouseDetailsFragment action;
                action = SearchFragmentDirections.actionSearchFragmentToHouseDetailsFragment();
                action.setHouseId(house.getHouseId());
                NavigationUtils.navigateSafe(mNavController, action);
            }
        };

        holder.mHouseItemImage.setOnClickListener(onClickListener);
        holder.mHouseItemTitle.setOnClickListener(onClickListener);

        if (mHouseIds.contains(house.getHouseId())) {
            house.setInFavorite(true);
        }

        if (house.isInFavorite()) {
            holder.mHouseItemAddToFavorite.setImageResource(R.drawable.favorite_added_24);
        } else {
            holder.mHouseItemAddToFavorite.setImageResource(R.drawable.favorite_24);
        }
        holder.mHouseItemAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUserViewModel.getFirebaseUser() != null) {
                    // create the snackbar
                    final CustomSnackbar snackbar = new CustomSnackbar(holder.itemView, mLayoutInflater, mNavLayout, R.layout.snackbar_wishlist, R.id.item_saved_to_wishlist);

                    house.setInFavorite(!house.isInFavorite());
                    // get the current user and its information
                    User user = mUserViewModel.getUserMutableLiveData().getValue();

                    if (house.isInFavorite()) {
                        snackbar.setMessage(R.string.item_saved_to_wishlist);
                        holder.mHouseItemAddToFavorite.setImageResource(R.drawable.favorite_added_24);
                        mUserViewModel.updateUserWishlist(user, house.getHouseId());
                    } else {
                        snackbar.setMessage(R.string.item_removed_from_wishlist);
                        holder.mHouseItemAddToFavorite.setImageResource(R.drawable.favorite_24);
                        mUserViewModel.removeHouseFromWishlist(user, house.getHouseId());
                    }
                    snackbar.show();
                } else {
                    NavDirections action = HomeFragmentDirections.actionHomeFragmentToWishlistGuestFragment();
                    NavigationUtils.navigateSafe(mNavController, action);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListOfHouses == null) return 0;
        return mListOfHouses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mHouseItemImage;
        private final TextView mHouseItemTitle, mHouseItemPrice, mHouseItemImageTotal, mHouseItemImageIndex, mHouseItemPriceCurrency, mHouseItemRentalTerm;
        private final ImageButton mHouseItemAddToFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mHouseItemImage = itemView.findViewById(R.id.detail_house_image);
            mHouseItemTitle = itemView.findViewById(R.id.house_item_title);
            mHouseItemPrice = itemView.findViewById(R.id.house_item_price);
            mHouseItemImageTotal = itemView.findViewById(R.id.house_item_image_total);
            mHouseItemImageIndex = itemView.findViewById(R.id.house_item_image_index);
            mHouseItemPriceCurrency = itemView.findViewById(R.id.house_item_price_currency);
            mHouseItemRentalTerm = itemView.findViewById(R.id.house_item_rental_term);
            mHouseItemAddToFavorite = itemView.findViewById(R.id.house_item_favorite_image_btn);
        }
    }

}
