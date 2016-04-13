package de.fischerprofil.fp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.fischerprofil.fp.AppController;
import de.fischerprofil.fp.R;
import de.fischerprofil.fp.model.contact.Kontakt;
import de.fischerprofil.fp.rest.HttpsJsonObjectRequest;
import de.fischerprofil.fp.rest.HttpsJsonTrustManager;
import de.fischerprofil.fp.rest.RestUtils;
import de.fischerprofil.fp.ui.UIUtils;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private Context mContext;
    public View mView;
    private AppController mAppController;
    private final String VOLLEY_TAG = "VOLLEY_TAG_rvContactListAdapter";
    public static String URL = RestUtils.getApiURL();

    private ArrayList<Kontakt> mDataset;

    public ContactListAdapter(Context c, ArrayList<Kontakt> d) {

        mAppController = AppController.getInstance();
        mDataset = d;
        mContext = c;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

//        Context mContext = parent.getContext();
        //mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        mView = inflater.inflate(R.layout.cardview_contactlist, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(mView);

        return viewHolder;
    }

    // Involves populating mDataset into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Kontakt current = mDataset.get(position);


        // Populate the mDataset into the template view using the mDataset object
        holder.position = position;

        holder.ivIcon.setImageResource(current.getIcon());
        holder.tvKonkaktname.setText((current.getVORNAME() + " " + current.getNAME()).trim());
        holder.tvPersonnr.setText(current.getPERSONNR());
        holder.tvKdNr.setText(current.getFIRMANR());
        holder.tvKTxt.setText(current.getRELFIRMA_KTXT());

        callAPILookupFirmaFGKNZ2(URL + "/lookup?qry=RELZTNUM&tabname=PERSV1&result=ktxt&Sprache=de&ztkey=" + current.getVERWENDUNG1(), holder, position);
        callAPIKommunikationByPersonNr(URL + "/com?relperson__personnr=" + holder.tvPersonnr.getText(),holder,position);

        holder.layTelefon.setVisibility(View.GONE);
        holder.layMail.setVisibility(View.GONE);
        holder.layFunktion.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }

    private void callAPILookupFirmaFGKNZ2(final String search, final ViewHolder viewHolder, final Integer pos) {

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.v("Volley Response:%n %s", response.toString(4));
                    JSONArray lookup = response.getJSONArray("lookup");
                    String s = lookup.getJSONObject(0).getString("KTXT");
                    if (viewHolder.position == pos) {
                        viewHolder.tvFunktion.setText(s);
                        //viewHolder.layFunktion.setVisibility(View.VISIBLE);
                    }
                }
                catch (JSONException e) {
                    Log.e("JSON Error: ", e.toString());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error: ", error.toString());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) ;
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        mAppController.addToRequestQueue(req,VOLLEY_TAG);
    }

    private void callAPIKommunikationByPersonNr(final String search, final ViewHolder viewHolder, final Integer pos) {

        HttpsJsonTrustManager.allowAllSSL();  // SSL-Fehlermeldungen ignorieren

        HttpsJsonObjectRequest req = new HttpsJsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.v("Volley Response:%n %s", response.toString(4));
                    JSONArray communications = response.getJSONArray("communications");

                    if (communications.length() > 1 && viewHolder.position == pos) {

                        for (int i = 0; i<communications.length(); i++) {

                            String val;
                            String komart;
                            String bemerkung;
                            komart = communications.getJSONObject(i).getString("KOMART");
                            val = communications.getJSONObject(i).getString("NUMMER");
                            // TODO bemerkung = communications.getJSONObject(i).getString("BEMEKRUNG");

                            switch (komart) {

                                case "1": // Telefon
                                    viewHolder.tvTelefonnummer.setText(val);
                                    // TODO viewHolder.tvBemerkung.setText(bemerkung);
                                    //viewHolder.layTelefon.setVisibility(View.VISIBLE);
                                    break;

                                case "4": // email
                                    viewHolder.tvMailadresse.setText(val);
                                    // TODO viewHolder.tvBemerkung.setText(bemerkung);
                                    //viewHolder.layMail.setVisibility(View.VISIBLE);
                                    break;

                                case "2": // fax
                                    break;
                            }
                        }
                    }
                }
                catch (JSONException e) {
                    Log.e("JSON Error: ", e.toString());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error: ", error.toString());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) ;
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 3, 2));
        mAppController.addToRequestQueue(req,VOLLEY_TAG);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        //static statt public wg speicher-probs

        public Integer position;
        private Boolean isExpanded = false;

        public ImageView ivIcon;
        public ImageButton ivMehrOben;
        public TextView tvPersonnr;
        public TextView tvKonkaktname;
        public TextView tvKdNr;
        public TextView tvKTxt;
        public TextView tvFunktion;

        public TextView tvTelefonnummer;
        public TextView tvMailadresse;
        // TODO public TextView tvBemerkung;

        public RelativeLayout layKontakt;
        public RelativeLayout layFunktion;
        public RelativeLayout layTelefon;
        public RelativeLayout layMail;

        private RelativeLayout layMehr;
        private ImageView ivMehr;
        private TextView tvTitel;
        int rotationAngle = 0;

        public ViewHolder(View view) {
            super(view);

            ivIcon = (ImageView) view.findViewById(R.id.ivKontakt);
            tvPersonnr = (TextView) view.findViewById(R.id.tvPersonNr);
            tvKonkaktname = (TextView) view.findViewById(R.id.tvImageName);
            tvKdNr = (TextView) view.findViewById(R.id.tvKdNr);
            tvKTxt = (TextView) view.findViewById(R.id.tvKTxt);
            tvFunktion = (TextView) view.findViewById(R.id.tvFunktion);
            ivMehrOben = (ImageButton) view.findViewById(R.id.ivMehrOben);

            tvTelefonnummer = (TextView) view.findViewById(R.id.tvTelefonnummer);
            tvMailadresse = (TextView) view.findViewById(R.id.tvMailadresse);
            // TODO bemerkung anzeigen

            layKontakt = (RelativeLayout) view.findViewById(R.id.layKontakt);
            layFunktion = (RelativeLayout) view.findViewById(R.id.layFunktion);
            layTelefon = (RelativeLayout) view.findViewById(R.id.layTelefon);
            layMail = (RelativeLayout) view.findViewById(R.id.layMail);

            layMehr = (RelativeLayout) view.findViewById(R.id.layMehr);
            ivMehr = (ImageView) view.findViewById(R.id.ivMehr);
            tvTitel = (TextView) view.findViewById(R.id.tvTitel);

            layKontakt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), tvPersonnr.getText(), Toast.LENGTH_SHORT).show();
                }
            });

            if (tvTelefonnummer.getText().toString()!="") {
                layTelefon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCallDialog(v, tvTelefonnummer.getText().toString());
                    }
                });
            }

            if (tvMailadresse.getText().toString()!="") {
                layMail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMailDialog(v, tvMailadresse.getText().toString());
                    }
                });
            }

            //layMehr.setOnClickListener(new View.OnClickListener() {
            ivMehrOben.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //UIUtils.makeToast(v.getContext(), "Daten für " + position.toString() + " anzeigen ..."); //TEST

                    if (!tvTelefonnummer.getText().equals("<Telefonnummer>"))
                        layTelefon.setVisibility(View.VISIBLE);

                    if (!tvMailadresse.getText().equals("<Mailadresse>"))
                        layMail.setVisibility(View.VISIBLE);

                    layFunktion.setVisibility(View.VISIBLE);

                    //ivMehrOben.setVisibility(View.INVISIBLE); //TODO: geht nicht - layoutfehler
                    //layMehr.setVisibility(View.GONE); //TODO: geht nicht - layoutfehler


