package com.example.ergyspuka.datatransmission;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by Peter on 25.09.2017.
 */

public class UdpReceivePackets extends Thread {

    public DatagramSocket datagramSocket = null;
    UdpClient uct = null;
    boolean active;
    String received;
    byte[] buf = new byte[2048];

    Long rcvUdpPacketTime = null;
    Long sendUdpPacketTime = null;


    public UdpReceivePackets(DatagramSocket datagramSocket, UdpClient uct) {

        this.datagramSocket = datagramSocket;
        this.uct = uct;

    }

    public void setActive(boolean b) {
        this.active = b;
    }


    public void run() {
        System.out.println("###");
        System.out.println("UdpReceivePacketsManager Name: " + this.getName() + ", id: " + this.getId());
        System.out.println(uct.getUdpActive());

        while (uct.getUdpActive()) {
            // This is your area to work with.
            try {
                System.out.println("Receiving...");

                DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
                datagramSocket.receive(receivedPacket);
                String payload = new String(
                        receivedPacket.getData(), 0, receivedPacket.getLength());
                System.out.println("Received payload: " + payload);

                /*
                JSONObject jsonObject = new JSONObject(payload);
                System.out.println("jsonObject:" + jsonObject.toString());
                long timestamp = (long) jsonObject.get("currentTimestamp");
                System.out.println("timestamp:" + timestamp);
                */

                long currentTimestamp = System.currentTimeMillis();
                long sentTimestamp = Long.parseLong(payload);
                long roundTripTime = currentTimestamp - sentTimestamp;

                uct.setRoundTripDelay(roundTripTime);


            } catch (IOException e) {
                System.out.println("IOexception! " + e.toString());
                e.printStackTrace();
            }
            /*
            catch (JSONException err){
                err.printStackTrace();
            }
             */

        }
    }

}
