package com.example.me.updatable_app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class UpdateDownloader extends AsyncTask<MainActivity, Void, Integer>  {
    MainActivity mainActivity = null;

    String path = "";

    @Override
    protected Integer doInBackground(MainActivity... mainActivities) {
        Log.d("TAG", "Started to update");
        this.mainActivity = mainActivities[0];
        path = "/sdcard/Updatable_app_v"+mainActivity.lastAppVersion+".apk";
        return downloadAPK("http://f0230501.xsph.ru/Updatable_app_"+mainActivity.lastAppVersion+".apk");
    }

    protected Integer downloadAPK(String... sUrl) {
        Log.d("TAG", "Started to download");
        try {
            URL url = new URL(sUrl[0]);
            URLConnection connection = url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(path);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    public void publishProgress(int progress) {
        mainActivity.setTextOnTextView(3, "downloading apk: " + progress + "%");
    }

    // begin the installation of downloaded APK
    @Override
    protected void onPostExecute(Integer result) {
        if (result == 1) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            this.mainActivity.startActivity(i);
        }
    }
}