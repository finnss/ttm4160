  
package server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;
import java.net.*;

import org.json.JSONException;
import org.json.JSONObject;

/*public*/ class UdpClient extends Thread {

    

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private String address = "192.168.0.198";
    private int port = 4423;


    public EchoServer() {
        // socket = new DatagramSocket(port);
    }
    @Override
    public void run() {
        try {
        socket = new DatagramSocket(port);
        running = true;

        while(running){
            System.out.println("Thread running...");
            DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
            socket.receive(receivedPacket);
            System.out.println("Received package");
            String payload = new String(
            receivedPacket.getData(), 0, receivedPacket.getLength());
            System.out.println("Received payload: " + payload);
            JSONObject jsonObject = new JSONObject(payload);
            System.out.println("jsonObject:" + jsonObject.toString());
            long timestamp = (long) jsonObject.get("currentTimestamp");
            System.out.println("timestamp:" + timestamp);
            String timestamp_str = "" + timestamp;



            //runtime.SchedulerData.addToQueueLast("UDP_message_received", buf);
            InetAddress ipAddress = InetAddress.getByName(address);
            DatagramPacket packet_send = new DatagramPacket(timestamp_str.getBytes(), timestamp_str.getBytes().length, ipAddress, port);
            if (receivedPacket.equals("end")) {
                running = false;
                continue;
            }
            socket.send(packet_send);
            System.out.println("Message sent");
        }
        socket.close();
        //System.out.println("Thread running...");
        
        }
        catch (Exception e) {
            e.printStackTrace();
            socket.close();
            return;
        }
    }

} 
