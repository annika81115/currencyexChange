package com.example.currencyexchange.filedownload;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.currencyexchange.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadFileTask {

    private Context context;
    private String url;

    public DownloadFileTask(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    public void execute() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute( () -> {
                try {
                    // Background work here (e.g., downloading file)
                    Log.d("INSIDE-RUN", "START");
                    String result = downloadFileFromUrl(url);
                    Log.d("INSIDE-RUN", "DOWNLOAD");
                    // UI Thread work (updating UI or saving file) should be done using handler.post
                        Log.d("INSIDE-RUN", "AFTER POST");
                            if (result != null) {
                                Log.d("INSIDE-RUN", "NOT NULL");
                                // File downloaded successfully
                                //Toast.makeText(context, "File downloaded: " + result, Toast.LENGTH_LONG).show();
                                // Save file to specific path
                                String outputPath = new File(context.getFilesDir()+ "/values.csv").getPath(); // Example path
                                Log.d("INSIDE-RUN", outputPath);
                                saveToFile(outputPath, result);
                                Log.d("OPP", "OPP: " + outputPath);

                                try {
                                    FileInputStream fis = new FileInputStream(outputPath);
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
                                    Log.d("CSVReader", "Failed to read and print: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            } else {
                                // Error downloading file
                                //Toast.makeText(context, "Error downloading file", Toast.LENGTH_LONG).show();
                                Log.d("DOWNLOADER", "Failed to Download");
                            }
                } catch (Exception e) {
                    Log.e("DownloadFileTask", "Exception occurred: " + e.getMessage());
                    e.printStackTrace();
                }

        });

        // Remember to shutdown executor when you're done
        executor.shutdown();
    }

    private String downloadFileFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private void saveToFile(String outputPath, String fileContent) {
        Log.d("DownloadFileTask", "Saving file to: " + outputPath);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(outputPath);
            outputStream.write(fileContent.getBytes());
            outputStream.close();

            updateLatestUpdateDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLatestUpdateDate() {

        try {
            File lastUpdateFile = new File(context.getFilesDir(), "last_update.txt");
            FileOutputStream fos = new FileOutputStream(lastUpdateFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            //write to file
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            osw.write(sdf.format(new Date()));
            osw.close();
            fos.close();

            Log.d("LatestUpdate", "Successfully updated the latest update date");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Updater", "Could write the latest update date to latestUpdateDate file");
        }

    }

}