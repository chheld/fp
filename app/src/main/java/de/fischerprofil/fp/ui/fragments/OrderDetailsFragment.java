package de.fischerprofil.fp.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.fischerprofil.fp.AppController;
import de.fischerprofil.fp.R;
import de.fischerprofil.fp.StringUtils;
import de.fischerprofil.fp.model.address.Adresse;
import de.fischerprofil.fp.model.company.Firma;
import de.fischerprofil.fp.model.contact.Kontakt;
import de.fischerprofil.fp.model.order.Auftrag;
import de.fischerprofil.fp.rest.HttpsJsonObjectRequest;
import de.fischerprofil.fp.rest.HttpsJsonTrustManager;
import de.fischerprofil.fp.rest.RestUtils;
import de.fischerprofil.fp.ui.UIUtils;

public  class OrderDetailsFragment extends Fragment {

    private AppController mAppController;
    private Context mContext;
    private View mView;
    private Auftrag mAuftrag;
    private String mMapsAddress;
    private ProgressBar progressBarAuftrag;
    private String mANr = "";
    private TextView tvVertreterName;
    private ProgressBar pbVertreter;
    private TextView tvLieferadresse;
    private ProgressBar pbLieferadresse;
    private ProgressBar pbarKunde;
    private final String VOLLEY_TAG = "VOLLEY_TAG_OrderDetailsFragment";
    private final String URL = RestUtils.getApiURL();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mAppController = AppController.getInstance();
        mContext = getActivity();
        mView = inflater.inflate(R.layout.fragment_orderdetails, container, false);
        progressBarAuftrag = (ProgressBar) mView.findViewById(R.id.progressBarAuftrag);

