package com.kuro.yema.views.fragments.main.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kuro.androidutils.layout.CustomSnackbar;
import com.kuro.androidutils.ui.NavigationUtils;
import com.kuro.yema.R;
import com.kuro.yema.data.enums.HouseType;
import com.kuro.yema.data.enums.ListingType;
import com.kuro.yema.data.model.Filter;
import com.kuro.yema.data.model.Search;
import com.kuro.androidutils.string.StringConvertor;
import com.kuro.androidutils.ui.SoftKeyboard;
import com.kuro.yema.views.MainActivity;
import com.kuro.yema.views.dialogs.ConfirmationDialogFragment;

import java.util.Arrays;

public class FilterFragment extends Fragment {
    private final float MIN_PRICE = 10_000, MAX_PRICE = 900_000;
    private NavController mNavController;
    private RangeSlider mPriceRangeSlider;
    private TextView mAveragePrice, mResetFilter;
    private MainActivity mMainActivity;
    private TextInputEditText mPriceMinEditText, mPriceMaxEditText, mAreaMinEditText, mAreaMaxEditText, mKeywordsEditText;
    private TextView mSelectedBedroomTextView, mSelectedBathroomTextView, mSelectedParkingSpotTextView;
    private TextView mAnyBedroomTextView, mOneBedroomTextView, mTwoBedroomTextView, mThreeBedroomTextView, mFourBedroomTextView, mFiveAndMoreBedroomTextView;
    private GridLayout mHouseTypeGridLayout;
    private TextView mAnyBathroomTextView, mOneBathroomTextView, mTwoBathroomTextView, mThreeBathroomTextView, mFourBathroomTextView, mFiveAndMoreBathroomTextView;
    private TextView mAnyParkingSpotTextView, mOneParkingSpotTextView, mTwoParkingSpotTextView, mThreeParkingSpotTextView, mFourParkingSpotTextView, mFiveAndMoreParkingSpotTextView;
    private RadioButton mForSaleButton, mForRentButton;
    private Button mApplyFilterButton;

    // values to send to the search fragment
    private Filter mFilter;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMainActivity = (MainActivity) requireActivity();

        FilterFragment.this.mMainActivity.getNavLayout().setVisibility(View.GONE);

        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View filterView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(filterView, savedInstanceState);

        mNavController = Navigation.findNavController(filterView);

        Search search = FilterFragmentArgs.fromBundle(getArguments()).getSearch();
        if (search != null) {
            mFilter = search.getFilter();
        }
        // initialize views
        initViews(filterView);

        if (mFilter == null) {
            mFilter = new Filter();
            mFilter.setPriceMin(MIN_PRICE);
            mFilter.setPriceMax(MAX_PRICE);
        }

