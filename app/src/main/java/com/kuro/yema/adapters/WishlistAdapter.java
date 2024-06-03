package com.kuro.yema.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kuro.yema.R;
import com.kuro.yema.data.model.House;
import com.kuro.androidutils.ui.NavigationUtils;import com.kuro.yema.views.fragments.main.wishlist.WishlistFragmentDirections;

import java.util.ArrayList;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private final Context mContext;
    private final NavController mNavController;
    private boolean isEditWishlistActivated = false;
    private ArrayList<House> mListOfHouses;
    private ArrayList<House> mListOfSelectedHouses = new ArrayList<>();

    public WishlistAdapter(NavController controller, Context context) {
        mNavController = controller;
        mContext = context;
    }

    public ArrayList<House> getListOfHouses() {
        return mListOfHouses;
    }

    public ArrayList<House> getListOfSelectedHouses() {
        return mListOfSelectedHouses;
    }


    public void setEditWishlistActivated(boolean editWishlistActivated) {
        isEditWishlistActivated = editWishlistActivated;
        notifyDataSetChanged();
    }

    public void setListOfHouses(ArrayList<House> mListOfHouses) {
        this.mListOfHouses = mListOfHouses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WishlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false);

        return new WishlistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.ViewHolder holder, int position) {
        House house = mListOfHouses.get(position);
        String title = house.getTitle();

        if (isEditWishlistActivated) {
            holder.mEditCheckBox.setVisibility(View.VISIBLE);
            holder.mWishlistLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mEditCheckBox.setChecked(!holder.mEditCheckBox.isChecked());
                    if (holder.mEditCheckBox.isChecked()) {
                        mListOfSelectedHouses.add(house);
                    } else {
                        mListOfSelectedHouses.remove(house);
                    }
                }
            });
        } else {
            holder.mEditCheckBox.setChecked(false);
            holder.mEditCheckBox.setVisibility(View.GONE);
            holder.mWishlistLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavDirections action = WishlistFragmentDirections.actionWishlistFragmentToHouseDetailsFragment(house.getHouseId());
                    NavigationUtils.navigateSafe(mNavController,action);
                }
            });
        }
        if (!house.getLocation().isEmpty()) {
            title += " at " + house.getLocation();
        }
        holder.mHouseTitle.setText(title);
        holder.mHousePrice.setText(String.valueOf(house.getPrice()));
        // TODO: use Glide to show images
        Glide.with(holder.itemView).load(house.getImages().get(0))
//                .placeholder(R.drawable.template_house_item)
                .into(holder.mHouseImage);
        holder.mHousePriceCurrency.setText(house.getPriceCurrency().name());
        String rentalTerm = mContext.getString(R.string.house_rental_term);

        holder.mHouseRentalTerm.setText(String.format(rentalTerm, house.getRentalTerm().name().toLowerCase()));
    }

    @Override
    public int getItemCount() {
        if (mListOfHouses == null) return 0;
        return mListOfHouses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mHouseTitle, mHousePrice, mHousePriceCurrency, mHouseRentalTerm;
        private final ImageView mHouseImage;
        private final LinearLayout mWishlistLinearLayout;
        private CheckBox mEditCheckBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mEditCheckBox = itemView.findViewById(R.id.checkbox);
            mHouseTitle = itemView.findViewById(R.id.wishlist_item_title);
            mHousePrice = itemView.findViewById(R.id.wishlist_item_price);
            mHousePriceCurrency = itemView.findViewById(R.id.wishlist_item_price_currency);
            mHouseRentalTerm = itemView.findViewById(R.id.wishlist_item_rental_term);
            mHouseImage = itemView.findViewById(R.id.wishlist_item_image);
            mWishlistLinearLayout = itemView.findViewById(R.id.wishlist_linear_layout);
        }
    }
}
