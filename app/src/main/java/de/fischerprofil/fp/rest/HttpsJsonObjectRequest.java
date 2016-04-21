package de.fischerprofil.fp.rest;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.fischerprofil.fp.AppController;

public class HttpsJsonObjectRequest extends JsonObjectRequest {

    public HttpsJsonObjectRequest(int method,
                                  String url,
                                  JSONObject jsonRequest,
                                  Response.Listener<JSONObject> listener,
                                  Response.ErrorListener errorListener) {

        super(method, url, jsonRequest, listener, errorListener);
    }

    public HttpsJsonObjectRequest(String url,
                                  Response.Listener<JSONObject> listener,
                                  Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public HttpsJsonObjectRequest(String url,
                                  JSONObject jsonRequest,
                                  Response.Listener<JSONObject> listener,
                                  Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        String username = AppController.preferences.getString( "username", "oh no" );
        String pw = AppController.preferences.getString("password", "oh no");
        return createBasicAuthHeader(username, pw);
    }

    Map<String, String> createBasicAuthHeader(String username, String password) {

        Map<String, String> headerMap = new HashMap<String, String>();

        String credentials = username + ":" + password;
        String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headerMap.put("Authorization", "Basic " + encodedCredentials);
        //headerMap.put("Authorization", "Basic Y2hyaXN0b3BoLmhlbGRAZmlzY2hlcnByb2ZpbC5kZTpkZXBwMTI=" ); // test user
        return headerMap;
    }
}