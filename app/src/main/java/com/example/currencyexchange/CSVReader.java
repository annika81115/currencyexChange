package com.example.currencyexchange;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.currencyexchange.ui.home.HomeFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CSVReader {

    static String csvSplitBy = ",";

    static ArrayList<String> currencies = new ArrayList<>();

    static ArrayList<String> exchangeRates = new ArrayList<>();

    public static ArrayList<String> getExchangeRates(){
        return exchangeRates;
    }

    public static void readCSVToSpinner(Context context, Spinner spinner, Spinner secondSpinner, String pathToFile) {

        currencies.add("Euro");
        parseCSVtoArray(pathToFile);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, currencies);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        ArrayAdapter<String> secondSpinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, currencies);
        secondSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondSpinner.setAdapter(secondSpinnerAdapter);
        secondSpinner.setSelection(1);
    }

    private static void parseCSVtoArray(String pathToFile){
        try {
            File file = new File(pathToFile);
            BufferedReader br = new BufferedReader(new FileReader(file));

            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] daten = line.split(csvSplitBy);

                if (daten.length > 2) {
                    currencies.add(daten[2]);
                }

                if (daten.length > 3) {
                    exchangeRates.add(daten[3]);
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}