  
package server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;
import java.net.*;

/*public*/ class UdpClient extends Thread {

    

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private String address = "192.168.0.196";
    private int port = 4445;


    public EchoServer() {
        socket = new DatagramSocket(4445);
    }
    @Override
    public void run() {
        try {
        socket = new DatagramSocket(4445);
        running = true;

        while(running){
            DatagramPacket packet_receive = new DatagramPacket(buf, buf.length);
            System.out.println("Thread running...");
            socket.receive(packet_receive);

            InetAddress ipAddress = InetAddress.getByName(address);
            DatagramPacket packet_send = new DatagramPacket(buf, buf.length, ipAddress, port);
            String received = new String(packet_receive.getData(), 0, packet_receive.getLength());
            if (received.equals("end")) {
                running = false;
                continue;
            }
            socket.send(packet_send);
        }
        socket.close();
        //System.out.println("Thread running...");
        
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

} 
