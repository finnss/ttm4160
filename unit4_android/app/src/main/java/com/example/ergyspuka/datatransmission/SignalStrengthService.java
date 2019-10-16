package com.example.ergyspuka.datatransmission;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ergyspuka on 09/05/2017.
 */

public class SignalStrengthService extends Service {

    TelephonyManager tm;
    PhoneStateListener psl;
    Integer signalStrength = -1;


    public void setSignalStrength(Integer iStrength) {
        this.signalStrength = iStrength;
    }

    public Integer getSignalStrength() {
        return this.signalStrength;
    }


    private final IBinder mBinder = new SignalStrengthServicerBinder();


    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(getBaseContext(), "The SignalStrength Service is running ...", Toast.LENGTH_SHORT).show();


        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        psl = new PhoneStateListener() {
            public void onSignalStrengthsChanged(android.telephony.SignalStrength sigStr) {
                if (sigStr != null) {
                    signalStrength = sigStr.getGsmSignalStrength();
                    setSignalStrength(signalStrength);
                    Log.d("Android : ", "Signal Strength changed to " + getSignalStrength());
                    Toast.makeText(getBaseContext(), "Signal Strength changed to " + getSignalStrength(), Toast.LENGTH_SHORT).show();


                    //System.out.println("sigStr.getGsmBitErrorRate(): " + sigStr.getGsmBitErrorRate());
                    //System.out.println("sigStr.getLevel(): " + sigStr.getLevel());
                    //System.out.println("sigStr.getEvdoSnr(): " + sigStr.getEvdoSnr());


                    //System.out.println("tm.getPhoneType(): " + tm.getPhoneType());
                    //System.out.println("tm.getNetworkType(): " + tm.getNetworkType());
                    //System.out.println("tm.getCellLocation(): " + tm.getCellLocation());
                    //System.out.println("tm.getSimOperator(): " + tm.getSimOperator());
                    //System.out.println("tm.getAllCellInfo(): " + tm.getAllCellInfo());
                }
            }
        };


        tm.listen(psl,
                PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR |
                        PhoneStateListener.LISTEN_CALL_STATE |
                        PhoneStateListener.LISTEN_CELL_LOCATION |
                        PhoneStateListener.LISTEN_DATA_ACTIVITY |
                        PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
                        PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR |
                        PhoneStateListener.LISTEN_SERVICE_STATE |
                        PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
        );

        return START_STICKY;

    }


    @Override
    public void onDestroy() {
        System.out.println("Stopping the PhoneStateListener!!!");
        setSignalStrength(-1);
        tm.listen(psl, PhoneStateListener.LISTEN_NONE);

        tm = null;
        psl = null;

        Toast.makeText(getBaseContext(), "The SignalStrength Service is stopped ...", Toast.LENGTH_SHORT).show();

    }


    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public class SignalStrengthServicerBinder extends Binder {
        public SignalStrengthService getService() {
            return SignalStrengthService.this;
        }
    }

}
