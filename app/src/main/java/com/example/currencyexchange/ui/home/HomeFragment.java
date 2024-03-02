package com.example.currencyexchange.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.currencyexchange.R;
import com.example.currencyexchange.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

import com.example.currencyexchange.ui.home.logic;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private EditText input1;

    private EditText input2;

    private double exchangeRate = 1.5 ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.changeSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSides();
                Snackbar.make(view, "Jetzt wird getauscht.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exchange(view);
            }
        });

        binding.firstSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        getExchangeRate();
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });


        binding.secondSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        getExchangeRate();
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void exchange(View view){
        input1 = (EditText) getView().findViewById(R.id.input1);
        if (input1.getText().toString().trim().length() > 0) {
            getExchangeRate();
            double result = calculate(exchangeRate);
            String result2 = String.valueOf(result);
            setText(R.id.input2, result2);
        }else {
            Snackbar.make(view, "Gebe bitte erst eine Zahl ein!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            setText(R.id.input2, "");
        }
    }

    private void changeSides(){
        String spinner1 = getSpinner(R.id.firstSpinner);
        String spinner2 = getSpinner(R.id.secondSpinner);
        String number1 = getTextString(R.id.input1);
        String number2 = getTextString(R.id.input2);

        setText(R.id.input1, number2);
        setText(R.id.input2, number1);

        setSpinner(R.id.firstSpinner, spinner2);
        setSpinner(R.id.secondSpinner, spinner1);
    }

    private double getExchangeRate() {
        double result = 1.5;
        String currency1 = getSpinner(R.id.firstSpinner);
        String currency2 = getSpinner(R.id.secondSpinner);
        if (!Objects.equals(currency1, currency2)){
          //TODO: implement the rest
        }
        return result;
    }

    public void setSpinner(int pSpinnerId, int pInput){
        Spinner spinner = (Spinner) getView().findViewById(pSpinnerId);
        spinner.setSelection(pInput);
    }

    public void setSpinner(int pSpinnerId, String pInput){
        Spinner spinner = (Spinner) getView().findViewById(pSpinnerId);
        ArrayAdapter myAdapter = (ArrayAdapter) spinner.getAdapter(); //cast to an ArrayAdapter
        int position = myAdapter.getPosition(pInput);
        spinner.setSelection(position);
    }

    public String getSpinner(int pSpinnerId){
        Spinner spinner = (Spinner) getView().findViewById(pSpinnerId);
        return spinner.getSelectedItem().toString();
    }

    public void setText(int pTextId, String pText){
        EditText input = (EditText) getView().findViewById(pTextId);
        input.setText(pText);
    }


    public String getTextString(int pTextId){
        EditText objekt = (EditText) getView().findViewById(pTextId);
        return objekt.getText().toString();
    }

    private double calculate(double pExchangeRate){
        double zahl1 = Double.parseDouble(input1.getText().toString());
        return zahl1 * pExchangeRate;
    }
}