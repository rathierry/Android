package com.team.lezomadetana.utils;

import android.util.Base64;

/**
 * Created by Hery Andoniaina on 03/09/2018.
 */

public class InfoConfig {

    public static String ROOT_URL = "http://mdz-user-api-test.herokuapp.com/";
    public static String APP_USER_NAME = "app";
    public static String APP_PASSWORD  = "app-password";



    public static String BasicAuth(){
        return "Basic "+ Base64.encodeToString((APP_USER_NAME+":"+APP_PASSWORD).getBytes(),Base64.NO_WRAP);

    }

}
