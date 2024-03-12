package com.example.currencyexchange;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

    public static void readCSVToSpinner(Context context, Spinner spinner, Spinner secondSpinner, String inputStream) {

        currencies.add("Euro");
        parseCSVtoArray(new File(inputStream), 2);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, currencies);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        ArrayAdapter<String> secondSpinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, currencies);
        secondSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondSpinner.setAdapter(secondSpinnerAdapter);
        secondSpinner.setSelection(1);
    }


    public void getExchangeRates(InputStream pInputStream){
        //parseCSVtoArray(pInputStream, 3);
    }

    private static void parseCSVtoArray(File pInputStream, int pColumnToRead){
        try {
            BufferedReader br = new BufferedReader(new FileReader(pInputStream));

            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] daten = line.split(csvSplitBy);

                // Überprüfe, ob die Spalte vorhanden ist, um ArrayIndexOutOfBoundsException zu vermeiden
                if (daten.length > pColumnToRead) {
                    currencies.add(daten[pColumnToRead]);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
