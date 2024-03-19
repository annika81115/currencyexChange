package com.example.currencyexchange;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.currencyexchange.databinding.ActivityMainBinding;
import com.example.currencyexchange.filedownload.DownloadFileTask;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;


    private String url = "http://194.164.56.173:1234/csv";

    public String firstSpinnerText;


    public EditText input1;
    public EditText input2;

    SwitchCompat switchMode;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // to reproduce the latest bug
        this.fix();

        this.updateValues();

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setDarkModeSettings();

        String pathToFile = MainActivity.this.getFilesDir() + "/values.csv";
        CSVReader.readCSVToSpinner(this, findViewById(R.id.firstSpinner), findViewById(R.id.secondSpinner), pathToFile);


        switchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("nightMode", false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("nightMode", true);
                }
                editor.apply();
            }
        });

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    private void fix() {
        File file = new File(MainActivity.this.getFilesDir(), "values.csv");
        if (file.exists()) {
            boolean b = file.delete();
            if (b) {
                Log.d("FILE-TO-CSV", file.getAbsolutePath());
            }
        }


        File file2 = new File(MainActivity.this.getFilesDir(), "last_update.txt");
        if (file2.exists()) {
            boolean b = file2.delete();
            if (b) {
                Log.d("LATEST", "DELETED FILE");
            }
        }

    }

    private void setDarkModeSettings() {
        switchMode = findViewById(R.id.switchMode);

        sharedPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("nightMode", false);

        if (nightMode) {
            switchMode.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setSpinner(int pID) {
        Spinner secondSpinner = (Spinner) findViewById(pID);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> secondSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.currency,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        secondSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        secondSpinner.setAdapter(secondSpinnerAdapter);
    }

    public void readAndPrintCsvFile() {
        // Construct the full file path
        String filePath = new File(MainActivity.this.getFilesDir(), "/values").getPath();

        // Read the CSV file
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                // Print each line to logcat
                Log.d("CSVReader", line);
            }

            br.close();
            isr.close();
            //fis.close();
        } catch (IOException e) {
            Log.d("CSVReader", "Failed to read and print: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateValues() {

        if (isUpdateNeeded()) {
            DownloadFileTask downloadTask = new DownloadFileTask(MainActivity.this, "http://194.164.56.173:1234/csv");
            downloadTask.execute();
            Log.d("CSVReader", "Between download and read");
            this.readAndPrintCsvFile();

            updateLatestUpdateDate();
        }


    }


    private boolean isUpdateNeeded() {

        File lastUpdateFile = new File(MainActivity.this.getFilesDir(), "last_update.txt");
        if (!lastUpdateFile.exists()) {
            return true;
        }

        try {

            FileInputStream fis = new FileInputStream(lastUpdateFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String lastUpdate = br.readLine();
            br.close();
            isr.close();
            fis.close();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date lastUpdateDate = sdf.parse(lastUpdate);

            Date currenctDate = new Date();
            return !sdf.format(currenctDate).equals(sdf.format(lastUpdateDate));

        } catch (IOException | java.text.ParseException e) {
            e.printStackTrace();
            Log.d("Updater", "Failed to parse from file");
            return true;
        }

    }

    private void updateLatestUpdateDate() {

        try {
            File lastUpdateFile = new File(MainActivity.this.getFilesDir(), "last_update.txt");
            FileOutputStream fos = new FileOutputStream(lastUpdateFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            //write to file
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            osw.write(sdf.format(new Date()));
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Updater", "Could write the latest update date to latestUpdateDate file");
        }
    }

}