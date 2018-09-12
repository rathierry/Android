package com.team.lezomadetana.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
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
import com.team.lezomadetana.fragment.FragmentBuyItem;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.utils.CircleTransform;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by RaThierry on 06/09/2018.
 **/

public class BUYAdapter extends BaseAdapter {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<Request> requestList;
    private BUYAdapter.RequestAdapterListener listener;
    private FragmentBuyItem fragmentBuyItem;

    // ===========================================================
    // Constructors
    // ===========================================================

    public BUYAdapter(Activity activity, List<Request> requestList) {
        this.activity = activity;
        this.requestList = requestList;
    }

    public BUYAdapter(Activity activity, List<Request> requestList, RequestAdapterListener listener, FragmentBuyItem fragmentBuyItem) {
        this.activity = activity;
        this.requestList = requestList;
        this.listener = listener;
        this.fragmentBuyItem = fragmentBuyItem;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    public int getCount() {
        return requestList.size();
    }

    @Override
    public Object getItem(int location) {
        return requestList.get(location);
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
        final Request req = requestList.get(position);

        // display profile image
        if (req.getAssetUrls() != null) {
            String itemUrl = req.getAssetUrls().get(0);
            itemUrl = itemUrl.replace("\"", "");
            applyProfilePicture(imgProfile, iconText, itemUrl);
        }

        // get current date
        DateFormat df = new SimpleDateFormat("MMM dd");
        String date = df.format(Calendar.getInstance().getTime());

        // displaying text view data
        from.setText(Html.fromHtml("<b>" + req.getProduct() + "</b>"));
        subject.setText(Html.fromHtml("Lanjany/Isa: <b>" + req.getQuantity() + "</b>" + req.getUnitType().name()));

        // check if getUser is not null
        if (req.getUser() != null) {
            message.setText("nalefan\'i " + req.getUser().getName().toString());
        } else {
            message.setText("nalefan\'i ... . ...");
        }

        // displaying the first letter of From in icon text
        iconText.setText(req.getProduct().substring(0, 1));

        // sum answer btn
        if (req.getOffers() != null) {
            // set value
            btnSum.setText(String.valueOf(req.getOffers().size()));

            // event
            if (req.getOffers().size() != 0) {
                btnSum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragmentBuyItem.startPaymentFragment(req);
                    }
                });
            }
        }

        // answer btn
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentBuyItem.showAnswerOfferPopup(req.getId());
            }
        });

        // change the font style depending on message read status
        applyReadStatus(from, subject, req);

        // apply click events
        applyClickEvents(iconContainer, messageContainer, position, req);

        return convertView;
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    public interface RequestAdapterListener {
        void onIconClicked(int position);

        void onMessageRowClicked(int position, Request request);

        void replaceFragment(Fragment fragment, Request request);
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
        // verification
        if (!TextUtils.isEmpty(url)) {
            Glide.with(activity)
                    .load(url)
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(activity))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(imgProfile);
            imgProfile.setColorFilter(null);
            iconText.setVisibility(View.GONE);
        } else {
            imgProfile.setImageResource(R.drawable.bg_circle);
            imgProfile.setColorFilter(fragmentBuyItem.getRandomMaterialColor("400"));
            iconText.setVisibility(View.VISIBLE);
        }
    }

    private void applyClickEvents(RelativeLayout iconContainer, LinearLayout messageContainer, final int position, final Request request) {
        iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position, request);
            }
        });
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}