package com.taatiko.places;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class TaatikoPlaces implements AsyncTaskCompleteListener {
    private final String NAME = "";
    private final String ADDRESS_NAME = "";
    private final String VICINITY_NAME = "";
    private final String LAT_LNG = "";
    private final float LAT = 0;
    private final float LNG = 0;
    private final String MAP_KEY = "";

    public static final String PREF_NAME = "taatiko places";
    private static SharedPreferences app_preferences;
    private static TaatikoPlaces preferenceHelper = new TaatikoPlaces();

    private TaatikoPlaces() {

    }

    public static TaatikoPlaces initialize(Context context, String key) {
        app_preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferenceHelper.set_key(key);
        return preferenceHelper;
    }


    private void set_key(String map_key) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString(MAP_KEY, map_key);
        editor.commit();
    }

    public String get_key() {
        return app_preferences.getString(MAP_KEY, "");
    }

    private void set_name(String name) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString(NAME, name);
        editor.commit();
    }

    public String get_name() {
        return app_preferences.getString(NAME, "");
    }

    private void set_address(String address) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString(ADDRESS_NAME, address);
        editor.commit();
    }

    public String get_address() {
        return app_preferences.getString(ADDRESS_NAME, "");
    }

    private void set_vicinity(String vicinity) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString(VICINITY_NAME, vicinity);
        editor.commit();
    }

    public String get_vicinity() {
        return app_preferences.getString(VICINITY_NAME, "");
    }

    private void set_latlng(String latlng) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString(LAT_LNG, latlng);
        editor.commit();
    }

    public String get_latlng() {
        return app_preferences.getString(LAT_LNG, "");
    }

    private void set_lat(Float lat) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putFloat(String.valueOf(LAT), lat);
        editor.commit();
    }

    public double get_lat() {
        return app_preferences.getFloat(String.valueOf(LAT), 0);
    }

    private void set_lng(Float lng) {
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putFloat(String.valueOf(LNG), lng);
        editor.commit();
    }

    public double get_lng() {
        return app_preferences.getFloat(String.valueOf(LNG), 0);
    }

    public void getPrediction(Context context, AsyncTaskCompleteListener asyncTaskCompleteListener, String search) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("query", search);
            new HttpRequester(context, "https://mp.feres.co/query", jsonObject, 1,
                    asyncTaskCompleteListener, HttpRequester.POST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        if (TextUtils.isEmpty(response)) {
            return;
        }
        switch (serviceCode) {
            case 1:
                Log.d("taatikoPlaces", response);
                break;
            default:
                // do with default
                break;
        }
    }
}
