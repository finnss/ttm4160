package com.example.ergyspuka.datatransmission;

import android.location.Location;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by ergyspuka on 09/05/2017.
 */

public class SignalStrengthThread extends Thread {

    TelephonyManager tm;
    PhoneStateListener mPsl;
    int strength = -1;

    public String networkType = "";
    private static final int REQUEST_READ_PHONE_STATE = 1;


    MainActivity mParent;
    int signalStrength;


    public SignalStrengthThread(MainActivity parent, int signalStrength) {
        super();
        this.mParent = parent;
        this.signalStrength = signalStrength;
    }


    public void run() {


        try {

            System.out.println("signalStrengthService: " + signalStrength);

            mParent.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mParent.getBaseContext(), "Service: The signal strength is: " + signalStrength, Toast.LENGTH_SHORT).show();
                }
            });


            if((mParent.mSignalStrengthService != null) && (mParent.mCurrentPositionService != null) && (mParent.mCurrentPositionServiceMapsAPI != null))
            {
                final int lSignalStrength = mParent.mSignalStrengthService.getSignalStrength();

                final Location lLocation = mParent.mCurrentPositionService.getLocation();
                System.out.println("mParent.mCurrentPositionService: lSignalStrength: " + lSignalStrength + ", lLocation.getLatitude(): " + lLocation.getLatitude() + ", lLocation.getLongitude(): " + lLocation.getLongitude());
                mParent.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mParent.getBaseContext(), "mParent.mCurrentPositionService: lSignalStrength: " + lSignalStrength + ", lLocation.getLatitude(): " + lLocation.getLatitude() + ", lLocation.getLongitude(): " + lLocation.getLongitude(), Toast.LENGTH_LONG).show();
                    }
                });

                final Location mapsLocation = mParent.mCurrentPositionServiceMapsAPI.getLocation();
                System.out.println("mParent.mCurrentPositionServiceMapsAPI: lSignalStrength: " + lSignalStrength + ", mapsLocation.getLatitude(): " + mapsLocation.getLatitude() + ", mapsLocation.getLongitude(): " + mapsLocation.getLongitude());
                mParent.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mParent.getBaseContext(), "mParent.mCurrentPositionServiceMapsAPI: lSignalStrength: " + lSignalStrength + ", mapsLocation.getLatitude(): " + mapsLocation.getLatitude() + ", mapsLocation.getLongitude(): " + mapsLocation.getLongitude(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{
                System.out.println("One of the services, or both, can be NULL ...");
            }


        } catch (Exception e) {
            System.out.println("Unable to obtain cell signal information");
            e.printStackTrace();
        }


    }


    public String getNetworkName(int i) {
        switch (i) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "";
            case 4:
                return "CDMA";
            case 2:
                return "EDGE";
            case 16:
                return "GSM";
            case 8:
                return "HSDPA";
            case 10:
                return "HSPA";
            case 15:
                return "HSPA+";
            case 9:
                return "HSUPA";
            case 13:
                return "LTE";
            case 17:
                return "SCDMA";
            case 3:
                return "UMTS";
            case 18:
                return "IWLAN";
            case 14:
                return "HRPD";
            default:
                return "NONE";
        }
    }
}
