package com.kuro.yema.viewModels;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kuro.yema.data.model.User;
import com.kuro.yema.repositories.UserRepository;
import com.kuro.yema.views.dialogs.VerifyDialogFragment;

import java.util.Objects;

public class UserViewModel extends AndroidViewModel implements UserRepository.OnFirestoreTaskComplete {
    public static FragmentManager FRAGMENT_MANAGER;
    private final MutableLiveData<User> userMutableLiveData;
    private final UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(this);
        userMutableLiveData = new MutableLiveData<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            getUser(auth.getUid());
        }
    }

    public void getUser(String uid) {
        userRepository.getUser(uid);
    }

    public void addUser(String uid, String phoneNumber) {
        userRepository.addUser(uid, phoneNumber);
    }

    public void updateUserWishlist(User user, String homeID) {
        userRepository.updateUserWishlist(user, homeID);
    }

    public void removeHouseFromWishlist(User user, String homeID) {
        userRepository.removeHouseFromWishlist(user, homeID);
    }

    public FirebaseUser getFirebaseUser() {
        return userRepository.getFirebaseUser();
    }

    @Override
    public void userDataLoaded(User user) {
        userMutableLiveData.setValue(user);
    }

    @Override
    public void onError(Exception e) {
        Log.e("UserFirestore", Objects.requireNonNull(e.getMessage()));
    }

    @Override
    public void onRuntimeError(RuntimeException e) {

        VerifyDialogFragment verifyDialogFragment = new VerifyDialogFragment("An error occurred on the server. \n Exiting the app");
        verifyDialogFragment.show(UserViewModel.FRAGMENT_MANAGER, "DIALOG");

        Log.e("USER_", "error : " + e.getMessage());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FRAGMENT_MANAGER.getFragments().get(0).getActivity().finish();
            }
        }, 5000);
    }

    public MutableLiveData<User> getUserMutableLiveData() {
        return userMutableLiveData;
    }
}
