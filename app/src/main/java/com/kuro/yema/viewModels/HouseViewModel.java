package com.kuro.yema.viewModels;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kuro.yema.data.model.House;
import com.kuro.yema.repositories.HouseRepository;
import com.kuro.yema.views.dialogs.VerifyDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HouseViewModel extends AndroidViewModel implements HouseRepository.OnFirestoreTaskComplete {
    public static FragmentManager FRAGMENT_MANAGER;
    private final MutableLiveData<House> houseMutableLiveData;
    private final HouseRepository houseRepository;
    private final MutableLiveData<List<House>> houseListMutableLiveData;
    private MutableLiveData<String> houseId;

    public HouseViewModel(@NonNull Application application) {
        super(application);
        houseRepository = new HouseRepository(this);
        houseMutableLiveData = new MutableLiveData<>();
        houseListMutableLiveData = new MutableLiveData<>();
        houseId = new MutableLiveData<>();
    }

    public MutableLiveData<List<House>> getHouseListMutableLiveData() {
        return houseListMutableLiveData;
    }

    public void setHouseId(MutableLiveData<String> houseId) {
        this.houseId = houseId;
    }

    public void getHouse(String houseId) {
        houseRepository.getHouseByHouseId(houseId);
    }

    public void getHousesInListOfIds(ArrayList<String> houseIds) {
        houseRepository.getHousesInListOfIds(houseIds);
    }

    @Override
    public void housesDataLoaded(List<House> houseList) {
        houseListMutableLiveData.setValue(houseList);
    }

    @Override
    public void houseDataLoaded(House house) {
        houseMutableLiveData.setValue(house);
    }

    @Override
    public void onError(Exception e) {
        Log.e("HouseFirestore", Objects.requireNonNull(e.getMessage()));
    }

    @Override
    public void onRuntimeError(RuntimeException e) {

        VerifyDialogFragment verifyDialogFragment = new VerifyDialogFragment("An error occurred on the server. Please come back later or contact the administrator!!");
        verifyDialogFragment.show(HouseViewModel.FRAGMENT_MANAGER, "DIALOG");

        Log.e("HouseFirestore", "Runtime error : " + e.getMessage());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FRAGMENT_MANAGER.getFragments().get(0).getActivity().finish();
            }
        }, 2000);
    }

    public MutableLiveData<House> getHouseMutableLiveData() {
        return houseMutableLiveData;
    }
}