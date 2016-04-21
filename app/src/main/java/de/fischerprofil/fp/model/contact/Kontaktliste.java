package de.fischerprofil.fp.model.contact;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.fischerprofil.fp.listener.KontaktlisteGeaendertListener;

public class Kontaktliste {

    private ArrayList<Kontakt> mDataset;
    private KontaktlisteGeaendertListener mListeGeaendertListener = null;

    public Kontaktliste(ArrayList<Kontakt> lst) {
        super();
        mDataset = lst;
    }


    public Kontaktliste() {
        super();
        mDataset = new ArrayList<Kontakt>();
    }


    public void add(Kontakt a) {
        mDataset.add(a);
        OnKontaktAdded(a.getPERSONNR());
    }


    public void add(JSONArray array) {

        //GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss"); //Format of our JSON dates
        //Gson gson = gsonBuilder.create();
        Gson gson=new Gson();
        Kontakt kontakt;
        try {
            for (int i = 0; i<=array.length(); i++) {
                kontakt = gson.fromJson(array.getJSONObject(i).toString(), Kontakt.class);
                this.add(kontakt);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void add(JSONObject json){
        try {
            this.add(json.getJSONArray("orders"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<Kontakt> getDataset() {

        return mDataset;
    }


    private void OnKontaktAdded(String s) {
        if (mListeGeaendertListener!=null) {
            mListeGeaendertListener.onKontaktlisteGeaendert(s);
            }
    }


    public void setKontaktlisteGeaendertListener(KontaktlisteGeaendertListener lsn) {
        mListeGeaendertListener = lsn;
    }


    public void Clear() {
        mDataset.clear();
        OnKontaktAdded("");
    }
}
