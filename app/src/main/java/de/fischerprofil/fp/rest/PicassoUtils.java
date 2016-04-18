package de.fischerprofil.fp.rest;

import android.content.Context;
import android.util.Base64;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.fischerprofil.fp.AppController;

/**
 * Created by held on 26.01.2016.
 */
public class PicassoUtils {

    public static Picasso buildPicasso(Context context){

        OkHttpClient httpClient = HttpsPicassoTrustAllCerts.createAcceptAllClient(); //SSL-Zertifikate ugnorieren

        String username = AppController.preferences.getString( "username", "oh no" );
        String pw = AppController.preferences.getString("password", "oh no");
        String credentials = username + ":" + pw;
        final String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        httpClient.interceptors().add(new Interceptor() {

            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                com.squareup.okhttp.Request.Builder newRequest = chain.request().newBuilder();
                newRequest.header("Authorization","Basic " + encodedCredentials );
                return chain.proceed(newRequest.build());
            }
        });

        Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(httpClient))
                //.memoryCache(new LruCache(context))
                //.memoryCache(new LruCache(24000))
                .build();
        picasso.setIndicatorsEnabled(true);
        //picasso.setLoggingEnabled(true);
        //Picasso.setSingletonInstance(picasso);
        return picasso;
    }
}
