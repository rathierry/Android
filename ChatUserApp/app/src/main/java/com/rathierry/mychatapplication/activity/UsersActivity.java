package com.rathierry.mychatapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rathierry.mychatapplication.R;
import com.rathierry.mychatapplication.manager.SessionManager;
import com.rathierry.mychatapplication.model.UserDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class UsersActivity extends BaseActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private ListView usersList;
    private TextView noUsersText;
    private ArrayList<String> stringArrayList = new ArrayList<>();

    private int totalUsers = 0;

    private SessionManager _sessionManager;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // variable
        _sessionManager = new SessionManager(getApplicationContext());
        usersList = (ListView) findViewById(R.id.usersList);
        noUsersText = (TextView) findViewById(R.id.noUsersText);

        if (!_sessionManager.isLoggedIn()) {
            _sessionManager.setLogin(false);
            _sessionManager.setPreferences(UsersActivity.this, _sessionManager.KEY_SESSION_STATUS, "0");

            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            // toolBar
            SpannableString spannableString = new SpannableString(getResources().getString(R.string.activity_users_toolbar_title));
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_grey_blue)), 0,
                    getResources().getString(R.string.activity_users_toolbar_title).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(spannableString);

            // show spinner
            showLoadingView();

            // request
            StringRequest request = new StringRequest(Request.Method.GET, rootUrlUserJson, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    doOnSuccess(s);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError);
                }
            });

            RequestQueue rQueue = Volley.newRequestQueue(UsersActivity.this);
            rQueue.add(request);

            // event onClick
            usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UserDetails.chatWith = stringArrayList.get(position);

                    startActivity(new Intent(UsersActivity.this, ChatActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.users_activity, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_logout) {
            logOutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        logOutUser();
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void doOnSuccess(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while (i.hasNext()) {
                key = i.next().toString();

                if (!key.equals(UserDetails.username)) {
                    stringArrayList.add(key);
                }

                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (totalUsers <= 1) {
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else {
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringArrayList));
        }

        // hide spinner
        hideLoadingView();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void logOutUser() {
        new AlertDialog.Builder(UsersActivity.this)
                .setTitle(getResources().getString(R.string.default_logout_title))
                .setMessage(getResources().getString(R.string.default_logout_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _sessionManager.setLogin(false);
                        _sessionManager.setPreferences(UsersActivity.this, _sessionManager.KEY_SESSION_STATUS, "0");

                        dialog.dismiss();

                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}