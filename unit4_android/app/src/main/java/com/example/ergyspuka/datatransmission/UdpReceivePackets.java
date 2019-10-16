package com.example.ergyspuka.datatransmission;

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
        System.out.println("UdpReceivePacketsManager Name: " + this.getName() + ", id: " + this.getId());

        while (uct.getUdpActive()) {
            // This is your area to work with.
        }
    }

}
