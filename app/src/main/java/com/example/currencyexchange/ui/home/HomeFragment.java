package com.example.currencyexchange.ui.home;

import android.os.Bundle;
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

import com.example.currencyexchange.CSVReader;
import com.example.currencyexchange.R;
import com.example.currencyexchange.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private EditText input1;

    private EditText input2;

    private double doenerPrice = 6.5;

    private double exchangeRate = 0;

    boolean complicate = false;

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
                        calculateExchangeRate(view);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        binding.secondSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        calculateExchangeRate(view);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        return root;
    }

    void setExchangeRate(String pExchangeRate){
        TextView textView = (TextView) getView().findViewById(R.id.text_home);
        textView.setText(pExchangeRate);
    }

    void setExplanation(String pExplanation){
        TextView textView = (TextView) getView().findViewById(R.id.text_explanation);
        textView.setText(pExplanation);
    }

    void setDoenerValue(String pDoenerValue){
        TextView textView = (TextView) getView().findViewById(R.id.text_doener);
        textView.setText("Der eingegebene Wert entspricht ca. " + pDoenerValue + " Döner zu einem Preis von 6.50 Euro.");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void exchange(View view){
        input1 = (EditText) getView().findViewById(R.id.input1);
        if (input1.getText().toString().trim().length() > 0) {
            exchangeRate = calculateExchangeRate(view);
            double result = calculate(exchangeRate);
            String result2 = String.valueOf(result);
            setText(R.id.input2, result2);
            setDoenerValue(result);
            setExplanation(getTextString(R.id.input1) + " " + getSpinner(R.id.firstSpinner) + " entsprechen " + getTextString(R.id.input2) +" " + getSpinner(R.id.secondSpinner) + ".");
        }else {
            Snackbar.make(view, "Gebe bitte erst eine Zahl ein!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            setText(R.id.input2, "");
        }
    }

    private void setDoenerValue(double pResult) {
        if (complicate){
            pResult = pResult/6.5;
        }
        double doenerValue = round(pResult/ doenerPrice, 2);
        setDoenerValue(String.valueOf(doenerValue));
    }

    private void changeSides(){
        String spinner1 = getSpinner(R.id.firstSpinner);
        String spinner2 = getSpinner(R.id.secondSpinner);
        String number2 = getTextString(R.id.input2);

        calculateExchangeRate(getView());

        setText(R.id.input1, number2);
        setText(R.id.input2, " ");

        setSpinner(R.id.firstSpinner, spinner2);
        setSpinner(R.id.secondSpinner, spinner1);
    }

    private double calculateExchangeRate(View view) {
        String stringResult = "0";
        double result = 0;
        complicate = false;
        int currency1 = getSpinnerPosition(R.id.firstSpinner);
        int currency2 = getSpinnerPosition(R.id.secondSpinner);
        ArrayList<String> exchangeRates = CSVReader.getExchangeRates();
        if (!Objects.equals(currency1, currency2) && currency1 == 0){
            stringResult = exchangeRates.get(currency2 - 1);
            result = Double.parseDouble(stringResult);
            doenerPrice = 6.5 * result;
        } else if (!Objects.equals(currency1, currency2) && currency2 == 0) {
            String interim = exchangeRates.get(currency1 - 1);
            result = 1 / Double.parseDouble(interim);
            stringResult = String.valueOf(result);
            doenerPrice = 6.5;
        } else if (!Objects.equals(currency1, currency2)) {
            String firstInterim = exchangeRates.get(currency1-1);
            String secondInterim = exchangeRates.get(currency2-1);
            result = Double.parseDouble(firstInterim) * (1/ Double.parseDouble(secondInterim));
            stringResult = String.valueOf(result);
            complicate = true;
            doenerPrice = 6.5 * result;
        } else {
        Snackbar.make(view, "Bitte ändere eine der ausgewählten Währungen.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        }
        setExchangeRate(stringResult);
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

    public int getSpinnerPosition(int pSpinnerId){
        Spinner spinner = (Spinner) getView().findViewById(pSpinnerId);
        return spinner.getSelectedItemPosition();
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
        return round(zahl1 * pExchangeRate, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}