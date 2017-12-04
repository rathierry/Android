package com.rathierry.mychatapplication.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by ThierryNakay on 20/02/17.
 */
public class SessionManager {

    // ===========================================================
    // Constants
    // ===========================================================

    private static String TAG = SessionManager.class.getSimpleName();

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "MyChatPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static final String PREF_SESSION_NAME = "SessionStatus";
    public static final String KEY_SESSION_STATUS = "status";

    // ===========================================================
    // Fields
    // ===========================================================

    public SharedPreferences pref;
    public SharedPreferences.Editor editor;

    public Context _context;

    // ===========================================================
    // Constructors
    // ===========================================================

    public SessionManager(Context _context) {
        this._context = _context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Boolean setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.commit();

        Log.d(TAG, "User login session modified!");
        return null;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setPreferences(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_SESSION_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();

    }

    public String getPreferences(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_SESSION_NAME, Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}