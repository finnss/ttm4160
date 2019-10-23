package com.example.ergyspuka.datatransmission;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    Button udpButton;
    Button testUdpButton;
    EditText udpAddress, udpPort;
    EditText udpTimeSlot;
    EditText udpPeriodEditText;
    Button startSignalStrengthAndLocalizationServices;


    UdpClient udpClientThread;
    TestUdpServerConnection testUdpServerConnection;
    SignalStrengthThread signalStrengthThread;
    SignalStrengthService mSignalStrengthService;
    CurrentPositionService mCurrentPositionService;
    CurrentPositionServiceMapsAPI mCurrentPositionServiceMapsAPI;


    private Intent mSignalStrengthServiceIntent;
    private Intent mCurrentPositionServiceIntent;
    private Intent mCurrentPositionServiceMapsAPIIntent;


    String myHttpServerUrl = null;

    //IP Address and the Port number of the UDP Server
    String udpIPAddress;
    String udpPortNumber;


    String udpTimeSlotSaving;

    String udpPeriodString;


    boolean udpClicked = false;
    boolean startServicesClicked = false;

    boolean signalStrengthServiceRunning = false;
    boolean localizationServiceRunning = false;
    boolean localizationMapsAPIServiceRunning = false;

    boolean udpCommunicationRunning = false;


    private boolean bound = false;
    private boolean isBound = false;
    private boolean isBoundSignalStrength = false;
    private boolean isBoundLocalization = false;
    private boolean isBoundLocalizationMapsAPI = false;


    public ServiceConnection signalStrengthServiceConnection;
    public ServiceConnection currentPositionServiceConnection;
    public ServiceConnection currentPositionServiceMapsAPIConnection;


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;




    public String getUdpTimeSlotSaving(){
        return this.udpTimeSlotSaving;
    }




    public String getUdpPeriod(){
        return this.udpPeriodString;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("onCreate ...");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }



        testUdpButton = (Button) findViewById(R.id.test_udp_button);
        udpButton = (Button) findViewById(R.id.udp_button);

        startSignalStrengthAndLocalizationServices = (Button) findViewById(R.id.start_sigstr_and_loc_services_button);


        NetworkInfo info = Connectivity.getNetworkInfo(this.getApplicationContext());
        if (info != null && info.isConnected()) {
            System.out.println("info.getType(): " + info.getType());
            System.out.println("info.getTypeName(): " + info.getTypeName());
            System.out.println("info.getSubtype(): " + info.getSubtype());
            System.out.println("info.getSubtypeName(): " + info.getSubtypeName());
            System.out.println("isConnectedWifi(): " + Connectivity.isConnectedWifi(this.getApplicationContext()));
            System.out.println("isConnectedMobile(): " + Connectivity.isConnectedMobile(this.getApplicationContext()));
            System.out.println("isConnectedFast(): " + Connectivity.isConnectedFast(this.getApplicationContext()));
        }

        Toast.makeText(this, "Choose the communication protocol.", Toast.LENGTH_SHORT).show();







        startSignalStrengthAndLocalizationServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("The Start Signal Strength and Localization Services Button is clicked!");
                mSignalStrengthServiceIntent = new Intent(getBaseContext(), SignalStrengthService.class);
                mCurrentPositionServiceIntent = new Intent(getBaseContext(), CurrentPositionService.class);
                mCurrentPositionServiceMapsAPIIntent = new Intent(getBaseContext(), CurrentPositionServiceMapsAPI.class);


                /** Callbacks for service binding, passed to bindService() */
                signalStrengthServiceConnection = new ServiceConnection() {

                    @Override
                    public void onServiceConnected(ComponentName className, IBinder service) {
                        SignalStrengthService.SignalStrengthServicerBinder binder = (SignalStrengthService.SignalStrengthServicerBinder) service;
                        System.out.println("A");
                        mSignalStrengthService = binder.getService();
                        bound = true;
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName arg0) {
                        System.out.println("B");
                        mSignalStrengthService = null;
                        bound = false;
                    }
                };



                /** Callbacks for service binding, passed to bindService() */
                currentPositionServiceConnection = new ServiceConnection() {

                    @Override
                    public void onServiceConnected(ComponentName className, IBinder service) {
                        CurrentPositionService.CurrentPositionServiceBinder binder = (CurrentPositionService.CurrentPositionServiceBinder) service;
                        mCurrentPositionService = binder.getService();
                        System.out.println("A");
                        bound = true;
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName arg0) {
                        System.out.println("B");
                        mCurrentPositionService = null;
                        bound = false;
                    }
                };



                /** Callbacks for service binding, passed to bindService() */
                currentPositionServiceMapsAPIConnection = new ServiceConnection() {

                    @Override
                    public void onServiceConnected(ComponentName className, IBinder service) {
                        CurrentPositionServiceMapsAPI.CurrentPositionServiceMapsAPIBinder binder = (CurrentPositionServiceMapsAPI.CurrentPositionServiceMapsAPIBinder) service;
                        mCurrentPositionServiceMapsAPI = binder.getService();
                        System.out.println("A");
                        bound = true;
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName arg0) {
                        System.out.println("B");
                        mCurrentPositionServiceMapsAPI = null;
                        bound = false;
                    }
                };





                if (startServicesClicked == false) {
                    //The SignalStrengthThread is started
                    System.out.println("The Start Signal Strength and Localization Services Button is clicked!");
                    startServicesClicked = true;
                    startSignalStrengthAndLocalizationServices.setText("Stop Signal Strength and Localization Services");



                    startService(mSignalStrengthServiceIntent);
                    isBoundSignalStrength = getApplicationContext().bindService(mSignalStrengthServiceIntent, signalStrengthServiceConnection, Context.BIND_AUTO_CREATE);
                    signalStrengthServiceRunning = true;
                    //Starting the Localization Service
                    startService(mCurrentPositionServiceIntent);
                    isBoundLocalization = getApplicationContext().bindService(mCurrentPositionServiceIntent, currentPositionServiceConnection, Context.BIND_AUTO_CREATE);
                    localizationServiceRunning = true;
                    //Starting the Localization Maps API Service
                    // startService(mCurrentPositionServiceMapsAPIIntent);
                    // isBoundLocalizationMapsAPI = getApplicationContext().bindService(mCurrentPositionServiceMapsAPIIntent, currentPositionServiceMapsAPIConnection, Context.BIND_AUTO_CREATE);
                    // localizationMapsAPIServiceRunning = true;

                } else if ((startServicesClicked == true) && (!udpCommunicationRunning)) {
                    System.out.println("The Stop Signal Strength and Localization Services Button is clicked!");
                    startServicesClicked = false;
                    startSignalStrengthAndLocalizationServices.setText("Start Signal Strength and Localization Services");


                    stopService(mSignalStrengthServiceIntent);
                    mSignalStrengthService.onDestroy();
                    signalStrengthServiceRunning = false;

                    stopService(mCurrentPositionServiceIntent);
                    mCurrentPositionService.onDestroy();
                    localizationServiceRunning = false;

                    stopService(mCurrentPositionServiceMapsAPIIntent);
                    mCurrentPositionServiceMapsAPI.onDestroy();
                    localizationMapsAPIServiceRunning = false;


                } else if ((startServicesClicked == true) && (udpCommunicationRunning)) {

                    Toast.makeText(v.getContext(), "Please, make sure that there is no communication with the servers!", Toast.LENGTH_SHORT).show();

                } else {
                    System.out.println("StartStopServices: I do not know what to do...");
                }

            }
        });






        testUdpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(v.getContext(), "The Test UDP Connection Button is clicked!", Toast.LENGTH_LONG ).show();
                System.out.println("The Test UDP Connection Button is clicked!");


                udpAddress = (EditText) findViewById(R.id.udp_address);
                udpPort = (EditText) findViewById(R.id.udp_port);

                //URL of the UDP Server
                udpIPAddress = udpAddress.getText().toString();
                udpPortNumber = udpPort.getText().toString();
                System.out.println("udpPortNumber" + udpPortNumber);


                testUdpServerConnection = new TestUdpServerConnection(MainActivity.this,
                        udpIPAddress,
                        Integer.parseInt(udpPortNumber));
                testUdpServerConnection.start();


            }
        });





        udpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                udpAddress = (EditText) findViewById(R.id.udp_address);
                udpPort = (EditText) findViewById(R.id.udp_port);
                udpTimeSlot = (EditText) findViewById(R.id.udp_time_slot);
                udpPeriodEditText = (EditText) findViewById(R.id.udp_sent_preriod);

                //URL of the UDP Server
                udpIPAddress = udpAddress.getText().toString();
                udpPortNumber = udpPort.getText().toString();
                udpTimeSlotSaving = udpTimeSlot.getText().toString();
                udpPeriodString = udpPeriodEditText.getText().toString();

                System.out.println("The UDP Button is clicked");


                if ((udpClicked == false) && (signalStrengthServiceRunning && localizationServiceRunning)) {

                    System.out.println("Start UDP!");
                    udpButton.setText("Stop UDP Communication");
                    udpClicked = true;
                    udpCommunicationRunning = true;
                    udpButton.setEnabled(false);


                    Toast.makeText(v.getContext(), "Starting a UDP connection with the server: " + udpIPAddress + ":" + udpPortNumber, Toast.LENGTH_SHORT).show();

                    udpClientThread = new UdpClient(MainActivity.this,
                            udpIPAddress,
                            Integer.parseInt(udpPortNumber));
                    udpClientThread.start();

                    udpButton.setEnabled(true);
                }  else if ((udpClicked == false) && (!signalStrengthServiceRunning && !localizationServiceRunning)){

                    Toast.makeText(v.getContext(), "Please, check if the services have been started correctly!", Toast.LENGTH_SHORT).show();

                } else if ((udpClicked == true)){

                    Toast.makeText(v.getContext(), "Stoping the UDP connection with the server: " + udpIPAddress + ":" + udpPortNumber, Toast.LENGTH_SHORT).show();
                    System.out.println("The UDP Button is clicked - Stop!");
                    udpClientThread.setUdpActive(false);
                    udpButton.setEnabled(true);
                    udpClicked = false;
                    udpCommunicationRunning = false;
                    udpButton.setText("Start UDP Communication");

                } else{
                    Toast.makeText(v.getContext(), "Something has gone wrong!!", Toast.LENGTH_SHORT).show();
                }
            }
        });













    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}