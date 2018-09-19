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

public class XRequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    // ===========================================================
    // Fields
    // ===========================================================

    private Context mContext;
    private List<Request> requests;
    private RequestAdapterListener listener;

    private boolean isLoadingAdded = false;

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

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }


    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.layout_request_row, parent, false);
        viewHolder = new MovieVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        // class model
        final Request request = requests.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final MovieVH movieVH = (MovieVH) holder;

                // verify server's response
                String _productName = TextUtils.equals(request.getProduct(), "null") ? "null" : request.getProduct();
                String _quantity = String.valueOf(TextUtils.equals(String.valueOf(request.getQuantity()), null) ? "null" : request.getQuantity());

                // displaying the first letter of From in icon text
                // displaying text view data
                movieVH.iconText.setText("T");
                movieVH.from.setText(Html.fromHtml("<b>" + _productName + "</b>"));

                if (request.getUnitType() != null) {
                    movieVH.subject.setText(Html.fromHtml("Lanjany/Isa: <b>" + _quantity + "</b>" + request.getUnitType().name()));
                } else {
                    movieVH.subject.setText(Html.fromHtml("Lanjany/Isa: <b>" + _quantity + "</b> xxx"));
                }

                // user name
                if (request.getUser() != null) {
                    movieVH.message.setText("nalefan\'i " + request.getUser().getName().toString());
                } else {
                    movieVH.message.setText("nalefan\'i ... . ...");
                }

                // sum btn
                if (request.getOffers() != null) {
                    movieVH.sum.setText(String.valueOf(request.getOffers().size()));
                    if (request.getOffers().size() != 0) {
                        movieVH.sum.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onMessageRowClicked(position, request);
                            }
                        });
                    }
                }

                // display avatar image
                applyProfilePicture(movieVH, request);

                // apply click events
                applyClickEvents(movieVH, position, request);
                break;

            case LOADING:
                // Do nothing
                break;
        }
    }

    @Override
    public int getItemCount() {
        return requests == null ? 0 : requests.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == requests.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
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

    public void addListItemToAdapter(List<Request> list) {
        // add list to current array list of data
        requests.addAll(list);

        // notify UI
        this.notifyDataSetChanged();
    }

     /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Request r) {
        requests.add(r);
        notifyItemInserted(requests.size() - 1);
    }

    public void addAll(List<Request> moveResults) {
        for (Request result : moveResults) {
            add(result);
        }
    }

    public void remove(Request r) {
        int position = requests.indexOf(r);
        if (position > -1) {
            requests.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Request());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = requests.size() - 1;
        Request result = getItem(position);

        if (result != null) {
            requests.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Request getItem(int position) {
        return requests.get(position);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void applyProfilePicture(MovieVH holder, Request request) {
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

    private void applyClickEvents(MovieVH holder, final int position, final Request request) {
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

    public class MovieVH extends RecyclerView.ViewHolder {
        public ImageView imgProfile;
        public TextView iconText, from, subject, message;
        public Button sum, answer;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer;

        public MovieVH(View itemView) {
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

    protected class LoadingVH extends RecyclerView.ViewHolder {
        public LoadingVH(View itemView) {
            super(itemView);
        }
    }
}
