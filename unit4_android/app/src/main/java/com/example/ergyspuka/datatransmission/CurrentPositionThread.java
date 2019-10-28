package com.example.ergyspuka.datatransmission;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ergyspuka on 02/05/2017.
 */

public class CurrentPositionThread extends Thread {

    private static final String TAG = CurrentPositionThread.class.getSimpleName();

    String mProtocol;

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation = null;
    private LocationRequest mLocationRequest = null;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    MainActivity mSuperParent;



    public CurrentPositionThread(MainActivity superParent, String protocol) {
        super();
        this.mSuperParent = superParent;
        this.mProtocol = protocol;

    }


    public void run(){
        System.out.println("Welcome to the " + mProtocol + " CurrentPositionThread!");


        mGoogleApiClient = new GoogleApiClient.Builder(mSuperParent.getApplicationContext())
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds



        Location location = null;

        if (ContextCompat.checkSelfPermission(mSuperParent.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            System.out.println("mLocationPermissionGranted: " + mLocationPermissionGranted);
        } else {
            ActivityCompat.requestPermissions(mSuperParent,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            System.out.println("mLocationPermissionGranted: " + mLocationPermissionGranted);
        }

          //Get the best and most recent location of the device, which may be null in rare
          //cases when a location is not available.

        if (mLocationPermissionGranted) {
            System.out.println("mLocationPermissionGranted is " + mLocationPermissionGranted + " !!!");

            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, LocationListener);


            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            System.out.println("A: mLastKnownLocation: " + mLastKnownLocation);

        }


        if (mLastKnownLocation != null) {
            System.out.println("B: mLastKnownLocation: " + mLastKnownLocation);
            System.out.println("mLastKnownLocation.getLatitude(): " + mLastKnownLocation.getLatitude());
            System.out.println("mLastKnownLocation.getLongitude(): " + mLastKnownLocation.getLongitude());
            System.out.println("mLastKnownLocation.getAltitude(): " + mLastKnownLocation.getAltitude());
            System.out.println("mLastKnownLocation.getBearing(): " + mLastKnownLocation.getBearing());
            System.out.println("mLastKnownLocation.getTime(): " + mLastKnownLocation.getTime());
            System.out.println("mLastKnownLocation.getExtras(): " + mLastKnownLocation.getExtras());
            System.out.println("mLastKnownLocation.getSpeed(): " + mLastKnownLocation.getSpeed());
            System.out.println("mLastKnownLocation.getAccuracy(): " + mLastKnownLocation.getAccuracy());
        }

    }


}

