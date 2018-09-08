package com.team.lezomadetana.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team.lezomadetana.R;
import com.team.lezomadetana.activity.BaseActivity;
import com.team.lezomadetana.api.APIClient;
import com.team.lezomadetana.api.APIInterface;
import com.team.lezomadetana.fragment.FragmentAvailableItem;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.task.DownLoadImageTask;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RaThierry on 06/09/2018.
 **/

public class RequestAvailableAdapter extends BaseAdapter {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<Request> requestItems;
    private FragmentAvailableItem fragmentAvailableItem;

    // ===========================================================
    // Constructors
    // ===========================================================

    public RequestAvailableAdapter(Activity activity, List<Request> requestItems,FragmentAvailableItem frag) {
        this.activity = activity;
        this.requestItems = requestItems;
        this.fragmentAvailableItem = frag;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.list_request_row, null);

        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        final TextView person = (TextView) convertView.findViewById(R.id.person);
        TextView template = (TextView) convertView.findViewById(R.id.template);
        TextView quantity = (TextView) convertView.findViewById(R.id.quantity);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        Button numberView = (Button) convertView.findViewById(R.id.numberView);

        // getting request data for the row
        final Request r = requestItems.get(position);

        // set values
        if (r.getAssetUrls().size() == 0) {
            Drawable myDrawable = convertView.getResources().getDrawable(R.mipmap.ic_launcher_round);
            Bitmap defaultAvatar = ((BitmapDrawable) myDrawable).getBitmap();
            thumbNail.setImageBitmap(defaultAvatar);
        } else {
            for (int i = 0; i < r.getAssetUrls().size(); i++) {
                new DownLoadImageTask(thumbNail).execute(r.getAssetUrls().get(0));
            }
        }
        person.setText("Send by " + r.getUserId());
        template.setText("Search \"" + r.getProduct() + "\"");
        quantity.setText("Quantity: " + r.getQuantity().toString() + "T");
        price.setText("1 " + r.getUnitType() + " = " + r.getPrice().toString() + " Jeton");
        numberView.setText("0 ANSWER");

        // set retrofit api
        APIInterface api = APIClient.getClient(BaseActivity.ROOT_MDZ_USER_API).create(APIInterface.class);

        // create basic authentication
        String auth = BaseActivity.BasicAuth();

        // send query
        Call<JsonObject> call = api.getUserById(auth,r.getUserId());

        // request
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    person.setText(response.body().get("name").getAsString());

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

        // event
        numberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentAvailableItem.ShowPostOffertPopup(r.getId());

                Toast.makeText(activity, "---------------" +
                        "\nButton clicked with" +
                        "\nThumbNail: ---" +
                        "\nSend by: " + r.getUserId() +
                        "\nSearch: " + r.getProduct() +
                        "\nQuantity: " + r.getQuantity() +
                        "\nUnitType: " + r.getUnitType() +
                        "\nPrice: " + r.getPrice() +
                        "\n---------------", Toast.LENGTH_SHORT).show();
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
