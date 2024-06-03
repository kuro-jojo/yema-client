package com.kuro.yema.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kuro.yema.data.model.User;

import java.util.Objects;

/**
 * The type User repository.
 */
public class UserRepository {

    private final CollectionReference mCollectionReference;
    private final OnFirestoreTaskComplete onFirestoreTaskComplete;
    private final FirebaseUser mFirebaseUser;

    public UserRepository(OnFirestoreTaskComplete onFirestoreTaskComplete) {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mCollectionReference = mFirestore.collection("users");
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
    }

    public FirebaseUser getFirebaseUser() {
        return mFirebaseUser;
    }

    public void getUser(String uid) {
        mCollectionReference.document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    onFirestoreTaskComplete.userDataLoaded(task.getResult().toObject(User.class));
                } catch (RuntimeException e) {
                    onFirestoreTaskComplete.onRuntimeError(e);
                }
            } else {
                onFirestoreTaskComplete.onError(task.getException());
            }
        });
    }


    /**
     * Add user at the registration.
     *
     * @param phoneNumber the phone number
     */
    public void addUser(String uid, String phoneNumber) {
        User user = new User(uid, phoneNumber);
        mCollectionReference.document(uid).set(user, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            Log.d("USER_REPOSITORY", "User created successfully");
                            onFirestoreTaskComplete.userDataLoaded(user);
                        } catch (RuntimeException e) {
                            onFirestoreTaskComplete.onRuntimeError(e);
                        }
                    } else {
                        Log.d("USER_REPOSITORY", "Error while creating the user (" + phoneNumber + ")" + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void updateUserWishlist(User user, String homeId) {
        user.addHouseToWishlist(homeId);
        mCollectionReference.document(user.getUid())
                .set(user, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            try {
                                Log.d("USER_REPOSITORY", "House added to wishlist successfully");
                                onFirestoreTaskComplete.userDataLoaded(user);
                            } catch (RuntimeException e) {
                                onFirestoreTaskComplete.onRuntimeError(e);
                            }
                        } else {
                            Log.d("USER_REPOSITORY", "Error while adding the house to wishlist (" + user.getUid() + ")" + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }

    public void removeHouseFromWishlist(User user, String houseID) {
        user.removeFromWishlist(houseID);
        mCollectionReference.document(user.getUid())
                .set(user, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            Log.d("USER_REPOSITORY", "House removed from wishlist successfully. ID : " + houseID);
                            onFirestoreTaskComplete.userDataLoaded(user);
                        } catch (RuntimeException e) {
                            onFirestoreTaskComplete.onRuntimeError(e);
                        }
                    } else {
                        Log.d("USER_REPOSITORY", "Error while removing the house from wishlist (" + user.getUid() + ")" + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public interface OnFirestoreTaskComplete {

        void userDataLoaded(User user);

        void onError(Exception e);

        void onRuntimeError(RuntimeException e);
    }
}
