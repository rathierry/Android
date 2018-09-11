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

import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.api.APIClient;
import com.team.lezomadetana.api.APIInterface;
import com.team.lezomadetana.fragment.FragmentBuyItem;
import com.team.lezomadetana.model.receive.Request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // get current date
        DateFormat df = new SimpleDateFormat("MMM dd");
        String date = df.format(Calendar.getInstance().getTime());

        // displaying text view data
        from.setText(Html.fromHtml("<b>" + req.getProduct() + "</b>"));
        subject.setText(Html.fromHtml("Lanjany/Isa: <b>" + req.getQuantity() + "</b>" + req.getUnitType().name()));
        // message.setText(req.getUserName());
        getUserInfo(req.getUserId(), message);

        // displaying the first letter of From in icon text
        iconText.setText(req.getProduct().substring(0, 1));

        // sum answer btn
        if (req.getOffers() != null) {
            btnSum.setText(String.valueOf(req.getOffers().size()));
            btnSum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentBuyItem.ChangeThisFragment();
                }
            });
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

        void onButtonAnswerClicked(int position);

        void onMessageRowClicked(int position);

        void replaceFragment(Fragment fragment);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void getUserInfo(String userId, final TextView textView) {
        // set retrofit api
        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_USER_API).create(APIInterface.class);

        // create basic authentication
        String auth = BaseActivity.BasicAuth();

        // send query
        Call<JsonObject> call = api.getUserById(auth, userId);

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    textView.setText("nalefan\'i " + response.body().get("name").getAsString());
                } else {
                    //
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //
            }
        });
    }

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
            /*Glide.with(activity)
                    .load(url)
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(activity))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_account_circle_black)
                    .into(imgProfile);
            imgProfile.setColorFilter(null);
            iconText.setVisibility(View.GONE);*/
            //Toast.makeText(activity, "!null : " + url, Toast.LENGTH_SHORT).show();
            imgProfile.setImageResource(R.drawable.bg_circle);
            imgProfile.setColorFilter(fragmentBuyItem.getRandomMaterialColor("400"));
            iconText.setVisibility(View.VISIBLE);
        } else {
            /*imgProfile.setImageResource(R.drawable.bg_circle);
            imgProfile.setColorFilter(fragmentBuyItem.getRandomMaterialColor("400"));
            iconText.setVisibility(View.VISIBLE);*/
            //Toast.makeText(activity, "NULL : " + url, Toast.LENGTH_SHORT).show();
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
                listener.onButtonAnswerClicked(position);
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