package com.example.android.quakereport;

/**
 * Created by barth on 03/10/16.
 */

public class Earthquake {

    private double mMagnitude;
    private String mLocation;
    private long mTimeInMilliSeconds;
    private String mUrl;



    public Earthquake(double magnitude, String location, long time, String url){

        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliSeconds = time;
        mUrl = url;
    }


    public double getMagnitude(){
        return mMagnitude;
    }

    public String getLocation(){
        return mLocation;
    }

    public long getTimeInMilliSeconds(){
        return mTimeInMilliSeconds;
    }

    public String getUrl(){
        return mUrl;
    }
}
