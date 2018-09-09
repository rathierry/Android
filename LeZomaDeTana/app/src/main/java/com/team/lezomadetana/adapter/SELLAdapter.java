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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.team.lezomadetana.R;
import com.team.lezomadetana.fragment.FragmentSellItem;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.utils.CircleTransform;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by RaThierry on 06/09/2018.
 **/

public class SELLAdapter extends BaseAdapter {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<Request> requestItems;
    private SELLAdapter.RequestAdapterListener listener;
    private FragmentSellItem fragmentSellItem;

    // ===========================================================
    // Constructors
    // ===========================================================

    public SELLAdapter(Activity activity, List<Request> requestItems) {
        this.activity = activity;
        this.requestItems = requestItems;
    }

    public SELLAdapter(Activity activity, List<Request> requestItems, RequestAdapterListener listener) {
        this.activity = activity;
        this.requestItems = requestItems;
        this.listener = listener;
    }

    public SELLAdapter(Activity activity, List<Request> requestItems, RequestAdapterListener listener, FragmentSellItem fragmentSellItem) {
        this.activity = activity;
        this.requestItems = requestItems;
        this.listener = listener;
        this.fragmentSellItem = fragmentSellItem;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    public int getCount() {
        return requestItems.size();
    }

    @Override
    public Object getItem(int location) {
        return requestItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.layout_request_row, null);

        TextView from = (TextView) convertView.findViewById(R.id.from);
        TextView subject = (TextView) convertView.findViewById(R.id.txt_primary);
        TextView message = (TextView) convertView.findViewById(R.id.txt_secondary);

        TextView iconText = (TextView) convertView.findViewById(R.id.icon_text);
        Button btnSum = (Button) convertView.findViewById(R.id.sum_answer);

        RelativeLayout iconFront = (RelativeLayout) convertView.findViewById(R.id.icon_front);
        Button btnAnswer = (Button) convertView.findViewById(R.id.answer_item);
        ImageView imgProfile = (ImageView) convertView.findViewById(R.id.icon_profile);
        LinearLayout messageContainer = (LinearLayout) convertView.findViewById(R.id.message_container);
        RelativeLayout iconContainer = (RelativeLayout) convertView.findViewById(R.id.icon_container);

        // getting request data for the row
        final Request req = requestItems.get(position);

        // get current date
        DateFormat df = new SimpleDateFormat("MMM dd");
        String date = df.format(Calendar.getInstance().getTime());

        // displaying text view data
        from.setText(Html.fromHtml("Manana <b>" + req.getProduct() + "</b>"));
        subject.setText(Html.fromHtml("Lanjany/Isa: <b>" + req.getQuantity() + "</b>" + req.getUnitType().name()));
        message.setText("nalefan\'i " + req.getUserId());

        // displaying the first letter of From in icon text
        iconText.setText(req.getProduct().substring(0, 1));

        // sum answer btn
        if (req.getOffers() == null) {
            btnSum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO to be continued
                }
            });
        } else {
            btnSum.setText(String.valueOf(req.getOffers().size()));
            btnSum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO to be continued
                }
            });
        }

        // answer btn
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSellItem.showPostOfferPopup(req.getId());
            }
        });

        // change the font style depending on message read status
        applyReadStatus(from, subject, req);

        // display profile image
        // Toast.makeText(activity, "imageUrl : " + req.getAssetUrls().get(0), Toast.LENGTH_SHORT).show();
        applyProfilePicture(imgProfile, iconText, req.getAssetUrls().get(0));

        // apply click events
        applyClickEvents(iconContainer, convertView, messageContainer, position);

        return convertView;
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    public interface RequestAdapterListener {
        void onIconClicked(int position);

        void onIconImportantClicked(int position);

        void onMessageRowClicked(int position);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void applyReadStatus(TextView from, TextView subject, Request request) {

        // TODO // TODO // TODO
        /*if (request.isRead()) {
            from.setTypeface(null, Typeface.NORMAL);
            subject.setTypeface(null, Typeface.NORMAL);
            from.setTextColor(ContextCompat.getColor(activity, R.color.subject));
            subject.setTextColor(ContextCompat.getColor(activity, R.color.message));
        } else {
            from.setTypeface(null, Typeface.BOLD);
            subject.setTypeface(null, Typeface.BOLD);
            from.setTextColor(ContextCompat.getColor(activity, R.color.from));
            subject.setTextColor(ContextCompat.getColor(activity, R.color.subject));
        }*/
    }

    private void applyProfilePicture(ImageView imgProfile, TextView iconText, String url) {
        // verif
        if (!TextUtils.isEmpty(url)) {
            Glide.with(activity).load(url)
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(activity))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_account_circle_black)
                    .into(imgProfile);
            imgProfile.setColorFilter(null);
            iconText.setVisibility(View.VISIBLE);
        } else {
            imgProfile.setImageResource(R.drawable.bg_circle);
            imgProfile.setColorFilter(android.R.color.holo_red_light);
            iconText.setVisibility(View.VISIBLE);
        }
    }

    private void applyClickEvents(RelativeLayout iconContainer, View itemView, LinearLayout messageContainer, final int position) {
        iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconImportantClicked(position);
            }
        });

        messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}