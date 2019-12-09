package com.example.epsg.udpconnection;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

public class UdpServerThread extends Thread {

    private final static String TAG = "Borja_" + UdpServerThread.class.getSimpleName();

    int serverPort;
    DatagramSocket socket;

    boolean running;

    public UdpServerThread(int serverPort) {
        super();
        this.serverPort = serverPort;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run() {

        running = true;

        try {
//            updateState("Starting UDP Server");
            socket = new DatagramSocket(serverPort);

//            updateState("UDP Server is running");
            Log.e(TAG, "UDP Server is running");

            while(running){
                byte[] bufReceive = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(bufReceive, bufReceive.length);
                socket.receive(packet);     //this code block the program flow


                //Borja
                String message = new String(packet.getData());


                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();

//                updatePrompt("Request from: " + address + ":   " + port + "   \n" + message + "\n");

                String dString = new String(new Date().toString() + "\n" +
                        "Your address " + address.toString() + ":" + String.valueOf(port) +  "\n" +
                        "I have nothing to responde" + "\n");

                byte[] bufSend = new byte[256];

                bufSend = dString.getBytes();
                packet = new DatagramPacket(bufSend, bufSend.length, address, port);
                socket.send(packet);

            }

            Log.e(TAG, "UDP Server ended");

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                socket.close();
                Log.e(TAG, "socket.close()");
            }
        }
    }
/*
    private void updateState(final String state){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewState.setText(state);
            }
        });
    }

    private void updatePrompt(final String prompt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewPrompt.append(prompt);
            }
        });
    }
*/
}