        mANr = getArguments().getString("anr");
        //mANr = "400006"; // TEST

        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_orderdetails_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callAPIOrderByANR(URL + "/orders?qry=byANr&anr=" + mANr);
            }
        });

        callAPIOrderByANR(URL + "/orders?qry=byANr&anr=" + mANr);

        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        // This will tell to Volley to cancel all the pending requests
        mAppController.cancelPendingRequests(VOLLEY_TAG);
    }

    private void setColorStatusIcon(ImageView iv, int level) {
        ClipDrawable cdAB = (ClipDrawable) iv.getDrawable();
        cdAB.setLevel(cdAB.getLevel() + level); // min 0 - max 10000
    }

    private void callAPIContactsByPersonNr(final String search) {

        // OnClickListener: erneut laden mit Klick auf Icon
        ImageView img = (ImageView) mView.findViewById(R.id.ivKontakt);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPIContactsByPersonNr(search);
            }
        });

        pbVertreter.setVisibility(View.VISIBLE);

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    JSONArray contact = response.getJSONArray("contacts");
                    JSONObject jsonA = contact.getJSONObject(0);
                    Gson gson = new Gson();
                    Kontakt kontakt = gson.fromJson(contact.getJSONObject(0).toString(), Kontakt.class);
                    String vname = kontakt.getVORNAME() + " ";
                    String nachname = kontakt.getNAME();
                    tvVertreterName.setText(vname + nachname);
                    pbVertreter.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    VolleyLog.e("Error: ", e.getMessage());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    pbVertreter.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                pbVertreter.setVisibility(View.GONE);
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        mAppController.addToRequestQueue(req, VOLLEY_TAG);
    }

    private void callAPIAdresseByAdresseNr(final String search) {

        // OnClickListener: erneut laden mit Klick auf Icon
        ImageView imv = (ImageView) mView.findViewById(R.id.ivLKW);
        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPIAdresseByAdresseNr(search);
            }
        });

        pbLieferadresse.setVisibility(View.VISIBLE);

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    JSONArray address = response.getJSONArray("addresses");
                    Gson gson = new Gson();
                    Adresse adresse = gson.fromJson(address.getJSONObject(0).toString(), Adresse.class);
                    String adr1 = adresse.getZUSATZ1();
                    if (StringUtils.IsNotNullOrEmpty(adr1)) adr1 = adr1 + "\n";
                    String adr2 = adresse.getZUSATZ2();
                    if (StringUtils.IsNotNullOrEmpty(adr2)) adr2 = adr2 + "\n";
                    String str = adresse.getSTRASSE();
                    if (StringUtils.IsNotNullOrEmpty(str)) str = str + "\n";
                    String plz = adresse.getPLZORT();
                    if (StringUtils.IsNotNullOrEmpty(plz)) plz = plz + " ";
                    String ort = adresse.getORT();
                    mMapsAddress = str + "," + plz + " " + ort;
                    tvLieferadresse.setText(adr1 + adr2 + str + plz + ort);
                    pbLieferadresse.setVisibility(View.GONE);

                } catch (JSONException e) {

                    e.printStackTrace();
                    VolleyLog.e("Error: ", e.getMessage());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    pbLieferadresse.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                pbLieferadresse.setVisibility(View.GONE);
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        mAppController.addToRequestQueue(req, VOLLEY_TAG);
    }

    private void callAPIFirmaByFirmaNr(final String search) {

        // OnClickListener: erneut laden mit Klick auf Icon
        ImageView img = (ImageView) mView.findViewById(R.id.ivFirma);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPIFirmaByFirmaNr(search);
            }
        });

        pbarKunde.setVisibility(View.VISIBLE);

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("Volley Response:%n %s", response.toString(4));
                    JSONArray companies = response.getJSONArray("companies");
                    Gson gson = new Gson();
                    Firma firma = gson.fromJson(companies.getJSONObject(0).toString(), Firma.class);
                    TextView tvKlassifizierung = (TextView) mView.findViewById(R.id.tvKlassifizierung);
                    //tvKlassifizierung.setText(firma.getFGKNZ_2());
                    pbarKunde.setVisibility(View.GONE);  // Fortschritt ausblenden

                    // Klartext der Klassifizierung nachladen
                    callAPILookupFirmaFGKNZ2(URL + "/lookup?qry=relztgk&tabname=KGRKNZ2&result=ktxt&ztkey=" + firma.getFGKNZ_2());
                }
                catch (JSONException e) {
                    Log.e("Volley Error: ", e.toString());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    pbarKunde.setVisibility(View.GONE);  // Fortschritt ausblenden
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error: ", error.toString());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                pbarKunde.setVisibility(View.GONE);  // Fortschritt ausblenden
            }
        }) ;
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        mAppController.addToRequestQueue(req,VOLLEY_TAG);
    }

    private void callAPIOrderByANR(final String search) {

        // OnClickListener: erneut laden mit Klick auf Icon
        ImageView img = (ImageView) mView.findViewById(R.id.ivOrder);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPIOrderByANR(search);
            }
        });

        //progressBarAuftrag.setVisibility(View.VISIBLE);

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("Volley Response:%n %s", response.toString(4));
                    JSONArray orders = response.getJSONArray("orders");
                    Gson gson = new Gson();
                    mAuftrag = gson.fromJson(orders.getJSONObject(0).toString(), Auftrag.class);
                    setupLayout(mView);
                    progressBarAuftrag.setVisibility(View.GONE);  // Fortschritt ausblenden
                    showProgressCircle(mSwipeRefreshLayout, false);
            }
                catch (JSONException e) {
                    Log.e("Volley Error: ", e.toString());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    progressBarAuftrag.setVisibility(View.GONE);  // Fortschritt ausblenden
                    showProgressCircle(mSwipeRefreshLayout, false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error: ", error.toString());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                progressBarAuftrag.setVisibility(View.GONE);  // Fortschritt ausblenden
                showProgressCircle(mSwipeRefreshLayout, false);
            }
        }) ;
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        mAppController.addToRequestQueue(req,VOLLEY_TAG);
    }

    private void callAPILookupFirmaFGKNZ2(String search) {

        pbarKunde.setVisibility(View.VISIBLE);

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("Volley Response:%n %s", response.toString(4));

                    JSONArray lookup = response.getJSONArray("lookup");
                    String s = lookup.getJSONObject(0).getString("KTXT");
                    TextView tvKlasse = (TextView) mView.findViewById(R.id.tvKlassifizierung);
                    tvKlasse.setText(s);
                    pbarKunde.setVisibility(View.GONE);  // Fortschritt ausblenden
                }
                catch (JSONException e) {
                    Log.e("Volley Error: ", e.toString());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    pbarKunde.setVisibility(View.GONE);  // Fortschritt ausblenden
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error: ", error.toString());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                pbarKunde.setVisibility(View.GONE);  // Fortschritt ausblenden
            }
        }) ;
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        mAppController.addToRequestQueue(req,VOLLEY_TAG);
    }

    private void setupLayout(View view) {

        // Auftrag
        TextView tvANr = (TextView) view.findViewById(R.id.tvANr);
        TextView tvKW = (TextView) view.findViewById(R.id.tvKW);
        TextView tvKJ = (TextView) view.findViewById(R.id.tvKJ);
        TextView tvBemerkung = (TextView) view.findViewById(R.id.tvImageName); //TODO: tvName in tvBemerkung ändern
        TextView tvBestellnummer = (TextView) view.findViewById(R.id.tvBestellnummer);
        TextView tvKommission = (TextView) view.findViewById(R.id.tvKommission);

        // Status
        TextView tvStatus2 = (TextView) view.findViewById(R.id.tvStatus2);
        TextView tvzDesc = (TextView) view.findViewById(R.id.tvZDesc);
        TextView tvSpezifizierung = (TextView) view.findViewById(R.id.tvSpezifizierung);

        // Verkäufer
        pbVertreter = (ProgressBar) view.findViewById(R.id.progressBarVertreter);
        TextView tvVertr1 = (TextView) view.findViewById(R.id.tvVertreter1);
        tvVertreterName = (TextView) view.findViewById(R.id.tvVertreterName);

        // Termine
        TextView tvKdWunschTermin = (TextView) view.findViewById(R.id.tvKdWunschTermin);
        TextView tvKdBestTermin = (TextView) view.findViewById(R.id.tvKdBestTermin);
        TextView tvProdPlanTermin = (TextView) view.findViewById(R.id.tvProdPlanTermin);
        TextView tvProdDispTermin = (TextView) view.findViewById(R.id.tvProdDispTermin);

        // Kunde
        pbarKunde = (ProgressBar) view.findViewById(R.id.progressBarKunde);
        TextView tvKdNr = (TextView) view.findViewById(R.id.tvKdNr);
        TextView tvKTxt = (TextView) view.findViewById(R.id.tvKTxt);
        TextView tvKlasse = (TextView) view.findViewById(R.id.tvKlassifizierung);

        // Lieferanschrift
        TextView tvLieferadresseNr = (TextView) view.findViewById(R.id.tvAdresseNr);
        pbLieferadresse = (ProgressBar) view.findViewById(R.id.progressBarLieferadresse);
        tvLieferadresse = (TextView) view.findViewById(R.id.tvLieferAdresse);

        // Auftragswerte
        TextView tvNetto0 = (TextView) view.findViewById(R.id.tvNetto0);
        TextView tvGesamtrabatt = (TextView) view.findViewById(R.id.tvGesamtrabatt);
        TextView tvNetto1 = (TextView) view.findViewById(R.id.tvNetto1);
        TextView tvZusatzaufwand = (TextView) view.findViewById(R.id.tvZusatzaufwand);
        TextView tvNetto2 = (TextView) view.findViewById(R.id.tvNetto2);
        TextView tvUmsatzsteuer = (TextView) view.findViewById(R.id.tvUmsatzsteuer);
        TextView tvBrutto = (TextView) view.findViewById(R.id.tvBrutto);

        // -----------------------------------------------------------------------------------------------

        if (mAuftrag!=null) {

            // Auftrag anzeigen
            tvANr.setText(mAuftrag.getANR());
            tvKW.setText(Integer.toString(mAuftrag.getKW())); //TODO: wohin damit ?
            tvKJ.setText(Integer.toString(mAuftrag.getKJ()));

            if(StringUtils.IsNotNullOrEmpty(mAuftrag.getBEMERKUNG())) {
                tvBemerkung.setText(mAuftrag.getBEMERKUNG());
            } else {
                tvBemerkung.setVisibility(View.GONE);
            }

            tvBestellnummer.setText(mAuftrag.getBELEGNRBEST());

            if(tvBestellnummer.getText().toString().trim().length()==0) {
                tvBestellnummer.setVisibility(View.GONE);
            } else
            {
                tvBestellnummer.setText("Bestellung " + mAuftrag.getBELEGNRBEST());
            }
            tvKommission.setText(mAuftrag.getKOMM());
            if(tvKommission.getText().toString().trim().length()==0) {
                tvKommission.setVisibility(View.GONE);
            } else
            {
                tvKommission.setText("Kommission " + mAuftrag.getKOMM());
            }

            // Vertreter nachladen
            tvVertr1.setText(mAuftrag.getVERTRETER1());
            if(tvVertr1.getText().toString().trim().length()==0) {
                tvVertr1.setVisibility(View.GONE);
            }
            else
            {
                callAPIContactsByPersonNr(URL + "/contacts?relperson__personnr=" + mAuftrag.getVERTRETER1()); // '%' = %25
            }

            // Status anzeigen
            tvzDesc.setText(StringUtils.asUpperCaseFirstChar(mAuftrag.getZDESC()));
            if (tvzDesc.getText().toString().trim().length()==0)  tvzDesc.setVisibility(View.GONE);
            tvStatus2.setText(mAuftrag.getSTATUS2());
            if(tvStatus2.getText().toString().trim().length()==0)  tvStatus2.setVisibility(View.GONE);
            tvSpezifizierung.setText(mAuftrag.getSTATUS1());
            if(tvSpezifizierung.getText().toString().trim().length()==0)  tvSpezifizierung.setVisibility(View.GONE);
            if (mAuftrag.getSEGM2ZART()==191) setColorStatusIcon((ImageView) view.findViewById(R.id.ivAB), 10000);
            if (mAuftrag.getSEGM2ZART()==193) setColorStatusIcon((ImageView) view.findViewById(R.id.ivAB), 5000);
            if (mAuftrag.getSEGM4ZART()==211) setColorStatusIcon((ImageView) view.findViewById(R.id.ivLS), 10000);
            if (mAuftrag.getSEGM4ZART()==212) setColorStatusIcon((ImageView) view.findViewById(R.id.ivLS), 5000);
            if (mAuftrag.getSEGM5ZART()==220) setColorStatusIcon((ImageView) view.findViewById(R.id.ivENT), 10000);
            if (mAuftrag.getSEGM5ZART()==221) setColorStatusIcon((ImageView) view.findViewById(R.id.ivENT), 5000);
            if (mAuftrag.getSEGM6ZART()==231) setColorStatusIcon((ImageView) view.findViewById(R.id.ivRG), 10000);
            if (mAuftrag.getSEGM6ZART()==232) setColorStatusIcon((ImageView) view.findViewById(R.id.ivRG), 5000);

            // Termine anzeigen
            tvKdWunschTermin.setText(mAuftrag.getUSEINTREFFTERMIN()); //TODO: USEintreffTermin nicht in REST Abfrage ?
            if(tvKdWunschTermin.getText().toString().trim().length()==0) {
                tvKdWunschTermin.setVisibility(View.GONE);
                TextView lbl = (TextView) view.findViewById(R.id.lblKdWunschTermin);
                lbl.setVisibility(View.GONE);
            }

            tvKdBestTermin.setText(mAuftrag.getUSEINTREFFTBEST()); // USEintreffTBest
            if(tvKdBestTermin.getText().toString().trim().length()==0) {
                tvKdBestTermin.setVisibility(View.GONE);
                TextView lbl = (TextView) view.findViewById(R.id.lblKdBestTermin);
                lbl.setVisibility(View.GONE);
            }

            tvProdPlanTermin.setText(mAuftrag.getSEGM1TERM()); // Segm1.Term
            if(tvProdPlanTermin.getText().toString().trim().length()==0) {
                tvProdPlanTermin.setVisibility(View.GONE);
                TextView lbl = (TextView) view.findViewById(R.id.lblProdPlanTermin);
                lbl.setVisibility(View.GONE);
            }

            tvProdDispTermin.setText(mAuftrag.getSEGM2TERM()); // Segm2.Term
            if(tvProdDispTermin.getText().toString().trim().length()==0) {
                tvProdDispTermin.setVisibility(View.GONE);
                TextView lbl = (TextView) view.findViewById(R.id.lblProdDispTermin);
                lbl.setVisibility(View.GONE);
            }

            // Kunde anzeigen
            tvKdNr.setText(mAuftrag.getMNR());
            tvKTxt.setText(mAuftrag.getKTXT());
            if(tvKTxt.getText().toString().trim().length()==0)  tvKTxt.setVisibility(View.GONE);

            if (StringUtils.IsNullOrEmpty(mAuftrag.getMNR())) {
                tvKlasse.setVisibility(View.GONE);
            }
            else
            {
                callAPIFirmaByFirmaNr(URL + "/companies?qry=firmabyfirmanr&firmanr=" + mAuftrag.getMNR());
            }

            // Lieferanschrift nachladen
            tvLieferadresseNr.setText(mAuftrag.getADRNR2());
            if(tvLieferadresseNr.getText().toString().trim().length()==0) {
                tvLieferadresseNr.setVisibility(View.GONE);
            }
            else
            {
                callAPIAdresseByAdresseNr(URL + "/addresses?qry=AddressByAdresseNr&reladresse__adressenr=" + mAuftrag.getADRNR2());

                ImageButton img = (ImageButton) view.findViewById(R.id.btnMaps);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMaps();
                    }
                });
            }
            // Auftragswerte anzeigen
            tvNetto0.setText(StringUtils.getGermanCurrencyFormat(mAuftrag.getACPPARTNETTO0()));
            tvGesamtrabatt.setText(StringUtils.getGermanCurrencyFormat(mAuftrag.getRABSUM()));
            tvNetto1.setText(StringUtils.getGermanCurrencyFormat(mAuftrag.getNETTO1()));
            tvZusatzaufwand.setText(StringUtils.getGermanCurrencyFormat(mAuftrag.getZSUM()));
            tvNetto2.setText(StringUtils.getGermanCurrencyFormat(mAuftrag.getNETTO2()));
            tvUmsatzsteuer.setText(StringUtils.getGermanCurrencyFormat(mAuftrag.getMWSTWERT()));
            tvBrutto.setText(StringUtils.getGermanCurrencyFormat(mAuftrag.getBRUTTO()));
        }
    }

    private void showMaps() {

        UIUtils.makeToast(mContext, "Suche Adresse \n" + mMapsAddress);

        try {
            String address = mMapsAddress; //"Kiefernweg 15, 57250 Netphen"; // TEST
            Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address));
            startActivity(geoIntent);
        }
        catch (Exception e) {
            UIUtils.makeToast(mContext, "Keine Navi-App installiert");
        }
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