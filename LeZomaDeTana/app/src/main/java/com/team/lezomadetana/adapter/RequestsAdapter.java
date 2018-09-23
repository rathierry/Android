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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.team.lezomadetana.R;
import com.team.lezomadetana.fragment.FragmentRequestBuy;
import com.team.lezomadetana.fragment.FragmentRequestSell;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.utils.CircleTransform;

import java.util.List;

/**
 * Created by RaThierry on 18/09/2018.
 **/

public class RequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_LOADING = 1;

    // ===========================================================
    // Fields
    // ===========================================================

    private Context mContext;
    private List<Request> requests;
    private RequestAdapterListener listener;
    private FragmentRequestBuy fragmentRequestBuy;
    private FragmentRequestSell fragmentRequestSell;

    private boolean isLoadingAdded = false;
    private boolean fromRequestBuyFragment = false;
    private boolean searchRequestBuyFragment = false;
    private boolean fromRequestSellFragment = false;
    private boolean searchRequestSellFragment = false;

    // ===========================================================
    // Constructors
    // ===========================================================

    public RequestsAdapter(Context mContext, List<Request> requests, RequestAdapterListener listener, FragmentRequestBuy fragmentRequestBuy, boolean searchRequestBuyFragment) {
        this.mContext = mContext;
        this.requests = requests;
        this.listener = listener;
        this.fragmentRequestBuy = fragmentRequestBuy;
        this.fromRequestBuyFragment = true;
        this.searchRequestBuyFragment = searchRequestBuyFragment;
    }

    public RequestsAdapter(Context mContext, List<Request> requests, RequestAdapterListener listener, FragmentRequestSell fragmentRequestSell, boolean searchRequestSellFragment) {
        this.mContext = mContext;
        this.requests = requests;
        this.listener = listener;
        this.fragmentRequestSell = fragmentRequestSell;
        this.fromRequestSellFragment = true;
        this.searchRequestSellFragment = searchRequestSellFragment;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case VIEW_LOADING:
                View v2 = inflater.inflate(R.layout.progress_bar, parent, false);
                viewHolder = new LoadingVH(v2);

                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.row_for_request, parent, false);
        viewHolder = new RequestVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case VIEW_ITEM:
                // class model
                final Request request = requests.get(position);

                // view holder
                final RequestVH requestVH = (RequestVH) holder;

                // verification
                String quantity = String.valueOf(TextUtils.equals(String.valueOf(request.getQuantity()), null) ? "" : request.getQuantity());

                // take the first letter of "From" in icon text
                if (request.getProduct() == null) {
                    requestVH.iconText.setText("?");
                    requestVH.from.setText("");
                } else {
                    requestVH.iconText.setText(request.getProduct().substring(0, 1));
                    requestVH.from.setText(Html.fromHtml("<b>" + request.getProduct() + "</b>"));
                }

                // set price and unitType
                if (request.getUnitType() == null) {
                    requestVH.subject.setText(Html.fromHtml("Lanjany/Isa: <b>" + quantity + "</b> "));
                } else {
                    requestVH.subject.setText(Html.fromHtml("Lanjany/Isa: <b>" + quantity + "</b>" + request.getUnitType().name()));
                }

                // user name
                if (request.getUser() == null) {
                    requestVH.message.setText("nalefan\'i ");
                } else {
                    requestVH.message.setText("nalefan\'i " + request.getUser().getName());
                }

                // total offers
                if (request.getOffers() == null) {
                    requestVH.sum.setText("0");
                } else {
                    requestVH.sum.setText(String.valueOf(request.getOffers().size()));
                    // event of sum button
                    if (request.getOffers().size() != 0) {
                        requestVH.sum.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onMessageRowClicked(position, request);
                            }
                        });
                    }
                }

                // display avatar image
                applyProfilePicture(requestVH, request);

                // apply click events
                applyClickEvents(requestVH, position, request);
                break;

            case VIEW_LOADING:
                // 1) simple buy request list
                if (fromRequestBuyFragment && !searchRequestBuyFragment) {
                    fragmentRequestBuy.loadNextPage();
                    //Toast.makeText(mContext, "- fragmentRequestBuy.loadNextPage -", Toast.LENGTH_SHORT).show();
                }
                // 2) search buy request list
                else if (fromRequestBuyFragment && searchRequestBuyFragment) {
                    fragmentRequestBuy.loadSearchNextPage();
                    //Toast.makeText(mContext, "- search / BUY / loadNextPage -", Toast.LENGTH_SHORT).show();
                }
                // 3) simple sell request list
                else if (fromRequestSellFragment && !searchRequestSellFragment) {
                    fragmentRequestSell.loadNextPage();
                    Toast.makeText(mContext, "< fragmentRequestSell.loadNextPage >", Toast.LENGTH_SHORT).show();
                }
                // 4) search sell request list
                else if (fromRequestSellFragment && searchRequestSellFragment) {
                    Toast.makeText(mContext, "< search / SELL / loadNextPage >", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return requests == null ? 0 : requests.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == requests.size() - 1 && isLoadingAdded) ? VIEW_LOADING : VIEW_ITEM;
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

     /*
   Helpers
   _________________________________________________________________________________________________
    */

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
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
    }

    public Request getItem(int position) {
        return requests.get(position);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void applyProfilePicture(RequestVH holder, Request request) {
        if (request.getAssetUrls() != null && request.getAssetUrls().size() != 0) {
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

    private void applyClickEvents(RequestVH holder, final int position, final Request request) {
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

    public class RequestVH extends RecyclerView.ViewHolder {
        public ImageView imgProfile;
        public TextView iconText, from, subject, message;
        public Button sum, answer;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer;

        public RequestVH(View itemView) {
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
        public ProgressBar progressBar;

        public LoadingVH(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.load_more_progress);
        }
    }
}
