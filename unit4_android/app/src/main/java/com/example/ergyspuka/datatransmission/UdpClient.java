package com.example.ergyspuka.datatransmission;

/**
 * Created by Peter on 25.09.2017.
 */

import android.net.NetworkInfo;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;



public class UdpClient extends Thread {

    NetworkInfo info;

    String dstAddress;
    int dstPort;
    private static MainActivity mParent;
    boolean udpActive = true;
    int udpPacketSendingPeriod;

    DatagramSocket socket;
    InetAddress address;
    UdpReceivePackets myRcvThread;

    String udpCommunicationTechnology = null;
    Location myLocation;
    String mySignalStrength = "-1";
    long myRoundTripDelay = -1;


    //Setter and Getter methods for the udpActive
    public void setUdpActive(boolean b) {
        this.udpActive = b;
    }


    public boolean getUdpActive() {
        return this.udpActive;
    }


    public UdpClient(MainActivity parent, String addr, int port) {
        super();
        this.mParent = parent;
        dstAddress = addr;
        dstPort = port;
    }

    public void setRoundTripDelay(long rtt) {
        this.myRoundTripDelay = rtt;
        System.out.println("Logged round trip delay: " + rtt);
        mParent.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(
                        mParent.getBaseContext(),
                        "Round trip time: " + myRoundTripDelay,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public void run() {
        udpPacketSendingPeriod = Integer.parseInt(mParent.getUdpPeriod());
        try {
            info = Connectivity.getNetworkInfo(mParent.getApplicationContext());
            if (info != null && info.isConnected()) {
                String typeName = info.getTypeName();
                String subTypeName = info.getSubtypeName();
                System.out.println("info.getTypeName(): " + typeName);
                System.out.println("info.getSubtypeName(): " + subTypeName);
                if (typeName == "") {
                    udpCommunicationTechnology = subTypeName;
                } else {
                    udpCommunicationTechnology = typeName;
                }

            } else {
                System.out.println("There is no connection available in this android mobile phone or android phone emulator");
                udpCommunicationTechnology = null;
            }


            socket = new DatagramSocket(dstPort);
            address = InetAddress.getByName(dstAddress);

            //Calling athe Receiving Thread and setting the variable Active to True
            myRcvThread = new UdpReceivePackets(socket, this);
            System.out.println("Setting the variable Active to True!!");
            myRcvThread.setActive(true);
            myRcvThread.start();

            Thread.sleep(2000);

            DatagramPacket packet;
            long currentTimestamp;
            String payload;
            JSONObject json;

            while (getUdpActive()) {
                System.out.println("after continue one ...");
                System.out.println("A - time: " + System.currentTimeMillis());

                Thread.sleep(udpPacketSendingPeriod);

                // From here, it is your turn!
                json = new JSONObject();

                currentTimestamp = System.currentTimeMillis();
                json.put("currentTimestamp", currentTimestamp);
                json.put("communicationTechnology", udpCommunicationTechnology);

                Location mLocation = mParent.mCurrentPositionService.getLocation();
                double latitutde = -1;
                double longitude = -1;
                if (mLocation != null) {
                    latitutde = mLocation.getLatitude();
                    longitude = mLocation.getLongitude();
                }
                json.put("latitude", latitutde);
                json.put("longitude", longitude);

                mySignalStrength = "" + mParent.mSignalStrengthService.getSignalStrength();
                json.put("signalStrength", mySignalStrength);

                json.put("roundTripDelay", myRoundTripDelay);

                System.out.println("Packet to send: " + json.toString());

                byte[] bytePayload = json.toString().getBytes();
                packet = new DatagramPacket(bytePayload, bytePayload.length, address, dstPort);
                socket.send(packet);
            }


        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("SocketException - Setting the variable Active to False!");
            setUdpActive(false);
            //myRcvThread.setActive(false);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("UnknownHostException - Setting the variable Active to False!");
            setUdpActive(false);
            //myRcvThread.setActive(false);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException - Setting the variable Active to False!");
            setUdpActive(false);
            //myRcvThread.setActive(false);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }



    }
