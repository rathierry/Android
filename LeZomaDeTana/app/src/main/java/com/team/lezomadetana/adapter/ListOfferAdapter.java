package com.team.lezomadetana.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.team.lezomadetana.R;
import com.team.lezomadetana.model.receive.Offer;

import java.util.List;

/**
 * Created by RaThierry on 12/09/2018.
 */
public class ListOfferAdapter extends BaseAdapter {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<Offer> offerList;

    // ===========================================================
    // Constructors
    // ===========================================================


    public ListOfferAdapter() {
    }

    public ListOfferAdapter(Activity activity, List<Offer> offerList) {
        this.activity = activity;
        this.offerList = offerList;
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    public int getCount() {
        return offerList.size();
    }

    @Override
    public Object getItem(int location) {
        return offerList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.layout_list_offer_row, null);

        // initialize view
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon_profile);
        TextView from = (TextView) convertView.findViewById(R.id.from);
        TextView txt_primary = (TextView) convertView.findViewById(R.id.txt_primary);
        TextView txt_secondary = (TextView) convertView.findViewById(R.id.txt_secondary);
        TextView iconText = (TextView) convertView.findViewById(R.id.icon_text);
        Button button = (Button) convertView.findViewById(R.id.answer_item);

        // getting request data for the row
        final Offer ofr = offerList.get(position);

        // display profile image
        // TODO TODO TODO

        // displaying text view data
        from.setText(Html.fromHtml("Nalefan\'i<b> *** . ***</b>"));
        // TODO TODO TODO

        txt_primary.setText(Html.fromHtml("<b>Manana OVY " + ofr.getQuantity() + " " + ofr.getUnitType().name()));
        txt_secondary.setText(Html.fromHtml("Vidiny: " + ofr.getPrice() + " " + activity.getResources().getString(R.string.app_payment_unity_type_text)));

        // displaying the first letter of From in icon text
        //iconText.setText(ofr.getProduct().substring(0, 1));
        // TODO TODO TODO

        // event onClick button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Alaina ve?", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
