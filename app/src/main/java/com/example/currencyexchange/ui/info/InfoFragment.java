package com.example.currencyexchange.ui.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.currencyexchange.databinding.FragmentInfoBinding;

public class InfoFragment extends Fragment {

    private FragmentInfoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewModel
        InfoViewModel infoViewModel = new ViewModelProvider(this).get(InfoViewModel.class);

        // Inflate the layout for this fragment
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up TextView with ViewModel
        final TextView textView = binding.textGallery;
        infoViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release binding to avoid memory leaks
        binding = null;
    }
}