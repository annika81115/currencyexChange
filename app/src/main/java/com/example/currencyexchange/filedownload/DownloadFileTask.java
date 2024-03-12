package com.example.currencyexchange.filedownload;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Background work here (e.g., downloading file)
                    String result = downloadFileFromUrl(url);

                    // UI Thread work (updating UI or saving file) should be done using handler.post
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (result != null) {
                                // File downloaded successfully
                                Toast.makeText(context, "File downloaded: " + result, Toast.LENGTH_SHORT).show();
                                // Save file to specific path
                                String outputPath = "/storage/emulated/0/downloaded_file.txt"; // Example path
                                saveToFile(outputPath, result);
                            } else {
                                // Error downloading file
                                Toast.makeText(context, "Error downloading file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(outputPath);
            outputStream.write(fileContent.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}