        ImageButton mBackToSearch = filterView.findViewById(R.id.back_to_search);
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                FilterFragmentDirections.ActionFilterFragmentToSearchFragment action;
                action = FilterFragmentDirections.actionFilterFragmentToSearchFragment();
                action.setSearch(search);
                NavigationUtils.navigateSafe(mNavController, action);
            }
        });

        mApplyFilterButton.setOnClickListener(view -> {
            FilterFragmentDirections.ActionFilterFragmentToSearchFragment action;
            action = FilterFragmentDirections.actionFilterFragmentToSearchFragment();

            final CustomSnackbar snackbar = new com.kuro.androidutils.layout.CustomSnackbar(filterView, getLayoutInflater(), mMainActivity.getNavLayout(), R.layout.snackbar_wishlist, R.id.item_saved_to_wishlist);
            Filter filter = null;
            if (mFilter.isEmpty() && mFilter.getPriceMin() == MIN_PRICE && mFilter.getPriceMax() == MAX_PRICE) {
                snackbar.setMessage(R.string.no_filter_applied);
            } else {
                filter = mFilter;
                snackbar.setMessage(R.string.filter_applied);
            }
            if (search != null) {
                search.setFilter(filter);
                action.setSearch(search);
            } else {
                action.setSearch(new Search(filter));
            }

            NavigationUtils.navigateSafe(mNavController, action);
            snackbar.show();
        });
        mResetFilter.setOnClickListener(view -> {
            if (!mFilter.isEmpty() || (mFilter.getPriceMin() != MIN_PRICE || mFilter.getPriceMax() != MAX_PRICE)) {
                resetFilter();
            }
        });

        mBackToSearch.setOnClickListener(view -> {
            FilterFragmentDirections.ActionFilterFragmentToSearchFragment action;
            action = FilterFragmentDirections.actionFilterFragmentToSearchFragment();
            action.setSearch(search);
            NavigationUtils.navigateSafe(mNavController, action);
        });
    }

    private void initViews(View filterView) {
        mResetFilter = filterView.findViewById(R.id.reset_filter);

        mApplyFilterButton = filterView.findViewById(R.id.apply_filter);

        mForRentButton = filterView.findViewById(R.id.for_rent);
        mForSaleButton = filterView.findViewById(R.id.for_sale);

        mPriceMinEditText = filterView.findViewById(R.id.min_price);
        mPriceMaxEditText = filterView.findViewById(R.id.max_price);
        mPriceRangeSlider = filterView.findViewById(R.id.price_range_slider);
        mAveragePrice = filterView.findViewById(R.id.average_listing_price);

        TextInputLayout minPriceLayout = filterView.findViewById(R.id.min_price_layout);
        TextInputLayout maxPriceLayout = filterView.findViewById(R.id.max_price_layout);
        minPriceLayout.setPlaceholderText(StringConvertor.formatNumberToPrice(MIN_PRICE));
        maxPriceLayout.setPlaceholderText(StringConvertor.formatNumberToPrice(MAX_PRICE));

        mAnyBedroomTextView = filterView.findViewById(R.id.any_bedroom);
        mOneBedroomTextView = filterView.findViewById(R.id._1_bedroom);
        mTwoBedroomTextView = filterView.findViewById(R.id._2_bedroom);
        mThreeBedroomTextView = filterView.findViewById(R.id._3_bedroom);
        mFourBedroomTextView = filterView.findViewById(R.id._4_bedroom);
        mFiveAndMoreBedroomTextView = filterView.findViewById(R.id._5_bedroom);

        mAnyBathroomTextView = filterView.findViewById(R.id.any_bathroom);
        mOneBathroomTextView = filterView.findViewById(R.id._1_bathroom);
        mTwoBathroomTextView = filterView.findViewById(R.id._2_bathroom);
        mThreeBathroomTextView = filterView.findViewById(R.id._3_bathroom);
        mFourBathroomTextView = filterView.findViewById(R.id._4_bathroom);
        mFiveAndMoreBathroomTextView = filterView.findViewById(R.id._5_bathroom);

        mAnyParkingSpotTextView = filterView.findViewById(R.id.any_parking);
        mOneParkingSpotTextView = filterView.findViewById(R.id._1_parking);
        mTwoParkingSpotTextView = filterView.findViewById(R.id._2_parking);
        mThreeParkingSpotTextView = filterView.findViewById(R.id._3_parking);
        mFourParkingSpotTextView = filterView.findViewById(R.id._4_parking);
        mFiveAndMoreParkingSpotTextView = filterView.findViewById(R.id._5_parking);

        mAreaMinEditText = filterView.findViewById(R.id.min_area);
        mAreaMaxEditText = filterView.findViewById(R.id.max_area);

        mHouseTypeGridLayout = filterView.findViewById(R.id.house_type_grid);
        mKeywordsEditText = filterView.findViewById(R.id.keywords);

        // hiding the keyboard on focus lost
        View[] focusableViews = {mPriceMinEditText, mPriceMaxEditText, mAreaMinEditText, mAreaMaxEditText, mKeywordsEditText};
        if (filterView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) filterView).getChildCount(); i++) {
                View innerView = ((ViewGroup) filterView).getChildAt(i);
                if (!(innerView instanceof TextInputEditText)) {
                    innerView.setOnTouchListener(SoftKeyboard.hideSoftKeyboardOnTouchEvent(focusableViews, requireContext()));
                }
            }
        }
        // saving the listing type of the house
        getSelectedListingType();

        // saving the price range
        getSelectedPriceRange();

        // saving the house types selected by the user
        getSelectedHouseTypes();

        // saving the listing type of the house
        getSelectedNumberOfBedrooms();
        getSelectedNumberOfBathrooms();
        getSelectedNumberOfParkingSpot();

        // saving the area range
        getSelectedLivingAreaRange();

        // saving the keywords
        getSelectedKeywords();

        // retrieve previous filters and set them back onto the ui
        if (mFilter != null) {
            // price
            mPriceMinEditText.setText(StringConvertor.formatNumberToPrice(mFilter.getPriceMin()));
            mPriceMaxEditText.setText(StringConvertor.formatNumberToPrice(mFilter.getPriceMax()));

            float minPrice = mFilter.getPriceMin(), maxPrice = mFilter.getPriceMax();

            if (minPrice < MIN_PRICE || minPrice > MAX_PRICE) {
                minPrice = MIN_PRICE;
            }
            if (maxPrice < MIN_PRICE || maxPrice > MAX_PRICE) {
                maxPrice = MAX_PRICE;
            }
            mPriceRangeSlider.setValues(minPrice, maxPrice);


            if (mFilter.getListingType() != null) {
                switch (mFilter.getListingType()) {
                    case RENT:
                        mForRentButton.setChecked(true);
                        break;
                    case SALE:
                        mForSaleButton.setChecked(true);
                }
            }
            if (mFilter.getNumberBedrooms() > 0) {
                switch (mFilter.getNumberBedrooms()) {
                    case 1:
                        mSelectedBedroomTextView = mOneBedroomTextView;
                        break;
                    case 2:
                        mSelectedBedroomTextView = mTwoBedroomTextView;
                        break;
                    case 3:
                        mSelectedBedroomTextView = mThreeBedroomTextView;
                        break;
                    case 4:
                        mSelectedBedroomTextView = mFourBedroomTextView;
                        break;
                    case 5:
                        mSelectedBedroomTextView = mFiveAndMoreBedroomTextView;
                        break;
                }
                mSelectedBedroomTextView = updateSelectionOf(mSelectedBedroomTextView, mAnyBedroomTextView, TypeItem.BEDROOM);

            }
            if (mFilter.getNumberBathrooms() > 0) {
                switch (mFilter.getNumberBathrooms()) {
                    case 1:
                        mSelectedBathroomTextView = mOneBathroomTextView;
                        break;
                    case 2:
                        mSelectedBathroomTextView = mTwoBathroomTextView;
                        break;
                    case 3:
                        mSelectedBathroomTextView = mThreeBathroomTextView;
                        break;
                    case 4:
                        mSelectedBathroomTextView = mFourBathroomTextView;
                        break;
                    case 5:
                        mSelectedBathroomTextView = mFiveAndMoreBathroomTextView;
                        break;
                }
                mSelectedBathroomTextView = updateSelectionOf(mSelectedBathroomTextView, mAnyBathroomTextView, TypeItem.BATHROOM);
            }
            if (mFilter.getNumberParkingSpot() > 0) {
                switch (mFilter.getNumberParkingSpot()) {
                    case 1:
                        mSelectedParkingSpotTextView = mOneParkingSpotTextView;
                        break;
                    case 2:
                        mSelectedParkingSpotTextView = mTwoParkingSpotTextView;
                        break;
                    case 3:
                        mSelectedParkingSpotTextView = mThreeParkingSpotTextView;
                        break;
                    case 4:
                        mSelectedParkingSpotTextView = mFourParkingSpotTextView;
                        break;
                    case 5:
                        mSelectedParkingSpotTextView = mFiveAndMoreParkingSpotTextView;
                        break;
                }
                mSelectedParkingSpotTextView = updateSelectionOf(mSelectedParkingSpotTextView, mAnyParkingSpotTextView, TypeItem.PARKING);
            }
            if (mFilter.getLivingAreaMin() > 0) {
                mAreaMinEditText.setText(String.valueOf(mFilter.getLivingAreaMin()));
            }
            if (mFilter.getLivingAreaMax() > 0) {
                mAreaMaxEditText.setText(String.valueOf(mFilter.getLivingAreaMax()));
            }
            if (mFilter.getKeywords().length > 0) {
                mKeywordsEditText.setText(Arrays.toString(mFilter.getKeywords()));
            }
        }

    }

    private void resetFilter() {
        // add confirmation dialog
        ConfirmationDialogFragment dialogFragment = new ConfirmationDialogFragment(R.string.reset_filter, R.string.yes_filter);
        dialogFragment.setOnConfirmationListener(view -> {

            mFilter = new Filter();
            mFilter.setPriceMin(MIN_PRICE);
            mFilter.setPriceMax(MAX_PRICE);

            // resetting the UI
            mForSaleButton.setChecked(false);
            mForRentButton.setChecked(false);

            mPriceMinEditText.setText("");
            mPriceMinEditText.clearFocus();
            mPriceMaxEditText.setText("");
            mPriceMaxEditText.clearFocus();

            mPriceRangeSlider.setValueFrom(MIN_PRICE);
            mPriceRangeSlider.setValueTo(MAX_PRICE);
            mPriceRangeSlider.setValues(MIN_PRICE, MAX_PRICE);
            mPriceRangeSlider.clearFocus();

            mHouseTypeGridLayout.removeAllViews();
            getSelectedHouseTypes();

            mSelectedBedroomTextView = resetSelectionOf(mSelectedBedroomTextView, TypeItem.BEDROOM);
            mSelectedBathroomTextView = resetSelectionOf(mSelectedBathroomTextView, TypeItem.BATHROOM);
            mSelectedParkingSpotTextView = resetSelectionOf(mSelectedParkingSpotTextView, TypeItem.PARKING);

            mAreaMinEditText.setText("");
            mAreaMinEditText.clearFocus();
            mAreaMaxEditText.setText("");
            mAreaMaxEditText.clearFocus();

            mKeywordsEditText.setText("");
            mKeywordsEditText.clearFocus();
            dialogFragment.dismiss();
        });
        dialogFragment.show(getChildFragmentManager(), "DIALOG");
        // resetting the values sent to search fragment (the filter object)
    }

    private void getSelectedListingType() {
        mForRentButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                mFilter.setListingType(ListingType.RENT);
            }
        });

        mForSaleButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                mFilter.setListingType(ListingType.SALE);
            }
        });
    }

    private void getSelectedPriceRange() {

        mPriceRangeSlider.setValueFrom(MIN_PRICE);
        mPriceRangeSlider.setValueTo(MAX_PRICE);
        mPriceRangeSlider.setValues(MIN_PRICE, MAX_PRICE);


        String averageText = getString(R.string.average_listing_price);
        int averagePrice = (int) ((MAX_PRICE + MIN_PRICE) / 2);
        mAveragePrice.setText(String.format(averageText, averagePrice, "CFA"));

//        // TODO : remove later if not needed by the client
//        Spinner spinner = filterView.findViewById(R.id.currency_spinner);
//        String[] choices = {"€", "CFA", "$"}; // Array of choices
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, choices);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                mCurrency = adapterView.getItemAtPosition(i).toString();
//                mAveragePrice.setText(String.format(averageText, averagePrice, mCurrency));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//
//        });


        // TODO: fix this  slider value greater error
        mPriceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            float minPrice = slider.getValues().get(0);
            float maxPrice = slider.getValues().get(1);

            if (fromUser) {
                mFilter.setPriceMin(minPrice);
                mFilter.setPriceMax(maxPrice);
                mPriceMinEditText.setText(StringConvertor.formatNumberToPrice(minPrice));
                mPriceMaxEditText.setText(StringConvertor.formatNumberToPrice(maxPrice));
            }
        });

        mPriceMinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = String.valueOf(editable);
                float price = mFilter.getPriceMin();

                if (!value.isEmpty()) {
                    try {
                        price = Float.parseFloat(value);
                        mFilter.setPriceMin(price);
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (mPriceMinEditText.hasFocus()) {
                    float minPrice = MIN_PRICE;
                    float maxPrice = MAX_PRICE;

                    if (price < MIN_PRICE) {
                        maxPrice = (mFilter.getPriceMax() < MIN_PRICE) ? MIN_PRICE : Math.min(mFilter.getPriceMax(), MAX_PRICE);
                    } else if (price <= MAX_PRICE) {
                        minPrice = price;
                        maxPrice = (mFilter.getPriceMax() <= price) ? price : Math.min(mFilter.getPriceMax(), MAX_PRICE);
                    } else {
                        minPrice = MAX_PRICE;
                    }
                    mPriceRangeSlider.setValues(minPrice, maxPrice);
                }
            }
        });
        mPriceMaxEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = String.valueOf(editable);
                float price = mFilter.getPriceMax();

                if (!value.isEmpty()) {
                    try {
                        price = Float.parseFloat(value);
                        mFilter.setPriceMax(price);
                    } catch (NumberFormatException ignored) {
                    }
                }

                if (mPriceMaxEditText.hasFocus()) {
                    float minPrice = MIN_PRICE;
                    float maxPrice = MAX_PRICE;

                    if (price > MAX_PRICE) {
                        minPrice = (mFilter.getPriceMin() > MAX_PRICE) ? MAX_PRICE : Math.max(mFilter.getPriceMin(), MIN_PRICE);
                    } else if (price >= MIN_PRICE) {
                        maxPrice = price;
                        minPrice = (mFilter.getPriceMin() >= price) ? price : Math.max(mFilter.getPriceMin(), MIN_PRICE);
                    } else {
                        maxPrice = MIN_PRICE;
                    }
                    mPriceRangeSlider.setValues(minPrice, maxPrice);
                }
            }
        });

        mPriceMinEditText.setOnFocusChangeListener((view, hasFocus) -> {
            float value = mFilter.getPriceMin();
            if (!hasFocus) {
                mPriceMinEditText.setText(StringConvertor.formatNumberToPrice(value));
            } else {
                if (mFilter.getPriceMin() > mFilter.getPriceMax()) {
                    value = mFilter.getPriceMax();
                }
                mPriceMinEditText.setText(String.valueOf(value));
            }
        });
        mPriceMaxEditText.setOnFocusChangeListener((view, hasFocus) -> {
            float value = mFilter.getPriceMax();
            if (!hasFocus) {
                mPriceMaxEditText.setText(StringConvertor.formatNumberToPrice(value));
            } else {
                mPriceMaxEditText.setText(String.valueOf(value));
            }
        });
    }

    private void getSelectedHouseTypes() {

        HouseType[] types = HouseType.values();
        for (int i = 0; i < types.length; i++) {
            HouseType current = types[i];
            MaterialCheckBox checkBox = new MaterialCheckBox(requireContext());
            checkBox.setText(current.type);
            if (mFilter != null && mFilter.getHouseTypes() != null && mFilter.getHouseTypes().contains(current)) {
                checkBox.setChecked(true);
            }

            if (i % 2 != 0) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(200, 0, 0, 0); // left, top, right, bottom
                checkBox.setLayoutParams(params);
            }

            mHouseTypeGridLayout.addView(checkBox);
            checkBox.addOnCheckedStateChangedListener((checkBox1, state) -> {
                if (checkBox1.isChecked()) {
                    mFilter.addHouseTypes(current);
                } else {
                    mFilter.removeHouseType(current);
                }
            });
        }
    }

    private void getSelectedNumberOfBedrooms() {
        mSelectedBedroomTextView = mAnyBedroomTextView;

        View.OnClickListener onClickListener = view -> {
            if (!view.equals(mSelectedBedroomTextView)) {
                mSelectedBedroomTextView = updateSelectionOf((TextView) view, mSelectedBedroomTextView, TypeItem.BEDROOM);
            }
        };
        mAnyBedroomTextView.setOnClickListener(onClickListener);
        mOneBedroomTextView.setOnClickListener(onClickListener);
        mTwoBedroomTextView.setOnClickListener(onClickListener);
        mThreeBedroomTextView.setOnClickListener(onClickListener);
        mFourBedroomTextView.setOnClickListener(onClickListener);
        mFiveAndMoreBedroomTextView.setOnClickListener(onClickListener);
    }

    private void getSelectedNumberOfBathrooms() {
        mSelectedBathroomTextView = mAnyBathroomTextView;

        View.OnClickListener onClickListener = view -> {
            if (!view.equals(mSelectedBathroomTextView)) {
                mSelectedBathroomTextView = updateSelectionOf((TextView) view, mSelectedBathroomTextView, TypeItem.BATHROOM);
            }
        };
        mAnyBathroomTextView.setOnClickListener(onClickListener);
        mOneBathroomTextView.setOnClickListener(onClickListener);
        mTwoBathroomTextView.setOnClickListener(onClickListener);
        mThreeBathroomTextView.setOnClickListener(onClickListener);
        mFourBathroomTextView.setOnClickListener(onClickListener);
        mFiveAndMoreBathroomTextView.setOnClickListener(onClickListener);
    }

    private void getSelectedNumberOfParkingSpot() {
        mSelectedParkingSpotTextView = mAnyParkingSpotTextView;

        View.OnClickListener onClickListener = view -> {
            if (!view.equals(mSelectedParkingSpotTextView)) {
                mSelectedParkingSpotTextView = updateSelectionOf((TextView) view, mSelectedParkingSpotTextView, TypeItem.PARKING);
            }
        };

        mAnyParkingSpotTextView.setOnClickListener(onClickListener);
        mOneParkingSpotTextView.setOnClickListener(onClickListener);
        mTwoParkingSpotTextView.setOnClickListener(onClickListener);
        mThreeParkingSpotTextView.setOnClickListener(onClickListener);
        mFourParkingSpotTextView.setOnClickListener(onClickListener);
        mFiveAndMoreParkingSpotTextView.setOnClickListener(onClickListener);
    }

    private void getSelectedLivingAreaRange() {
        mAreaMinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = String.valueOf(editable);
                if (!value.isEmpty()) {
                    mFilter.setLivingAreaMin(Float.parseFloat(value));
                }
            }
        });
        mAreaMaxEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = String.valueOf(editable);
                if (!value.isEmpty()) {
                    mFilter.setLivingAreaMax(Float.parseFloat(value));
                }
            }
        });
    }

    private void getSelectedKeywords() {
        mKeywordsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = String.valueOf(editable).replaceFirst("\\s+", "").replaceAll(",\\s+", ",");
                if (!value.isEmpty()) {
                    mFilter.setKeywords(value.split(","));
                }
            }
        });
    }

    private TextView updateSelectionOf(TextView viewClicked, TextView previousSelectedItem, TypeItem typeOfItem) {
        previousSelectedItem.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.unselected_square));
        previousSelectedItem.setTextColor(getResources().getColor(R.color.light_black, null));

        viewClicked.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.selected_square));
        viewClicked.setTextColor(getResources().getColor(R.color.white, null));

        previousSelectedItem = viewClicked;
        switch (typeOfItem) {
            case BEDROOM:
                if (previousSelectedItem.equals(mAnyBedroomTextView)) {
                    mFilter.setNumberBedrooms(0);
                } else if (previousSelectedItem.equals(mFiveAndMoreBedroomTextView)) {
                    mFilter.setNumberBedrooms(5);
                } else {
                    mFilter.setNumberBedrooms(Integer.parseInt(previousSelectedItem.getText().toString()));
                }
                break;
            case BATHROOM:
                if (previousSelectedItem.equals(mAnyBathroomTextView)) {
                    mFilter.setNumberBathrooms(0);
                } else if (previousSelectedItem.equals(mFiveAndMoreBathroomTextView)) {
                    mFilter.setNumberBathrooms(5);
                } else {
                    mFilter.setNumberBathrooms(Integer.parseInt(previousSelectedItem.getText().toString()));
                }
                break;
            case PARKING:
                if (previousSelectedItem.equals(mAnyParkingSpotTextView)) {
                    mFilter.setNumberParkingSpot(0);
                } else if (previousSelectedItem.equals(mFiveAndMoreParkingSpotTextView)) {
                    mFilter.setNumberParkingSpot(5);
                } else {
                    mFilter.setNumberParkingSpot(Integer.parseInt(previousSelectedItem.getText().toString()));
                }
                break;
        }
        return previousSelectedItem;
    }

    private TextView resetSelectionOf(TextView prevSelectedItem, TypeItem typeOfItem) {
        prevSelectedItem.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.unselected_square));
        prevSelectedItem.setTextColor(getResources().getColor(R.color.light_black, null));
        TextView defaultSelectedItem;


        switch (typeOfItem) {
            case BATHROOM:
                mFilter.setNumberBathrooms(0);
                defaultSelectedItem = mAnyBathroomTextView;
                break;
            case BEDROOM:
                mFilter.setNumberParkingSpot(0);
                defaultSelectedItem = mAnyParkingSpotTextView;
                break;
            default:
                mFilter.setNumberBedrooms(0);
                defaultSelectedItem = mAnyBedroomTextView;
                break;

        }
        defaultSelectedItem.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.selected_square));
        defaultSelectedItem.setTextColor(getResources().getColor(R.color.white, null));

        return defaultSelectedItem;
    }

    private enum TypeItem {
        BEDROOM,
        BATHROOM,
        PARKING,
    }
}
