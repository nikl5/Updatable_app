package com.example.me.libupdater;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdatesChecker extends AsyncTask<UpdatesCheckListener, Void, Void> {

    private UpdatesCheckListener updatesCheckListener;
    private String updateInfoUrl;

    /**Actual version of code (the main number of update history)*/
    private int lastCodeVersion = -1;
    /**Name of actual version, shown to user*/
    private String lastVersionName = null;
    /**URL of actual app's apk storing*/
    private String lastAppURL = null;

    private boolean hasExecuted = false;

    public UpdatesChecker(String updateInfoUrl) {
        this.updateInfoUrl = updateInfoUrl;
    }

    /**@param updatesCheckListeners - [0]th element is the context of call*/
    @Override
    protected Void doInBackground(UpdatesCheckListener... updatesCheckListeners) {
        this.updatesCheckListener = updatesCheckListeners[0];
        checkUpdates();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (hasExecuted && isValidUpdatesInfo()) {
            updatesCheckListener.onUpdatesChecked(this);
        }
        else {
            updatesCheckListener.onUpdatesCouldNotCheck(this);
        }
    }

    /**
     * Checks last code version, last version name, last app url
     * and store them in RAM. These can be obtained through getters of this class UpdatesChecker
     */
    private void checkUpdates() {
        try {
            if (updatesCheckListener == null) throw new NullPointerException();

            URL url = new URL(updateInfoUrl); //MalformedURLException
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //IOException
            conn.setConnectTimeout(60000); // timing out in a minute
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            int idx;
            while((str = in.readLine()) != null) {
                idx = str.indexOf("code_version");
                if (idx != -1) {
                    str = str.substring(idx + ("code_version").length()).trim();
                    lastCodeVersion = Integer.parseInt(str);
                }
                idx = str.indexOf("version_name");
                if (idx != -1) {
                    str = str.substring(idx + ("version_name").length()).trim();
                    lastVersionName = str;
                }
                idx = str.indexOf("apk_URL");
                if (idx != -1) {
                    str = str.substring(idx + ("apk_URL").length()).trim();
                    lastAppURL = str;
                }
            }
            in.close();
            conn.disconnect();
            hasExecuted = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            updatesCheckListener.onUpdatesCouldNotCheck(this);
        } catch (IOException e) {
            e.printStackTrace();
            updatesCheckListener.onUpdatesCouldNotCheck(this);
        }
    }

    /**Actual version of code (the main number of update history)*/
    public int getLastCodeVersion() {
        return lastCodeVersion;
    }

    /**Name of actual version, shown to user*/
    public String getLastVersionName() {
        return lastVersionName;
    }

    /**URL of actual app's apk storing*/
    public String getLastAppURL() {
        return lastAppURL;
    }

    /*Is info about updates in proper format?*/
    public boolean isValidUpdatesInfo() {
        if ((lastCodeVersion >= 0)&&
                (lastVersionName != null)&&
                (lastAppURL != null))
            return true;
        else
            return false;
    }
}