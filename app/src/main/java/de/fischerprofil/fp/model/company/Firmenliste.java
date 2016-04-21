package de.fischerprofil.fp.model.company;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.fischerprofil.fp.listener.FirmenlisteGeaendertListener;

public class Firmenliste {

    private ArrayList<Firma> mListe;
    private FirmenlisteGeaendertListener mListeGeaendertListener = null;

    public Firmenliste(ArrayList<Firma> lst) {
        super();
        mListe = lst;
    }

    public Firmenliste() {
        super();
        mListe = new ArrayList<Firma>();
    }

    public void add(Firma a) {
        mListe.add(a);
        OnFirmaAdded(a.getFIRMANR());
    }

    public void add(JSONArray array) {

        //GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss"); //Format of our JSON dates
        //Gson gson = gsonBuilder.create();
        Gson gson=new Gson();
        Firma firma;
        try {
            for (int i = 0; i<=array.length(); i++) {
                firma = gson.fromJson(array.getJSONObject(i).toString(), Firma.class);
                this.add(firma);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void add(JSONObject json){
        try {
            this.add(json.getJSONArray("addresses"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Firma> getList() {

        return mListe;
    }

    private void OnFirmaAdded(String s) {
        if (mListeGeaendertListener!=null) {
            mListeGeaendertListener.onFirmenlisteGeaendert(s);
            }
    }

    public void setFirmenlisteGeaendertListener(FirmenlisteGeaendertListener lsn) {
        mListeGeaendertListener = lsn;
    }

    public void Clear() {
        mListe.clear();
        OnFirmaAdded("");
    }
}
