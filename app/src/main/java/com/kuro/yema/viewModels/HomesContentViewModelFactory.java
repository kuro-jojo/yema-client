package com.kuro.yema.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kuro.yema.utils.TabItem;

public class HomesContentViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final TabItem tabType;

    public HomesContentViewModelFactory(Application application, TabItem tabType) {
        super(application);
        this.application = application;
        this.tabType = tabType;
    }

    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomesContentViewModel.class)) {
            return (T) new HomesContentViewModel(application, tabType);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
