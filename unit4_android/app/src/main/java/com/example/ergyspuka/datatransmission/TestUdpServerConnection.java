package com.example.ergyspuka.datatransmission;

import android.net.NetworkInfo;
import android.widget.Toast;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by ergyspuka on 27/04/2017.
 */

public class TestUdpServerConnection extends Thread{


    private static MainActivity mParent;
    private String mServerAddress;
    private int mServerPort;

    NetworkInfo info;

    DatagramSocket socket;
    InetAddress address;



    public TestUdpServerConnection(MainActivity parent, String addr, int port) {
        super();
        this.mParent = parent;
        mServerAddress = addr;
        mServerPort = port;
    }



    public void run(){

        try {


            info = Connectivity.getNetworkInfo(mParent.getApplicationContext());
            if (info != null && info.isConnected()) {
                System.out.println("info.getTypeName(): " + info.getTypeName());
                System.out.println("info.getSubtypeName(): " + info.getSubtypeName());
            } else {
                System.out.println("There is no connection available in this android mobile phone or android phone emulator");
            }



            socket = new DatagramSocket(mServerPort);
            address = InetAddress.getByName(mServerAddress);


            // send request
            byte[] sentBuffer;
            byte[] receivedBuffer;
            String receivedBufferString;
            int bufferLength;

            String testConnectionPacket = "Hei";
            String testExpectedPacket = "Hei";

            bufferLength = testConnectionPacket.length();

            sentBuffer = testConnectionPacket.getBytes();
            System.out.println("sentBuffer: " + sentBuffer);




            DatagramPacket packet = new DatagramPacket(sentBuffer, sentBuffer.length, address, mServerPort);

            //Sending the packet
            socket.send(packet);


            //Receiving the packet
            socket.setSoTimeout(5000);
            socket.receive(packet);

            receivedBuffer = new String(packet.getData(), 0, packet.getLength()).getBytes();
            receivedBufferString = new String(packet.getData(), 0, packet.getLength());

            socket.close();


            if(testExpectedPacket.equals(receivedBufferString)) {

                mParent.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mParent.getBaseContext(), "Successfully connected with the server UDP: " + mServerAddress, Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{

                mParent.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mParent.getBaseContext(), "Unsuccessful attempt to connect with the server: " + mServerAddress, Toast.LENGTH_LONG).show();
                    }
                });
            }

        }

        catch (SocketException e) {
            socket.close();
            e.printStackTrace();
            System.out.println("SocketException - Setting the variable Active to False!");
        }
        catch (UnknownHostException e) {
            socket.close();
            e.printStackTrace();
            System.out.println("UnknownHostException - Setting the variable Active to False!");
        }
        catch (SocketTimeoutException e) {
            socket.close();
            e.printStackTrace();
            System.out.println("SocketTimeoutException - Setting the variable Active to False!");

            mParent.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mParent.getBaseContext(), "Out of Time - Unsuccessful attempt to connect with the server: " + mServerAddress, Toast.LENGTH_LONG).show();
                }
            });

        }
        catch (IOException e) {
            socket.close();
            e.printStackTrace();
            System.out.println("IOException - Setting the variable Active to False!");
        }
    }

}
