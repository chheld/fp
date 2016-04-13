package de.fischerprofil.fp.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.fischerprofil.fp.AppController;
import de.fischerprofil.fp.R;
import de.fischerprofil.fp.model.order.Auftrag;
import de.fischerprofil.fp.model.order.Auftragsliste;
import de.fischerprofil.fp.rest.HttpsJsonObjectRequest;
import de.fischerprofil.fp.rest.HttpsJsonTrustManager;
import de.fischerprofil.fp.rest.RestUtils;
import de.fischerprofil.fp.ui.OrderDetailsActivity;
import de.fischerprofil.fp.adapter.OrderListAdapter;

@SuppressLint("ValidFragment")
public class OrderListFragment extends Fragment {

    private Context mContext;
    private Auftragsliste mAuftragsliste = new Auftragsliste();
    private OrderListAdapter mAdapter = null;
    private int mSearchRequestCounter = 0;      // Zähler für die http-Anfragen initialisieren
    private String mSearchString;
    private ListView listView;
    //private ProgressBar progressBar;
    private AppController mAppController;
    private final String VOLLEY_TAG = "VOLLEY_TAG_OrderListFragment";
    private final String URL = RestUtils.getApiURL();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //TODO: Restore the fragment's state here
            mSearchString= savedInstanceState.getString("search");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO: Save the fragment's state here
        outState.putString("search", mSearchString);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mAppController = AppController.getInstance();
        mContext =  getActivity();

        View view = inflater.inflate(R.layout.fragment_orderlist, container, false);
        listView = (ListView) view.findViewById(R.id.listview);
        //progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_conctactlist_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doSearch(mSearchString);
            }
        });

        mSearchString = getArguments().getString("search", null); // evtl. übergebene Such-Parameter ermitteln
        if (mSearchString != null) doSearch(mSearchString);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mAppController.cancelPendingRequests(VOLLEY_TAG); // Cancel all Volley pending requests
    }

    private void doSearch(String search) {

        if (search.length() < 4) {
            Toast.makeText(mContext, "Mindestens 5 Zeichen eingeben", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Suche '" + search + "'", Toast.LENGTH_SHORT).show();

            //Adapter erzeugen und Listview zuweisen
            mAdapter = new OrderListAdapter(mContext, mAuftragsliste.getList());
            listView.setAdapter(mAdapter);

            //progressBar.setVisibility(View.VISIBLE);  // Fortschrittsanzeige anzeigen
            showProgressCircle(mSwipeRefreshLayout, true);

            // Bei Auswahl eines Listeneintrags neue Activity starten
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // This will tell to Volley to cancel all the pending requests
                    //mAppController.cancelPendingRequests(VOLLEY_TAG);
                    //progressBar.setVisibility(View.GONE);  // Fortschrittsanzeige ausblenden
                    showProgressCircle(mSwipeRefreshLayout, false);

                    Auftrag auftrag = (Auftrag) parent.getItemAtPosition(position);

                    //TODO: speichern des Auftrags in HOME - letzte Vorgänge

                    Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                    intent.putExtra("anr",auftrag.getANR());
                    startActivity(intent);
                }
            });

            //alte Liste löschen
            mAuftragsliste.Clear();

            // start http requests
            mSearchRequestCounter = 0;

            callAPIOrdersByANR(URL + "/orders?qry=OrderListByANr&anr=" + RestUtils.cleanURL(search) + "%25"); // '%' = %25
            callAPIOrdersByMNR(URL + "/orders?qry=OrderListByMNr&mnr=" + RestUtils.cleanURL(search) + "%25"); // '%' = %25
            callAPIOrdersByKTXT(URL + "/orders?qry=OrderListByKtxt&ktxt=" + RestUtils.cleanURL(search) + "%25"); // '%' = %25

            // TODO: Lade-Fkt in auftrag verlagern
            // Auftrag auftrag = new Auftrag();
            // auftrag.loadOrderDataByANR(mContext,URL+"/orders/anr?where=" + search);
        }
    }

    private void callAPIOrdersByANR(String search) {

        mSearchRequestCounter++; // Increase counter for pending search requests

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("Volley Response:%n %s", response.toString(4));
                    JSONArray orders = response.getJSONArray("orders");
                    mAuftragsliste.add(orders);
                    mAdapter.notifyDataSetChanged();
                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        //progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                        showProgressCircle(mSwipeRefreshLayout, false);
                        Toast.makeText(mContext, orders.length() + " Einträge über ANR gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("Volley Error: ", e.toString());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                    showProgressCircle(mSwipeRefreshLayout, false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error: ", error.toString());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                mSearchRequestCounter--;
                //if (mSearchRequestCounter < 1) progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                if (mSearchRequestCounter < 1) showProgressCircle(mSwipeRefreshLayout, false);
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        mAppController.addToRequestQueue(req, VOLLEY_TAG);
    }

    private void callAPIOrdersByMNR(String search) {

        mSearchRequestCounter++; // Increase counter for pending search requests

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("Volley Response:%n %s", response.toString(4));
                    JSONArray orders = response.getJSONArray("orders");
                    mAuftragsliste.add(orders);
                    mAdapter.notifyDataSetChanged();
                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        //progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                        showProgressCircle(mSwipeRefreshLayout, false);
                        Toast.makeText(mContext, orders.length() + " Einträge über MNR gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("Volley Error: ", e.toString());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                    showProgressCircle(mSwipeRefreshLayout, false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error: ", error.toString());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                mSearchRequestCounter--;
                //if (mSearchRequestCounter < 1) progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                if (mSearchRequestCounter < 1) showProgressCircle(mSwipeRefreshLayout, false);
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        mAppController.addToRequestQueue(req, VOLLEY_TAG);
    }

    private void callAPIOrdersByKTXT(String search) {

        mSearchRequestCounter++; // Increase Counter for Progressbar

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("Volley Response:%n %s", response.toString(4));
                    JSONArray orders = response.getJSONArray("orders");
                    mAuftragsliste.add(orders);
                    mAdapter.notifyDataSetChanged();
                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        //progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                        showProgressCircle(mSwipeRefreshLayout, false);
                        Toast.makeText(mContext, orders.length() + " Einträge über KTXT gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("Volley Error: ", e.toString());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                    showProgressCircle(mSwipeRefreshLayout, false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error: ", error.toString());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                mSearchRequestCounter--;
                //if (mSearchRequestCounter < 1) progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                if (mSearchRequestCounter < 1) showProgressCircle(mSwipeRefreshLayout, false);
                ;  // Fortschritt ausblenden
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        mAppController.addToRequestQueue(req, VOLLEY_TAG);
    }

    private void showProgressCircle(final SwipeRefreshLayout s, final Boolean v) {
        s.setColorSchemeResources(
                R.color.settings_color);
        s.post(new Runnable() {
            @Override
            public void run() {
                s.setRefreshing(v);
            }
        });
    }
}


