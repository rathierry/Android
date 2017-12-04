package com.zahomahay.appnakay.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.zahomahay.appnakay.R;
import com.zahomahay.appnakay.adapter.UsersRecyclerAdapter;
import com.zahomahay.appnakay.model.User;
import com.zahomahay.appnakay.sql.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends BaseActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private AppCompatActivity activity = UserListActivity.this;
    private AppCompatTextView textViewName;
    private RecyclerView recyclerViewUsers;
    private List<User> listUsers;
    private UsersRecyclerAdapter usersRecyclerAdapter;
    private DatabaseHelper databaseHelper;

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
        setContentView(R.layout.activity_user_list);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_user_list_toolBar_title));

        initViews();
        initObjects();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adds menu entries to ActionBar
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    // manage the click on an action of the ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_power:
                logoutUser();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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

    /**
     * This method is to initialize views
     */
    private void initViews() {
        textViewName = (AppCompatTextView) findViewById(R.id.textViewName);
        recyclerViewUsers = (RecyclerView) findViewById(R.id.recyclerViewUsers);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        listUsers = new ArrayList<>();
        usersRecyclerAdapter = new UsersRecyclerAdapter(listUsers);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewUsers.setLayoutManager(mLayoutManager);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setAdapter(usersRecyclerAdapter);
        databaseHelper = new DatabaseHelper(activity);

        String emailFromIntent = getIntent().getStringExtra("EMAIL");
        textViewName.setText(emailFromIntent);

        getDataFromSQLite();
    }

    /**
     * This method is to fetch all user records from SQLite
     */
    private void getDataFromSQLite() {
        // AsyncTask is used that SQLite operation not blocks the UI Thread.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                listUsers.clear();
                listUsers.addAll(databaseHelper.getAllUser());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                usersRecyclerAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    /**
     * This method is to logout user
     */
    private void logoutUser() {
        new AlertDialog.Builder(activity)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.logout))
                .setIcon(R.drawable.ic_power_black)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();

                        showLoadingView();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideLoadingView();
                                disconnectUser();
                            }
                        }, LOADING_TIME_OUT);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}