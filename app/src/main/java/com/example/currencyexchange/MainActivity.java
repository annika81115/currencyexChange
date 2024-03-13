package com.example.currencyexchange;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.currencyexchange.filedownload.DownloadFileTask;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.currencyexchange.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
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
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setDarkModeSettings();

        InputStream inputStream = getResources().openRawResource(R.raw.output);
        CSVReader.readCSVToSpinner(this, findViewById(R.id.firstSpinner), findViewById(R.id.secondSpinner), inputStream);


        switchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nightMode){
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
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getSpinner(R.id.firstSpinner);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

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



        // still testing
        // this part is for downloading the file
        // maybe need some improvement
        this.updateValues();

    }

    private void setDarkModeSettings() {
        switchMode = findViewById(R.id.switchMode);

        sharedPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("nightMode", false);

        if (nightMode){
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
        String fileName = "values.csv";
        File file = new File(MainActivity.this.getFilesDir(), fileName);

        // Read the CSV file
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                // Print each line to logcat
                Log.d("CSVReader", line);
            }

            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            Log.d("CSVReader", e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateValues() {

        if(isUpdateNeeded()) {
            DownloadFileTask downloadTask = new DownloadFileTask(MainActivity.this, "http://194.164.56.173:1234/csv");
            downloadTask.execute();
            Log.d("CSVReader", "Between download and read");
            this.readAndPrintCsvFile();

            updateLatestUpdateDate();
        }


    }


    private boolean isUpdateNeeded() {

        File lastUpdateFile = new File(MainActivity.this.getFilesDir(), "last_update.txt");
        if(!lastUpdateFile.exists()) {
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