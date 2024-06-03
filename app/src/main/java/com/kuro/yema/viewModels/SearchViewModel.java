package com.kuro.yema.viewModels;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kuro.yema.data.model.House;
import com.kuro.yema.data.model.Search;
import com.kuro.yema.repositories.HouseRepository;
import com.kuro.yema.views.dialogs.VerifyDialogFragment;

import java.util.List;
import java.util.Objects;

public class SearchViewModel extends AndroidViewModel implements HouseRepository.OnFirestoreTaskComplete {
    public static FragmentManager FRAGMENT_MANAGER;
    private final MutableLiveData<List<House>> houseMutableLiveData;
    private final HouseRepository houseRepository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        houseRepository = new HouseRepository(this);
        houseMutableLiveData = new MutableLiveData<>();
    }

    public void getHouses(String houseLocation, String housePrice) {
        if (!houseLocation.isEmpty() && !housePrice.isEmpty()) {
            houseRepository.getHousesByLocationAndPrice(houseLocation, housePrice);
        } else if (!houseLocation.isEmpty()) {
            houseRepository.getHousesByLocation(houseLocation);
        } else if (!housePrice.isEmpty()) {
            houseRepository.getHousesByPrice(housePrice);
        } else {
            houseRepository.getAllHouses();
        }
    }

    public void getHouses(Search search) {
        houseRepository.getHouses(search.getQueryFilter(), search.getLocation());
    }

    @Override
    public void housesDataLoaded(List<House> houseList) {
        houseMutableLiveData.setValue(houseList);
    }

    @Override
    public void houseDataLoaded(House house) {

    }

    @Override
    public void onError(Exception e) {
        Log.e("HouseFirestore", Objects.requireNonNull(e.getMessage()));
    }

    @Override
    public void onRuntimeError(RuntimeException e) {

        VerifyDialogFragment verifyDialogFragment = new VerifyDialogFragment("An error occurred on the server. \n Exiting the app");
        verifyDialogFragment.show(SearchViewModel.FRAGMENT_MANAGER, "DIALOG");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FRAGMENT_MANAGER.getFragments().get(0).getActivity().finish();
            }
        }, 5000);
    }

    public MutableLiveData<List<House>> gethouseMutableLiveData() {
        return houseMutableLiveData;
    }
}
