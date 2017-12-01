package com.nakay.zeroandroid.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nakay.zeroandroid.R;
import com.nakay.zeroandroid.activity.BaseFragmentActivity;
import com.nakay.zeroandroid.adapter.UsersRecyclerAdapter;
import com.nakay.zeroandroid.model.User;
import com.nakay.zeroandroid.sql.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private AppCompatTextView textViewName;
    private RecyclerView recyclerViewUsers;
    private List<User> listUsers;
    private UsersRecyclerAdapter usersRecyclerAdapter;
    private DatabaseHelper databaseHelper;

    // ===========================================================
    // Constructors
    // ===========================================================

    public UserListFragment() {
        // Required empty public constructor
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        textViewName = (AppCompatTextView) view.findViewById(R.id.textViewName);
        recyclerViewUsers = (RecyclerView) view.findViewById(R.id.recyclerViewUsers);

        initObjects();

        return view;
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
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        BaseFragmentActivity baseFragmentActivity = new BaseFragmentActivity();
        listUsers = new ArrayList<>();
        usersRecyclerAdapter = new UsersRecyclerAdapter(listUsers);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(baseFragmentActivity);
        recyclerViewUsers.setLayoutManager(mLayoutManager);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setAdapter(usersRecyclerAdapter);

        databaseHelper = new DatabaseHelper(getContext());

        textViewName.setText(baseFragmentActivity.getCurrentUser(getContext()).getEmail());

        getDataFromSQLite();
    }

    /**
     * This method is to fetch all user records from SQLite
     */
    protected void getDataFromSQLite() {
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

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
