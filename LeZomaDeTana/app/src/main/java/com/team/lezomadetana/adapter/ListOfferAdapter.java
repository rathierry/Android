package com.team.lezomadetana.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.team.lezomadetana.R;
import com.team.lezomadetana.fragment.FragmentListOffer;
import com.team.lezomadetana.model.receive.Offer;
import com.team.lezomadetana.utils.CircleTransform;

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
    private FragmentListOffer fragmentListOffer;

    // ===========================================================
    // Constructors
    // ===========================================================


    public ListOfferAdapter() {
    }

    public ListOfferAdapter(Activity activity, List<Offer> offerList, FragmentListOffer fragmentListOffer) {
        this.activity = activity;
        this.offerList = offerList;
        this.fragmentListOffer = fragmentListOffer;
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
            convertView = layoutInflater.inflate(R.layout.row_for_offer, null);

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
        if (ofr.getUser() != null) {
            // image avatar
            String itemUrl = ofr.getUser().getProfileImageUrl();
            itemUrl = itemUrl.replace("\"", "");
            // verification
            if (!TextUtils.isEmpty(itemUrl)) {
                Glide.with(activity)
                        .load(itemUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .transform(new CircleTransform(activity))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .into(imageView);
                imageView.setColorFilter(null);
                iconText.setVisibility(View.GONE);
            } else {
                imageView.setImageResource(R.drawable.bg_circle);
                imageView.setColorFilter(fragmentListOffer.getRandomMaterialColor("400"));
                iconText.setText(ofr.getUser().getName().substring(0, 1));
                // displaying the first letter of From in icon text
                iconText.setVisibility(View.VISIBLE);
            }

            // name
            from.setText(Html.fromHtml("Avy amin\'i <b>" + ofr.getUser().getName() + "</b>"));
        }

        txt_primary.setText(Html.fromHtml("Izaho manana <b>" + ofr.getQuantity() + "" + ofr.getUnitType().name() + "</b>"));
        txt_secondary.setText(Html.fromHtml("Vidiny: " + ofr.getPrice() + " " + activity.getResources().getString(R.string.app_payment_unity_type_text)));

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
