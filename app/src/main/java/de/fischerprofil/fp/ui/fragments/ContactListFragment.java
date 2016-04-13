package de.fischerprofil.fp.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.fischerprofil.fp.AppController;
import de.fischerprofil.fp.R;
import de.fischerprofil.fp.model.contact.Kontaktliste;
import de.fischerprofil.fp.rest.HttpsJsonObjectRequest;
import de.fischerprofil.fp.rest.HttpsJsonTrustManager;
import de.fischerprofil.fp.rest.RestUtils;
import de.fischerprofil.fp.ui.UIUtils;
import de.fischerprofil.fp.adapter.ContactListAdapter;


@SuppressLint("ValidFragment")
public class ContactListFragment extends Fragment {

    private AppController mAppController;
    private Context mContext;
    private Kontaktliste mKontaktliste = new Kontaktliste();
    private ContactListAdapter mAdapter;
    private int mSearchRequestCounter = 0;      // Zaehler fuer die http-Anfragen
    private String mSearchString;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final String VOLLEY_TAG = "VOLLEY_TAG_ContactListFragment";
    private final String URL = RestUtils.getApiURL();


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //TODO: Restore the fragment's state here
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO: Save the fragment's state here
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        mAppController = AppController.getInstance();

        View view = inflater.inflate(R.layout.fragment_recycleview_contactlist, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_conctactlist_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doSearch(mSearchString);
            }
        });

        mSearchString = getArguments().getString("search", null); // evtl. übergebene SUCH-Parameter ermitteln
        if (mSearchString != null) doSearch(mSearchString);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        // This will tell to Volley to cancel all the pending requests
        mAppController.cancelPendingRequests(VOLLEY_TAG);
    }

    private void doSearch(String search) {

        if (search.length() < 1) {
            Toast.makeText(mContext, "Mindestens 2 Zeichen eingeben", Toast.LENGTH_SHORT).show();
        } else {
            UIUtils.makeToast(mContext, "Suche '" + search + "'");

            //Adapter erzeugen und zuweisen
            mAdapter = new ContactListAdapter(mContext, mKontaktliste.getDataset());
            mRecyclerView.setAdapter(mAdapter);

            showProgressCircle(mSwipeRefreshLayout, true);

            //alte Liste löschen
            mKontaktliste.Clear();

            // start http requests
            mSearchRequestCounter = 0;
            callAPIContactsByPersonNr(URL + "/contacts?relperson__personnr=" + RestUtils.cleanURL(search) + "%25"); // '%' = %25
            callAPIContactsByFirmaNr(URL + "/contacts?relfirma__firmanr=" + RestUtils.cleanURL(search) + "%25"); // '%' = %25
            callAPIContactsByFirmaKTxt(URL + "/contacts?relfirma__ktxt=" + RestUtils.cleanURL(search) + "%25"); // '%' = %25
            callAPIContactsByName(URL + "/contacts?relperson__name=" + RestUtils.cleanURL(search) + "%25"); // '%' = %25
            callAPIContactsByName(URL + "/contacts?relperson__vorname=" + RestUtils.cleanURL(search) + "%25"); // '%' = %25
        }
    }

    private void callAPIContactsByPersonNr(String search) {

        // Increase counter for pending search requests
        mSearchRequestCounter++;

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    JSONArray contacts = response.getJSONArray("contacts");
                    mKontaktliste.add(contacts);
                    mAdapter.notifyDataSetChanged();
                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        showProgressCircle(mSwipeRefreshLayout, false);
                        Toast.makeText(mContext, contacts.length() + " Einträge über PERSONNR gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showProgressCircle(mSwipeRefreshLayout, false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                mSearchRequestCounter--;
                if (mSearchRequestCounter < 1) showProgressCircle(mSwipeRefreshLayout, false); // Fortschritt ausblenden
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        AppController.getInstance().addToRequestQueue(req,VOLLEY_TAG);
    }

    private void callAPIContactsByFirmaNr(String search) {

        // Increase counter for pending search requests
        mSearchRequestCounter++;

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    JSONArray contacts = response.getJSONArray("contacts");
                    mKontaktliste.add(contacts);
                    mAdapter.notifyDataSetChanged();
                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        showProgressCircle(mSwipeRefreshLayout, false);
                        Toast.makeText(mContext, contacts.length() + " Einträge über FIRMANR gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showProgressCircle(mSwipeRefreshLayout, false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                mSearchRequestCounter--;
                if (mSearchRequestCounter < 1) showProgressCircle(mSwipeRefreshLayout, false); // Fortschritt ausblenden
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        AppController.getInstance().addToRequestQueue(req,VOLLEY_TAG);
    }

    private void callAPIContactsByFirmaKTxt(String search) {

        // Increase counter for pending search requests
        mSearchRequestCounter++;

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    JSONArray contacts = response.getJSONArray("contacts");
                    mKontaktliste.add(contacts);
                    mAdapter.notifyDataSetChanged();
                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        showProgressCircle(mSwipeRefreshLayout, false);
                        Toast.makeText(mContext, contacts.length() + " Einträge über FIRMA_KTXT gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showProgressCircle(mSwipeRefreshLayout, false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                mSearchRequestCounter--;
                if (mSearchRequestCounter < 1) showProgressCircle(mSwipeRefreshLayout, false); // Fortschritt ausblenden
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        AppController.getInstance().addToRequestQueue(req,VOLLEY_TAG);
    }

    private void callAPIContactsByName(String search) {

        // Increase counter for pending search requests
        mSearchRequestCounter++;

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    JSONArray contacts = response.getJSONArray("contacts");
                    mKontaktliste.add(contacts);
                    mAdapter.notifyDataSetChanged();
                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        showProgressCircle(mSwipeRefreshLayout, false);
                        Toast.makeText(mContext, contacts.length() + " Einträge zu NAME gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showProgressCircle(mSwipeRefreshLayout, false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                mSearchRequestCounter--;
                if (mSearchRequestCounter < 1) showProgressCircle(mSwipeRefreshLayout, false); // Fortschritt ausblenden
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        AppController.getInstance().addToRequestQueue(req,VOLLEY_TAG);
    }

    private void showProgressCircle(final SwipeRefreshLayout s, final Boolean v) {
        s.setColorSchemeResources(
                R.color.settings_color);
        s.post(new Runnable() {
            @Override public void run() {
                s.setRefreshing(v);
            }
        });
    }
}


