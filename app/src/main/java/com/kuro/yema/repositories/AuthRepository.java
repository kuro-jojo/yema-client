package com.kuro.yema.repositories;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kuro.yema.utils.VerificationSMS;

import java.util.Objects;

public class AuthRepository {


    private final MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<Boolean> isVerificationFailed;

    public AuthRepository(Application application) {

        firebaseUserMutableLiveData = new MutableLiveData<>();
        isVerificationFailed = new MutableLiveData<>(false);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());
        }
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsVerificationFailed() {
        return isVerificationFailed;
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }


    public void logIn(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    isVerificationFailed.postValue(false);
                    firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());

                } else {
                    isVerificationFailed.postValue(true);
                    Log.e("firebaseSMS", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                }
                Handler handler = new Handler();
                handler.postDelayed(() -> isVerificationFailed.postValue(false), 1000);
            }
        });
    }

    // called when the signIn/signOut button is pressed
    public void startVerification(Fragment fragment, String phoneNumber) {
        VerificationSMS.init(fragment, phoneNumber, this);
        PhoneAuthOptions options = VerificationSMS.getVerificationOptions();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void logout() {
        firebaseAuth.signOut();
    }
}
