package com.example.currencyexchange.filedownload;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.example.currencyexchange.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DownloadFileTask extends AsyncTask<String, Void, String> {

    private Context context;

    public DownloadFileTask(Context context) {
        this.context = context;
    }


    @Override
    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                InputStream in = urlConnection.getInputStream();
                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");
                boolean hasInput = scanner.hasNext();
                if(hasInput) {
                    return scanner.next();
                }else {
                    return null;
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onPostExecute(String result) {
        if(result != null) {
            Toast.makeText(context, "File downloaded: " + result, Toast.LENGTH_SHORT).show();

            //save to file
            this.saveToFile(result);
        }else {
            Toast.makeText(context, "Error downloading file", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToFile(String fileContent) {

        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput("values.csv" , Context.MODE_PRIVATE);
            outputStream.write(fileContent.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
