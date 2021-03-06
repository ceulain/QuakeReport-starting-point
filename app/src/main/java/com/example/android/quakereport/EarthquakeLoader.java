package com.example.android.quakereport;


import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by barth on 06/10/16.
 */

public class EarthquakeLoader extends AsyncTaskLoader<LinkedList<Earthquake>> {

    private String mUrl;
    private final static String LOG_TAG = EarthquakeLoader.class.getName();

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "on start loading");
        forceLoad();
    }

    @Override
    public LinkedList<Earthquake> loadInBackground() {
        Log.i(LOG_TAG, "on load in background");

        if (mUrl == null) {
            return null;
        }
        return QueryUtils.fetchEarthquakeData(mUrl);
    }
}
