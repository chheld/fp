package de.fischerprofil.fp.rest;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.fischerprofil.fp.AppController;

public class VolleyJsonObjectRequestHigh extends JsonObjectRequest {

    Request.Priority mPriority;

    public VolleyJsonObjectRequestHigh(String url,
                                       Response.Listener<JSONObject> listener,
                                       Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        this.setShouldCache(true);
        this.setPriority(Priority.HIGH);
    }


    public void setPriority(Request.Priority priority) {
        mPriority = priority;
    }

    @Override
    public Request.Priority getPriority() {
        // If you didn't use the setPriority method,
        // the priority is automatically set to HIGH

/*
        Priority.LOW // images, thumbnails, ...
        Priority.NORMAL // residual
        Priority.HIGH // descriptions, lists, ...
        Priority.IMMEDIATE // login, logout, ...
*/

        return mPriority != null ? mPriority : Priority.HIGH;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        String username = AppController.preferences.getString( "username", "oh no" );
        String pw = AppController.preferences.getString("password", "oh no");
        return createBasicAuthHeader(username,pw);
    }

    Map<String, String> createBasicAuthHeader(String username, String password) {

        Map<String, String> headerMap = new HashMap<String, String>();

        String credentials = username + ":" + password;
//        String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        headerMap.put("Authorization", "Basic " + encodedCredentials);

        return headerMap;
    }
}
