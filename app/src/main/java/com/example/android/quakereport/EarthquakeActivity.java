/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<LinkedList<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    public static final String USSG_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query";

    EarthquakeAdapter earthquakeAdapter;

    TextView mEmptyTextView;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        mEmptyTextView = (TextView) findViewById(R.id.empty_text_view);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data

            getSupportLoaderManager().initLoader(0, null, this);

            earthquakeAdapter = new EarthquakeAdapter(this, new LinkedList<Earthquake>());


            // Find a reference to the {@link ListView} in the layout
            ListView earthquakeListView = (ListView) findViewById(R.id.list);



            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(earthquakeAdapter);

            earthquakeListView.setEmptyView(mEmptyTextView);

            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(LOG_TAG, "click");
                    Earthquake currentEarthquake = earthquakeAdapter.getItem(position);
                    String url = currentEarthquake.getUrl();
                    Uri webpage = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
        } else {
            // display error
            mProgressBar.setVisibility(View.GONE);
            mEmptyTextView.setText("No Internet Connection");
        }



    }

    @Override
    public Loader<LinkedList<Earthquake>> onCreateLoader(int id, Bundle args) {

        Log.i(LOG_TAG, "on create loader");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );


        Uri baseUri = Uri.parse(USSG_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);


        return new EarthquakeLoader(EarthquakeActivity.this, uriBuilder.toString());


    }

    @Override
    public void onLoadFinished(Loader<LinkedList<Earthquake>> loader, LinkedList<Earthquake> data) {
        Log.i(LOG_TAG, "on load finished");

        mProgressBar.setVisibility(View.GONE);
        mEmptyTextView.setText("No data");
        earthquakeAdapter.clear();


        if(data != null && !data.isEmpty()){
            earthquakeAdapter.addAll(data);
        }

   }

    @Override
    public void onLoaderReset(Loader<LinkedList<Earthquake>> loader) {
        earthquakeAdapter.clear();
        Log.i(LOG_TAG, "on loader reset");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


//    private class EarthquakeTask extends AsyncTask<String, Void, LinkedList<Earthquake>>{
//
//        @Override
//        protected LinkedList<Earthquake> doInBackground(String... params) {
//            if(params.length < 1 || params[0] == null){
//                return null;
//            }
//
//                return QueryUtils.fetchEarthquakeData(params[0]);
//        }
//
//        @Override
//        protected void onPostExecute(LinkedList<Earthquake> earthquakes) {
//            if(earthquakes == null){
//                return;
//            }
//
//            updateUI(earthquakes);
//        }
//    }
}
