package de.fischerprofil.fp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.fischerprofil.fp.model.order.Auftrag;

public class OrderDetailsAdapter extends ArrayAdapter<Auftrag> {

    public OrderDetailsAdapter(Context c, ArrayList<Auftrag> o) {

        super(c, de.fischerprofil.fp.R.layout.fragment_orderdetails, o);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Auftrag ord = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(de.fischerprofil.fp.R.layout.fragment_orderdetails, parent, false);

            // Viewholder an View anbinden
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(de.fischerprofil.fp.R.id.ivKontakt);
            viewHolder.tvKdNr = (TextView) convertView.findViewById(de.fischerprofil.fp.R.id.tvKdNr);
            viewHolder.tvKTxt = (TextView) convertView.findViewById(de.fischerprofil.fp.R.id.tvKTxt);
            // Hier weitere Anbindungen hinzufuegen
            convertView.setTag(viewHolder);
        }
        else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the mDataset into the template view using the mDataset object
        viewHolder.tvKTxt.setText(ord.getKTXT());
        viewHolder.tvKdNr.setText(ord.getMNR());
        viewHolder.ivIcon.setImageResource(ord.getIcon());
        // Hier weitere Zuweisungen hinzufuegen

        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {

        ImageView ivIcon;
        TextView tvKdNr;
        TextView tvKTxt;
        // Hier weitere Holder-Eigenschaften hinzufuegen
    }

}
