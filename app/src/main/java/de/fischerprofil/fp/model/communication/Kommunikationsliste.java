package de.fischerprofil.fp.model.communication;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.fischerprofil.fp.listener.KommunikationslisteGeaendertListener;

public class Kommunikationsliste {

    private ArrayList<Kommunikation> mDataset;
    private KommunikationslisteGeaendertListener mListeGeaendertListener = null;

    public Kommunikationsliste(ArrayList<Kommunikation> lst) {
        super();
        mDataset = lst;
    }


    public Kommunikationsliste() {
        super();
        mDataset = new ArrayList<Kommunikation>();
    }


    public void add(Kommunikation a) {
        mDataset.add(a);
        OnKommunikationAdded(a.getPERSONNR());
    }


    public void add(JSONArray array) {

        //GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss"); //Format of our JSON dates
        //Gson gson = gsonBuilder.create();
        Gson gson=new Gson();
        Kommunikation Kommunikation;
        try {
            for (int i = 0; i<=array.length(); i++) {
                Kommunikation = gson.fromJson(array.getJSONObject(i).toString(), Kommunikation.class);
                this.add(Kommunikation);
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


    public ArrayList<Kommunikation> getDataset() {

        return mDataset;
    }


    private void OnKommunikationAdded(String s) {
        if (mListeGeaendertListener!=null) {
            mListeGeaendertListener.onKommunikationslisteGeaendert(s);
            }
    }


    public void setKommunikationlisteGeaendertListener(KommunikationslisteGeaendertListener lsn) {
        mListeGeaendertListener = lsn;
    }


    public void Clear() {
        mDataset.clear();
        OnKommunikationAdded("");
    }
}
