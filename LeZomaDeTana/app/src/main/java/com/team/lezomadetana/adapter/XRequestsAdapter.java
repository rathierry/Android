package com.team.lezomadetana.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.team.lezomadetana.R;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.utils.CircleTransform;

import java.util.List;

/**
 * Created by RaThierry on 18/09/2018.
 **/

public class XRequestsAdapter extends RecyclerView.Adapter<XRequestsAdapter.MyViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private Context mContext;
    private List<Request> requests;
    private RequestAdapterListener listener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public XRequestsAdapter(Context mContext, List<Request> requests, RequestAdapterListener listener) {
        this.mContext = mContext;
        this.requests = requests;
        this.listener = listener;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_request_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        // class model
        final Request request = requests.get(position);

        // displaying the first letter of From in icon text
        holder.iconText.setText(request.getProduct().substring(0, 1));

        // displaying text view data
        holder.from.setText(Html.fromHtml("<b>" + request.getProduct() + "</b>"));
        holder.subject.setText(Html.fromHtml("Lanjany/Isa: <b>" + request.getQuantity() + "</b>" + request.getUnitType().name()));
        if (request.getUser() != null) {
            holder.message.setText("nalefan\'i " + request.getUser().getName().toString());
        } else {
            holder.message.setText("nalefan\'i ... . ...");
        }

        // sum btn
        if (request.getOffers() != null) {
            holder.sum.setText(String.valueOf(request.getOffers().size()));
            if (request.getOffers().size() != 0) {
                holder.sum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onMessageRowClicked(position, request);
                    }
                });
            }
        }

        // display avatar image
        applyProfilePicture(holder, request);

        // apply click events
        applyClickEvents(holder, position, request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    public interface RequestAdapterListener {
        void onIconClicked(int position, Request request);

        void onMessageRowClicked(int position, Request request);

        void onButtonAnswerClicked(int position, Request request);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void applyProfilePicture(MyViewHolder holder, Request request) {
        if (request.getAssetUrls() != null) {
            String itemUrl = request.getAssetUrls().get(0);
            itemUrl = itemUrl.replace("\"", "");
            // download
            if (!TextUtils.isEmpty(itemUrl)) {
                Glide.with(mContext).load(itemUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .transform(new CircleTransform(mContext))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imgProfile);
                holder.imgProfile.setColorFilter(null);
                holder.iconText.setVisibility(View.GONE);
            }
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle);
            holder.imgProfile.setColorFilter(request.getColor());
            holder.iconText.setVisibility(View.VISIBLE);
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position, final Request request) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onIconClicked(position, request);
            }
        });

        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageRowClicked(position, request);
            }
        });

        holder.answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonAnswerClicked(position, request);
            }
        });
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgProfile;
        public TextView iconText, from, subject, message;
        public Button sum, answer;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgProfile = (ImageView) itemView.findViewById(R.id.icon_profile);
            iconText = (TextView) itemView.findViewById(R.id.icon_text);
            from = (TextView) itemView.findViewById(R.id.from);
            subject = (TextView) itemView.findViewById(R.id.txt_primary);
            message = (TextView) itemView.findViewById(R.id.txt_secondary);
            sum = (Button) itemView.findViewById(R.id.sum_answer);
            answer = (Button) itemView.findViewById(R.id.answer_item);
            messageContainer = (LinearLayout) itemView.findViewById(R.id.message_container);
            iconContainer = (RelativeLayout) itemView.findViewById(R.id.icon_container);
        }
    }
}
