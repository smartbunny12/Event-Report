package com.example.eventreporter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Api;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LocationTracker implements LocationListener {
    @Override
    public void onLocationChanged(Location location){

    }

    @Override
    public void onProviderDisabled(String provider){

    }

    @Override
    public void onProviderEnabled(String provider){

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){

    }


    private final Activity mContext;
    private static final int PERMISSION_REQUEST_LOCATION = 99;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;

    private boolean mIsGPSEnabled = false;
    private boolean mIsNetworkEnabled;

    private boolean mCangetLocation;
    private Location location;
    private double latitude;
    private double longitude;

    // declaring a location manager
    private LocationManager locationManager;

    public LocationTracker(Activity context){
        this.mContext = context;
    }

    public Location getLocation(){
        try{
            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);

            //get GPS status
            mIsGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            //get network status
            mIsNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!mIsGPSEnabled && !mIsNetworkEnabled) {
                return null;
            } else {
                mCangetLocation = true;
                //first get location from network provider
                checkLocationPermission();
                if (mIsNetworkEnabled){
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                //if GPS Enabled get lat/long using GPS service
                if (mIsGPSEnabled) {
                    if (location == null){
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GOS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Function to get latitude
     */

    public double getLatitude(){
        if (location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude(){
        if (location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    // runtime permission check
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            //No explanation needed, we can request the permission
            ActivityCompat.requestPermissions(mContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
        return true;
    }

    // convert lat lon to street name, zip code etc
    public static JSONObject getLocationInfo(double lat, double lng){
        //HttpGet httpGet = new
                //HttpGet("http://maps.googleapis.com/maps/api/geocode/json?latlng="+ lat+","+lng +"&key=AIzaSyBih-tXYn3-k30B2rZgqWqZ51qg-m7ab4o");
        //key=AIzaSyBih-tXYn3-k30B2rZgqWqZ51qg-m7ab4o
        //sensor=true
        //http://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng+"&key=AIzaSyBih-tXYn3-k30B2rZgqWqZ51qg-m7ab4o
////
//        HttpClient client = new DefaultHttpClient();
//        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();
//
//        try {
//            response = client.execute(httpGet);
//            HttpEntity entity = response.getEntity();
//            InputStream stream = entity.getContent();
//            int b;
//            while ((b = stream.read()) != -1){
//                stringBuilder.append((char) b);
//            }
//
//        } catch (ClientProtocolException e){
//
//        } catch (IOException e){
//
//        }

        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng="+ lat + "," + lng +"&key=AIzaSyBih-tXYn3-k30B2rZgqWqZ51qg-m7ab4o");
            System.out.println(url.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                stringBuilder.append(line);
            }
            connection.disconnect();

        }catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public List<String> getCurrentLocationViaJSON(double lat, double lng){
        List<String> address = new ArrayList<>();
        JSONObject jsonObj = getLocationInfo(lat, lng);
        try {
            String status = jsonObj.getString("status").toString();
            if (status.equalsIgnoreCase("OK")){
                JSONArray results = jsonObj.getJSONArray("results");

                int i = 0;
                do {
                    JSONObject r = results.getJSONObject(i);
                    if (!r.getString("formatted_address").equals("")){
                        String formatted_address[] = r.getString("formatted_address").split(",");
                        address.add(formatted_address[0]);
                        address.add(formatted_address[1]);
                        address.add(formatted_address[2]);
                        address.add(formatted_address[3]);
                        break;

                    }
                    i++;
                } while (i <results.length());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return address;
    }

}
