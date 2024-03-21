package com.example.currencyexchange;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CSVReader {

    static String csvSplitBy = ",";
    static ArrayList<String> currencies = new ArrayList<String>();
    static ArrayList<String> exchangeRates = new ArrayList<>();
    static boolean dataLoaded = false;

    // Method to load CSV data into global variables
    public static void loadCSVData(Context context, String filePath) {
        if (!dataLoaded) {
            parseCSVToArray(context, filePath);
            dataLoaded = true;
        }
    }

    // Method to retrieve exchange rates
    public static ArrayList<String> getExchangeRates() {
        return exchangeRates;
    }

    // Method to read CSV data and populate spinners
    public static void readCSVToSpinners(Context context, Spinner spinner, Spinner secondSpinner) {
        if (!currencies.contains("Euro")) {
            currencies.add(0, "Euro"); // Add "Euro" to the top of the list
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, currencies);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        ArrayAdapter<String> secondSpinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, currencies);
        secondSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondSpinner.setAdapter(secondSpinnerAdapter);
        secondSpinner.setSelection(1);
    }

    // Method to parse CSV data into arrays
    private static void parseCSVToArray(Context context, String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            br.readLine(); // Skip header line

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);

                if (data.length > 2) {
                    currencies.add(data[2]);
                }

                if (data.length > 3) {
                    exchangeRates.add(data[3]);
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}