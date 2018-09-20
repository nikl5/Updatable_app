package com.example.me.updatable_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new UpdateChecker().execute(this);
    }

    public void requestUpdate(final Float lastAppVersion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Доступно обновление приложения Updatable_upp до версии " +
                        lastAppVersion + " - желаете обновиться? " +
                        "Если вы согласны - вы будете перенаправлены к скачиванию APK файла,"
                        +" который затем нужно будет открыть.")
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                        /*        Intent intent = new Intent(Intent.ACTION_VIEW);
                                String apkUrl = "https://yadi.sk/d/yMEIx0XVw1f9Tw";
                                intent.setData(Uri.parse(apkUrl));

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);*/
                                new UpdateDownloader().execute();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void setTextOnTextView(final int textViewNumber, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = null;
                switch(textViewNumber) {
                    case 1:
                        textView = findViewById(R.id.textView1CurrentVersion);
                        break;
                    case 2:
                        textView = findViewById(R.id.textView2LatestVersion);
                        break;
                    case 3:
                        textView = findViewById(R.id.textView3DownloadingProgress);
                        break;
                }
                if (textView != null)
                    textView.setText(text);
            }
        });
    }
}
