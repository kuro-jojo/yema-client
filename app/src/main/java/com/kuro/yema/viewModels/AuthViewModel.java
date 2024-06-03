package com.kuro.yema.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.kuro.yema.repositories.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final FirebaseUser currentUser;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<Boolean> isVerificationFailed;


    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        currentUser = authRepository.getCurrentUser();
        userMutableLiveData = authRepository.getFirebaseUserMutableLiveData();
        isVerificationFailed = authRepository.getIsVerificationFailed();
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsVerificationFailed() {
        return isVerificationFailed;
    }

    public void startVerification(Fragment fragment, String phoneNumber) {
        authRepository.startVerification(fragment, phoneNumber);
    }

    public void logIn(PhoneAuthCredential credential) {
        authRepository.logIn(credential);
    }

    public void logOut() {
        authRepository.logout();
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }
}
