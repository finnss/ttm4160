  
package server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;
import java.net.*;

import org.json.JSONObject;
import runtime.IStateMachineData;
import runtime.SchedulerData;

import static server.ServerStateMachine.INPUT_ACQUIRED;

class UdpClient extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private String address = "192.168.0.198";
    private int port = 4423;

    private SchedulerData scheduler;

    public UdpClient(SchedulerData scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        System.out.println("Thread running...");
        try {
        socket = new DatagramSocket(port);
        running = true;

        while(running){

            DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
            socket.receive(receivedPacket);

            String payload = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
            System.out.println("Received payload: " + payload);

            JSONObject jsonObject = new JSONObject(payload);
            scheduler.addToQueueLast(INPUT_ACQUIRED, jsonObject);


            if (receivedPacket.equals("end")) {
                running = false;
                continue;
            }
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

    public void sendTimestamp(JSONObject jsonObject) {
        System.out.println("Sending timestamp...");
        try {
            long timestamp = (long) jsonObject.get("currentTimestamp");
            System.out.println("timestamp:" + timestamp);
            byte[] timestampPayload = ("" + timestamp).getBytes();

            InetAddress ipAddress = InetAddress.getByName(address);

            DatagramPacket packet_send = new DatagramPacket(timestampPayload, timestampPayload.length, ipAddress, port);
            socket.send(packet_send);
            System.out.println("Timestamp sent to device");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

} 
