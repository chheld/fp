package de.fischerprofil.fp.rest;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.fischerprofil.fp.AppController;

public class RestUtils {

    private String mUsername;
    private String mPassword;

    private static String mRootURL = "";

    public static void makeToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static boolean isURLReachable() {
/*        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://google.com");   // Change to "http://google.com" for www  test.
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(1 * 1000);          // 1 s.
                urlc.connect();
                if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                    Log.d("isConnectedToServer1", "Success !");
                    return true;
                } else {
                    Log.d("isConnectedToServer1", "Failure !");
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }*/
        return false;
    }

    public static boolean isPingable (String url) {

        String str = "";

        try
        {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 8 " + "google.de");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            //StringBuffer output = new StringBuffer();
            StringBuilder output = new StringBuilder();
            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 0, i);
            reader.close();
            str = output.toString();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static String getApiURL(){

        if (AppController.inEmulatorMode) {
//            return "https://222.222.222.60/api";        // internal URL
            return getRootURL() + "/api";        // internal URL
        }
        else {
//            return "https://fpvk.fischerprofil.de/api"; // external URL
            return getRootURL() + "/api"; // external URL
        }
    }

    public static String getPicURL(){

        if (AppController.inEmulatorMode) {
//            return "https://222.222.222.60/api";        // internal URL
            return getRootURL() + "/pics";        // internal URL
        }
        else {
//            return "https://fpvk.fischerprofil.de/api"; // external URL
            return getRootURL() + "/pics"; // external URL
        }
    }

    public static String getRootURL() {

        if (AppController.inEmulatorMode) {
            mRootURL = "https://222.222.222.60";        // internal URL
            mRootURL = "https://fpvk.fischerprofil.de"; // external URL
            return mRootURL;
        }

        else {
            mRootURL = "https://fpvk.fischerprofil.de"; // external URL
            return mRootURL;
        }
    }


    public String getUsername() {return mUsername;}

    public String getPassword() {return mPassword;}

    public static String cleanURL(String s) {
        s = s.replace(" ","%20");
        s = s.replace(".","__");
        return s;
    }

}