package com.example.currencyexchange.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private String recentCurrency = "1,5";

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(recentCurrency);
    }

    public LiveData<String> getText() {
        return mText;
    }
}