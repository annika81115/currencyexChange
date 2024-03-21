package com.example.currencyexchange;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
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
import com.example.currencyexchange.ui.home.HomeFragment;
import com.example.currencyexchange.ui.info.InfoFragment;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private SwitchCompat switchMode;
    private boolean nightMode;
    private SharedPreferences sharedPreferences;

    private Spinner firstSpinner;
    private Spinner secondSpinner;

    private InfoFragment infoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        infoFragment = new InfoFragment();

        setupNavigation();
        setDarkModeSettings();
        setListeners();
        updateValues();
        readCSVToSpinners();
    }

    private void showHomeFragment() {
        // Replace the fragment container with HomeFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    private void setupNavigation() {
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setDarkModeSettings() {
        switchMode = findViewById(R.id.switchMode);
        sharedPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("nightMode", false);
        switchMode.setChecked(nightMode);
        switchMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("nightMode", isChecked);
            editor.apply();
        });
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        readCSVToSpinners(); // Repopulate spinners when activity resumes
    }

    private void setListeners() {
        // Set listeners here
    }

    private void updateValues() {
        if (isUpdateNeeded()) {
            DownloadFileTask downloadTask = new DownloadFileTask(MainActivity.this, "http://194.164.56.173:1234/csv");
            downloadTask.execute();
            readAndPrintCsvFile();
        }
    }

    private boolean isUpdateNeeded() {
        File lastUpdateFile = new File(MainActivity.this.getFilesDir(), "last_update.txt");
        if (!lastUpdateFile.exists()) {
            return true;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String lastUpdate = new BufferedReader(new InputStreamReader(new FileInputStream(lastUpdateFile))).readLine();
            Date lastUpdateDate = sdf.parse(lastUpdate);
            return !sdf.format(new Date()).equals(sdf.format(lastUpdateDate));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            Log.d("Updater", "Failed to parse from file");
            return true;
        }
    }

    private void readAndPrintCsvFile() {
        String filePath = new File(MainActivity.this.getFilesDir(), "/values").getPath();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                Log.d("CSVReader", line);
            }
        } catch (IOException e) {
            Log.d("CSVReader", "Failed to read and print: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private void populateSpinner() {
        // Add "Euro" to the spinner data
        ArrayList<String> spinnerData = new ArrayList<>();
        spinnerData.add("Euro");

        // Populate your spinner with data here
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_spinner_item,
                spinnerData
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstSpinner.setAdapter(adapter);
    }

    private void readCSVToSpinners() {
        firstSpinner = findViewById(R.id.firstSpinner);
        secondSpinner = findViewById(R.id.secondSpinner);

        // Populate firstSpinner with "Euro"
        populateSpinner();

        // Load CSV data into the spinners
        CSVReader.loadCSVData(this, MainActivity.this.getFilesDir() + "/values.csv");
        CSVReader.readCSVToSpinners(this, firstSpinner, secondSpinner);
    }
}