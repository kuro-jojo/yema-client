package com.kuro.yema.viewModels;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kuro.yema.data.enums.HouseType;
import com.kuro.yema.data.model.House;
import com.kuro.yema.repositories.HouseRepository;
import com.kuro.yema.utils.TabItem;
import com.kuro.yema.views.dialogs.VerifyDialogFragment;

import java.util.List;
import java.util.Objects;

public class HomesContentViewModel extends AndroidViewModel implements HouseRepository.OnFirestoreTaskComplete {
    private final static int DEFAULT_DAYS_AGO = 7;
    public static FragmentManager FRAGMENT_MANAGER;
    private final MutableLiveData<List<House>> houseMutableLiveData;
    private final HouseRepository houseRepository;

    public HomesContentViewModel(@NonNull Application application, TabItem tabType) {
        super(application);
        houseRepository = new HouseRepository(this);
        houseMutableLiveData = new MutableLiveData<>();
        getHouses(tabType);
    }

    public void getHouses(TabItem tabType) {
        switch (tabType) {
            case NEW:
                houseRepository.getNewHouses(DEFAULT_DAYS_AGO);
                break;
            case STUDIO:
                houseRepository.getHousesByHouseType(HouseType.STUDIO);
                break;
            case BEDROOM_2:
                houseRepository.getHousesWithExactBedrooms(2);
                break;
            case BEDROOM_3:
                houseRepository.getHousesWithExactBedrooms(3);
                break;
            default:
                houseRepository.getAllHouses();
        }
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
        verifyDialogFragment.show(HomesContentViewModel.FRAGMENT_MANAGER, "DIALOG");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FRAGMENT_MANAGER.getFragments().size() > 0) {
                    FRAGMENT_MANAGER.getFragments().get(0).requireActivity().finish();
                }
            }
        }, 5000);
    }

    public MutableLiveData<List<House>> gethouseMutableLiveData() {
        return houseMutableLiveData;
    }
}
