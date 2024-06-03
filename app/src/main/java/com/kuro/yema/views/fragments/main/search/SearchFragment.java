package com.kuro.yema.views.fragments.main.search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.kuro.yema.R;
import com.kuro.yema.adapters.HouseAdapter;
import com.kuro.yema.data.enums.HouseType;
import com.kuro.yema.data.model.Filter;
import com.kuro.yema.data.model.Search;
import com.kuro.androidutils.string.StringConvertor;
import com.kuro.androidutils.ui.NavigationUtils;import com.kuro.androidutils.ui.SoftKeyboard;
import com.kuro.yema.viewModels.SearchViewModel;
import com.kuro.yema.viewModels.UserViewModel;
import com.kuro.yema.views.MainActivity;
import com.kuro.yema.views.fragments.main.MainFragmentInterface;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements MainFragmentInterface {

    private NavController mNavController;
    private FirebaseAuth mAuth;
    private UserViewModel mUserViewModel;
    private SearchViewModel mSearchViewModel;
    private TextInputEditText mBudgetEditText;
    private TextView mSearchResultTextView;
    private ProgressBar mProgressBar;

    private static boolean isNumeric(String str) {
        // Use regular expression to check if the string contains only digits
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mUserViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(UserViewModel.class);

        SearchViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mSearchViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View searchView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(searchView, savedInstanceState);
        MainActivity mainActivity = (MainActivity) requireActivity();

        mainActivity.getNavLayout().setVisibility(View.VISIBLE);
        Search searchQuery = FilterFragmentArgs.fromBundle(getArguments()).getSearch();

        mainActivity.setNavHomeClickListener(onNavHomeClicked());
        mainActivity.setNavSearchClickListener(onNavSearchClicked());
        mainActivity.setNavWishlistClickListener(onNavWishlistClicked());
        mainActivity.setNavProfileClickListener(onNavProfileClicked());

        // set the view as active tab
        mainActivity.setActiveNavigationItem(MainActivity.NavigationItems.SEARCH);


        mNavController = Navigation.findNavController(searchView);
        mAuth = FirebaseAuth.getInstance();

        RecyclerView mRecyclerView = searchView.findViewById(R.id.search_recycler_view);
        mBudgetEditText = searchView.findViewById(R.id.budget_edit_text);
        TextInputEditText mLocationEditText = searchView.findViewById(R.id.home_search_edit_text);
        Button mGetResultsBtn = searchView.findViewById(R.id.get_search_results);
        TextView mFilterTextView = searchView.findViewById(R.id.filter_search);
        mSearchResultTextView = searchView.findViewById(R.id.search_number_result);
        mProgressBar = searchView.findViewById(R.id.search_progressbar);

        // hiding the keyboard on focus lost
        View[] focusableViews = {mLocationEditText, mLocationEditText};
        searchView.setOnTouchListener(SoftKeyboard.hideSoftKeyboardOnTouchEvent(focusableViews, requireContext()));
        mGetResultsBtn.setOnTouchListener(SoftKeyboard.hideSoftKeyboardOnTouchEvent(focusableViews, requireContext()));
        mRecyclerView.setOnTouchListener(SoftKeyboard.hideSoftKeyboardOnTouchEvent(focusableViews, requireContext()));

        mSearchResultTextView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.GONE);

        HouseAdapter houseAdapter = new HouseAdapter(mNavController, requireContext(), mUserViewModel, getLayoutInflater(), mainActivity.getNavLayout());
        houseAdapter.setFromSearchFragment();
        mRecyclerView.setAdapter(houseAdapter);

        if (searchQuery != null) {
            mLocationEditText.setText(searchQuery.getLocation());
            if (searchQuery.getFilter() != null) {
                mBudgetEditText.setText(StringConvertor.formatNumberToPrice(searchQuery.getFilter().getPriceMax()));
                searchQuery.setPrice(String.valueOf(searchQuery.getFilter().getPriceMax()));
            } else {
                mBudgetEditText.setText(searchQuery.getPrice());
            }
        } else {
            searchQuery = new Search("", "");
        }

        Search finalSearchQuery = searchQuery;

        mLocationEditText.addTextChangedListener(getTextWatcherLocation(finalSearchQuery));
        mBudgetEditText.addTextChangedListener(getTextWatcherPrice(finalSearchQuery, mBudgetEditText));

        mBudgetEditText.setOnFocusChangeListener((view, hasFocus) -> {
            mBudgetEditText.setText(finalSearchQuery.getPrice());
            if (!hasFocus && !finalSearchQuery.getPrice().isEmpty()) {
                mBudgetEditText.setText(StringConvertor.formatNumberToPrice(Float.parseFloat(finalSearchQuery.getPrice())));
            }
        });

        mFilterTextView.setOnClickListener(view -> {
            SearchFragmentDirections.ActionSearchFragmentToFilterFragment action;
            action = SearchFragmentDirections.actionSearchFragmentToFilterFragment();
            action.setSearch(finalSearchQuery);
            NavigationUtils.navigateSafe(mNavController,action);
        });

        mGetResultsBtn.setOnClickListener(view -> {
            {
                processFiltering(finalSearchQuery);
                mSearchViewModel.getHouses(finalSearchQuery);
                mProgressBar.setVisibility(View.VISIBLE);
                mSearchViewModel.gethouseMutableLiveData().observe(getViewLifecycleOwner(), houses -> {
                    if (houses != null) {
                        mProgressBar.setVisibility(View.GONE);
                        mSearchResultTextView.setVisibility(View.VISIBLE);
                        houseAdapter.setListOfHouses(houses);
                        if (houses.isEmpty()) {
                            mSearchResultTextView.setText(getText(R.string.search_no_result));
                        } else {
                            String result = getString(R.string.search_result_found);
                            mSearchResultTextView.setText(String.format(result, houses.size()));
                        }
                    }
                });
            }
        });
        if (mUserViewModel.getFirebaseUser() != null) {
            mUserViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    ArrayList<String> houseIds = user.getListOfWishlistHouses();
                    houseAdapter.setHouseIds(houseIds);
                }
            });
        }

    }

    private void processFiltering(Search search) {
        StringBuilder queryFilter = new StringBuilder();
        Filter filter = search.getFilter();
        float price = 0;
        if (filter != null) {
            if (filter.getListingType() != null) {
                queryFilter.append("listingType:").append(filter.getListingType()).append(" AND ");
            }
            ArrayList<HouseType> houseTypes = filter.getHouseTypes();
            int size = houseTypes.size();
            if (size > 0) {
                queryFilter.append("(");
                for (int i = 0; i < size - 1; i++) {
                    queryFilter.append("houseType:").append(houseTypes.get(i)).append(" OR ");
                }
                queryFilter.append("houseType:").append(houseTypes.get(size - 1)).append(")").append("  AND ");
            }
            if (filter.getNumberBedrooms() > 0 && filter.getNumberBedrooms() < 5) {
                queryFilter.append("numberBedrooms<=").append(filter.getNumberBedrooms()).append(" AND ");
            } else if (filter.getNumberBedrooms() == 5) {
                queryFilter.append("numberBedrooms>=").append(filter.getNumberBedrooms()).append(" AND ");
            }
            if (filter.getNumberBathrooms() > 0 && filter.getNumberBathrooms() < 5) {
                queryFilter.append("numberBathrooms<=").append(filter.getNumberBathrooms()).append(" AND ");
            } else if (filter.getNumberBathrooms() == 5) {
                queryFilter.append("numberBathrooms>=").append(filter.getNumberBathrooms()).append(" AND ");
            }
            if (filter.getNumberParkingSpot() > 0 && filter.getNumberParkingSpot() < 5) {
                queryFilter.append("numberParkingSpot<=").append(filter.getNumberParkingSpot()).append(" AND ");
            } else if (filter.getNumberParkingSpot() == 5) {
                queryFilter.append("numberParkingSpot>=").append(filter.getNumberParkingSpot()).append(" AND ");
            }

            if (filter.getLivingAreaMin() >= 0) {
                queryFilter.append("livingArea>=").append(filter.getLivingAreaMin()).append(" AND ");
            }
            if (filter.getLivingAreaMax() > filter.getLivingAreaMin()) {
                queryFilter.append("livingArea<=").append(filter.getLivingAreaMax()).append(" AND ");
            }

            // for prices
            queryFilter.append("price>=").append(Math.min(filter.getPriceMin(), filter.getPriceMax())).append(" AND ");
//            queryFilter.append("price<=").append(Math.max(filter.getPriceMin(), filter.getPriceMax())).append(" AND ");
            price = Math.max(filter.getPriceMin(), filter.getPriceMax());
            // TODO : handle keywords

            // Remove the last " AND " if present
            int length = queryFilter.length();
            if (length >= 5) { // Check if there's enough room for " AND " at the end
                queryFilter.delete(length - 5, length);
            }
        }
        if (queryFilter.length() != 0) {
            queryFilter.append(" AND ");
        }
        if (!search.getPrice().isEmpty()) {
            queryFilter.append("price<=").append(Math.max(price, Float.parseFloat(search.getPrice())));
        } else if (price > 0) {
            queryFilter.append("price<=").append(price);
        }
        search.setQueryFilter(queryFilter.toString());
    }

    private TextWatcher getTextWatcherLocation(Search search) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search.setLocation(String.valueOf(editable));
            }
        };
    }

    private TextWatcher getTextWatcherPrice(Search search, TextView view) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (view.hasFocus()) {
                    search.setPrice(String.valueOf(editable));
                }
            }
        };
    }

    @Override
    public View.OnClickListener onNavHomeClicked() {
        return view -> {
            NavigationUtils.navigateSafe(mNavController,SearchFragmentDirections.actionSearchFragmentToHomeFragment());
        };
    }

    @Override
    public View.OnClickListener onNavSearchClicked() {
        return view -> {
        };
    }

    @Override
    public View.OnClickListener onNavWishlistClicked() {
        return view -> {
            NavDirections navDirections;
            if (mAuth != null) {
                navDirections = SearchFragmentDirections.actionSearchFragmentToWishlistFragment();
            } else {
                navDirections = SearchFragmentDirections.actionSearchFragmentToWishlistGuestFragment();
            }
            NavigationUtils.navigateSafe(mNavController,navDirections);
        };
    }

    @Override
    public View.OnClickListener onNavProfileClicked() {
        return view -> {
            NavDirections navDirections;
            if (mAuth != null) {
                navDirections = SearchFragmentDirections.actionSearchFragmentToProfileFragment();
            } else {
                navDirections = SearchFragmentDirections.actionSearchFragmentToProfileGuestFragment();
            }
            NavigationUtils.navigateSafe(mNavController,navDirections);
        };
    }
}