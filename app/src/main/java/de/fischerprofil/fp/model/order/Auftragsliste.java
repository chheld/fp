package de.fischerprofil.fp.model.order;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.fischerprofil.fp.listener.AuftragslisteGeaendertListener;

public class Auftragsliste {

    private ArrayList<Auftrag> mListe;
    private AuftragslisteGeaendertListener mListeGeaendertListener = null;

    public Auftragsliste(ArrayList<Auftrag> lst) {
        super();
        mListe = lst;
    }


    public Auftragsliste() {
        super();
        mListe = new ArrayList<Auftrag>();
    }


    public void add(Auftrag a) {
        mListe.add(a);
        OnAuftragAdded(a.getANR());
    }


    public void add(JSONArray orders) {

        //GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss"); //Format of our JSON dates
        //Gson gson = gsonBuilder.create();
        Gson gson=new Gson();
        Auftrag auftrag;
        try {
            for (int i = 0; i<=orders.length(); i++) {
                auftrag = gson.fromJson(orders.getJSONObject(i).toString(), Auftrag.class);
                this.add(auftrag);
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


    public ArrayList<Auftrag> getList() {

        return mListe;
    }


    private void OnAuftragAdded(String s) {
        if (mListeGeaendertListener!=null) {
            mListeGeaendertListener.onAuftragslisteGeaendert(s);
            }
    }


    public void setAuftragslisteGeaendertListener(AuftragslisteGeaendertListener lsn) {
        mListeGeaendertListener = lsn;
    }


    public void Clear() {
        mListe.clear();
        OnAuftragAdded("");
    }
}
