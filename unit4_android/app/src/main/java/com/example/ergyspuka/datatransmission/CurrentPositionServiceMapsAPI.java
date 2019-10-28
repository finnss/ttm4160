package com.example.ergyspuka.datatransmission;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Iterator;

/**
 * Created by ergyspuka on 12/05/2017.
 */

public class CurrentPositionServiceMapsAPI extends Service implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GpsStatus.Listener{



    private final IBinder mBinder = new CurrentPositionServiceMapsAPIBinder();


    private static final String TAG = CurrentPositionServiceMapsAPI.class.getSimpleName();

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    //private Iterable<GpsSatellite> satellites;

    private LocationRequest mLocationRequest = null;

    protected LocationManager locationManager;





    @Override
    public void onCreate() {
        System.out.println("onCreate ...");

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        //If you call here the connect() method the GoogleApiClient will not start anymore after stopping the service
        //System.out.println("Before: mGoogleApiClient.connect() ...");
        //mGoogleApiClient.connect();
        //System.out.println("After: mGoogleApiClient.connect() ...");



        //System.out.println("Before: LocationRequest.create() ...");
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)        // 1 seconds, in milliseconds
                .setFastestInterval(500); // 1 second, in milliseconds
        //System.out.println("After: LocationRequest.create() ...");
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getBaseContext(), "The CurrentPositionServiceMapsAPI Service is running ...", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.addGpsStatusListener(this);
        }
        else{
            Toast.makeText(getBaseContext(), "Location permissions should be enabled ...", Toast.LENGTH_SHORT).show();
        }

        //System.out.println("getLocation(): " + getLocation());
        return START_STICKY;
    }




    @Override
    public void onLocationChanged(Location location) {

        System.out.println("Location changed in the CurrentPositionServiceMapsAPI!");

        if(location != null) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
            if(mLocationPermissionGranted) {

                mLastKnownLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
            }
            else{
                Toast.makeText(getBaseContext(), "Location permissions should be enabled ...", Toast.LENGTH_SHORT).show();
            }





        }
    }



    @Override
    public void onDestroy() {
        Toast.makeText(getBaseContext(), "The CurrentPositionServiceMapsAPI Service is stopped ...", Toast.LENGTH_SHORT).show();


        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }
        if (mLocationPermissionGranted) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        else{
            Toast.makeText(getBaseContext(), "Location permissions should be enabled ...", Toast.LENGTH_SHORT).show();
        }

        mGoogleApiClient.disconnect();

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }
        if (mLocationPermissionGranted) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else{
            Toast.makeText(getBaseContext(), "Location permissions should be enabled ...", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("onMapReady ...");

    }



    @Override
    public void onGpsStatusChanged(int event) {
        System.out.println("CurrentPositionServiceMapsAPI - onGpsStatusChanged ...");

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            GpsStatus gpsStatus = locationManager.getGpsStatus(null);
            if(gpsStatus != null) {
                Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                Iterator<GpsSatellite> sat = satellites.iterator();
                int isatellites = 0;
                int satellitesInFix = 0;
                String lSatellites = null;
                int i = 0;
                while (sat.hasNext()) {

                    GpsSatellite satellite = sat.next();

                    if(satellite.usedInFix()) {
                        satellitesInFix++;
                    }
                    isatellites++;

                }
            }

        }
        else{
            Toast.makeText(getBaseContext(), "Location permissions should be enabled ...", Toast.LENGTH_SHORT).show();
        }

    }


    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class CurrentPositionServiceMapsAPIBinder extends Binder {
        public CurrentPositionServiceMapsAPI getService() {
            return CurrentPositionServiceMapsAPI.this;
        }
    }



    public Location getLocation(){
        return this.mLastKnownLocation;
    }



}
