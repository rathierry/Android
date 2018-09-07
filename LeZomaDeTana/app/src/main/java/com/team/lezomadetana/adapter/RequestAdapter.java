package com.team.lezomadetana.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.team.lezomadetana.R;
import com.team.lezomadetana.model.receive.Request;
import com.team.lezomadetana.task.DownLoadImageTask;

import java.util.List;

/**
 * Created by RaThierry on 06/09/2018.
 **/

public class RequestAdapter extends BaseAdapter {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<Request> requestItems;

    // ===========================================================
    // Constructors
    // ===========================================================

    public RequestAdapter(Activity activity, List<Request> requestItems) {
        this.activity = activity;
        this.requestItems = requestItems;
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
            convertView = layoutInflater.inflate(R.layout.list_request_row, null);

        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView person = (TextView) convertView.findViewById(R.id.person);
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
        numberView.setText(r.getPrice().toString() + " ANSWER");

        // event
        numberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
