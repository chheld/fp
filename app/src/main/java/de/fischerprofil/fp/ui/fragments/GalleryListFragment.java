package de.fischerprofil.fp.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.fischerprofil.fp.AppController;
import de.fischerprofil.fp.R;
import de.fischerprofil.fp.adapter.GalleryListAdapter;
import de.fischerprofil.fp.listener.EndlessRecyclerViewScrollListener;
import de.fischerprofil.fp.model.reference.GalleryImage;
import de.fischerprofil.fp.rest.HttpsJsonObjectRequest;
import de.fischerprofil.fp.rest.HttpsJsonTrustManager;
import de.fischerprofil.fp.rest.PicassoUtils;
import de.fischerprofil.fp.rest.RestUtils;
import de.fischerprofil.fp.ui.UIUtils;


@SuppressLint("ValidFragment")
public class GalleryListFragment extends Fragment {

    private AppController mAppController;
    private Context mContext;
    private GalleryListAdapter mAdapter;
    private int mSearchRequestCounter = 0;      // Zähler fuer die http-Anfragen
    private String mSearchString;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final String VOLLEY_TAG = "VOLLEY_TAG_ReferenceListFragment";
    private final String URL = RestUtils.getApiURL();

    private Picasso mPicasso;
    private ArrayList<GalleryImage> mDataset = new ArrayList<>();

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

        mPicasso = PicassoUtils.buildPicasso(mContext);

        View view = inflater.inflate(R.layout.fragment_recycleview_gallerylist, container, false);

        Integer rows = getArguments().getInt("rows");
        mSearchString = getArguments().getString("search", null); // evtl. übergebene SUCH-Parameter ermitteln

        if (rows==0) rows=3; // default Spalten für Anzeige setzen
        int numColumns = 3;

        //Setup layout manager
        //v1
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, rows);
        //v2
//        PreCachingGridLayoutManager layoutManager = new PreCachingGridLayoutManager(mContext, rows);
//        layoutManager.setExtraLayoutSpace(UIUtils.getScreenHeight(mContext)*2);

        //Setup Recyclerview
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 2 * numColumns); //TODO: löschen ??
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);

                //final Picasso picasso = PicassoUtils.buildPicasso(mContext);
                if (scrollState == 1 || scrollState == 0) {
                    mPicasso.resumeTag(mContext);

                } else {
                    mPicasso.pauseTag(mContext);
                }
            }
        });

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new mDataset needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                customLoadMoreDataFromApi(page);
            }
        });

        //Setup Swipe Layput
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_conctactlist_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doSearch(mSearchString);
            }
        });

        if (mSearchString != null) doSearch(mSearchString);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        // This will tell to Volley to cancel all the pending requests
        mAppController.cancelPendingRequests(VOLLEY_TAG);
//        Picasso.with(mContext).cancelTag(mContext);
        mPicasso.cancelTag(mContext);
    }

    // Append more data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void customLoadMoreDataFromApi(int offset) {
        // TODO: loading per page
        /// / Send an API request to retrieve appropriate data using the offset value as a parameter.
        // Deserialize API response and then construct new objects to append to the adapter
        // Add the new objects to the data source for the adapter
        //items.addAll(moreItems);

        // For efficiency purposes, notify the adapter of only the elements that got changed
        // curSize will equal to the index of the first element inserted because the list is 0-indexed
        //int curSize = adapter.getItemCount();
        //adapter.notifyItemRangeInserted(curSize, items.size() - 1);
        Toast.makeText(mContext, "TODO: Seite nachladen", Toast.LENGTH_SHORT).show();
//        Toast.makeText(mContext, "Seite " + offset + " nachladen", Toast.LENGTH_SHORT).show();
    }

    private void doSearch(String search) {

        if (search.length() < 1) {
            Toast.makeText(mContext, "Mindestens 2 Zeichen eingeben", Toast.LENGTH_SHORT).show();
        } else {
            UIUtils.makeToast(mContext, "Suche '" + search + "'");

            showProgressCircle(mSwipeRefreshLayout, true);

            //alte Liste löschen
            mDataset.clear();

            callAPIImageListByDir(URL + "/pics"); // TODO Parameter übergeben
//            callAPIImageListByDir(URL + "/pics"); // TODO Parameter übergeben
//            callAPIImageListByDir(URL + "/pics?qry="+ picURL + "&mask=*__*&sub=true");
        }
    }

    private void callAPIImageListByDir(String search) {

        // Increase counter for pending search requests
        mSearchRequestCounter++;

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray images = response.getJSONArray("images");
                    Log.v("JSON", images.length() + " Elemente gefunden");

                    // Daten in Array laden
                   for (int i = 0; i < images.length(); i++) {
                        GalleryImage image = new GalleryImage();
                        image.setName("Image_" + i);
                        image.setUrl(URL + "/" + images.get(i));
                        mDataset.add(image);
                    }
                    //Adapter zuweisen
                    mAdapter = new GalleryListAdapter(mContext, mDataset);
                    mRecyclerView.setAdapter(mAdapter);

                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        showProgressCircle(mSwipeRefreshLayout, false);
                        Toast.makeText(mContext, images.length() + " Einträge gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    showProgressCircle(mSwipeRefreshLayout, false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                mSearchRequestCounter--;
                if (mSearchRequestCounter < 1) showProgressCircle(mSwipeRefreshLayout, false); // Fortschritt ausblenden
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        AppController.getInstance().addToRequestQueue(req,VOLLEY_TAG);
    }

    private void showProgressCircle(final SwipeRefreshLayout s, final Boolean v) {
        s.setColorSchemeResources(R.color.settings_color);
        s.post(new Runnable() {
            @Override public void run() {
                s.setRefreshing(v);
            }
        });
    }
}


