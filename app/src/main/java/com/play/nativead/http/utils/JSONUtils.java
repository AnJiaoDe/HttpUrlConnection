package com.play.nativead.http.utils;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cy on 2016/10/3.
 */
public class JSONUtils {

    public static boolean isGoodJson(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }


}
