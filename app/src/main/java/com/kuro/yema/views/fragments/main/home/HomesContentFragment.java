package com.kuro.yema.views.fragments.main.home;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuro.yema.R;
import com.kuro.yema.adapters.HouseAdapter;
import com.kuro.yema.data.model.House;
import com.kuro.yema.data.model.User;
import com.kuro.yema.utils.TabItem;
import com.kuro.yema.viewModels.HomesContentViewModel;
import com.kuro.yema.viewModels.HomesContentViewModelFactory;
import com.kuro.yema.viewModels.UserViewModel;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * HomesContentFragment class
 * Fragment that will contain the result (houses) of each tab item
 */
public class HomesContentFragment extends Fragment {

    private LinearLayout mNavLayout;
    private TabItem mTabType;
    private HomesContentViewModel mHomesContentViewModel;
    private UserViewModel mUserViewModel;
    private NavController mNavController;
    private TextView mHomesNotAvailable;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public HomesContentFragment() {
    }

    public HomesContentFragment(TabItem tabType, NavController navController, LinearLayout navLayout) {
        mTabType = tabType;
        mNavController = navController;
        mNavLayout = navLayout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the data of the corresponding tab
        HomesContentViewModelFactory mViewModelFactory = new HomesContentViewModelFactory(requireActivity().getApplication(), mTabType);
        HomesContentViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mHomesContentViewModel = new ViewModelProvider(this, mViewModelFactory).get(HomesContentViewModel.class);

        UserViewModel.FRAGMENT_MANAGER = getChildFragmentManager();
        mUserViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_contents, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mRecyclerView = view.findViewById(R.id.house_home_recyclerview);
        mHomesNotAvailable = view.findViewById(R.id.no_homes_available);
        mProgressBar = view.findViewById(R.id.progressBar);
        mSwipeRefreshLayout = view.findViewById(R.id.house_home_swipe_refresh_layout);

        HouseAdapter houseAdapter = new HouseAdapter(mNavController, requireContext(), mUserViewModel, getLayoutInflater(), mNavLayout);
        houseAdapter.setFromHomeFragment();
        mRecyclerView.setAdapter(houseAdapter);
        if (mUserViewModel.getFirebaseUser() != null) {
            mUserViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        ArrayList<String> houseIds = user.getListOfWishlistHouses();
                        houseAdapter.setHouseIds(houseIds);
                    }
                }
            });
        }
        resetFragment(houseAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // make  another request to firestore to get the (updated) data
                mHomesContentViewModel.getHouses(mTabType);
                resetFragment(houseAdapter);
                new Handler().postDelayed(() -> mSwipeRefreshLayout.setRefreshing(false), 500);
            }
        });


    }

    private void resetFragment(HouseAdapter houseAdapter) {
        mProgressBar.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mHomesNotAvailable.setVisibility(View.GONE);

        mHomesContentViewModel.gethouseMutableLiveData().observe(getViewLifecycleOwner(), houses -> {
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);

            if (houses.isEmpty()) {
                mHomesNotAvailable.setVisibility(View.VISIBLE);
            } else {
                mHomesNotAvailable.setVisibility(View.GONE);
                // TODO: remove it after adding the houses

//                    SearchClient client = DefaultSearchClient.create("1AFSE5SY2C", "538c75118a4ba1b53d6bc6869bb06a62");
//                    SearchIndex<House> index = client.initIndex("yema", House.class);
//
//                    CompletableFuture<Void> saveObjectFuture = CompletableFuture.runAsync(() -> {
//                        try {
//                            index.saveObjectsAsync(houses, null).join(); // Use join to block and wait for completion
//                        } catch (Exception e) {
//                            Log.e("ALGOLIA", "Error saving object: " + e.getMessage());
//                        }
//                    });
//
//                    saveObjectFuture.join();

                // TODO : remove adding new collection to firestore
                // adding collection for each country
//                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//                CollectionReference togoReference = firestore.collection("houses");
//                for (House house : houses) {
//                    house.setLocation(house.getLocation() + ", Togo");
//                    togoReference.document("togo").collection(house.getHouseType().type.toLowerCase()).document(house.getHouseId()).set(house);
//                }


                // Search the index and print the results
            }
            houseAdapter.setListOfHouses(houses);
        });
    }
}