/*
                    if (isExpanded==true) {

                        tvTitel.setText("Mehr anzeigen");

                    } else {

                        tvTitel.setText("Weniger anzeigen");

                    }

                    isExpanded =! isExpanded;
*/

/*

                    //v.setVisibility( v.isShown() ? View.GONE : View.VISIBLE );


                    if (isExpanded==true) {
//                    if (tvTitel.getText().equals("Weniger anzeigen")) {
                        layTelefon.setVisibility(View.GONE);
                        layMail.setVisibility(View.GONE);
                        layFunktion.setVisibility(View.GONE);
                        tvTitel.setText("Mehr anzeigen");

                        ObjectAnimator anim = ObjectAnimator.ofFloat(ivMehr, "rotation", rotationAngle, rotationAngle + 180);
                        anim.setDuration(500);
                        anim.start();
                        rotationAngle += 180;
                        rotationAngle = rotationAngle%360;

                    } else {

                        // TODO: erst laden wenn anzeige gewünscht
                        UIUtils.makeToast(v.getContext(), "Daten für " + tvPersonnr.getText() + " nachladen ..."); //TEST

//                        callAPILookupFirmaFGKNZ2(URL + "/lookup?qry=RELZTNUM&tabname=PERSV1&result=ktxt&Sprache=de&ztkey=" + this.getVERWENDUNG1(), v, position);
//                        callAPIKommunikationByPersonNr(URL + "/com?relperson__personnr=" + v.tvPersonnr.getText(),v,position);

                        layTelefon.setVisibility(View.VISIBLE);
                        layMail.setVisibility(View.VISIBLE);
                        layFunktion.setVisibility(View.VISIBLE);
                        tvTitel.setText("Weniger anzeigen");

                        ObjectAnimator anim = ObjectAnimator.ofFloat(ivMehr, "rotation", rotationAngle, rotationAngle + 180);
                        anim.setDuration(500);
                        anim.start();
                        rotationAngle += 180;
                        rotationAngle = rotationAngle%360;
                    }
                    isExpanded =! isExpanded;
*/

                }

            });

        }

        private void showCallDialog(View v, String nr) {

            String uri = "tel:" + nr.replaceAll("[^0-9|\\+]", "");

            UIUtils.makeToast(v.getContext(), uri); //TEST

            ImageButton img = (ImageButton) v.findViewById(R.id.btnCall);

            try {
                if (nr != null && nr != "") {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "02737508174")); //TODO: Nummer übernehmen
                    v.getContext().startActivity(intent);
                } else {
                    img.setVisibility(View.GONE);
                }
            }
            catch (Exception e) {
                UIUtils.makeToast(v.getContext(), "Telefon-App kann nicht angezeigt werden");
            }
        }

        private void showMailDialog(View v, String nr) {

            UIUtils.makeToast(v.getContext(), "Starte Mail-App ..."); //TEST

            String uri = "mail:" + nr.replaceAll("[^0-9|\\+]", "");
            ImageButton img = (ImageButton) v.findViewById(R.id.btnMail);

            try {
                if (nr != null && nr != "") {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("text/plain");
//                    emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"Recipient@mailadress.com"});
                    emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{nr});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "test betreff");
                    emailIntent.putExtra(Intent.EXTRA_TEXT   , "test Body");
                    v.getContext().startActivity(Intent.createChooser(emailIntent, "Mail versenden mit ..."));

                } else {
                    img.setVisibility(View.GONE);
                }
            }
            catch (Exception e) {
                UIUtils.makeToast(v.getContext(), "Mail-App kann nicht angezeigt werden");
            }
        }
    }
}
